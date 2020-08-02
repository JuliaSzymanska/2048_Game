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
     * Sets field's value to 0.
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
     * {@inheritDoc}
     * Take into consideration: field's value.
     */
    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("value", value)
                .toString();
    }

    /**
     * {@inheritDoc}
     * Take into consideration: field's value.
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
     * {@inheritDoc}
     * Take into consideration: field's value.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,  37)
                .append(this.value)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     * Take into consideration: field's value.
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

