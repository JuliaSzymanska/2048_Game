package com.game.module;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BoardTest {

    List<Integer> integers = new ArrayList<Integer>(){
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

    // TODO: 18.05.2020 no usunac te printy brzydkie 
    @Test
    public void boardMoveRightTest() {
        Board board = new Board(integers);
        System.out.println(board);
        board.moveRight();
        System.out.println(board);

    }
}
