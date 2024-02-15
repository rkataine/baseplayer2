package org.baseplayer;

import javafx.event.ActionEvent;

public class FXMLController {
    public void openFileMenu(ActionEvent event) {
        FileDialog.chooseFiles();
    }    
}
