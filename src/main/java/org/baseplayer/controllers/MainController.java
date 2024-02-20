package org.baseplayer.controllers;

import org.baseplayer.MainApp;
import org.baseplayer.draw.DrawChromData;
import org.baseplayer.draw.DrawFunctions;
import org.baseplayer.draw.DrawSampleData;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;


public class MainController {
  @FXML private AnchorPane drawCanvas;
  @FXML private AnchorPane chromCanvas;
  @FXML private TextField positionField;
  @FXML private SplitPane chromSplit;
  @FXML private SplitPane drawSplit;
  @FXML private AnchorPane drawSideBar;
  @FXML private AnchorPane chromSideBar;
  @FXML private SplitPane mainSplit;
  @FXML private AnchorPane chromPane;

  public static boolean dividerHovered;
  public static boolean isActive = false;
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
      setWindowSizeListener();
      setSplitPaneDividerListener();
  }
  void setWindowSizeListener() {   
    mainSplit.setOnMouseEntered(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        isActive = true;
        setWidthConstraints();
      }
    });
    mainSplit.setOnMouseExited(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {  
        isActive = false;
        setWidthConstraints();
      }
    });
  }
  void setSplitPaneDividerListener() {
    chromSplit.getDividers().get(0).positionProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (!isActive) return;
        drawSplit.getDividers().get(0).setPosition(newValue.doubleValue());
      }
    });
    drawSplit.getDividers().get(0).positionProperty().addListener(new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
          if (!isActive) return;
          chromSplit.getDividers().get(0).setPosition(newValue.doubleValue());
        }
    });
  }
  void setWidthConstraints() {
    if (isActive) {
      drawSideBar.setMinWidth(Region.USE_COMPUTED_SIZE);
      drawSideBar.setMaxWidth(Region.USE_COMPUTED_SIZE);
      chromSideBar.setMinWidth(Region.USE_COMPUTED_SIZE);
      chromSideBar.setMaxWidth(Region.USE_COMPUTED_SIZE);
      chromPane.setMinHeight(Region.USE_COMPUTED_SIZE);
      chromPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
      return;
    }
    drawSideBar.setMinWidth(drawSideBar.getWidth());
    drawSideBar.setMaxWidth(drawSideBar.getWidth());
    chromSideBar.setMinWidth(chromSideBar.getWidth());
    chromSideBar.setMaxWidth(chromSideBar.getWidth());
    chromPane.setMinHeight(chromPane.getHeight());
    chromPane.setMaxHeight(chromPane.getHeight());
  }
}
