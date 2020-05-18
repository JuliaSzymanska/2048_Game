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
public class FieldTest {

    List<Integer> listOfPossibleFieldValues = new ArrayList<Integer>() {
        {
            add(0);
            add(2);
            add(4);
            add(8);
            add(16);
            add(32);
            add(64);
            add(128);
            add(256);
            add(512);
            add(1024);
            add(2048);
        }
    };

    // TODO: 18.05.2020 CHCE ODPALAC TESTY W GRADLU
    //  ale nie umiem ._.
    //  umiem za to przez android studio odpalic wszystkie na raz, ale chce w gradlu!
    @Test
    public void fieldCreationTest() {
        for (int i : this.listOfPossibleFieldValues) {
            Assertions.assertEquals(new Field(i).getValue(), i);
        }
        Assertions.assertNotEquals(new Field(-1), -1);
    }

    @Test
    public void fieldSetValueTest() {
        Field field = new Field(4);
        Assertions.assertEquals(field.getValue(), 4);
        field.setValue(3);
        Assertions.assertNotEquals(field, 3);
        Assertions.assertEquals(field.getValue(), 4);
    }

}