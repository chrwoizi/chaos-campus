package LevelObjects;

import Position;

/**
 * used to save spawned items
 * @author Christian Woizischke
 *
 */
public class ItemDescriptor {
		public short id;
		public Position position;
		public boolean disabled;
		public byte type;
		public ItemDescriptor() {}
}
