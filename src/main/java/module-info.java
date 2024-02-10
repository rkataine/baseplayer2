module baseplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    
    opens org.baseplayer to javafx.fxml;
    exports org.baseplayer;
}
