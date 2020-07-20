package com.game.module.dao;

import android.content.Context;

import com.game.module.Board;

public class GameSaveDaoFactory implements AutoCloseable {

    private GameSaveDaoFactory() {
    }

    public static Dao<Board, Integer> getFileBoardDao(String fileName, Context context) {
        return new GameSaveDao(fileName, context);
    }

    @Override
    public void close() throws Exception {

    }
}
