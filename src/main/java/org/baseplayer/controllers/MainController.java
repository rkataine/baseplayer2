package org.baseplayer.controllers;

import java.util.ArrayList;

import org.baseplayer.draw.DrawFunctions;
import org.baseplayer.draw.DrawSampleData;
import org.baseplayer.draw.DrawStack;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import org.baseplayer.utils.BaseUtils;

public class MainController {
  @FXML private SplitPane drawCanvas;
  @FXML private SplitPane chromCanvas;
  @FXML private TextField positionField;
  @FXML private SplitPane chromSplit;
  @FXML private SplitPane drawSplit;
  @FXML private AnchorPane drawSideBar;
  @FXML private AnchorPane chromSideBar;
  @FXML private SplitPane mainSplit;

  @FXML private Label memLabel;
  public static SplitPane chromPane;
  public static SplitPane drawPane;
  public static boolean dividerHovered;
  public static boolean isActive = false;
  public static AnchorPane staticDraw;
  Runtime instance = Runtime.getRuntime();
  IntegerProperty memoryUsage = new SimpleIntegerProperty(0);
  public static DrawStack hoverStack;
  public static ArrayList<DrawStack> drawStacks = new ArrayList<DrawStack>(); 
   
  public void initialize() {
      chromPane = chromCanvas;
      drawPane = drawCanvas;

      memoryUsage.addListener((observable, oldValue, newValue) -> {
        int maxMem = BaseUtils.toMegabytes.apply(instance.maxMemory());
        int proportion = (int)(BaseUtils.round((newValue.doubleValue() / maxMem), 2) * 100);
        if (proportion > 80) memLabel.setStyle("-fx-text-fill: red;");
        else memLabel.setStyle("-fx-text-fill: white;");
        memLabel.setText(BaseUtils.formatNumber(newValue.intValue()) +" / " +BaseUtils.formatNumber(maxMem)  +"MB ( " +proportion +"% )" );
      });

      addStack(true);  
      
      DrawSampleData.update.addListener((observable, oldValue, newValue) -> {
       
        if (DrawFunctions.resizing) {
          for (DrawStack pane : drawStacks) {
            pane.chromCanvas.draw();
            pane.drawCanvas.draw();
          }
        }
        if (hoverStack != null) {
          hoverStack.chromCanvas.draw();
          hoverStack.drawCanvas.draw();
        }
       
        memoryUsage.set(BaseUtils.toMegabytes.apply(instance.totalMemory() - instance.freeMemory()));
      });      
      setWindowSizeListener();
      setSplitPaneDividerListener();
  }
  public static void zoomout() {
    hoverStack.drawCanvas.zoomAnimation(1, hoverStack.chromSize);
  }
  public static void addStack(boolean add) {
    if (add) {
      DrawStack drawStack = new DrawStack();    
      drawStacks.add(drawStack);
      chromPane.getItems().add(drawStack.chromStack);
      drawPane.getItems().add(drawStack.drawStack);
    } else {
      if (drawStacks.size() < 2) return;
      drawStacks.removeLast();
      drawPane.getItems().removeLast();
      chromPane.getItems().removeLast();
    }
    setDividerListeners();
    double[] drawPositions = new double[drawPane.getItems().size() - 1];
    for (int i = 0; i < drawStacks.size() - 1; i++) {
      drawPositions[i] = (i + 1) / (double)drawStacks.size();
    }
    drawPane.setDividerPositions(drawPositions);
    chromPane.setDividerPositions(drawPositions);
  
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
  static void setDividerListeners() {
    drawPane.getDividers().forEach(divider -> {
      divider.positionProperty().addListener((obs, oldVal, newVal) -> {
          int index = drawPane.getDividers().indexOf(divider);
          if (index < chromPane.getDividers().size()) {
            chromPane.getDividers().get(index).setPosition(newVal.doubleValue());
          }
      });
    });
  
    chromPane.getDividers().forEach(divider -> {
        divider.positionProperty().addListener((obs, oldVal, newVal) -> {
            int index = chromPane.getDividers().indexOf(divider);
            if (index < drawPane.getDividers().size()) {
              drawPane.getDividers().get(index).setPosition(newVal.doubleValue());
            }
        });
    });
  }
  void takeSnapshot() {
    for (DrawStack pane : drawStacks)
      pane.drawCanvas.snapshot = pane.drawCanvas.snapshot(null, null);    
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
