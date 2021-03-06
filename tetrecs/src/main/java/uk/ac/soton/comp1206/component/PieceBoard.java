package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Class PieceBoard is a 3*3 GameBoard used to store a GamePiece.
 */
public class PieceBoard extends GameBoard {

    /**
     * Create a new PieceBoard with its super class GameBoard.
     *
     * @param width  width of this board
     * @param height height of this board
     */
    public PieceBoard(double width, double height) {
        super(3, 3, width, height);
        this.setStyle("-fx-padding: 10");
    }

    /**
     * Create a new PieceBoard with given cols and rows
     *
     * @param cols   number of rows
     * @param rows   number of cols
     * @param width  width of board
     * @param height height of board
     */
    public PieceBoard(int cols, int rows, double width, double height) {
        super(cols, rows, width, height);
        this.setStyle("-fx-padding: 10");
    }

    /**
     * Update the grid with given value at given location
     *
     * @param x x coordinate of block
     * @param y y coordinate of block
     * @param value new value to update
     */
    public void updateValue(int x, int y, int value) {
        grid.set(x,y,value);
    }

    /**
     * Setting the piece displays in the board.
     *
     * @param currentPiece The GamePiece to be displayed.
     */
    public void setPiece(GamePiece currentPiece) {
        // Reset this PieceBoard first
        resetBoard();

        int[][] currentArray = currentPiece.getBlocks();

        for (int i = 0; i < currentArray.length; i++) {
            for (int j = 0; j < currentArray[i].length; j++) {
                if (currentArray[i][j] != 0) {
                    grid.set(i, j, currentArray[i][j]);
                }
            }
        }
    }

    /**
     * Reset the PieceBoard
     */
    public void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid.set(i, j, 0);
            }
        }
    }

    /**
     * Show indicator of this PieceBoard
     */
    public void toggleIndicator() {
        this.blocks[1][1].paintIndicator();
    }

    /**
     * Override the inherited mouseMovesHandler. PieceBoard doesn't need this
     * feature.
     */
    @Override
    public void mouseMovesHandler(GameBlock block) {
    }
}
