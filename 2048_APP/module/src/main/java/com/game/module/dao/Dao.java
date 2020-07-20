package com.game.module.dao;

import android.util.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Dao<T, Y> extends AutoCloseable {
    Pair<T, Y> read() throws IOException, ClassNotFoundException;
    void write(T t, Y y) throws IOException;
    @Override
    void close();
}