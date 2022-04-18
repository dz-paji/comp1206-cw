package uk.ac.soton.comp1206.component;

import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class ChannelPane extends GridPane {
    private final Text channelName = new Text();
    public ChannelPane() {
        
    }

    public void updateName(String name) {
        channelName.setText(name);
    }

    public void updatePlayer(String players) {
        players = players.split(" ")[1];
        String[] player = players.split("\n");
        
    }
}
