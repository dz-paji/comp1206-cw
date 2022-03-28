package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

public class PieceBoard extends GameBoard {

    /**
     * Create a new PieceBoard with its super class GameBoard.
     * 
     * @param width  width of this board
     * @param height height of this board
     */
    public PieceBoard(double width, double height) {
        super(3, 3, width, height);
    }

    /**
     * Setting the piece displays in the board.
     * 
     * @param currentPiece The GamePiece to be displayed.
     */
    public void setPiece(GamePiece currentPiece) {
        int[][] currentArray = currentPiece.getBlocks();

        for (int i = 0; i < currentArray.length; i++) {
            for (int j = 0; j < currentArray[i].length; j++) {
                if (currentArray[i][j] != 0) {
                    grid.set(i, j, currentArray[i][j]);
                }
            }
        }
    }

}
