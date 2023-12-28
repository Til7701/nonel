package de.holube.noel;

import com.sun.javafx.application.LauncherImpl;
import de.holube.noel.fx.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NoElApplication extends Application {

    public static void main(String[] args) {
        LauncherImpl.launchApplication(NoElApplication.class, NoElPreloader.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView();
        Scene scene = new Scene(mainView, 320, 240);
        primaryStage.setTitle("NoEl");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
