package ru.dahaka934.cardiodb.view

import javafx.scene.control.ScrollPane
import ru.dahaka934.cardiodb.data.Patient
import ru.dahaka934.cardiodb.data.Patient.Data
import ru.dahaka934.cardiodb.data.Visit
import ru.dahaka934.cardiodb.util.DocCreator
import ru.dahaka934.cardiodb.util.createRun
import java.io.File

class SecondaryVisitController : BaseVisitController<ScrollPane>() {

    override fun init(patient: Patient, data: Data, visit: Visit) {
        super.init(patient, data, visit)

        objStatusP3.text = ""
        objStatusP4.text = ""
        objStatusP5.text = ""
        objStatusP3.isDisable = true
        objStatusP4.isDisable = true
        objStatusP5.isDisable = true
        objStatusP5b.isDisable = true
    }

    companion object {
        fun generateDocReception(file: File, patient: Patient, data: Data, visit: Visit): Boolean {
            val creator = DocCreator()
            creator.paragraph {
                style = "Top"
                createRun("ПОВТОРНЫЙ ПРИЕМ", bold = true)
            }

            genDocHeader(creator, patient, data, visit)
            genDocComplaints(creator, patient, data, visit)
            genDocObjStatus(creator, patient, data, visit, false)
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
            genPlace(creator)
            creator.paragraph {
                style = "Top"
                createRun("КОНСУЛЬТАТИВНОЕ ЗАКЛЮЧЕНИЕ", bold = true)
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