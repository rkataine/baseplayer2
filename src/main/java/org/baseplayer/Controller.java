package org.baseplayer;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;


public class Controller {
  @FXML
  private AnchorPane drawCanvas;

  public void initialize() {
    
    new DrawSampleData(drawCanvas);
    
  }
  
}
