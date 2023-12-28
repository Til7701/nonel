package de.holube.noel;

import de.holube.noel.fx.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.Taskbar.Feature;

public class NoElApplication extends Application {

    public static void main(String[] args) {
        System.setProperty("javafx.preloader", "de.holube.noel.NoElPreloader");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        setupIcons(primaryStage);
        MainView mainView = new MainView();
        Scene scene = new Scene(mainView, 320, 240);
        primaryStage.setTitle("NoEl");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupIcons(Stage primaryStage) {
        //Set icon on the application bar
        Image appIcon = new Image(String.valueOf(getClass().getResource("/icons/noel-icon.png")));
        primaryStage.getIcons().add(appIcon);

        //Set icon on the taskbar/dock
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            if (taskbar.isSupported(Feature.ICON_IMAGE)) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                final java.awt.Image dockIcon = defaultToolkit.getImage(getClass().getResource("/icons/noel-icon.png"));
                taskbar.setIconImage(dockIcon);
            }
        }
    }

}
