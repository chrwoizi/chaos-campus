package LevelObjects;

import Gui.HeadUpDisplay;
import GameManager;
import Player;
import StreamOperations;
import CountingInputStream;
import Level;
import Block;
import Position;


public class ContainerTrigger extends Trigger {
	
	// the direction which the player must face to fire the trigger
	private byte direction;
	private byte neededItem;
	private short container;
	
	public ContainerTrigger(Position position, Block parentBlock, byte direction, byte neededItem, short target) {
		super(position, parentBlock);

		this.direction = direction;
		this.neededItem = neededItem;
		this.container = target;
	}
	
	/**
	 * Loads a ContainerTrigger from file
	 * @param parentLevel
	 * @param stream
	 * @return
	 */
	public static ContainerTrigger load(Block parentBlock, short blockId, CountingInputStream stream) throws Exception {
		// load bytes
		byte position_and_flags = StreamOperations.readByte(stream);
		byte position_bits_x = (byte)((position_and_flags & (128+64+32))>>5);
		byte position_bits_y = (byte)((position_and_flags & (16+8+4))>>2);
		byte dir_bits = (byte)(position_and_flags & (2+1));
		byte needed_item = StreamOperations.readByte(stream);
		short target_serial = StreamOperations.readShort(stream);

		// convert bytes to data types
		Position position = Level.activeLevel.makePosition(blockId, position_bits_x, position_bits_y);
		
		// convert direction
		switch( dir_bits) {
		case 0: dir_bits = Level.dirUp; break;
		case 1: dir_bits = Level.dirRight; break;
		case 2: dir_bits = Level.dirDown; break;
		case 3: dir_bits = Level.dirLeft; break;
		}
		
		// return new Container
		return new ContainerTrigger(position, parentBlock, dir_bits, needed_item, target_serial);
	}

	public void use() {
		if( (Player.getInstance().getDirection() & direction) != 0)
		{
			if( Player.getInstance().hasItem(neededItem) || neededItem == 0 )
			{
				// keys are removed from the inventory
				if( neededItem == 10 || neededItem == 11 )
					Player.getInstance().removeItemFromInventory(neededItem);
				Container c = (Container)Level.activeLevel.getObject(container);
				if(c != null) {
					c.open();
				}
				else {
					// object does not exist. set state of object instead.
					LevelObjectState state = Level.activeLevel.getObjectState(container);
					if(state != null) {
						state.state |= LevelObjectState.OPEN;
					}
				}
			}
			else
				GameManager.getInstance().playComment((byte)37);
		}
	}
	
	public void hit() {
		if( (Player.getInstance().getDirection() & direction) != 0)
		{
			Container c = (Container)Level.activeLevel.getObject(container);
			if(c != null) {
				if( !c.isOpen() )
					HeadUpDisplay.showText("3: öffnen", (short)25);
			}
		}
	}
}
