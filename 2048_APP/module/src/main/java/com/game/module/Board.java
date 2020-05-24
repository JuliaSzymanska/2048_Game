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

    // TODO: 19.05.2020 rzad to bedzie tak jak wdlg komentarza nizej: rzad pierwszy(zerowy) 12, 13, 14, 15
    private List<Field> getRow(int col) {
        return Arrays.asList(
                this.getFieldByPos(0, col),
                this.getFieldByPos(1, col),
                this.getFieldByPos(2, col),
                this.getFieldByPos(3, col));
    }

    // TODO: 19.05.2020 kolumna to bedzie tak jak wdlg komentarza nizej: kolumna pierwsza(zerowa) 12, 8, 4, 0
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
            System.out.println(row);
            rows.set(i, checkAvailableMoves(row));
            System.out.println(rows.get(i));
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
            System.out.println(row);
            Collections.reverse(row);
            row = checkAvailableMoves(row);
            Collections.reverse(row);
            System.out.println(row);
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
            System.out.println(col);
            col = checkAvailableMoves(col);
            cols.set(i, col);
            System.out.println(cols.get(i));
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
            System.out.println(col);
            Collections.reverse(col);
            col = checkAvailableMoves(col);
            Collections.reverse(col);
            System.out.println(col);
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
                // TODO: 19.05.2020 dalam tutaj te boole to w debuggerze bylo widac co jest false a co true
                boolean iIterator = (i != 0);
                boolean equals = (list.get(i).getValue() == list.get(index).getValue());
                boolean zero = (list.get(i).getValue() != 0);
//                if (i != 0 && list.get(i).getValue() == list.get(index).getValue() && list.get(i).getValue() != 0) {
                if (iIterator && equals && zero) {
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
        // TODO: 19.05.2020 narazie nie mam pomyslu ale jesli jest np taki rzad : 0 8 0 0, to przesunie ostatnie zero
        // TODO: i będzie 0 0 8 0 i to ostatnie zero tutaj zostanie i juz go nie usunie

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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("board", board)
                .toString();
    }
}
