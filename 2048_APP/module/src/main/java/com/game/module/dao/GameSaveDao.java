package com.game.module.dao;

import android.content.Context;
import android.util.Pair;

import com.game.module.Board;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


class GameSaveDao implements Dao<Board, Integer> {

    private String filename;
    private Context context;

    GameSaveDao(String filename, Context context) {
        this.filename = filename;
        this.context = context;
    }

    @Override
    public Pair<Board, Integer> read() throws IOException, ClassNotFoundException {
        try(FileInputStream fileInputStream = context.openFileInput(filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            MyPair<Board, Integer> boardIntegerPair = (MyPair<Board, Integer>) objectInputStream.readObject();
            return Pair.create(boardIntegerPair.first, boardIntegerPair.second);
        }
    }

    @Override
    public void write(Board board, Integer score) throws IOException {
        try(FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(MyPair.create(board, score));
        }
    }

    private class MyPair<F, S> extends android.util.Pair<F, S> implements Serializable {

        /**
         * Constructor for a Pair.
         *
         * @param first  the first object in the Pair
         * @param second the second object in the pair
         */
        public MyPair(F first, S second) {
            super(first, second);
        }
    }

    @Override
    public void close() {

    }
}
