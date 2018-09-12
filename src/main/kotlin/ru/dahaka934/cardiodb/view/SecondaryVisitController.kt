package ru.dahaka934.cardiodb.view

import javafx.scene.control.ScrollPane
import ru.dahaka934.cardiodb.data.Patient
import ru.dahaka934.cardiodb.data.Patient.Data
import ru.dahaka934.cardiodb.data.Visit
import java.io.File

class SecondaryVisitController : BaseVisitController<ScrollPane>() {
    companion object {
        fun generateDocReception(file: File, patient: Patient, data: Data, visit: Visit): Boolean {
            return true
        }

        fun generateDocConclusion(file: File, patient: Patient, data: Data, visit: Visit): Boolean {
            return true
        }
    }
}