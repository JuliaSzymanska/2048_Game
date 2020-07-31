package com.game.module;

import android.content.Context;

import androidx.annotation.Nullable;

import com.game.module.dao.Dao;
import com.game.module.dao.GameSaveDaoFactory;
import com.game.module.exceptions.GameOverException;
import com.game.module.exceptions.GoalAchievedException;

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
    private Context context;

    private int highScore;
    private boolean isUserAuthenticated = false;

    private long gameBeginTime;
    private long pausedTimeDuration;
    private boolean isSuspended;

    public final static int MOVE_UP = 0;
    public final static int MOVE_RIGHT = 1;
    public final static int MOVE_DOWN = 2;
    public final static int MOVE_LEFT = 3;

    private final static String GAME_SAVE_NAME = "GameSave";
    private final static int SAVE_GAME_DELAY_SECONDS = 5;

    private Thread saveGameBackgroundThread;

    /**
     * Class constructor specifying if user is authenticated and context from activity.
     * @param isUserAuthenticated whether the user is authenticated.
     * @param context context from activity.
     */
    public Game(boolean isUserAuthenticated, @Nullable Context context) {
        this.setUserAuthenticated(isUserAuthenticated);
        this.setContext(context);
        this.startNewGame();
        if (this.isUserAuthenticated) {
            // unikam konieczności używania boola
            try {
                this.loadGame();
            } catch (LoadException e) {
                e.printStackTrace();
                this.startNewGame();
            }
        }
        this.saveGameBackgroundThread = new Thread(this.saveGameBackgroundRunnable);
    }

    /**
     * Sets context.
     * @param context passed in constructor.
     */
    public void setContext(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    /**
     * @return copy of the board.
     */
    public List<Field> getCopyOfTheBoard() {
        return gameBoard.getCopyBoard();
    }

    /**
     * @return board.
     */
    public List<Field> getBoard() {
        return gameBoard.getBoard();
    }

    /**
     * Calls appropriate method from Board class to move board.
     * @param direction of the move
     * @throws GoalAchievedException when one or more fields have value equal or higher then 2048.
     * @throws GameOverException when the game ended.
     */
    public void move(int direction) throws GameOverException, GoalAchievedException {
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
                        throw new IllegalArgumentException("Value can only be equal to 0, 1, 2 or 3");
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
        } catch (GoalAchievedException e) {
            throw new GoalAchievedException("", e);
        }
    }

    /**
     * Saves game.
     * Called after moves.
     */
    private Runnable saveGameOnce = new Runnable() {
        @Override
        public void run() {
            saveGame();
        }
    };

    /**
     * Saves game every <i>SAVE_GAME_DELAY_SECONDS</i> seconds.
     */
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

    /**
     * Calls method from Board class to undo previous move.
     */
    public void undoPreviousMove() {
        this.gameBoard.undoPreviousMove();
    }

    /**
     * Calls method from Board class to get number of available undo.
     * @return number of available undo.
     */
    public int getAvaiableUndoNumber() {
        return this.gameBoard.getAvaiableUndoNumber();
    }

    /**
     * Saves game using DAO.
     */
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

    /**
     * @return list with amount of moves.
     */
    public List<Integer> getAmountMovedList() {
        return this.gameBoard.getAmountMovedList();
    }

    /**
     * Load exception class.
     */
    private static class LoadException extends Exception {
        LoadException(Exception e) {
            super(e);
        }
    }

    /**
     * Loads game using DAO.
     * @throws LoadException when loading encounters a problem.
     */
    public void loadGame() throws LoadException {
        if (this.context != null && this.isUserAuthenticated) {
            try (Dao<Board, Integer, Long> daoBoard = GameSaveDaoFactory.getFileBoardDao(GAME_SAVE_NAME, context)) {
                this.gameBoard = daoBoard.read().getLeft();
                this.highScore = daoBoard.read().getMiddle();
                this.gameBeginTime = System.nanoTime() - daoBoard.read().getRight();
            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                // FIXME: 18.07.2020
                e.printStackTrace();
                throw new LoadException(e);
            }
        }
    }

    /**
     * Check if current score is higher than current high score, if yes high score is updated to current score.
     */
    private void updateHighscore() {
        if (this.gameBoard.getScore() > this.highScore && this.isUserAuthenticated) {
            this.highScore = this.gameBoard.getScore();
        }
    }

    /**
     * Starts new game. Board and time are reset.
     */
    public void startNewGame() {
        this.gameBoard.restartGame();
        this.gameBeginTime = System.nanoTime();
        this.unpauseTimer();
    }

    /**
     * Calls method to start new game and saves current state if user is authenticated.
     */
    public void restartGame() {
        this.startNewGame();
        if (isUserAuthenticated) {
            this.saveGame();
        }
    }

    /**
     * Pause game's timer.
     */
    public void pauseTimer() {
        if (!this.isSuspended) {
            this.isSuspended = true;
            this.pausedTimeDuration = System.nanoTime() - this.gameBeginTime;
            this.saveGameBackgroundThread.interrupt();
        }
    }

    /**
     * Unpause game's timer.
     */
    public void unpauseTimer() {
        if (this.isSuspended) {
            this.isSuspended = false;
            this.gameBeginTime = System.nanoTime() - this.pausedTimeDuration;
            this.pausedTimeDuration = 0;
            if (this.saveGameBackgroundThread.isInterrupted()) {
                // TODO: 23.07.2020 nie jestem pewien czy to takie dobre jest 
                this.saveGameBackgroundThread = new Thread(this.saveGameBackgroundRunnable);
            }
        }
    }

    /**
     * @return if game is suspended.
     */
    public boolean isSuspended() {
        return this.isSuspended;
    }

    /**
     * @return current game time in nanoseconds.
     */
    public long getElapsedTime() {
        if (this.isSuspended) {
            return this.pausedTimeDuration;
        }
        return System.nanoTime() - this.gameBeginTime;
    }

    /**
     * @return time as String in format: HH:MM:SS.
     */
    public String getElapsedTimeToString() {
        long timeSeconds = TimeUnit.MILLISECONDS.convert(this.getElapsedTime(), TimeUnit.NANOSECONDS);
        String time = DurationFormatUtils.formatDurationHMS(timeSeconds);
        return time.substring(0, time.length() - 4);
    }


    /**
     * Calls Board class method to get current score.
     * @return board's score.
     */
    public int getScore() {
        return this.gameBoard.getScore();
    }

    /**
     * @return game's high score.
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * @return if user is authenticated.
     */
    public boolean isUserAuthenticated() {
        return isUserAuthenticated;
    }

    /**
     * Sets if user is authenticated.
     * @param userAuthenticated if user is authenticated.
     */
    public void setUserAuthenticated(boolean userAuthenticated) {
        this.isUserAuthenticated = userAuthenticated;
    }

    /**
     * @param o the object to check for equality.
     * @return true if <i>this</i> is numerically equal to param.
     */
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

    /**
     * @return hash code for game.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(gameBoard)
                .append(highScore)
                .append(gameBeginTime)
                .toHashCode();
    }

    /**
     * @return String representation of class.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("gameBoard", gameBoard)
                .append("time elapsed", this.getElapsedTimeToString())
                .append("highScore", highScore)
                .toString();
    }

    // TODO: 28.07.2020 javadoc
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
