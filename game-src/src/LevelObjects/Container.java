package LevelObjects;

import StreamOperations;
import CountingInputStream;
import Level;
import Block;
import Position;
import Story.StoryManager;

import javax.microedition.lcdui.game.Sprite;


public class Container extends DynamicLevelObject {

	static final int CLOSED = 0;
	static final int OPEN = 1;
	private boolean isOpen;
	private short item;
	public short getItem() {return item;}
	
	public Container(short id, Position position, Block parentBlock, boolean disabled, short graphicOpen, short graphicClosed, short item) {
		super(id, position, parentBlock);

		setEnabled(!disabled);
		this.item = item;
		
		sprite = new Sprite(Level.activeLevel.getTileset(), 16,16);
		int[] sequence = {graphicClosed,graphicOpen};
		sprite.setFrameSequence(sequence);
		sprite.setFrame(0);
		sprite.defineReferencePixel(8, 8);
		sprite.defineCollisionRectangle(4, 8, 10, 8);
	}
	
	/**
	 * Loads a Container from file
	 * @param parentLevel
	 * @param stream
	 * @return
	 */
	public static Container load(Block parentBlock, short blockId, CountingInputStream stream) throws Exception {
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
		boolean disabled = disabled_bit > 0;
		short graphicOpen = graphic1;
		short graphicClosed = graphic2;
		
		// return new Container
		return new Container(serial, position, parentBlock, disabled, graphicOpen, graphicClosed, item_id);
	}
	
	/**
	 * Open the container and return the item
	 * @return item id
	 */
	public void open() {
		if(!isOpen) {
			StoryManager.getInstance().onContainerOpened(id);
			sprite.setFrame(OPEN);
			isOpen = true;
			if(item > 0) {
				Position pos = new Position(this.getPosition());
				pos.fieldY += 1;
				Item.spawnItem(pos, false, (byte)item);
			}
		}
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	/**
	 * Fills LevelObjectState with life status
	 */
	protected void fillState(LevelObjectState state) {
		super.fillState(state);
		if( isOpen )
			state.state |= LevelObjectState.OPEN; 
	}
	
	/**
	 * Loads properties from a LevelObjectState. 
	 */
	public void setState(LevelObjectState state) {
		// retrieve data from state
		isOpen = (state.state & LevelObjectState.OPEN) != 0;
		if( isOpen ) {
			if(sprite != null) sprite.setFrame(OPEN);
		}
		else {
			if(sprite != null) sprite.setFrame(CLOSED);
		}
		super.setState(state);
	}
	
	protected void onUpdate() {
		
	}

	protected int[] getFrameSequence(byte type) {
		return null;
	}

	protected byte getFrameDuration(byte animation, byte frame) {
		return 0;
	}

}
