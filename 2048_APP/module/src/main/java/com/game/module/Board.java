package com.game.module;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Board implements Serializable {

    private List<Field> board;
    private List<Integer> amountMovedList;
    private List<List<Field>> previousBoards;
    private List<Integer> previousScores;
    private int score = 0;
    private boolean isGoalAchieved = false;

    private final static int PREVIOUS_BOARDS_STORED_AMOUNT = 3;
    private final static int BOARD_DIMENSIONS = 4;
    private final static int BOARD_SIZE = BOARD_DIMENSIONS * BOARD_DIMENSIONS;
    private final static int FIELD_POSITION_MIN = 0;
    private final static int FIELD_POSITION_MAX = BOARD_DIMENSIONS - 1;

    /**
     * Default class constructor.
     */
    public Board() {
        this.board = newFieldsList();
        this.amountMovedList = newAmountMovedList();
        this.resetBoard();
    }

    /**
     * Class constructor specifying fields of the board.
     * @param integerList - list of int to put in the board.
     */
    Board(List<Integer> integerList) {
        this.board = newFieldsList();
        this.amountMovedList = newAmountMovedList();
        int counter = 0;
        for (int i : integerList) {
            board.get(counter).setValue(i);
            counter++;
        }
        this.setVariablesToDefault();
    }

    /**
     * Sets variables like score, previousBoards, previousScores, isGoalAchieved to default values.
     */
    private void setVariablesToDefault() {
        this.score = 0;
        this.previousBoards = new ArrayList<>();
        this.previousScores = new ArrayList<>();
        this.isGoalAchieved = false;
    }

    /**
     * Restarts the game.
     */
    void restartGame() {
        this.resetBoard();
        this.setVariablesToDefault();
    }

    /**
     * Creates new fields list and set their values to 0.
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
     * Creates new list filled with 0 for checking amount moved fields.
     * @return new list filled with 0.
     */
    private List<Integer> newAmountMovedList() {
        List<Integer> amountMovedList = Arrays.asList(new Integer[BOARD_SIZE]);
        java.util.Collections.fill(amountMovedList, 0);
        return amountMovedList;
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
        } catch (GoalAchievedException ignore) {
            // nie może się rzucić fizycznie ten wyjątek tutaj
        }
    }

    /**
     * @return board's score.
     */
    int getScore() {
        return score;
    }

    /**
     * @return available undo amount.
     */
    int getAvaiableUndoAmount() {
        return this.previousBoards.size();
    }

    /**
     * Updates score by adding param to current score.
     * @param scoreDelta - number by which score have to be increased.
     */
    private void updateScore(int scoreDelta) {
        this.score += scoreDelta;
    }

    /**
     * Creates new list of empty fields in board.
     * @return - list of empty fields in board.
     */
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
    private void addNewNonEmptyFieldAfterMove() throws GoalAchievedException {
        this.isGoalAchieved();
        List<Field> allEmptyFields = getAllEmptyFields();
        Collections.shuffle(allEmptyFields);
        allEmptyFields.get(0).setValue(Math.random() >= .9 ? 4 : 2);
    }

    /**
     * Checks if there is field with a number higher or equal to 2048.
     * @throws GoalAchievedException - exception is thrown when there is field with a number higher or equal to 2048.
     */
    private void isGoalAchieved() throws GoalAchievedException {
        for (Field i : board) {
            if (i.getValue() >= 2048 && !this.isGoalAchieved) {
                this.isGoalAchieved = true;
                throw new GoalAchievedException("Goal Achieved");
            }
        }
    }

    /**
     * @param x horizontal position.
     * @param y vertical position.
     * @return return field at x, y position.
     */
    private Field getFieldByPos(int x, int y) {
        if ((x < FIELD_POSITION_MIN || x > FIELD_POSITION_MAX) || (y < FIELD_POSITION_MIN || y > FIELD_POSITION_MAX)) {
            throw new IndexOutOfBoundsException("Values have to be in range " + FIELD_POSITION_MIN + " - " + FIELD_POSITION_MAX);
        }
        return this.board.get(x + y * BOARD_DIMENSIONS); // od lewej do prawej, od dołu do góry
    }

    /**
     * @param x horizontal position.
     * @param y vertical position.
     * @return return the moved amount for field at x, y position.
     */
    private Integer getAmountMovedByPos(int x, int y) {
        if ((x < FIELD_POSITION_MIN || x > FIELD_POSITION_MAX) || (y < FIELD_POSITION_MIN || y > FIELD_POSITION_MAX)) {
            throw new IndexOutOfBoundsException("Values have to be in range " + FIELD_POSITION_MIN + " - " + FIELD_POSITION_MAX);
        }
        return this.amountMovedList.get(x + y * BOARD_DIMENSIONS);
    }

    /**
     * @param row row's number.
     * @return row at row's number position.
     */
    private List<Field> getRow(int row) {
        return Arrays.asList(
                this.getFieldByPos(0, row),
                this.getFieldByPos(1, row),
                this.getFieldByPos(2, row),
                this.getFieldByPos(3, row));
    }

    /**
     * @param col column's number.
     * @return column at column's position
     */
    private List<Field> getColumn(int col) {
        return Arrays.asList(
                this.getFieldByPos(col, 0),
                this.getFieldByPos(col, 1),
                this.getFieldByPos(col, 2),
                this.getFieldByPos(col, 3));
    }

    /**
     *
     * @param row row's number.
     * @return fields at row's number.
     */
    private List<Integer> getAmountMovedRow(int row) {
        return Arrays.asList(
                this.getAmountMovedByPos(0, row),
                this.getAmountMovedByPos(1, row),
                this.getAmountMovedByPos(2, row),
                this.getAmountMovedByPos(3, row));
    }

    /**
     *
     * @param col column's number.
     * @return fields at column's number.
     */
    private List<Integer> getAmountMovedColumn(int col) {
        return Arrays.asList(
                this.getAmountMovedByPos(col, 0),
                this.getAmountMovedByPos(col, 1),
                this.getAmountMovedByPos(col, 2),
                this.getAmountMovedByPos(col, 3));
    }

    /**
     * Sets at class list specific row with row from list amountMovedListRow.
     * @param row row's number.
     * @param amountMovedListRow list of amount of moves for fields.
     */
    private void setAmoutMovedRow(int row, List<Integer> amountMovedListRow) {
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            this.amountMovedList.set(i + row * BOARD_DIMENSIONS, amountMovedListRow.get(i));
        }
    }

    /**
     * Sets at class list specific column with column from list amountMovedListColumn.
     * @param col column's number.
     * @param amountMovedListColumn list of amount of moves for fields.
     */
    private void setAmountMovedColumn(int col, List<Integer> amountMovedListColumn) {
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            this.amountMovedList.set(col + i * BOARD_DIMENSIONS, amountMovedListColumn.get(i));
        }
    }

    /**
     * Saves current score and board to make undo available.
     * Deletes first saved score and board when available amount of undo is exceeded.
     */
    private void appendPreviousBoardToHistory() {
        this.previousBoards.add(this.getCopyBoard());
        this.previousScores.add(this.score);
        if (this.previousBoards.size() > PREVIOUS_BOARDS_STORED_AMOUNT) {
            this.previousBoards.remove(0);
            this.previousScores.remove(0);
        }
    }

    /**
     * @param listA first list to pair
     * @param listB second list to pair
     * @param <A> type of elements of listA
     * @param <B> type of elements of listB
     * @return pair of two list passed as params
     */
    private static <A, B> List<Pair<A, B>> zip(List<A> listA, List<B> listB) {
        if (listA.size() != listB.size()) {
            throw new IllegalArgumentException("Lists must have same size");
        }

        List<Pair<A, B>> pairList = new LinkedList<>();

        for (int index = 0; index < listA.size(); index++) {
            pairList.add(Pair.of(listA.get(index), listB.get(index)));
        }
        return pairList;
    }

    /**
     * Restores the board and sthe score to the state it was in before the last move.
     */
    void undoPreviousMove() {
        if (this.previousBoards.size() == 0) {
            return;
        }
        List<Field> previousBoard = this.previousBoards.get(this.previousBoards.size() - 1);
        for (Pair<Field, Field> item : zip(previousBoard, this.board)) {
            item.getRight().setValue(item.getLeft().getValue());
        }
        this.score = this.previousScores.get(this.previousScores.size() - 1);
        this.previousBoards.remove(this.previousBoards.size() - 1);
        this.previousScores.remove(this.previousScores.size() - 1);
    }

    // TODO: 28.07.2020 sprawdz cyz poprawnie to opisalam
    /**
     * @param copyList previous state of board.
     * @return if board has changed.
     */
    private boolean checkIfBoardChanged(List<Field> copyList) {
        boolean hasChanged = false;
        for (Pair<Field, Field> item : zip(copyList, this.board)) {
            if (!item.getLeft().equals(item.getRight())) {
                hasChanged = true;
            }
        }
        if (hasChanged) {
            for (Pair<Field, Field> item : zip(copyList, this.board)) {
                item.getRight().setValue(item.getLeft().getValue());
            }
        }
        return hasChanged;
    }

    /**
     * Chceck if there is any available moves that will change board.
     * @param copyList previous state of board.
     * @throws GoalAchievedException when one or more fields have value equal or higher then 2048.
     * @throws GameOverException when the game ended.
     */
    private void testIfGameOver(List<Field> copyList) throws GoalAchievedException, GameOverException {
        for (Pair<Field, Field> item : zip(copyList, this.board)) {
            if (!item.getLeft().equals(item.getRight())) {
                this.addNewNonEmptyFieldAfterMove();
                return;
            }
        }
        if (this.getAllEmptyFields().size() != 0) {
            return;
        }
        int score_before_test = this.score;
        this.moveDownAndDontTestIfGameOver();
        this.amountMovedList = newAmountMovedList();
        if (this.checkIfBoardChanged(copyList)) {
            this.score = score_before_test;
            return;
        }
        this.moveLeftAndDontTestIfGameOver();
        this.amountMovedList = newAmountMovedList();
        if (this.checkIfBoardChanged(copyList)) {
            this.score = score_before_test;
            return;
        }
        this.moveUpAndDontTestIfGameOver();
        this.amountMovedList = newAmountMovedList();
        if (this.checkIfBoardChanged(copyList)) {
            this.score = score_before_test;
            return;
        }
        this.moveRightAndDontTestIfGameOver();
        this.amountMovedList = newAmountMovedList();
        if (this.checkIfBoardChanged(copyList)) {
            this.score = score_before_test;
            return;
        }
        throw new GameOverException("Game lost");
    }

    private void moveRightAndDontTestIfGameOver() {
        this.appendPreviousBoardToHistory();
        this.amountMovedList = this.newAmountMovedList();
        List<Field> row;
        List<Integer> rowAmountMoved;
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            row = getRow(i);
            rowAmountMoved = getAmountMovedRow(i);
            moveFieldsInRowOrColumn(row, rowAmountMoved);
            setAmoutMovedRow(i, rowAmountMoved);
        }
    }

    void moveRight() throws GameOverException, GoalAchievedException {
        List<Field> copyList = this.getCopyBoard();
        this.moveRightAndDontTestIfGameOver();
        testIfGameOver(copyList);
    }

    private void moveLeftAndDontTestIfGameOver() {
        this.appendPreviousBoardToHistory();
        this.amountMovedList = this.newAmountMovedList();
        List<Field> row;
        List<Integer> rowAmountMoved;
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            row = getRow(i);
            rowAmountMoved = getAmountMovedRow(i);
            Collections.reverse(row);
            Collections.reverse(rowAmountMoved);
            this.moveFieldsInRowOrColumn(row, rowAmountMoved);
            Collections.reverse(rowAmountMoved);
            this.setAmoutMovedRow(i, rowAmountMoved);
        }
    }

    void moveLeft() throws GameOverException, GoalAchievedException {
        List<Field> copyList = this.getCopyBoard();
        this.moveLeftAndDontTestIfGameOver();
        this.testIfGameOver(copyList);
    }

    private void moveDownAndDontTestIfGameOver() {
        this.appendPreviousBoardToHistory();
        this.amountMovedList = this.newAmountMovedList();
        List<Field> col;
        List<Integer> colAmountMoved;
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            col = getColumn(i);
            colAmountMoved = getAmountMovedColumn(i);
            this.moveFieldsInRowOrColumn(col, colAmountMoved);
            this.setAmountMovedColumn(i, colAmountMoved);
        }
    }

    void moveDown() throws GameOverException, GoalAchievedException {
        List<Field> copyList = this.getCopyBoard();
        this.moveDownAndDontTestIfGameOver();
        this.testIfGameOver(copyList);
    }

    private void moveUpAndDontTestIfGameOver() {
        this.appendPreviousBoardToHistory();
        this.amountMovedList = this.newAmountMovedList();
        List<Field> col;
        List<Integer> colAmountMoved;
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            col = getColumn(i);
            colAmountMoved = getAmountMovedColumn(i);
            Collections.reverse(col);
            Collections.reverse(colAmountMoved);
            this.moveFieldsInRowOrColumn(col, colAmountMoved);
            Collections.reverse(colAmountMoved);
            this.setAmountMovedColumn(i, colAmountMoved);
        }
    }

    void moveUp() throws GameOverException, GoalAchievedException {
        List<Field> copyList = this.getCopyBoard();
        this.moveUpAndDontTestIfGameOver();
        testIfGameOver(copyList);
    }

    /**
     * Moves fields to right side and set last fields value to 0.
     *
     * @param fieldsList 2d list of fields in board as rows or columns.
     * @param index      index of the field to start with.
     */
    // TODO: 25.07.2020 w liscie trzeba inkrementowac dla indeksow dla pol ktore się przesuenly
    private void moveFieldsPositions(List<Field> fieldsList, int index, List<Integer> moveCountList) {
        for (int i = index; i >= 0; i--) {
            if (i > 0) {
                //jesli nie jest to ostatni element to przesuwa go o jeden w prawo
                fieldsList.get(i).setValue(fieldsList.get(i - 1).getValue());
            } else {
                //jesli to jest ostatni element to ustawiamy wartosc na 0
                fieldsList.get(i).setValue(0);
            }
//            if (i != index) {
//                moveCountList.set(i, moveCountList.get(i) + 1);
//                amountCalled++;
//            }
        }
    }

    /**
     * @param fieldsList list of moving fields in board where zero should be deleted.
     * @return amount of zeros in param to delete during move.
     */
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
     * @return list with amount of moves.
     */
    List<Integer> getAmountMovedList() {
        return amountMovedList;
    }

    /**
     * Removes zeros before others values.
     * @param fieldsList 2d list of fields in board as rows or columns.
     */
    // TODO: 25.07.2020 przy kazdym przejsciu algorytmu, wykonaniu ruchu, należy zinkrementować int na pozycjach na których się przesuneły pola usuwajac zero.
    //  Ostatecznie dla pól na których były 0 resetujemy int
    private void removeZerosInMove(List<Field> fieldsList, List<Integer> moveCountList) {
        int countMoves = countZerosToDelete(fieldsList);
        // TODO: 26.07.2020 tutaj jest sprawdzanie o ile się ruszyło
        for(int i = 1; i < fieldsList.size(); i++) {
            if(fieldsList.get(i).getValue() == 0) {
                for(int j = i - 1; j >= 0; j--) {
                    moveCountList.set(j, moveCountList.get(j) + 1);
                }
            }
        }
        for (int i = 0; i < countMoves; i++) {
            for (int j = BOARD_DIMENSIONS - 1; j >= 0; j--) {
                //jesli wartosc jest rowna 0 to przesun od tej pozycji w prawo o 1
                if (fieldsList.get(j).getValue() == 0) {
                    moveFieldsPositions(fieldsList, j, moveCountList);
                }
            }
        }
    }

    /**
     * Moves right fields in row or column pass as param.
     * @param fieldsList list of fields in row or column to move.
     * @param moveCountList list with numbers of field's move.
     */
    private void moveFieldsInRowOrColumn(List<Field> fieldsList, List<Integer> moveCountList) {
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
                    // TODO: 26.07.2020 tutaj też jest sprawdzanie o ile się ruszyło, jeżeli kasujemy frajera(laczymy klocek)
                    for (int j = index; j >= 0; j--) {
                        moveCountList.set(j, moveCountList.get(j) + 1);
                    }
                    fieldsList.get(i).setNextValue();
                    //zwieksz liczbe pkt
                    this.updateScore(fieldsList.get(i).getValue());
                    //przesun pola o jeden w prawo
                    moveFieldsPositions(fieldsList, index, moveCountList);
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
        removeZerosInMove(fieldsList, moveCountList);
    }

    /**
     * @return copy of the board.
     */
    List<Field> getCopyBoard() {
        List<Field> cloneBoard = newFieldsList();
        for (int i = 0; i < BOARD_SIZE; i++) {
            cloneBoard.get(i).setValue(this.board.get(i).getValue());
        }
        return cloneBoard;
    }

    /**
     * @return board.
     */
    public List<Field> getBoard() {
        return this.board;
    }

    /**
     * Checks if game is over.
     * CALL ONLY BEFORE TRYING TO ADD A NEW FIELD AFTER MOVE
     *
     * @throws Exception when there are no empty fields on the board
     */

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

    private String testToString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE);
        toStringBuilder.append("BOARD \n");

        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            toStringBuilder.append(this.getRow(i));
            toStringBuilder.append("\n");
        }
        toStringBuilder.append("MOVED AMOUNT \n");
        for (int i = 0; i < BOARD_DIMENSIONS; i++) {
            toStringBuilder.append(this.getAmountMovedRow(i));
            toStringBuilder.append("\n");
        }
        return toStringBuilder.toString();
    }

    /**
     * @param o the object to check for equality.
     * @return true if <i>this</i> is numerically equal to param.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;
        return new EqualsBuilder()
                .append(board.board, this.board)
                .isEquals();
    }

    /**
     * @return hash code for board.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(this.board)
                .toHashCode();
    }
}
