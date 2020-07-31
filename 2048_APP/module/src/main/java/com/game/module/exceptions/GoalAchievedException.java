package com.game.module.exceptions;

public class GoalAchievedException extends Exception {

    public GoalAchievedException(String s) {
        super(s);
    }

    public GoalAchievedException(String s, Exception e) {
        super(s, e);
    }

}
