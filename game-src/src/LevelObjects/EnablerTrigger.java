package LevelObjects;

import StreamOperations;
import CountingInputStream;
import Level;
import Block;
import Position;


public class EnablerTrigger extends Trigger {

	private boolean disabler;
	private short[] targetIds;
	
	public EnablerTrigger(Position position, Block parentBlock, boolean disabler, short[] targetIds) {
		super(position, parentBlock);

		this.disabler = disabler;
		this.targetIds = targetIds;
	}
	
	/**
	 * Loads a EnablerTrigger from file
	 * @param parentLevel
	 * @param stream
	 * @return
	 */
	public static EnablerTrigger load(Block parentBlock, short blockId, CountingInputStream stream) throws Exception {
		// load bytes
		byte position_and_flags = StreamOperations.readByte(stream);
		byte position_bits_x = (byte)((position_and_flags & (128+64+32))>>5);
		byte position_bits_y = (byte)((position_and_flags & (16+8+4))>>2);
		byte disabler_bit = (byte)((position_and_flags & (2))>>1);
		//byte multi_bit = (byte)(position_and_flags & (1));

		byte target_count = 1;
		short[] target_ids = null;
		target_count = StreamOperations.readByte(stream);
		
		// read target ids if multi
		//if(multi_bit == 1) {
			// initialize target ids array
			target_ids = new short[target_count];
			for(int i = 0; i < target_count; i++) {
				target_ids[i] = StreamOperations.readShort(stream);
			}
		/*} 
		else
		{
			// initialize target ids array
			target_count = 1;
			target_ids = new short[1];
			target_ids[0] = StreamOperations.readShort(stream);
		}*/

		// convert bytes to data types
		Position position = Level.activeLevel.makePosition(blockId, position_bits_x, position_bits_y);
		
		return new EnablerTrigger(position, parentBlock, disabler_bit>0, target_ids);
	}

	/**
	 * Called when the player activates the trigger.
	 * Enables or disables the targets.
	 */
	public void hit() {
		for(short i = 0; i < targetIds.length; i++) {
			short id = targetIds[i];
			LevelObject obj = Level.activeLevel.getObject(id);
			if(obj != null) {
				// set the state of the existing object
				if(disabler) {
					obj.setEnabled(false);
				}
				else
				{
					obj.setEnabled(true);
				}
			}
			else {
				// object does not exist. set state of object instead.
				LevelObjectState state = Level.activeLevel.getObjectState(id);
				if(state != null) {
					if(disabler) {
						state.state |= LevelObjectState.ENABLED;
					}
					else if( (state.state & LevelObjectState.ENABLED) != 0 )
					{
						state.state -= LevelObjectState.ENABLED;
					}
				}
			}
		}
	}
}
