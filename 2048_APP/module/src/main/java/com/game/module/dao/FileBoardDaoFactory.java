package com.game.module.dao;

import android.content.Context;

import com.game.module.Board;

public class FileBoardDaoFactory implements AutoCloseable {

    private FileBoardDaoFactory() {
    }

    public static Dao<Board> getFileBoardDao(String fileName, Context context) {
        return new FileBoardDao(fileName, context);
    }

    @Override
    public void close() throws Exception {

    }
}
