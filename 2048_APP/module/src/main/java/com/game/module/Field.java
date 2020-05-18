package com.game.module;

import org.apache.commons.lang3.builder.ToStringStyle;

public class Field {

    private int value;

    public Field(int value) {

        if (value >= 0 && value % 2 == 0) {
            this.value = value;
        }
    }

    public Field() {
        value = 0;
    }

    public void setValue(int value) {
        // TODO: 17.05.2020 value moze byc tylko potega dwojki
        if (value >= 0 && value % 2 == 0) {
            this.value = value;
        }
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

}
