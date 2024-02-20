package org.baseplayer.draw;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.function.Function;
import org.baseplayer.utils.BaseUtils;

public class DrawChromData extends DrawFunctions {
  private GraphicsContext gc;

  public DrawChromData(Canvas reactiveCanvas, Pane parent) {
    super(reactiveCanvas, parent);
    gc = getGraphicsContext2D();
    gc.setFont(new Font("Segoe UI Regular", 8));
  }

  Function<Double, Double> heightToScreen = height -> getHeight() * height;
 
  public void draw() {
    gc.setFill(backgroundColor);
    gc.fillRect(0, 0, getWidth(), getHeight());

    drawCyto();
    drawIndicators();
    
  }
  void drawCyto() {
    gc.setFill(zoomColor);
    gc.setStroke(Color.INDIANRED);
    double xpos = start / chromSize * getWidth() + 3;
    double width = Math.max(20, viewLength / chromSize * getWidth() - 6);
    //gc.strokeRect(xpos, 1, width, 10);
    gc.fillRoundRect(xpos, 3, width, 20, 20, 20);
    gc.strokeRoundRect(xpos, 3, width, 20, 20, 20);
    
  }
  void drawIndicators() {
    gc.setFill(Color.LIGHTGREY);
    gc.setStroke(Color.GREY);
    gc.setLineWidth(1);
   
    String text = BaseUtils.formatNumber((int)viewLength) + " bp";
    int textWidth = text.length() * 2;
    
    gc.strokeLine(0, getHeight()-22, getWidth()/2 - textWidth - 10, getHeight()-22);
    gc.strokeLine(getWidth()/2 + textWidth + 10, getHeight()-22, getWidth(), getHeight()-22);
    gc.fillText(text, getWidth()/2 - textWidth, getHeight()-20);

    if (viewLength >= 40000000) drawIndicatorLines(10000000, "0M", 4, false);
    else if (viewLength > 2000000) drawIndicatorLines(1000000, "M", 4, false);
    else if (viewLength > 60000) drawIndicatorLines(100000, null, 4, false);
    else if (viewLength > 10000) drawIndicatorLines(10000, null, 4, false);
    else if (viewLength > 1000) drawIndicatorLines(1000, null, 4, false);
    else { 
      drawIndicatorLines(100, null, 4, false);
      if(viewLength < 100) {
        drawIndicatorLines(10, null, 4, false);
        drawIndicatorLines(1, null, 4, true);
      }
    } 
    if (viewLength < 100) {
      gc.setLineDashes(2, 4);
      int middlePos = (int)screenPosToChromPos.apply(getWidth()/2);
      gc.strokeLine(getWidth()/2 - pixelSize/2, 0, getWidth()/2 - pixelSize/2, getHeight());
      gc.strokeLine(getWidth()/2 + pixelSize/2, 0, getWidth()/2 + pixelSize/2, getHeight());
      gc.fillText(""+BaseUtils.formatNumber(middlePos), getWidth()/2 - 19, getHeight()-11);
    }
    gc.setLineDashes(0);
  }
  void drawIndicatorLines(int scale, String postfix, int lineheight, boolean skip) {
    gc.setStroke(Color.GRAY);
    gc.setFill(Color.WHITESMOKE);
    
    int startvalue = (int)Math.round(start / scale) * scale;
   
    for (int i = (int)startvalue; i < chromSize; i += scale) {
      if (i < start) continue;
      if (i > end) break;
     
      double linepos = chromPosToScreenPos.apply(i * 1.0);
      String text = BaseUtils.formatNumber(i);
      if (postfix != null) 
        text = ""+(int)(i/scale) + postfix;
      int textWidth = (int)gc.getFont().getSize() * text.length();
      if (!skip) gc.fillText(text.toString(), linepos - textWidth/3 , getHeight()-lineheight);
     
      gc.strokeLine(linepos, getHeight()-lineheight, linepos, getHeight());
    }
  }
}
