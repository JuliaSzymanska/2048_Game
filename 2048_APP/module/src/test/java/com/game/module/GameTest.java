package com.game.module;

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
        int score = game.getCurrentScore();
        try {
            game.move(Game.MOVE_RIGHT);
            game.move(Game.MOVE_LEFT);
            game.move(Game.MOVE_UP);
            game.move(Game.MOVE_DOWN);
            assertTrue(score < game.getCurrentScore());
            score = game.getCurrentScore();
        } catch (GameOverException e) {
            Assert.fail();
        }
        game.pauseTimer();
        try {
            game.move(Game.MOVE_RIGHT);
            game.move(Game.MOVE_LEFT);
            game.move(Game.MOVE_UP);
            game.move(Game.MOVE_DOWN);
            assertEquals(score, game.getCurrentScore());
            score = game.getCurrentScore();
        } catch (GameOverException e) {
            Assert.fail();
        }
        game.unpauseTimer();
        try {
            game.move(5);
        } catch (GameOverException ignore) {

        }
    }

    @Test
    public void startNewGameTest() {
        Game game = new Game(false, null);
        int score = game.getCurrentScore();
        while (game.getCurrentScore() == 0) {
            try {
                game.move(Game.MOVE_RIGHT);
                game.move(Game.MOVE_LEFT);
                game.move(Game.MOVE_UP);
                game.move(Game.MOVE_DOWN);
            } catch (GameOverException e) {
                Assert.fail();
            }
        }
        assertTrue(score < game.getCurrentScore());
        score = game.getCurrentScore();
        game.startNewGame();
        assertNotEquals(score, game.getCurrentScore());
    }

    @Test
    public void pauseAndUnpauseTimer() throws InterruptedException {
        Game game = new Game(false, null);
        TimeUnit.MILLISECONDS.sleep(10);
        game.pauseTimer();
        long time_passed = game.getElapsedTime();
        TimeUnit.MILLISECONDS.sleep(10);
        Assert.assertEquals(time_passed, game.getElapsedTime());
        game.unpauseTimer();
        TimeUnit.MILLISECONDS.sleep(10);
        Assert.assertTrue(game.getElapsedTime() > time_passed);
    }

    @Test
    public void getElapsedTime() {
        Game game = new Game(false, null);
        Assert.assertTrue(game.getElapsedTime() > 0);
    }

    // TODO: 29.05.2020 update somehow 
    @Test
    public void getCurrentScore() {
        Game game = new Game(false, null);
        Assert.assertEquals(game.getCurrentScore(), 0);
        int score = game.getCurrentScore();
        while (game.getCurrentScore() == 0) {
            try {
                game.move(Game.MOVE_RIGHT);
                game.move(Game.MOVE_LEFT);
                game.move(Game.MOVE_UP);
                game.move(Game.MOVE_DOWN);
            } catch (GameOverException e) {
                Assert.fail();
            }
        }
        assertTrue(score < game.getCurrentScore());
    }

    // TODO: 29.05.2020 update somehow
    @Test
    public void getHighScore() {
        Game game = new Game(true, null);
        Assert.assertEquals(game.getHighScore(), 0);
        int score = game.getCurrentScore();
        while (game.getCurrentScore() == 0) {
            try {
                game.move(Game.MOVE_RIGHT);
                game.move(Game.MOVE_LEFT);
                game.move(Game.MOVE_UP);
                game.move(Game.MOVE_DOWN);
            } catch (GameOverException e) {
                Assert.fail();
            }
        }
        assertTrue(score < game.getCurrentScore());
        assertEquals(game.getHighScore(), game.getCurrentScore());
        score = game.getHighScore();
        game.restartGame();
        assertEquals(score, game.getHighScore());
    }
}
