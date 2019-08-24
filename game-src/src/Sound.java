import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.Manager;
import java.io.InputStream;

/**
 * 
 * @author Martin Fiebig, Martin Wahnschaffe
 *
 */
public class Sound {
	private Player player;
	private String strType;
	private boolean loaded;
	private byte id;
	
	public static final String midi = "audio/midi";
	public static final String wave = "audio/x-wav";

	/* 
	 *
	 * Mediatypen die bis jetzt funktionieren:	Resourceendung|	Mediatyp
	 * 											--------------+-------------
	 * 											midi (*.mid)  |	audio/midi
	 * 											wave (*.wav)  |	audio/x-wav
	 */
	Sound()
	{
		this.id = 0;
		this.loaded = false;
		this.strType = "";
	}

	Sound(byte id)
	{
		if( id <= 0 )
			new Error("sound id must be larger than 0");
		this.id = id;
		this.loaded = false;
		this.strType = Sound.midi;
	}

	public void load()
	{
		try {
			InputStream is = getClass().getResourceAsStream("sounds/sound"+id+".mid");
			player = Manager.createPlayer(is, this.strType);
			player.realize();
			player.prefetch();
			if (player.getState() != Player.PREFETCHED)
				throw new Exception("prefetching didnt work");
		} 
		catch (Exception e) {
			new Error("eror loading sound", e);
		}
		 loaded = true;
	}

	void play()
	{
		if (this.loaded != true)
			this.load();
		try {
			player.start();
		} catch (MediaException e) {
			new Error("error playing sound", e);
		}
	}

	boolean isLoaded()
	{
		return this.loaded;
	}

	byte getId() { return id; }
	
	void setLoop(int loops) {
		try {
			player.setLoopCount(loops);
		} catch (Exception e) {
			new Error("sound setLoop", e);
		}
	}

	int getLength() {
		return (int) player.getDuration()/1000;
	}
	
	public boolean isPlaying()
	{
		return (this.player.getState() == Player.CLOSED)? false : true;
	}
	
	public void stop()
	{
		try {
			player.stop();
		} catch (Exception e) {
			new Error("error stopping sound", e);
		}
	}
	
	public void close()
	{
		try {
			player.stop();
			player.close();
			loaded = false;
		} catch (Exception e) {
			new Error("error closing sound", e);
		}
	}
}
