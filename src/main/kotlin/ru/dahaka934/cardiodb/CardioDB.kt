package ru.dahaka934.cardiodb

import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage
import ru.dahaka934.cardiodb.data.PatientRegistry
import java.util.concurrent.Executors

class CardioDB : Application() {
    companion object {
        lateinit var app: CardioDB
            private set

        val icon = Image(CardioDB::class.java.getResourceAsStream("/assets/CardioDB.png"))

        val registry = PatientRegistry()
        val executor = Executors.newSingleThreadExecutor()

        @JvmStatic fun main(args: Array<String>) {
            Application.launch(CardioDB::class.java, *args)
        }
    }

    init {
        app = this
    }

    override fun start(primaryStage: Stage) {
        AppProperties.reload()
    }

    override fun stop() {
        registry.saveOnExitAsync()
        executor.shutdown()
        AppProperties.save()
    }
}
