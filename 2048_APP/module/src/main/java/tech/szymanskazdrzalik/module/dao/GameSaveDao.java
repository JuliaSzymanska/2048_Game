package tech.szymanskazdrzalik.module.dao;

import android.content.Context;

import androidx.annotation.Nullable;

import tech.szymanskazdrzalik.module.Board;

import org.apache.commons.lang3.tuple.Triple;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


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
        if (lock.tryLock()) {
            try (FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                List<Object> list = new ArrayList<Object>();
                list.add(board);
                list.add(score);
                list.add(time);
                objectOutputStream.writeObject(list);
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
