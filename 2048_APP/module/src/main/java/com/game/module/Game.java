package com.game.module;

import android.content.Context;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO: 29.05.2020 DAO
//  https://www.youtube.com/watch?v=0cg09tlAAQ0
//  ^ wygląda legitnie

public class Game {
    // TODO: 19.07.2020 mądra jest ta podpowiedź, ale nie bardzo wiem co z tym zrobić
    private static final Game INSTANCE = new Game();

    private Board gameBoard = new Board();

    // TODO: 19.07.2020 to nie jest serializowalne i nie wiem co z tym zrobić
    //  również, jeżeli chcielibyśmy przypisac context do gry przed uruchomieniem gry
    //  to wtedy zacznie się liczyć czas bo powstanie instancja klasy.
    //  jeśli poczekamy na ekranie startowym to będzie późniejszy czas
    private StopWatch watch = new StopWatch();
    private int highScore;

    private boolean isUserAuthenticated = false;

    public final static int MOVE_UP = 0;
    public final static int MOVE_RIGHT = 1;
    public final static int MOVE_DOWN = 2;
    public final static int MOVE_LEFT = 3;

    private Context context;

    private Game() {
        // TODO: 19.07.2020 mam problem z tym żeby to zrobić żeby to dzialalo
        //  zeby zaladować grę to potrzebuję context
        //  w momencie kiedy jestem w stanie przekazać context to już constructor jest wykonany ehh
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

    public static Game getInstance() {
        return INSTANCE;
    }

    public List<Field> getCopyOfTheBoard() {
        return gameBoard.getCopyBoard();
    }

    public void move(int direction) throws GameOverException {
        // to w takim razie to było zepsute już wcześniej bo ja tylko zmieniałem nazwy :v
        // a działało u mnie przez zepsuty proximity sensor ( :
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
            try(FileOutputStream fileOutputStream = context.openFileOutput("GameSave", Context.MODE_PRIVATE);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                List<Object> list = new ArrayList<>();
                list.add(this.gameBoard);
                list.add(Integer.valueOf(this.getHighScore()));
                // TODO: 19.07.2020 stopwatch jest unseralizable ._.
//                list.add(this.watch);
                objectOutputStream.writeObject(list);

            } catch (IOException e) {
                e.printStackTrace();
                // FIXME: 18.07.2020
            }
        }
    }

    // TODO: 19.07.2020 niby powinno być w osobnym thread, ale i tak trzeba boola zwrócić?
    //  Chyba że nie trzeba zwracać jakoś hmm
    public boolean loadGame() {
        if (this.context != null && this.isUserAuthenticated) {
            // TODO: 18.07.2020 string
            try(FileInputStream fileInputStream = context.openFileInput("GameSave");
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                List<Object> list = new ArrayList<>();
                list = (ArrayList<Object>)objectInputStream.readObject();
                this.gameBoard = (Board) list.get(0);
                this.highScore = (int) list.get(1);
//                this.watch = (StopWatch) list.get(2);
                return true;
            } catch (IOException | ClassNotFoundException | IndexOutOfBoundsException e){
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
        this.watch.reset();
        this.watch.start();
    }

    public void restartGame() {
        this.startNewGame();
        if (isUserAuthenticated) {
            this.saveGame();
        }
    }

    public void pauseTimer() {
        if (!watch.isSuspended()) {
            watch.suspend();
        }
    }

    public void unpauseTimer() {
        if (watch.isSuspended()) {
            watch.resume();
        }
    }

    public boolean isSuspended() {
        return watch.isSuspended();
    }

    public long getElapsedTime() {
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
