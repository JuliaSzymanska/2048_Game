package tech.szymanskazdrzalik.module.dao;

import android.content.Context;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import tech.szymanskazdrzalik.module.Board;


class GameSaveDao implements Dao<Board, Integer, Long> {

    private static Lock lock = new ReentrantLock();

    private String filename;
    private Context context;

    GameSaveDao(String filename, Context context) {
        this.filename = filename;
        this.context = context;
    }

    @Nullable
    @Override
    public SaveGame read() throws SaveGame.SaveGameException {
        try (FileInputStream fileInputStream = context.openFileInput(filename);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            Object obj = objectInputStream.readObject();
            if (obj instanceof SaveGame) {
                return (SaveGame) obj;
            } else throw new SaveGame.SaveGameException("Invalid Save Object");
        } catch (Exception e) {
            throw new SaveGame.SaveGameException("Save Game Operation Failed", e);
        }

    }

    @Override
    public void write(Board board, Integer score, Long time) throws IOException {
        if (lock.tryLock()) {
            try (FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                SaveGame saveGame = new SaveGame(board, score, time);
                objectOutputStream.writeObject(saveGame);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void close() {

    }
}
