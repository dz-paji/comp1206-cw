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

    private static double menuVol = 1;

    private static double bgmVol = 1;

    private static double fxVol = 1;


    /**
     * Set volume of menu media player
     *
     * @param vol new volume
     */
    public static void setMenuVol(double vol) {
        menuVol = vol;
        if (menuPlayer != null) {
            menuPlayer.setVolume(vol);
        }
    }

    /**
     * Set volume of sound media player
     *
     * @param vol new volume
     */
    public static void setFxVol(double vol) {
        fxVol = vol;
        if (audioPlayer != null) {
            audioPlayer.setVolume(vol);
        }
    }

    /**
     * Set volume of bgm media player
     *
     * @param vol new volume
     */
    public static void setBGMVol(double vol) {
        bgmVol = vol;
        if (bgmPlayer != null) {
            bgmPlayer.setVolume(vol);
        }
    }

    /**
     * Get current volume of menu player
     *
     * @return current volume
     */
    public static double getMenuVol() {
        return menuVol;
    }

    /**
     * Get current volume of bgm player
     *
     * @return current volume
     */
    public static double getBGMVol() {
        return bgmVol;
    }

    /**
     * Get current volume of sound player
     *
     * @return current volume
     */
    public static double getFxVol() {
        return fxVol;
    }


    /**
     * Method that plays given audio file
     *
     * @param fileName name of the audio file
     */
    public void playAudio(String fileName) {
        Media play = new Media(Multimedia.class.getResource("/sounds/" + fileName).toExternalForm());
        audioPlayer = new MediaPlayer(play);
        audioPlayer.setVolume(fxVol);
        audioPlayer.play();
        logger.info("Playing media {}", Multimedia.class.getResource("/sounds/" + fileName).toExternalForm());
    }

    /**
     * Method plays BGM.
     */
    public static void playBGM() {
        Media bgm = new Media(Multimedia.class.getResource("/music/game_cruel_angel.mp3").toExternalForm());
        bgmPlayer = new MediaPlayer(bgm);
        bgmPlayer.setVolume(bgmVol);
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
        menuPlayer.setVolume(menuVol);
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
        menuPlayer.setVolume(menuVol);
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
