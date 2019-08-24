package LevelObjects;

import StreamOperations;
import Level;
import Block;
import Position;
import Story.StoryManager;
import CountingInputStream;


import javax.microedition.lcdui.game.Sprite;


public class Breakable extends DynamicLevelObject {

	private boolean destroyed;
	
	private short item;
	public short getItem() {return item;}
	
	public Breakable(short id, Position position, Block parentBlock, boolean disabled, short graphicOpen, short graphicClosed, short item) {
		super(id, position, parentBlock);

		setEnabled(!disabled);
		this.item = item;
		destroyed = false;

		sprite = new Sprite(Level.activeLevel.getTileset(), 16,16);
		int[] sequence = {graphicClosed,graphicOpen};
		sprite.defineReferencePixel(8, 8);
		sprite.setFrameSequence(sequence);
		sprite.setFrame(0);
	}
	
	/**
	 * Loads a Container from file
	 * @param parentLevel
	 * @param stream
	 * @return
	 */
	public static Breakable load(Block parentBlock, short blockId, CountingInputStream stream) throws Exception {
		// load bytes
		byte position_and_flags = StreamOperations.readByte(stream);
		byte position_bits_x = (byte)((position_and_flags & (128+64+32))>>5);
		byte position_bits_y = (byte)((position_and_flags & (16+8+4))>>2);
		byte disabled_bit = (byte)((position_and_flags & (2))>>1);
		short serial = StreamOperations.readShort(stream);
		byte item_id = StreamOperations.readByte(stream);
		short graphic1 = StreamOperations.readShort(stream);
		short graphic2 = StreamOperations.readShort(stream);

		// convert bytes to data types
		Position position = Level.activeLevel.makePosition(blockId, position_bits_x, position_bits_y);
		short graphicOpen = graphic1;
		short graphicClosed = graphic2;
		
		// return new Container
		return new Breakable(serial, position, parentBlock, disabled_bit>0, graphicOpen, graphicClosed, item_id);
	}
	
	public void destroy() {
		if( !destroyed ) {
			destroyed = true;
			if(item > 0) Item.spawnItem(this.getPosition(), false, (byte)item);
			StoryManager.getInstance().onBreakableDestroyed(id);
		}
	}

	protected int[] getFrameSequence(byte type) {
		return null;
	}

	protected byte getFrameDuration(byte animation, byte frame) {
		return 0;
	}
}
