package ru.dahaka934.cardiodb.fxlib

import javafx.concurrent.Task
import org.apache.commons.io.FileUtils
import ru.dahaka934.cardiodb.CardioDB
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Supplier

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
        FXHelper.showError(msg, e)
        null
    }
}

inline fun <T> tryWithError(msg: String = "Неожиданная ошибка", def: T, block: () -> T): T {
    return try {
        block()
    } catch (e: Exception) {
        FXHelper.showError(msg, e)
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

inline fun <T> Executor.completable(crossinline action: () -> T): CompletableFuture<T> {
    return CompletableFuture.supplyAsync(Supplier { action() }, this)
}

inline fun <T, U> CompletableFuture<T>.thenApplyFX(crossinline action: (T) -> U): CompletableFuture<U> {
    val ret = CompletableFuture<U>()
    val jfxTask = object : Task<Unit>() {
        override fun call() {}
    }.apply {
        setOnSucceeded {
            ret.complete(action(this@thenApplyFX.get()))
        }
        setOnFailed { ret.completeExceptionally(exception) }
        setOnCancelled { ret.cancel(false) }
    }
    CardioDB.app.executor.execute(jfxTask)
    return ret
}


