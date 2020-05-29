package com.game.module;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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

}
