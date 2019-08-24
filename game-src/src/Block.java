import javax.microedition.lcdui.*; 
import javax.microedition.lcdui.game.*;

import LevelObjects.*;

import java.util.*;

/**
 * The Level is made of Blocks. A Block contains 8*8 tiles.
 * @author Martin Wahnschaffe
 *
 */
public class Block {
	
	public static final int FIELDS_PER_BLOCK = 8;
	public static final byte PIXELS_PER_FIELD = 16;
	public static final int PIXELS_PER_BLOCK = FIELDS_PER_BLOCK*PIXELS_PER_FIELD;
	
	private TiledLayer groundLayer;
	
	// TODO replace Field class, because it's only a byte with overhead
	private Field[][] fields;
	
	// this array of lists holds all visble objects
	// within this block in lines ordered by their position
	private Vector[] staticObjects;
	private Vector[] dynamicObjects;
	
	private Vector triggers;
	
	// block position in tiles - 0,0 is top|left of the left
	private Position position;
	
	
	/**
	 * Constructor
	 * @param _parentLevel The Level which contains this
	 */
	public Block(int blockX, int blockY) {
		try {
			// Create fields
			fields = new Field[FIELDS_PER_BLOCK][FIELDS_PER_BLOCK];
			for(int x = 0; x < FIELDS_PER_BLOCK; x++) {
				for(int y = 0; y < FIELDS_PER_BLOCK; y++) {
					fields[x][y] = new Field();
				}
			}
			
			// get tileset from parent
			Image tileset = Level.activeLevel.getTileset();
			
			// create ground layer
			groundLayer = new TiledLayer(8, 8, tileset, 16, 16);
			
			staticObjects = new Vector[FIELDS_PER_BLOCK];
			for(int i=0; i<FIELDS_PER_BLOCK; i++)
				staticObjects[i] = new Vector(FIELDS_PER_BLOCK/2);
			dynamicObjects = new Vector[FIELDS_PER_BLOCK];
			for(int i=0; i<FIELDS_PER_BLOCK; i++)
				dynamicObjects[i] = new Vector(FIELDS_PER_BLOCK/2);
			// TODO find good value for triggers
			triggers = new Vector(4);
			
			// create position
			position = new Position((short)(blockX * FIELDS_PER_BLOCK), (short)(blockY * FIELDS_PER_BLOCK));
		}
		catch(Exception e) {
			new Error("Block(" + blockX + "," + blockY + ")", e);
		}

		// add cormenius
		/*if( blockX == 3 && blockY == 1) {
			addDynamicObject_test(new LevelObjects.Item((short)19, 
					new Position(26,14), this, false, (byte)4));
			// test potion
			addDynamicObject_test(new LevelObjects.Item((short)20, 
					new Position(26,12), this, false, (byte)1));
		}*/
		// add club
		/*if(blockX == 0 && blockY == 6) {
			addDynamicObject_test(new LevelObjects.Item((short)18, 
					new Position(5,53), this, false, (byte)5));
		}*/
		// add closed doors, story trigger and teleporter
		/*if( blockX == 2 && blockY == 2) {
			addDynamicObject_test(new Door((short)21,
					new Position(21,20), this, false, false, (short)25, (short)0));
			addDynamicObject_test(new Door((short)22,
					new Position(22,20), this, false, false, (short)25, (short)0));

			addTrigger_test(new StoryTrigger(new Position(21,19), this, (byte)1));
			addTrigger_test(new StoryTrigger(new Position(22,19), this, (byte)1));
			
			addTrigger_test(new TeleportTrigger(new Position(21,20), this, (short)3, (short)49));
			addTrigger_test(new TeleportTrigger(new Position(22,20), this, (short)4, (short)49));
		}*/
		// add comment trigger
		/*if( blockX == 2 && blockY == 0) {
			addTrigger_test(new StoryTrigger(new Position(16,7), this, (byte)2));
		}
		// add test enemies
		if(blockX == 1 && blockY == 6) {
			addDynamicObject_test(NpcCreator.createNpc((short)50,
					new Position(12,53), this, false, 0));
			addDynamicObject_test(NpcCreator.createNpc((short)51,
					new Position(12,54), this, false, 0));
			addDynamicObject_test(NpcCreator.createNpc((short)52,
					new Position(12,55), this, false, 0));

			addDynamicObject_test(NpcCreator.createNpc((short)53,
					new Position(16,53), this, false, 1));
			addDynamicObject_test(NpcCreator.createNpc((short)54,
					new Position(17,54), this, false, 1));
			addDynamicObject_test(NpcCreator.createNpc((short)55,
					new Position(18,55), this, false, 1));
		}
		if(blockX == 2 && blockY == 6) {
			addDynamicObject_test(NpcCreator.createNpc((short)56,
					new Position(22,53), this, false, 2));
			addDynamicObject_test(NpcCreator.createNpc((short)57,
					new Position(22,54), this, false, 2));
			addDynamicObject_test(NpcCreator.createNpc((short)58,
					new Position(22,55), this, false, 2));
		}/*
		// trigger for riddles
		if(blockX == 1 && blockY == 5) {
			addTrigger_test(new StoryTrigger(new Position(9,44), this, (byte)6));
		}
		// trigger for riddles
		if(blockX == 1 && blockY == 5) {
			addTrigger_test(new StoryTrigger(new Position(9,46), this, (byte)7));
		}
		// trigger for riddles
		if(blockX == 1 && blockY == 6) {
			addTrigger_test(new StoryTrigger(new Position(9,49), this, (byte)5));
		}*/
	}
	
