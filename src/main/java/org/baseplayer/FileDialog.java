package org.baseplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;
import javafx.stage.FileChooser;

public class FileDialog {
  private final int dialogType;
  private final String header;
  private final String filtertype;
  private final boolean multiSelect;
  private String filterString = "";
  private static HashMap<String, String> filefilters = new HashMap<String, String>();
  public static HashMap<String, String> defaultPaths = new HashMap<String, String>();
  public static HashMap<String, String> savePaths = new HashMap<String, String>();

  public FileDialog(String header, String filtertype, int dialogType /*FileDialog.LOAD / SAVE*/, boolean multiSelect) {
    this.dialogType = dialogType;
    this.header = header;
    this.filterString = filefilters.get(filtertype);
    this.filtertype = filtertype;
    this.multiSelect = multiSelect;
  }
  public static void chooseFiles() {
   // if (VariantHandler.frame != null) VariantHandler.frame.setState(Frame.ICONIFIED);
      final FileChooser fileChooser = new FileChooser(); // FileDialog.LOAD FileDialog.SAVE
      fileChooser.setTitle("  Open BAM File");
      fileChooser.showOpenDialog(MainApp.stage);
      /*  fc.setDirectory(defaultPaths.get(filtertype));      
      fc.setFilenameFilter(fileNameFilter);
      fc.setFile(this.filterString);
      fc.setMultipleMode(multiSelect);
      fc.setVisible(true);
      File[] files = fc.getFiles(); */
    /*   savePath(files);
      return files; */
  }
  private void savePath(File[] files) {
    if (files.length == 0) return;
    defaultPaths.put(filtertype, files[0].getParent());
    //MainPane.writeToConfig(savePaths.get(filtertype) +"=" + defaultPaths.get(filtertype));
  }
  private FilenameFilter fileNameFilter = (dir, name) -> Arrays.asList(this.filterString.replace("*", "").split(";")).stream().anyMatch(name.toLowerCase()::endsWith);
  
  static {
    savePaths.put("VCF", "DefaultDir");
    savePaths.put("BAM", "DefaultTrackDir");
    savePaths.put("BED", "TrackDir");
    savePaths.put("JSON", "DefaultProjectDir");

    /* defaultPaths.put("VCF", MainPane.path);
    defaultPaths.put("BAM", MainPane.path);
    defaultPaths.put("BED", MainPane.trackDir);
    defaultPaths.put("JSON", MainPane.projectDir); */

    filefilters.put("VCF", "*.vcf.gz");
    filefilters.put("BAM", "*.bam;*.cram");
    filefilters.put("BED", "*.bed;*.bed.gz;*.bedgraph.gz;*.gff.gz;*.gff3.gz;*.bigwig;*.bw;*.bigbed;*.bb");
    filefilters.put("JSON", "*.json");
 
  }
}
