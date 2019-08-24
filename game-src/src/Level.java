import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

import LevelObjects.Breakable;
import LevelObjects.LevelObject;
import LevelObjects.LevelObjectState;
import LevelObjects.LevelObjectStates;
import LevelObjects.NPC;

import java.util.Hashtable;
import java.util.Vector;


/**
 * Manages a level with all graphics and game content 
 * @author Martin Wahnschaffe, Christian Woizischke
 */

//TODO: Singelton
public class Level {
	
	private LevelLoader levelLoader;

	private int viewTranslateX;
	public int getViewTranslateX() {
		return viewTranslateX;
	}
	
	private int viewTranslateY;
	public int getViewTranslateY() {
		return viewTranslateY;
	}
	
	private Image groundTileset;
	public Image getTileset() { 
		return groundTileset; 
	}

	public static final byte PRELOAD_BLOCKS = 2;
	public static final byte PRELOAD_BLOCKS_AT_DISTANCE = 1;
	
	/**
	 * Possible directions for movement 
	 */
	public static final byte dirNone = 0;
	public static final byte dirUp = 1;
	public static final byte dirRight = 2;
	public static final byte dirDown = 4;
	public static final byte dirLeft = 8;	
	
	public static Level activeLevel = null;
	
	public byte widthInBlocks;
	public byte heightInBlocks;
	
	public Position playerStart = new Position(4,8);
	
	public String[] levelNames = null;
	public byte levelId = -1;
	
	public Block[][] blocks;

	// all blocks inside this rect are visible. 1 unit = 1 block id
	private Rect blocksVisible;
	// all blocks inside this rect are loaded. 1 unit = 1 block id
	private Rect blocksInMemory;
	
	// states of all objects
	public LevelObjectStates objectStates;
	
	// all objects in the level
	private Hashtable objects;
	public void addObjectReference(LevelObject obj) {
		short id = obj.getId();
		objects.put(new Short(id), obj);
		biggestObjectIdAround = (short)Math.max(biggestObjectIdAround, id);
	}
	public void removeObjectReference(LevelObject obj) {
		short id = obj.getId();
		Level.activeLevel.objects.remove(new Short(id));
	}
	public boolean objectExists(short id) {
		if(id == -1) return false;
		return objects.containsKey(new Short(id));
	}
	
	// Biggest object id ever seen. used by LevelObjectStates to save the states of all objects
	private short biggestObjectIdAround = 700;
	public short getBiggestObjectIdAround() { return biggestObjectIdAround; }
	
	// When set to true, an extra thread will be used to load the blocks
	private boolean multiThreadedLoading = true;
	
	private static final int MAX_OBJECT_COUNT = 1000;

	private Level() {};
	/**
	 * Constructor of Level.
	 *
	 */
	public Level(String[] levelNames) {
		this.levelNames = levelNames;
		if( this.levelNames == null )
			new Error("public Level(String[] levelNames): no level names passed");
		activeLevel = this;
		// objectStates will save some properties of objects that will be deleted dynamically.
		// argument is the max number of objects. 
		objectStates = new LevelObjectStates(MAX_OBJECT_COUNT); 
		objects = new Hashtable(MAX_OBJECT_COUNT);
		
		viewTranslateX = viewTranslateY = 0;		
		
		blocksVisible = new Rect(0,0,0,0);
		blocksInMemory = new Rect(0,0,0,0);
		
		// initialize level loader. the level loader loads blocks in a seperate thread.
		levelLoader = new LevelLoader();
	}
	
	public byte getLevelId() { return levelId; }
	public String getName() { return levelNames[levelId-1]; }
	
	private void clear() {
		blocks = null;
		widthInBlocks = 0;
		heightInBlocks = 0;
		blocksInMemory.set(0,0,0,0);
		objectStates = new LevelObjectStates(200); 
		objects = new Hashtable(200);
	}
	
	public void loadWithLoadingScreen(byte id, Position startPos) {
		levelId = id;
		GameManager.getInstance().changeState(GameManager.STATE_LOADING);
		IODevice.getInstance().repaint();
		System.gc();
		levelLoader.loadNewLevel(id, startPos);
	}
	
	/**
	 * Load the level
	 * @param levelName
	 */
	public boolean load(byte id, Position startPos) {
		clear();
		
		String name = levelNames[id-1];
		levelId = id;
		
		// load the tileset
		try {
			groundTileset = Image.createImage("/level/" + name + ".png");
	    } catch (Exception e) {
	        new Error("ground tileset", e);
	        return false;
	    }

	    // load the map (loads map size and creates blocks)
	    if(!levelLoader.loadMapHeader()) {
	    	return false;
	    }

	    // TODO load object states
	    // not done because of loading time (needed upto 60 seconds)
	    // might cause errors when using triggers that call
	    // objects which have not been loaded yet, because
	    // the player wasn't near them
	    /*if(!levelLoader.loadObjectStates()) {
	    	return false;
	    }*/

		// bring the player to the start point
		if(startPos != null) {
			Player.getInstance().teleport(startPos);
		}
		else {
			Player.getInstance().teleport(Level.activeLevel.playerStart);
		}

		// the level has changed. update block visibilities and positions
		onPlayerFieldPositionChanged();
		onPlayerPixelPositionChanged();

		IdGenerator.init(this.getBiggestObjectIdAround());

		return true;
	}
	
