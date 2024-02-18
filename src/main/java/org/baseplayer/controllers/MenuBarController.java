package org.baseplayer.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import java.util.List;

import org.baseplayer.io.FileDialog;

import java.io.File;

public class MenuBarController {
    public void openFileMenu(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String[] types = menuItem.getId().split("_");
        String filtertype = types[1];
        boolean multiSelect = filtertype.equals("SES") ? false : true; // TODO myöhemmin kun avataan bam tai vcf trackille, refactoroi toimimaan myös sille
        FileDialog fileDialog = new FileDialog(menuItem.getText(), types[1], types[0], multiSelect);
        
        List<File> files = fileDialog.chooseFiles();
        System.out.println(files);
    }    
}
