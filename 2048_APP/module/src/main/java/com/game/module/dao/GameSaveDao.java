package com.game.module.dao;

import android.content.Context;

import androidx.annotation.Nullable;

import com.game.module.Board;

import org.apache.commons.lang3.tuple.Triple;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


class GameSaveDao implements Dao<Board, Integer, Long> {

    private String filename;
    private Context context;

    GameSaveDao(String filename, Context context) {
        this.filename = filename;
        this.context = context;
    }

    // TODO: 24.07.2020 PAMIETAJ O MNIE PRZEMKU
    @Nullable
    @Override
    public Triple<Board, Integer, Long> read() throws IOException, ClassNotFoundException, NullPointerException {
        try(FileInputStream fileInputStream = context.openFileInput(filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            try {
                Board inBoard = null;
                Integer inScore = null;
                Long inTime = null;
                Object obj = objectInputStream.readObject();
                if (obj instanceof ArrayList<?>) {
                    ArrayList<?> al = (ArrayList<?>) obj;
                    if (al.size() > 0) {
                        for(Object o : al) {
                            if (o instanceof Board) {
                                inBoard = (Board) o;
                            } else if (o instanceof Integer) {
                                inScore = (Integer) o;
                            } else if (o instanceof Long) {
                                inTime = (Long) o;
                            }
                        }
                    }
                }
                return Triple.of(inBoard, inScore, inTime);
            } catch (ClassCastException | IndexOutOfBoundsException e) {
                return null;
            }
        }
    }

    @Override
    public void write(Board board, Integer score, Long time) throws IOException {
        try(FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            List<Object> list = new ArrayList<Object>();
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
