package ru.dahaka934.cardiodb.view.internal

import javafx.application.Platform
import javafx.scene.Node
import javafx.stage.Stage

abstract class Controller<N : Node> {
    lateinit var stage: Stage
        private set
    lateinit var node: N
        private set

    fun init(stage: Stage, node: N) {
        this.stage = stage
        this.node = node
    }

    open fun init() {}

    fun focusNode() {
        Platform.runLater { node.requestFocus() }
    }
}