


/**
 * Position in the level 
 * @author Martin Wahnschaffe, Christian Woizischke
 */
public class Position {
	public short fieldX;
	public short fieldY;
	public byte pixelOffsetX;
	public byte pixelOffsetY;

	public Position() {
		fieldX = fieldY = -1;
		pixelOffsetX = pixelOffsetY = 0;
	}
	
	public Position(int _fieldX, int _fieldY) {
		setField((short)_fieldX,(short)_fieldY);
	}
	
	public Position(Position p) {
		set(p);
	}

	public Position(Position p, short pixelMoveX, short pixelMoveY) {
		set(p);
		pixelOffsetX += pixelMoveX;
		pixelOffsetY += pixelMoveY;
		pixelOffsetChanged();
	}
	
	public void setField(short x, short y) {
		this.fieldX = x;
		this.fieldY = y;
	}
	
	public void set(Position p) {
		this.fieldX = p.fieldX;
		this.fieldY = p.fieldY;
		this.pixelOffsetX = p.pixelOffsetX;
		this.pixelOffsetY = p.pixelOffsetY;
	}
	
	public void movePixels(short pixelMoveX, short pixelMoveY) {
		pixelOffsetX += pixelMoveX;
		pixelOffsetY += pixelMoveY;
		pixelOffsetChanged();
	}
	
	private void pixelOffsetChanged() {
		while( pixelOffsetX > (Block.PIXELS_PER_FIELD/2)) {
			this.fieldX++;
			pixelOffsetX -= Block.PIXELS_PER_FIELD;
		}
		while( pixelOffsetX < -(Block.PIXELS_PER_FIELD/2)) {
			this.fieldX--;
			pixelOffsetX += Block.PIXELS_PER_FIELD;
		}
		while( pixelOffsetY > (Block.PIXELS_PER_FIELD/2)) {
			this.fieldY++;
			pixelOffsetY -= Block.PIXELS_PER_FIELD;
		}
		while( pixelOffsetY < -(Block.PIXELS_PER_FIELD/2)) {
			this.fieldY--;
			pixelOffsetY += Block.PIXELS_PER_FIELD;
		}
	}
	
	/*public Position subtract(Position p) {
		return new Position((short)(fieldX - p.fieldX), (short)(fieldY - p.fieldY));
	}
	
	public Position add(Position p) {
		return new Position((short)(fieldX + p.fieldX), (short)(fieldY + p.fieldY));
	}*/
	
	public boolean smallerThan(Position p) {
		if( fieldX > p.fieldX ) return false;
		if( fieldY > p.fieldY ) return false;
		if( fieldX == p.fieldX && pixelOffsetX > p.pixelOffsetX ) return false;
		if( fieldY == p.fieldY && pixelOffsetY > p.pixelOffsetY ) return false;
		return true;
	}
	
	public boolean greaterThan(Position p) {
		if( fieldX < p.fieldX ) return false;
		if( fieldY < p.fieldY ) return false;
		if( fieldX == p.fieldX && pixelOffsetX < p.pixelOffsetX ) return false;
		if( fieldY == p.fieldY && pixelOffsetY < p.pixelOffsetY ) return false;
		return true;
	}
	
	public boolean equals(Position p) {
		return fieldX == p.fieldX && fieldY == p.fieldY && pixelOffsetX == p.pixelOffsetX && pixelOffsetY == p.pixelOffsetY;
	}
	
	public boolean sameFieldAs(Position p) {
		return fieldX == p.fieldX && fieldY == p.fieldY;
	}
	
	public short maxPixelDistanceTo(Position p) {
		short distX = (short)((fieldX -p.fieldX)*Block.PIXELS_PER_FIELD + pixelOffsetX - p.pixelOffsetX);
		short distY = (short)((fieldY -p.fieldY)*Block.PIXELS_PER_FIELD + pixelOffsetY - p.pixelOffsetY);
		if( distX < 0 ) distX = (short)-distX;
		if( distY < 0 ) distY = (short)-distY;
		if( distY > distX )
			return distY;
		return distX;		
	}
	
	public String toString() {
		return new String("(" + fieldX + ";" + fieldY + ")");
	}
	
	public int getBlockRelativeFieldX() { return fieldX % Block.FIELDS_PER_BLOCK; }
	public int getBlockRelativeFieldY() { return fieldY % Block.FIELDS_PER_BLOCK; }

	public short getBlockX() { return (short)(fieldX / Block.FIELDS_PER_BLOCK); }
	public short getBlockY() { return (short)(fieldY / Block.FIELDS_PER_BLOCK); }

	public short getPixelX() { return (short)(fieldX*Block.PIXELS_PER_FIELD + pixelOffsetX); }
	public short getPixelY() { return (short)(fieldY*Block.PIXELS_PER_FIELD + pixelOffsetY); }
}