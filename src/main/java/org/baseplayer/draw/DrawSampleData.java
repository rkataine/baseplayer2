package org.baseplayer.draw;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import org.baseplayer.SharedModel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

public class DrawSampleData extends Canvas {
  double chromSize = 100000;
  double start = 0;
  double end = chromSize;
  double viewLength = chromSize;
  double pixelSize = 0;
  double scale = 0;
  private GraphicsContext gc;
  private Line[] lines;

  int LINES = 1000;
  SharedModel model = new SharedModel();

  Color lineColor = new Color(0, 1, 0, 0.5);
  Font tekstifont = new Font("Arial", 10);
  
  Function<Double, Double> chromPosToScreenPos = chromPos -> (chromPos - start) * this.pixelSize;
  Function<Double, Double> heightToScreen = height -> getHeight() * height;
  Function<Double, Integer> screenPosToChromPos = screenPos -> (int)(start + screenPos * scale);
  
  public void draw() {
    // Clear the canvas
    gc.clearRect(0, 0, getWidth(), getHeight());
    gc.setStroke(lineColor);
    gc.setFill(lineColor);
    for (Line line : lines) {
      if (line.getEndX() < start-1) continue;
      if (line.getStartX() > end) break;
      
      double screenPos = chromPosToScreenPos.apply(line.getStartX());
      double ypos = heightToScreen.apply(line.getEndY());
      if (pixelSize > 1) { 
        gc.fillRect(screenPos, ypos, pixelSize, getHeight() - getHeight() * line.getEndY());
       
      } else gc.strokeLine(screenPos, getHeight(), screenPos, ypos);

    }
    if (pixelSize > 5) {
      gc.setStroke(Color.WHITESMOKE);
      gc.setFill(Color.WHITESMOKE);
      double firstpos = (double)Math.round(start);
      double startvalue = chromPosToScreenPos.apply(firstpos);
      for (int i = 0; i < getWidth(); i ++) {
        double startpos = startvalue + i*pixelSize; 
        double height = 20;
    
        if ((firstpos+i) % 10 == 0) { 
          height = 40;
          gc.fillText(""+(int)(firstpos+i), startpos, getHeight()-height);
        }
        gc.strokeLine(startpos, getHeight()-height, startpos, getHeight());
      }
      
    }
  }
  public DrawSampleData() {  
    
    lines = new Line[LINES];
    for (int i = 0; i < lines.length; i++) {
      int x = (int)(Math.random() * chromSize);
    
      lines[i] = new Line(x, 0, x, Math.random());
    }

    Arrays.sort(lines, Comparator.comparing(line -> line.getStartX()));
    gc = getGraphicsContext2D();  
    gc.setFont(Font.font("Segoe UI Regular", 12));
   
    widthProperty().addListener((obs, oldVal, newVal) -> { setStartEnd(start, end); });
    heightProperty().addListener((obs, oldVal, newVal) -> { setStartEnd(start, end); });
 /* 
    drawCanvas.heightProperty().addListener((obs, oldVal, newVal) -> {
      canvas.setHeight(newVal.doubleValue());
      for (int i = 0; i < lines.length; i++) {
        lines[i] = new Line(Math.random() * canvas.getWidth(), Math.random() * canvas.getHeight(), Math.random() * canvas.getWidth(), Math.random() * canvas.getHeight());
      }
    }); */
    
  
    setOnMouseClicked(event -> { });
   
    /* model.sharedValuePropertyX().addListener((observable, oldValue, newValue) -> {
      translate.setX(translate.getX() + newValue.intValue() * SCROLL_FACTOR);
    });
    model.sharedValuePropertyY().addListener((observable, oldValue, newValue) -> {
      translate.setY(translate.getY() + newValue.intValue() * SCROLL_FACTOR);
    }); */
    
    setOnScroll(event -> {
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
    });
  }
  void setStart(double start) {
    if (start < 0) start = 0;
    if (start + viewLength > chromSize) return;
    setStartEnd(start, start+viewLength);
  }
  void setStartEnd(double start, double end) {
    if (start < 0) start = 0;
    if (end > chromSize) end = chromSize;
    this.start = start;
    this.end = end;
    this.viewLength = end - start;
    this.pixelSize = getWidth() / viewLength;
    
    this.scale = viewLength / getWidth();
    draw();
  }
  public void zoomout() { viewLength = chromSize; setStart(0); draw(); };

  void zoom(double zoomFactor, double mousePos) {
    double pivot = mousePos / getWidth();
    double acceleration = viewLength/getWidth() * 10;
    double newSize = viewLength - zoomFactor * acceleration;
    if (newSize < 40) newSize = 40;
    double start = screenPosToChromPos.apply(mousePos) - (pivot * newSize);
    double end = start + newSize;
    setStartEnd(start, end);
  }
}
