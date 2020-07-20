package com.game.module.dao;

import android.content.Context;

import com.game.module.Board;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

class FileBoardDao implements Dao<Board>{

    private String filename;
    private Context context;

    FileBoardDao(String filename, Context context) {
        this.filename = filename;
        this.context = context;
    }

    @Override
    public Board read() throws IOException, ClassNotFoundException {
        try(FileInputStream fileInputStream = context.openFileInput(filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return (Board) objectInputStream.readObject();
        }
    }

    @Override
    public void write(Board board) throws IOException {
        try(FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(board);
        }
    }

    @Override
    public void close() {

    }
}
