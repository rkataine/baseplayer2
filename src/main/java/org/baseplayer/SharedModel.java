package org.baseplayer;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class SharedModel {
    private final IntegerProperty sharedValueX = new SimpleIntegerProperty(this, "sharedValueX", 0);
    private final IntegerProperty sharedValueY = new SimpleIntegerProperty(this, "sharedValueY", 0);

    public final IntegerProperty sharedValuePropertyX() {
      return this.sharedValueX;        
    }
    public final IntegerProperty sharedValuePropertyY() {
      return this.sharedValueY;        
    }
    public final int getSharedValueX() {
      return this.sharedValuePropertyY().get();
    }
    public final int getSharedValueY() {
      return this.sharedValuePropertyX().get();
    }
    public final void setSharedValueX(int value) {
        this.sharedValuePropertyX().set(value);
    }
    public final void setSharedValueY(int value) {
      this.sharedValuePropertyY().set(value);
    }
}