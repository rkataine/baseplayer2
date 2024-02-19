package org.baseplayer.draw;

import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DrawFunctions extends Canvas {
  static double chromSize = 100000000;
  int minZoom = 40;
  public static double start = 1;
  public static BooleanProperty update = new SimpleBooleanProperty(false);
  public static double end = chromSize + 1;
  static double viewLength = chromSize;
  static double pixelSize = 0;
  static double scale = 0;
  private GraphicsContext gc;
  private GraphicsContext reactivegc;
  private boolean lineZoomer = false;
  public static Color lineColor = new Color(0, 1, 0, 0.5);
  static Color zoomColor = new Color(0, 1, 1, 0.2);
  static Font tekstifont = new Font("Arial", 10);
  
  static Function<Double, Double> chromPosToScreenPos = chromPos -> (chromPos - start) * pixelSize;
  Function<Double, Double> heightToScreen = height -> getHeight() * height;
  static Function<Double, Integer> screenPosToChromPos = screenPos -> (int)(start + screenPos * scale);
  private double mousePressedX;
  private Canvas reactiveCanvas;
  
  private double mouseDraggedX;
  private boolean zoomDrag;
  private double zoomFactor = 20;
  
  public DrawFunctions(Canvas reactiveCanvas, Pane parent) {
    this.reactiveCanvas = reactiveCanvas;
    end = chromSize;
    gc = getGraphicsContext2D();  
    gc.setFont(Font.font("Segoe UI Regular", 12));
    widthProperty().addListener((obs, oldVal, newVal) -> { setStartEnd(start, end); });
    heightProperty().addListener((obs, oldVal, newVal) -> { setStartEnd(start, end); });
    heightProperty().bind(parent.heightProperty());
    widthProperty().bind(parent.widthProperty());
    reactiveCanvas.heightProperty().bind(parent.heightProperty());
    reactiveCanvas.widthProperty().bind(parent.widthProperty());
    setReactiveCanvas(reactiveCanvas);
  }
  public Canvas getReactiveCanvas() { return reactiveCanvas; }

  void setReactiveCanvas(Canvas reactiveCanvas) {
    
    reactivegc = reactiveCanvas.getGraphicsContext2D();
   
    reactiveCanvas.setOnMouseClicked(event -> { });
    reactiveCanvas.setOnMousePressed(event -> mousePressedX = event.getX() );
    reactiveCanvas.setOnMouseDragged(event -> handleDrag(event));
    reactiveCanvas.setOnScroll(event -> handleScroll(event) );
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
    zoomDrag = true;
    mouseDraggedX = dragX;
    clearReactive();
    if (!lineZoomer && mouseDraggedX >= mousePressedX) reactivegc.fillRect(mousePressedX, 0, mouseDraggedX-mousePressedX, getHeight());
    else {
      zoomDrag = false;
      lineZoomer = true;
      zoom(dragX - mousePressedX, mousePressedX);
      mousePressedX = dragX;
    }
  }
  void handleMouseRelease(MouseEvent event) {
    if (lineZoomer) { lineZoomer = false; return; }
    if (zoomDrag) {
      zoomDrag = false;
      clearReactive();
      if (mousePressedX > mouseDraggedX) return;

      double start = screenPosToChromPos.apply(mousePressedX);
      double end = screenPosToChromPos.apply(mouseDraggedX);
      setStartEnd(start, end);
    }
  }
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
    if (end > chromSize) end = chromSize + 1;
    
    DrawSampleData.start = start;
    
    DrawSampleData.end = end;
    viewLength = end - start;
    pixelSize = getWidth() / viewLength;
    scale = viewLength / getWidth();
    update.set(!update.get());
  }
  public void zoomout() { viewLength = chromSize; setStart(1); };

  void zoom(double zoomDirection, double mousePos) {
    int direction = zoomDirection > 0 ? 1 : -1;
    double pivot = mousePos / getWidth();
    double acceleration = viewLength/getWidth() * 10;
    double newSize = viewLength - zoomFactor * acceleration * direction;
    if (newSize < minZoom) newSize = minZoom;
    double start = screenPosToChromPos.apply(mousePos) - (pivot * newSize);
    double end = start + newSize;
    setStartEnd(start, end);
  }
}
