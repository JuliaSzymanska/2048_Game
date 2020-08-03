package com.game.a2048_app.helpers;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundPlayer {

    // TODO: 03.08.2020  czy to powinien być singleton? chyba nie ale narazie jest lul
    private static SoundPlayer instance = new SoundPlayer();

    public static SoundPlayer getInstance() {
        return instance;
    }

    public void setVolume(float volume) {
        if(!(volume == 0 || volume == 1)) {
            throw new IllegalArgumentException("volume 0 or 1 (mute or not)");
        }
        this.volume = volume;
        PreferencesHelper.getInstance().setVolume((int) this.volume);
    }

    public AssetFileDescriptor getAsset(Context context, int id) {
        return context.getApplicationContext().getResources().openRawResourceFd(id);
    }

    public void playSound(@NonNull AssetFileDescriptor assetFileDescriptor) {
        setMediaPlayer(assetFileDescriptor, this.onCompletionListener);
    }

    public void playSound(@NonNull AssetFileDescriptor assetFileDescriptor, @NonNull MediaPlayer.OnCompletionListener onCompletionListener) {
        CompositeListener compositeListener = new CompositeListener();
        compositeListener.registerListener(this.onCompletionListener);
        compositeListener.registerListener(onCompletionListener);
        setMediaPlayer(assetFileDescriptor, compositeListener);
    }

    private SoundPlayer() {
        this.volume = PreferencesHelper.getInstance().getVolume();
    }

    private float volume;

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.release();
        }
    };

    private MediaPlayer initMediaPlayer() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setVolume(volume, volume);
        return mediaPlayer;
    }

    private class CompositeListener implements MediaPlayer.OnCompletionListener {

        private List<MediaPlayer.OnCompletionListener> registeredListeners = new ArrayList<>();

        public void registerListener (MediaPlayer.OnCompletionListener listener) {
            registeredListeners.add(listener);
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            for (MediaPlayer.OnCompletionListener listener : registeredListeners) {
                listener.onCompletion(mp);
            }
        }
    }


    private void setMediaPlayer(AssetFileDescriptor assetFileDescriptor, MediaPlayer.OnCompletionListener onCompletion) {
            MediaPlayer mediaPlayer = this.initMediaPlayer();
            try {
            mediaPlayer.setDataSource(assetFileDescriptor);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(onCompletion);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
