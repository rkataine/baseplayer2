package org.baseplayer.controllers;

import org.baseplayer.MainApp;
import org.baseplayer.draw.DrawSampleData;
import org.baseplayer.io.FileDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class MenuBarController {
    @FXML private TextField positionField;
    @FXML private HBox topBar;
    @FXML private MenuBar menuBar;

    public void initialize() {
        DrawSampleData.update.addListener((observable, oldValue, newValue) -> {
            if(MainController.hoverStack == null) return;
            positionField.setText("chr1:" + (int)MainController.hoverStack.start + " - " + (int)(MainController.hoverStack.end - 1));
        });
        menuBar.widthProperty().addListener((observable, oldValue, newValue) -> {
            menuBar.setMinWidth(newValue.doubleValue());
            menuBar.setMaxWidth(newValue.doubleValue());
        });
    }
    public void openFileMenu(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String[] types = menuItem.getId().split("_");
        String filtertype = types[1];
        boolean multiSelect = filtertype.equals("SES") ? false : true; // TODO myöhemmin kun avataan bam tai vcf trackille, refactoroi toimimaan myös sille
        /* FileDialog fileDialog =  */new FileDialog(menuItem.getText(), types[1], types[0], multiSelect);
    }
    public void addStack(ActionEvent event) { MainController.addStack(true); }
    public void removeStack(ActionEvent event) { MainController.addStack(false); }
    public void setDarkMode(ActionEvent event) { MainApp.setDarkMode(); }
    public void zoomout(ActionEvent event) { MainController.zoomout(); }
    public void cleanMemory(ActionEvent event) { System.gc(); }

    @FXML private Button minimizeButton;
    @FXML private Button maximizeButton;
    @FXML private Button closeButton;
    @FXML
    private void minimizeWindow() {
        Window window = MainApp.stage.getScene().getWindow();
        if (window instanceof Stage) {
            ((Stage) window).setIconified(true);
        }
    }

    @FXML
    private void maximizeWindow() {
        Window window = MainApp.stage.getScene().getWindow();
        if (window instanceof Stage) {
            Stage stage = (Stage) window;
            if (stage.isMaximized()) {
                Stage newStage = new Stage(StageStyle.DECORATED);
                newStage.setScene(MainApp.stage.getScene());
                newStage.getIcons().add(MainApp.icon);
                newStage.setTitle("BasePlayer 2");
                MainApp.stage.close();
                MainApp.stage = newStage;
                MainApp.stage.show();
                MainApp.stage.setMaximized(false);
            } else {
                Stage newStage = new Stage(StageStyle.UNDECORATED);
                newStage.setScene(MainApp.stage.getScene());
                MainApp.stage.close();
                MainApp.stage = newStage;
                MainApp.stage.show();
                MainApp.stage.setMaximized(true);
            }
        }
    }

    @FXML
    private void closeWindow() {
        Window window = MainApp.stage.getScene().getWindow();
        if (window instanceof Stage) {
            ((Stage) window).close();
        }
    }

}
