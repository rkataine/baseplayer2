package org.baseplayer.controllers;

import java.util.ArrayList;

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

  private ArrayList<DrawSampleData> drawCanvases = new ArrayList<>();
  
  public void setDarkMode(ActionEvent event) {
    MainApp.setDarkMode();
  }
  public void zoomout(ActionEvent event) {
    for (DrawSampleData drawSampleData : drawCanvases) drawSampleData.zoomout();
  }
  public void initialize() {
    Canvas reactiveCanvas = new Canvas();
    Canvas reactiveCanvas2 = new Canvas();
    DrawSampleData canvas = new DrawSampleData(reactiveCanvas);
    DrawChromData cCanvas = new DrawChromData(reactiveCanvas2);
    drawCanvases.add(canvas);
    canvas.heightProperty().bind(drawCanvas.heightProperty());
    canvas.widthProperty().bind(drawCanvas.widthProperty());
    cCanvas.heightProperty().bind(chromCanvas.heightProperty());
    cCanvas.widthProperty().bind(chromCanvas.widthProperty());

    drawCanvas.getChildren().addAll(canvas, reactiveCanvas);
    chromCanvas.getChildren().addAll(cCanvas, reactiveCanvas2);
    
    DrawSampleData.update.addListener((observable, oldValue, newValue) -> { 
      canvas.draw();
      cCanvas.draw();
      positionField.setText("Chr1:" + (int)DrawFunctions.start + " - " + (int)(DrawFunctions.end - 1));
    });
  }
  
}
