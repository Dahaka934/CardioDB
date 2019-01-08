package ru.dahaka934.cardiodb.data

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import ru.dahaka934.cardiodb.fxlib.internal.LocalizedObj

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

    fun copy() = Diagnose().also { copy ->
        copy.type.value = type.value
        copy.mkbID.value = mkbID.value
        copy.info.value = info.value
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