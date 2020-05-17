package com.game.module;

import java.util.Arrays;
import java.util.List;

public class Board {

    private List<List<Field>> board;
    private int BOARD_DIMENSIONS = 4;
    private int score = 0;

    public Board() {
        this.board = createUninitializedBoard();
    }

    private List<List<Field>> createUninitializedBoard() {
        return Arrays.asList(
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]));
    }

    public int getScore() {
        return score;
    }

    public int getField(int x, int y){
        return board.get(x).get(y).getValue();
    }

    

}
