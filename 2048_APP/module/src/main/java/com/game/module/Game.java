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
    private static final Game INSTANCE = new Game();

    private Board gameBoard = new Board();
    private StopWatch watch = new StopWatch();
    private int highScore;

    private boolean isUserAuthenticated = false;

    public final static int MOVE_UP = 0;
    public final static int MOVE_RIGHT = 1;
    public final static int MOVE_DOWN = 2;
    public final static int MOVE_LEFT = 3;

    private Game() {
        if(this.isUserAuthenticated) {
            if(this.loadGame()){
                return;
            }
        }
        this.startNewGame();
    }

    public static Game getInstance() {
        return INSTANCE;
    }

    public List<Field> getCopyOfTheBoard() {
        return gameBoard.getCopyBoard();
    }

    public void move(int direction) throws GameOverException {
        if (!this.watch.isSuspended()) {
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
            this.updateHighscore();
            if(this.isUserAuthenticated) {
                Thread t = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        saveGame();
                    }
                };
                t.start();
            }
        }
    }

    private void saveGame() {
        // FIXME: 11.07.2020
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

    public void restartGame() {
        this.startNewGame();
        if(isUserAuthenticated) {
            this.saveGame();
        }
    }

    private boolean loadGame() {
        // FIXME: 11.07.2020
        return false;
    }

    public void pauseTimer() {
        watch.suspend();
    }

    public void unpauseTimer() {
        watch.resume();
    }

    public boolean isSuspended(){
        return watch.isSuspended();
    }

    public long getElapsedTime() {
        System.out.println("Czas elapsed: " + watch.getNanoTime());
        System.out.println("Is suspended: " + watch.isSuspended());
        return watch.getNanoTime();
    }

    public long getElapsedTimeSeconds() {
        return watch.getTime(TimeUnit.SECONDS);
    }

    public int getCurrentScore() {
        return this.gameBoard.getScore();
    }

    public int getHighScore() {
        return highScore;
    }

    public boolean isUserAuthenticated() {
        return isUserAuthenticated;
    }

    public void setUserAuthenticated(boolean userAuthenticated) {
        this.isUserAuthenticated = userAuthenticated;
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
