package ru.dahaka934.cardiodb.view

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.util.converter.BooleanStringConverter
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER
import ru.dahaka934.cardiodb.CardioDB
import ru.dahaka934.cardiodb.data.Patient
import ru.dahaka934.cardiodb.data.Patient.Data
import ru.dahaka934.cardiodb.data.Visit
import ru.dahaka934.cardiodb.fxlib.internal.LocalDateConverter
import ru.dahaka934.cardiodb.fxlib.internal.LocalDateISOConverter
import ru.dahaka934.cardiodb.util.DocCreator
import ru.dahaka934.cardiodb.util.createRun
import ru.dahaka934.cardiodb.view.MainController.ControllerTab
import java.math.BigInteger

abstract class BaseVisitController<N : Node> : ControllerTab<N>() {
    private lateinit var patient: Patient
    private lateinit var patientData: Patient.Data
    private lateinit var visit: Visit

    private val needUnbind = ArrayList<Node>()

    @FXML lateinit var headerName: Label
    @FXML lateinit var headerBirthday: Label
    @FXML lateinit var headerVisitDate: DatePicker

    @FXML lateinit var complaintsP1: TextArea

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
    @FXML lateinit var objStatusP17S1: TextField
    @FXML lateinit var objStatusP17S1c: ComboBox<String>
    @FXML lateinit var objStatusP18: TextField
    @FXML lateinit var objStatusP18c: ComboBox<String>
    @FXML lateinit var objStatusP19: TextField
    @FXML lateinit var objStatusP19c: ComboBox<String>
    @FXML lateinit var objStatusP20: TextField
    @FXML lateinit var objStatusP20c: ComboBox<String>
    @FXML lateinit var objStatusP21: TextField
    @FXML lateinit var objStatusP21c: ComboBox<String>
    @FXML lateinit var objStatusP22: ComboBox<String>

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
        node.textProperty().bindBidirectional(visit.metaProperty(node.id, defValue))
        needUnbind += node
    }

    fun bind(node: ComboBox<String>, vararg values: String) {
        node.valueProperty().bindBidirectional(visit.metaProperty(node.id, values.getOrNull(0) ?: ""))
        node.items.addAll(values)
        needUnbind += node
    }

    fun bind(node: DatePicker, defValue: String = "") {
        node.valueProperty().let {
            val p = visit.metaProperty(node.id, defValue)
            it.set(LocalDateISOConverter.fromString(p.value))
            Bindings.bindBidirectional(p, it, LocalDateISOConverter)
        }
        needUnbind += node
    }

    fun bind(node: CheckBox, defValue: Boolean) {
        node.selectedProperty().let {
            val p = visit.metaProperty(node.id, defValue.toString())
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
        headerBirthday.textProperty().bind(SimpleStringProperty(
            LocalDateConverter.toString(patient.birthday.value)))
        headerVisitDate.valueProperty().bindBidirectional(visit.date)

        bind(complaintsP1)

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
        bind(objStatusP10, "уд/мин.")
        bind(objStatusP11, objStatusP11c, true, false, "ритмичный", "аритмичный", "дефицита пульса нет",
             "удовлетворительного наполнения и напряжения", "напряжён")
        bind(objStatusP12, objStatusP12c, true, false, "ясные", "звучные", "приглушены", "глухие", "ритмичные",
             "аритмичные")
        bind(objStatusP13)
        bind(objStatusP14, objStatusP14c, true, false, "не выслушиваются", "систолический выслушивается",
             "диастолический выслушивается")
        bind(objStatusP15, "в мин.")
        bind(objStatusP16, objStatusP16c, true, false, "везикулярное", "жесткое", "жестковатое",
             "ослабленное", "сухие хрипы", "влажные хрипы", "хрипов нет", "не выслушивается")
        bind(objStatusP17, objStatusP17c, false, true, "мягкий", "напряженный")
        bind(objStatusP17S1, objStatusP17S1c, false, true, "безболезненный", "чувствительный", "болезненный")
        bind(objStatusP18, objStatusP18c, false, true, "на уровне реберной дуги",
             "выступает из-под края реберной дуги на см")
        bind(objStatusP19, objStatusP19c, false, true, "безболезненное с обеих сторон", "болезненное", "чувствительное")
        bind(objStatusP20, objStatusP20c, false, true, "нет", "отёки голеней", "пастозность голеней",
             "лёгкая пастозность голеней")
        bind(objStatusP21, objStatusP21c, true, true, "не изменены", "запоры", "поносы", "неустойчивый стул",
             "частое мочеиспускание", "затруднение при мочеиспускание")
        bind(objStatusP22, "клинический", "предварительный")

        bind(currHelpP1, true)
        bind(currHelpP2)

        bind(surveyPlanP1, """
            Общий анализ крови. Общий анализ мочи. Анализ крови на глюкозу АСТ, АЛТ, билирубин, холестерин (липиды), креатинин. Коагулограмма №1. Анализ крови на гормоны ТТГ, Т4своб. Анализ крови на электролиты К, Са, Mg, Na, Cl.
            УЗИ почек, Дуплексное сканирование БЦА, Осмотр окулиста (глазное дно).
            ФЛГ, ЭКГ, ЭХОКГ, Суточный монитор ЭКГ (ЭКГ+АД).
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

        paneDiagnosis.content = CardioDB.app.loadNode("DiagnosisTableLayout.fxml")
        controllerDiagnosis = CardioDB.app.getLoadedController()
        controllerDiagnosis.init(visit.diagnoses)
    }

    override fun onClose() {
        patient.recalcLastVisit(patientData)

        needUnbind.forEach {
            val other = visit.metaProperty(it.id)
            when (it) {
                is TextInputControl -> other.unbindBidirectional(it.textProperty())
                is ComboBoxBase<*>  -> other.unbindBidirectional(it.valueProperty())
                is CheckBox         -> other.unbindBidirectional(it.selectedProperty())
            }
        }

        controllerDiagnosis.onClose()
    }

    companion object {
        fun convertedDate(dateISO: String): String {
            return LocalDateConverter.toString(
                LocalDateISOConverter.fromString(dateISO))
        }

        fun String.dot(): String {
            if (this.isEmpty()) {
                return "."
            } else if (this.last() == '.') {
                return this
            }
            return this + '.'
        }

        infix fun String.with(str: String): String {
            return when {
                this.isEmpty() -> str
                str.isEmpty()  -> this
                else           -> "$this, $str"
            }
        }

        fun genDocHeader(cr: DocCreator, patient: Patient, data: Data, visit: Visit) {
            cr.paragraph {
                createRun("ФИО пациента:  ")
                createRun(patient.name.value.dot(), bold = true) {
                    fontSize = 14
                }
                createRun("                 " +
                          "Дата рождения:  ${LocalDateConverter.toString(patient.birthday.value).dot()}")
            }

            cr.line()
            cr.line("Дата консльтации:  ${LocalDateConverter.toString(patient.lastVisit.value).dot()}")
            cr.line()
        }

        fun genDocComplaints(cr: DocCreator, patient: Patient, data: Data, visit: Visit) {
            cr.line("ЖАЛОБЫ", 9, true)
            cr.lineSplit(visit.meta("complaintsP1").dot())
        }

        fun genDocObjStatus(cr: DocCreator, patient: Patient, data: Data, visit: Visit, primary: Boolean) {
            cr.line("ОБЪЕКТИВНЫЙ СТАТУС", 9, true)
            cr.line("Общее состояние: ${visit.meta("objStatusP1").dot()} " +
                    "Температура: ${visit.meta("objStatusP2")}C.")
            if (primary) {
                cr.line("Рост: ${visit.meta("objStatusP3")}см. " +
                        "Масса тела: ${visit.meta("objStatusP4")}см. " +
                        "ИМТ: ${visit.meta("objStatusP5").dot()}")
            }
            cr.line("Кожные покровы ${visit.meta("objStatusP6").dot()}")
            cr.line("АД: левая рука ${visit.meta("objStatusP7")}мм.рт.ст., " +
                    "правая рука ${visit.meta("objStatusP8")}мм.рт.ст. " +
                    "${visit.meta("objStatusP9")}: ${visit.meta("objStatusP10")} уд/мин, " +
                    visit.meta("objStatusP11").dot())
            cr.line("Тоны сердца ${visit.meta("objStatusP12")}, " +
                    "акцент ${visit.meta("objStatusP13")}, " +
                    "шумы ${visit.meta("objStatusP14").dot()}")
            cr.line("ЧДД - ${visit.meta("objStatusP15")} в мин. Дыхание ${visit.meta("objStatusP16").dot()}")
            cr.line("Живот ${(visit.meta("objStatusP17") with visit.meta("objStatusP17S1")).dot()} " +
                    "Печень ${visit.meta("objStatusP18").dot()} " +
                    "Поколачивание по пояснице ${visit.meta("objStatusP19").dot()} " +
                    "Периферические отёки: ${visit.meta("objStatusP20").dot()}")
            cr.line("Физиологические отправления ${visit.meta("objStatusP21")} (со слов).")
        }

        fun genDocDiagnosis(cr: DocCreator, patient: Patient, data: Data, visit: Visit) {
            cr.line("${visit.meta("objStatusP22").toUpperCase()} ДИАГНОЗ", 9, true)
            cr.doc.createTable(visit.diagnoses.size + 1, 3).apply {
                getRow(0).apply {
                    getCell(0).apply {
                        ctTc.addNewTcPr().addNewNoWrap()
                        ctTc.addNewTcPr().addNewTcW().w = BigInteger.valueOf(3000)
                        paragraphs[0].apply {
                            alignment = ParagraphAlignment.CENTER
                            createRun("Тип диагноза", bold = true)
                        }
                    }
                    getCell(1).apply {
                        ctTc.addNewTcPr().addNewNoWrap()
                        ctTc.addNewTcPr().addNewTcW().w = BigInteger.valueOf(3000)
                        paragraphs[0].apply {
                            alignment = ParagraphAlignment.CENTER
                            createRun("Код МКБ-10", bold = true)
                        }
                    }
                    getCell(2).apply {
                        ctTc.addNewTcPr().addNewNoWrap()
                        ctTc.addNewTcPr().addNewTcW().w = BigInteger.valueOf(11000)
                        paragraphs[0].apply {
                            alignment = ParagraphAlignment.CENTER
                            createRun("Наименование", bold = true)
                        }
                    }
                }
                visit.diagnoses.forEachIndexed { i, it ->
                    getRow(i + 1).apply {
                        getCell(0).apply {
                            paragraphs[0].apply {
                                alignment = ParagraphAlignment.CENTER
                                createRun(it.type.value.localName)
                            }
                        }
                        getCell(1).apply {
                            paragraphs[0].apply {
                                alignment = ParagraphAlignment.CENTER
                                createRun(it.mkbID.value)
                            }
                        }
                        getCell(2).apply {
                            paragraphs[0].apply {
                                alignment = ParagraphAlignment.LEFT
                                createRun(it.info.value)
                            }
                        }
                    }
                }
            }
        }

        fun genDocCurrHelp(cr: DocCreator, patient: Patient, data: Data, visit: Visit) {
            val hide = visit.meta("currHelpP1").toBoolean()
            if (hide) return

            cr.line("ОКАЗАННАЯ МЕДИКОМЕНТОЗНАЯ ПОМОЩЬ В КАБИНЕТЕ ВРАЧА", 9, true)
            cr.lineSplit(visit.meta("currHelpP2").dot())
        }

        fun genDocSurveyPlan(cr: DocCreator, patient: Patient, data: Data, visit: Visit) {
            cr.line("ПЛАН ОБСЛЕДОВАНИЯ", 9, true)
            cr.lineSplit(visit.meta("surveyPlanP1"))
        }

        fun genDocTreatPlan(cr: DocCreator, patient: Patient, data: Data, visit: Visit) {
            cr.line("ПЛАН ЛЕЧЕНИЯ", 9, true)
            cr.lineSplit(visit.meta("treatPlanP1").dot())
        }

        fun genDocRecomms(cr: DocCreator, patient: Patient, data: Data, visit: Visit) {
            cr.line("РЕКОМЕНДАЦИИ", 9, true)
            cr.lineSplit(visit.meta("recommsP1").dot())
        }

        fun genDocEVN(cr: DocCreator, patient: Patient, data: Data, visit: Visit) {
            val hide = visit.meta("evnP1").toBoolean()
            if (hide) return

            cr.line("ЭВН", 9, true)
            cr.lineSplit(visit.meta("evnP2").dot())
            cr.line("Выдан первичный листок нетрудоспособности №${visit.meta("evnP3")} " +
                    "с ${convertedDate(visit.meta("evnP4"))} по ${convertedDate(visit.meta("evnP5"))}г.")
            cr.line("Явка ${convertedDate(visit.meta("evnP5"))}г.")
        }

        fun genPlace(cr: DocCreator) {
            cr.paragraph {
                style = "Top"
                createRun(CardioDB.app.user.place, 9, true)
            }
        }

        fun genSignature(cr: DocCreator) {
            cr.line()
            cr.line()
            cr.line()
            cr.paragraph {
                createRun("Специалист ____________________ врач кардиолог   ${CardioDB.app.user.name}", bold = true) {
                    alignment = CENTER
                    fontSize = 10
                }
            }
        }
    }
}