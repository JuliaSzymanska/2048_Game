package com.game.module;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// TODO: 17.05.2020 https://rosettacode.org/wiki/2048#Java kod gry w javie

public class Board implements Serializable {

    private List<Field> board;
    private int score = 0;

    final static int MOVE_UP = 0;
    final static int MOVE_RIGHT = 1;
    final static int MOVE_DOWN = 2;
    final static int MOVE_LEFT = 3;
    private final int BOARD_DIMENSIONS = 4;
    private final int BOARD_SIZE = BOARD_DIMENSIONS * BOARD_DIMENSIONS;

    public Board() {
        this.resetBoard();
        // 2 pola na poczatku gry
        this.addNewNonEmptyFieldAfterMove();
        this.addNewNonEmptyFieldAfterMove();
    }

    Board(List<Integer> integerList) {
        this.resetBoard();
        int counter = 0;
        for (int i : integerList) {
            board.get(counter).setValue(i);
            counter++;
        }
    }

    void restartGame() {
        this.resetBoard();
        this.score = 0;
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
    void resetBoard() {
        this.board = newFieldsList();
    }

    int getScore() {
        return score;
    }

    private void updateScore(int scoreDelta) {
        this.score += scoreDelta;
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
        try {
            this.isGameOver();
        } catch (Exception e) {
            // TODO: 29.05.2020 Aktualnie tylko łapię i nic nei robię.
            //  Zamiar jest taki żeby rzucać ten wyjątek gdzies w klasie Game żeby stwierdzic czy gra sie skonczyla.
            e.printStackTrace();
        }
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


    // TODO: 29.05.2020 zrobic z tego enum moze?, moze przeniesc do innej klasy
    void move(int direction) {
        switch (direction) {
            case MOVE_UP:
                moveUp();
                break;
            case MOVE_RIGHT:
                moveRight();
                break;
            case MOVE_DOWN:
                moveDown();
                break;
            case MOVE_LEFT:
                moveLeft();
                break;
            default:
                throw new IllegalArgumentException("value can only be equal to 0, 1, 2 or 3");
        }
        this.addNewNonEmptyFieldAfterMove();
    }
    
    private void moveRight() {
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
    

    private void moveLeft() {
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

    private void moveDown() {
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
        // TODO: 29.05.2020 W każdym z nich - nie można podmieniać referencji do fieldów
        for (int i = 0; i < BOARD_SIZE; i++) {
            board.set(i, cols.get(i % BOARD_DIMENSIONS).get(i / BOARD_DIMENSIONS));
        }
    }

    private void moveUp() {
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

    // TODO: 29.05.2020 To trzeba rozbić i okomentować ładnie i najlepiej uprościć
    //  nie jestem w stanie zdebugować dlaczego ruch do góry nie działa bo nie wiadomo co tutaj się dzieje
    //  je wiem że mi opowiadałaś co tam na klikałaś, ale no jakby dzien pozniej juz nie wiadomo o co cho
    private List<Field> checkAvailableMoves(List<Field> list) {
        for (int i = BOARD_DIMENSIONS - 1; i >= 0; i--) {
            boolean found = false;
            int index = i - 1;
            while (!found && index >= 0) {
                if (i != 0 && list.get(i).getValue() == list.get(index).getValue() && list.get(i).getValue() != 0) {
                    list.get(i).setNextValue();

                    // TODO: 29.05.2020 chyba ok
                    this.updateScore(list.get(i).getValue());

                    found = true;
                    for (int j = index; j >= 0; j--) {
                        if (j > 0) {
                            list.get(j).setValue(list.get(j - 1).getValue());
                        } else {
                            list.get(j).setValue(0);
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
                            list.get(k).setValue(list.get(k - 1).getValue());
                        } else {
                            list.get(k).setValue(0);
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
            cloneBoard.set(i, new Field(this.board.get(i)));
        }
        return cloneBoard;
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE);
        toStringBuilder.append("");
        for(int i = 0; i < BOARD_DIMENSIONS; i++) {
            toStringBuilder.append(this.getRow(i));
            toStringBuilder.append("\n");
        }
        return toStringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Board)) {
            return false;
        }
        Board board = (Board) o;
        return new EqualsBuilder().append(board.board, this.board).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.board).toHashCode();
    }

    // TODO: 29.05.2020 Add GameOverException
    /**
     * Checks if game is over.
     * CALL ONLY BEFORE TRYING TO ADD A NEW FIELD AFTER MOVE
     * @throws Exception when there are no empty fields on the board
     */
    private void isGameOver() throws Exception {
        for (Field i : this.board) {
            if(i.getValue() == 0) {
                return;
            }
        }
        // TODO: 29.05.2020 add own exception and catch to detect game over
        throw new Exception("GAME OVER");
    }

}
