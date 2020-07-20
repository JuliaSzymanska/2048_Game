package com.game.module.dao;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Dao<T> extends AutoCloseable {
    T read() throws IOException, ClassNotFoundException;
    void write(T t) throws IOException;
    @Override
    void close();
}