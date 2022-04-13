package uk.ac.soton.comp1206.game;

import java.util.Random;

import javafx.beans.property.IntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleIntegerProperty;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.NextPieceListener;

/**
 * The Game class handles the main logic, state and properties of the TetrECS
 * game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * The game piece that the player going to place.
     */
    protected GamePiece currentPiece;

    /**
     * The game piece that followed by the currentPiece.
     */
    protected GamePiece followingPiece;

    /**
     * Tracks previous interval of score. 0 means score is between 0-999, 1 means
     * score is between 1000-1999.
     */
    private int scoreTracker = 0;

    /**
     * Score of current game
     */
    private SimpleIntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Level of current game
     */
    private SimpleIntegerProperty level = new SimpleIntegerProperty(0);

    /**
     * Lives remaining of this game
     */
    private SimpleIntegerProperty lives = new SimpleIntegerProperty(3);

    /**
     * Score multiplier of this game
     */
    private SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);

    /**
     * Listener updates the current GamePiece.
     */
    private NextPieceListener nextPieceListener;

    /**
     * Create a new game with the specified rows and columns. Creates a
     * corresponding grid model.
     * 
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        // Create a new grid model to represent the game state
        this.grid = new Grid(cols, rows);

        // Spawn a new piece
        this.currentPiece = spawnPiece();
        this.followingPiece();
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");

        this.score.addListener((event) -> {
            logger.info("Score got changed!");
            int changedScore = this.score.get() % 1000;
            if (changedScore - this.scoreTracker > 0) {
                this.scoreTracker += changedScore;
                this.level.set(this.level.get() + changedScore);
            }
        });
    }

    /**
     * Handle what should happen when a particular block is clicked
     * 
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        // Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        // Check if the new piece can be placed
        if (this.grid.canPlayPiece(this.currentPiece, x, y)) {
            this.grid.playPiece(this.currentPiece, x, y);
            logger.info("{} will be placed at {},{}", this.currentPiece.toString(), x, y);

            // Check for lines to clear
            afterPiece();
        } else {
            logger.warn("{} can't be placed at {},{}", this.currentPiece.toString(), x, y);
        }

        // Get the new value for this block
        // int previousValue = grid.get(x,y);
        // int newValue = previousValue + 1;
        // if (newValue > GamePiece.PIECES) {
        // newValue = 0;
        // }

        // Update the grid with the new value
        // grid.set(x,y,newValue);
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * 
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * 
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * 
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Create a new piece
     * 
     * @return the new piece
     */
    public GamePiece spawnPiece() {
        Random rndPiece = new Random();

        GamePiece newPiece = GamePiece.createPiece(rndPiece.nextInt(14));
        logger.info("New piece spawned {}", newPiece.toString());
        return newPiece;
    }

    /**
     * Clear any fully occupied lines.
     */
    public void afterPiece() {
        Boolean[] xFull = new Boolean[this.rows];
        Boolean[] yFull = new Boolean[this.cols];
        int xCount = 0;
        int yCount = 0;
        int numBlockCount = 0;

        // Check for vertical lines
        // i for x, j for y.
        for (int i = 0; i < this.cols; i++) {
            xFull[i] = true;

            // Loop through every col at given row
            for (int j = 0; j < this.rows; j++) {
                // logger.info("Checking if x:{} is full", i);
                if (this.grid.get(i, j) == 0) {
                    // logger.info("x:{} is not full", i);
                    xFull[i] = false;
                    break;
                }
            }
        }

        // Check for hotizental lines
        for (int j = 0; j < this.cols; j++) {
            yFull[j] = true;

            // Loop through every x at given y
            for (int i = 0; i < this.rows; i++) {
                // logger.info("Checking if y:{} is full", j);
                if (this.grid.get(j, i) == 0) {
                    // logger.info("y:{} is not full", j);
                    yFull[j] = false;
                    break;
                }
            }
        }

        // Clear lines and count number of cleared lines.
        for (int j = 0; j < this.rows; j++) {
            if (xFull[j] == true) {
                logger.info("Vertical line no.{} will be cleared", j);
                for (int i = 0; i < this.cols; i++) {
                    this.grid.set(j, i, 0);
                }
                yCount++;
            }
        }

        for (int i = 0; i < this.cols; i++) {
            if (yFull[i] == true) {
                logger.info("Horizontal line no.{} will be cleared", i);
                for (int j = 0; j < this.rows; j++) {
                    this.grid.set(j, i, 0);
                }
                xCount++;
            }
        }

        // Check if there's intersecting lines cleared
        if (yCount != 0 && xCount != 0) {
            numBlockCount = yCount * this.cols + xCount * this.rows - yCount * xCount;
        } else {
            numBlockCount = yCount * this.cols + xCount * this.rows;
        }

        this.currentPiece = this.followingPiece;
        this.followingPiece();

        // Update PieceBoard
        updatePieceBoard();
        score(yCount + xCount, numBlockCount);
        checkMultiplier(yCount + xCount);
    }

    /**
     * Set the score of the game
     * 
     * @param score the new score
     */
    public void setScore(int score) {
        logger.info("New score set to {}", score);
        this.score.set(score);
    }

    /**
     * Get current scores of the game
     * 
     * @return score of the game
     */
    public IntegerProperty getScore() {
        return this.score;
    }

    /**
     * Set the challenge level of the game
     * 
     * @param level The new challenge level
     */
    public void setLevel(int level) {
        logger.info("New level set to {}", level);
        this.level.set(level);
    }

    /**
     * Get the challenge level of the game
     * 
     * @return challenge level
     */
    public IntegerProperty getLevel() {
        return this.level;
    }

    /**
     * Set lives reaming of the game
     * 
     * @param lives new lives
     */
    public void setLives(int lives) {
        logger.info("New lives set to {}", lives);
        this.lives.set(lives);
    }

    /**
     * Get current lives
     * 
     * @return lives
     */
    public IntegerProperty getLives() {
        return this.lives;
    }

    /**
     * Set the multiplier of the game
     * 
     * @param multiplier the new multiplier to be set.
     */
    public void setMultiplier(int multiplier) {
        logger.info("New multiplier set to {}", multiplier);
        this.multiplier.set(multiplier);
    }

    /**
     * Get current multiplier
     * 
     * @return Multiplier
     */
    public IntegerProperty getMultiplier() {
        return this.multiplier;
    }

    /**
     * Add score awared for clearing any lines.
     * 
     * @param numLines  number of lines cleared by placing a block
     * @param numBlocks number of blocks cleared
     */
    public void score(int numLines, int numBlocks) {
        this.score.set(this.score.get() + (numLines * numBlocks * 10 * this.multiplier.get()));
    }

    /**
     * Update the multiplier
     * 
     * @param numLines number of lines cleared after block click
     */
    public void checkMultiplier(int numLines) {
        if (numLines == 0) {
            this.multiplier.set(1);
        } else {
            this.multiplier.set(this.multiplier.get() + 1);
        }

    }

    /**
     * Get the current GamePiece
     * 
     * @return Current GamePiece
     */
    public GamePiece getPiece() {
        return this.currentPiece;
    }

    /**
     * Get the following GamePiece.
     * 
     * @return GamePiece after currentPiece.
     */
    public GamePiece getFollowingPiece() {
        return this.followingPiece;
    }

    /**
     * Update the GamePiece whenever a new one generates.
     */
    public void setNextPieceListener(NextPieceListener listener) {
        this.nextPieceListener = listener;
    }

    /**
     * Call the handler to update the PieceBoard.
     */
    private void updatePieceBoard() {
        this.nextPieceListener.nextPiece(this.currentPiece, this.followingPiece);
    }

    /**
     * Rotate the current GamePiece
     */
    public void rotateCurrentPiece() {
        this.currentPiece.rotate();
        logger.info("Rotating currentPiece.");
    }

    /**
     * Initialise the piece after currentPiece.
     */
    public void followingPiece() {
        this.followingPiece = spawnPiece();
    }

    /**
     * Swap currentPiece with followingPiece
     */
    public void swapPiece() {
        logger.info("Swapping GamePiece");
        
        GamePiece bufferPiece;
        bufferPiece = this.currentPiece;
        this.currentPiece = this.followingPiece;
        this.followingPiece = bufferPiece;

        // Update PieceBoard.
        updatePieceBoard();
    }

}
