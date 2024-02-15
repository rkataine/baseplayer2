package org.baseplayer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;


public class Controller {
  @FXML
  private AnchorPane drawCanvas;
  private boolean darkMode = false;
  public void setDarkMode(ActionEvent event) {
    if (darkMode) {
      MainApp.stage.getScene().getStylesheets().remove(MainApp.class.getResource("darkmode.css").toExternalForm());
      darkMode = false;
    } else {
      MainApp.stage.getScene().getStylesheets().add(MainApp.class.getResource("darkmode.css").toExternalForm());
      darkMode = true;
    }
  }    
  public void initialize() {
    
    new DrawSampleData(drawCanvas);
    
  }
  
}
