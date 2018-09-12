package ru.dahaka934.cardiodb.view

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.util.converter.BooleanStringConverter
import ru.dahaka934.cardiodb.CardioDB
import ru.dahaka934.cardiodb.data.Patient
import ru.dahaka934.cardiodb.data.Visit
import ru.dahaka934.cardiodb.util.LocalDateConverter
import ru.dahaka934.cardiodb.util.LocalDateISOConverter
import ru.dahaka934.cardiodb.view.MainController.ControllerTab

abstract class BaseVisitController<N : Node> : ControllerTab<N>() {
    private lateinit var patient: Patient
    private lateinit var patientData: Patient.Data
    private lateinit var visit: Visit

    private val needUnbind = ArrayList<Node>()

    @FXML lateinit var headerName: Label
    @FXML lateinit var headerBirthday: Label
    @FXML lateinit var headerVisitDate: DatePicker

    @FXML lateinit var complaintsP1: TextArea

    @FXML lateinit var anamDiseaseP1: TextArea
    @FXML lateinit var anamDiseaseP2: TextField
    @FXML lateinit var anamDiseaseP3: TextField
    @FXML lateinit var anamDiseaseP4: TextArea

    @FXML lateinit var anamInsuranceP1: TextArea

    @FXML lateinit var objStatusP1: TextField
    @FXML lateinit var objStatusP1c: ComboBox<String>
    @FXML lateinit var objStatusP2: TextField
    @FXML lateinit var objStatusP3: TextField
    @FXML lateinit var objStatusP4: TextField
    @FXML lateinit var objStatusP5: TextField
    @FXML lateinit var objStatusP5b: Button
    @FXML lateinit var objStatusP6: TextField
    @FXML lateinit var objStatusP6c: ComboBox<String>
    @FXML lateinit var objStatusP7: TextField
    @FXML lateinit var objStatusP8: TextField
    @FXML lateinit var objStatusP9: ComboBox<String>
    @FXML lateinit var objStatusP10: TextField
    @FXML lateinit var objStatusP11: TextField
    @FXML lateinit var objStatusP11c: ComboBox<String>
    @FXML lateinit var objStatusP12: TextField
    @FXML lateinit var objStatusP12c: ComboBox<String>
    @FXML lateinit var objStatusP13: TextField
    @FXML lateinit var objStatusP14: TextField
    @FXML lateinit var objStatusP14c: ComboBox<String>
    @FXML lateinit var objStatusP15: TextField
    @FXML lateinit var objStatusP16: TextField
    @FXML lateinit var objStatusP16c: ComboBox<String>
    @FXML lateinit var objStatusP17: TextField
    @FXML lateinit var objStatusP17c: ComboBox<String>
    @FXML lateinit var objStatusP18: TextField
    @FXML lateinit var objStatusP18c: ComboBox<String>
    @FXML lateinit var objStatusP19: TextField
    @FXML lateinit var objStatusP19c: ComboBox<String>
    @FXML lateinit var objStatusP20: TextField
    @FXML lateinit var objStatusP20c: ComboBox<String>
    @FXML lateinit var objStatusP21: TextField
    @FXML lateinit var objStatusP21c: ComboBox<String>

    @FXML lateinit var currHelpP1: CheckBox
    @FXML lateinit var currHelpP2: TextArea

    @FXML lateinit var surveyPlanP1: TextArea

    @FXML lateinit var treatPlanP1: TextArea

    @FXML lateinit var recommsP1: TextArea

    @FXML lateinit var evnP1: CheckBox
    @FXML lateinit var evnP2: TextArea
    @FXML lateinit var evnP3: TextField
    @FXML lateinit var evnP4: DatePicker
    @FXML lateinit var evnP5: DatePicker

    @FXML lateinit var paneDiagnosis: TitledPane
    @FXML lateinit var controllerDiagnosis: DiagnosisTableController

    fun bind(node: TextInputControl, defValue: String = "") {
        node.textProperty().bindBidirectional(visit.metaProp(node.id, defValue))
        needUnbind += node
    }

    fun bind(node: ComboBox<String>, vararg values: String) {
        node.valueProperty().bindBidirectional(visit.metaProp(node.id, values.getOrNull(0) ?: ""))
        node.items.addAll(values)
        needUnbind += node
    }

    fun bind(node: DatePicker, defValue: String = "") {
        node.valueProperty().let {
            val p = visit.metaProp(node.id, defValue)
            it.set(LocalDateISOConverter.fromString(p.value))
            Bindings.bindBidirectional(p, it, LocalDateISOConverter)
        }
        needUnbind += node
    }

    fun bind(node: CheckBox, defValue: Boolean) {
        node.selectedProperty().let {
            val p = visit.metaProp(node.id, defValue.toString())
            val conv = BooleanStringConverter()
            it.set(conv.fromString(p.value) ?: false)
            Bindings.bindBidirectional(p, it, conv)
        }
        needUnbind += node
    }

    fun bind(node: TextInputControl, cb: ComboBox<String>, add: Boolean, setFirst: Boolean, vararg values: String) {
        bind(node)
        cb.items.addAll(values)
        if (add) {
            cb.setOnAction {
                if (node.text.isNullOrEmpty()) {
                    node.text = cb.value
                } else {
                    node.text += ", ${cb.value}"
                }
            }
        } else {
            cb.setOnAction {
                node.text = cb.value
            }
        }
        if (setFirst && node.text.isNullOrEmpty()) {
            cb.selectionModel.selectFirst()
            node.text = cb.selectionModel.selectedItem
        }
    }

