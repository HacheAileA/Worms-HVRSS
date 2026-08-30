package view.gui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

class SoundPlayerTest {

    private SoundPlayer soundPlayer;

    @BeforeEach
    void setUp() {
        soundPlayer = new SoundPlayer();
    }

    @Test
    void testPlayBackgroundMusiccallsClipMethods() throws Exception {
        try (MockedStatic<AudioSystem> mockedAudioSystem = mockStatic(AudioSystem.class)) {
            Clip mockClip = mock(Clip.class);
            AudioInputStream mockStream = mock(AudioInputStream.class);
            FloatControl mockVolume = mock(FloatControl.class);

            mockedAudioSystem.when(() -> AudioSystem.getAudioInputStream(any(File.class))).thenReturn(mockStream);
            mockedAudioSystem.when(AudioSystem::getClip).thenReturn(mockClip);

            when(mockClip.getControl(FloatControl.Type.MASTER_GAIN)).thenReturn(mockVolume);
            when(mockVolume.getMinimum()).thenReturn(-80.0f);
            when(mockVolume.getValue()).thenReturn(-80.0f);

            soundPlayer.playBackgroundMusic("test.wav", 100);

            verify(mockClip).loop(Clip.LOOP_CONTINUOUSLY);
            verify(mockClip).start();
            verify(mockVolume).setValue(-80.0f);

            setPrivateField(soundPlayer, "clip", mockClip);
        }
    }

    @Test
    void testStopMusiccallsClipStop() throws Exception {
        Clip mockClip = mock(Clip.class);
        when(mockClip.isRunning()).thenReturn(true);

        setPrivateField(soundPlayer, "clip", mockClip);

        soundPlayer.stopMusic();

        verify(mockClip).stop();
    }

    @Test
    void testPlaySoundEffectcallsClipStart() throws Exception {
        try (MockedStatic<AudioSystem> mockedAudioSystem = mockStatic(AudioSystem.class)) {
            Clip mockClip = mock(Clip.class);
            AudioInputStream mockStream = mock(AudioInputStream.class);
            FloatControl mockVolume = mock(FloatControl.class);

            mockedAudioSystem.when(() -> AudioSystem.getAudioInputStream(any(File.class))).thenReturn(mockStream);
            mockedAudioSystem.when(AudioSystem::getClip).thenReturn(mockClip);

            when(mockClip.getControl(FloatControl.Type.MASTER_GAIN)).thenReturn(mockVolume);

            soundPlayer.playSoundEffect("sound.wav");

            verify(mockVolume).setValue(-20.0f);
            verify(mockClip).start();

            setPrivateField(soundPlayer, "clipSoundClip", mockClip);
        }
    }

    @Test
    void testLoadAndPlayRunSound() throws Exception {
        try (MockedStatic<AudioSystem> mockedAudioSystem = mockStatic(AudioSystem.class)) {
            Clip mockClip = mock(Clip.class);
            AudioInputStream mockStream = mock(AudioInputStream.class);

            mockedAudioSystem.when(() -> AudioSystem.getAudioInputStream(any(File.class))).thenReturn(mockStream);
            mockedAudioSystem.when(AudioSystem::getClip).thenReturn(mockClip);

            soundPlayer.loadRunSound();

            verify(mockClip).open(mockStream);

            setPrivateField(soundPlayer, "ClipRun", mockClip);

            when(mockClip.isRunning()).thenReturn(false);
            soundPlayer.playRunSound();

            verify(mockClip).setFramePosition(0);
            verify(mockClip).loop(Clip.LOOP_CONTINUOUSLY);

            when(mockClip.isRunning()).thenReturn(true);
            soundPlayer.stopRunSound();
            verify(mockClip).stop();
        }
    }

    @Test
    void testStopMusicwhenClipIsNull() {
        soundPlayer.stopMusic();
    }

    @Test
    void testPlayRunSoundwhenClipRunIsNull() {
        soundPlayer.playRunSound();
    }

    @Test
    void testStopRunSoundwhenClipRunIsNull() {
        soundPlayer.stopRunSound();
    }

    @Test
    void testPlayRunSoundwhenAlreadyRunning() throws Exception {
        Clip mockClip = mock(Clip.class);
        when(mockClip.isRunning()).thenReturn(true);

        setPrivateField(soundPlayer, "ClipRun", mockClip);
        soundPlayer.playRunSound();

        verify(mockClip, never()).setFramePosition(anyInt());
        verify(mockClip, never()).loop(anyInt());
    }

    @Test
    void testFadeInadjustsVolume() throws Exception {
        FloatControl mockVolume = mock(FloatControl.class);
        when(mockVolume.getMinimum()).thenReturn(-80.0f);
        when(mockVolume.getValue()).thenReturn(-80.0f);

        setPrivateField(soundPlayer, "volumeControl", mockVolume);

        var method = SoundPlayer.class.getDeclaredMethod("fadeIn", int.class);
        method.setAccessible(true);
        method.invoke(soundPlayer, 50);

        Thread.sleep(100);

        verify(mockVolume, atLeast(1)).setValue(anyFloat());
    }

    @Test
    void testPlaySoundEffectexceptionHandled() throws Exception {
        try (MockedStatic<AudioSystem> mockedAudioSystem = mockStatic(AudioSystem.class)) {
            mockedAudioSystem.when(() -> AudioSystem.getAudioInputStream(any(File.class))).thenThrow(new IOException("test"));

            soundPlayer.playSoundEffect("fake.wav");
        }
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