	public String toString() {
		return "Block(" + this.position.getBlockX() + "," + this.position.getBlockY() + ")";
	}
	
	/**
	 * Loads collision and tile ids from a DataInputStream
	 * @param resource
	 */
	public void load(CountingInputStream stream) throws Exception{
		for(int y = 0; y < FIELDS_PER_BLOCK; y++) {
			for(int x = 0; x < FIELDS_PER_BLOCK; x++) {
				byte b2 = StreamOperations.readByte(stream);
				byte b1 = StreamOperations.readByte(stream);
				
				fields[x][y].collision = (byte)(b1>>4);

				b1 = (byte)(b1&15);
				short ub1 = (short)(b1 < 0 ? 256 + b1 : b1);
				short ub2 = (short)(b2 < 0 ? 256 + b2 : b2);
				
				short graphicID = (short)((((short)ub1)<<8) + ub2); 
				groundLayer.setCell(x, y, graphicID+1);
			}	
		}
	}
	
	public Field[][] getFields() { return fields; }
	
	public void addStaticObject(LevelObject obj) {
		if( !obj.isVisible() ) {
	        new Error("invisible objects not handled");
	        return;
		}
		staticObjects[obj.getPosition().getBlockRelativeFieldY()].addElement(obj);
	}
	public void remStaticObject(LevelObject obj) {
		staticObjects[obj.getPosition().getBlockRelativeFieldY()].removeElement(obj);
	}

	public void addDynamicObject_test(LevelObject obj) {
		// add to level
		Level.activeLevel.addObjectReference(obj);
		// add to this
		addDynamicObject(obj);
		// load state if existing
		Level.activeLevel.objectStates.loadState(obj);
	}
	public void addDynamicObject(LevelObject obj) {
		if( !obj.isVisible() ) {
	        new Error("invisible objects not handled");
	        return;
		}
		dynamicObjects[obj.getPosition().getBlockRelativeFieldY()].addElement(obj);
	}
	public void remDynamicObject(LevelObject obj) {
		dynamicObjects[obj.getPosition().getBlockRelativeFieldY()].removeElement(obj);
	}
	
	public void addTrigger_test(Trigger obj) {
		Level.activeLevel.addObjectReference(obj);
		addTrigger(obj);
	}
	public void addTrigger(Trigger obj) {
		triggers.addElement(obj);
	}
	public void remTrigger(Trigger obj) {
		triggers.removeElement(obj);
	}	
	
	/**
	 * Returns the position of this.
	 * @return position.
	 */
	public Position getPosition() {
		return position;
	}
	
	/**
	 * Renders a line of objects
	 * @param g The graphics context to draw on.
	 */
	public void renderObjectLine(Graphics g, int line) {
		for( int i=0; i<staticObjects[line].size(); i++) {
			((LevelObject)staticObjects[line].elementAt(i)).render(g);
			//if(position.fieldX == 24 && position.fieldY == 0)
			//System.out.println(position + "____" + ((LevelObject)staticObjects[line].elementAt(i)));
		}
		for( int i=0; i<dynamicObjects[line].size(); i++) {
			((LevelObject)dynamicObjects[line].elementAt(i)).render(g);
		}
	}
	
	/**
	 * Renders the block ground 
	 * Called by Level
	 * @param g The graphics context to draw on.
	 */
	public void render(Graphics g) {
		// draw layer
		groundLayer.paint(g);
	}
	
