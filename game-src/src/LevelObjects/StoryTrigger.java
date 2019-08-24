package LevelObjects;

import StreamOperations;
import Story.StoryManager;
import CountingInputStream;
import Level;
import Block;
import Position;



public class StoryTrigger extends Trigger {
	
	private byte storyId;
	
	public StoryTrigger(Position position, Block parentBlock, byte storyId) {
		super(position, parentBlock);

		this.storyId = storyId;
	}
	
	/**
	 * Loads a ContainerTrigger from file
	 * @param parentLevel
	 * @param stream
	 * @return
	 */
	public static StoryTrigger load(Block parentBlock, short blockId, CountingInputStream stream) throws Exception {
		// load bytes
		byte position_and_flags = StreamOperations.readByte(stream);
		byte position_bits_x = (byte)((position_and_flags & (128+64+32))>>5);
		byte position_bits_y = (byte)((position_and_flags & (16+8+4))>>2);
		byte story_id = StreamOperations.readByte(stream);

		// convert bytes to data types
		Position position = Level.activeLevel.makePosition(blockId, position_bits_x, position_bits_y);
		
		// return new Container
		return new StoryTrigger(position, parentBlock, story_id);
	}

	public void hit() {
		StoryManager.getInstance().onStoryTrigger(storyId);
	}

}
