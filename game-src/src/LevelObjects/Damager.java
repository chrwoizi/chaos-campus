package LevelObjects;
import CountingInputStream;
import GameMIDlet;

import StreamOperations;
import Level;
import Block;
import Position;
import Player;
import Story.StoryManager;


import javax.microedition.lcdui.game.Sprite;

//TODO make damage a trigger
public class Damager extends DynamicLevelObject {
	
	private byte value;
	public byte getValue() { return value; }
	
	public Damager(short id, Position position, Block parentBlock, boolean disabled, byte value) {
		super(id, position, parentBlock);

		setEnabled(!disabled);
		this.value = value;
		
		updateFrequency = GameMIDlet.MAX_FRAMES_PER_SECOND;
	}
	
	/**
	 * Loads a Damager from file
	 * @param parentLevel
	 * @param stream
	 * @return
	 */
	public static Damager load(Block parentBlock, short blockId, CountingInputStream stream) throws Exception {
		// load bytes
		byte position_and_flags = StreamOperations.readByte(stream);
		byte position_bits_x = (byte)((position_and_flags & (128+64+32))>>5);
		byte position_bits_y = (byte)((position_and_flags & (16+8+4))>>2);
		byte disabled_bit = (byte)((position_and_flags & (2))>>1);
		short serial = StreamOperations.readShort(stream);
		byte damage_value = StreamOperations.readByte(stream);

		// convert bytes to data types
		Position position = Level.activeLevel.makePosition(blockId, position_bits_x, position_bits_y);
		boolean disabled = disabled_bit > 0;
		
		// return new Container
		return new Damager(serial, position, parentBlock, disabled, damage_value);
	}
	
	public void onUpdate() {
		Position pp = Player.getInstance().getPosition();
		Position tp = getPosition();
		
		if(pp.fieldX == tp.fieldX && pp.fieldY == tp.fieldY) {
			Player.getInstance().receiveDamage(value);
			StoryManager.getInstance().onDamagerHit(id);
		}
	}
	
	public boolean collidesWith(Sprite otherObject) {
		return false;
	}

	protected int[] getFrameSequence(byte type) {
		return null;
	}

	protected byte getFrameDuration(byte animation, byte frame) {
		return 0;
	}
	
}
