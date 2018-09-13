package ru.dahaka934.cardiodb

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import javafx.application.Application
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import ru.dahaka934.cardiodb.data.MKB10
import ru.dahaka934.cardiodb.data.PatientRegistry
import ru.dahaka934.cardiodb.util.IOTools
import ru.dahaka934.cardiodb.view.MainController
import ru.dahaka934.cardiodb.view.internal.Controller
import ru.dahaka934.cardiodb.view.internal.FXClassLoader
import java.util.concurrent.Executors

class CardioDB : Application() {
    companion object {
        lateinit var app: CardioDB
            private set

        private lateinit var loader: FXMLLoader
        private val fxClassLoader = FXClassLoader(FXMLLoader.getDefaultClassLoader())

        private val icons = Object2ObjectOpenHashMap<String, Image>()

        val registry = PatientRegistry()
        val executor = Executors.newSingleThreadExecutor()
        val user = SimpleStringProperty()

        @JvmStatic fun main(args: Array<String>) {
            Application.launch(CardioDB::class.java, *args)
        }

        fun <N : Node> loadNode(path: String): N {
            loader = FXMLLoader().apply {
                classLoader = fxClassLoader
            }
            return loader.load<N>(IOTools.resourceStream("/view/$path"))
        }

        fun <C> getLoadedController(): C = loader.getController()

        inline fun <C> createWindow(path: String, stage: Stage = Stage(), block: Stage.(C) -> Unit): Stage {
            return stage.apply {
                val node = loadNode<Parent>(path)
                icons.add(getIcon("main32"))
                scene = Scene(node)
                val controller = getLoadedController<C>()
                block(stage, controller)
                if (controller is Controller<*>) {
                    (controller as Controller<Node>).init(stage, node)
                    controller.init()
                }
            }
        }

        fun getIcon(name: String): Image {
            return icons.getOrPut(name) {
                Image(CardioDB::class.java.getResourceAsStream("/assets/icons/$name.png"))
            }
        }
    }

    init {
        app = this
    }

    override fun start(primaryStage: Stage) {
        MKB10.reloadAsync()
        AppProperties.reload()
        user.value = AppProperties.getProperty("user")
        CardioDB.createWindow<MainController>("MainLayout.fxml", primaryStage) {
            title = "CardioDB"
        }.show()
    }

    override fun stop() {
        registry.saveOnExitAsync()
        executor.shutdown()
        AppProperties.setProperty("user", user.value ?: "")
        AppProperties.save()
    }
}
