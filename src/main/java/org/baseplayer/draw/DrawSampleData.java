package org.baseplayer.draw;

import java.util.Arrays;
import java.util.Comparator;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class DrawSampleData extends DrawFunctions {
  private Line[] lines;
  int LINES = 10000;
  private GraphicsContext gc;

  public DrawSampleData(Canvas reactiveCanvas, Pane parent) {
    super(reactiveCanvas, parent);
    gc = getGraphicsContext2D();
    gc.setLineWidth(1);
    lines = new Line[LINES];

    for (int i = 0; i < lines.length; i++) {
      int x = (int)(Math.random() * chromSize);
      lines[i] = new Line(x, 0, x, Math.random());
    }    
    Arrays.sort(lines, Comparator.comparing(line -> line.getStartX()));
  }

  public void draw() {
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
  }
}
