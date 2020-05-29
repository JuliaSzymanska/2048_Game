package com.game.module;

import org.apache.commons.lang3.time.StopWatch;

// TODO: 29.05.2020 DAO
//  https://www.youtube.com/watch?v=0cg09tlAAQ0
//  ^ wygląda legitnie

public class Game {
    private Board gameBoard;
    private StopWatch watch = new StopWatch();
    private int highScore;

    // TODO: 29.05.2020 nie podoba mi sie
    final static int MOVE_UP = Board.MOVE_UP;
    final static int MOVE_RIGHT = Board.MOVE_RIGHT;
    final static int MOVE_DOWN = Board.MOVE_DOWN;
    final static int MOVE_LEFT = Board.MOVE_LEFT;

    public Game() {
        // TODO: 29.05.2020 Condition jak dodamy DAO, czy istnieje zapisana gra
        //  jezeli istnieje - przypisujemy ja do planszy, jesli nie - tworzymy nową.
        startNewGame();
    }

    public void move(int direction) {
        this.gameBoard.move(direction);
        // TODO: 29.05.2020 zapisac gre po kazdym ruchu gdy juz mamy dao
        this.updateHighscore();
    }

    private void updateHighscore() {
        if (this.gameBoard.getScore() > this.highScore) {
            this.highScore = this.gameBoard.getScore();
        }
    }

    public void startNewGame() {
        this.gameBoard.restartGame();
        this.watch.reset();
        this.watch.start();
    }

    public void pauseTimer() {
        watch.suspend();
    }

    public void unpauseTime() {
        watch.resume();
    }

    public long getElapsedTime() {
        return watch.getNanoTime();
    }

    public int getCurrentScore() {
        return this.gameBoard.getScore();
    }

}
