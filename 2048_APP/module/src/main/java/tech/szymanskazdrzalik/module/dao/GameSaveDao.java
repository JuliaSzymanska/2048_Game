package tech.szymanskazdrzalik.module.dao;

import android.content.Context;

import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


class GameSaveDao implements Dao<SaveGame> {

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
    public void write(SaveGame saveGame) throws IOException {
        try (FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(saveGame);
        }
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void close() {

    }
}
