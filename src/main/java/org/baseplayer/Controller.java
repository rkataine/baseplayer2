package org.baseplayer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;

public class Controller {
  @FXML
  private MenuBar menuBar; // refers to Main.fxml's MenuBar
  public void openFileMenu(ActionEvent event) {
    FileDialog.chooseFiles();
  }
}
