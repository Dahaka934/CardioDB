package ru.dahaka934.cardiodb.view

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.collections.transformation.FilteredList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData.YES
import javafx.scene.control.SelectionMode.SINGLE
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCode.*
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Modality.APPLICATION_MODAL
import javafx.stage.StageStyle
import ru.dahaka934.cardiodb.CardioDB
import ru.dahaka934.cardiodb.data.Patient
import ru.dahaka934.cardiodb.view.internal.FXHelper
import ru.dahaka934.cardiodb.view.internal.TableDateCell
import ru.dahaka934.cardiodb.view.internal.TableIndexCell
import ru.dahaka934.cardiodb.view.internal.select
import java.time.LocalDate

class PatientTableController : MainController.ControllerTab<VBox>() {

    @FXML lateinit var table: TableView<Patient>
    @FXML lateinit var columnIndex: TableColumn<Any, Int>
    @FXML lateinit var columnName: TableColumn<Patient, String>
    @FXML lateinit var columnBirthday: TableColumn<Patient, LocalDate>
    @FXML lateinit var columnLastVisit: TableColumn<Patient, LocalDate>

    @FXML lateinit var buttonNew: Button
    @FXML lateinit var buttonModify: Button
    @FXML lateinit var buttonDelete: Button
    @FXML lateinit var buttonOpen: Button

    @FXML lateinit var contextMenuTable: ContextMenu

    @FXML lateinit var filterPanel: ToolBar
    @FXML lateinit var fieldFilter: TextField
    @FXML lateinit var buttonOpenFiltering: Button
    @FXML lateinit var buttonFilterAccept: Button
    @FXML lateinit var buttonFilterCancel: Button

    @FXML fun initialize() {
        columnName.setCellValueFactory { it.value.name }
        columnBirthday.setCellValueFactory { it.value.birthday }
        columnLastVisit.setCellValueFactory { it.value.lastVisit }

        columnIndex.setCellFactory { TableIndexCell() }
        columnBirthday.setCellFactory { TableDateCell<Patient>() }
        columnLastVisit.setCellFactory { TableDateCell<Patient>() }

        columnIndex.style = "-fx-alignment: CENTER;"
        columnBirthday.style = "-fx-alignment: CENTER;"
        columnLastVisit.style = "-fx-alignment: CENTER;"

        CardioDB.registry.reloadAsync().thenAccept {
            table.items = CardioDB.registry.patients
            buttonNew.isDisable = false
        }
        buttonNew.isDisable = true

        table.apply {
            selectionModel.selectionMode = SINGLE
            contextMenu = null
            setRowFactory {
                TableRow<Patient>().also { row ->
                    val binding = Bindings.`when`(Bindings.isNotNull(row.itemProperty()))
                        .then(contextMenuTable).otherwise(null as ContextMenu?)
                    row.contextMenuProperty().bind(binding)

                    row.setOnMouseClicked {
                        if (it.clickCount == 2 && !row.isEmpty) {
                            openPatient(null)
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
        buttonOpen.disableProperty().bind(selectionBinding)
        buttonModify.disableProperty().bind(selectionBinding)
        buttonDelete.disableProperty().bind(selectionBinding)

        filterPanel.managedProperty().bind(filterPanel.visibleProperty())
        filterPanel.isVisible = false

        fieldFilter.setOnKeyPressed {
            if (it.code == ENTER) {
                filterPatients(null)
            }
        }
        fieldFilter.textProperty().addListener { _ ->
            filterPatients(null)
        }
        buttonOpenFiltering.disableProperty().bind(filterPanel.visibleProperty())

        FXHelper.bindClickOnPressEnter(buttonNew)
        FXHelper.bindClickOnPressEnter(buttonModify)
        FXHelper.bindClickOnPressEnter(buttonDelete)
        FXHelper.bindClickOnPressEnter(buttonOpen)
        FXHelper.bindClickOnPressEnter(buttonOpenFiltering)
        FXHelper.bindClickOnPressEnter(buttonFilterAccept)
        FXHelper.bindClickOnPressEnter(buttonFilterCancel)
    }

    override fun init() {
        node.setOnKeyPressed {
            if (it.code == F && it.isControlDown) {
                openFiltering(null)
            } else if (filterPanel.isVisible && it.code == KeyCode.ESCAPE) {
                closeFiltering(null)
            }
        }
    }

    @FXML fun newPatient(e: ActionEvent) {
        CardioDB.createWindow<EditPatientController>("EditPatientLayout.fxml") { controller ->
            title = "Новый пациент"
            isResizable = false
            initModality(APPLICATION_MODAL)
            setOnCloseRequest {
                controller.output?.let {
                    CardioDB.registry.register(it.first, it.second)
                    table.select(it.first)
                }
            }
        }.showAndWait()
    }

    @FXML fun editPatient(e: ActionEvent?) {
        val patient = table.selectionModel.selectedItem ?: return
        CardioDB.createWindow<EditPatientController>("EditPatientLayout.fxml") { controller ->
            title = "Изменение пациента"
            isResizable = false
            controller.setPatient(patient)
            initModality(APPLICATION_MODAL)
            setOnCloseRequest {
                controller.output?.let {
                    table.select(patient)
                }
            }
        }.showAndWait()
    }

    @FXML fun removePatient(e: ActionEvent?) {
        val patient = table.selectionModel.selectedItem ?: return
        val result = FXHelper.alertYesNo().run {
            title = "Предупреждение"
            headerText = null
            contentText = "Вы действительно хотите удалить пациента?"
            initStyle(StageStyle.UTILITY)
            showAndWait()
        }

        if (result.isPresent && result.get().buttonData == YES) {
            CardioDB.registry.unregister(patient)
            table.selectionModel.clearSelection()
        }
        Platform.runLater { table.requestFocus() }
    }

    @FXML fun openPatient(e: ActionEvent?) {
        val patient = table.selectionModel.selectedItem ?: return
        val fnp = patient.name.value.split(" ")
        val name = fnp[0] + " " + fnp.subList(1, fnp.size).joinToString(" ") {
            it.getOrNull(0)?.let { "$it." } ?: ""
        }
        val tab = main.openTab<Pane, PatientDataController>(name, "PatientDataLayout.fxml")
        tab.setPatient(patient)
    }

    @FXML fun openFiltering(e: ActionEvent?) {
        fieldFilter.text = ""
        filterPanel.isVisible = true
        Platform.runLater { filterPanel.requestFocus() }
    }

    @FXML fun closeFiltering(e: ActionEvent?) {
        fieldFilter.text = ""
        filterPanel.isVisible = false

        if (CardioDB.registry.patients !== table.items) {
            table.items = CardioDB.registry.patients
        }

        focusNode()
    }

    @FXML fun filterPatients(e: ActionEvent?) {
        val filter = fieldFilter.text
        if (filter.isEmpty()) {
            table.items = CardioDB.registry.patients
        } else {
            table.items = FilteredList<Patient>(CardioDB.registry.patients).apply {
                setPredicate {
                    it.name.value.startsWith(filter, ignoreCase = true)
                }
            }
        }
    }
}