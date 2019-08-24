package LevelObjects;

import StreamOperations;
import Level;
import Block;
import Position;
import Story.StoryManager;
import CountingInputStream;

import javax.microedition.lcdui.game.Sprite;

public class Moveable extends DynamicLevelObject {

	private short graphic;
	public short getGraphicId() { return graphic; }
	
	public Moveable(short id, Position position, Block parentBlock, boolean disabled, short graphic) {
		super(id, position, parentBlock);

		setEnabled(!disabled);
		this.graphic = graphic;
		sprite = new Sprite(Level.activeLevel.getTileset(), 16,16);
		int[] sequence = {graphic};
		sprite.setFrameSequence(sequence);
		sprite.setFrame(0);
	}
	
	/**
	 * Loads a Moveable from file
	 * @param parentLevel
	 * @param stream
	 * @return
	 */
	public static Moveable load(Block parentBlock, short blockId, CountingInputStream stream) throws Exception {
		// load bytes
		byte position_and_flags = StreamOperations.readByte(stream);
		byte position_bits_x = (byte)((position_and_flags & (128+64+32))>>5);
		byte position_bits_y = (byte)((position_and_flags & (16+8+4))>>2);
		byte disabled_bit = (byte)((position_and_flags & (2))>>1);
		short serial = StreamOperations.readShort(stream);
		short graphic = StreamOperations.readShort(stream);

		// convert bytes to data types
		Position position = Level.activeLevel.makePosition(blockId, position_bits_x, position_bits_y);
		boolean disabled = disabled_bit > 0;
		
		// return new Container
		return new Moveable(serial, position, parentBlock, disabled, graphic);
	}
	
	public boolean move(byte direction, byte speed) {
		StoryManager.getInstance().onMoveableMoved(id);
		return super.move(direction, speed);
	}

	protected int[] getFrameSequence(byte type) {
		return null;
	}

	protected byte getFrameDuration(byte animation, byte frame) {
		return 0;
	}
}
