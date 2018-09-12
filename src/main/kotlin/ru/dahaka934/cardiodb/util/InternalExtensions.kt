package ru.dahaka934.cardiodb.util

import org.apache.commons.io.FileUtils
import java.io.File

inline fun <T> tryWithIgnore(block: () -> T): T? {
    return try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

inline fun <T> tryWithError(msg: String = "Неожиданная ошибка", block: () -> T): T? {
    return try {
        block()
    } catch (e: Exception) {
        // TODO: showError(msg, e)
        null
    }
}

inline fun <T> tryWithError(msg: String = "Неожиданная ошибка", def: T, block: () -> T): T {
    return try {
        block()
    } catch (e: Exception) {
        // TODO: showError(msg, e)
        def
    }
}

fun File.toDir(): File {
    tryWithError("Не удалось создать каталог") {
        FileUtils.forceMkdir(this)
    }
    return this
}

fun File.toExistsFile(): File {
    if (!exists()) {
        tryWithError("Не удалось создать новый файл.") {
            createNewFile()
        }
    }
    return this
}

fun File.toNewFile(): File {
    if (exists()) {
        delete()
    }
    tryWithError("Не удалось создать новый файл.") {
        createNewFile()
    }
    return this
}

fun File.toExistsFileForce(): File {
    if (!exists()) {
        parentFile?.toDir()
        tryWithError("Не удалось создать новый файл.") {
            createNewFile()
        }
    }
    return this
}
