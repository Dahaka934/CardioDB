package ru.dahaka934.cardiodb.data

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener.Change
import javafx.collections.MapChangeListener
import ru.dahaka934.cardiodb.data.Visit.Type.PRIMARY
import ru.dahaka934.cardiodb.fxlib.internal.LocalizedObj
import java.time.LocalDate

class Visit : Listenable {
    enum class Type : LocalizedObj {
        PRIMARY, SECONDARY;

        override val localName: String
            get() = when (this) {
                PRIMARY   -> "Первичный"
                SECONDARY -> "Повторный"
            }
    }

    val type = SimpleObjectProperty<Type>(PRIMARY)
    val date = SimpleObjectProperty<LocalDate?>()
    val diagnoses = FXCollections.observableArrayList<Diagnose>()
    val meta = FXCollections.observableHashMap<String, SimpleStringProperty>()

    fun metaProperty(id: String, def: String = "") = meta.getOrPut(id) { SimpleStringProperty(def) }

    fun meta(id: String, def: String = "") = metaProperty(id, def).value ?: ""

    fun copy() = Visit().also {
        it.type.value = type.value
        it.date.value = date.value
        it.diagnoses.addAll(diagnoses)
        it.meta.putAll(meta)
    }

    override fun addListener(action: () -> Unit) {
        type.addListener { _, _, _ -> action() }
        date.addListener { _, _, _ -> action() }

        diagnoses.addListener { c: Change<out Diagnose> ->
            action()
            while (c.next()) {
                c.addedSubList.forEach { it.addListener(action) }
            }
        }
        diagnoses.forEach { it.addListener(action) }

        meta.addListener { c: MapChangeListener.Change<out String, out SimpleStringProperty> ->
            action()
            c.valueAdded?.addListener { _, _, _ -> action() }
        }
        meta.forEach {
            it.value.addListener { _, _, _ -> action() }
        }
    }
}