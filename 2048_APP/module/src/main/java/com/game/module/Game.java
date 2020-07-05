package com.game.module;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.StopWatch;

import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO: 29.05.2020 DAO
//  https://www.youtube.com/watch?v=0cg09tlAAQ0
//  ^ wygląda legitnie

public class Game {
    // TODO: 29.05.2020 Myslę że gra powina być singletonem
    private static final Game INSTANCE = new Game();

    private Board gameBoard = new Board();
    private StopWatch watch = new StopWatch();
    private int highScore;

    public final static int MOVE_UP = 0;
    public final static int MOVE_RIGHT = 1;
    public final static int MOVE_DOWN = 2;
    public final static int MOVE_LEFT = 3;
    // TODO: 05.07.2020 bool do blokowania ruchu jak gra jest pauzowana
    private boolean isMovable = true;

    private Game() {
        // TODO: 29.05.2020 Condition jak dodamy DAO, czy istnieje zapisana gra
        //  jezeli istnieje - przypisujemy ja do planszy, jesli nie - tworzymy nową.
        startNewGame();
    }

    public static Game getInstance() {
        return INSTANCE;
    }

    public List<Field> getCopyOfTheBoard() {
        return gameBoard.getCopyBoard();
    }

    // TODO: 05.07.2020 przenislam to wszystko tutaj, bo wedlug mnie gra odpowiada za
    //  to ktory ruch ma byc wykonany, a board za wykonanie ruchu
    public void move(int direction) throws GameOverException {
        if (this.isMovable) {
            switch (direction) {
                case MOVE_UP:
                    gameBoard.moveUp();
                    break;
                case MOVE_RIGHT:
                    gameBoard.moveRight();
                    break;
                case MOVE_DOWN:
                    gameBoard.moveDown();
                    break;
                case MOVE_LEFT:
                    gameBoard.moveLeft();
                    break;
                default:
                    throw new IllegalArgumentException("value can only be equal to 0, 1, 2 or 3");
            }
           // TODO: 29.05.2020 zapisac gre po kazdym ruchu gdy juz mamy dao
            this.updateHighscore();
        }
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
        this.isMovable = true;
    }

    public void pauseTimer() {
        watch.suspend();
        this.isMovable = false;
    }

    public void unpauseTimer() {
        watch.resume();
        this.isMovable = true;
    }

    public long getElapsedTime() {
        return watch.getTime(TimeUnit.SECONDS);
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
