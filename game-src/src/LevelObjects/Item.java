package LevelObjects;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

import StreamOperations;
import Level;
import CountingInputStream;
import Block;
import Position;
import Player;
import Story.StoryManager;
import IdGenerator;
import Error;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.Sprite;

public class Item  extends DynamicLevelObject {
	
	public static final byte ITEM_UNKNOWN = 0;
	public static final byte ITEM_BIER = 1;
	public static final byte ITEM_WEIN = 2;
	public static final byte ITEM_MET = 3;
	public static final byte ITEM_CORMENIUS = 4;
	public static final byte ITEM_KEULE = 5;
	public static final byte ITEM_DRINKALIBUR = 6;
	public static final byte ITEM_DRINK_PART1 = 7;
	public static final byte ITEM_DRINK_PART2 = 8;
	public static final byte ITEM_DRINK_PART3 = 9;
	public static final byte ITEM_KEY1 = 10;
	public static final byte ITEM_KEY2 = 11;
	public static final byte ITEM_MOSAIK = 12;
	public static final byte ITEM_DISTILLER = 13;
	
	private static Image itemsImage = null;
	public static Image getItemsImage() {
		if( itemsImage == null ) {
			try {
				itemsImage = Image.createImage("/Items.png");
			} catch ( Exception e ) {
				new Error("Item img", e);
			}
		}
		return itemsImage;
	}
	
	private byte type;
	public byte getType() {return type;}
	
	private static Vector spawnedItems = new Vector(10);
	
	public Item(short id, Position position, Block parentBlock, boolean disabled, byte type) {
		super(id, position, parentBlock);

		sprite = new Sprite(getItemsImage(), 16, 16);
		
		try {
			sprite.setFrame(type-1);
		}
		catch(IndexOutOfBoundsException e) {
			new Error("Item" + id + "'s type id must be >= 1 and <= [number of images in sprite sheet]. You have set: " + type);
		}
		
		sprite.defineReferencePixel(8, 8);
		
		setEnabled(!disabled);
		this.type = type;
	}
	
	public static void spawnItem(Position position, boolean disabled, byte type) {
		ItemDescriptor d = new ItemDescriptor();
		d.id = IdGenerator.createId();
		d.position = new Position();
		d.position.set(position);
		d.disabled = disabled;
		d.type = type;
		spawnedItems.addElement(d);
		
		Item i = new Item(
				d.id,
				d.position, 
				null, 
				d.disabled, 
				d.type);
		Level.activeLevel.addDynamicObject(i);

		if(i.block != null) i.block.viewerPositionChanged();
	}
	
	protected void onUpdate() {
		
	}
	
	public boolean collidesWith(Sprite otherObject) {
		if( sprite.collidesWith(otherObject, false)
				&& Player.getInstance().isPlayerSprite(otherObject) ) {
			Player.getInstance().addItemToInventory(type);
			
			setEnabled(false);
			
			for(int i = 0; i < spawnedItems.size(); i++) {
				ItemDescriptor d = (ItemDescriptor)spawnedItems.elementAt(i);
				if(d.id == getId()) {
					spawnedItems.removeElementAt(i);
					break;
				}
			}
			
			StoryManager.getInstance().onItemCollected(type);
		}
		return false;
	}
	
	/**
	 * Loads a Item from file
	 * @param parentLevel
	 * @param stream
	 * @return
	 */
	public static Item load(Block parentBlock, short blockId, CountingInputStream stream) throws Exception {
		// load bytes
		byte position_and_flags = StreamOperations.readByte(stream);
		byte position_bits_x = (byte)((position_and_flags & (128+64+32))>>5);
		byte position_bits_y = (byte)((position_and_flags & (16+8+4))>>2);
		byte disabled_bit = (byte)((position_and_flags & (2))>>1);
		short serial = StreamOperations.readShort(stream);
		byte type = StreamOperations.readByte(stream);

		// convert bytes to data types
		Position position = Level.activeLevel.makePosition(blockId, position_bits_x, position_bits_y);
		boolean disabled = disabled_bit > 0;
		
		// return new Container
		return new Item(serial, position, parentBlock, disabled, type);
	}

	protected int[] getFrameSequence(byte type) {
		return null;
	}

	protected byte getFrameDuration(byte animation, byte frame) {
		return 0;
	}
	

	
	/**
	 * Saves all attributes to a stream. The stream is given by SaveGameManager.
	 * @param stream
	 * @throws Exception
	 */
	public static void saveSpawnedItemsToStream(ByteArrayOutputStream stream) throws Exception {
		
		StreamOperations.writeShort(stream, (short)spawnedItems.size());
		
		for(int i = 0; i < spawnedItems.size(); i++) {
			ItemDescriptor d = (ItemDescriptor)spawnedItems.elementAt(i);

			StreamOperations.writeShort(stream, d.id);
			StreamOperations.writeShort(stream, d.position.fieldX);
			StreamOperations.writeShort(stream, d.position.fieldY);
			stream.write(d.disabled ? (byte)1 : (byte)0);
			stream.write(d.type);
		}
	}
	
	/**
	 * Resets the references to the spawned objects. does not reset an item
	 *
	 */
	public static void reset() {
		spawnedItems = new Vector(10);
	}
	
	/**
	 * Loads all attributes from a stream. The stream is given by SaveGameManager.
	 * @param stream
	 * @throws Exception
	 */
	public static void loadSpawnedItemsFromStream(CountingInputStream stream) throws Exception {

		reset();
		
		short count = StreamOperations.readShort(stream);
		
		for(short i = 0; i < count; i++) {
			ItemDescriptor d = new ItemDescriptor();

			d.id = StreamOperations.readShort(stream);
			d.position = new Position(StreamOperations.readShort(stream), StreamOperations.readShort(stream));
			d.disabled = StreamOperations.readByte(stream) == 0 ? false : true;
			d.type = StreamOperations.readByte(stream);
			
			spawnedItems.addElement(d);
			
			Item obj = new Item(
					d.id,
					d.position, 
					null, 
					d.disabled, 
					d.type);
			Level.activeLevel.addDynamicObject(obj);

			if(obj.block != null) obj.block.viewerPositionChanged();
		}
	}
}
