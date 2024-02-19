package org.baseplayer.draw;

import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DrawFunctions extends Canvas {
  static double chromSize = 100000000;
  public static double start = 1;
  public static BooleanProperty update = new SimpleBooleanProperty(false);
  public static double end = chromSize + 1;
  static double viewLength = chromSize;
  static double pixelSize = 0;
  static double scale = 0;
  private GraphicsContext gc;
  private GraphicsContext reactivegc;

  static Color lineColor = new Color(0, 1, 0, 0.1);
  static Color zoomColor = new Color(0, 1, 1, 0.2);
  static Font tekstifont = new Font("Arial", 10);
  
  static Function<Double, Double> chromPosToScreenPos = chromPos -> (chromPos - start) * pixelSize;
  Function<Double, Double> heightToScreen = height -> getHeight() * height;
  static Function<Double, Integer> screenPosToChromPos = screenPos -> (int)(start + screenPos * scale);
  private double mousePressedX;
  //private Canvas reactiveCanvas;
  
  private double mouseDraggedX;
  private boolean zoomDrag;
  private double zoomFactor = 20;
  
  public DrawFunctions(Canvas reactiveCanvas) {
   
    end = chromSize;
    gc = getGraphicsContext2D();  
    gc.setFont(Font.font("Segoe UI Regular", 12));
    widthProperty().addListener((obs, oldVal, newVal) -> { setStartEnd(start, end); });
    heightProperty().addListener((obs, oldVal, newVal) -> { setStartEnd(start, end); });
    setReactiveCanvas(reactiveCanvas);
  }
  
  void setReactiveCanvas(Canvas reactiveCanvas) {
    reactivegc = reactiveCanvas.getGraphicsContext2D();
    reactiveCanvas.heightProperty().bind(heightProperty());
    reactiveCanvas.widthProperty().bind(widthProperty());
    reactiveCanvas.setOnMouseClicked(event -> { });
    reactiveCanvas.setOnMousePressed(event -> mousePressedX = event.getX() );
    reactiveCanvas.setOnMouseDragged(event -> handleDrag(event.getX()));
    reactiveCanvas.setOnScroll(event -> handleScroll(event) );
    reactiveCanvas.setOnMouseReleased(event -> {
      if (zoomDrag) {
        zoomDrag = false;
        clearReactive();
        if (mousePressedX > mouseDraggedX) return;

        double start = screenPosToChromPos.apply(mousePressedX);
        double end = screenPosToChromPos.apply(mouseDraggedX);
        setStartEnd(start, end);
      }
    });
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
  void handleDrag(double dragX) {
    reactivegc.setFill(zoomColor);
    zoomDrag = true;
    mouseDraggedX = dragX;
    clearReactive();
    if (mouseDraggedX >= mousePressedX) reactivegc.fillRect(mousePressedX, 0, mouseDraggedX-mousePressedX, getHeight());
    else zoom(dragX - mousePressedX, mousePressedX);
  }
  void clearReactive() { reactivegc.clearRect(0, 0, getWidth(), getHeight()); }
  void setStart(double start) {
    if (start < 1) start = 1;
   
    if (start + viewLength > chromSize + 1) return;
    setStartEnd(start, start+viewLength);
  }
  void setStartEnd(Double start, double end) {
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
    if (newSize < 40) newSize = 40;
    double start = screenPosToChromPos.apply(mousePos) - (pivot * newSize);
    double end = start + newSize;
    setStartEnd(start, end);
  }
}
