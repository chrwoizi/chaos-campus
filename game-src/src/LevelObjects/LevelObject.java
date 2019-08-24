package LevelObjects;

import Block;
import Level;
import Position;
import Story.StoryManager;

import javax.microedition.lcdui.Graphics;

/**
 * An abstract class used for each object within a level 
 * @author Martin Wahnschaffe
 */
public class LevelObject {
	protected int pixelPositionX;
	protected int pixelPositionY;

	private Position position;
	protected Block block;
	
	protected boolean visible = false;
	
	public static final short NO_ID =-1;
	protected short id;
	public short getId() { return id; }
	
	private boolean enabled = true;
	public boolean isEnabled() {return enabled;}
	public void setEnabled(boolean b) {
		enabled = b;
		if( !b && (id == 178 || id == 179) )
			System.err.println("disabled " + id);
		if( enabled )
			StoryManager.getInstance().onObjectEnabled(id);
	}
	
	protected boolean dynamicLevelObject = false;
	public boolean isDynamicLevelObject() { return dynamicLevelObject; }
	
	public LevelObject(short _id, Position _position, Block parentBlock) {
		id = _id;
		block = parentBlock != null ? parentBlock : Level.activeLevel.getBlock(_position.fieldX, _position.fieldY);
		position = new Position(_position);
	}

	public Position getPosition() {
		Position result = new Position(position);
		return result; 
	}
	
	public boolean isVisible() { return visible; }
	
	public final void render(Graphics g) {
		if(enabled) {
			onRender(g);
		}
	}
	protected void onRender(Graphics g) {
		
	}
	
	public void setPosition(Position p) {
		if( position.fieldX == p.fieldX && position.fieldY == p.fieldY ) {
			// pixeloffset might have changed
			position.set(p); 		
		}
		else {
			if( p.getBlockX() != position.getBlockX()
					|| p.getBlockY() != position.getBlockY()) {
				// this has moved to another block.
				if(block != null) {
					if(dynamicLevelObject) {
						block.remDynamicObject(this);
					}
					else {
						block.remStaticObject(this);
					}
				}
				position.set(p);
				if(block != null) {
					if(dynamicLevelObject) {
						block = Level.activeLevel.addDynamicObject(this);
					}
					else {
						block = Level.activeLevel.addStaticObject(this);
					}
				}
			}		
			else if( p.getBlockRelativeFieldY() != position.getBlockRelativeFieldY()) {
				// this has moved to another line.
				if(block != null) {
					if(dynamicLevelObject) {
						block.remDynamicObject(this);
					}
					else {
						block.remStaticObject(this);
					}
				}
				position.set(p);
				if(block != null) {
					if(dynamicLevelObject) {
						block.addDynamicObject(this);
					}
					else {
						block.addStaticObject(this);
					}
				}
				else {
					if(dynamicLevelObject) {
						block = Level.activeLevel.addDynamicObject(this);
					}
					else {
						block = Level.activeLevel.addStaticObject(this);
					}
				}
			}
			else {
				position.set(p);
			}
		}

		if(block == null) {
			// this object will not exist anymore. delete it from level
			Level.activeLevel.removeObjectReference(this);
		}
		
		calcPixelPosition();
	}
	
	public void viewerPositionChanged() {
		calcPixelPosition();
	}
	
	protected void calcPixelPosition() {
		pixelPositionX = position.fieldX * Block.PIXELS_PER_FIELD
			+ position.pixelOffsetX + Level.activeLevel.getViewTranslateX();
		pixelPositionY = position.fieldY * Block.PIXELS_PER_FIELD
			+ position.pixelOffsetY + Level.activeLevel.getViewTranslateY();
			
		onPixelPositionChanged();
	}
	
	/**
	 * Creates LevelObjectState for storing in LevelObjectStates. 
	 * Can be overrided in derived classes for customization.
	 * Overrided methods must call super.fillState() with their state
	 * @return a new state object (contains position)
	 */
	public final LevelObjectState getState() {
		LevelObjectState result = new LevelObjectState();
		fillState(result);
		return result;
	}
	
	/**
	 * Fills LevelObjectState with position. 
	 * Should be used in overrided getState() and fillState() method to set some basic state data.
	 */
	protected void fillState(LevelObjectState state) {		
		state.position = new Position();
		state.position.set(getPosition());
		if( enabled )
			state.state |= LevelObjectState.ENABLED;
		if( visible )
			state.state |= LevelObjectState.VISIBLE;
	}
	
	/**
	 * Loads properties from a LevelObjectState. 
	 * If this method would be overrided, it must contain  super.setState(state);
	 * @param state
	 */
	public void setState(LevelObjectState state) {
		// retrieve data from state
		setPosition(state.position);
		setEnabled((state.state & LevelObjectState.ENABLED) != 0);
		visible = (state.state & LevelObjectState.VISIBLE) != 0;
	}
	
	/**
	 * Callback to derived types
	 *
	 */
	protected void onPixelPositionChanged() {
		
	}
}
