package ru.dahaka934.cardiodb

import javafx.application.Application
import javafx.stage.Stage
import ru.dahaka934.cardiodb.data.MKB10
import ru.dahaka934.cardiodb.data.PatientRegistry
import ru.dahaka934.cardiodb.data.User
import ru.dahaka934.cardiodb.fxlib.FXApp
import ru.dahaka934.cardiodb.view.MainController
import java.util.concurrent.Executors

class CardioDB : FXApp() {
    companion object {
        lateinit var app: CardioDB
            private set

        @JvmStatic fun main(args: Array<String>) {
            Application.launch(CardioDB::class.java, *args)
        }
    }

    val registry = PatientRegistry()
    val executor = Executors.newSingleThreadExecutor()
    val user = User()

    init {
        app = this
    }

    override fun start(primaryStage: Stage) {
        MKB10.reloadAsync()
        AppProperties.reload()
        user.load()
        createWindow<MainController>("MainLayout.fxml", primaryStage) {
            title = "CardioDB"
        }.show()
    }

    override fun stop() {
        registry.saveAllAsync()
        user.save()
        executor.shutdown()
        AppProperties.save()
    }
}
