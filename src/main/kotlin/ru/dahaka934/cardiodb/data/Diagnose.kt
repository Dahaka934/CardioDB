package ru.dahaka934.cardiodb.data

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

class Diagnose : Listenable {
    val type = SimpleObjectProperty<Type>(Type.MAIN)
    val mkbID = SimpleStringProperty("")
    val info = SimpleStringProperty("")

    override fun addListener(action: () -> Unit) {
        type.addListener { _, _, _ -> action() }
        mkbID.addListener { _, _, _ -> action() }
        info.addListener { _, _, _ -> action() }
        info.addListener { _, _, _ -> action() }
    }

    enum class Type : LocalizedObj {
        MAIN, COMPLEX, BKG, ACCOMP;

        override val localName: String
            get() = when (this) {
                MAIN    -> "Основной"
                COMPLEX -> "Осложнение"
                BKG     -> "Фоновый"
                ACCOMP  -> "Сопутствующий"
            }
    }
}