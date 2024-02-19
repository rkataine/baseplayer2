package org.baseplayer.draw;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.function.Function;
public class DrawChromData extends DrawSampleData {
  private GraphicsContext gc;

  public DrawChromData(Canvas reactiveCanvas) {
    super(reactiveCanvas);
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
    gc.strokeRect(xpos, 0, width, getHeight()/10);
  }
  void drawIndicators() {
    drawIndicatorLines(10000000, "0M");
   
    if (viewLength < 10000000) {
      drawIndicatorLines(1000000, "M");
    } 
    if (viewLength < 100000) {
      drawIndicatorLines(100000, "K");
    }
    if (viewLength < 100) {
      drawIndicatorLines(1, "");
    }
    
      
  }
  void drawIndicatorLines(int scale, String postfix) {
    gc.setStroke(Color.WHITESMOKE);
    gc.setFill(Color.WHITESMOKE);
    
    double startvalue = Math.round(start / scale);

    for (int i = (int)startvalue; i < chromSize; i += scale) {
      if (i < start) continue;
      if (i > end) break;
     
      double linepos = chromPosToScreenPos.apply(i * 1.0);
     
      double height = 5;
  
     // if ((firstpos+i) % scale == 0) { 
       
      gc.fillText(""+(int)(i/scale) +postfix, linepos + 4, getHeight()-(gc.getFont().getSize()/3));
      //}
      gc.strokeLine(linepos, getHeight()-height, linepos, getHeight());
    }
  }
}