	/**
	 * Resets the level.
	 *
	 */
	public void reset() {
		load(levelId, null);
	}
	
	/**
	 * 
	 * @param x x coordinate in fields
	 * @param y y coordinate in fields
	 * @return Block at position (x;y)
	 */
	public Block getBlock(int x, int y) {
		return blocks[x/Block.FIELDS_PER_BLOCK][y/Block.FIELDS_PER_BLOCK];
	}

	/**
	 * 
	 * @param x x coordinate in fields
	 * @param y y coordinate in fields
	 * @return Field at position (x;y)
	 */
	public Field getField(int x, int y) {
		return getBlock(x,y).getField( x, y);
	}

	public Position makePosition(short blockId, byte relativeFieldX, byte relativeFieldY) {
		return new Position(
				(blockId%widthInBlocks)*Block.FIELDS_PER_BLOCK + relativeFieldX,
				(blockId/widthInBlocks)*Block.FIELDS_PER_BLOCK + relativeFieldY
		);
	}
	
	/*
	public Position makePosition(short absoluteFieldX, short absoluteFieldY) {
		return new Position(
				absoluteFieldX,
				absoluteFieldY*widthInBlocks*Block.FIELDS_PER_BLOCK
		);
	}*/
	
	/**
	 * Renders the level. 
	 * Called by GameManager
	 * @param g The graphics context to draw on.
	 */
	public void render(Graphics g) {
		// render all visible blocks
		for(int y = blocksVisible.top; y <= blocksVisible.bottom; y++) {
			for(int x = blocksVisible.left; x <= blocksVisible.right; x++) {
				if( blocks[x][y] != null )
					blocks[x][y].render(g);
			}		
		}

		// render all objects line by line
		for(int y = blocksVisible.top; y <= blocksVisible.bottom; y++) {
			for(int line = 0; line < Block.FIELDS_PER_BLOCK; line++) {
				// render all objects in the line
				for(int x = blocksVisible.left; x <= blocksVisible.right; x++) {
					if( blocks[x][y] != null )
						blocks[x][y].renderObjectLine(g, line);
				}
				// render the player
				if( y == Player.getInstance().getPosition().getBlockY() && line == Player.getInstance().getPosition().getBlockRelativeFieldY() ) {
					Player.getInstance().render(g);
				}
			}				
		}	
	}

	/**
	 * Updates the level
	 * Frequently called by GameManager
	 */
	public void update() {

		// update blocks
		for(int y = blocksVisible.top; y <= blocksVisible.bottom; y++) {
			for(int x = blocksVisible.left; x <= blocksVisible.right; x++) {
				if( blocks[x][y] != null )
					blocks[x][y].update();
			}				
		}
	}
	
	/**
	 * Sets viewTranslate and notifies the blocks
	 * 
	 */
	public void onPlayerPixelPositionChanged() {
		viewTranslateX = -(Player.getInstance().getPosition().fieldX*Block.PIXELS_PER_FIELD
				+ Player.getInstance().getPosition().pixelOffsetX)// +Block.PIXELS_PER_FIELD/2)
				+ IODevice.getInstance().getWidth()/2;
		viewTranslateY = -(Player.getInstance().getPosition().fieldY*Block.PIXELS_PER_FIELD
				+ Player.getInstance().getPosition().pixelOffsetY)// +Block.PIXELS_PER_FIELD/2)
				+ IODevice.getInstance().getHeight()/2;

		// update block transformations
		for(int y = blocksVisible.top; y <= blocksVisible.bottom; y++) {				
			for(int x = blocksVisible.left; x <= blocksVisible.right; x++) {
				if( blocks[x][y] != null )
					blocks[x][y].viewerPositionChanged();
			}
		}
	}
	
	/**
	 * Called when the player moved from one field to another.
	 * Calculates visible blocks, makes sure that near blocks are preloaded.
	 * 
	 */
	public void onPlayerFieldPositionChanged() {
		calculateVisibleBlocks();
		preloadBlocks();

		// test hit-triggers
		Position p = Player.getInstance().getPosition();
		if( blocks[p.getBlockX()][p.getBlockY()] != null ) {
			blocks[p.getBlockX()][p.getBlockY()].hitTrigger(p);
		}
	}
	
	/**
	 * Called when the player changes his direction
	 * Calles all triggers on the players field
	 * 
	 */
	public void onPlayerDirectionChanged() {
		// test hit-triggers
		Position p = Player.getInstance().getPosition();
		if( blocks[p.getBlockX()][p.getBlockY()] != null ) {
			blocks[p.getBlockX()][p.getBlockY()].hitTrigger(p);
		}
	}
	
