package com.game.a2048_app.helpers;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is intended to simplify the process of playing sound clips in our application.
 * The purpose of the class is to automatize the process of  playing a given sound once and releasing the resources.
 */

public class SoundPlayer {

    // TODO: 03.08.2020  czy to powinien być singleton? chyba nie ale narazie jest lul
    private static SoundPlayer instance = new SoundPlayer();

    private float volume;

    public static SoundPlayer getInstance() {
        return instance;
    }

    /**
     * Sets the volume variable and saves the setting in SharedPreferences using {@link PreferencesHelper#setVolume(int)}.
     * @param volume Value of volume. Must be equal to 0 or 1
     * @throws IllegalArgumentException if given volume is not equal to 0 or 1
     */
    public void setVolume(float volume) {
        if(!(volume == 0 || volume == 1)) {
            throw new IllegalArgumentException("volume 0 or 1 (mute or not)");
        }
        this.volume = volume;
        PreferencesHelper.getInstance().setVolume((int) this.volume);
    }

    /**
     * Helper method intended to make passing the {@link AssetFileDescriptor} to the methods 
     * {@link #playSound(AssetFileDescriptor)} and {@link #playSound(AssetFileDescriptor, MediaPlayer.OnCompletionListener)} easier.
     * @param context Application context.
     * @param id Int representing the given asset
     * @return {@link AssetFileDescriptor} from a given id.
     */
    public AssetFileDescriptor getAsset(Context context, int id) {
        return context.getApplicationContext().getResources().openRawResourceFd(id);
    }

    /**
     * @see #playSound(AssetFileDescriptor, MediaPlayer.OnCompletionListener)
     */
    public void playSound(@NonNull AssetFileDescriptor assetFileDescriptor) {
        prepareAndRunMediaplayer(assetFileDescriptor, this.onCompletionListener);
    }

    /**
     * This method plays a given sound clip and sets user defined onCompletionListener on the MediaPlayer instance used to play the clip,
     * and releases the MediaPlayer instance after it's done playing.
     * Use {@link #playSound(AssetFileDescriptor)} to achieve the same functionality without using your own custom listener.
     * @param assetFileDescriptor Selected sound clip.
     * @param onCompletionListener User given listener, set to run on the end of the given soundclip.
     */
    public void playSound(@NonNull AssetFileDescriptor assetFileDescriptor, @NonNull MediaPlayer.OnCompletionListener onCompletionListener) {
        CompositeMediaPlayerOnCompletionListener compositeMediaPlayerOnCompletionListener = new CompositeMediaPlayerOnCompletionListener();
        compositeMediaPlayerOnCompletionListener.registerListener(this.onCompletionListener);
        compositeMediaPlayerOnCompletionListener.registerListener(onCompletionListener);
        prepareAndRunMediaplayer(assetFileDescriptor, compositeMediaPlayerOnCompletionListener);
    }

    private SoundPlayer() {
        this.volume = PreferencesHelper.getInstance().getVolume();
    }

    /**
     * Default onCompletionListener for {@link MediaPlayer} instances, calls {@link MediaPlayer#release()} on the MediaPlayer instance.
     */
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.release();
        }
    };

    /**
     * Initializes the MediaPlayer instances to correct {@link AudioAttributes} and volume.
     * @return Initialized MediaPlayer instance.
     */
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

    private class CompositeMediaPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

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

    /**
     * Prepares and runs the {@link MediaPlayer} instance. Uses {@link #initMediaPlayer()} to initialize the {@link MediaPlayer}.
     * @param assetFileDescriptor {@link AssetFileDescriptor} describing the audio file.
     * @param onCompletion Listener to run after the MediaPlayer instance stops playing its sound. By default {@link #onCompletionListener}.
     */
    private void prepareAndRunMediaplayer(AssetFileDescriptor assetFileDescriptor, MediaPlayer.OnCompletionListener onCompletion) {
            MediaPlayer mediaPlayer = this.initMediaPlayer();
            try {
            mediaPlayer.setDataSource(assetFileDescriptor);
            mediaPlayer.setOnCompletionListener(onCompletion);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
