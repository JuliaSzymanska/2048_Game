package com.game.module;

import com.game.module.exceptions.GameOverException;
import com.game.module.exceptions.GoalAchievedException;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

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

    private List<Integer> gameOverIntegers = new ArrayList<Integer>() {
        {
            add(2);
            add(4);
            add(2);
            add(4);

            add(4);
            add(2);
            add(4);
            add(2);

            add(16);
            add(32);
            add(64);
            add(128);

            add(2);
            add(4);
            add(2);
            add(4);
        }
    };

    private List<Integer> gameWinIntegers = new ArrayList<Integer>() {
        {
            add(0);
            add(0);
            add(0);
            add(0);

            add(0);
            add(0);
            add(0);
            add(0);

            add(0);
            add(0);
            add(0);
            add(0);

            add(1024);
            add(1024);
            add(0);
            add(0);
        }
    };

    @Test
    public void boardDefaultConstructorTest() {
        Board board = new Board();
        int numberOfNonZeroFields = 0;
        for (Field i : board.getBoard()) {
            if (i.getValue() != 0) {
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
        } catch (GameOverException | GoalAchievedException e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        for (Pair<Integer, Field> item : zip(expectedValuesAfterMove, board.getBoard())) {
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
        } catch (GameOverException | GoalAchievedException e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        for (Pair<Integer, Field> item : zip(expectedValuesAfterMove, board.getBoard())) {
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
        } catch (GameOverException | GoalAchievedException e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        for (Pair<Integer, Field> item : zip(expectedValuesAfterMove, board.getBoard())) {
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
        } catch (GameOverException | GoalAchievedException e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        for (Pair<Integer, Field> item : zip(expectedValuesAfterMove, board.getBoard())) {
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
        } catch (GameOverException | GoalAchievedException e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        Assert.assertEquals(board.getScore(), 12);
    }

    @Test
    public void createNewBoardFieldAmountTest() {
        Board board = new Board();
        int newFieldsCounter = 0;
        for (Field f : board.getBoard()) {
            if (f.getValue() != 0) {
                newFieldsCounter++;
            }
        }
        Assert.assertEquals(newFieldsCounter, 2);
    }

    @Test
    public void createNewValueAfterMoveTest() {
        Board board = new Board();
        int fieldsCounter = 0;
        for (Field f : board.getCopyBoard()) {
            if (f.getValue() != 0) {
                fieldsCounter++;
            }
        }
        int counter = 0;
        while (counter < 10) {
            try {
                board.moveRight();
            } catch (GameOverException | GoalAchievedException ignored) {
            }
            int newFieldsCounter = 0;
            for (Field f : board.getCopyBoard()) {
                if (f.getValue() != 0) {
                    newFieldsCounter++;
                }
            }
            if (newFieldsCounter > fieldsCounter)
                break;
            counter++;
        }
        if (counter >= 10)
            fail();
    }

    @Test
    public void copyBoardTest() {
        Board board = new Board(this.integers);
        List<Field> copyBoard = board.getCopyBoard();
        for (Pair<Field, Field> item : zip(copyBoard, board.getBoard())) {
            Assert.assertEquals(item.getLeft(), item.getRight());
        }
        copyBoard.get(7).setValue(16);
        Assert.assertNotEquals(copyBoard.get(7), board.getCopyBoard().get(7));
    }


    @Test
    public void getScoreTest() {
        Board board = new Board(this.integers);
        Assert.assertEquals(0, board.getScore());
        try {
            board.moveRight();
        } catch (GameOverException | GoalAchievedException e) {
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
        } catch (GameOverException | GoalAchievedException e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }
        Assert.assertEquals(12, board.getScore());
        board.restartGame();
        int numberOfNonZeroFields = 0;
        for (Field i : board.getBoard()) {
            if (i.getValue() != 0) {
                numberOfNonZeroFields++;
            }
        }
        Assert.assertEquals(board.getScore(), 0);
        Assert.assertEquals(numberOfNonZeroFields, 2);
    }

    @Test
    public void getCopyBoardTest() {
        Board board = new Board(integers);
        assertEquals(integers.toString(), board.getBoard().toString());
    }

    @Test
    public void toStringTest() {
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
        assertEquals(board1, board2);

        assertEquals(board1, board1);

        try {
            board1.moveRight();
        } catch (GameOverException | GoalAchievedException ignore) {
        }
        assertNotEquals(board1, board2);

        assertNotEquals(board1, null);

        int a = 3;
        assertNotEquals(board1, a);
    }

    @Test(expected = GameOverException.class)
    public void gameOverTest() throws GameOverException {
        Board board1 = new Board(gameOverIntegers);
        try {
            board1.moveDown();
        } catch (GoalAchievedException e) {
            Assert.fail();
        }
    }

    @Test(expected = GoalAchievedException.class)
    public void gameWinTest() throws GoalAchievedException {
        Board board1 = new Board(gameWinIntegers);
        try {
            board1.moveRight();
        } catch (GameOverException e) {
            Assert.fail();
        }
    }

    @Test
    public void undoTest() {
        Board board1 = new Board(integers);
        Board board2 = new Board(integers);
        assertEquals(board1, board2);
        try {
            board1.moveRight();
        } catch (GameOverException | GoalAchievedException e) {
            e.printStackTrace();
        }
        assertNotEquals(board1, board2);
        board1.undoPreviousMove();
        assertEquals(board1, board2);
    }

    @Test
    public void hashCodeTest() {
        Board board1 = new Board(integers);
        Board board2 = new Board(integers);
        assertEquals(board1, board2);
        assertEquals(board1.hashCode(), board2.hashCode());
    }

    private List<Integer> moveUpAmountsMoved = new ArrayList<Integer>() {
        {
            add(0);
            add(0);
            add(0);
            add(0);

            add(1);
            add(0);
            add(1);
            add(0);

            add(2);
            add(2);
            add(1);
            add(2);

            add(0);
            add(0);
            add(1);
            add(0);
        }
    };

    @Test
    public void amountMovedTest() {
        Board board = new Board(integers);
        try {
            board.moveUp();
        } catch (GameOverException | GoalAchievedException ignore) {
            Assert.fail();
        }
        int counter = 0;
        for (Pair<Integer, Integer> item : zip(moveUpAmountsMoved, board.getAmountMovedListCopyAndWipeAmountMovedList())) {
            if (integers.get(counter) != 0) {
                Assert.assertEquals(item.getLeft(), item.getRight());
            }
            counter++;
        }
    }

}
