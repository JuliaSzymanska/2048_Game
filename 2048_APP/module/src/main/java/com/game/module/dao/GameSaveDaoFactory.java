package com.game.module.dao;

import android.content.Context;

import com.game.module.Board;

public class GameSaveDaoFactory implements AutoCloseable {

    private GameSaveDaoFactory() {
    }

    public static Dao<Board, Integer, Long> getFileBoardDao(String fileName, Context context) {
        return new GameSaveDao(fileName, context);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void close() {

    }
}
