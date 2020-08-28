package tech.szymanskazdrzalik.module.dao;

import android.content.Context;

public class GameSaveDaoFactory implements AutoCloseable {

    private GameSaveDaoFactory() {
    }

    public static Dao<SaveGame> getFileBoardDao(String fileName, Context context) {
        return new GameSaveDao(fileName, context);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void close() {

    }
}
