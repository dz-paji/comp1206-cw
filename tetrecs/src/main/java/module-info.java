/**
 * Modules of this project.
 */
module uk.ac.soton.comp1206 {
    requires java.scripting;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires transitive javafx.graphics;
    requires org.apache.logging.log4j;
    opens uk.ac.soton.comp1206.ui to javafx.fxml;
    exports uk.ac.soton.comp1206;
    exports uk.ac.soton.comp1206.ui;
    exports uk.ac.soton.comp1206.scene;
    exports uk.ac.soton.comp1206.event;
    exports uk.ac.soton.comp1206.component;
    exports uk.ac.soton.comp1206.game;
}