package org.baseplayer.controllers;

import java.util.ArrayList;

import org.baseplayer.MainApp;
import org.baseplayer.draw.DrawSampleData;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;


public class MainController {
  @FXML
  private AnchorPane drawCanvas;
  private ArrayList<DrawSampleData> drawCanvases = new ArrayList<>();
  
  public void setDarkMode(ActionEvent event) {
    MainApp.setDarkMode();
  }
  public void zoomout(ActionEvent event) {
    for (DrawSampleData drawSampleData : drawCanvases) drawSampleData.zoomout();
  }
  public void initialize() {
    DrawSampleData canvas = new DrawSampleData();
    drawCanvases.add(canvas);
    canvas.heightProperty().bind(drawCanvas.heightProperty());
    canvas.widthProperty().bind(drawCanvas.widthProperty());
    drawCanvas.getChildren().add(canvas);
  }
  
}
