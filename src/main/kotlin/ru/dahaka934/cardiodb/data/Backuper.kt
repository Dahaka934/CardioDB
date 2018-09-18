package ru.dahaka934.cardiodb.data

import it.unimi.dsi.fastutil.io.FastBufferedOutputStream
import org.apache.commons.io.IOUtils
import ru.dahaka934.cardiodb.AppProperties
import ru.dahaka934.cardiodb.CardioDB
import ru.dahaka934.cardiodb.util.IOTools
import ru.dahaka934.cardiodb.util.LocalDateConverter
import ru.dahaka934.cardiodb.util.toDir
import ru.dahaka934.cardiodb.util.tryWithError
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
            AppProperties.getPropertyOrPut("backupRegex", def).toPattern().toRegex()
        } ?: run {
            def.toPattern().toRegex()
        }
    }

    fun makeBackupAuto() {
        var lastBackup = AppProperties.getProperty("lastBackup", "0").toLongOrNull() ?: 0L
        if (!fileBackups.exists()) {
            lastBackup = 0L
        }
        val backupFreqInHours = AppProperties.getPropertyOrPut("backupFreqInHours", "24").toLongOrNull() ?: 24L
        val backupFreq = backupFreqInHours * 60 * 60 * 1000
        val currTime = Date().time
        if (lastBackup + backupFreq <= currTime) {
            makeBackup()
            AppProperties.setProperty("lastBackup", currTime.toString())
            CardioDB.log("Backup data")
        }
    }

    fun makeBackup() {
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