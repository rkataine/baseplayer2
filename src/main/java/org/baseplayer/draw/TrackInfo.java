package org.baseplayer.draw;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import org.baseplayer.SharedModel;
import org.baseplayer.controllers.MainController;

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
    sidebar.sideCanvas.setOnMouseMoved((event) -> { 
      SharedModel.hoverSample.set((int)(event.getY() / (sidebar.sideCanvas.getHeight() / tracks.size())));  
    });
    sidebar.sideCanvas.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2) {
        if (SharedModel.firstVisibleSample == SharedModel.lastVisibleSample) {
          SharedModel.firstVisibleSample = 0;
          SharedModel.lastVisibleSample = tracks.size() - 1;
          DrawFunctions.update.set(!DrawFunctions.update.get());
          return;
        }
        SharedModel.firstVisibleSample = SharedModel.hoverSample.get();
        SharedModel.lastVisibleSample = SharedModel.hoverSample.get();
        DrawFunctions.update.set(!DrawFunctions.update.get());
      }
    });
    SharedModel.hoverSample.addListener((obs, oldVal, newVal) -> { if (oldVal != newVal) draw(); });
  }

  public void draw() {
    double sampleHeight = sidebar.sideCanvas.getHeight() / SharedModel.visibleSamples.getAsInt(); // tämä sharedmodeliin
    double scrollBarPosition = SharedModel.firstVisibleSample * sampleHeight;
    gc.clearRect(0, 0, sidebar.sideCanvas.getWidth(), sidebar.sideCanvas.getHeight());
    gc.setFill(Color.WHITE);
    gc.setStroke(Color.GRAY);
    
    for (int i = SharedModel.firstVisibleSample; i < SharedModel.lastVisibleSample + 1; i++) {
      gc.fillText(tracks.get(i), 10, gc.getFont().getSize() + i * sampleHeight - scrollBarPosition);
      gc.strokeLine(0, i * sampleHeight, sidebar.sideCanvas.getWidth(), i * sampleHeight);
      for(DrawStack stack : MainController.drawStacks) {
        stack.drawCanvas.getGraphicsContext2D().setStroke(SharedModel.hoverSample.get() == i ? Color.WHITE : Color.GRAY);
        stack.drawCanvas.getGraphicsContext2D().strokeLine(0, i * sampleHeight, stack.drawCanvas.getWidth(), i * sampleHeight);
      }
    }
  }
}
