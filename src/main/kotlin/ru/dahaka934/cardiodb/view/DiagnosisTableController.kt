package ru.dahaka934.cardiodb.view

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData.YES
import javafx.scene.control.SelectionMode.SINGLE
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import javafx.stage.Modality.APPLICATION_MODAL
import javafx.stage.StageStyle
import ru.dahaka934.cardiodb.CardioDB
import ru.dahaka934.cardiodb.data.Diagnose
import ru.dahaka934.cardiodb.view.internal.FXHelper
import ru.dahaka934.cardiodb.view.internal.TableEnumLocalizedCell
import ru.dahaka934.cardiodb.view.internal.TableIndexCell
import ru.dahaka934.cardiodb.view.internal.select

class DiagnosisTableController : MainController.ControllerTab<VBox>() {
    @FXML lateinit var table: TableView<Diagnose>
    @FXML lateinit var columnIndex: TableColumn<Any, Int>
    @FXML lateinit var columnType: TableColumn<Diagnose, Diagnose.Type>
    @FXML lateinit var columnCode: TableColumn<Diagnose, String>
    @FXML lateinit var columnInfo: TableColumn<Diagnose, String>

    @FXML lateinit var buttonNew: Button
    @FXML lateinit var buttonModify: Button
    @FXML lateinit var buttonDelete: Button

    @FXML lateinit var contextMenuTable: ContextMenu

    lateinit var diagnoses: ObservableList<Diagnose>

    fun init(diagnoses: ObservableList<Diagnose>) {
        this.diagnoses = diagnoses
        table.items = diagnoses
        table.sortOrder.add(columnType)
        table.sort()
    }

    @FXML fun initialize() {
        columnType.setCellValueFactory { it.value.type }
        columnCode.setCellValueFactory { it.value.mkbID }
        columnInfo.setCellValueFactory { it.value.info }
        columnIndex.setCellFactory { TableIndexCell() }
        columnType.setCellFactory { TableEnumLocalizedCell<Diagnose, Diagnose.Type>() }

        columnIndex.style = "-fx-alignment: CENTER;"
        columnType.style = "-fx-alignment: CENTER;"
        columnCode.style = "-fx-alignment: CENTER;"

        columnType.sortType = TableColumn.SortType.ASCENDING
        columnType.isSortable = true

        table.apply {
            selectionModel.selectionMode = SINGLE
            contextMenu = null
            setRowFactory {
                TableRow<Diagnose>().also { row ->
                    val binding = Bindings.`when`(Bindings.isNotNull(row.itemProperty()))
                        .then(contextMenuTable).otherwise(null as ContextMenu?)
                    row.contextMenuProperty().bind(binding)

                    row.setOnMouseClicked {
                        if (it.clickCount == 2 && !row.isEmpty) {
                            modifyDiagnose(null)
                        }
                    }
                }
            }
            setOnKeyPressed {
                if (it.code == KeyCode.ESCAPE) {
                    focusNode()
                }
            }
            sortOrder.add(columnType)
        }

        val selectionBinding = Bindings.`when`(Bindings.isNotNull(table.selectionModel.selectedItemProperty()))
            .then(false).otherwise(true)
        buttonDelete.disableProperty().bind(selectionBinding)
        buttonModify.disableProperty().bind(selectionBinding)

        buttonNew.graphic = FXHelper.imageView("add24")
        buttonModify.graphic = FXHelper.imageView("modify24")
        buttonDelete.graphic = FXHelper.imageView("delete24")

        FXHelper.bindClickOnPressEnter(buttonNew)
        FXHelper.bindClickOnPressEnter(buttonModify)
        FXHelper.bindClickOnPressEnter(buttonDelete)
    }

    @FXML fun newDiagnose(e: ActionEvent) {
        CardioDB.createWindow<EditDiagnoseController>("EditDiagnoseLayout.fxml") { controller ->
            title = "Новый диагноз"
            isResizable = false
            initModality(APPLICATION_MODAL)
            setOnCloseRequest {
                controller.output?.let {
                    diagnoses.add(it)
                    table.select(it)
                    table.sort()
                }
            }
        }.showAndWait()
    }

    @FXML fun modifyDiagnose(e: ActionEvent?) {
        val diagnose = table.selectionModel.selectedItem ?: return
        CardioDB.createWindow<EditDiagnoseController>("EditDiagnoseLayout.fxml") { controller ->
            title = "Изменение диагноза"
            isResizable = false
            controller.setDiagnose(diagnose)
            initModality(APPLICATION_MODAL)
            controller.output?.let {
                table.sort()
            }
        }.showAndWait()
    }

    @FXML fun deleteDiagnose(e: ActionEvent) {
        val diagnose = table.selectionModel.selectedItem ?: return
        val result = FXHelper.alertYesNo().run {
            title = "Предупреждение"
            headerText = null
            contentText = "Вы действительно хотите удалить диагноз?"
            initStyle(StageStyle.UTILITY)
            showAndWait()
        }

        if (result.isPresent && result.get().buttonData == YES) {
            diagnoses.remove(diagnose)
            table.selectionModel.clearSelection()
        }
        Platform.runLater { table.requestFocus() }
    }

    override fun onClose() {
        table.columns.clear()
        table.layout()
    }
}