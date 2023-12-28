package de.holube.noel;

import javafx.application.Preloader;
import javafx.stage.Stage;

public class NoElPreloader extends Preloader {

    @Override
    public void start(Stage primaryStage) {
        com.sun.glass.ui.Application.GetApplication().setName("NoEl");
    }

}
