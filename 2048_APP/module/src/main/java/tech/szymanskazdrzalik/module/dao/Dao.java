package tech.szymanskazdrzalik.module.dao;


import java.io.IOException;

public interface Dao<T, Y, Z> extends AutoCloseable {
    SaveGame read() throws IOException, ClassNotFoundException, SaveGame.SaveGameException;
    void write(T t, Y y, Z z) throws IOException;
    /**
     * {@inheritDoc}
     */
    @Override
    void close();
}