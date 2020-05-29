package com.game.module;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BoardTest {

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
        board.moveRight();
        Assert.assertEquals(board.toString(), expectedValuesAfterMove.toString());
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
        board.moveLeft();
        Assert.assertEquals(board.toString(), expectedValuesAfterMove.toString());
    }

    @Test
    public void boardMoveUpTest() {
        List<Integer> expectedValuesAfterMove = new ArrayList<Integer>() {
            {
                add(4);
                add(0);
                add(0);
                add(0);

                add(4);
                add(0);
                add(0);
                add(0);

                add(4);
                add(4);
                add(8);
                add(0);

                add(4);
                add(0);
                add(0);
                add(0);
            }
        };
        Board board = new Board(this.integers);
        board.moveUp();
        Assert.assertEquals(board.toString(), expectedValuesAfterMove.toString());
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
        board.moveDown();
        Assert.assertEquals(board.toString(), expectedValuesAfterMove.toString());
    }
}
