package com.game.module;

import android.content.Context;

import androidx.annotation.Nullable;

import com.game.module.dao.Dao;
import com.game.module.dao.GameSaveDaoFactory;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DurationFormatUtils;


import java.io.IOException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Game {

    private Board gameBoard = new Board();

    private int highScore;

    private boolean isUserAuthenticated = false;

    private long gameBeginTime;
    private long pausedTimeDuration;
    private boolean isSuspended;
    private Context context;

    public final static int MOVE_UP = 0;
    public final static int MOVE_RIGHT = 1;
    public final static int MOVE_DOWN = 2;
    public final static int MOVE_LEFT = 3;

    private final static String GAME_SAVE_NAME = "GameSave";

    private final static int SAVE_GAME_DELAY_SECONDS = 2;

    private final Thread saveGameBackgroundThread;

    public Game(boolean isUserAuthenticated, @Nullable Context context) {
        this.setUserAuthenticated(isUserAuthenticated);
        this.setContext(context);
        this.startNewGame();
        if (this.isUserAuthenticated) {
            // unikam konieczności używania boola
            this.loadGame();
        }
        saveGameBackgroundThread = new Thread(this.saveGameBackgroundRunnable);
    }

    public void setContext(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }


    public List<Field> getCopyOfTheBoard() {
        return gameBoard.getCopyBoard();
    }

    public void move(int direction) throws GameOverException {
        try {
            if (!this.isSuspended) {
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
                if (this.isUserAuthenticated) {
                    Thread t = new Thread(this.saveGameOnce);
                    t.start();
                }
            }
        } catch (GameOverException e) {
            this.saveGameBackgroundThread.interrupt();
            // TODO: 21.07.2020 String
            throw new GameOverException("", e);
        }
    }

    private Runnable saveGameOnce = new Runnable() {
        @Override
        public void run() {
            saveGame();
        }
    };

    // TODO: 21.07.2020 TEST ME! 
    private Runnable saveGameBackgroundRunnable = new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(SAVE_GAME_DELAY_SECONDS * 1000);
                    saveGame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    };


    private void saveGame() {
        if (this.context != null && this.isUserAuthenticated) {
            try (Dao<Board, Integer, Long> daoBoard = GameSaveDaoFactory.getFileBoardDao(GAME_SAVE_NAME, context)) {
                daoBoard.write(this.gameBoard, this.highScore, System.nanoTime() - this.gameBeginTime);
            } catch (IOException e) {
                // FIXME: 20.07.2020
                e.printStackTrace();
            }
        }
    }

    public void loadGame() {
        if (this.context != null && this.isUserAuthenticated) {
            try (Dao<Board, Integer, Long> daoBoard = GameSaveDaoFactory.getFileBoardDao(GAME_SAVE_NAME, context)) {
                this.gameBoard = daoBoard.read().getLeft();
                this.highScore = daoBoard.read().getMiddle();
                this.gameBeginTime = System.nanoTime() - daoBoard.read().getRight();
            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                // FIXME: 18.07.2020
                e.printStackTrace();
            }
        }
    }

    private void updateHighscore() {
        if (this.gameBoard.getScore() > this.highScore && this.isUserAuthenticated) {
            this.highScore = this.gameBoard.getScore();
        }
    }

    public void startNewGame() {
        this.gameBoard.restartGame();
        this.gameBeginTime = System.nanoTime();
        this.unpauseTimer();
    }

    public void restartGame() {
        this.startNewGame();
        if (isUserAuthenticated) {
            this.saveGame();
        }
    }

    public void pauseTimer() {
        if (!this.isSuspended) {
            this.isSuspended = true;
            this.pausedTimeDuration = System.nanoTime() - this.gameBeginTime;
        }
    }

    public void unpauseTimer() {
        if (this.isSuspended) {
            this.isSuspended = false;
            this.gameBeginTime = System.nanoTime() - this.pausedTimeDuration;
            this.pausedTimeDuration = 0;
        }
    }

    public boolean isSuspended() {
        return this.isSuspended;
    }

    public long getElapsedTime() {
        if (this.isSuspended) {
            return this.pausedTimeDuration;
        }
        return System.nanoTime() - this.gameBeginTime;
    }

    public String getElapsedTimeToString() {
        long timeSeconds = TimeUnit.MILLISECONDS.convert(this.getElapsedTime(), TimeUnit.NANOSECONDS);
        String time = DurationFormatUtils.formatDurationHMS(timeSeconds);
        return time.substring(0, time.length() - 4);
    }


    // TODO: 23.07.2020 usunac duplikaty
    public int getCurrentScore() {
        return this.gameBoard.getScore();
    }

    public int getScore() {
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
                .append(gameBeginTime, game.gameBeginTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(gameBoard)
                .append(highScore)
                .append(gameBeginTime)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("gameBoard", gameBoard)
                .append("time elapsed", this.getElapsedTimeToString())
                .append("highScore", highScore)
                .toString();
    }

    // W razie gdyby thread się nie przerwał, awaryjny
    @Override
    protected void finalize() throws Throwable {
        // TODO: 21.07.2020 nie jestem pewien czy to np może być tutaj null i się wywalić?
        //  albo może jak juz jest przerwany to nie można drugi raz przerwać?
        //  sprawdzić i ewentualnie usunąc ify
        if (this.saveGameBackgroundThread != null && !this.saveGameBackgroundThread.isInterrupted()) {
            this.saveGameBackgroundThread.interrupt();
        }
        super.finalize();
    }
}
