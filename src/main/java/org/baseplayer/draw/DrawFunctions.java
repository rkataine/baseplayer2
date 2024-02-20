package org.baseplayer.draw;

import java.util.function.Function;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

public class DrawFunctions extends Canvas {
  static double chromSize = 100000000;
  
  int minZoom = 40;
  public static BooleanProperty update = new SimpleBooleanProperty(false);
  public static double start = 1;
  
  public static double end = chromSize + 1;
  static double viewLength = chromSize;
  static double pixelSize = 0;
  static double scale = 0;
  private GraphicsContext gc;
  private GraphicsContext reactivegc;
  private boolean lineZoomer = false;
  public static Color lineColor = new Color(0.5, 0.8, 0.8, 0.5);
  public static Color backgroundColor = new Color(0.2, 0.2, 0.2, 1);
  static LinearGradient zoomColor = new LinearGradient(
    0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
    new Stop(0, javafx.scene.paint.Color.rgb(105, 255, 0, 0.2)),  
    new Stop(1, javafx.scene.paint.Color.rgb(0, 200, 255, 0.2))
  );
  static Font tekstifont = new Font("Arial", 10);
  
  static Function<Double, Double> chromPosToScreenPos = chromPos -> (chromPos - start) * pixelSize;
  Function<Double, Double> heightToScreen = height -> getHeight() * height;
  static Function<Double, Integer> screenPosToChromPos = screenPos -> (int)(start + screenPos * scale);
  private double mousePressedX;
  private Canvas reactiveCanvas;
  private double mouseDraggedX;
  private boolean zoomDrag;
  private double zoomFactor = 20;
  public int zoomY = - 1;
  public static boolean resizing = false;
  public static Image snapshot = null;

  public static boolean animationRunning = false;

