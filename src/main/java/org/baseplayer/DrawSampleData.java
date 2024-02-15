package org.baseplayer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class DrawSampleData {
  private GraphicsContext gc;
  private Line[] lines;
  private Scale scale;
  private Translate translate;
  int LINES = 100;
  SharedModel model = new SharedModel();
  /* private double mouseX;
  private double mouseY; */
  private static final double ZOOM_FACTOR = 1.1;
  private static final double SCROLL_FACTOR = 1.0;
  /* private static final double MIN_SCALE = 0.1;
    private static final double MAX_SCALE = 10.0; */
  Color lineColor = new Color(0, 0.2, 0, 0.2);
  Font tekstifont = new Font("Arial", 10);
  public void draw() {
    // Clear the canvas
    gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    gc.setFont(Font.font("Segoe UI Regular", 12));
    gc.setStroke(Color.BLACK);
    for (Line line : lines) {
      gc.fillText("Testiteksti", line.getStartX(), line.getStartY());
      //gc.fillText(null, MAX_SCALE, LINES);
        //gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        
    }
  }
  public DrawSampleData(AnchorPane drawCanvas) {  
    Canvas canvas = new Canvas(300, 300);
    canvas.widthProperty().bind(drawCanvas.widthProperty());
    canvas.heightProperty().bind(drawCanvas.heightProperty());
    /* drawCanvas.widthProperty().addListener((obs, oldVal, newVal) -> {
      canvas.setWidth(newVal.doubleValue());
      for (int i = 0; i < lines.length; i++) {
        lines[i] = new Line(Math.random() * canvas.getWidth(), Math.random() * canvas.getHeight(), Math.random() * canvas.getWidth(), Math.random() * canvas.getHeight());
      }
    });

    drawCanvas.heightProperty().addListener((obs, oldVal, newVal) -> {
      canvas.setHeight(newVal.doubleValue());
      for (int i = 0; i < lines.length; i++) {
        lines[i] = new Line(Math.random() * canvas.getWidth(), Math.random() * canvas.getHeight(), Math.random() * canvas.getWidth(), Math.random() * canvas.getHeight());
      }
    }); */
    drawCanvas.getChildren().add(canvas);
    gc = canvas.getGraphicsContext2D();
    
    lines = new Line[LINES];

    /* for (int i = 0; i < lines.length; i++) {
      lines[i] = new Line(Math.random() * canvas.getWidth(), Math.random() * canvas.getHeight(), Math.random() * canvas.getWidth(), Math.random() * canvas.getHeight());
    } */
    scale = new Scale();
    translate = new Translate();
    canvas.setOnMouseClicked(event -> {
      for (int i = 0; i < lines.length; i++) {
        lines[i] = new Line(Math.random() * canvas.getWidth(), Math.random() * canvas.getHeight(), Math.random() * canvas.getWidth(), Math.random() * canvas.getHeight());
      }
      draw();
    });
    /* canvas.setOnMouseMoved(event -> {
      mouseX = event.getX();
      mouseY = event.getY();
    }); */
   
    /* model.sharedValuePropertyX().addListener((observable, oldValue, newValue) -> {
      translate.setX(translate.getX() + newValue.intValue() * SCROLL_FACTOR);
    });
    model.sharedValuePropertyY().addListener((observable, oldValue, newValue) -> {
      translate.setY(translate.getY() + newValue.intValue() * SCROLL_FACTOR);
    }); */
    canvas.setOnScroll(event -> {
      event.consume();
      
      if (event.isControlDown()) {        
          // Zoom
          double scaleFactor = (event.getDeltaY() > 0) ? ZOOM_FACTOR : 1/ZOOM_FACTOR;
          scale.setPivotX(event.getX());
          scale.setPivotY(event.getY());
          scale.setX(scale.getX() * scaleFactor);
          scale.setY(scale.getY() * scaleFactor);
      } else {
          // Scroll
          translate.setX(translate.getX() +  event.getDeltaX() * SCROLL_FACTOR);
          translate.setY(translate.getY() +  event.getDeltaY() * SCROLL_FACTOR);

          //model.setSharedValueX((int) event.getDeltaX());
          //model.setSharedValueY((int) event.getDeltaY());
          //draw();
      }
     
      canvas.getTransforms().setAll(scale, translate);
    });
  }
}
