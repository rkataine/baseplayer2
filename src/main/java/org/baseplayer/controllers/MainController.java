package org.baseplayer.controllers;

import org.baseplayer.draw.DrawChromData;
import org.baseplayer.draw.DrawFunctions;
import org.baseplayer.draw.DrawSampleData;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import org.baseplayer.utils.BaseUtils;

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
  @FXML private Label memLabel;

  public static boolean dividerHovered;
  public static boolean isActive = false;
  public static AnchorPane staticDraw;
  Runtime instance = Runtime.getRuntime();
  IntegerProperty memoryUsage = new SimpleIntegerProperty(0);

  public void initialize() {
      staticDraw = drawCanvas;
      memoryUsage.addListener((observable, oldValue, newValue) -> {
        int maxMem = BaseUtils.toMegabytes.apply(instance.maxMemory());
        int proportion = (int)(BaseUtils.round((newValue.doubleValue() / maxMem), 2) * 100);
        if (proportion > 80) memLabel.setStyle("-fx-text-fill: red;");
        else memLabel.setStyle("-fx-text-fill: white;");
        memLabel.setText(BaseUtils.formatNumber(newValue.intValue()) +" / " +BaseUtils.formatNumber(maxMem)  +"MB ( " +proportion +"% )" );
      });

      DrawChromData cCanvas = new DrawChromData(new Canvas(), chromCanvas);
      DrawSampleData canvas = new DrawSampleData(new Canvas(), drawCanvas);
      
      chromCanvas.getChildren().addAll(cCanvas, cCanvas.getReactiveCanvas());
      drawCanvas.getChildren().addAll(canvas, canvas.getReactiveCanvas());
      
      DrawSampleData.update.addListener((observable, oldValue, newValue) -> {
        cCanvas.draw();
        canvas.draw();
        memoryUsage.set(BaseUtils.toMegabytes.apply(instance.totalMemory() - instance.freeMemory()));
      });      
      setWindowSizeListener();
      setSplitPaneDividerListener();
  }
  public static void zoomout() {
    DrawFunctions.zoomAnimation(1, DrawFunctions.chromSize, (DrawFunctions)staticDraw.getChildren().get(0));
  }
  void setWindowSizeListener() {
    mainSplit.setOnMouseEntered(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        isActive = true;
        DrawFunctions.update.set(!DrawFunctions.update.get());
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
    drawCanvas.setOnMouseExited(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {  
        takeSnapshot();
      }
    });
    drawCanvas.setOnMouseEntered(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {  
        DrawFunctions.update.set(!DrawFunctions.update.get());
      }
    });
  }
  void takeSnapshot() {
    DrawFunctions.snapshot = drawCanvas.snapshot(null, null);
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
