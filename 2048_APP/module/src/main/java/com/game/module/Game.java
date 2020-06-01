package com.game.module;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.StopWatch;

// TODO: 29.05.2020 DAO
//  https://www.youtube.com/watch?v=0cg09tlAAQ0
//  ^ wygląda legitnie

public class Game {
    // TODO: 29.05.2020 Myslę że gra powina być singletonem
    private static final Game INSTANCE = new Game();

    private Board gameBoard = new Board();
    private StopWatch watch = new StopWatch();
    private int highScore;

    // TODO: 29.05.2020 nie podoba mi sie -- mi tez nie
    final static int MOVE_UP = Board.MOVE_UP;
    final static int MOVE_RIGHT = Board.MOVE_RIGHT;
    final static int MOVE_DOWN = Board.MOVE_DOWN;
    final static int MOVE_LEFT = Board.MOVE_LEFT;

    private Game() {
        // TODO: 29.05.2020 Condition jak dodamy DAO, czy istnieje zapisana gra
        //  jezeli istnieje - przypisujemy ja do planszy, jesli nie - tworzymy nową.
        startNewGame();
    }

    public static Game getInstance() {
        return INSTANCE;
    }

    public void move(int direction) throws GameOverException {
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

    public void unpauseTimer() {
        watch.resume();
    }

    // TODO: 30.05.2020 przekonwertowac to na jakies madre wartosci
    public long getElapsedTime() {
        return watch.getNanoTime();
    }

    public int getCurrentScore() {
        return this.gameBoard.getScore();
    }


    public int getHighScore() {
        return highScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        return new EqualsBuilder()
                .append(highScore, game.highScore)
                .append(gameBoard, game.gameBoard)
                .append(watch, game.watch)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(gameBoard)
                .append(watch)
                .append(highScore)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("gameBoard", gameBoard)
                .append("watch", watch)
                .append("highScore", highScore)
                .toString();
    }
}
