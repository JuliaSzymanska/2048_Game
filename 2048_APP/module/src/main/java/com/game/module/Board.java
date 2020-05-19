package com.game.module;

import androidx.annotation.MainThread;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// TODO: 17.05.2020 https://rosettacode.org/wiki/2048#Java kod gry w javie

public class Board {

    private List<Field> board;
    private final int BOARD_DIMENSIONS = 4;
    private final int BOARD_SIZE = BOARD_DIMENSIONS * BOARD_DIMENSIONS;
    private int score = 0;

    public Board() {
        this.board = newBoard();
    }

    // FIXME: 18.05.2020 taa no to jest srednie ale testuje sobie ._.
    //  poza testem nie ma powodu żeby to istniało
    Board(List<Integer> integerList) {
        this.board = newBoard();
        int counter = 0;
        for (int i : integerList) {
            board.get(counter).setValue(i);
            counter++;
        }
    }

    private List<Field> newBoard() {
        List<Field> fieldList = Arrays.asList(new Field[BOARD_SIZE]);
        for (int i = 0; i < BOARD_SIZE; i++) {
            fieldList.set(i, new Field(0));
        }
        return fieldList;
    }

    public void resetBoard() {
        this.board = newBoard();
    }

    public int getScore() {
        return score;
    }

    private List<Field> getAllEmptyFields() {
        List<Field> listOfEmptyFields = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (this.board.get(i).getValue() == 0) {
                // przepisuje referencje specjalnie
                listOfEmptyFields.add(this.board.get(i));
            }
        }
        return listOfEmptyFields;
    }

    /**
     * Fills a random empty field in board with either 2 or 4 (9:1 probablility ratio).
     * Called after calling move methods.
     */
    private void addNewNonEmptyFieldAfterMove() {
        List<Field> allEmptyFields = getAllEmptyFields();
        Collections.shuffle(allEmptyFields);
        allEmptyFields.get(0).setValue(Math.random() >= .9 ? 4 : 2);
    }

    // TODO: 18.05.2020 takie obejscie myslalem w sumie żeby zrobić
    //  żeby brać sobie te fieldy tak jaby to był 2d list
    //  a potem i tak z tego nie korzystam
    private Field getFieldByPos(int x, int y) {
        if ((x < 0 || x > 3) || (y < 0 || y > 3)) {
            throw new IndexOutOfBoundsException("FIX ME"); // TODO: 18.05.2020 komunikat
        }
        return this.board.get(x + y * 4); // od lewej do prawej, od dołu do góry
    }

    private List<Field> getColumn(int col) {
        return Arrays.asList(
                this.getFieldByPos(0, col),
                this.getFieldByPos(1, col),
                this.getFieldByPos(2, col),
                this.getFieldByPos(3, col));
    }

    private List<Field> getRow(int row) {
        return Arrays.asList(
                this.getFieldByPos(row, 0),
                this.getFieldByPos(row, 1),
                this.getFieldByPos(row, 2),
                this.getFieldByPos(row, 3));
    }

    /*  12 13 14 15
        8  9  10 11
        4  5  6  7
        0  1  2  3
     */
    // TODO: 18.05.2020 NIE JESTEM DUMNY Z TEGO, prosze zrób ot jakoś ładnie
    //  jest źle, popraw
    void moveRight() {
        List<List<Field>> rows = Arrays.asList(
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]));
        List<Field> row;
        for (int i = 0; i < BOARD_DIMENSIONS - 1; i++) {
            row = getRow(i);
            System.out.println(row);
            rows.set(i, checkAvailableMoves(row));
            System.out.println(rows.get(i));
        }
        int counter = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            board.set(i, rows.get(i / BOARD_DIMENSIONS).get(i % BOARD_DIMENSIONS));
        }
    }

    private List<Field> checkAvailableMoves(List<Field> list) {
        boolean[] moved = new boolean[BOARD_DIMENSIONS];
        Arrays.fill(moved, Boolean.FALSE);
        for (int i = BOARD_DIMENSIONS - 1; i >= 0; i--) {
            boolean found = false;
            int index = i - 1;
            while (!found && index >= 0) {
                if (i != 0 && list.get(i).getValue() == list.get(index).getValue() && !moved[i] && !moved[i - 1] && list.get(i).getValue() != 0) {
                    list.get(i).setNextValue();
                    found = true;
                    moved[i] = true;
                    for (int j = i - 1; j >= 0; j--) {
                        if (j > 0) {
                            list.set(j, list.get(j - 1));
                        } else {
                            list.set(j, list.set(j, new Field()));
                        }
                    }
                } else if (list.get(i).getValue() == 0) {
                    if (i > 0) {
                        for (int j = i - 1; j >= 0; j--) {
                            if (j > 0) {
                                list.set(j, list.get(j - 1));
                            } else {
                                list.set(j, list.set(j, new Field()));
                            }
                        }
                    }
                }
                index--;
            }
        }
        for (int i = BOARD_DIMENSIONS - 1; i >= 0; i--) {
            if (list.get(i).getValue() == 0) {
                for (int j = i - 1; j >= 0; j--) {
                    if (j > 0) {
                        list.set(j, list.get(j - 1));
                    } else {
                        list.set(j, list.set(j, new Field()));
                    }
                }
            }
        }
        return list;
    }

    // TODO: 18.05.2020 myslalem że mogę potrzebować narazie do testów
    //  ale póki co nie używam
    public void setFieldValue(int x, int y, int value) {
        this.getFieldByPos(x, y).setValue(value);
    }

    public List<Field> getCopyBoard() {
        List<Field> cloneBoard = Arrays.asList(new Field[BOARD_SIZE]);
        for (int i = 0; i < BOARD_SIZE; i++) {
            cloneBoard.set(i, this.board.get(i));
        }
        return cloneBoard;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("board", board)
                .toString();
    }
}
