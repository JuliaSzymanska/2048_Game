package com.game.module;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board implements Serializable {

    private List<Field> board;
    private int score = 0;

    final static Map<String, Integer> moveDirections = new HashMap<>();
    static{

    }
    final static int MOVE_UP = 0;
    final static int MOVE_RIGHT = 1;
    final static int MOVE_DOWN = 2;
    final static int MOVE_LEFT = 3;
    private final int BOARD_DIMENSIONS = 4;
    private final int BOARD_SIZE = BOARD_DIMENSIONS * BOARD_DIMENSIONS;

    public Board() {
        this.board = newFieldsList();
        this.resetBoard();
    }

    Board(List<Integer> integerList) {
        this.board = newFieldsList();
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
   private void resetBoard() {
        for (Field i : board) {
            i.setValue(0);
        }
        // 2 pola na poczatku gry
        try {
            this.addNewNonEmptyFieldAfterMove();
            this.addNewNonEmptyFieldAfterMove();
        } catch (GameOverException e) {
            // TODO: 31.05.2020 narazie tu nie rzucam teog wyajtku bo sie robi straszny balagan
            e.printStackTrace();
        }
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
    private void addNewNonEmptyFieldAfterMove() throws GameOverException {
        this.isGameOver();
        List<Field> allEmptyFields = getAllEmptyFields();
        Collections.shuffle(allEmptyFields);
        allEmptyFields.get(0).setValue(Math.random() >= .9 ? 4 : 2);
    }

    /**
     * @param x
     * @param y
     * @return
     */
    private Field getFieldByPos(int x, int y) {
        if ((x < 0 || x > 3) || (y < 0 || y > 3)) {
            throw new IndexOutOfBoundsException("Values have to be in range 0 - 3");
        }
        return this.board.get(x + y * 4); // od lewej do prawej, od dołu do góry
    }

    /**
     * @param row
     * @return
     */
    private List<Field> getRow(int row) {
        return Arrays.asList(
                this.getFieldByPos(0, row),
                this.getFieldByPos(1, row),
                this.getFieldByPos(2, row),
                this.getFieldByPos(3, row));
    }

    /**
     * @param col
     * @return
     */
    private List<Field> getColumn(int col) {
        return Arrays.asList(
                this.getFieldByPos(col, 0),
                this.getFieldByPos(col, 1),
                this.getFieldByPos(col, 2),
                this.getFieldByPos(col, 3));
    }


    // TODO: 29.05.2020 zrobic z tego enum moze?,
    //  a moze nie? te inty są descriptive enough
    //  moze przeniesc do innej klasy

    // TODO: 31.05.2020 sprawdzic czy enum moze zwrocic funkcje po intcie
    void move(int direction) throws GameOverException {
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

    private List<List<Field>> get2dList() {
        return Arrays.asList(
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]),
                Arrays.asList(new Field[BOARD_DIMENSIONS]));
    }

    // TODO: 31.05.2020 Przemek skroc to tak jak sb wymarzyles
    private void moveRight() {
        List<List<Field>> rows = get2dList();
        List<Field> row;
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            row = getRow(i);
            moveFieldsInRowOrColumn(row);
            rows.set(i, row);
        }
    }

    private void moveLeft() {
        List<List<Field>> rows = get2dList();
        List<Field> row;
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            row = getRow(i);
            Collections.reverse(row);
            moveFieldsInRowOrColumn(row);
            Collections.reverse(row);
            rows.set(i, row);
        }
    }

    private void moveDown() {
        List<List<Field>> cols = get2dList();
        List<Field> col;
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            col = getColumn(i);
            moveFieldsInRowOrColumn(col);
            cols.set(i, col);
        }
    }

    private void moveUp() {
        List<List<Field>> cols = get2dList();
        List<Field> col;
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            col = getColumn(i);
            Collections.reverse(col);
            moveFieldsInRowOrColumn(col);
            Collections.reverse(col);
            cols.set(i, col);
        }
    }

    /**
     * Moves fields to right side and set last fields value to 0.
     *
     * @param fieldsList 2d list of fields in board as rows or columns.
     * @param index      index of the field to start with.
     */
    private void moveFieldsPositions(List<Field> fieldsList, int index) {
        for (int i = index; i >= 0; i--) {
            if (i > 0) {
                //jesli nie jest to ostatni element to przesuwa go o jeden w prawo
                fieldsList.get(i).setValue(fieldsList.get(i - 1).getValue());
            } else {
                //jesli to jest ostatni element to ustawiamy wartosc na 0
                fieldsList.get(i).setValue(0);
            }
        }
    }

    // TODO: 31.05.2020 dodaj tu komentarze bo ty to pisales
    private int countZerosToDelete(List<Field> fieldsList) {
        int index = 0;
        int zero_count = 0;
        for (Field f : fieldsList) {
            if (f.getValue() != 0) {
                break;
            }
            index++;
        }
        for (Field f : fieldsList) {
            if (f.getValue() == 0) {
                zero_count++;
            }
        }
        return zero_count - index;
    }

    /**
     * Removes zeros before others values.
     *
     * @param fieldsList 2d list of fields in board as rows or columns.
     */
    private void removeZerosInMove(List<Field> fieldsList) {
        int countMoves = countZerosToDelete(fieldsList);
        for (int i = 0; i < countMoves; i++) {
            for (int j = BOARD_DIMENSIONS - 1; j >= 0; j--) {
                //jesli wartosc jest rowna 0 to przesun od tej pozycji w prawo o 1
                if (fieldsList.get(j).getValue() == 0) {
                    moveFieldsPositions(fieldsList, j);
                }
            }
        }
    }

    private void moveFieldsInRowOrColumn(List<Field> fieldsList) {
        for (int i = BOARD_DIMENSIONS - 1; i >= 0; i--) {
            //bool, ktorego wartosc oznacza czy zostala znaleziony inny field z ktorym field moze sie polaczyc
            boolean found = false;
            // index na ktorym mamy szukac fielda do polaczenia
            int index = i - 1;
            //dopoki nie znalezlizmy(badz na pewno nie da sie znalezc) oraz index znajduje sie w rozmiarze liscie
            while (!found && index >= 0) {
                //jesli nie jest to ostatni element oraz to pole i pole na indexie sa rowne i pole ma wartosc rozna od 0 to
                if (i != 0 && fieldsList.get(i).getValue() == fieldsList.get(index).getValue() && fieldsList.get(i).getValue() != 0) {
                    //podnies do kwadratu wartosc pola
                    fieldsList.get(i).setNextValue();
                    //zwieksz liczbe pkt
                    this.updateScore(fieldsList.get(i).getValue());
                    //przesun pola o jeden w prawo
                    moveFieldsPositions(fieldsList, index);
                    //ustaw wartosc boola na true czyli znalezlismy
                    found = true;
                    //jesli pole jest rowne zero
                } else if (fieldsList.get(i).getValue() == 0) {
                    //ustaw bool na znalezione - czyli nie musimy szukac liczby do poalczenia bo to jest zero
                    found = true;
                    // jesli wartosc pola na indeksie jest rozna od zera
                } else if (fieldsList.get(index).getValue() != 0) {
                    //ustaw bool na znaleziony bo dla tego pola nie znajdziemy pola do polaczneia
                    found = true;
                }
                //zmniejsz indeks o 1, czyi szukamy na nastepnym polu do polaczenia, jesli found == true to nie ma znaczenia
                index--;
            }
        }
        //wywolaj metode usuwajaca zera z planszy pomiedzy liczbami
        removeZerosInMove(fieldsList);
    }

    // TODO: 18.05.2020 myslalem że mogę potrzebować narazie do testów
    //  ale póki co nie używam
    public void setFieldValue(int x, int y, int value) {
        this.getFieldByPos(x, y).setValue(value);
    }

    public List<Field> getCopyBoard() {
//        List<Field> cloneBoard = Arrays.asList(new Field[BOARD_SIZE]);
//        for (int i = 0; i < BOARD_SIZE; i++) {
//            cloneBoard.set(i, new Field(this.board.get(i)));
//        }
        // TODO: 02.06.2020 FIX
        return this.board;
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE);
        toStringBuilder.append("");
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            toStringBuilder.append(this.getRow(i));
            toStringBuilder.append("\n");
        }
        return toStringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;
        return new EqualsBuilder()
                .append(board.board, this.board)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(this.board)
                .toHashCode();
    }

    /**
     * Checks if game is over.
     * CALL ONLY BEFORE TRYING TO ADD A NEW FIELD AFTER MOVE
     *
     * @throws Exception when there are no empty fields on the board
     */
    private void isGameOver() throws GameOverException {
        for (Field i : this.board) {
            if (i.getValue() == 0) {
                return;
            }
        }
        throw new GameOverException("Game over");
    }
}
