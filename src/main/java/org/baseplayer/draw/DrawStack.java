package org.baseplayer.draw;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import java.util.Arrays;
import org.baseplayer.SharedModel;
import java.util.Comparator;

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

  public Variant[] lines;
  int LINES = 10000;

  public DrawStack() {
    
    lines = new Variant[LINES];

    for (int i = 0; i < lines.length; i++) {
      int x = (int)(Math.random() * chromSize);
      lines[i] = new Variant((int)(Math.random() * SharedModel.sampleList.size() + 1), new Line(x, 0, x, Math.random()));
    }    
    
    Arrays.sort(lines, Comparator.comparing(line -> line.line.getStartX()));

    chromStack.setMinSize(0, 0);
    drawStack.setMinSize(0, 0);

    chromCanvas = new DrawChromData(new Canvas(), chromStack, this);
    chromStack.getChildren().addAll(chromCanvas, chromCanvas.getReactiveCanvas());
    drawCanvas = new DrawSampleData(new Canvas(), drawStack, this);
    drawStack.getChildren().addAll(drawCanvas, drawCanvas.getReactiveCanvas());
  }

  class Variant {
    int index;
    Line line;
    public Variant(int index, Line line) {
      this.index = index;
      this.line = line;
    }
  }
}
