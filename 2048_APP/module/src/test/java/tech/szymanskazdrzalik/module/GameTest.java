package tech.szymanskazdrzalik.module;

import tech.szymanskazdrzalik.module.exceptions.GameOverException;
import tech.szymanskazdrzalik.module.exceptions.GoalAchievedException;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class GameTest {

    @Test
    public void gameConstructor() {
        Game game = new Game(false, null);
        assertFalse(game.isUserAuthenticated());
    }

    @Test(expected = IllegalArgumentException.class)
    public void move() {
        Game game = new Game(false, null);
        int score = game.getScore();
        game.pauseTimer();
        try {
            game.move(Game.MOVE_RIGHT);
            game.move(Game.MOVE_LEFT);
            game.move(Game.MOVE_UP);
            game.move(Game.MOVE_DOWN);
            assertEquals(score, game.getScore());
        } catch (GameOverException | GoalAchievedException e) {
            Assert.fail();
        }
        game.unPauseTimer();
        try {
            game.move(5);
        } catch (GameOverException | GoalAchievedException ignore) {

        }
    }

    @Test
    public void startNewGameTest() {
        Game game = new Game(false, null);
        int score = game.getScore();
        while (game.getScore() == 0) {
            try {
                game.move(Game.MOVE_RIGHT);
                game.move(Game.MOVE_LEFT);
                game.move(Game.MOVE_UP);
                game.move(Game.MOVE_DOWN);
            } catch (GameOverException | GoalAchievedException e) {
                Assert.fail();
            }
        }
        assertTrue(score < game.getScore());
        score = game.getScore();
        game.startNewGame();
        assertNotEquals(score, game.getScore());
    }

    @Test
    public void pauseAndUnpauseTimer() throws InterruptedException {
        Game game = new Game(false, null);
        TimeUnit.MILLISECONDS.sleep(10);
        game.pauseTimer();
        long time_passed = game.getElapsedTime();
        TimeUnit.MILLISECONDS.sleep(10);
        Assert.assertEquals(time_passed, game.getElapsedTime());
        game.unPauseTimer();
        TimeUnit.MILLISECONDS.sleep(10);
        Assert.assertTrue(game.getElapsedTime() > time_passed);
    }

    @Test
    public void getElapsedTime() {
        Game game = new Game(false, null);
        Assert.assertTrue(game.getElapsedTime() > 0);
    }

    @Test
    public void getCurrentScore() {
        Game game = new Game(false, null);
        Assert.assertEquals(game.getScore(), 0);
        int score = game.getScore();
        while (game.getScore() == 0) {
            try {
                game.move(Game.MOVE_RIGHT);
                game.move(Game.MOVE_LEFT);
                game.move(Game.MOVE_UP);
                game.move(Game.MOVE_DOWN);
            } catch (GameOverException | GoalAchievedException e) {
                Assert.fail();
            }
        }
        assertTrue(score < game.getScore());
    }

    @Test
    public void getHighScore() {
        Game game = new Game(true, null);
        Assert.assertEquals(game.getHighScore(), 0);
        int score = game.getScore();
        while (game.getScore() == 0) {
            try {
                game.move(Game.MOVE_RIGHT);
                game.move(Game.MOVE_LEFT);
                game.move(Game.MOVE_UP);
                game.move(Game.MOVE_DOWN);
            } catch (GameOverException | GoalAchievedException e) {
                Assert.fail();
            }
        }
        assertTrue(score < game.getScore());
        assertEquals(game.getHighScore(), game.getScore());
        score = game.getHighScore();
        game.restartGame();
        assertEquals(score, game.getHighScore());
    }

    @Test
    public void isSuspendedTest() throws InterruptedException {
        Game game = new Game(true, null);
        TimeUnit.MILLISECONDS.sleep(10);
        assertFalse(game.isSuspended());
        game.pauseTimer();
        assertTrue(game.isSuspended());
        TimeUnit.MILLISECONDS.sleep(10);
        game.unPauseTimer();
        assertFalse(game.isSuspended());
    }

    @Test
    public void getElapsedTimeTest() throws InterruptedException {
        Game game = new Game(false, null);
        TimeUnit.MILLISECONDS.sleep(10);
        game.pauseTimer();
        long time_passed = game.getElapsedTime();
        TimeUnit.MILLISECONDS.sleep(10);
        Assert.assertEquals(time_passed, game.getElapsedTime());
        game.unPauseTimer();
        TimeUnit.MILLISECONDS.sleep(10);
        Assert.assertTrue(game.getElapsedTime() > time_passed);
    }

    @Test
    public void getElapsedTimeToStringTest() throws InterruptedException {
        Game game = new Game(false, null);
        assertEquals("00:00:00", game.getElapsedTimeToString());
    }

    @Test
    public void isUserAuthenticatedTest(){
        Game game = new Game(false, null);
        assertFalse(game.isUserAuthenticated());
        game.setUserAuthenticated(true);
        assertTrue(game.isUserAuthenticated());
    }

    @Test
    public void equalsTest() {
        Game game1 = new Game(true, null);
        Game game2 = new Game(true, null);
        Assert.assertEquals(game1, game1);
        assertNotEquals(game1, game2);
        assertNotEquals(game1, null);
        int a = 3;
        assertNotEquals(game1, a);
    }

    @Test
    public void hashCodeTest(){
        Game game1 = new Game(false, null);
        Game game2 = new Game(false, null);
        if(game1.hashCode() != game2.hashCode()) {
            assertNotEquals(game1, game2);
        }
    }
}
