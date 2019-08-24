package LevelObjects;

import Block;
import Position;


public abstract class Trigger extends LevelObject {
	
	public Trigger(Position position, Block parentBlock) {
		super(LevelObject.NO_ID, position, parentBlock);
	}

	public void hit() {}
	public void use() {}
	public void attack() {}
}