	/**
	 * Sets the viewer position for rendering
	 * @param p Viewer Position in pixel
	 */
	public void viewerPositionChanged() {

		// set layer position
		groundLayer.setPosition( position.fieldX * PIXELS_PER_FIELD - PIXELS_PER_FIELD/2
			+ Level.activeLevel.getViewTranslateX(),
			position.fieldY * PIXELS_PER_FIELD - PIXELS_PER_FIELD/2
			+ Level.activeLevel.getViewTranslateY());

		// set object positions
		for(int line = 0; line < staticObjects.length; line++) {
			for(int i=0; i<staticObjects[line].size(); i++) {
				((LevelObject)staticObjects[line].elementAt(i)).viewerPositionChanged();
			}
		}
		for(int line = 0; line < dynamicObjects.length; line++) {
			for(int i=0; i<dynamicObjects[line].size(); i++) {
				((LevelObject)dynamicObjects[line].elementAt(i)).viewerPositionChanged();
			}
		}
	}

	public void updateObjects() {
		for( int i=0; i<this.dynamicObjects.length; i++ ) {
			for( int j=0; j<dynamicObjects[i].size(); j++ ) {
				DynamicLevelObject obj = (DynamicLevelObject)(dynamicObjects[i].elementAt(j));
				obj.update();
			}
		}
	}
	
	/**
	 * Adds all enemys and breakables within the region to foundObjects
	 * @param topLeft
	 * @param bottomRight
	 * @param foundObjects
	 */
	public void getActiveDynamicsInRegion(Position topLeft, Position bottomRight, Class type, Vector foundObjects)
	{
		for( int i=0; i<this.dynamicObjects.length; i++ ) {
			for( int j=0; j<dynamicObjects[i].size(); j++ ) {
				if( !type.isAssignableFrom(dynamicObjects[i].elementAt(j).getClass()))
					continue;
				DynamicLevelObject dynamic = (DynamicLevelObject)dynamicObjects[i].elementAt(j);
				if( !dynamic.isInsideRegion(topLeft, bottomRight))
					continue;
				if( !dynamic.isEnabled() )
					continue;
				foundObjects.addElement(dynamic);
			}
		}
	}
	
	public boolean testCollisionWithDynamics(Sprite objectSprite) 
	{
		for( int i=0; i<this.dynamicObjects.length; i++ ) {
			for( int j=0; j<dynamicObjects[i].size(); j++ ) {
				DynamicLevelObject obj = (DynamicLevelObject)dynamicObjects[i].elementAt(j);
				if( !obj.isEnabled() || !obj.canCollide)
					continue;
				if( obj.collidesWith(objectSprite) )
					return true;
			}
		}
		return false;
	}
	
	/** 
	 * hits all triggers at position p
	 * @param p
	 */	
	public void hitTrigger(Position p) {
		Trigger trigger;
		for( int i=0; i<triggers.size(); i++ ) {
			trigger = (Trigger)triggers.elementAt(i);
			if( trigger.isEnabled() && trigger.getPosition().sameFieldAs(p) )
				((Trigger)triggers.elementAt(i)).hit();
		}
	}
	
	/** 
	 * uses all triggers at position p
	 * @param p
	 */
	public void useTrigger(Position p) {
		for( int i=0; i<triggers.size(); i++ ) {
			if( ((Trigger)triggers.elementAt(i)).getPosition().sameFieldAs(p) ) {
				Trigger t = (Trigger)triggers.elementAt(i);
				if(t.isEnabled()) t.use();	
			}
		}
	}

	/**
	 * 
	 * @param x x coordinate in fields
	 * @param y y coordinate in fields
	 * @return Field at position (x;y)
	 */
	public Field getField(int x, int y) {
		// x,y may not be relative to this block
		x %= Block.FIELDS_PER_BLOCK;
		y %= Block.FIELDS_PER_BLOCK;
		return fields[x][y];
	}	
	
	public void update() {
		updateObjects();
	}
	
	/**
	 * Saves object states. Only called by LevelLoader when this is going to be deleted. 
	 *
	 */
	public void onDelete() {
		DynamicLevelObject obj;
		for( int i=0; i<this.dynamicObjects.length; i++ ) {
			for( int j=0; j<dynamicObjects[i].size(); j++ ) {
				obj = (DynamicLevelObject)dynamicObjects[i].elementAt(j);
				Level.activeLevel.objectStates.saveState(obj);
				Level.activeLevel.removeObjectReference(obj);
			}
		}
		for( int i=0; i<this.staticObjects.length; i++ ) {
			for( int j=0; j<staticObjects[i].size(); j++ ) {
				Level.activeLevel.objectStates.saveState((LevelObject)(staticObjects[i].elementAt(j)));
			}
		}
	}

}
