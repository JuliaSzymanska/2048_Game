package com.game.module;

public class GameOverException extends Exception {

    public GameOverException(String s) {
        super(s);
    }

    public GameOverException(String s, Exception e) {
        super(s, e);
    }

}