	/** 
	 * Player standing on p wants to use somethin
	 * @param p
	 */
	public void onPlayerUses(Position p) {
		if( blocks[p.getBlockX()][p.getBlockY()] != null ) {
			blocks[p.getBlockX()][p.getBlockY()].useTrigger(p);
		}
	}

	
	public void onPlayerTeleport() {
		// get visible blocks
		calculateVisibleBlocks();
		
		// blocks to remove
		int remidswidth = blocksInMemory.right - blocksInMemory.left +1;
		int remidsheight = blocksInMemory.bottom - blocksInMemory.top +1;
		short[] remids = new short[remidswidth*remidsheight];
		int element = 0;
		for(int y = blocksInMemory.top; y <= blocksInMemory.bottom; y++) {
			for(int x = blocksInMemory.left; x <= blocksInMemory.right; x++) {
				if(x < 0 || x >= widthInBlocks || y < 0 || y >= heightInBlocks) {
					continue; // out of bounds
				}
				remids[element] = (short)(y*widthInBlocks + x);
				element++;
			}	
		}
		
		// initialize the rect and blocks
		Rect newBlocksInMemory = new Rect(
				blocksVisible.left-PRELOAD_BLOCKS_AT_DISTANCE - PRELOAD_BLOCKS,
				blocksVisible.right+PRELOAD_BLOCKS_AT_DISTANCE + PRELOAD_BLOCKS,
				blocksVisible.top-PRELOAD_BLOCKS_AT_DISTANCE - PRELOAD_BLOCKS,
				blocksVisible.bottom+PRELOAD_BLOCKS_AT_DISTANCE + PRELOAD_BLOCKS
		);
		// make sure the rect is in bounds
		if(newBlocksInMemory.left < 0) newBlocksInMemory.left = 0;
		if(newBlocksInMemory.left >= widthInBlocks) newBlocksInMemory.left = (short)(widthInBlocks-1);
		if(newBlocksInMemory.right < 0) newBlocksInMemory.right = 0;
		if(newBlocksInMemory.right >= widthInBlocks) newBlocksInMemory.right = (short)(widthInBlocks-1);
		if(newBlocksInMemory.top < 0) newBlocksInMemory.top = 0;
		if(newBlocksInMemory.top >= heightInBlocks) newBlocksInMemory.top = (short)(heightInBlocks-1);
		if(newBlocksInMemory.bottom < 0) newBlocksInMemory.bottom = 0;
		if(newBlocksInMemory.bottom >= heightInBlocks) newBlocksInMemory.bottom = (short)(heightInBlocks-1);

		// blocks to load
		int addidswidth = newBlocksInMemory.right - newBlocksInMemory.left +1;
		int addidsheight = newBlocksInMemory.bottom - newBlocksInMemory.top +1;
		short[] addids = new short[addidswidth*addidsheight];
		element = 0;
		for(int y = newBlocksInMemory.top; y <= newBlocksInMemory.bottom; y++) {
			for(int x = newBlocksInMemory.left; x <= newBlocksInMemory.right; x++) {
				addids[element] = (short)(y*widthInBlocks + x);
				element++;
			}	
		}
		
		/*
		// REMOVE Blocks
		Vector remids = new Vector();
		// left
		if( blocksInMemory.left < newBlocksInMemory.left ) {
			for( int x=blocksInMemory.left; x < newBlocksInMemory.left; x++ )
				for( int y = blocksInMemory.top; y<= blocksInMemory.bottom; y++ )
					remids.addElement(new Short((short)(y*widthInBlocks + x)));
		}
		// right
		if( blocksInMemory.right > newBlocksInMemory.right ) {
			for( int x=blocksInMemory.right; x > newBlocksInMemory.right; x-- )
				for( int y = blocksInMemory.top; y<= blocksInMemory.bottom; y++ )
					remids.addElement(new Short((short)(y*widthInBlocks + x)));
		}
		// top
		if( blocksInMemory.top < newBlocksInMemory.top ) {
			for( int y=blocksInMemory.top; y < newBlocksInMemory.top; y++ )
				for( int x = blocksInMemory.left; x<= blocksInMemory.right; x++ )
					remids.addElement(new Short((short)(y*widthInBlocks + x)));
		}
		// bottom
		if( blocksInMemory.bottom > newBlocksInMemory.bottom ) {
			for( int y=blocksInMemory.bottom; y > newBlocksInMemory.bottom; y-- )
				for( int x = blocksInMemory.left; x<= blocksInMemory.right; x++ )
					remids.addElement(new Short((short)(y*widthInBlocks + x)));
		}*/

		// load and remove the blocks
		levelLoader.updateBlocks(addids, remids, false);
		
		// update this' rect
		blocksInMemory = newBlocksInMemory;
		
		// player's pixel position has changed
		onPlayerPixelPositionChanged();
	}

	
	private void calculateVisibleBlocks()
	{
		// screen size
		int hsw = IODevice.getInstance().getWidth()/2;
		int hsh = IODevice.getInstance().getHeight()/2;
		
		// view position in pixel
		int vppx = Player.getInstance().getPosition().fieldX * Block.PIXELS_PER_FIELD;
		int vppy = Player.getInstance().getPosition().fieldY * Block.PIXELS_PER_FIELD;

		// screen borders in level coordinates		
		int left = vppx - hsw;
		int top = vppy - hsh;		
		int right = vppx + hsw;		
		int bottom = vppy + hsh;

		// screen corners are inside the following block rows/columns
		int left_block_col = (left-Block.PIXELS_PER_FIELD) / Block.PIXELS_PER_BLOCK;
		int top_block_row = (top-Block.PIXELS_PER_FIELD) / Block.PIXELS_PER_BLOCK;
		int right_block_col = (right+Block.PIXELS_PER_FIELD) / Block.PIXELS_PER_BLOCK;
		int bottom_block_row = (bottom+Block.PIXELS_PER_FIELD) / Block.PIXELS_PER_BLOCK;
		
		// make sure the rows and columns are inside the level
		if(left_block_col < 0) left_block_col = 0;
		if(top_block_row < 0) top_block_row = 0;
		if(right_block_col >= widthInBlocks) right_block_col = widthInBlocks-1;
		if(bottom_block_row >= heightInBlocks) bottom_block_row = heightInBlocks-1;
		
		// set the rect
		blocksVisible.set(
				left_block_col,
				right_block_col,
				top_block_row,
				bottom_block_row);
	}
	
