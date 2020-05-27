package com.game.module;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// TODO: 17.05.2020 https://rosettacode.org/wiki/2048#Java kod gry w javie

public class Board {

    private List<Field> board;
    private final int BOARD_DIMENSIONS = 4;
    private final int BOARD_SIZE = BOARD_DIMENSIONS * BOARD_DIMENSIONS;
    private int score = 0;

    public Board() {
        this.board = newFieldsList();
    }

    Board(List<Integer> integerList) {
        this.board = newFieldsList();
        int counter = 0;
        for (int i : integerList) {
            board.get(counter).setValue(i);
            counter++;
        }
    }

    /**
     * Creates new fields list and set their values to 0.
     *
     * @return List of fields.
     */
    private List<Field> newFieldsList() {
        List<Field> fieldList = Arrays.asList(new Field[BOARD_SIZE]);
        for (int i = 0; i < BOARD_SIZE; i++) {
            fieldList.set(i, new Field(0));
        }
        return fieldList;
    }

    /**
     * Reset board variable by creating new fields list.
     */
    public void resetBoard() {
        this.board = newFieldsList();
    }

    public int getScore() {
        return score;
    }

    private List<Field> getAllEmptyFields() {
        List<Field> listOfEmptyFields = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (this.board.get(i).getValue() == 0) {
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

    private Field getFieldByPos(int x, int y) {
        if ((x < 0 || x > 3) || (y < 0 || y > 3)) {
            throw new IndexOutOfBoundsException("Values have to be in range 0 - 3");
        }
        return this.board.get(x + y * 4); // od lewej do prawej, od dołu do góry
    }

    private List<Field> getRow(int col) {
        return Arrays.asList(
                this.getFieldByPos(0, col),
                this.getFieldByPos(1, col),
                this.getFieldByPos(2, col),
                this.getFieldByPos(3, col));
    }

    private List<Field> getColumn(int row) {
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

    void moveRight() {
        List<List<Field>> rows = Arrays.asList(
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]));
        List<Field> row;
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            row = getRow(i);
            rows.set(i, checkAvailableMoves(row));
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            board.set(i, rows.get(i / BOARD_DIMENSIONS).get(i % BOARD_DIMENSIONS));
        }
    }

    void moveLeft() {
        List<List<Field>> rows = Arrays.asList(
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]));
        List<Field> row;
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            row = getRow(i);
            Collections.reverse(row);
            row = checkAvailableMoves(row);
            Collections.reverse(row);
            rows.set(i, row);
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            board.set(i, rows.get(i / BOARD_DIMENSIONS).get(i % BOARD_DIMENSIONS));
        }
    }

    void moveDown() {
        List<List<Field>> cols = Arrays.asList(
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]));
        List<Field> col;
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            col = getColumn(i);
            col = checkAvailableMoves(col);
            cols.set(i, col);
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            board.set(i, cols.get(i % BOARD_DIMENSIONS).get(i / BOARD_DIMENSIONS));
        }
    }

    void moveUp() {
        List<List<Field>> cols = Arrays.asList(
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]));
        List<Field> col;
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            col = getColumn(i);
            Collections.reverse(col);
            col = checkAvailableMoves(col);
            Collections.reverse(col);
            cols.set(i, col);
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            board.set(i, cols.get(i / BOARD_DIMENSIONS).get(i % BOARD_DIMENSIONS));
        }
    }

    private List<Field> checkAvailableMoves(List<Field> list) {
        for (int i = BOARD_DIMENSIONS - 1; i >= 0; i--) {
            boolean found = false;
            int index = i - 1;
            while (!found && index >= 0) {
//                boolean iIterator = (i != 0);
//                boolean equals = (list.get(i).getValue() == list.get(index).getValue());
//                boolean zero = (list.get(i).getValue() != 0);
                if (i != 0 && list.get(i).getValue() == list.get(index).getValue() && list.get(i).getValue() != 0) {
//                if (iIterator && equals && zero) {
                    list.get(i).setNextValue();
                    found = true;
                    for (int j = index; j >= 0; j--) {
                        if (j > 0) {
                            list.set(j, list.get(j - 1));
                        } else {
                            list.set(j, new Field());
                        }
                    }
                } else if (list.get(i).getValue() == 0) {
                    found = true;
                } else if (list.get(index).getValue() != 0) {
                    found = true;
                }
                index--;
            }
        }

        int index = 0;
        int zero_count = 0;
        for (Field f : list) {
            if (f.getValue() != 0) {
                break;
            }
            index++;
        }
        for (Field f : list) {
            if (f.getValue() == 0) {
                zero_count++;
            }
        }

        int move_right_count = zero_count - index;

        for (int i = 0; i < move_right_count; i++) {
            for (int j = BOARD_DIMENSIONS - 1; j >= 0; j--) {

                if (list.get(j).getValue() == 0) {
                    for (int k = j; k >= 0; k--) {
                        if (k > 0) {
                            list.set(k, list.get(k - 1));
                        } else {
                            list.set(k, list.set(k, new Field()));
                        }
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
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("board", board)
                .toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Board)) {
            return false;
        }

        Board c = (Board) obj;
        return this.board == c.board;
    }
}
