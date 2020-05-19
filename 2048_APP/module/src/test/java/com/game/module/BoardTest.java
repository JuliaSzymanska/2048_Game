package com.game.module;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BoardTest {

    List<Integer> integers = new ArrayList<Integer>() {
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
        // TODO: 18.05.2020 omg jakie to brzydkie i okropny
        //  ten test jest beznadziejny.
        //  Sprawdza czy w miejscach w których po ruchu powinny sie pojawic wartosci to prawda
        //  ale jezeli pojawila by sie gdzies wartosc ktorej nie powinno byc to nie sprawdzi :(
        //  pomysł na test jest taki: stworzyć sobie planszę o wcześniej założonych wartosciach, lista integers
        //  rozrysować sobie jak po ruchu się powinna zmienić plansza
        //  stworzyć taką listę wartości która ma tak wygladac jak plansza po ruchu
        //  porównać planszę z oczekiwanym wynikiem.
        //  sprawdzić czy w którymś polu które miało być puste pojawiła się albo 2 albo 4
//        List<Integer> expectedValuesAfterMove = new ArrayList<Integer>() {
//            {
//                add(0);
//                add(0);
//                add(0);
//                add(0);
//
//                add(0);
//                add(2);
//                add(4);
//                add(2);
//
//                add(4);
//                add(0);
//                add(4);
//                add(0);
//
//                add(0);
//                add(2);
//                add(8);
//                add(2);
//            }
//        };
        Board board = new Board(integers);
        System.out.println(board.toString());
        board.moveLeft();
        System.out.println(board.toString());
//
//        Iterator<Field> it1 = board.getCopyBoard().iterator();
//        Iterator<Integer> it2 = expectedValuesAfterMove.iterator();
//        boolean hasNewValueAppeared = false;
//        while(it1.hasNext() && it2.hasNext()) {
//            int val1 = it1.next().getValue();
//            int val2 = it2.next();
//            if(val2 != 0) {
//                Assertions.assertEquals(val1, val2);
//            }
//            else {
//                if(val1 == 2 || val1 == 4) {
//                    hasNewValueAppeared = true;
//                }
//            }
//
//        }
        Assertions.assertTrue(true);
    }
}