    open fun init(patient: Patient, data: Patient.Data, visit: Visit) {
        this.patient = patient
        this.patientData = data
        this.visit = visit

        headerName.textProperty().bind(patient.name)
        headerBirthday.textProperty().bind(SimpleStringProperty(LocalDateConverter.toString(patient.birthday.value)))
        headerVisitDate.valueProperty().bindBidirectional(visit.date)

        bind(complaintsP1)

        bind(anamDiseaseP1)
        bind(anamDiseaseP2, "0/0")
        bind(anamDiseaseP3, "0/0")
        bind(anamDiseaseP4)

        var tmp1 = "Не имеет листка нетрудоспособности"
        if (!patientData.job.value.isNullOrEmpty()) {
            tmp1 += ", ${patientData.job.value}"
        }
        bind(anamInsuranceP1, tmp1)

        bind(objStatusP1, objStatusP1c, false, true, "удовлетворительное", "средней тяжести", "тяжелое")
        bind(objStatusP2)
        bind(objStatusP3)
        bind(objStatusP4)
        bind(objStatusP5)
        objStatusP5b.setOnAction {
            val h = objStatusP3.text.toDoubleOrNull()
            val m = objStatusP4.text.toDoubleOrNull()
            if (m == null || h == null || m < 0.0 || h <= 0.0) {
                objStatusP5.text = ""
            } else {
                val value = m / (h * h) * 10000
                objStatusP5.text = String.format("%.3f, %s", value, when {
                    value < 16.0        -> "выраженный дефицит массы тела"
                    value in 16.0..18.5 -> "недостаточная масса тела"
                    value in 18.5..25.0 -> "нормальная масса тела"
                    value in 25.0..30.0 -> "избыточная масса тела (предожирение)"
                    value in 30.0..35.0 -> "ожирение 1-ой степени"
                    value in 35.0..40.0 -> "ожирение 2-ой степени"
                    else                -> "ожирение 3-ой степени"
                })
            }
        }
        bind(objStatusP6, objStatusP6c, true, false, "чистые", "с высыпаниями", "обычной окраски",
             "гиперемированы", "бледные", "нормальной влажности", "гипергидроз", "сухие")
        bind(objStatusP7)
        bind(objStatusP8)
        bind(objStatusP9, "ЧСС=PS", "ЧЖС≠PS")
        bind(objStatusP10, "NN уд/мин.")
        bind(objStatusP11, objStatusP11c, true, false, "ритмичный", "аритмичный", "дефицита пульса нет",
             "удовлетворительное наполнение", "напряжение")
        bind(objStatusP12, objStatusP12c, true, false, "ясные", "звучные", "приглушены", "глухие", "ритмичные")
        bind(objStatusP13)
        bind(objStatusP14, objStatusP14c, true, false, "не выслушиваются", "систолический выслушивается",
             "диастолический выслушивается")
        bind(objStatusP15)
        bind(objStatusP16, objStatusP16c, true, false, "дыхание везикулярное", "дыхание жесткой", "дыхание жестковатое",
             "дыхание ослабленное", "дыхание везикулярное", "сухие хрипы", "влажные хрипы", "хрипов нет")
        bind(objStatusP17, objStatusP17c, true, false, "мягкий", "напряжен", "безболезненный", "болезненный")
        bind(objStatusP18, objStatusP18c, true, true, "ну уровне реберной дуги",
             "выступает из под края реберной дуги на NN см")
        bind(objStatusP19, objStatusP19c, true, true, "безболезненный с обеих сторон", "болезненный")
        bind(objStatusP20, objStatusP20c, true, true, "нет", "отеки голеней", "пастозность", "лёгкая пастозность")
        bind(objStatusP21, objStatusP21c, true, true, "не изменены", "запоры", "поносы", "неустойчивый стул",
             "частое мочеиспускание", "затруднение при мочеиспускание")

        bind(currHelpP1, true)
        bind(currHelpP2)

        bind(surveyPlanP1, """
            Общий анализ крови. Общий анализ мочи. Анализ крови на АСТ, АЛТ, Белирубин, Холестерин (Липиды), Креатинин. Коагулограмма №1. Анализ крови на гормоны ТТГ, Т4своб. Анализ крови на электролиты К, Са, Mg, Na, Cl.
            ФЛГ, ЭКГ, ЭХОКГ, Суточный монитор ЭКГ (Экг+АД).
            Консультация.
        """.trimIndent())

        bind(treatPlanP1)

        bind(recommsP1, """
            Дневник АД и пульса.
            Явка.
        """.trimIndent())

        bind(evnP1, true)
        bind(evnP2, "Нетрудоспособен(на)")
        bind(evnP3)
        bind(evnP4)
        bind(evnP5)

        paneDiagnosis.content = CardioDB.loadNode("DiagnosisTableLayout.fxml")
        controllerDiagnosis = CardioDB.getLoadedController()
        controllerDiagnosis.init(visit.diagnoses)
    }

    override fun onClose() {
        patient.recalcLastVisit(patientData)

        needUnbind.forEach {
            val other = visit.metaProp(it.id)
            when (it) {
                is TextInputControl -> other.unbindBidirectional(it.textProperty())
                is ComboBoxBase<*>  -> other.unbindBidirectional(it.valueProperty())
                is CheckBox         -> other.unbindBidirectional(it.selectedProperty())
            }
        }

        controllerDiagnosis.onClose()
    }
}