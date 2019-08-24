package LevelObjects;

import StreamOperations;
import Level;
import Block;
import Position;

import CountingInputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.TiledLayer;




/**
 * An extension of the abstract LevelObject class.
 * Simple static objects like trees, etc. 
 * @author Martin Wahnschaffe
 */
public class Static extends LevelObject {
	
	private TiledLayer objectLayer = null;
	
	private byte width;
	public byte getWidth() {
		return width;
	}
	
	private byte height;
	public byte getHeight() {
		return height;
	}
	
	public Static(Position position, Block parentBlock, byte width, byte height, short[] graphics) {
		super(LevelObject.NO_ID, position, parentBlock);
		
		this.width = width;
		this.height = height;
		
		// create object layer
		objectLayer = new TiledLayer(width, height, Level.activeLevel.getTileset(), 16, 16);
		
		// fill layer with ids
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) { 
				objectLayer.setCell(x, y, graphics[y*width+x]);				
			}
		}
		
		// update layer position
		onPixelPositionChanged();
	}
	
	/**
	 * Loads a Static from file
	 * @param parentLevel
	 * @param stream
	 * @return
	 */
	public static Static load(Block parentBlock, short blockId, CountingInputStream stream) throws Exception {
		// load bytes
		byte position_and_flags = StreamOperations.readByte(stream);
		byte position_bits_x = (byte)((position_and_flags & (128+64+32))>>5);
		byte position_bits_y = (byte)((position_and_flags & (16+8+4))>>2);
		byte width_height_part1 = (byte)(position_and_flags & (2+1));
		byte width_height_part2 = StreamOperations.readByte(stream);
		
		// get width and height
		byte width = (byte)((width_height_part1 << 3) | ((width_height_part2 & (128+64+32))>>5)); 
		byte height = (byte)(width_height_part2 & (16+8+4+2+1));
		
		// load graphic ids
		short[] graphic_ids = new short[width*height];
		for(int i = 0; i < width*height; i++) {
			graphic_ids[i] = (short)(StreamOperations.readShort(stream)+1);
		}

		// convert bytes to data types
		Position position = Level.activeLevel.makePosition(blockId, position_bits_x, position_bits_y);
		return new Static(position, parentBlock, width, height, graphic_ids);
	}
	
	public boolean isVisible() { return true; }
	
	public void onRender(Graphics g) {
		objectLayer.paint(g);
	}

	
	/**
	 * called whenever the pixel position changes
	 */
	protected void onPixelPositionChanged() {
		if( objectLayer != null )
			objectLayer.setPosition(pixelPositionX - Block.PIXELS_PER_FIELD/2, 
				pixelPositionY - Block.PIXELS_PER_FIELD/2
				- (objectLayer.getRows() - 1) * Block.PIXELS_PER_FIELD);
	}
	
	public String toString() {
		return super.toString() + " static " + width + "x" + height + " position:" + getPosition();
	}
}
