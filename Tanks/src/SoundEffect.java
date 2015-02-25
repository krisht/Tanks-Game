

import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;

public enum SoundEffect
{
	tanksTheme("ThemeTanks.wav"),
	bulletExplosion("explosion.wav"),
	gameLost("YouLost.wav"),
	gameWon("YouWon.wav"),
	bombExplosion("bulletBomb.wav"); //scoreBaord("ScoreBoardMusic.mp3");

	public static enum Volume
	{
		MUTE, LOW, MEDIUM, HIGH
	}

	public static Volume volume = Volume.LOW;
	private Clip clip;


	SoundEffect(String soundFileName)
	{
		try
		{
			// Use URL (instead of File) to read from disk and JAR.
			URL url = this.getClass().getClassLoader().getResource(soundFileName);
			// Set up an audio input stream piped from the sound file.
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
			// Get a clip resource.
			clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioInputStream);
		}
		catch (UnsupportedAudioFileException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (LineUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	// Play or Re-play the sound effect from the beginning, by rewinding.
	public void playOnce()
	{
		if (volume != Volume.MUTE)
		{
			if (clip.isRunning())
				clip.stop();   // Stop the player if it is still running
			clip.setFramePosition(0); // rewind to the beginning
			clip.start();     //Will play sound once
			//will repeatedly loop sound until you call stop()
		}
	}
	
	public boolean isPlaying(){
		return clip.isRunning();
	}

	public void playCont(){

		if(clip.isRunning())
			clip.stop();
		clip.setFramePosition(0);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	public void stop()
	{
		clip.stop();
	}

	public static void init()
	{
		values(); 
	}
}