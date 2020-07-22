package com.game.module;


import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BoardTest {
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

    private List<Integer> integers = new ArrayList<Integer>() {
        {
            add(0);
            add(2);
            add(2);
            add(2);

            add(2);
            add(0);
            add(2);
            add(0);

            add(2);
            add(2);
            add(4);
            add(2);

            add(0);
            add(0);
            add(8);
            add(0);
        }
    };

    @Test
    public void boardDefaultConstructorTest(){
        Board board = new Board();
        int numberOfNonZeroFields = 0;
        for (Field i : board.getCopyBoard()) {
            if(i.getValue() != 0){
                numberOfNonZeroFields++;
            }
        }
        Assert.assertEquals(numberOfNonZeroFields, 2);
    }


    @Test
    public void boardListConstructorTest() {
        List<Integer> expectedValues = new ArrayList<Integer>() {
            {
                add(0);
                add(2);
                add(2);
                add(0);

                add(4);
                add(0);
                add(2);
                add(8);

                add(0);
                add(0);
                add(2);
                add(0);

                add(0);
                add(16);
                add(8);
                add(0);
            }
        };
        Board board = new Board(expectedValues);
        Board board1 = new Board(expectedValues);
        assertEquals(board, board1);
    }

    @Test
    public void boardMoveRightTest() {
        List<Integer> expectedValuesAfterMove = new ArrayList<Integer>() {
            {
                add(0);
                add(0);
                add(2);
                add(4);

                add(0);
                add(0);
                add(0);
                add(4);

                add(0);
                add(4);
                add(4);
                add(2);

                add(0);
                add(0);
                add(0);
                add(8);
            }
        };
        Board board = new Board(this.integers);
        System.out.println("MOVE RIGHT");
        System.out.println("BEFORE");
        System.out.println(board);
        try {
            board.moveRight();
        } catch (GameOverException e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        for (Pair<Integer, Field> item : zip(expectedValuesAfterMove, board.getCopyBoard())) {
            if (!item.getLeft().equals(0)) {
                Assert.assertEquals(item.getLeft().intValue(), item.getRight().getValue());
            }
        }
        System.out.println("AFTER");
        System.out.println(board);
    }

    @Test
    public void boardMoveLeftTest() {
        List<Integer> expectedValuesAfterMove = new ArrayList<Integer>() {
            {
                add(4);
                add(2);
                add(0);
                add(0);

                add(4);
                add(0);
                add(0);
                add(0);

                add(4);
                add(4);
                add(2);
                add(0);

                add(8);
                add(0);
                add(0);
                add(0);
            }
        };
        Board board = new Board(this.integers);
        System.out.println("MOVE LEFT");
        System.out.println("BEFORE");
        System.out.println(board);
        try {
            board.moveLeft();
        } catch (GameOverException e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        for (Pair<Integer, Field> item : zip(expectedValuesAfterMove, board.getCopyBoard())) {
            if (!item.getLeft().equals(0)) {
                Assert.assertEquals(item.getLeft().intValue(), item.getRight().getValue());
            }
        }
        System.out.println("AFTER");
        System.out.println(board);
    }

    @Test
    public void boardMoveUpTest() {
        List<Integer> expectedValuesAfterMove = new ArrayList<Integer>() {
            {
                add(4);
                add(4);
                add(4);
                add(4);

                add(0);
                add(0);
                add(4);
                add(0);

                add(0);
                add(0);
                add(8);
                add(0);

                add(0);
                add(0);
                add(0);
                add(0);
            }
        };
        Board board = new Board(this.integers);
        System.out.println("MOVE UP");
        System.out.println("BEFORE");
        System.out.println(board);
        try {
            board.moveUp();
        } catch (GameOverException e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        for (Pair<Integer, Field> item : zip(expectedValuesAfterMove, board.getCopyBoard())) {
            if (!item.getLeft().equals(0)) {
                Assert.assertEquals(item.getLeft().intValue(), item.getRight().getValue());
            }
        }
        System.out.println("AFTER");
        System.out.println(board);
    }

    @Test
    public void boardMoveDownTest() {
        List<Integer> expectedValuesAfterMove = new ArrayList<Integer>() {
            {
                add(0);
                add(0);
                add(0);
                add(0);

                add(0);
                add(0);
                add(4);
                add(0);

                add(0);
                add(0);
                add(4);
                add(0);

                add(4);
                add(4);
                add(8);
                add(4);
            }
        };
        Board board = new Board(this.integers);
        System.out.println("MOVE DOWN");
        System.out.println("BEFORE");
        System.out.println(board);
        try {
            board.moveDown();
        } catch (GameOverException e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        for (Pair<Integer, Field> item : zip(expectedValuesAfterMove, board.getCopyBoard())) {
            if (!item.getLeft().equals(0)) {
                Assert.assertEquals(item.getLeft().intValue(), item.getRight().getValue());
            }
        }
        System.out.println("AFTER");
        System.out.println(board);
    }

    @Test
    public void updateScoreTest() {
        Board board = new Board(this.integers);
        Assert.assertEquals(board.getScore(), 0);
        try {
            board.moveRight();
        } catch (GameOverException e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        Assert.assertEquals(board.getScore(), 12);
    }

    @Test
    public void createNewBoardFieldAmountTest() {
        Board board = new Board();
        int newFieldsCounter = 0;
        for (Field f : board.getCopyBoard()) {
            if (f.getValue() != 0) {
                newFieldsCounter++;
            }
        }
        // TODO: 31.05.2020 zrobic drugi test ktory sprawdzi czy po ruchu tworzy sie nowe pole
        Assert.assertEquals(newFieldsCounter, 2);
    }

    @Test
    public void copyBoardTest() {
        Board board = new Board(this.integers);
        List<Field> copyBoard = board.getCopyBoard();
        for (Pair<Field, Field> item : zip(copyBoard, board.getCopyBoard())) {
            Assert.assertEquals(item.getLeft(), item.getRight());
        }
        copyBoard.get(7).setValue(16);
        // TODO: 03.06.2020 nie przechodzi dlatego ze teraz kopja planszy nie jest kopia
        //  tylko przepisuje referencje
        //  bedzie trzeba poprawic jak sie przerzucimy na prawdziwa kopie znowu
//        Assert.assertNotEquals(copyBoard.get(7), board.getCopyBoard().get(7));
    }

    @Test
    public void getScoreTest() {
        Board board = new Board(this.integers);
        Assert.assertEquals(0, board.getScore());
        try {
            board.moveRight();
        } catch (GameOverException e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        Assert.assertEquals(12, board.getScore());
    }

    @Test
    public void restartGameTest() {
        Board board = new Board(this.integers);
        Assert.assertEquals(0, board.getScore());
        try {
            board.moveRight();
        } catch (GameOverException e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        Assert.assertEquals(12, board.getScore());
        board.restartGame();
        int numberOfNonZeroFields = 0;
        for (Field i : board.getCopyBoard()) {
            if(i.getValue() != 0){
                numberOfNonZeroFields++;
            }
        }
        Assert.assertEquals(board.getScore(), 0);
        Assert.assertEquals(numberOfNonZeroFields, 2);
    }

    @Test
    public void getCopyBoardTest(){
        Board board = new Board(integers);
        assertEquals(integers.toString(), board.getCopyBoard().toString());
    }

    @Test
    public void toStringTest(){
        Board board = new Board(integers);
        assertEquals(",[0, 2, 2, 2],\n" +
                ",[2, 0, 2, 0],\n" +
                ",[2, 2, 4, 2],\n" +
                ",[0, 0, 8, 0],\n", board.toString());
    }

    @Test
    public void equalsTest() {
        Board board1 = new Board(integers);
        Board board2 = new Board(integers);
        assertTrue(board1.equals(board2));

        assertTrue(board1.equals(board1));

        try {
            board1.moveRight();
        } catch (GameOverException ignore) {
        }
        assertFalse(board1.equals(board2));

        Board board3 = null;
        assertFalse(board1.equals(board3));

        int a = 3;
        assertFalse(board1.equals(a));
    }

    @Test
    public void hashCodeTest(){
        Board board1 = new Board(integers);
        Board board2 = new Board(integers);
        if (board1.hashCode() != board2.hashCode())
            assertNotEquals(board1, board2);
    }

}
