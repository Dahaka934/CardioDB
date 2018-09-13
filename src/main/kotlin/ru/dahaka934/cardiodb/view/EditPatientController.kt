package ru.dahaka934.cardiodb.view

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.stage.WindowEvent
import ru.dahaka934.cardiodb.CardioDB
import ru.dahaka934.cardiodb.data.Patient
import ru.dahaka934.cardiodb.view.internal.Controller
import ru.dahaka934.cardiodb.view.internal.FXHelper

class EditPatientController : Controller<Pane>() {
    @FXML lateinit var fieldName: TextField
    @FXML lateinit var comboBoxSex: ChoiceBox<String>
    @FXML lateinit var fieldBirthday: DatePicker
    @FXML lateinit var fieldAddress: TextField
    @FXML lateinit var fieldJob: TextField
    @FXML lateinit var comboBoxJob: ComboBox<String>
    @FXML lateinit var fieldTelephone: TextField

    @FXML lateinit var buttonSave: Button
    @FXML lateinit var buttonCancel: Button

    var output: Pair<Patient, Patient.Data>? = null

    fun setPatient(patient: Patient) {
        fieldName.text = patient.name.value
        fieldBirthday.value = patient.birthday.value

        val data = CardioDB.registry.getData(patient)
        comboBoxSex.selectionModel.select(FXHelper.convSex.toString(data.sex.value))
        fieldAddress.text = data.address.value
        fieldJob.text = data.job.value
        fieldTelephone.text = data.telephone.value

        output = patient to data
    }

    @FXML fun initialize() {
        comboBoxJob.items.addAll("Пенсионер", "Инвалид I группы", "Инвалид II группы", "Инвалид III группы")
        comboBoxSex.items.addAll(enumValues<Patient.Sex>().map { it.localName })

        comboBoxJob.setOnAction {
            fieldJob.text = comboBoxJob.value
        }

        fieldName.textProperty().addListener { _ ->
            buttonSave.isDisable = fieldName.text.isEmpty()
        }
        buttonSave.isDisable = fieldName.text.isEmpty()

        buttonSave.setOnAction {
            var output = output
            if (output == null) {
                output = Patient() to Patient.Data()
                this.output = output
            }

            output.first.apply {
                name.value = fieldName.text
                birthday.value = fieldBirthday.value
            }
            output.second.apply {
                sex.value = FXHelper.convSex.fromString(comboBoxSex.selectionModel.selectedItem)
                address.value = fieldAddress.text
                job.value = fieldJob.text
                telephone.value = fieldTelephone.text
            }
            close()
        }

        buttonCancel.setOnAction {
            close()
        }

        Platform.runLater { fieldName.requestFocus() }

        buttonSave.graphic = FXHelper.imageView("ok24")
        buttonCancel.graphic = FXHelper.imageView("cancel24")

        FXHelper.bindTabOnPressEnter(fieldName)
        FXHelper.bindTabOnPressEnter(fieldBirthday)
        FXHelper.bindTabOnPressEnter(fieldAddress)
        FXHelper.bindTabOnPressEnter(fieldJob)
        FXHelper.bindTabOnPressEnter(fieldTelephone)

        FXHelper.bindClickOnPressEnter(buttonSave)
        FXHelper.bindClickOnPressEnter(buttonCancel)
    }

    override fun init() {
        node.setOnKeyPressed {
            if (it.code == KeyCode.ESCAPE) {
                close()
            }
        }
    }

    fun close() {
        stage.fireEvent(WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST))
    }
}