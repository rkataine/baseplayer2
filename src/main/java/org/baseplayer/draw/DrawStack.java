package org.baseplayer.draw;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;

public class DrawStack {
  public double chromSize = 100000000;
  public double start = 1;
  public double end = chromSize + 1;
  public double viewLength = chromSize;
  public double pixelSize = 0;
  public double scale = 0;
  public StackPane chromStack = new StackPane(); 
  public StackPane drawStack = new StackPane();
  public DrawChromData chromCanvas;
  public DrawSampleData drawCanvas;

  public DrawStack() {
    chromStack.setMinSize(0, 0);
    drawStack.setMinSize(0, 0);
   
    chromCanvas = new DrawChromData(new Canvas(), chromStack, this);
    chromStack.getChildren().addAll(chromCanvas, chromCanvas.getReactiveCanvas());
    drawCanvas = new DrawSampleData(new Canvas(), drawStack, this);
    drawStack.getChildren().addAll(drawCanvas, drawCanvas.getReactiveCanvas());
  }
}
