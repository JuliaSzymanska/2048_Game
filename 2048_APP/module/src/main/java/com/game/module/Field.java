package com.game.module;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringStyle;

public class Field {

    private int value;

    public Field(int value) {

        if (isPowerOfTwo(value)) {
            this.value = value;
        } else{
            throw new IllegalArgumentException("Value has to be a power of 2");
        }
    }

    public Field() {
        value = 0;
    }

    public void setValue(int value) {
        if (isPowerOfTwo(value)) {
            this.value = value;
        } else{
            throw new IllegalArgumentException("Value has to be a power of 2");
        }
    }

    public boolean isPowerOfTwo(int x)
    {
        return (x & (x - 1)) == 0;
    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("value", value)
                .toString();
    }

    public void setNextValue() {
        this.value = this.value * 2;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Field)) {
            return false;
        }

        Field c = (Field) obj;
        return this.value == c.value;
    }

}
