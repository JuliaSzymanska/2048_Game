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
import java.util.ArrayList;
import java.util.List;


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
            try {
                List boardIntegerPair = (ArrayList) objectInputStream.readObject();
                return Pair.create((Board)boardIntegerPair.get(0),(Integer) boardIntegerPair.get(1));
            } catch (ClassCastException e) {
                return null;
            }
        }
    }

    @Override
    public void write(Board board, Integer score) throws IOException {
        try(FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            List list = new ArrayList();
            list.add(board);
            list.add(score);
            objectOutputStream.writeObject(list);
        }
    }


    @Override
    public void close() {

    }
}
