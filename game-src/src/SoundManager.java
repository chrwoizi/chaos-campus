
/**
 * 
 * @author Martin Fiebig, Martin Wahnschaffe
 *
 */
public class SoundManager 
{
	private static SoundManager instance = null;

	private Sound activeSound;
	private boolean mute;
	
	public SoundManager() {
		mute = false;
		activeSound = null;
	}

	public static SoundManager getInstance() {
		if(instance == null) {
			instance = new SoundManager();
		}
		return instance;
	}

	public void setActiveSound(byte id)
	{
		if( activeSound != null ) {
			if( activeSound.getId() == id )
				return;
			releaseActiveSound();
		}
		activeSound = new Sound(id);
		activeSound.load();
	}
	
	public void playSoundLooped(int loops)
	{
		if( activeSound == null || mute )
			return;
		this.activeSound.setLoop(loops);
		this.activeSound.play();
	}

	public void playSound()
	{
		if( activeSound == null || mute )
			return;
		this.activeSound.play();
	}
	
	public void releaseActiveSound()
	{	
		if( activeSound == null )
			return;
		activeSound.close();
		activeSound = null;
	}
	
	public void setMute(boolean mute)
	{
		this.mute = mute;
		if( activeSound != null )
			activeSound.stop();
	}
	
	public boolean getMute()
	{
		return this.mute;
	}
}
