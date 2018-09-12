package ru.dahaka934.cardiodb

import javafx.application.Application
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import javafx.stage.Stage
import ru.dahaka934.cardiodb.data.MKB10
import ru.dahaka934.cardiodb.data.PatientRegistry
import java.util.concurrent.Executors

class CardioDB : Application() {
    companion object {
        lateinit var app: CardioDB
            private set

        val icon = Image(CardioDB::class.java.getResourceAsStream("/assets/CardioDB.png"))

        val registry = PatientRegistry()
        val executor = Executors.newSingleThreadExecutor()
        val user = SimpleStringProperty()

        @JvmStatic fun main(args: Array<String>) {
            Application.launch(CardioDB::class.java, *args)
        }
    }

    init {
        app = this
    }

    override fun start(primaryStage: Stage) {
        MKB10.reloadAsync()
        AppProperties.reload()
        user.value = AppProperties.getProperty("user")
    }

    override fun stop() {
        registry.saveOnExitAsync()
        executor.shutdown()
        AppProperties.save()
        AppProperties.setProperty("user", user.value ?: "")
    }
}
