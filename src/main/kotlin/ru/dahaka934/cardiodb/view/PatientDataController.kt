package ru.dahaka934.cardiodb.view

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData.YES
import javafx.scene.control.SelectionMode.SINGLE
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.stage.StageStyle
import ru.dahaka934.cardiodb.CardioDB
import ru.dahaka934.cardiodb.data.Patient
import ru.dahaka934.cardiodb.data.Visit
import ru.dahaka934.cardiodb.data.Visit.Type
import ru.dahaka934.cardiodb.data.Visit.Type.*
import ru.dahaka934.cardiodb.util.LocalDateConverter
import ru.dahaka934.cardiodb.view.MainController.ControllerTab
import ru.dahaka934.cardiodb.view.internal.FXHelper
import ru.dahaka934.cardiodb.view.internal.TableDateCell
import ru.dahaka934.cardiodb.view.internal.TableIndexCell
import ru.dahaka934.cardiodb.view.internal.select
import java.time.LocalDate

class PatientDataController : ControllerTab<Pane>() {

    private lateinit var patient: Patient
    private lateinit var patientData: Patient.Data

    @FXML lateinit var table: TableView<Visit>
    @FXML lateinit var columnIndex: TableColumn<Any, Int>
    @FXML lateinit var columnType: TableColumn<Visit, Visit.Type>
    @FXML lateinit var columnDate: TableColumn<Visit, LocalDate>

    @FXML lateinit var fieldName: Label
    @FXML lateinit var fieldSex: Label
    @FXML lateinit var fieldBirthday: Label
    @FXML lateinit var fieldAddress: Label
    @FXML lateinit var fieldJob: Label
    @FXML lateinit var fieldTelephone: Label

    @FXML lateinit var buttonNew: SplitMenuButton
    @FXML lateinit var buttonCopy: SplitMenuButton
    @FXML lateinit var buttonDoc: SplitMenuButton
    @FXML lateinit var buttonOpen: Button
    @FXML lateinit var buttonDelete: Button

    @FXML lateinit var contextMenuTable: ContextMenu

    override fun init() {
        node.requestFocus()

        columnType.setCellValueFactory { it.value.type }
        columnDate.setCellValueFactory { it.value.date }
        columnIndex.setCellFactory { TableIndexCell() }
        columnDate.setCellFactory { TableDateCell<Visit>() }
        columnType.setCellFactory {
            object : TableCell<Visit, Visit.Type>() {
                override fun updateItem(item: Type?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = (if (!empty && item != null) item.localName else null)
                }
            }
        }
        columnIndex.style = "-fx-alignment: CENTER;"
        columnType.style = "-fx-alignment: CENTER;"
        columnDate.style = "-fx-alignment: CENTER;"

        table.apply {
            selectionModel.selectionMode = SINGLE
            contextMenu = null
            setRowFactory {
                TableRow<Visit>().also { row ->
                    val binding = Bindings.`when`(Bindings.isNotNull(row.itemProperty()))
                        .then(contextMenuTable).otherwise(null as ContextMenu?)
                    row.contextMenuProperty().bind(binding)

                    row.setOnMouseClicked {
                        if (it.clickCount == 2 && !row.isEmpty) {
                            openVisit(null)
                        }
                    }
                }
            }
            setOnKeyPressed {
                if (it.code == KeyCode.ESCAPE) {
                    focusNode()
                }
            }
        }

        val selectionBinding = Bindings.`when`(Bindings.isNotNull(table.selectionModel.selectedItemProperty()))
            .then(false).otherwise(true)
        buttonCopy.disableProperty().bind(selectionBinding)
        buttonOpen.disableProperty().bind(selectionBinding)
        buttonDelete.disableProperty().bind(selectionBinding)
        buttonDoc.disableProperty().bind(selectionBinding)

        FXHelper.bindClickOnPressEnter(buttonOpen)
        FXHelper.bindClickOnPressEnter(buttonDelete)
    }

    fun setPatient(patient: Patient) {
        this.patient = patient
        patientData = CardioDB.registry.getData(patient)

        fieldName.textProperty().bind(patient.name)
        fieldSex.textProperty().let {
            Bindings.bindBidirectional(it, patientData.sex, FXHelper.convSex)
        }
        fieldBirthday.textProperty()
        fieldBirthday.textProperty().let {
            Bindings.bindBidirectional(it, patient.birthday, LocalDateConverter)
        }
        fieldAddress.textProperty().bind(patientData.address)
        fieldJob.textProperty().bind(patientData.job)
        fieldTelephone.textProperty().bind(patientData.telephone)

        table.items = patientData.visits
    }

    private fun addVisit(visit: Visit, type: Visit.Type) {
        visit.type.value = type
        visit.date.value = LocalDate.now()
        patientData.visits.add(visit)
        table.select(visit)
        patient.recalcLastVisit(patientData)
    }

    @FXML fun newVisit(e: ActionEvent) {
        addVisit(Visit(), PRIMARY)
    }

    @FXML fun newVisit1(e: ActionEvent) {
        addVisit(Visit(), PRIMARY)
    }

    @FXML fun newVisit2(e: ActionEvent) {
        addVisit(Visit(), SECONDARY)
    }

    @FXML fun copyVisit(e: ActionEvent?) {
        val visit = table.selectionModel.selectedItem ?: return
        addVisit(visit.copy(), visit.type.value)
    }

    @FXML fun copyVisit1(e: ActionEvent?) {
        val visit = table.selectionModel.selectedItem ?: return
        addVisit(visit.copy(), PRIMARY)
    }

    @FXML fun copyVisit2(e: ActionEvent?) {
        val visit = table.selectionModel.selectedItem ?: return
        addVisit(visit.copy(), SECONDARY)
    }

    @FXML fun removeVisit(e: ActionEvent?) {
        val visit = table.selectionModel.selectedItem ?: return
        val result = FXHelper.alertYesNo().run {
            title = "Предупреждение"
            headerText = null
            contentText = "Вы действительно хотите удалить осмотр?"
            initStyle(StageStyle.UTILITY)
            showAndWait()
        }

        if (result.isPresent && result.get().buttonData == YES) {
            patientData.visits.remove(visit)
            table.selectionModel.clearSelection()
        }
        Platform.runLater { table.requestFocus() }
    }

    @FXML fun openVisit(e: ActionEvent?) {
        val visit = table.selectionModel.selectedItem ?: return
        val type = visit.type.value ?: PRIMARY
        val tab = when (type) {
            PRIMARY   -> {
                val name = "$tabName - ${type.localName}"
                main.openTab<ScrollPane, PrimaryVisitController>(name, "PrimaryVisitLayout.fxml")
            }
            SECONDARY -> {
                val name = "$tabName - ${type.localName}"
                main.openTab<ScrollPane, SecondaryVisitController>(name, "SecondaryVisitLayout.fxml")
            }
        }

        tab.node.prefHeight = Double.NEGATIVE_INFINITY
        tab.init(patient, patientData, visit)
        patient.recalcLastVisit(patientData)
    }

    @FXML fun onGenDocReception(e: ActionEvent) {

    }

    @FXML fun onGenDocConclusion(e: ActionEvent) {

    }

    override fun onClose() {
        table.columns.clear()
        table.layout()

        fieldName.textProperty().unbind()
        fieldSex.textProperty().unbindBidirectional(patientData.sex)
        fieldBirthday.textProperty().unbindBidirectional(patient.birthday)
        fieldAddress.textProperty().unbind()
        fieldJob.textProperty().unbind()
        fieldTelephone.textProperty().unbind()
    }
}