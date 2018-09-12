package ru.dahaka934.cardiodb.data

interface Listenable {
    fun addListener(action: () -> Unit)
}