/**
 * 
 * @author Martin Fiebig
 *
 */
public class PlayerAttack {
	public int dmgDelay;		// frames until simon deals dmg 
	public int timeForCombo; 	// frames between dmgDelay where new combo is possible
	public int stun;			// stun duration
	public int dmg;				// dmg dealt
	public byte ownSpecial;		// specials for simon
	public byte mobSpecial;		// specials for mob
	private short size;			// dmg area
	private short distance;		// distance between simon & dmg area (need for ranged attacks)
	public boolean multiTargetDmg;	// dmg
	public boolean available;
	
	/**
	 * Konstruktor
	 * Standard initialisation for variables 
	 */
	PlayerAttack()
	{
		dmgDelay = 5;
		timeForCombo = 2;
		ownSpecial = -1;
		mobSpecial = -1;
		setZone((short)22, (short)10);
		multiTargetDmg = false;
		available = true;
	}

	/**
	 * Set new Dmg area
	 * @param _size
	 * @param _distance
	 */
	public void setZone(short _size, short _distance)
	{
		this.size = _size;
		this.distance = _distance;
	}

	/**
	 * returns Top-Left-Position of dmg area
	 * @param _pos
	 * @param _dir
	 * @return
	 */
	public Position getTLAncor(Position _pos, byte _dir)
	{
		if( (_dir & Level.dirUp) != 0 )
			return new Position(_pos, (short)(-size/2), (short)(-size/2-distance));
		else if( (_dir & Level.dirRight) != 0 )
			return new Position(_pos, (short)(-size/2+distance), (short)(-size/2));
		else if( (_dir & Level.dirDown) != 0 )
			return new Position(_pos, (short)(-size/2), (short)(-size/2+distance));
		else if( (_dir & Level.dirLeft) != 0 )
			return new Position(_pos, (short)(-size/2-distance), (short)(-size/2));

		return new Position(_pos,(short) 0,(short) 0);
	}

	/**
	 * returns Bottom-Right-Position of dmg area
	 * @param _pos
	 * @param _dir
	 * @return
	 */
	public Position getBRAncor(Position _pos, byte _dir)
	{
		if( (_dir & Level.dirUp) != 0 )
			return new Position(_pos, (short)(+size/2), (short)(+size/2-distance));
		else if( (_dir & Level.dirRight) != 0 )
			return new Position(_pos, (short)(+size/2+distance), (short)(+size/2));
		else if( (_dir & Level.dirDown) != 0 )
			return new Position(_pos, (short)(+size/2), (short)(+size/2+distance));
		else if( (_dir & Level.dirLeft) != 0 )
			return new Position(_pos, (short)(+size/2-distance), (short)(+size/2));

		return new Position(_pos,(short) 0,(short) 0);
	}
	
	public int getBottom(byte _dir)
	{
		if( (_dir & Level.dirUp) != 0 )
			return (+size/2-distance);
		else if( (_dir & Level.dirRight) != 0 )
			return (short)(+size/2);
		else if( (_dir & Level.dirDown) != 0 )
			return (+size/2+distance);
		else if( (_dir & Level.dirLeft) != 0 )
			return (short)(+size/2);

		return 0;
	}
	public int getRight(byte _dir)
	{
		if( (_dir & Level.dirUp) != 0 )
			return (size/2);
		else if( (_dir & Level.dirRight) != 0 )
			return (size/2+distance);
		else if( (_dir & Level.dirDown) != 0 )
			return (size/2);
		else if( (_dir & Level.dirLeft) != 0 )
			return (size/2-distance);

		return 0;
	}
	
	public int getTop(byte _dir)
	{
		if( (_dir & Level.dirUp) != 0 )
			return (-size/2-distance);
		else if( (_dir & Level.dirRight) != 0 )
			return (-size/2);
		else if( (_dir & Level.dirDown) != 0 )
			return (-size/2+distance);
		else if( (_dir & Level.dirLeft) != 0 )
			return (-size/2);

		return 0;
	}
	
	public int getLeft(byte _dir)
	{
		if( (_dir & Level.dirUp) != 0 )
			return (-size/2);
		else if( (_dir & Level.dirRight) != 0 )
			return (-size/2+distance);
		else if( (_dir & Level.dirDown) != 0 )
			return (-size/2);
		else if( (_dir & Level.dirLeft) != 0 )
			return (-size/2-distance);

		return 0;
	}
}
