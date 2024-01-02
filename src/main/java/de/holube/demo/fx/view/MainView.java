package de.holube.demo.fx.view;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class MainView extends Pane {

    private final Label label;

    public MainView() {
        label = new Label("Hello World!");
        getChildren().add(label);
    }

    public void setText(String text) {
        label.setText(text);
    }

}
