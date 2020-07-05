package com.game.module;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class GameTest {

    @Before
    public void startNewGame() {
        Game.getInstance().startNewGame();
    }

    @Test
    public void move() {
        Game game = Game.getInstance();
        try {
            game.move(Game.MOVE_RIGHT);
        } catch (GameOverException e) {
            Assert.fail();
        }
    }

    @Test
    public void pauseAndUnpauseTimer() throws InterruptedException {
        Game game = Game.getInstance();
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
        Game game = Game.getInstance();
        Assert.assertTrue(game.getElapsedTime() > 0);
    }

    // TODO: 29.05.2020 update somehow 
    @Test
    public void getCurrentScore() {
        Game game = Game.getInstance();
        Assert.assertEquals(game.getCurrentScore(), 0);
    }

    // TODO: 29.05.2020 update somehow
    @Test
    public void getHighScore() {
        Game game = Game.getInstance();
        Assert.assertEquals(game.getHighScore(), 0);
    }
}
