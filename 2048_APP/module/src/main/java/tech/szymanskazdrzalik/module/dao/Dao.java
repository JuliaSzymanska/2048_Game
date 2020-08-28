package tech.szymanskazdrzalik.module.dao;


import java.io.IOException;

public interface Dao<T> extends AutoCloseable {
    SaveGame read() throws IOException, ClassNotFoundException, SaveGame.SaveGameException;

    void write(T t) throws IOException;

    /**
     * {@inheritDoc}
     */
    @Override
    void close();
}