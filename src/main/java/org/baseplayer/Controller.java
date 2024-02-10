package org.baseplayer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;

public class Controller {
  @FXML
  private MenuBar menuBar; // refers to Main.fxml's MenuBar
  public void openFileMenu(ActionEvent event) {
    System.out.println("Open File Menu22");
    menuBar.setStyle("-fx-background-color: #336699;");
  }
}
