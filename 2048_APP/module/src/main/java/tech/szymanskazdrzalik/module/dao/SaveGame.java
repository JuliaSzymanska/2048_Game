package tech.szymanskazdrzalik.module.dao;

import java.io.Serializable;

import tech.szymanskazdrzalik.module.Board;

public class SaveGame implements Serializable {
    SaveGame(Board board, Integer highScore, Long time) {
        this.board = board;
        this.highScore = highScore;
        this.time = time;
    }

    private Board board;
    private Integer highScore;
    private Long time;

    public Board getBoard() {
        return board;
    }

    public Integer getHighScore() {
        return highScore;
    }

    public Long getTime() {
        return time;
    }

    public static class SaveGameException extends Exception {
        public SaveGameException(Exception e) {
            super(e);
        }

        public SaveGameException(String s) {
            super(s);
        }

        public SaveGameException(String s, Exception e) {
            super(s, e);
        }
    }
}

