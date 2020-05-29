package com.game.module;


import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
        System.out.println(board);
        board.move(Board.MOVE_RIGHT);
        for (Pair<Integer, Field> item: zip(expectedValuesAfterMove, board.getCopyBoard())) {
            if (!item.getLeft().equals(0)) {
                Assert.assertEquals(item.getLeft().intValue(), item.getRight().getValue());
            }
        }
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
        System.out.println(board);
        board.move(Board.MOVE_LEFT);
        for (Pair<Integer, Field> item: zip(expectedValuesAfterMove, board.getCopyBoard())) {
            if (!item.getLeft().equals(0)) {
                Assert.assertEquals(item.getLeft().intValue(), item.getRight().getValue());
            }
        }
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
        System.out.println(board);
        board.move(Board.MOVE_UP);
        for (Pair<Integer, Field> item: zip(expectedValuesAfterMove, board.getCopyBoard())) {
            if (!item.getLeft().equals(0)) {
                Assert.assertEquals(item.getLeft().intValue(), item.getRight().getValue());
            }
        }
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
        System.out.println(board);
        board.move(Board.MOVE_DOWN);
        for (Pair<Integer, Field> item: zip(expectedValuesAfterMove, board.getCopyBoard())) {
            if (!item.getLeft().equals(0)) {
                Assert.assertEquals(item.getLeft().intValue(), item.getRight().getValue());
            }
        }
        System.out.println(board);
    }

    @Test
    public void updateScoreTest() {
        Board board = new Board(this.integers);
        Assert.assertEquals(board.getScore(), 0);
        board.move(Board.MOVE_RIGHT);
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
        Assert.assertEquals(newFieldsCounter, 2);
    }

    @Test
    public void copyBoardTest() {
        Board board = new Board(this.integers);
        List<Field> copyBoard = board.getCopyBoard();
        for (Pair<Field, Field> item: zip(copyBoard, board.getCopyBoard())) {
                Assert.assertEquals(item.getLeft(), item.getRight());
        }
        copyBoard.get(7).setValue(16);
        System.out.println(copyBoard.get(7));
        System.out.println(board.getCopyBoard().get(7));
        Assert.assertNotEquals(copyBoard.get(7), board.getCopyBoard().get(7));
    }

    @Test
    public void restartGameTest() {
        Board board = new Board(this.integers);
        Assert.assertEquals(board.getScore(), 0);
        board.move(Board.MOVE_RIGHT);
        Assert.assertEquals(board.getScore(), 12);
        board.restartGame();
        for (Field i : board.getCopyBoard()) {
            Assert.assertEquals(i.getValue(), 0);
        }
        Assert.assertEquals(board.getScore(), 0);
    }
}
