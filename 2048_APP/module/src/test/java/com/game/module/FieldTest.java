package com.game.module;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

// TODO: 29.05.2020  JACOCO nie działa :(
//  https://android.jlelse.eu/get-beautiful-coverage-reports-in-your-android-projects-ce9ba281507f
//  spróbuj może to w wolnej chwili


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

    @Test(expected = IllegalArgumentException.class)
    public void fieldCreationValueTest() {
        for (int i : this.listOfPossibleFieldValues) {
            assertEquals(new Field(i).getValue(), i);
        }
        assertNotEquals(new Field(-1), -1);
    }

    @Test
    public void FieldCopyConstructorTest() {
        Field field = new Field(4);
        assertEquals(new Field(field).getValue(), field.getValue());
    }

    @Test
    public void FieldDefaultConstructorTest() {
        assertEquals(new Field(0).getValue(), new Field().getValue());
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
    public void setNextValueTest() {
        Field field = new Field(2);
        assertEquals(2, field.getValue());
        field.setNextValue();
        assertEquals(4, field.getValue());
    }

    @Test
    public void toStringTest() {
        assertEquals(new Field(4).toString(), "4");
    }

    @Test
    public void equalsTest() {
        Field fieldFirst = new Field(2);
        Field fieldSecond = new Field(2);
        assertTrue(fieldFirst.equals(fieldSecond));

        assertTrue(fieldFirst.equals(fieldFirst));

        fieldFirst.setNextValue();
        assertFalse(fieldFirst.equals(fieldSecond));

        Field fieldThird = null;
        assertFalse(fieldFirst.equals(fieldThird));

        int a = 3;
        assertFalse(fieldFirst.equals(a));
    }

    @Test
    public void compareToTest() {
//        Field fieldFirst = new Field(2);
//        Field fieldSecond = new Field(2);
//        assertTrue(fieldFirst.equals(fieldSecond));
//
//        assertTrue(fieldFirst.equals(fieldFirst));
//
//        fieldFirst.setNextValue();
//        assertFalse(fieldFirst.equals(fieldSecond));
//
//        Field fieldThird = null;
//        assertFalse(fieldFirst.equals(fieldThird));
//
//        int a = 3;
//        assertFalse(fieldFirst.equals(a));
    }

}