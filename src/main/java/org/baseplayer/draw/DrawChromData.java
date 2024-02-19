package org.baseplayer.draw;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.function.Function;
public class DrawChromData extends DrawFunctions {
  private GraphicsContext gc;

  public DrawChromData(Canvas reactiveCanvas, Pane parent) {
    super(reactiveCanvas, parent);
    gc = getGraphicsContext2D();
    gc.setFont(new Font("Segoe UI Regular", 8));
  }

  Function<Double, Double> heightToScreen = height -> getHeight() * height;
 
  public void draw() {
    gc.clearRect(0, 0, getWidth(), getHeight());
    drawCyto();
    drawIndicators();
  }
  void drawCyto() {
    gc.setFill(Color.RED);
    gc.setStroke(Color.RED);
    double xpos = start / chromSize * getWidth();
    double width = viewLength / chromSize * getWidth();
    gc.strokeRect(xpos, 1, width, getHeight()/10);
  }
  void drawIndicators() {
    drawIndicatorLines(10000000, "0M", 20);
   
    if (viewLength < 50000000) {
      drawIndicatorLines(1000000, "M", 10);
    } 
    if (viewLength < 1000000) {
      drawIndicatorLines(10000, "0K", 7);
    }
    if (viewLength < 10000) {
      drawIndicatorLines(1000, "K", 5);
    }
    if (viewLength < 100) {
      drawIndicatorLines(1, "", 3);
    }
    
      
  }
  void drawIndicatorLines(int scale, String postfix, int lineheight) {
    gc.setStroke(Color.GRAY);
    gc.setFill(Color.WHITESMOKE);
    
    double startvalue = Math.round(start / scale);

    for (int i = (int)startvalue; i < chromSize; i += scale) {
      if (i < start) continue;
      if (i > end) break;
     
      double linepos = chromPosToScreenPos.apply(i * 1.0);
  
     // if ((firstpos+i) % scale == 0) { 
       
      if (lineheight > 3) gc.fillText(""+(int)(i/scale) +postfix, linepos + 4, getHeight()-(gc.getFont().getSize()/3));
      //}
      gc.strokeLine(linepos, getHeight()-lineheight, linepos, getHeight());
    }
  }
}
