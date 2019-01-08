package ru.dahaka934.cardiodb.fxlib

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.Charset

object IOTools {

    fun resourceStream(path: String): InputStream? = IOTools::class.java.getResourceAsStream(path)

    fun fileStream(file: File): FileInputStream? {
        return if (file.isFile && file.exists()) tryWithError { FileInputStream(file) } else null
    }

    fun readString(file: File, charset: Charset = Charsets.UTF_8): String {
        return tryWithError("Не удалось прочитать файл '${file.name}'") {
            fileStream(file)?.buffered()?.use { IOUtils.toString(it, charset) }
        } ?: ""
    }

    fun findUniqueFile(file: File): File {
        if (file.exists()) {
            val path = file.absolutePath
            val ext = path.substringAfterLast('.', "")
            val name = path.substringBeforeLast(".$ext", file.name)
            var copy: File = file
            for (i in 0 until Int.MAX_VALUE) {
                val index = if (i == 0) "" else "-$i"
                copy = File("$name$index.$ext")
                if (!copy.exists()) {
                    break
                }
            }
            return copy
        }
        return file
    }

    fun writeString(file: File, data: String, copyIfExist: Boolean) {
        if (copyIfExist && file.exists()) {
            tryWithError("Не удалось скопировать файл '${file.name}'") {
                FileUtils.copyFile(file, findUniqueFile(file))
            }
        }

        tryWithError("Не удалось записать файл '${file.name}'") {
            FileUtils.writeStringToFile(file, data, Charsets.UTF_8)
        }
    }

    fun forEachFile(dir: File, recursive: Boolean, block: (file: File) -> Unit) {
        for (file in dir.listFiles()) {
            if (file.isDirectory) {
                if (recursive) {
                    forEachFile(file, recursive, block)
                }
            } else {
                block(file)
            }
        }
    }
}
