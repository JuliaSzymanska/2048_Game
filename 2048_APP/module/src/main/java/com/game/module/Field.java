package com.game.module;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class Field implements Serializable, Comparable<Field> {

    private int value;

    Field(int value) {

        if (isPowerOfTwo(value)) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("Value has to be a power of 2");
        }
    }

    Field (Field field) {
        this.value = field.getValue();
    }

    Field() {
        value = 0;
    }

    void setValue(int value) {
        if (isPowerOfTwo(value)) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("Value has to be a power of 2");
        }
    }

    private boolean isPowerOfTwo(int x) {
        return (x & (x - 1)) == 0;
    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("value", value)
                .toString();
    }

    void setNextValue() {
        this.value = this.value * 2;
    }

    int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Field)) {
            return false;
        }
        Field field = (Field) o;
        return new EqualsBuilder().append(field.value, this.value).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.value).toHashCode();
    }

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

