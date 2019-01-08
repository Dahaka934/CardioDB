package ru.dahaka934.cardiodb.fxlib

import java.io.IOException
import java.net.URL
import java.util.*

class FXClassLoader(parent: ClassLoader) : ClassLoader(parent) {
    private val classes = HashMap<String, Class<*>?>()

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String): Class<*> = findClass(name) ?: throw ClassNotFoundException(name)

    @Throws(ClassNotFoundException::class)
    override fun findClass(className: String): Class<*>? {
        return classes.getOrPut(className) {
            try {
                parent.loadClass(className)
            } catch (ignore: ClassNotFoundException) {
                null
            }
        }
    }

    override fun getResource(name: String): URL? {
        return parent.getResource(name)
    }

    @Throws(IOException::class)
    override fun getResources(name: String): Enumeration<URL> {
        return parent.getResources(name)
    }

    override fun toString(): String {
        return parent.toString()
    }

    override fun setDefaultAssertionStatus(enabled: Boolean) {
        parent.setDefaultAssertionStatus(enabled)
    }

    override fun setPackageAssertionStatus(packageName: String, enabled: Boolean) {
        parent.setPackageAssertionStatus(packageName, enabled)
    }

    override fun setClassAssertionStatus(className: String, enabled: Boolean) {
        parent.setClassAssertionStatus(className, enabled)
    }

    override fun clearAssertionStatus() {
        parent.clearAssertionStatus()
    }
}