package LevelObjects;

import StreamOperations;
import Level;
import Block;
import Position;
import Player;
import Story.StoryManager;
import CountingInputStream;


public class TeleportTrigger extends Trigger {
	
	private Position target;
	
	public TeleportTrigger(Position position, Block parentBlock, short targetX, short targetY) {
		super(position, parentBlock);

		target = new Position(targetX, targetY);
	}
	
	/**
	 * Loads a ExitTrigger from file
	 * @param parentLevel
	 * @param stream
	 * @return
	 */
	public static TeleportTrigger load(Block parentBlock, short blockId, CountingInputStream stream) throws Exception {
		// load bytes
		byte position_and_flags = StreamOperations.readByte(stream);
		byte position_bits_x = (byte)((position_and_flags & (128+64+32))>>5);
		byte position_bits_y = (byte)((position_and_flags & (16+8+4))>>2);
		short targetX = StreamOperations.readShort(stream);
		short targetY = StreamOperations.readShort(stream);

		// convert bytes to data types
		Position position = Level.activeLevel.makePosition(blockId, position_bits_x, position_bits_y);
		
		return new TeleportTrigger(position, parentBlock, targetX, targetY);
	}

	public void hit() {
		Player.getInstance().teleport(target);
		StoryManager.getInstance().onTeleported(target);
	}
}
