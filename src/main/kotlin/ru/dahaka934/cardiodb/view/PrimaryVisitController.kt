package ru.dahaka934.cardiodb.view

import javafx.fxml.FXML
import javafx.scene.control.DatePicker
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import ru.dahaka934.cardiodb.data.Patient
import ru.dahaka934.cardiodb.data.Patient.*
import ru.dahaka934.cardiodb.data.Patient.Sex.MALE
import ru.dahaka934.cardiodb.data.Visit
import ru.dahaka934.cardiodb.util.DocCreator
import ru.dahaka934.cardiodb.util.createRun
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
        fun genDocAnamLife(cr: DocCreator, patient: Patient, data: Data, visit: Visit) {
            cr.line("АНАМНЕЗ ЖИЗНИ", true)
            cr.line("Перенесенные заболевания, травмы, операции: ${visit.meta("anamLifeP1").dot()}")
            cr.line("Гепатит: ${visit.meta("anamLifeP2").dot()} " +
                    "B24: ${visit.meta("anamLifeP3").dot()} " +
                    "Tbc: ${visit.meta("anamLifeP4").dot()} " +
                    "ЗППП: ${visit.meta("anamLifeP5").dot()} ")
            cr.line("Проф. вредности: ${visit.meta("anamLifeP6").dot()} " +
                    "Наслед. ${visit.meta("anamLifeP7").dot()}")

            if (data.sex == Sex.FEMALE) {
                cr.line("Последнее посещение гинеколога: ${convertedDate(visit.meta("anamLifeP8")).dot()}")
            }
            cr.line("Последняя ФЛГ орг. гр. кл. (со слов): ${convertedDate(visit.meta("anamLifeP9")).dot()}")
        }

        fun genDocBHabits(cr: DocCreator, patient: Patient, data: Data, visit: Visit) {
            cr.line("ПАГУБНЫЕ ПРИВЫЧКИ", true)
            cr.line("Курение: ${visit.meta("bhabitsP1").dot()}")
            cr.line("Злоупотребление алкоголем: ${visit.meta("bhabitsP2").dot()}")
            cr.line("Прием ПАВ: ${visit.meta("bhabitsP3").dot()}")
        }

        fun genDocAllergies(cr: DocCreator, patient: Patient, data: Data, visit: Visit) {
            cr.line("АЛЛЕРГОЛОГИЧЕСКИЙ АНАМНЕЗ", true)
            cr.line("Лекарственные: ${visit.meta("allergiesP1").dot()}")
            cr.line("Пищевые: ${visit.meta("allergiesP2").dot()}")
            cr.line("Прочие: ${visit.meta("allergiesP3").dot()}")
        }

        fun generateDocReception(file: File, patient: Patient, data: Data, visit: Visit): Boolean {
            val creator = DocCreator()
            creator.paragraph {
                style = "Top"
                createRun("ПЕРВИЧНЫЙ ПРИЕМ", true)
            }

            genDocHeader(creator, patient, data, visit)
            genDocComplaints(creator, patient, data, visit)
            genDocDisease(creator, patient, data, visit)
            genDocAnamLife(creator, patient, data, visit)
            genDocBHabits(creator, patient, data, visit)
            genDocAllergies(creator, patient, data, visit)
            genDocAnamInsurance(creator, patient, data, visit)
            genDocObjStatus(creator, patient, data, visit)
            genDocDiagnosis(creator, patient, data, visit)
            genDocCurrHelp(creator, patient, data, visit)
            genDocSurveyPlan(creator, patient, data, visit)
            genDocTreatPlan(creator, patient, data, visit)
            genDocRecomms(creator, patient, data, visit)
            genDocEVN(creator, patient, data, visit)
            genSignature(creator)

            return creator.save(file)
        }

        fun generateDocConclusion(file: File, patient: Patient, data: Data, visit: Visit): Boolean {
            val creator = DocCreator()
            creator.paragraph {
                style = "Top"
                createRun("ПЕРВИЧНЫЙ ПРИЕМ", true)
            }

            genDocHeader(creator, patient, data, visit)
            genDocDiagnosis(creator, patient, data, visit)
            genDocCurrHelp(creator, patient, data, visit)
            genDocSurveyPlan(creator, patient, data, visit)
            genDocTreatPlan(creator, patient, data, visit)
            genDocRecomms(creator, patient, data, visit)
            genDocEVN(creator, patient, data, visit)
            genSignature(creator)

            return creator.save(file)
        }
    }
}