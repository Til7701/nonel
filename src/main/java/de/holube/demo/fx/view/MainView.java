package de.holube.demo.fx.view;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class MainView extends Pane {

    public MainView() {
        final Label label = new Label("Hello World!");
        getChildren().add(label);
    }

}
