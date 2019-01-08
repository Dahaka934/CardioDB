package ru.dahaka934.cardiodb.fxlib

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import ru.dahaka934.cardiodb.CardioDB

abstract class FXApp : Application() {
    private val fxClassLoader = FXClassLoader(FXMLLoader.getDefaultClassLoader())

    private val icons = Object2ObjectOpenHashMap<String, Image>()

    private lateinit var loader: FXMLLoader

    fun <N : Node> loadNode(path: String): N {
        loader = FXMLLoader().apply {
            classLoader = fxClassLoader
        }
        return loader.load<N>(IOTools.resourceStream("/view/$path"))
    }

    fun getIcon(name: String): Image {
        return icons.getOrPut(name) {
            Image(CardioDB::class.java.getResourceAsStream("/assets/icons/$name.png"))
        }
    }

    fun <C> getLoadedController(): C = loader.getController()

    inline fun <C> createWindow(path: String, stage: Stage = Stage(), block: Stage.(C) -> Unit): Stage {
        return stage.apply {
            val node = loadNode<Parent>(path)
            icons.add(getIcon("main"))
            scene = Scene(node)
            val controller = getLoadedController<C>()
            block(stage, controller)
            if (controller is FXController<*>) {
                (controller as FXController<Node>).init(stage, node)
                controller.init()
            }
        }
    }

    fun log(str: String) {
        println(str) // TODO: Maybe log?
    }
}