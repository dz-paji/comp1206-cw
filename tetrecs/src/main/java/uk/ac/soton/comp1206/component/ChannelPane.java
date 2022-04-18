package uk.ac.soton.comp1206.component;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.event.ChannelMsgListener;

public class ChannelPane extends GridPane {
    private final Text channelName = new Text();
    private final ListView<Label> playerView = new ListView<Label>();
    private ChannelMsgListener listener;
    private ScrollPane msgArea;
    public ChannelPane() {
        this.channelName.getStyleClass().add("joinpaneTitle");
        this.playerView.getStyleClass().add("playerBox");
        this.add(channelName, 1, 0);
        this.add(playerView, 1, 1,1,2);

        // Use a scrollpane to display messages.
        msgArea = new ScrollPane();
        msgArea.getStyleClass().add("scroller");
        this.add(msgArea, 1,3,1,2);

        var msgField = new TextField();
        var msgBox = new HBox();
        var sendButton = new Button("Send");
        msgBox.getChildren().addAll(msgField, sendButton);
        HBox.setHgrow(msgField, Priority.ALWAYS);
        this.add(msgBox, 1,5);
    }

    public void updateName(String name) {
        channelName.setText(name);
    }

    public void updatePlayer(String players) {
        players = players.split(" ")[1];
        String[] player = players.split("\n");
        playerView.getItems().clear();

        // propagate players
        for (int i = 0; i < playerView.getItems().size(); i ++) {
            var playerLabel = new Label(player[i]);
            playerView.getItems().add(playerLabel);
        }
    }

    public void channelMsgHandler(String msg) {
        
    }

    public ScrollPane getMsgArea() {
        return this.msgArea;
    }

    public void setChannelMsgListener(ChannelMsgListener listener) {
        this.listener = listener;
    }
}