  public DrawFunctions(Canvas reactiveCanvas, Pane parent) {
    this.reactiveCanvas = reactiveCanvas;
    end = chromSize;
    gc = getGraphicsContext2D();  
    gc.setFont(Font.font("Segoe UI Regular", 12));
    heightProperty().addListener((obs, oldVal, newVal) -> { resizing = true; update.set(!update.get()); resizing = false; });
    heightProperty().bind(parent.heightProperty());
    widthProperty().bind(parent.widthProperty());
    reactiveCanvas.heightProperty().bind(parent.heightProperty());
    reactiveCanvas.widthProperty().bind(parent.widthProperty());
    setReactiveCanvas(reactiveCanvas);
    // Platform.runLater(() -> { setStartEnd(start, end); });
  }
  public Canvas getReactiveCanvas() { return reactiveCanvas; }
  void drawSnapShot() { if (snapshot != null) gc.drawImage(snapshot, 0, 0, getWidth(), getHeight()); }
  void setReactiveCanvas(Canvas reactiveCanvas) {
    
    reactivegc = reactiveCanvas.getGraphicsContext2D();
   
    reactiveCanvas.setOnMouseClicked(event -> { });
    reactiveCanvas.setOnMousePressed(event -> mousePressedX = event.getX() );
    reactiveCanvas.setOnMouseDragged(event -> handleDrag(event));
    reactiveCanvas.setOnScroll(event -> handleScroll(event) );
    //reactiveCanvas.setOnMouseMoved(event -> { });
    reactiveCanvas.setOnMouseReleased(event -> { handleMouseRelease(event); });
  }
  void handleScroll(ScrollEvent event) {
    event.consume();
      
    if (event.isControlDown()) {        
        // Zoom
        double zoomFactor = event.getDeltaY();
        double mousePos = event.getX();
        zoom(zoomFactor, mousePos);
    } else {
        // Scroll
        double acceleration = viewLength/getWidth() * 2;
        double xPos = event.getDeltaX() * acceleration;
        setStart(start - xPos);
    }
  }
  void handleDrag(MouseEvent event) {
    double dragX = event.getX();
    
    if (event.getButton() == MouseButton.SECONDARY) {      
      setStart(start - (dragX - mousePressedX) / pixelSize);
      mousePressedX = dragX;
      return;
    }
    
    reactivegc.setFill(zoomColor);
    reactivegc.setStroke(Color.INDIANRED);
    zoomDrag = true;
    mouseDraggedX = dragX;
    //clearReactive();
    if (!lineZoomer && mouseDraggedX >= mousePressedX) {
      clearReactive();
      reactivegc.fillRect(mousePressedX, zoomY, mouseDraggedX-mousePressedX, getHeight());
      reactivegc.strokeRect(mousePressedX, zoomY, mouseDraggedX-mousePressedX, getHeight() + 2);
    } else {
      zoomDrag = false;
      lineZoomer = true;
      
      zoom(dragX - mousePressedX, mousePressedX);
      mousePressedX = dragX;
    }
  }
  void handleMouseRelease(MouseEvent event) {
    clearReactive();
   
    if (lineZoomer) { lineZoomer = false; return; }
    if (zoomDrag) {
      zoomDrag = false;
     
      if (mousePressedX > mouseDraggedX) return;

      double start = screenPosToChromPos.apply(mousePressedX);
      double end = screenPosToChromPos.apply(mouseDraggedX);
      zoomAnimation(start, end);
    }
  }
  void clearReactive(double x, double y, double width) { reactivegc.clearRect(x, y, width, getHeight()); }
  void clearReactive() { reactivegc.clearRect(0, 0, getWidth(), getHeight()); }
  void setStart(double start) {
    
    if (start < 1) start = 1;
   
    if (start + viewLength > chromSize + 1) return;
    setStartEnd(start, start+viewLength);
  }
  void setStartEnd(Double start, double end) {
    if (end - start < minZoom) {
        start = (start+(end-start)/2) - minZoom/2;
        end = start + minZoom;
    };
    if (start < 1) start = 1.0;
    if (end >= chromSize - 1) end = chromSize + 1;
    DrawSampleData.start = start;
    DrawSampleData.end = end;
    viewLength = end - start;
    pixelSize = getWidth() / viewLength;
    scale = viewLength / getWidth();
    update.set(!update.get());
  }
  public void zoomout() { zoomAnimation(1, chromSize); };

  void zoom(double zoomDirection, double mousePos) {
   
    int direction = zoomDirection > 0 ? 1 : -1;
    double pivot = mousePos / getWidth();
    double acceleration = viewLength/getWidth() * 15;
    double newSize = viewLength - zoomFactor * acceleration * direction;
    if (newSize < minZoom) newSize = minZoom;
    double start = Math.max(1, screenPosToChromPos.apply(mousePos) - (pivot * newSize));
    double end = Math.min(chromSize + 1, start + newSize);
    if (DrawFunctions.start == start && DrawFunctions.end == end) return;
    setStartEnd(start, end);
  }

  void zoomAnimation(double start, double end) {
    new Thread(() -> {
      animationRunning = true;
      final DoubleProperty currentStart = new SimpleDoubleProperty(DrawFunctions.start);
      final DoubleProperty currentEnd = new SimpleDoubleProperty(DrawFunctions.end);
      int startStep = (int)(start - DrawFunctions.start)/10;
      int endStep = (int)(DrawFunctions.end - end)/10;
      boolean ended = false;
      for(int i = 0; i < 10; i++) {
        Platform.runLater(() -> { setStartEnd(currentStart.get(), currentEnd.get()); });
        currentStart.set(currentStart.get() + startStep);
        currentEnd.set(currentEnd.get() - endStep);
        if ((startStep > 0 && currentStart.get() >= start) || (startStep < 0 && currentStart.get() <= start)) {
          animationRunning = false;
          ended = true;
          Platform.runLater(() -> setStartEnd(start, end) );
          break;
        }
        
        try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); break; }
      }
      if (!ended) Platform.runLater(() -> setStartEnd(start, end) );
    }).start();
  }
}
