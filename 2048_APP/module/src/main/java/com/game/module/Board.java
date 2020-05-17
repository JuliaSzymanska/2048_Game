package com.game.module;

import java.util.Arrays;
import java.util.List;

// TODO: 17.05.2020 https://rosettacode.org/wiki/2048#Java kod gry w javie

public class Board {

    private List<List<Field>> board;
    private int BOARD_DIMENSIONS = 4;
    private int score = 0;

    public Board() {
        this.board = newBoard();
    }

    private List<List<Field>> newBoard() {
        return Arrays.asList(
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]));
    }

    public int getScore() {
        return score;
    }

    public void set(int x, int y, int value) {
        board.get(x).set(y, new Field(value));
    }

    public int get(int x, int y) {
        return board.get(x).get(y).getValue();
    }

    public List<List<Field>> getBoard() {
        List<List<Field>> cloneBoard = Arrays.asList(
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]));
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            for (int j = 0; j < BOARD_DIMENSIONS; j++) {
                cloneBoard.get(i).set(j, this.board.get(i).get(j));
            }
        }
        return cloneBoard;
    }

}
