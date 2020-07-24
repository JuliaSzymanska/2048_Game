package com.game.module;

public class GoalAchievedException extends Exception {

    public GoalAchievedException(String s) {
        super(s);
    }

    public GoalAchievedException(String s, Exception e) {
        super(s, e);
    }

}
