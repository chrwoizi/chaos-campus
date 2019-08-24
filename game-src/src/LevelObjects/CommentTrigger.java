package LevelObjects;

import StreamOperations;
import CountingInputStream;
import GameManager;
import Level;
import Block;
import Position;
import Player;
import Gui.HeadUpDisplay;



public class CommentTrigger extends Trigger {
	
	// the comment will be played if this fires
	private byte commentId;
	
	// if true, the player must face the up direction to trigger
	private boolean up;
	
	// if true, the player must press use to trigger
	private boolean use;
	
	public CommentTrigger(Position position, Block parentBlock, byte commentId, boolean up, boolean use) {
		super(position, parentBlock);

		this.commentId = commentId;
		this.up = up;
		this.use = use;
	}
	
	/**
	 * Loads a ContainerTrigger from file
	 * @param parentLevel
	 * @param stream
	 * @return
	 */
	public static CommentTrigger load(Block parentBlock, short blockId, CountingInputStream stream) throws Exception {
		// load bytes
		byte position_and_flags = StreamOperations.readByte(stream);
		byte position_bits_x = (byte)((position_and_flags & (128+64+32))>>5);
		byte position_bits_y = (byte)((position_and_flags & (16+8+4))>>2);
		byte comment_id = StreamOperations.readByte(stream);

		// convert bytes to data types
		Position position = Level.activeLevel.makePosition(blockId, position_bits_x, position_bits_y);

		boolean up = (position_and_flags & 1) > 0;
		boolean use = (position_and_flags & 2) > 0;
		
		// return new Container
		return new CommentTrigger(position, parentBlock, comment_id, up, use);
	}

	public void hit() {
		if(!use) {
			if(!up || Player.getInstance().getDirection() == Level.dirUp) {
				GameManager.getInstance().playComment(commentId);
			}
		}
		else if(!up || Player.getInstance().getDirection() == Level.dirUp)
			HeadUpDisplay.showText("3: ansehen", (short)25);
	}

	public void use() {
		if(use) {
			if(!up || Player.getInstance().getDirection() == Level.dirUp) {
				GameManager.getInstance().playComment(commentId);
			}
		}
	}

}
