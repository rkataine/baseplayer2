package org.baseplayer.draw;

import java.util.Arrays;
import java.util.Comparator;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class DrawSampleData extends DrawFunctions {
  private Line[] lines;
  int LINES = 1000;
  public Image snapshot;
  private GraphicsContext gc;
  private Line selectedLine;
  
  public DrawSampleData(Canvas reactiveCanvas, StackPane parent, DrawStack drawStack) {
    super(reactiveCanvas, parent, drawStack);
    widthProperty().addListener((obs, oldVal, newVal) -> { resizing = true; setStartEnd(drawStack.start, drawStack.end); resizing = false; });
    gc = getGraphicsContext2D();
    gc.setLineWidth(1);
    lines = new Line[LINES];
    
    // set onmousemoved event
    reactiveCanvas.setOnMouseExited(event -> { selectedLine = null; clearReactive(); });
    reactiveCanvas.setOnMouseMoved(event -> {
     
      double x = event.getX();
      double y = event.getY();

      selectedLine = null;
      
      for (Line line : lines) {
        if (line.getEndX() < drawStack.start-1) continue;
        if (line.getStartX() > drawStack.end) break;
       
        double screenPos = chromPosToScreenPos.apply(line.getStartX());
        double screenPosY = getHeight() * line.getEndY();
        
        if (x >= screenPos -4 && x <= screenPos + drawStack.pixelSize + 4) {
          if (y >= screenPosY){
            selectedLine = line;
            break;
          }
        }        
      }
      
      reactivegc.clearRect(0, 0, getWidth(), getHeight());
      if (selectedLine != null) {        
        drawLine(selectedLine, Color.INDIANRED, reactivegc);
      }

    });

    for (int i = 0; i < lines.length; i++) {
      int x = (int)(Math.random() * drawStack.chromSize);
      lines[i] = new Line(x, 0, x, Math.random());
    }    
    Arrays.sort(lines, Comparator.comparing(line -> line.getStartX()));
    Platform.runLater(() -> { draw(); });
  }
  void drawSnapShot() { if (snapshot != null) gc.drawImage(snapshot, 0, 0, getWidth(), getHeight()); }
  public void draw() {
    //if (resizing) { drawSnapShot(); return; }
    gc.setFill(backgroundColor);
    gc.fillRect(0, 0, getWidth()+1, getHeight()+1);
    drawVariants();
    super.draw();
  }
  void drawVariants() {    
    for (Line line : lines) {
      if (line.getEndX() < drawStack.start-1) continue;
      if (line.getStartX() > drawStack.end) break;
      drawLine(line, lineColor, gc);    
    }
  }
  void drawLine(Line line, Color color, GraphicsContext gc) {
    gc.setStroke(color);
    gc.setFill(color);
    double screenPos = chromPosToScreenPos.apply(line.getStartX());
    double ypos = heightToScreen.apply(line.getEndY());
    if (drawStack.pixelSize > 1) { 
      gc.fillRect(screenPos, ypos, drawStack.pixelSize, getHeight() - getHeight() * line.getEndY());
     
    } else gc.strokeLine(screenPos, getHeight(), screenPos, ypos);
  }

}
