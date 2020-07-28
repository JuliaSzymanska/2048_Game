package com.game.module;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class Field implements Serializable, Comparable<Field> {

    private int value;

    /**
     * Class constructor specifying field's value.
     * @param value - field's value.
     */
    public Field(int value) {

        if (isPowerOfTwo(value)) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("Value has to be a power of 2");
        }
    }

    /**
     * Copy cosntructor.
     * @param field field to copy value of.
     */
    Field (Field field) {
        this.value = field.getValue();
    }

    /**
     * Default class constructor.
     */
    Field() {
        value = 0;
    }

    /**
     * Doubles the field's value.
     */
    void setNextValue() {
        this.value = this.value * 2;
    }

    /**
     * @return field's value.
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets field's value to param.
     * @param value new field's value.
     */
    void setValue(int value) {
        if (isPowerOfTwo(value)) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("Value has to be a power of 2");
        }
    }

    /**
     * Checks if param is power of two.
     * @param x value to check.
     * @return if param is power of two.
     */
    private boolean isPowerOfTwo(int x) {
        return (x & (x - 1)) == 0;
    }

    /**
     * @return String representation of class.
     */
    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("value", value)
                .toString();
    }

    /**
     * @param o the object to check for equality.
     * @return true if <i>this</i> is numerically equal to param.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;
        return new EqualsBuilder()
                .append(field.value, this.value)
                .isEquals();
    }

    /**
     * @return hash code for board.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,  37)
                .append(this.value)
                .toHashCode();
    }

    /**
     * @param o the object to check for compare.
     * @return 1, 0 or -1 depending on which of the compared objects is larger
     */
    @Override
    public int compareTo(Field o) {
        if (o == null) {
            throw new NullPointerException("comparing to null");
        }
        if (this.equals(o)) {
            return 0;
        } else if (this.value < o.value) {
            return -1;
        } else {
            return 1;
        }
    }
}

