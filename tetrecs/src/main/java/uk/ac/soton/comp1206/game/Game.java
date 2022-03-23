package uk.ac.soton.comp1206.game;

import java.util.Random;

import javafx.beans.property.IntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleIntegerProperty;
import uk.ac.soton.comp1206.component.GameBlock;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
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

    protected GamePiece currentPiece;

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
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);

        // Spawn a new piece
        this.currentPiece = spawnPiece();
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
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
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


        //Get the new value for this block
        // int previousValue = grid.get(x,y);
        // int newValue = previousValue + 1;
        // if (newValue  > GamePiece.PIECES) {
        //     newValue = 0;
        // }

        //Update the grid with the new value
        // grid.set(x,y,newValue);
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Create a new piece
     * @return the new piece
     */
    public GamePiece spawnPiece() {
        Random rndPiece = new Random();
        
        GamePiece newPiece = GamePiece.createPiece(rndPiece.nextInt(14));
        logger.info("New piece spawned {}", newPiece.toString() );
        return newPiece;
    }

    /**
     * Subsitute currentPiece with a new spawned piece.
     */
    public void nextPiece() {
        this.currentPiece = spawnPiece();
    }

    /**
     * Clear any fully occupied lines.
     */
    public void afterPiece() {

        // Check for vertical lines
        // i for x, j for y.
        for (int i = 0; i < this.rows; i++) {
            Boolean isVOccupied = true;

            // Loop through every y at given x
            for (int j = 0; j < this.cols; j++) {
                if (this.grid.get(i, j) == 0) {
                    isVOccupied = false;
                }
            }

            if (isVOccupied == true) {
                for (int j = 0; j < this.cols; j++) {
                    this.grid.set(i, j, 0);
                }
                logger.info("Clearing vertical line no.{}", i);
            }
        }

        // Check for hotizental lines
        for (int j = 0; j < this.cols; j ++) {
            Boolean isHOccupied = true;

            // Loop through every x at given y
            for (int i = 0; i < this.rows; i++) {
                if (this.grid.get(i, j) == 0) {
                    isHOccupied = false;
                }
            }

            if (isHOccupied == true) {
                for (int i = 0; i < this.rows; i++) {
                    this.grid.set(i, j, 0);
                }
                logger.info("Clearing horizontal line no.{}", j);
            }
        }

        this.currentPiece=spawnPiece();
    }

    public void setScore(int score) {
        logger.info("New score set to {}", score);
        this.score.set(score);
    }

    public IntegerProperty getScore() {
        return this.score;
    }

    public void setLevel(int level) {
        logger.info("New level set to {}", level);
        this.level.set(level);
    }

    public IntegerProperty getLevel() {
        return this.level;
    }

    public void setLives(int lives) {
        logger.info("New lives set to {}", lives);
        this.lives.set(lives);
    }

    public IntegerProperty getLives() {
        return this.lives;
    }

    public void setMultiplier(int multiplier) {
        logger.info("New multiplier set to {}", multiplier);
        this.multiplier.set(multiplier);
    }

    public IntegerProperty getMultiplier() {
        return this.multiplier;
    }

}