	/**
	 * preloads the next blocks if the visibility rect comes near to the border of the blocksInMemory rect
	 * use PRELOAD_BLOCKS_AT_DISTANCE to set the minimum distance between visibility rect and blocksInMemory rect. must be >=1 .
	 * use PRELOAD_BLOCKS to set the number of rows/columns to preload at once. must be >=1 .
	 *
	 */
	private void preloadBlocks() {
		// check if any blocks have been loaded yet
		if(blocksInMemory.left == 0 && blocksInMemory.right == 0 && blocksInMemory.top == 0 && blocksInMemory.bottom == 0)	{
			// initialize the rect and blocks
			Rect newBlocksInMemory = new Rect(
					blocksVisible.left-PRELOAD_BLOCKS_AT_DISTANCE - PRELOAD_BLOCKS,
					blocksVisible.right+PRELOAD_BLOCKS_AT_DISTANCE + PRELOAD_BLOCKS,
					blocksVisible.top-PRELOAD_BLOCKS_AT_DISTANCE - PRELOAD_BLOCKS,
					blocksVisible.bottom+PRELOAD_BLOCKS_AT_DISTANCE + PRELOAD_BLOCKS
			);
			// make sure the rect is in bounds
			if(newBlocksInMemory.left < 0) newBlocksInMemory.left = 0;
			if(newBlocksInMemory.left >= widthInBlocks) newBlocksInMemory.left = (short)(widthInBlocks-1);
			if(newBlocksInMemory.right < 0) newBlocksInMemory.right = 0;
			if(newBlocksInMemory.right >= widthInBlocks) newBlocksInMemory.right = (short)(widthInBlocks-1);
			if(newBlocksInMemory.top < 0) newBlocksInMemory.top = 0;
			if(newBlocksInMemory.top >= heightInBlocks) newBlocksInMemory.top = (short)(heightInBlocks-1);
			if(newBlocksInMemory.bottom < 0) newBlocksInMemory.bottom = 0;
			if(newBlocksInMemory.bottom >= heightInBlocks) newBlocksInMemory.bottom = (short)(heightInBlocks-1);

			// load all blocks of the rect
			// create id array for levelLoader
			int addidswidth = newBlocksInMemory.right - newBlocksInMemory.left +1;
			int addidsheight = newBlocksInMemory.bottom - newBlocksInMemory.top +1;
			short[] addids = new short[addidswidth*addidsheight];
			int element = 0;
			for(int y = newBlocksInMemory.top; y <= newBlocksInMemory.bottom; y++) {
				for(int x = newBlocksInMemory.left; x <= newBlocksInMemory.right; x++) {
					addids[element] = (short)(y*widthInBlocks + x);
					element++;
				}	
			}
			
			// load the blocks
			levelLoader.updateBlocks(addids, null, false);
			
			// update this' rect
			blocksInMemory = newBlocksInMemory;
		}
		else {
			// check if the viewer comes near the unloaded area
			if((blocksVisible.left-PRELOAD_BLOCKS_AT_DISTANCE) >= 0 
					&& (blocksVisible.left-PRELOAD_BLOCKS_AT_DISTANCE) < blocksInMemory.left) {
				// view rect moved left
				// load a maximum of PRELOAD_BLOCKS Blocks at the left of blocksInMemory rect 
				// and remove a maximum of PRELOAD_BLOCKS Blocks at the right of blocksInMemory rect 
				Rect newBlocksInMemory = new Rect(
						blocksVisible.left - PRELOAD_BLOCKS,
						blocksVisible.right + PRELOAD_BLOCKS,
						blocksInMemory.top,
						blocksInMemory.bottom
				);
				// make sure rect is in bounds
				if(newBlocksInMemory.left < 0) newBlocksInMemory.left = 0;
				if(newBlocksInMemory.right >= widthInBlocks) newBlocksInMemory.right = (short)(widthInBlocks-1);

				int inmemrectheight = blocksInMemory.bottom - blocksInMemory.top +1;

				// prepare id array for levelLoader
				int addidswidth = blocksInMemory.left - newBlocksInMemory.left;
				short[] addids = new short[addidswidth*inmemrectheight];
				int element = 0;
				for(int y = blocksInMemory.top; y <= blocksInMemory.bottom; y++) {
					for(int x = newBlocksInMemory.left; x < blocksInMemory.left; x++) {
						addids[element] = (short)(y*widthInBlocks + x);
						element++;
					}	
				}

				// prepare id array for levelLoader
				int remidswidth = blocksInMemory.right - newBlocksInMemory.right;
				short[] remids = new short[remidswidth*inmemrectheight]; 
				element = 0;
				for(int y = blocksInMemory.top; y <= blocksInMemory.bottom; y++) {
					for(int x = newBlocksInMemory.right+1; x <= blocksInMemory.right; x++) {
						remids[element] = (short)(y*widthInBlocks + x);
						element++;
					}	
				}

				// load/unload the blocks
				levelLoader.updateBlocks(addids, remids, multiThreadedLoading);

				// update this' rect
				blocksInMemory = newBlocksInMemory;
			}
			else if((blocksVisible.right+PRELOAD_BLOCKS_AT_DISTANCE) < widthInBlocks 
					&& (blocksVisible.right+PRELOAD_BLOCKS_AT_DISTANCE) > blocksInMemory.right) {
				// view rect moved right
				// load a maximum of PRELOAD_BLOCKS Blocks at the right of blocksInMemory rect 
				// and remove a maximum of PRELOAD_BLOCKS Blocks at the left of blocksInMemory rect 
				Rect newBlocksInMemory = new Rect(
						blocksVisible.left - PRELOAD_BLOCKS,
						blocksVisible.right + PRELOAD_BLOCKS,
						blocksInMemory.top,
						blocksInMemory.bottom
				);
				// make sure rect is in bounds
				if(newBlocksInMemory.left < 0) newBlocksInMemory.left = 0;
				if(newBlocksInMemory.right >= widthInBlocks) newBlocksInMemory.right = (short)(widthInBlocks-1);

				int inMemRectHeight = blocksInMemory.bottom - blocksInMemory.top +1;
				
				// prepare id array for levelLoader
				int addIdsWidth = newBlocksInMemory.right - blocksInMemory.right;
				short[] addids = new short[addIdsWidth*inMemRectHeight];
				int element = 0;
				for(int y = blocksInMemory.top; y <= blocksInMemory.bottom; y++) {
					for(int x = blocksInMemory.right+1; x <= newBlocksInMemory.right; x++) {
						addids[element] = (short)(y*widthInBlocks + x);
						element++;
					}	
				}

				// prepare id array for levelLoader
				int remIdsWidth = newBlocksInMemory.left - blocksInMemory.left;
				short[] remids = new short[remIdsWidth*inMemRectHeight]; 
				element = 0;
				for(int y = blocksInMemory.top; y <= blocksInMemory.bottom; y++) {
					for(int x = blocksInMemory.left; x < newBlocksInMemory.left; x++) {
						remids[element] = (short)(y*widthInBlocks + x);
						element++;
					}	
				}

				// load/unload the blocks
				levelLoader.updateBlocks(addids, remids, multiThreadedLoading);

				// update this' rect
				blocksInMemory = newBlocksInMemory;
			}
	
			
			if((blocksVisible.top-PRELOAD_BLOCKS_AT_DISTANCE) >= 0 
					&& (blocksVisible.top-PRELOAD_BLOCKS_AT_DISTANCE) < blocksInMemory.top) {
				// view rect moved up
				// load a maximum of PRELOAD_BLOCKS Blocks at the top of blocksInMemory rect 
				// and remove a maximum of PRELOAD_BLOCKS Blocks at the bottom of blocksInMemory rect 
				Rect newBlocksInMemory = new Rect(
						blocksInMemory.left,
						blocksInMemory.right,
						blocksVisible.top - PRELOAD_BLOCKS,
						blocksVisible.bottom + PRELOAD_BLOCKS
				);
				// make sure rect is in bounds
				if(newBlocksInMemory.top < 0) newBlocksInMemory.top = 0;
				if(newBlocksInMemory.bottom >= heightInBlocks) newBlocksInMemory.bottom = (short)(heightInBlocks-1);

				int inmemrectwidth = blocksInMemory.right - blocksInMemory.left +1;

				// prepare id array for levelLoader
				int addIdsHeight = blocksInMemory.top - newBlocksInMemory.top;
				short[] addids = new short[addIdsHeight*inmemrectwidth];
				int element = 0;
				for(int y = newBlocksInMemory.top; y < blocksInMemory.top; y++) {
					for(int x = blocksInMemory.left; x <= blocksInMemory.right; x++) {
						addids[element] = (short)(y*widthInBlocks + x);
						element++;
					}	
				}

				// prepare id array for levelLoader
				int remIdsHeight = blocksInMemory.bottom - newBlocksInMemory.bottom;
				short[] remids = new short[remIdsHeight*inmemrectwidth]; 
				element = 0;
				for(int y = newBlocksInMemory.bottom+1; y <= blocksInMemory.bottom; y++) {
					for(int x = blocksInMemory.left; x <= blocksInMemory.right; x++) {
						remids[element] = (short)(y*widthInBlocks + x);
						element++;
					}	
				}

				// load/unload the blocks
				levelLoader.updateBlocks(addids, remids, multiThreadedLoading);

				// update this' rect
				blocksInMemory = newBlocksInMemory;
			}
			else if((blocksVisible.bottom+PRELOAD_BLOCKS_AT_DISTANCE) < heightInBlocks 
					&& (blocksVisible.bottom+PRELOAD_BLOCKS_AT_DISTANCE) > blocksInMemory.bottom) {
				// view rect moved down
				// load a maximum of PRELOAD_BLOCKS Blocks at the bottom of blocksInMemory rect 
				// and remove a maximum of PRELOAD_BLOCKS Blocks at the top of blocksInMemory rect 
				Rect newBlocksInMemory = new Rect(
						blocksInMemory.left,
						blocksInMemory.right,
						blocksVisible.top - PRELOAD_BLOCKS,
						blocksVisible.bottom + PRELOAD_BLOCKS
				);
				// make sure rect is in bounds
				if(newBlocksInMemory.top < 0) newBlocksInMemory.top = 0;
				if(newBlocksInMemory.bottom >= heightInBlocks) newBlocksInMemory.bottom = (short)(heightInBlocks-1);

				int inmemrectwidth = blocksInMemory.right - blocksInMemory.left +1;

				// prepare id array for levelLoader
				int addIdsHeight = newBlocksInMemory.bottom - blocksInMemory.bottom;
				short[] addids = new short[addIdsHeight*inmemrectwidth];
				int element = 0;
				for(int y = blocksInMemory.bottom+1; y <= newBlocksInMemory.bottom; y++) {
					for(int x = blocksInMemory.left; x <= blocksInMemory.right; x++) {
						addids[element] = (short)(y*widthInBlocks + x);
						element++;
					}	
				}

				// prepare id array for levelLoader
				int remIdsHeight = newBlocksInMemory.top - blocksInMemory.top;
				short[] remids = new short[remIdsHeight*inmemrectwidth]; 
				element = 0;
				for(int y = blocksInMemory.top; y < newBlocksInMemory.top; y++) {
					for(int x = blocksInMemory.left; x <= blocksInMemory.right; x++) {
						remids[element] = (short)(y*widthInBlocks + x);
						element++;
					}	
				}

				// load/unload the blocks
				levelLoader.updateBlocks(addids, remids, multiThreadedLoading);

				// update this' rect
				blocksInMemory = newBlocksInMemory;
			}
		}
	}
	
