package LevelObjects;


import CountingInputStream;
import StreamOperations;
import Level;
import Position;
import Block;

/**
 * The Class NpcCreator is a Factoryclass which creates different NPCs for the Level
 * 
 * @author Sascha Lity, Malte Mauritz
 *
 */
public class NpcCreator {
	/**
	 * This method creates different NPC by controling the param npcType
	 * 
	 * @param id
	 * @param position
	 * @param parentBlock
	 * @param disabled
	 * @param npcType Give back which NPC have to create
	 * @param itemWhenDying Determinds which items the NPC will drop 
	 * @return The created NPC
	 */
	public static NPC createNpc(short id, Position position, Block parentBlock, boolean disabled,
				int npcType, byte itemWhenDying) {
		NPC npc = null;
		if (npcType == 0) {
			// create Zombie
			npc = new Zombie(id, position, parentBlock, disabled, npcType, itemWhenDying);
		}
		else if (npcType == 1) {
			// create Skeleton
			npc = new Skeleton(id, position, parentBlock, disabled, npcType, itemWhenDying);
		}
		else if (npcType == 2) {
			// create Werewolf
			npc = new Werewolf(id, position, parentBlock, disabled, npcType, itemWhenDying);
		}
		if (npcType == 3) {
			// create Zombie Master
			npc = new ZombieMaster(id, position, parentBlock, disabled, npcType, itemWhenDying);
		}
		else if (npcType == 101) {
			// create Glutamator
			npc = new Glutamator(id, position, parentBlock, disabled, npcType, itemWhenDying);
		}
		else if (npcType == 102) {
			// create Golem
			npc = new Golem(id, position, parentBlock, disabled, npcType, itemWhenDying);
		}
		else if (npcType == 103) {
			// create Witch
			npc = new Witch(id, position, parentBlock, disabled, npcType, itemWhenDying);
		}
		return npc;
	}
	
	/**
	 * Loads Npc from a Stream
	 * @param parentBlock
	 * @param blockId
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	public static NPC loadNpcFromStream(Block parentBlock, short blockId, 
				CountingInputStream stream) throws Exception{
		
		//load bytes
		byte position_and_flags = StreamOperations.readByte(stream);
		byte position_bits_x = (byte)((position_and_flags & (128+64+32))>>5);
		byte position_bits_y = (byte)((position_and_flags & (16+8+4))>>2);
		byte disabled_bit = (byte)(position_and_flags & (2));
		short serial = StreamOperations.readShort(stream);
		byte npcType = StreamOperations.readByte(stream);
		byte itemWhenDying = StreamOperations.readByte(stream);

		// convert bytes to data types
		Position position = Level.activeLevel.makePosition(blockId, position_bits_x, position_bits_y);
		
		// return new Container
		return createNpc(serial, position, parentBlock, disabled_bit>0, npcType, itemWhenDying);
	}
	
	/*
	 * Autmatisierter Test der Klasse NpcCreator
	 */
	
	public static String test() {
	    String result ="";
		for (int type=0;type<4;type++) {
		NPC enemy = createNpc((short)1, new Position(1,1),new Block(1,1), false, type, (byte)0);
		if(enemy.npcType != type) result = result + "NpcCreator did not created the right enemy! Should have created "+type+", but created "+ enemy.npcType;
		}
		if (result.equals(""))return "Success";
		else return result;
	}
}


