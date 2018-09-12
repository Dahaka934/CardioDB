package ru.dahaka934.cardiodb.view

import javafx.scene.control.ScrollPane
import ru.dahaka934.cardiodb.data.Patient
import ru.dahaka934.cardiodb.data.Patient.Data
import ru.dahaka934.cardiodb.data.Visit
import ru.dahaka934.cardiodb.util.DocCreator
import java.io.File

class SecondaryVisitController : BaseVisitController<ScrollPane>() {
    companion object {
        fun generateDocReception(file: File, patient: Patient, data: Data, visit: Visit): Boolean {
            val creator = DocCreator()

            return creator.save(file)
        }

        fun generateDocConclusion(file: File, patient: Patient, data: Data, visit: Visit): Boolean {
            val creator = DocCreator()

            return creator.save(file)
        }
    }
}