package ru.dahaka934.cardiodb.data

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener.Change
import ru.dahaka934.cardiodb.CardioDB
import ru.dahaka934.cardiodb.fxlib.IOTools
import ru.dahaka934.cardiodb.fxlib.completable
import ru.dahaka934.cardiodb.fxlib.thenApplyFX
import ru.dahaka934.cardiodb.fxlib.toDir
import ru.dahaka934.cardiodb.util.GsonHelper
import java.io.File
import java.util.concurrent.CompletableFuture

class PatientRegistry {
    private lateinit var patientMap: HashMap<Int, Patient>
    private lateinit var patientMapInv: HashMap<Patient, Int>
    private val dataMap = FXCollections.observableHashMap<Patient, Patient.Data>()
    private val saveRequests = ObjectOpenHashSet<Patient>()
    private var lastId: Int = 0

    val patients = FXCollections.observableArrayList<Patient>()
    val isDirty = SimpleBooleanProperty(false)

    private val dirRegistry = File("data").toDir()
    private val dirMapping = File(dirRegistry, "patients").toDir()
    private val fileHeader = File(dirRegistry, "patients.json")

    init {
        patients.addListener { c: Change<out Patient> ->
            while (c.next()) {
                c.addedSubList.removeIf {
                    if (patientMapInv.containsKey(it)) {
                        manageObject(it, null)
                        isDirty.value = true
                        false
                    } else true
                }
            }
        }
    }

    private fun reqId(patient: Patient): Int = patientMapInv[patient]!! // TODO: need fix it

    private fun manageObject(obj: Listenable, patient: Patient?) {
        if (patient == null) {
            obj.addListener {
                isDirty.value = true
            }
        } else {
            obj.addListener {
                isDirty.value = true
                saveRequests += patient
            }
        }
    }

    fun reloadAsync(): CompletableFuture<Unit> {
        return CardioDB.app.executor.completable {
            GsonHelper.readObjOrRewrite<HashMap<Int, Patient>>(fileHeader, ::HashMap)
        }.thenApplyFX {
            patientMap = it
            patientMapInv = patientMap.entries.associate { (k, v) -> v to k } as HashMap
            dataMap.clear()
            lastId = patientMap.keys.max() ?: -1

            patients.clear()
            patients.addAll(patientMap.values.toList())
            isDirty.value = false

            CardioDB.app.log("Reload patients data")
        }
    }

    fun getData(patient: Patient): Patient.Data {
        return dataMap.getOrPut(patient) {
            GsonHelper.readObjOrRewrite(File(dirMapping, "${reqId(patient)}.json"), Patient::Data).also {
                manageObject(it, patient)

                CardioDB.app.log("Read '${patient.name.value}' data")
            }
        }
    }

    fun savePatientsAsync(): CompletableFuture<Unit> {
        val str = GsonHelper.writeObj(patientMap)
        CardioDB.app.log("Saving patients data")
        return if (str != null) {
            CardioDB.app.executor.completable {
                IOTools.writeString(fileHeader, str, false)
            }
        } else {
            CompletableFuture.completedFuture(Unit)
        }
    }

    fun saveDataAsync(patient: Patient): CompletableFuture<Unit> {
        val data = dataMap[patient]
        return if (data != null) {
            val str = GsonHelper.writeObj(data)
            return if (str != null) {
                CardioDB.app.executor.completable {
                    IOTools.writeString(File(dirMapping, "${reqId(patient)}.json"), str, false)
                    CardioDB.app.log("Saving '${patient.name.value}' data")
                }
            } else {
                CompletableFuture.completedFuture(Unit)
            }
        } else {
            CompletableFuture.completedFuture(data)
        }
    }

    fun register(patient: Patient, data: Patient.Data) {
        if (patientMapInv[patient] != null) {
            return
        }

        ++lastId
        patientMap[lastId] = patient
        patientMapInv[patient] = lastId
        dataMap[patient] = data
        patients.add(patient)
        manageObject(data, patient)
        saveRequests += patient

        CardioDB.app.log("Register patient '${patient.name.value}' with id:$lastId")
    }

    fun unregister(patient: Patient) {
        val id = patientMapInv.remove(patient) ?: return
        patientMap.remove(id)
        dataMap -= patient
        patients -= patient
        saveRequests -= patient

        val file = File(dirMapping, "$id.json")
        if (file.exists()) {
            file.delete()
        }

        CardioDB.app.log("Unregister patient '${patient.name.value}' with id:$id")
    }

    fun saveAllAsync() {
        if (isDirty.value) {
            val list = ArrayList<CompletableFuture<Unit>>()

            list += savePatientsAsync()

            saveRequests.forEach {
                list += saveDataAsync(it)
            }
            saveRequests.clear()

            CompletableFuture.runAsync {
                list.forEach { it.join() }
                Backuper.makeBackupAuto()
            }

            isDirty.value = false
        }
    }
}