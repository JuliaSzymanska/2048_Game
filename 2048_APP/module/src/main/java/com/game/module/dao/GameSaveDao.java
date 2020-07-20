package com.game.module.dao;

import android.content.Context;
import android.util.Pair;

import com.game.module.Board;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


class GameSaveDao implements Dao<Board, Integer, Long> {

    private String filename;
    private Context context;

    GameSaveDao(String filename, Context context) {
        this.filename = filename;
        this.context = context;
    }

    @Override
    public Triple<Board, Integer, Long> read() throws IOException, ClassNotFoundException, NullPointerException {
        try(FileInputStream fileInputStream = context.openFileInput(filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            try {
                List boardIntegerTimeTriple = (ArrayList) objectInputStream.readObject();
                return Triple.of((Board)boardIntegerTimeTriple.get(0),(Integer) boardIntegerTimeTriple.get(1), (Long) boardIntegerTimeTriple.get(2));
            } catch (ClassCastException | IndexOutOfBoundsException e) {
                return null;
            }
        }
    }

    @Override
    public void write(Board board, Integer score, Long time) throws IOException {
        try(FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            List list = new ArrayList();
            list.add(board);
            list.add(score);
            list.add(time);
            objectOutputStream.writeObject(list);
        }
    }


    @Override
    public void close() {

    }
}
