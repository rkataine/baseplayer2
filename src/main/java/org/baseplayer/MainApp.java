package org.baseplayer;

import java.io.IOException;
import java.net.URL;

import org.baseplayer.draw.DrawFunctions;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application {
    public static Stage stage;
    static Scene scene;
    public static boolean darkMode = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        scene = new Scene(loadFXML("Main"));       
        scene.getStylesheets().add(getResource("styles.css").toExternalForm());
        setDarkMode();       
        Image icon = new Image(getResource("BasePlayer_icon.png").toString());
        stage.getIcons().add(icon);
        stage.setTitle("BasePlayer 2");
        stage.setScene(scene);
        stage.setFullScreen(false);       
        stage.show();
    }
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    public static void setDarkMode() {
        
        if (darkMode) scene.getStylesheets().remove(getResource("darkmode.css").toExternalForm());
        else scene.getStylesheets().add(getResource("darkmode.css").toExternalForm());
        DrawFunctions.lineColor = darkMode ? DrawFunctions.lineColor = new Color(0.3, 0.6, 0.6, 0.5) : new Color(0.5, 0.8, 0.8, 0.5);
        darkMode = !darkMode;
        DrawFunctions.update.set(!DrawFunctions.update.get());
    }
    static URL getResource(String string) { return MainApp.class.getResource(string); }
    public static void main(String[] args) { launch(args); }
}
