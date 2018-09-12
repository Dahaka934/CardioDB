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

inline fun <T> tryWithError(msg: String = "Ignored exception", block: () -> T): T? {
    return try {
        block()
    } catch (e: Exception) {
        // TODO: showError(msg, e)
        null
    }
}

inline fun <T> tryWithError(msg: String = "Ignored exception", def: T, block: () -> T): T {
    return try {
        block()
    } catch (e: Exception) {
        // TODO: showError(msg, e)
        def
    }
}

fun File.toDir(): File {
    FileUtils.forceMkdir(this)
    return this
}

fun File.toExistsFile(): File {
    if (!exists()) {
        createNewFile()
    }
    return this
}

fun File.toNewFile(): File {
    if (exists()) {
        delete()
    }
    createNewFile()
    return this
}

fun File.toExistsFileForce(): File {
    if (!exists()) {
        parentFile?.toDir()
        createNewFile()
    }
    return this
}
