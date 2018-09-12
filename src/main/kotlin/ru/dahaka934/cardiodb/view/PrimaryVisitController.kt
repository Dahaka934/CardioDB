package ru.dahaka934.cardiodb.view

import javafx.fxml.FXML
import javafx.scene.control.DatePicker
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ru.dahaka934.cardiodb.data.Patient
import ru.dahaka934.cardiodb.data.Patient.Data
import ru.dahaka934.cardiodb.data.Patient.Sex.MALE
import ru.dahaka934.cardiodb.data.Visit
import ru.dahaka934.cardiodb.util.DocCreator
import java.io.File

class PrimaryVisitController : BaseVisitController<ScrollPane>() {

    @FXML lateinit var anamLifeP1: TextArea
    @FXML lateinit var anamLifeP2: TextField
    @FXML lateinit var anamLifeP3: TextField
    @FXML lateinit var anamLifeP4: TextField
    @FXML lateinit var anamLifeP5: TextField
    @FXML lateinit var anamLifeP6: TextField
    @FXML lateinit var anamLifeP7: TextField
    @FXML lateinit var anamLifeP8: DatePicker
    @FXML lateinit var anamLifeP9: DatePicker

    @FXML lateinit var bhabitsP1: TextField
    @FXML lateinit var bhabitsP2: TextField
    @FXML lateinit var bhabitsP3: TextField

    @FXML lateinit var allergiesP1: TextField
    @FXML lateinit var allergiesP2: TextField
    @FXML lateinit var allergiesP3: TextField

    override fun init(patient: Patient, data: Patient.Data, visit: Visit) {
        super.init(patient, data, visit)

        bind(anamLifeP1)
        bind(anamLifeP2, "отрицает")
        bind(anamLifeP3, "отрицает")
        bind(anamLifeP4, "отрицает")
        bind(anamLifeP5, "отрицает")
        bind(anamLifeP6, "отрицает")
        bind(anamLifeP7, "отрицает")
        bind(anamLifeP8)
        anamLifeP8.isDisable = data.sex.value == MALE
        bind(anamLifeP9)

        bind(bhabitsP1, "отрицает")
        bind(bhabitsP2, "отрицает")
        bind(bhabitsP3, "отрицает")

        bind(allergiesP1, "отрицает")
        bind(allergiesP2, "отрицает")
        bind(allergiesP3, "отрицает")
    }

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