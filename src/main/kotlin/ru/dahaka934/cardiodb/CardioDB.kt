package ru.dahaka934.cardiodb

import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage

class CardioDB : Application() {
    companion object {
        lateinit var app: CardioDB
            private set

        val icon = Image(CardioDB::class.java.getResourceAsStream("/assets/CardioDB.png"))

        @JvmStatic fun main(args: Array<String>) {
            Application.launch(CardioDB::class.java, *args)
        }
    }

    init {
        app = this
    }

    override fun start(primaryStage: Stage) {

    }

    override fun stop() {

    }
}
