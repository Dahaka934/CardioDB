package ru.dahaka934.cardiodb

import ru.dahaka934.cardiodb.fxlib.toExistsFile
import ru.dahaka934.cardiodb.fxlib.tryWithError
import java.io.File
import java.io.FileOutputStream
import java.util.*

object AppProperties : Properties() {
    private val file = File("config.properties").toExistsFile()

    fun reload() {
        tryWithError("Не удалось загрузить '${file.name}'") {
            file.inputStream().reader().buffered().use {
                load(it)
            }
        }
    }

    fun save() {
        tryWithError("Не удалось сохранить '${file.name}'") {
            FileOutputStream(file).writer().buffered().use {
                store(it, "CardioDB config properties")
            }
        }
    }

    fun getStringList(key: String): List<String> {
        val ret: String? = getProperty(key)
        if (ret != null) {
            return ret.split("|").asSequence().map { it.trim() }.toList()
        }
        return emptyList()
    }

    fun setStringList(key: String, list: Collection<String>) {
        setProperty(key, list.joinToString(separator = "|") { it })
    }

    fun getStringOrPut(key: String, def: String): String {
        var ret: String? = getProperty(key)
        if (ret == null) {
            ret = def
            setProperty(key, def)
        }
        return ret
    }

    fun getIntOrPut(key: String, def: Int): Int {
        return getStringOrPut(key, def.toString()).toIntOrNull() ?: def
    }

    fun getLongOrPut(key: String, def: Long): Long {
        return getStringOrPut(key, def.toString()).toLongOrNull() ?: def
    }

    fun getUIntOrPut(key: String, def: Int): Int {
        val ret = getIntOrPut(key, def)
        return if (ret < 0) {
            setProperty(key, def.toString())
            def
        } else ret
    }

    fun getULongOrPut(key: String, def: Long): Long {
        val ret = getLongOrPut(key, def)
        return if (ret < 0) {
            setProperty(key, def.toString())
            def
        } else ret
    }
}