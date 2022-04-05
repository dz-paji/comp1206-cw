package uk.ac.soton.comp1206.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Multimedia {
    private static MediaPlayer audioPlayer;
    private static MediaPlayer bgmPlayer;
    private static final Logger logger = LogManager.getLogger(Multimedia.class);


    /**
     * Method that plays given audio file
     * @param fileName name of the audio file
     */
    public void playAudio(String fileName) {
        Media play = new Media(Multimedia.class.getResource("/sounds/" + fileName).toExternalForm());
        audioPlayer = new MediaPlayer(play);
        audioPlayer.play();
        logger.info("Playing media {}", Multimedia.class.getResource("/sounds/" + fileName).toExternalForm());
    }

    /**
     * Method plays BGM.
     */
    public static void playBGM() {
        Media bgm = new Media(Multimedia.class.getResource("/music/game_cruel_angel.mp3").toExternalForm());
        bgmPlayer = new MediaPlayer(bgm);
        bgmPlayer.play();
        logger.info("Playing BGM");

        bgmPlayer.setOnEndOfMedia( () -> {
            playBGM();
            logger.info("BGM ends, restarting..");
        });
    }

    /**
     * Stop the current playing BGM.
     */
    public static void stopBGM() {
        bgmPlayer.stop();
    }

    /**
     * Method plays BGM.
     */
    public static void playMenu() {
        Media bgm = new Media(Multimedia.class.getResource("/music/menu_phoenix.mp3").toExternalForm());
        bgmPlayer = new MediaPlayer(bgm);
        bgmPlayer.play();
        logger.info("Playing BGM");

        bgmPlayer.setOnEndOfMedia( () -> {
            playMenu();
            logger.info("BGM ends, restarting..");
        });
    }

    /**
     * Stop the current playing BGM.
     */
    public static void stopMenu() {
        bgmPlayer.stop();
    }

}
