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
import org.apache.commons.lang3.time.StopWatch;


import java.io.IOException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Game {
    private Board gameBoard = new Board();

    // TODO: 19.07.2020 to nie jest serializowalne i nie wiem co z tym zrobić
    //  również, jeżeli chcielibyśmy przypisac context do gry przed uruchomieniem gry
    //  to wtedy zacznie się liczyć czas bo powstanie instancja klasy.
    //  jeśli poczekamy na ekranie startowym to będzie późniejszy czas
    private int highScore;

    private boolean isUserAuthenticated = true;

    public final static int MOVE_UP = 0;
    public final static int MOVE_RIGHT = 1;
    public final static int MOVE_DOWN = 2;
    public final static int MOVE_LEFT = 3;

    private long gameBeginTime;
    private long pausedTimeDuration;
    private boolean isSuspended;

    private Context context;

    public Game(boolean isUserAuthenticated, @Nullable Context context) {
        this.setUserAuthenticated(isUserAuthenticated);
        this.setContext(context);
        if (this.isUserAuthenticated) {
            if (this.loadGame()) {
                return;
            }
        }
        this.startNewGame();
    }

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }


    public List<Field> getCopyOfTheBoard() {
        return gameBoard.getCopyBoard();
    }

    public void move(int direction) throws GameOverException {
        // to w takim razie to było zepsute już wcześniej bo ja tylko zmieniałem nazwy :v
        // a działało u mnie przez zepsuty proximity sensor ( :
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
                Thread t = new Thread() {
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
        if (this.context != null && this.isUserAuthenticated) {
            // TODO: 18.07.2020 string
            try(Dao<Board, Integer, Long> daoBoard = GameSaveDaoFactory.getFileBoardDao("GameSave", context)) {
                daoBoard.write(this.gameBoard, this.highScore, System.nanoTime() - this.gameBeginTime);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO: 19.07.2020 niby powinno być w osobnym thread, ale i tak trzeba boola zwrócić?
    //  Chyba że nie trzeba zwracać jakoś hmm
    public boolean loadGame() {
        if (this.context != null && this.isUserAuthenticated) {
            // TODO: 18.07.2020 string
            try(Dao<Board, Integer, Long> daoBoard = GameSaveDaoFactory.getFileBoardDao("GameSave", context)) {
                this.gameBoard = daoBoard.read().getLeft();
                this.highScore = daoBoard.read().getMiddle();
                this.gameBeginTime = System.nanoTime() - daoBoard.read().getRight();
                return true;
            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                // FIXME: 18.07.2020
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private void updateHighscore() {
        if (this.gameBoard.getScore() > this.highScore && this.isUserAuthenticated) {
            this.highScore = this.gameBoard.getScore();
        }
    }

    public void startNewGame() {
        this.gameBoard.restartGame();
        this.gameBeginTime = System.nanoTime();
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
}
