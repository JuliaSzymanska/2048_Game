package tech.szymanskazdrzalik.module;

import android.content.Context;

import androidx.annotation.Nullable;

import tech.szymanskazdrzalik.module.dao.Dao;
import tech.szymanskazdrzalik.module.dao.GameSaveDaoFactory;
import tech.szymanskazdrzalik.module.dao.SaveGame;
import tech.szymanskazdrzalik.module.exceptions.GameOverException;
import tech.szymanskazdrzalik.module.exceptions.GoalAchievedException;

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

    private int highScore = 0;
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
     * Class constructor specifying if user is authenticated ({@link Game#isUserAuthenticated}) and context from activity ({@link Game#context}).
     * Starts new game if user is not authenticated, otherwise calls {@link Game#loadGame()}.
     * Creates new thread ({@link Game#saveGameBackgroundThread}) for saving game in background while playing.
     * @param isUserAuthenticated whether the user is authenticated.
     * @param context context from activity.
     */
    public Game(boolean isUserAuthenticated, @Nullable Context context) {
        this.setUserAuthenticated(isUserAuthenticated);
        this.setContext(context);
        this.startNewGame();
        if (this.isUserAuthenticated) {
            try {
                this.loadGame();
            } catch (LoadException e) {
                e.printStackTrace();
                this.startNewGame();
            }
        }
        this.saveGameBackgroundThread = new Thread(this.saveGameBackgroundRunnable);
        this.saveGameBackgroundThread.start();
    }

    /**
     * Sets {@link Game#context}.
     * @param context passed in constructor.
     */
    public void setContext(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    /**
     * Calls {@link Board#getCopyBoard()} method.
     * @return copy of the board.
     */
    public List<Field> getCopyOfTheBoard() {
        return gameBoard.getCopyBoard();
    }

    /**
     * Calls {@link Board#getBoard()} method.
     * @return game's board.
     */
    public List<Field> getBoard() {
        return gameBoard.getBoard();
    }

    /**
     * Calls appropriate method from Board class to move board.
     * To move up: {@link Board#moveUp()}.
     * To move right: {@link Board#moveRight()}.
     * To move down: {@link Board#moveDown()}.
     * To move left: {@link Board#moveLeft()}.
     * Updates game's high score by calling {@link Game#updateHighScore()}
     * @param direction of the move.
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
                this.updateHighScore();
                if (this.isUserAuthenticated) {
                    Thread t = new Thread(this.saveGameOnceRunnable);
                    t.start();
                }
            }
        } catch (GameOverException e) {
            this.saveGameBackgroundThread.interrupt();
            throw new GameOverException(e.getLocalizedMessage(), e);
        } catch (GoalAchievedException e) {
            this.updateHighScore();
            throw new GoalAchievedException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Anonymous implementation of Runnable that saves game.
     * Called after moves in {@link Game#move(int)} method.
     */
    private Runnable saveGameOnceRunnable = new Runnable() {
        @Override
        public void run() {
            saveGame();
        }
    };

    /**
     * Anonymous implementation of Runnable that saves game every {@link Game#SAVE_GAME_DELAY_SECONDS} seconds.
     */
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
     * Calls {@link Board#undoPreviousMove()} method to undo previous move.
     */
    public void undoPreviousMove() {
        this.gameBoard.undoPreviousMove();
    }

    /**
     * Calls {@link Board#getAvailableUndoNumber()} method to get number of available undo.
     * @return number of available undo.
     */
    public int getAvailableUndoNumber() {
        return this.gameBoard.getAvailableUndoNumber();
    }

    /**
     * Saves game calling {@link tech.szymanskazdrzalik.module.dao.Dao#write(Object, Object, Object)}.
     * The game is saved in a file called {@link #GAME_SAVE_NAME}.
     * The game is only saved for authenticated users.
     */
    private void saveGame() {
        if (this.context != null && this.isUserAuthenticated) {
            try (Dao<Board, Integer, Long> daoBoard = GameSaveDaoFactory.getFileBoardDao(GAME_SAVE_NAME, context)) {
                daoBoard.write(this.gameBoard, this.highScore, System.nanoTime() - this.gameBeginTime);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Calls {@link Board#getAmountMovedListCopyAndWipeAmountMovedList()} method.
     * @return list with amount of moves.
     */
    public List<Integer> getAmountMovedList() {
        return this.gameBoard.getAmountMovedListCopyAndWipeAmountMovedList();
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
     * Loads game using {@link Dao#read()}.
     * @throws LoadException when loading encounters a problem.
     */
    public void loadGame() throws LoadException {
        if (this.context != null && this.isUserAuthenticated) {
            try (Dao<Board, Integer, Long> daoBoard = GameSaveDaoFactory.getFileBoardDao(GAME_SAVE_NAME, context)) {
                SaveGame saveGame = daoBoard.read();
                this.gameBoard = saveGame.getBoard();
                this.highScore = saveGame.getHighScore();
                this.gameBeginTime = System.nanoTime() - saveGame.getTime();
            } catch (SaveGame.SaveGameException e) {
                e.printStackTrace();
                throw new LoadException(e);
            } catch (Exception e) {
                // TODO: 16.08.2020 tutaj sie crashowalo to juz sie nie zcrashuje xdd
                //  Nie łączyć z poprzednim żeby było widać że jest osobno!
                e.printStackTrace();
                throw new LoadException(e);
            }
        }
    }

    /**
     * Check if current score is higher than current high score, if yes high score is updated to current score.
     * To get current score calls {@link Board#getScore()} method.
     */
    private void updateHighScore() {
        if (this.gameBoard.getScore() > this.highScore && this.isUserAuthenticated) {
            this.highScore = this.gameBoard.getScore();
        }
    }

    /**
     * Starts new game by calling {@link Board#restartGame()}. Time is reset.
     */
    public void startNewGame() {
        this.gameBoard.restartGame();
        this.gameBeginTime = System.nanoTime();
        this.unPauseTimer();
    }

    /**
     * Calls method to start new game ({@link Game#startNewGame()}) and saves current state if user is authenticated.
     */
    public void restartGame() {
        this.startNewGame();
        if (isUserAuthenticated) {
            this.saveGame();
        }
    }

    /**
     * Pause game's timer.
     * Stops game saving at the background.
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
     * Resumes saving the game.
     */
    public void unPauseTimer() {
        if (this.isSuspended) {
            this.isSuspended = false;
            this.gameBeginTime = System.nanoTime() - this.pausedTimeDuration;
            this.pausedTimeDuration = 0;
            if (this.saveGameBackgroundThread.isInterrupted()) {
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
     * Calls {@link Board#getScore()} method to get current score.
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
     * {@inheritDoc}
     * Take into consideration: Board class object, game's high score, game's begin time.
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
     * {@inheritDoc}
     * Take into consideration: Board class object, game's high score, game's begin time.
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
     * {@inheritDoc}
     * Take into consideration: Board class object, game's high score, game's begin time.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("gameBoard", gameBoard)
                .append("time elapsed", this.getElapsedTimeToString())
                .append("highScore", highScore)
                .toString();
    }

    /**
     * {@inheritDoc}
     * Stops saving game at the background.
     */
    @Override
    protected void finalize() throws Throwable {
        if (this.saveGameBackgroundThread != null && !this.saveGameBackgroundThread.isInterrupted()) {
            this.saveGameBackgroundThread.interrupt();
        }
        super.finalize();
    }
}
