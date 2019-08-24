package LevelObjects;
import java.io.ByteArrayOutputStream;
import CountingInputStream;

import Position;
import StreamOperations;


/**
 * Used by LevelObjectStates to retrieve variable information about a LevelObject.
 * Saving/Loading derived types is not supported yet -> final
 * @author Christian Woizischke
 *
 */
public final class LevelObjectState {
	public static final byte ENABLED = 1;
	public static final byte VISIBLE = 2;
	public static final byte ALIVE = 4;
	public static final byte OPEN = 8;
	
	public Position position = null;
	public byte state = 0;
	
	/**
	 * Saves state to a stream. The stream is given by SaveGameManager.
	 * @param stream
	 * @throws Exception
	 */
	public void saveToStream(ByteArrayOutputStream stream) throws Exception {
		// save position
		StreamOperations.writeShort(stream, position.fieldX);
		StreamOperations.writeShort(stream, position.fieldY);
		
		// save state
		stream.write(state);
	}
	
	/**
	 * Loads state from a stream. The stream is given by SaveGameManager.
	 * @param stream
	 * @throws Exception
	 */
	public void loadFromStream(CountingInputStream stream) throws Exception {
		// load position
		position = new Position(StreamOperations.readShort(stream), StreamOperations.readShort(stream));
		
		// load state
		state = StreamOperations.readByte(stream);
	}
}
