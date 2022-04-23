package uk.ac.soton.comp1206.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

/**
 * Class multimedia handles the play of sound files and BGMs.
 */
public class Multimedia {
    private static MediaPlayer audioPlayer;
    private static MediaPlayer bgmPlayer;

    private static MediaPlayer menuPlayer;
    private static final Logger logger = LogManager.getLogger(Multimedia.class);

    /**
     * Set volume of menu media player
     *
     * @param vol new volume
     */
    public static void setMenuVol(double vol) {
        menuPlayer.setVolume(vol);
    }

    public static void setBGMVol(double vol) {
        bgmPlayer.setVolume(vol);
    }

    /**
     * Method that plays given audio file
     * 
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

        bgmPlayer.setOnEndOfMedia(() -> {
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
    public static void playMenuIntro() {
        Media bgm = new Media(Multimedia.class.getResource("/music/tendo_intro.mp3").toExternalForm());
        menuPlayer = new MediaPlayer(bgm);
        // bgmPlayer.setVolume(0.3);
        menuPlayer.play();
        logger.info("Playing BGM intro");

        menuPlayer.setOnEndOfMedia(() -> {
            playMenuMain();
        });
    }

    /**
     * Play main part of menu and loop it.
     */
    public static void playMenuMain() {
        Media bgm = new Media(Multimedia.class.getResource("/music/tendo_main.mp3").toExternalForm());
        menuPlayer = new MediaPlayer(bgm);
        // bgmPlayer.setVolume(0.3);
        menuPlayer.play();
        logger.info("Playing BGM main");

        menuPlayer.setOnEndOfMedia(() -> {
            playMenuMain();
            logger.info("BGM ends, restarting..");
        });

    }

    /**
     * Stop the current playing BGM.
     */
    public static void stopMenu() {
        menuPlayer.stop();
    }

    /**
     * Check if menu bgm is playing
     * 
     * @return status of menu bgm.
     */
    public static boolean isMenuPlaying() {
        if (menuPlayer == null) {
            return false;
        }

        Status nStatus = menuPlayer.getStatus();
        if (nStatus == MediaPlayer.Status.PLAYING) {
            return true;
        } else {
            return false;
        }
    }

}
