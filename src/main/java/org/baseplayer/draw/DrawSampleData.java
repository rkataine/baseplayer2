package org.baseplayer.draw;

import org.baseplayer.SharedModel;
import org.baseplayer.variant.Variant;

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
    if (drawStack.variants != null) drawVariants();
    super.draw();
  }
  void drawVariants() {    
    for (Variant variant : drawStack.variants) {
      if (variant.line.getEndX() < drawStack.start-1) continue;
      if (variant.line.getStartX() > drawStack.end) break;

      drawLine(variant, lineColor, gc);    
    }
  }
  void drawLine(Variant variant, Color color, GraphicsContext gc) {
    if (variant.index < SharedModel.firstVisibleSample || variant.index > SharedModel.lastVisibleSample + 1) return;
   
    gc.setStroke(color);
    gc.setFill(color);
    double screenPos = chromPosToScreenPos.apply(variant.line.getStartX());
    double ypos = SharedModel.sampleHeight * variant.index - SharedModel.scrollBarPosition;
    double height = heightToScreen.apply(variant.line.getEndY());
    
    if (drawStack.pixelSize > 1) 
         gc.fillRect(screenPos, ypos - height, drawStack.pixelSize, height);
    else gc.strokeLine(screenPos, ypos, screenPos, ypos-height);
  }

}
