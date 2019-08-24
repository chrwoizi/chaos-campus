package LevelObjects;

import CountingInputStream;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

import StreamOperations;
import Level;
import Block;
import Position;
import Story.StoryManager;


public class Door extends DynamicLevelObject{

	static final int CLOSED = 0;
	static final int OPEN = 1;
	public boolean isOpen() {return sprite.getFrame() == OPEN;}

	private boolean open;
	private boolean initFromState; 
	
	public Door(short id, Position position, Block parentBlock, boolean disabled, boolean open, short graphicOpen, short graphicClosed) {
		super(id, position, parentBlock);

		setEnabled(!disabled);
		
		if(!initFromState) {
			this.open = open;
		}

		// load container images if needed
		sprite = new Sprite(Level.activeLevel.getTileset(), 16,16);
		int[] sequence = {graphicClosed,graphicOpen};
		sprite.setFrameSequence(sequence);
		sprite.setFrame(this.open?OPEN:CLOSED);
		sprite.defineReferencePixel(8, 8);
	}
	
	public void onRender(Graphics g) {
		//g.drawRegion(level.getTileset(), , , 16, 16, Sprite.TRANS_NONE, , , Graphics.TOP|Graphics.LEFT);
		sprite.paint(g);
	}
	
	public void onUpdate() {
		
	}

	public void open() {
		open = true;
		sprite.setFrame(OPEN);
		StoryManager.getInstance().onDoorOpened(id);
	}
	public void close() {
		open = false;
		sprite.setFrame(CLOSED);
	}
	
	/**
	 * overriden collision when door is opened
	 */
	public boolean collidesWith(Sprite otherObject) {
		if( otherObject == sprite )
			new Error("a door should not test for collsion");
		if( isOpen() )
			return false;
		return super.collidesWith(otherObject);
	}
	
	/**
	 * Fills LevelObjectState with door status
	 */
	protected void fillState(LevelObjectState state) {
		//EnemyState estate = (EnemyState)state;
		//estate.isAlive = !isAlive;
		super.fillState(state);
		if( isOpen() )
			state.state |= LevelObjectState.OPEN; 
	}
	
	/**
	 * Loads properties from a LevelObjectState. 
	 */
	public void setState(LevelObjectState state) {
		initFromState = true;
		
		// retrieve data from state
		if( (state.state & LevelObjectState.OPEN) != 0 ) {
			open = true;
			if(sprite != null) sprite.setFrame(OPEN);
		}
		else {
			open = false;
			if(sprite != null) sprite.setFrame(CLOSED);
		}
		super.setState(state);
	}
	
	/**
	 * Loads a Door from file
	 * @param parentLevel
	 * @param stream
	 * @return
	 */
	public static Door load(Block parentBlock, short blockId, CountingInputStream stream) throws Exception {
		// load bytes
		byte position_and_flags = StreamOperations.readByte(stream);
		byte position_bits_x = (byte)((position_and_flags & (128+64+32))>>5);
		byte position_bits_y = (byte)((position_and_flags & (16+8+4))>>2);
		byte disabled_bit = (byte)((position_and_flags & (2))>>1);
		byte open_bit = (byte)(position_and_flags & (1));
		short serial = StreamOperations.readShort(stream);
		short graphicOpen = StreamOperations.readShort(stream);
		short graphicClosed = StreamOperations.readShort(stream);

		// convert bytes to data types
		Position position = Level.activeLevel.makePosition(blockId, position_bits_x, position_bits_y);
		boolean disabled = disabled_bit > 0;
		boolean open = open_bit > 0;
		
		// return new Container
		return new Door(serial, position, parentBlock, disabled, open, graphicOpen, graphicClosed);
	}

	protected int[] getFrameSequence(byte type) {
		return null;
	}

	protected byte getFrameDuration(byte animation, byte frame) {
		return 0;
	}

}