	/**
	 * Adds a static object to the level. Internally it is being redirected to the according Block
	 * @param obj The static object
	 * @return The block where the object has been added to
	 */
	public Block addStaticObject(LevelObject obj) {
		Block block = blocks[obj.getPosition().getBlockX()][obj.getPosition().getBlockY()];
		if(block != null) {
			block.addStaticObject(obj);
			return block;
		}
		else {
			return null;
		}
	}

	/**
	 * Adds a moving object to the level. Internally it is being redirected to the according Block
	 * @param obj The moving object
	 * @return The block where the object has been added to
	 */
	public Block addDynamicObject(LevelObject obj) {
		Block block = blocks[obj.getPosition().getBlockX()][obj.getPosition().getBlockY()];
		if(block != null) {
			block.addDynamicObject(obj);
			return block;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Returns all active dynamic objects that lie within the passed region
	 * and are instances of the passed Class
	 * @param topLeft top-left edge of testing-rectangle
	 * @param bottomRight bottom-right edge of testing-rectangle
	 * @return a vector of all affected objects	 * 
	 */
	public Vector getActiveDynamicsInRegion(Position topLeft, Position bottomRight, Class type)
	{
		Vector foundObjects = new Vector();
		Rect blocksAffected = new Rect(
				topLeft.getBlockX(),
				bottomRight.getBlockX(),
				topLeft.getBlockY(),
				bottomRight.getBlockY());
		
		for(int y = blocksAffected.top; y <= blocksAffected.bottom; y++) {
			for(int x = blocksAffected.left; x <= blocksAffected.right; x++) {
				blocks[x][y].getActiveDynamicsInRegion(topLeft, bottomRight, type, foundObjects);
			}				
		}
		return foundObjects;
	}

	/**
	 * Returns the neartest lving NPC that lies within the passed region
	 * @param topLeft top-left edge of testing-rectangle
	 * @param bottomRight bottom-right edge of testing-rectangle
	 * @param reference refernce position for distance calculation
	 * @return a vector of all affected objects	 * 
	 */
	public NPC getNearestNpc(Position topLeft, Position bottomRight, Position reference)
	{
		Vector foundNpcs = getActiveDynamicsInRegion(topLeft, bottomRight, NPC.class);
		int nearest = 0;
		short minDistance = 9999;
		for( int i=0; i<foundNpcs.size(); i++) {
			NPC enemy = (NPC)foundNpcs.elementAt(i);
			if( enemy.getLifePoints() <= 0 )
				continue;
			short distance = enemy.getPosition().maxPixelDistanceTo(reference);
			if (distance < minDistance) {
				minDistance = distance;
				nearest = i;
			}
		}
		if( minDistance == 9999 )
			return null;
		return (NPC)foundNpcs.elementAt(nearest);
	}
	
	/**
	 * Destroyes the nearest breakable (eg a crate) within the passed region
	 * @param topLeft top-left edge of testing-rectangle
	 * @param bottomRight bottom-right edge of testing-rectangle
	 * @param reference refernce position for distance calculation
	 */
	public void destroyNearestBreakable(Position topLeft, Position bottomRight, Position reference)
	{
		Vector foundBreakables = getActiveDynamicsInRegion(topLeft, bottomRight, Breakable.class);
		int nearest = 0;
		short minDistance = 9999;
		for( int i=0; i<foundBreakables.size(); i++) {
			Breakable breakable = (Breakable)foundBreakables.elementAt(i);
			short distance = breakable.getPosition().maxPixelDistanceTo(reference);
			if (distance < minDistance) {
				minDistance = distance;
				nearest = i;
			}
		}
		if( minDistance == 9999 )
			return;
		((Breakable)foundBreakables.elementAt(nearest)).destroy();
	}

	/**
	 * Returns the object with the id or null if it does not exist or has not been loaded.
	 * @param id
	 * @return
	 */
	public LevelObject getObject(short id) {
		return (LevelObject)objects.get(new Short(id));
	}
	
	/**
	 * Returns the saved state of the object with the id. 
	 * This state may be modified. The object loads this state when it is created.
	 * Returns null if the object exists!! Use getObject in this case.
	 * @param id
	 * @return
	 */
	public LevelObjectState getObjectState(short id) {
		if(id == LevelObject.NO_ID) {
			new Error("Somebody wanted the state for object id " + LevelObject.NO_ID + ". This should not happen.");
			return null;
		}
		
		LevelObject obj = getObject(id);
		if(obj != null) {
			return obj.getState();
		}
		else {
			// object is not in memory. get saved state
			return (LevelObjectState)objectStates.getState(id);
		}
	}
	

	/**
	 * Tests if it's possible to leave the old field in the wanted direction  
	 * @param p
	 * @param direction
	 * @param COLLISION_DELTA
	 * @return
	 */
	// TODO: Methode umbenennen
	private byte getMaxMovement(Position p, byte direction, byte wantedMovement) {
		if( wantedMovement > 16 )
			new Error("movement of more than 16 pixels is not supported");

		Position pMoved = new Position(p);
		switch(direction) {
		case dirLeft:
			pMoved.movePixels((short)-wantedMovement, (short)0);
			if( pMoved.fieldX != p.fieldX ) {
				if( getField(p.fieldX, p.fieldY).testCollision(Field.COLLISION_LEFT) ) {
					wantedMovement = (byte)(8 + p.pixelOffsetX);
				}
			}
			break;
		case dirRight:
			pMoved.movePixels((short)wantedMovement, (short)0);
			if( pMoved.fieldX != p.fieldX ) {
				if( getField(p.fieldX, p.fieldY).testCollision(Field.COLLISION_RIGHT) ) {
					wantedMovement = (byte)(8 - p.pixelOffsetX);
				}
			}
			break;
		case dirUp:
			pMoved.movePixels((short)0, (short)-wantedMovement);
			if( pMoved.fieldY != p.fieldY ) {
				if( getField(p.fieldX, p.fieldY).testCollision(Field.COLLISION_TOP) ) {
					wantedMovement = (byte)(8 + p.pixelOffsetY);
				}
			}
			break;
		case dirDown:
			pMoved.movePixels((short)0, (short)wantedMovement);
			if( pMoved.fieldY != p.fieldY ) {
				if( getField(p.fieldX, p.fieldY).testCollision(Field.COLLISION_BOTTOM) ) {
					wantedMovement = (byte)(8 - p.pixelOffsetY);
				}
			}
			break;
		}
		return wantedMovement;
	}
	
	/**
	 * Tests if the objects sprite hits any other sprites
	 * @param p new position
	 * @param objectSprite sprite at the new position
	 * @return
	 */
	
	private boolean testCollisionWithDynamics(Position p, Sprite objectSprite) 
	{
		if( blocks[p.getBlockX()][p.getBlockY()] == null ) {
			System.err.println("block not in memory");
			return true;
		}
		// parent block
		if( blocks[p.getBlockX()][p.getBlockY()].testCollisionWithDynamics(objectSprite) )
			return true;
		
		
		// right border
		if( p.getBlockRelativeFieldX() == Block.FIELDS_PER_BLOCK-1 && blocks[p.getBlockX()+1][p.getBlockY()] != null ) {
			if( blocks[p.getBlockX()+1][p.getBlockY()].testCollisionWithDynamics(objectSprite) )
				return true;
		}
		// left border
		else if( p.getBlockRelativeFieldX() == 0 && blocks[p.getBlockX()-1][p.getBlockY()] != null ) {
			if( blocks[p.getBlockX()-1][p.getBlockY()].testCollisionWithDynamics(objectSprite) )
				return true;
		}
		// bottom border
		if( p.getBlockRelativeFieldY() == Block.FIELDS_PER_BLOCK-1 && blocks[p.getBlockX()][p.getBlockY()+1] != null ) {
			if( blocks[p.getBlockX()][p.getBlockY()+1].testCollisionWithDynamics(objectSprite) )
				return true;
		}
		// top border
		else if( p.getBlockRelativeFieldY() == 0 && blocks[p.getBlockX()][p.getBlockY()-1] != null ) {
			if( blocks[p.getBlockX()][p.getBlockY()-1].testCollisionWithDynamics(objectSprite) )
				return true;
		}
		
		return false;
	}
	
	/**
	 * Moves object.
	 * @param position The position of the object. This will be changed by this method if there is no collision.
	 * @param objectSprite A sprite for collision with dynamics. May be null.
	 * @param direction The direction where to move the object
	 * @param speed The speed in pixels
	 * @return 0 if the position has not been changed. 1 if the pixel position changed. 2 if the field position changed. 3 if both changed.
	 */
	public byte move(Position position, Sprite objectSprite, byte direction, byte speed, byte collisionRadius) 
	{
		Position oldPos = new Position(position);
		
		// move up
		if( (direction & Level.dirUp) != 0 && (direction & Level.dirDown) == 0 ) {
			// just look at the top border of the moved object
			Position posBorderLow = new Position(position);
			Position posBorderHigh = new Position(position);
			posBorderLow.movePixels((short)-collisionRadius, (short)-collisionRadius);
			posBorderHigh.movePixels((short)collisionRadius, (short)-collisionRadius);
			// find out how many pixel we are allowed to move
			byte movement = getMaxMovement(posBorderLow, Level.dirUp, speed);
			movement = getMaxMovement(posBorderHigh, Level.dirUp, movement);
			// move
			if( movement > 0 ) {
				position.movePixels((short)0, (short)-movement);
				// test against dynamics
				if( objectSprite != null ) {
					objectSprite.move(0, -movement);
					// or revert the movement?
					if( testCollisionWithDynamics(position, objectSprite) 
							|| Player.getInstance().testCollisionWithSprite(objectSprite) ) {
						position.movePixels((short)0, (short)movement);
						objectSprite.move(0, movement);
					}
				}
			}
		}
		// move down
		else if( (direction & Level.dirDown) != 0 && (direction & Level.dirUp) == 0 ) {
			// just look at the bottom border of the moved object
			Position posBorderLow = new Position(position);
			Position posBorderHigh = new Position(position);
			posBorderLow.movePixels((short)-collisionRadius, (short)collisionRadius);
			posBorderHigh.movePixels((short)collisionRadius, (short)collisionRadius);
			// find out how many pixel we are allowed to move
			byte movement = getMaxMovement(posBorderLow, Level.dirDown, speed);
			movement = getMaxMovement(posBorderHigh, Level.dirDown, movement);
			// move
			if( movement > 0 ) {
				position.movePixels((short)0, (short)movement);
				// test against dynamics
				if( objectSprite != null ) {
					objectSprite.move(0, movement);
					// or revert the movement?
					if( testCollisionWithDynamics(position, objectSprite) 
							|| Player.getInstance().testCollisionWithSprite(objectSprite) ) {
						position.movePixels((short)0, (short)-movement);
						objectSprite.move(0, -movement);
					}
				}
			}
		}
		
		// move left
		if( (direction & Level.dirLeft) != 0 && (direction & Level.dirRight) == 0 ) {
			// just look at the left border of the moved object
			Position posBorderLow = new Position(position);
			Position posBorderHigh = new Position(position);
			posBorderLow.movePixels((short)-collisionRadius, (short)-collisionRadius);
			posBorderHigh.movePixels((short)-collisionRadius, (short)collisionRadius);
			// find out how many pixel we are allowed to move
			byte movement = getMaxMovement(posBorderLow, Level.dirLeft, speed);
			movement = getMaxMovement(posBorderHigh, Level.dirLeft, movement);
			// move
			if( movement > 0 ) {
				position.movePixels((short)-movement, (short)0);
				// test against dynamics
				if( objectSprite != null ) {
					objectSprite.move(-movement, 0);
					// or revert the movement?
					if( testCollisionWithDynamics(position, objectSprite) 
							|| Player.getInstance().testCollisionWithSprite(objectSprite) ) {
						position.movePixels((short)movement, (short)0);
						objectSprite.move(movement, 0);
					}
				}
			}
		}
		// move right
		else if( (direction & Level.dirRight) != 0 && (direction & Level.dirLeft) == 0 ) {
			// just look at the right border of the moved object
			Position posBorderLow = new Position(position);
			Position posBorderHigh = new Position(position);
			posBorderLow.movePixels((short)collisionRadius, (short)-collisionRadius);
			posBorderHigh.movePixels((short)collisionRadius, (short)collisionRadius);
			// find out how many pixel we are allowed to move
			byte movement = getMaxMovement(posBorderLow, Level.dirRight, speed);
			movement = getMaxMovement(posBorderHigh, Level.dirRight, movement);
			// move
			if( movement > 0 ) {
				position.movePixels((short)movement, (short)0);
				// test against dynamics
				if( objectSprite != null ) {
					objectSprite.move(movement, 0);
					// or revert the movement?
					if( testCollisionWithDynamics(position, objectSprite) 
							|| Player.getInstance().testCollisionWithSprite(objectSprite) ) {
						position.movePixels((short)-movement, (short)0);
						objectSprite.move(-movement, 0);
					}
				}
			}
		}
		
		byte result = 0;
		// test what was changed
		if( !position.equals(oldPos) ) {
			result |= 1;
			if( !position.sameFieldAs(oldPos) )
				result |= 2;
		}
		return result;
	}
	

	/** Tests **/
	public static void test() {
		System.out.println("Level.testPreload(): " + testPreload());
	}
	private static String testPreload() {
		try {
			if ((Level.activeLevel.blocksInMemory.right
					- Level.activeLevel.blocksInMemory.left) 
					> (Level.activeLevel.blocksVisible.right
					- Level.activeLevel.blocksVisible.left
					+ 2*(Level.PRELOAD_BLOCKS + Level.PRELOAD_BLOCKS_AT_DISTANCE)))
				return "Too many blocks in memory.";
			if ((Level.activeLevel.blocksInMemory.bottom
					- Level.activeLevel.blocksInMemory.top) 
					> (Level.activeLevel.blocksVisible.bottom
					- Level.activeLevel.blocksVisible.top
					+ 2*(Level.PRELOAD_BLOCKS + Level.PRELOAD_BLOCKS_AT_DISTANCE)))
				return "Too many blocks in memory.";
			
			return "Success";
		}
		catch(Exception e) {
			return "Exception: " + e.toString();
		}
	}

	
}


