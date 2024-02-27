package org.baseplayer.draw;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import org.baseplayer.SharedModel;

public class TrackInfo {
  SideBarStack sidebar;
  GraphicsContext gc;
  GraphicsContext reactivegc;
  
  ArrayList<String> tracks;

  public TrackInfo(SideBarStack sidebar) {
    this.sidebar = sidebar;
    this.gc = sidebar.sideCanvas.getGraphicsContext2D();
    this.reactivegc = sidebar.reactiveCanvas.getGraphicsContext2D();
    this.tracks = SharedModel.sampleList;
  }

  public void draw() {
    double sampleHeight = sidebar.sideCanvas.getHeight() / tracks.size(); // tämä sharedmodeliin
    gc.clearRect(0, 0, sidebar.sideCanvas.getWidth(), sidebar.sideCanvas.getHeight());
    gc.setFill(Color.WHITE);
    gc.setStroke(Color.LIGHTGRAY);
    for (int i = 0; i < tracks.size(); i++) {
      gc.fillText(tracks.get(i), 10, gc.getFont().getSize() + i * sampleHeight);
      gc.strokeLine(0, i * sampleHeight, sidebar.sideCanvas.getWidth(), i * sampleHeight);
    }
  }
}
