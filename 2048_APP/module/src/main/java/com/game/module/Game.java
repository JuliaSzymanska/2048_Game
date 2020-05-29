package com.game.module;

import org.apache.commons.lang3.time.StopWatch;

public class Game {
    private Board gameBoard;
    private StopWatch watch = new StopWatch();

    public Game() {
        // TODO: 29.05.2020 Condition jak dodamy DAO, czy istnieje zapisana gra
        //  jezeli istnieje - przypisujemy ja do planszy, jesli nie - tworzymy nową.
        startNewGame();
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

}
