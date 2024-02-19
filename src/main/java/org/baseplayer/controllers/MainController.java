package org.baseplayer.controllers;

import org.baseplayer.MainApp;
import org.baseplayer.draw.DrawChromData;
import org.baseplayer.draw.DrawFunctions;
import org.baseplayer.draw.DrawSampleData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;


public class MainController {
  @FXML
  private AnchorPane drawCanvas;
  @FXML
  private AnchorPane chromCanvas;
  @FXML
  private TextField positionField;

  public void setDarkMode(ActionEvent event) { MainApp.setDarkMode(); }
  public void zoomout(ActionEvent event) { ((DrawFunctions)drawCanvas.getChildren().get(0)).zoomout(); }

  public void initialize() {
    DrawChromData cCanvas = new DrawChromData(new Canvas(), chromCanvas);
    DrawSampleData canvas = new DrawSampleData(new Canvas(), drawCanvas);
    
    chromCanvas.getChildren().addAll(cCanvas, cCanvas.getReactiveCanvas());
    drawCanvas.getChildren().addAll(canvas, canvas.getReactiveCanvas());
    
    DrawSampleData.update.addListener((observable, oldValue, newValue) -> {
      cCanvas.draw();
      canvas.draw();
      
      positionField.setText("Chr1:" + (int)DrawFunctions.start + " - " + (int)(DrawFunctions.end - 1));
    });
  }
  
}
