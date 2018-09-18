package ru.dahaka934.cardiodb

import ru.dahaka934.cardiodb.util.toExistsFile
import ru.dahaka934.cardiodb.util.tryWithError
import java.io.File
import java.util.*

object AppProperties : Properties() {
    private val file = File("config.properties").toExistsFile()

    fun reload() {
        tryWithError("Не удалось загрузить '${file.name}'") {
            file.inputStream().buffered().use {
                load(it)
            }
        }
    }

    fun save() {
        tryWithError("Не удалось сохранить '${file.name}'") {
            file.outputStream().buffered().use {
                store(it, "CardioDB config properties")
            }
        }
    }

    fun getPropertyOrPut(key: String, def: String): String {
        var ret: String? = getProperty(key)
        if (ret == null) {
            ret = def
            setProperty(key, def)
        }
        return ret
    }
}