package LevelObjects;

import StreamOperations;
import CountingInputStream;
import Level;
import GameManager;
import Block;
import Position;


public class ExitTrigger extends Trigger {
	
	private byte targetLevel;
	private Position startPos;
	
	public ExitTrigger(Position position, Block parentBlock, byte targetLevel, short startX, short startY) {
		super(position, parentBlock);

		this.targetLevel = targetLevel;
		startPos = new Position();
		startPos.fieldX = startX;
		startPos.fieldY = startY;
	}
	
	/**
	 * Loads a ExitTrigger from file
	 * @param parentLevel
	 * @param stream
	 * @return
	 */
	public static ExitTrigger load(Block parentBlock, short blockId, CountingInputStream stream) throws Exception {
		// load bytes
		byte position_and_flags = StreamOperations.readByte(stream);
		byte position_bits_x = (byte)((position_and_flags & (128+64+32))>>5);
		byte position_bits_y = (byte)((position_and_flags & (16+8+4))>>2);
		byte target_level = StreamOperations.readByte(stream);
		short startX = StreamOperations.readShort(stream);
		short startY = StreamOperations.readShort(stream);

		// convert bytes to data types
		Position position = Level.activeLevel.makePosition(blockId, position_bits_x, position_bits_y);
		
		return new ExitTrigger(position, parentBlock, target_level, startX, startY);
	}

	public void hit() {
		GameManager.getInstance().changeLevel(targetLevel, startPos);
		
	}
}
