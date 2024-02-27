package org.baseplayer.draw;

import org.baseplayer.SharedModel;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class DrawSampleData extends DrawFunctions {
  
  public Image snapshot;
  private GraphicsContext gc;
  //private Line selectedLine;
  //private TrackInfo trackInfo;

  public DrawSampleData(Canvas reactiveCanvas, StackPane parent, DrawStack drawStack) {
    super(reactiveCanvas, parent, drawStack);
    //trackInfo = new TrackInfo(sidebargc, gc, SharedModel.sampleList);
    widthProperty().addListener((obs, oldVal, newVal) -> { resizing = true; setStartEnd(drawStack.start, drawStack.end); resizing = false; });
    gc = getGraphicsContext2D();
    gc.setLineWidth(1);
     
    // set onmousemoved event
    //reactiveCanvas.setOnMouseExited(event -> { selectedLine = null; clearReactive(); });
    /* reactiveCanvas.setOnMouseMoved(event -> {
     
      double x = event.getX();
      double y = event.getY();

      selectedLine = null;
      
      for (DrawStack.Variant line : drawStack.lines) {
        if (line.line.getEndX() < drawStack.start-1) continue;
        if (line.line.getStartX() > drawStack.end) break;
       
        double screenPos = chromPosToScreenPos.apply(line.line.getStartX());
        double screenPosY = getHeight() * line.line.getEndY();
        
        if (x >= screenPos -4 && x <= screenPos + drawStack.pixelSize + 4) {
          if (y >= screenPosY){
            selectedLine = line.line;
            break;
          }
        }        
      }
      
      reactivegc.clearRect(0, 0, getWidth(), getHeight());
      if (selectedLine != null) {        
        drawLine(selectedLine, Color.INDIANRED, reactivegc);
      }

    }); */

    Platform.runLater(() -> { draw(); });
  }
  void drawSnapShot() { if (snapshot != null) gc.drawImage(snapshot, 0, 0, getWidth(), getHeight()); }
  @Override
  public void draw() {
    //if (resizing) { drawSnapShot(); return; }
    gc.setFill(backgroundColor);
    gc.fillRect(0, 0, getWidth()+1, getHeight()+1);
    drawVariants();
    super.draw();
  }
  void drawVariants() {    
    for (DrawStack.Variant line : drawStack.lines) {
      if (line.line.getEndX() < drawStack.start-1) continue;
      if (line.line.getStartX() > drawStack.end) break;
      drawLine(line, lineColor, gc);    
    }
  }
  void drawLine(DrawStack.Variant line, Color color, GraphicsContext gc) {
    gc.setStroke(color);
    gc.setFill(color);
    double sampleHeight = getHeight() / SharedModel.sampleList.size();
    double screenPos = chromPosToScreenPos.apply(line.line.getStartX());
    double ypos = sampleHeight * line.index;
    double height = heightToScreen.apply(line.line.getEndY());
    
    if (drawStack.pixelSize > 1) 
         gc.fillRect(screenPos, ypos, drawStack.pixelSize, ypos - height);
    else gc.strokeLine(screenPos, ypos, screenPos, ypos-height);
  }

}
