package ru.dahaka934.cardiodb.data

import it.unimi.dsi.fastutil.io.FastBufferedOutputStream
import org.apache.commons.io.IOUtils
import ru.dahaka934.cardiodb.AppProperties
import ru.dahaka934.cardiodb.CardioDB
import ru.dahaka934.cardiodb.fxlib.IOTools
import ru.dahaka934.cardiodb.fxlib.internal.LocalDateConverter
import ru.dahaka934.cardiodb.fxlib.toDir
import ru.dahaka934.cardiodb.fxlib.tryWithError
import java.io.*
import java.time.LocalDate
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object Backuper {
    private val fileBackups = File("backups")
    private val backupRegex: Regex

    init {
        val def = "[\\S\\s]*.docx"
        AppProperties.setProperty("backupRegexExample", def)
        backupRegex = tryWithError("Invalid backup regex. Set to default.") {
            AppProperties.getStringOrPut("backupRegex", def).toPattern().toRegex()
        } ?: run {
            def.toPattern().toRegex()
        }
    }

    fun makeBackupAuto() {
        var lastBackup = AppProperties.getULongOrPut("backupLast", 0L)
        if (!fileBackups.exists()) {
            lastBackup = 0L
        }
        val backupFreqInHours = AppProperties.getUIntOrPut("backupFreqInHours", 24)
        val backupFreq = backupFreqInHours * 60L * 60L * 1000L
        val currTime = Date().time
        if (lastBackup + backupFreq <= currTime) {
            makeBackup()
            AppProperties.setProperty("backupLast", currTime.toString())
            CardioDB.app.log("Backup data")
        }
    }

    fun checkBackupsCount() {
        val backupCount = AppProperties.getIntOrPut("backupCount", 20)
        if (backupCount <= 0) {
            return
        }

        val files = fileBackups.toDir().listFiles()
        Arrays.sort(files) { f1, f2 -> java.lang.Long.compare(f1.lastModified(), f2.lastModified()) }
        for (i in 0 until files.size - backupCount - 1) {
            files[i].delete()
        }
    }

    fun makeBackup() {
        checkBackupsCount()
        var file = File(fileBackups.toDir(), "${LocalDateConverter.toString(LocalDate.now())}.zip")
        file = IOTools.findUniqueFile(file)
        tryWithError("Ошибка при создании бэкапа") {
            zip(file, File("data").absoluteFile, backupRegex)
        }
    }

    @Throws(IOException::class)
    fun zip(zipfile: File, directory: File, filter: Regex) {
        if (!directory.exists()) {
            return
        }

        val base = directory.absoluteFile.parentFile.toURI()
        val queue = LinkedList<File>()
        queue.push(directory)

        ZipOutputStream(FastBufferedOutputStream(FileOutputStream(zipfile), 65536)).use { zout ->
            val buffer = ByteArray(65536)
            while (!queue.isEmpty()) {
                val dir = queue.pop()
                if (!dir.isDirectory) {
                    continue
                }
                for (kid in dir.listFiles()) {
                    var name = base.relativize(kid.toURI()).path
                    if (kid.isDirectory) {
                        queue.push(kid)
                        name = if (name.endsWith("/")) name else "$name/"
                        zout.putNextEntry(ZipEntry(name))
                    } else {
                        if (name.matches(filter)) {
                            continue
                        }
                        zout.putNextEntry(ZipEntry(name))
                        var fin: FileInputStream? = null
                        try {
                            fin = FileInputStream(kid)
                            IOUtils.copyLarge(fin, zout, buffer)
                        } catch (ignored: FileNotFoundException) {

                        } finally {
                            IOUtils.closeQuietly(fin)
                            zout.closeEntry()
                        }
                    }
                }
            }
        }
    }
}