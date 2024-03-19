package org.baseplayer.controllers;
import java.util.ArrayList;
import org.baseplayer.SharedModel;
import org.baseplayer.draw.DrawFunctions;
import org.baseplayer.draw.DrawSampleData;
import org.baseplayer.draw.DrawStack;
import org.baseplayer.draw.SideBarStack;
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
import javafx.scene.layout.StackPane;

public class MainController {
  @FXML private SplitPane drawCanvas;
  @FXML private SplitPane chromCanvas;
  @FXML private TextField positionField;
  @FXML private SplitPane chromSplit;
  @FXML private SplitPane drawSplit;
  @FXML private SplitPane drawSideBar;
  @FXML private StackPane drawSideBarStackPane;
  //@FXML private Canvas drawSideBarCanvas;
  @FXML private AnchorPane chromSideBar;
  @FXML private SplitPane mainSplit;
  @FXML private AnchorPane chromPane;

  @FXML private Label memLabel;
  public Canvas drawSideBarCanvas; 
  public static SplitPane chromSplitPane;
  public static SplitPane drawPane;
 
  public static boolean dividerHovered;
  public static boolean isActive = false;
  public static AnchorPane staticDraw;
  Runtime instance = Runtime.getRuntime();
  IntegerProperty memoryUsage = new SimpleIntegerProperty(0);
  public static DrawStack hoverStack;
  public static ArrayList<DrawStack> drawStacks = new ArrayList<DrawStack>();
  SideBarStack sideBarStack;
  
  public void initialize() {
      chromSplitPane = chromCanvas;
      drawPane = drawCanvas;
      int samples = 5;
      for (int i=1; i<=samples; i++) SharedModel.sampleList.add("Sample " + i);
      SharedModel.lastVisibleSample = samples - 1;
      sideBarStack = new SideBarStack(drawSideBarStackPane);

      addStack(true);  
      addMemUpdateListener();
      addUpdateListener();
      setWindowSizeListener();
      setSplitPaneDividerListener();
  }
  public static void zoomout() {
    hoverStack.drawCanvas.zoomAnimation(1, hoverStack.chromSize);
  }
  void addMemUpdateListener() {
    memoryUsage.addListener((observable, oldValue, newValue) -> {
      if (oldValue.intValue() < newValue.intValue()) return;
      int maxMem = BaseUtils.toMegabytes.apply(instance.maxMemory());
      int proportion = (int)(BaseUtils.round((newValue.doubleValue() / maxMem), 2) * 100);
      if (proportion > 80) memLabel.setStyle("-fx-text-fill: red;");
      else memLabel.setStyle("-fx-text-fill: white;");
      memLabel.setText(BaseUtils.formatNumber(newValue.intValue()) +" / " +BaseUtils.formatNumber(maxMem)  +"MB ( " +proportion +"% )" );
    });
  }
  public static void addStack(boolean add) {
    if (add) {
      DrawStack drawStack = new DrawStack();    
      drawStacks.add(drawStack);
      chromSplitPane.getItems().add(drawStack.chromStack);
      drawPane.getItems().add(drawStack.drawStack);
    } else {
      if (drawStacks.size() < 2) return;
      drawStacks.removeLast();
      drawPane.getItems().removeLast();
      chromSplitPane.getItems().removeLast();
    }
    setDividerListeners();
    double[] drawPositions = new double[drawPane.getItems().size() - 1];
    for (int i = 0; i < drawStacks.size() - 1; i++) {
      drawPositions[i] = (i + 1) / (double)drawStacks.size();
    }
    drawPane.setDividerPositions(drawPositions);
    chromSplitPane.setDividerPositions(drawPositions);
  
  }
  void addUpdateListener() {
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

      sideBarStack.trackInfo.draw();
      memoryUsage.set(BaseUtils.toMegabytes.apply(instance.totalMemory() - instance.freeMemory()));
    });
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
          if (index < chromSplitPane.getDividers().size()) {
            chromSplitPane.getDividers().get(index).setPosition(newVal.doubleValue());
          }
      });
    });
  
    chromSplitPane.getDividers().forEach(divider -> {
        divider.positionProperty().addListener((obs, oldVal, newVal) -> {
            int index = chromSplitPane.getDividers().indexOf(divider);
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
