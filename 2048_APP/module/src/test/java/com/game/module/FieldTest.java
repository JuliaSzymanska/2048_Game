package com.game.module;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FieldTest {

    private List<Integer> listOfPossibleFieldValues = new ArrayList<Integer>() {
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
    @Test(expected = IllegalArgumentException.class)
    public void fieldCreationTest() {
        for (int i : this.listOfPossibleFieldValues) {
            assertEquals(new Field(i).getValue(), i);
        }
        assertNotEquals(new Field(-1), -1);
    }

    @Test
    public void fieldSetValueTest() {
        Field field = new Field(4);
        assertEquals(4, field.getValue());

        try {
            field.setValue(3);
        } catch (IllegalArgumentException ignore){}

        assertNotEquals(3, field.getValue());
        assertEquals(4, field.getValue());

        try {
            field.setValue(12);
        } catch (IllegalArgumentException ignore) {}

        assertNotEquals(12, field.getValue());
        assertEquals(4, field.getValue());
    }

    @Test
    public void toStringTest() {
        assertEquals(new Field(4).toString(), "4");
    }

}