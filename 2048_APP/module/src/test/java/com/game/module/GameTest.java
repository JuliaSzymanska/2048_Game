package com.game.module;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class GameTest {

    @Before
    public void startNewGame() {
        new Game(false, null).startNewGame();
    }

    @Test
    public void move() {
        Game game = new Game(false, null);
        try {
            game.move(Game.MOVE_RIGHT);
        } catch (GameOverException e) {
            Assert.fail();
        }
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
    }

    // TODO: 29.05.2020 update somehow
    @Test
    public void getHighScore() {
        Game game = new Game(false, null);
        Assert.assertEquals(game.getHighScore(), 0);
    }
}
