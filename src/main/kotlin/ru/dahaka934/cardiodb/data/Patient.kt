package ru.dahaka934.cardiodb.data

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener.Change
import ru.dahaka934.cardiodb.fxlib.internal.LocalizedObj
import java.time.LocalDate

class Patient : Listenable {
    val name = SimpleStringProperty("")
    val birthday = SimpleObjectProperty<LocalDate?>()
    val lastVisit = SimpleObjectProperty<LocalDate?>()

    fun recalcLastVisit(data: Data) {
        lastVisit.value = data.visits.asSequence().map { it.date.value }.filterNotNull().sorted().lastOrNull()
    }

    override fun addListener(action: () -> Unit) {
        name.addListener { _, _, _ -> action() }
        birthday.addListener { _, _, _ -> action() }
        lastVisit.addListener { _, _, _ -> action() }
    }

    class Data : Listenable {
        val sex = SimpleObjectProperty<Sex>(Sex.MALE)
        val address = SimpleStringProperty("")
        val job = SimpleStringProperty("")
        val telephone = SimpleStringProperty("")
        val visits = FXCollections.observableArrayList<Visit>()

        override fun addListener(action: () -> Unit) {
            sex.addListener { _, _, _ -> action() }
            address.addListener { _, _, _ -> action() }
            job.addListener { _, _, _ -> action() }
            telephone.addListener { _, _, _ -> action() }

            visits.addListener { c: Change<out Visit> ->
                action()
                while (c.next()) {
                    c.addedSubList.forEach { it.addListener(action) }
                }
            }
            visits.forEach { it.addListener(action) }
        }
    }

    enum class Sex : LocalizedObj {
        MALE, FEMALE;

        override val localName: String
            get() = when (this) {
                MALE   -> "мужской"
                FEMALE -> "женский"
            }
    }
}
