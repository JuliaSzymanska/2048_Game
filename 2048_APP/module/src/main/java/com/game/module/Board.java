package com.game.module;

import java.util.Arrays;
import java.util.List;

// TODO: 17.05.2020 https://rosettacode.org/wiki/2048#Java kod gry w javie

public class Board {

    private List<Field> board;
    private int BOARD_DIMENSIONS = 16;
    private int score = 0;

    public Board() {
        this.board = newBoard();
    }

    private List<Field> newBoard() {
        return Arrays.asList(new Field[BOARD_DIMENSIONS]);
    }

    public void resetBoard() {
        this.board = newBoard();
    }

    public int getScore() {
        return score;
    }

    public Field getFieldByPos(int x, int y) {
        if ((x < 0 || x > 3) || (y < 0 || y > 3)) {
            throw new IndexOutOfBoundsException("FIX ME"); // TODO: 18.05.2020 komunikat
        }
        return this.board.get(x + y * 4); // od lewej do prawej, od dołu do góry
    }

    public List<Field> getCopyBoard() {
        List<Field> cloneBoard = Arrays.asList(new Field[BOARD_DIMENSIONS]);
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            cloneBoard.set(i, this.board.get(i));
        }
        return cloneBoard;
    }

}
