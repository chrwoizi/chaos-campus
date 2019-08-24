import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.*;
import java.io.ByteArrayOutputStream;

import Gui.*;
import LevelObjects.NPC;
import Story.StoryManager;

import java.util.*;


/**
 *  
  
 Hier ist auf Anfrage noch einmal das Kampfsystem wie es momentan steht: 
  
 - Gegner oder Spieler fuehren eine Attacke aus. 
 - Wird waehrend der Attacke ein Treffer kassiert, wird die Attacke unterbrochen 
   und man kann sich eine gewisse Zeit lang nicht bewegen. 
 - Wird die Attacke erfolgreich ausgefuehrt, so unterbricht man die momentane Aktion
   des Gegners, richtet einen gewissen Schaden an, und er kann sich fuer eine kurze 
   Zeit nicht bewegen. 
 - Jede Attacke hat, um das Kampfsystem interessanter zu gestalten, ihre eigenes Timing,
   das zur Verbesserung des Spielspasses spaeter noch veraendert werden kann.
 
 Combos: 
 - Werden mehrere Basisattacken mit bestimmtem Timing nacheinander ausgefuehrt, entwickelt
   sich der Schaden und die Dauer, in der ein Gegner nach dem Treffer hilflos ist, anders.
   Aus Resourcegruenden wird es wohl trotzdem einfach wie mehrere normale Schlaege aussehen. 
 - Mit jeder Waffe sind nur eine begrenzte Anzahl von Comboschlaegen moeglich. 
  
 Faehigkeiten: 
 - Man hat jeweils eine Sonderfaehigkeit aktiv, die man ueber das Menue zusammen mit der
   Waffe auswaehlt. Diese Faehigkeit kann durch das Druecken einer zweiten Faehigkeit 
   ausgeloest werden. Wird sie innerhalb einer Combo ausgefuehrt, macht sie mehr Schaden, 
   je mehr Schlaege ihr zuvorgegangen sind.

 */
/**
 * 
 * @author Martin Fiebig
 *
 */
public class Player {
	// singleton instance
	private static Player instance = null;

	private Position position;			// position of player in worldmap
	private byte direction;				// current directon of player

	// Grafik
	private byte height, width;			// Spielerhöhe, -breite (Basis für Spriteanimation)
	private String mainSource;			// image filename
	private Image imgPlayer;			// image with player animation
	private Sprite sprPlayer;			// sprite with player animation

	private String sourceComenius;				// image filename
	private String sourceClub;					// image filename
	private String sourceSword;					// image filename

	private Image imgPlayerWeapon;	// imgage with active player weapon
	private Sprite sprPlayerWeapon;	// sprite with active playerweapon animation

	private byte currAnimState;

	// States
	public static final byte ANIM_NOTHING 	 = 0;
	public static final byte ANIM_MOVING 	 = 1;
	public static final byte ANIM_IDLE 		 = 2;
	public static final byte ANIM_ATTACKING  = 3;
	public static final byte ANIM_SATTACKING = 4;
	public static final byte ANIM_DIE		 = 5;
	// DEFINE
	public static final byte NON_CHANGING_FRAMES = 1; // Frames between pictures

	// Player Values
	private byte maxAlc;
	private byte currAlc;
	private Vector inventory;

	//	private Random randomDamage = new Random();

	// for debuging (collision rect)
	int top=0, left=0, right=0, bottom=0;

	/**
	 * movement
	 * Array of framesequences for different movement.
	 */
	private int movement [] [] =
	{ {0,1,2,3,4,5,6},			// 0 up
	  {20,21,22,23,24,25,26},	// 1 right
	  {30,31,32,33,34,35,36},	// 2 down
	  {10,11,12,13,14,15,16},	// 3 left
	  {40,41,42},				// 4 idle
	  {43,44,45,46,47},			// 5 die
	  {7,8,9},					// 6 para top
	  {27,28,29},				// 7 para right
	  {37,38,39},				// 8 para down
	  {17,18,19}				// 9 para left
	};

	private int attacking [] [] =
	{ {3,4,5},		// Oben
	  {6,7,8},		// Rechts
	  {0,1,2},		// Unten
	  {9,10,11},	// Links
	  {15,16,17},		// Oben
	  {18,19,20},		// Rechts
	  {12,13,14},		// Unten
	  {21,22,23}	// Links
	};

	private byte currMoveDirection;
	private byte currMovement = 3;

	private byte animDelay 	 	= 0;
	private byte animFrame	 	= 0;
	private int idleTime 	 	= 0;
	private byte idleCyclesLeft	= 6;
	
	// Variablen: Kampfsystem
	
	// States
	public static final byte STATE_NOTHING				= 0;
	public static final byte STATE_ATTACKING			= 1;
	public static final byte STATE_ATTACKING_INC_COMBO	= 2;
	public static final byte STATE_SPECIAL_ATTACKING	= 3;
	public static final byte STATE_FINISH_STUN			= 4;
	public static final byte STATE_DEAD					= 5;
	public static final byte STATE_ATTACKING_NO_COMBO	= 6;
	public static final byte STATE_ATTACKING_S_INC		= 7;
	
	// Consts
	public static final byte ATTACK_TIME	 = 2;
	public static final byte MAX_ATTACKS	 = 3;
	public static final byte WEAPON_COMENIUS = 0;
	public static final byte WEAPON_CLUB 	 = 1;
	public static final byte WEAPON_SWORD 	 = 2;
	public static final byte WEAPON_SPECIAL_HASTE 			= 0;
	public static final byte WEAPON_SPECIAL_SHADOW_DRAWING	= 1;
	public static final byte WEAPON_SPECIAL_HOLD			= 2;
	public static final byte WEAPON_SPECIAL_CLUB			= 0;
	public static final byte WEAPON_SPECIAL_SWORD			= 0;
	
	// cooldown of special abilitiys
	public static final short SPECIAL_ATTACK_COOLDOWN = 8 * GameMIDlet.MAX_FRAMES_PER_SECOND;
	
	// attacks & current Weapon/Attack/Special Attack 
	private PlayerAttack attacks[][] = new PlayerAttack[3][];	// all attacks
	private PlayerAttack currWeapon[];
	private byte currWeaponID = -1;
	private PlayerAttack specialAttacks[][] = new PlayerAttack[3][];
	private PlayerAttack currSpecialAttack;
	private byte currSpecialAttackID = -1;
	private short specialAttackCooldown;
	
	private boolean shadowDrawing = false;
	
	public static final byte RUNNING_SPECIAL_ATTACK_DURATION = 64;
	public static final byte PIXELMOVEMENT_PER_FRAME_RUNNING = 5;
	public static final byte PIXELMOVEMENT_PER_FRAME = 3;
	private byte running = 0;

	// available attacks
	private boolean loadedWeapons[] = new boolean[3];
	// control vars
	private byte currState;
	private int stateFramesLeft;
	//private int sleepFramesLeft;
	private byte currComboID;		// active attack (Combo Stufe)
	private byte nextComboID;		// next attack (Nächste Combo Stufe)

	/**
	 * Konstruktor
	 * allokation of private vars
	 */
	Player()
	{
		this.inventory = new Vector();
		this.mainSource = "/player/player.png";
		this.sourceComenius = "/player/simon-buch.png";
		this.sourceClub		= "/player/simon-knueppel.png";
		this.sourceSword	= "/player/simon-schwert.png";
		this.height 		= 24;
		this.width 			= 16;

		try {
			imgPlayer = Image.createImage(this.mainSource);
			sprPlayer = new Sprite(imgPlayer,this.width, this.height);
		}
		catch (Exception e){
			new Error("Player Mainimg Loading", e);
		}

		try {
			this.imgPlayerWeapon = Image.createImage(this.sourceComenius);
			this.sprPlayerWeapon = new Sprite(this.imgPlayerWeapon , (byte) 32, (byte) 32);
		}
		catch (Exception e){
			new Error("Weapon Loading!", e);
		}

		this.sprPlayer.defineReferencePixel(8, 20);	// Offset
		this.sprPlayer.defineCollisionRectangle(2, 16, 12, 8);
		this.sprPlayerWeapon.defineReferencePixel(16, 28);	// Offset

		// define attacks & combos
		// Comenius
		this.attacks[0] = new PlayerAttack[2];
		this.attacks[0][0] = new PlayerAttack();
		this.attacks[0][0].dmgDelay 	= 12;
		this.attacks[0][0].timeForCombo = 12;
		this.attacks[0][0].stun			= 2;
		this.attacks[0][0].dmg			= 2;
		this.attacks[0][1] = new PlayerAttack();
		this.attacks[0][1].dmgDelay 	= 9;
		this.attacks[0][1].timeForCombo = 9;
		this.attacks[0][1].stun			= 4;
		this.attacks[0][1].dmg			= 2;

		// Knüppel
		this.attacks[1] = new PlayerAttack[4];
		this.attacks[1][0] = new PlayerAttack();
		this.attacks[1][0].dmgDelay 	= 12;
		this.attacks[1][0].timeForCombo = 12;
		this.attacks[1][0].stun			= 2;
		this.attacks[1][0].dmg			= 4;
		this.attacks[1][1] = new PlayerAttack();
		this.attacks[1][1].dmgDelay 	= 9;
		this.attacks[1][1].timeForCombo = 9;
		this.attacks[1][1].stun			= 3;
		this.attacks[1][1].dmg			= 4;
		this.attacks[1][2] = new PlayerAttack();
		this.attacks[1][2].dmgDelay 	= 6;
		this.attacks[1][2].timeForCombo = 6;
		this.attacks[1][2].stun			= 4;
		this.attacks[1][2].dmg			= 4;
		this.attacks[1][3] = new PlayerAttack();
		this.attacks[1][3].dmgDelay 	= 4;
		this.attacks[1][3].timeForCombo = 4;
		this.attacks[1][3].stun			= 6;
		this.attacks[1][3].dmg			= 6;


		// Drinkallibur
		this.attacks[2] = new PlayerAttack[6];
		this.attacks[2][0] = new PlayerAttack();
		this.attacks[2][0].dmgDelay 	= 12;
		this.attacks[2][0].timeForCombo = 12;
		this.attacks[2][0].stun			= 2;
		this.attacks[2][0].dmg			= 6;
		this.attacks[2][1] = new PlayerAttack();
		this.attacks[2][1].dmgDelay 	= 10;
		this.attacks[2][1].timeForCombo = 10;
		this.attacks[2][1].stun			= 3;
		this.attacks[2][1].dmg			= 6;
		this.attacks[2][2] = new PlayerAttack();
		this.attacks[2][2].dmgDelay 	= 8;
		this.attacks[2][2].timeForCombo = 8;
		this.attacks[2][2].stun			= 4;
		this.attacks[2][2].dmg			= 6;
		this.attacks[2][3] = new PlayerAttack();
		this.attacks[2][3].dmgDelay 	= 6;
		this.attacks[2][3].timeForCombo = 6;
		this.attacks[2][3].stun			= 6;
		this.attacks[2][3].dmg			= 8;
		this.attacks[2][4] = new PlayerAttack();
		this.attacks[2][4].dmgDelay 	= 5;
		this.attacks[2][4].timeForCombo = 5;
		this.attacks[2][4].stun			= 8;
		this.attacks[2][4].dmg			= 10;
		this.attacks[2][5] = new PlayerAttack();
		this.attacks[2][5].dmgDelay 	= 4;
		this.attacks[2][5].timeForCombo = 4;
		this.attacks[2][5].stun			= 10;
		this.attacks[2][5].dmg			= 12;

		// Special attacks

		//	 C O M E N I U S
		//Runspeed
		this.specialAttacks[0] = new PlayerAttack[3];
		this.specialAttacks[0][0] = new PlayerAttack();
		this.specialAttacks[0][0].dmgDelay 		= 1*GameMIDlet.MAX_FRAMES_PER_SECOND;
		this.specialAttacks[0][0].stun			= 0;
		this.specialAttacks[0][0].dmg			= 0;
		this.specialAttacks[0][0].mobSpecial	= 0;
		this.specialAttacks[0][0].ownSpecial	= 2;
		this.specialAttacks[0][0].available		= false;
//		this.specialAttacks[0][0].setZone();
		// Schattenbild
		this.specialAttacks[0][1] = new PlayerAttack();
		this.specialAttacks[0][1].dmgDelay 		= 1*GameMIDlet.MAX_FRAMES_PER_SECOND;
		this.specialAttacks[0][1].stun			= 0;
		this.specialAttacks[0][1].dmg			= 0;
		this.specialAttacks[0][1].mobSpecial	= 2;
		this.specialAttacks[0][1].ownSpecial	= 1;
		this.specialAttacks[0][1].available		= false;
//		this.specialAttacks[0][1].setZone();
		// Halten
		this.specialAttacks[0][2] = new PlayerAttack();
		this.specialAttacks[0][2].dmgDelay 		= 1*GameMIDlet.MAX_FRAMES_PER_SECOND;
		this.specialAttacks[0][2].stun			= 7;
		this.specialAttacks[0][2].dmg			= 0;
		this.specialAttacks[0][2].mobSpecial	= 3;
		this.specialAttacks[0][2].available		= false;
		this.specialAttacks[0][2].setZone((byte)120,(byte)1);

		// C L U B
		// Zertrümmerer
		this.specialAttacks[1] = new PlayerAttack[1];
		this.specialAttacks[1][0] = new PlayerAttack();
		this.specialAttacks[1][0].dmgDelay 		= 7;
		this.specialAttacks[1][0].stun			= 50;
		this.specialAttacks[1][0].dmg			= 4;
		this.specialAttacks[1][0].mobSpecial	= 4;
		this.specialAttacks[1][0].available		= false;
		this.specialAttacks[1][0].setZone((byte)40, (byte)0);
		
		// S W O R D
		this.specialAttacks[2] = new PlayerAttack[1];
		this.specialAttacks[2][0] = new PlayerAttack();
		this.specialAttacks[2][0].dmgDelay 		= 16;
		this.specialAttacks[2][0].stun			= 4;
		this.specialAttacks[2][0].dmg			= 8;
		this.specialAttacks[2][0].mobSpecial	= 7;
		this.specialAttacks[2][0].available		= false;
		this.specialAttacks[2][0].setZone((byte)40, (byte)0);
		
		this.reset();
	}
/**
 * Stereotyp: Singleton
 * @return
 */
	public static Player getInstance() {
		
		if(instance == null) {
			instance = new Player();
		}
		return instance;
	}
	
	public boolean alive()
	{
		return (this.currAlc>0);
	}

	public Position getPosition() {
		return position;
	}

	public boolean isPlayerSprite(Sprite otherSprite) {
		if( sprPlayer == otherSprite )
			return true;
		return false;
	}
	
	/**
	 * updating player vars
	 * @param moveDirection
	 */
	public void update(byte moveDirection)
	{
		if (this.running > 0)
			this.running--;
		if (this.specialAttackCooldown > 0)
			this.specialAttackCooldown--;
		// ATTACKING
		switch (this.currState) {
		case Player.STATE_ATTACKING: 
		case Player.STATE_ATTACKING_NO_COMBO: {
			this.currAnimState = Player.ANIM_ATTACKING;
			if (this.stateFramesLeft == 0) {
				this.calcDmgInZone();
				this.currAnimState = Player.ANIM_NOTHING;
				this.currState = Player.STATE_NOTHING;
			} else this.stateFramesLeft --;
		}break;
		case Player.STATE_ATTACKING_INC_COMBO: {
			this.currAnimState = Player.ANIM_ATTACKING;
			if (this.stateFramesLeft == 0) {
				this.calcDmgInZone();
				if (this.nextComboID > 0) {
					ScrollingCombatText.getInstance().addText(IODevice.getInstance().getWidth()/2, IODevice.getInstance().getHeight()/2 - 30,
							0,-1,25,"COMBO", 255, 255, 0);
				}
				this.currComboID = this.nextComboID;
				this.nextComboID = -1;
				this.currState = Player.STATE_ATTACKING;
				this.stateFramesLeft = this.currWeapon[currComboID].dmgDelay;
			} else {
				this.stateFramesLeft --;
			}
			}break;
		case Player.STATE_ATTACKING_S_INC: {
			this.currAnimState = Player.ANIM_ATTACKING;
			if (this.stateFramesLeft == 0) {
				this.calcDmgInZone();
				this.nextComboID = -1;
				this.specialAttackCooldown = (short)(Player.SPECIAL_ATTACK_COOLDOWN + this.currSpecialAttack.dmgDelay);
				this.currState = Player.STATE_SPECIAL_ATTACKING;
				this.stateFramesLeft = this.currSpecialAttack.dmgDelay;
				this.idleTime = 0;
			} else {
				this.stateFramesLeft --;
			}			
		} break;
		case Player.STATE_SPECIAL_ATTACKING: {
			this.currAnimState = Player.ANIM_SATTACKING;
			if (this.stateFramesLeft == 0) {
				//this.sct.addText(IODevice.getInstance().getWidth()/2, IODevice.getInstance().getHeight()/2 - 30, 0,-1,25,"Special", 128, 128, 255);
				this.calcDmgInZoneSpecial();
				this.currState = Player.STATE_NOTHING;
				this.currAnimState = Player.ANIM_NOTHING;
			} else {
				this.stateFramesLeft --;
			}
			}break;
		case Player.STATE_FINISH_STUN: {
			if (this.stateFramesLeft == 0) {
				this.currState = Player.STATE_NOTHING;
			} else {
				this.stateFramesLeft --;
			}
			}break;
		case Player.STATE_DEAD: {
			this.animFrame = 0;
			this.currAnimState = Player.ANIM_DIE;
		}break;
		case Player.STATE_NOTHING: {
			// MOVING
			if (this.alive())
			{
				move(moveDirection);
			}
		}break;
		}
		
		// Animation
		
		switch (this.currAnimState) {
		case Player.ANIM_ATTACKING: {
			this.sprPlayerWeapon.defineReferencePixel(16, 28);	// Offset
			this.idleTime = 0;
			if (this.stateFramesLeft <=3)
				this.sprPlayerWeapon.setFrame(this.attacking[this.currMovement][2]);
			else if (this.stateFramesLeft <=5)
				this.sprPlayerWeapon.setFrame(this.attacking[this.currMovement][1]);
			else if (this.stateFramesLeft <= 9)
				this.sprPlayerWeapon.setFrame(this.attacking[this.currMovement][0]);

			// Correct Reference Pixel
			if (this.currWeaponID == 2)
			{
				if (this.currMovement == 1)
					this.sprPlayerWeapon.defineReferencePixel(6, 28);	// Offset
				else if (this.currMovement == 3)
					this.sprPlayerWeapon.defineReferencePixel(26, 28);	// Offset
				else
					this.sprPlayerWeapon.defineReferencePixel(16, 28);	// Offset
			}
		}break;
		case Player.ANIM_SATTACKING: {
			this.idleTime = 0;
			if (this.currWeaponID == 0)
			{
				if (this.currMovement == 1)
					this.sprPlayerWeapon.defineReferencePixel(6, 28);	// Offset
				else if (this.currMovement == 3)
					this.sprPlayerWeapon.defineReferencePixel(26, 28);	// Offset
				else
					this.sprPlayerWeapon.defineReferencePixel(16, 28);	// Offset
			} else if (this.currWeaponID == 1) {
				this.sprPlayerWeapon.defineReferencePixel(16, 28);	// Offset
			}
			if (this.currWeaponID != 2)
			{
				if (this.stateFramesLeft <=3)
					this.sprPlayerWeapon.setFrame(this.attacking[this.currMovement+4][2]);
				else if (this.stateFramesLeft <=5)
					this.sprPlayerWeapon.setFrame(this.attacking[this.currMovement+4][1]);
				else
					this.sprPlayerWeapon.setFrame(this.attacking[this.currMovement+4][0]);
			} else {
				if (this.stateFramesLeft <=16)
				{
					this.sprPlayerWeapon.setFrame(12+8-(this.stateFramesLeft/2));
					this.sprPlayerWeapon.defineReferencePixel(14, 24);	// Offset
				}
			}
		}break;
		case Player.ANIM_IDLE: {
			if (this.animDelay == 0) {
				this.animFrame++;
				try {
					this.sprPlayer.setFrame(this.movement[4][this.animFrame]);
					
					// last Frame?
					if (this.animFrame == this.movement[4].length-1)
					{
			//			this.sct.addText(IODevice.getInstance().getWidth()/2, IODevice.getInstance().getHeight()/2 - 30, 0,-1,25,"Idle +" + this.idleCyclesLeft, 255, 255, 0);
						// Repeat Idle (Paging)
						if (this.idleCyclesLeft != 0) {
							this.animFrame = 0;
							this.idleCyclesLeft--;
						}
						else
						{
							this.currAnimState = Player.ANIM_NOTHING;
							this.idleTime = 0;
						}
					}
					this.animDelay = Player.NON_CHANGING_FRAMES*18;
				} catch(Exception e) {
					new Error("ANIM_IDLE", e);
				}					
			}
			this.animDelay--;
		}break;
		case Player.ANIM_MOVING: {
			if (this.currMoveDirection == Level.dirNone)
				this.currAnimState = Player.ANIM_NOTHING;
			if (this.animDelay == 0) {
				this.idleTime = 0;
				this.animFrame++;
				try {
					this.animFrame %= this.movement[this.currMovement].length;
					this.sprPlayer.setFrame(this.movement[this.currMovement][this.animFrame]);
				} catch(Exception e) {
					new Error("ANIM_MOVING", e);
				}
				this.animDelay = Player.NON_CHANGING_FRAMES;
			}
			this.animDelay--;
		}break;
		case Player.ANIM_DIE: {

			if (this.animDelay == 0) {
				this.idleTime = 0;
				try{
					if (this.animFrame < this.movement[5].length-1)
						this.animFrame++;
					this.sprPlayer.setFrame(this.movement[5][this.animFrame]);
				} catch(Exception e) {
					new Error("ANIM_DIE", e);
				}
				this.animDelay = Player.NON_CHANGING_FRAMES;
			}
			this.animDelay--;
		}break;
		case Player.ANIM_NOTHING:
		default: {
			try {
				if (this.currMoveDirection != Level.dirNone)
					this.currAnimState = Player.ANIM_MOVING;
				this.sprPlayer.setFrame(this.movement[this.currMovement][0]);
				this.idleTime++;
				if (this.idleTime >= 120) {
					this.currAnimState = Player.ANIM_IDLE;
					this.idleCyclesLeft = 6;
					this.animFrame = 0;
					this.animDelay = Player.NON_CHANGING_FRAMES*6;
					this.sprPlayer.setFrame(this.movement[4][0]);
				}
			} catch(Exception e) {
				new Error("ANIM_MOVING", e);
			}
		}break;
		} // switch
	}

	private void onDirectionChanged(byte direction) {
		if( (direction & Level.dirUp) != 0) {
			this.currMovement=0;
			this.currAnimState = Player.ANIM_MOVING;
		} else if( (direction & Level.dirDown) != 0) {
			this.currMovement=2;
			this.currAnimState = Player.ANIM_MOVING;
		} else if( (direction & Level.dirLeft) != 0) {
			this.currMovement=3;
			this.currAnimState = Player.ANIM_MOVING;
		} else if( (direction & Level.dirRight) != 0) {
			this.currMovement=1;
			this.currAnimState = Player.ANIM_MOVING;
		} else {
			this.currAnimState = Player.ANIM_NOTHING;
		}
		if (direction != Level.dirNone && this.direction != direction) {
			this.direction = direction;
			Level.activeLevel.onPlayerDirectionChanged();
		}
		this.currMoveDirection = direction;		// Aktuelle Bewegungsrichtung speichern
		this.animFrame = (byte) (this.movement[this.currMovement].length);
		this.animDelay = 0;	// für Animationsgeschwindigkeit Offset auf 0 setzten

	}

	/**
	 * Moves the player.
	 * @param direction The inverse player direction
	 */
	private void move(byte direction) {
		// animation stuff
		if (direction != this.currMoveDirection) {
			// Bewegungsänderung seit letztem move
			onDirectionChanged(direction);
		}

		// position & collision stuff
		if(direction != Level.dirNone) {
			// try to do the movement.
			int changedWhat = Level.activeLevel.move(position, 
					sprPlayer, direction, 
					(this.running>0)? PIXELMOVEMENT_PER_FRAME_RUNNING :PIXELMOVEMENT_PER_FRAME,
					(byte)6);

			// test if the position changed:
			switch(changedWhat) {
				case 0:
					break;
				case 1:
					// changed pixel position
					Level.activeLevel.onPlayerPixelPositionChanged();
					break;
				case 2:
					// changed field
					Level.activeLevel.onPlayerFieldPositionChanged();
					break;
				case 3:
					// changed field
					Level.activeLevel.onPlayerFieldPositionChanged();
					// changed pixel position
					Level.activeLevel.onPlayerPixelPositionChanged();
			}
		}
	}

	/**
	 * Renders the Player
	 * @param gra
	 */
	public void render(Graphics gra)
	{
		//gra.setColor(255,0,0);
		//gra.drawRect(IODevice.getInstance().getWidth()/2 + left, IODevice.getInstance().getHeight()/2 + top, right, bottom);
		// visualisation of special attack "Schattenbild"
		if( this.shadowDrawing ) {
			int color = (int)((System.currentTimeMillis()%1000)/5)-100;
			if( color < 0 )	color = -color;
			color += 20;
			gra.setColor(color, color, color);
			gra.fillArc(IODevice.getInstance().getWidth()/2 - 10, 
					IODevice.getInstance().getHeight()/2 - 3, 19, 10, 0, 360);
		}	
		if( this.running > 0) {
			gra.setColor(200, 200,0);
			gra.drawArc(IODevice.getInstance().getWidth()/2 - 9,
					IODevice.getInstance().getHeight()/2 - 3,
					16, 10, 30*(this.running%12), 60);
			gra.drawArc(IODevice.getInstance().getWidth()/2 - 9,
					IODevice.getInstance().getHeight()/2 - 3,
					16, 10, 180+30*(this.running%12), 60);
		}
		
		this.sprPlayer.setRefPixelPosition(IODevice.getInstance().getWidth()/2, IODevice.getInstance().getHeight()/2);
		this.sprPlayerWeapon.setRefPixelPosition(IODevice.getInstance().getWidth()/2, IODevice.getInstance().getHeight()/2);

		switch (this.currAnimState) {
		case Player.ANIM_ATTACKING: {
			if (this.stateFramesLeft <= 9)
				this.sprPlayerWeapon.paint(gra);
			else
				this.sprPlayer.paint(gra);
		}break;
		case Player.ANIM_SATTACKING: {
			if (this.stateFramesLeft <= 10)
				this.sprPlayerWeapon.paint(gra);
			else
				this.sprPlayerWeapon.paint(gra);
		}break;
		case Player.ANIM_IDLE:
		case Player.ANIM_MOVING:
		case Player.ANIM_DIE:
		case Player.ANIM_NOTHING: {
			this.sprPlayer.paint(gra);
		}break;
		} // switch
		
		//ScrollingCombatText.getInstance().render(gra);
		
		/*
		if( this.currWeapon != null  ) {*/

/*		int pixelBRX = posBR.fieldX * Block.PIXELS_PER_FIELD
		+ 8 + Level.activeLevel.getViewTranslateX();
		int pixelBRY = posBR.fieldY * Block.PIXELS_PER_FIELD
		+ 9 + Level.activeLevel.getViewTranslateY();
		gra.drawLine(pixelTLX, pixelTLY, pixelBRX, pixelBRY);*/

	}

	public byte getDirection() { return this.direction; }

	private void calcDmgInZone()
	{
		try {
			Position posTL;
			Position posBR;

			posTL = this.currWeapon[this.currComboID].getTLAncor(this.position, this.direction);
			posBR = this.currWeapon[this.currComboID].getBRAncor(this.position, this.direction);

			this.top =  this.currWeapon[this.currComboID].getTop(this.direction);
			this.left = this.currWeapon[this.currComboID].getLeft(this.direction);
			this.bottom = this.currWeapon[this.currComboID].getBottom(this.direction)-this.top;
			this.right = this.currWeapon[this.currComboID].getRight(this.direction)-this.left;
						

			// calculate objects in dmg-zone
			NPC enemy = (NPC)Level.activeLevel.getNearestNpc(posTL,posBR,position);
			if( enemy != null ) {
				enemy.receiveAttack((byte)this.currWeapon[this.currComboID].dmg,(byte)this.currWeapon[this.currComboID].stun, (byte)this.currWeapon[this.currComboID].mobSpecial);
			}
		}catch (Exception e){
			new Error("calcDmgInZone", e);
		}

	}
	
	private void calcDmgInZoneSpecial()
	{
		HeadUpDisplay.setSpecialTimer(specialAttackCooldown);
		if (this.currSpecialAttack.ownSpecial == 1){	// Schattenbild
			this.shadowDrawing = true;
			return;
		} else if(this.currSpecialAttack.ownSpecial == 2) { // runspeed
			this.running = Player.RUNNING_SPECIAL_ATTACK_DURATION;
		}
		try {
			Position posTL;
			Position posBR;

			System.err.println("CalcDMG:");
			posTL = this.currSpecialAttack.getTLAncor(this.position, this.direction);
			posBR = this.currSpecialAttack.getBRAncor(this.position, this.direction);

			
			this.top =  this.currSpecialAttack.getTop(this.direction);
			this.left = this.currSpecialAttack.getLeft(this.direction);
			this.bottom = this.currSpecialAttack.getBottom(this.direction)-this.top;
			this.right = this.currSpecialAttack.getRight(this.direction)-this.left;
		
			// calculate objects in dmg-zone
			if (this.currWeaponID == 0) // comenius
			{
				System.err.println("freeze them:");
				// Freeze multiple Targets
				Vector enemys = new Vector();
				enemys = Level.activeLevel.getActiveDynamicsInRegion(posTL, posBR , NPC.class);
				if( !enemys.isEmpty()) {
					for (Enumeration enum=enemys.elements(); enum.hasMoreElements(); ) {
						System.err.println("next");
						NPC enemy = (NPC)enum.nextElement();
						enemy.receiveAttack((byte)0,(byte)this.currSpecialAttack.stun, (byte)this.currSpecialAttack.mobSpecial);
					}
				} else {
					System.err.println("keiner da :-/");
				}
			} else if (this.currWeaponID == 1){	// club
				// DMG to one target
				NPC singleEnemy = (NPC)Level.activeLevel.getNearestNpc(posTL,posBR,position);
				if( singleEnemy != null ) {
					singleEnemy.receiveAttack((byte)this.currSpecialAttack.dmg,(byte)this.currSpecialAttack.stun, (byte)this.currSpecialAttack.mobSpecial);
				}
				// Stun to multiple Targets
				Vector enemys = new Vector();
				enemys = Level.activeLevel.getActiveDynamicsInRegion(posTL, posBR , NPC.class);
				if( !enemys.isEmpty() ) {
					for (Enumeration enum=enemys.elements(); enum.hasMoreElements(); ) {
						NPC enemy = (NPC)enum.nextElement();
						enemy.receiveAttack((byte)0,(byte)this.currSpecialAttack.stun, (byte)this.currSpecialAttack.mobSpecial);
					}
				}
			} else if (this.currWeaponID == 2){ // sword = multiple dmg targets
				Vector enemys = new Vector();
				enemys = Level.activeLevel.getActiveDynamicsInRegion(posTL, posBR , NPC.class);
				if( !enemys.isEmpty()) {
					for (Enumeration enum=enemys.elements(); enum.hasMoreElements(); ) {
						NPC enemy = (NPC)enum.nextElement();
						enemy.receiveAttack((byte)this.currSpecialAttack.dmg,(byte)this.currSpecialAttack.stun, (byte)this.currSpecialAttack.mobSpecial);
					}
				}
			}
		}catch (Exception e){
			new Error("player calcdmginzonespecial", e);
		}

	}

	public void attack()
	{
		if (this.currWeaponID >= 0)
		{
			try {
				if (this.currAlc > 0)
				{
					// Currently attacking & NO following Combo yet? Set next Combo!
					if ((this.currState == Player.STATE_ATTACKING))
					{
						if (this.stateFramesLeft<=this.currWeapon[this.currComboID].timeForCombo)
						{
							if ( (this.currComboID +1) < this.currWeapon.length) {
							this.nextComboID = (byte)(this.currComboID+1);
							this.currState = Player.STATE_ATTACKING_INC_COMBO;
							}
						} else {
							this.currState = Player.STATE_ATTACKING_NO_COMBO;
						}
					} else if (this.currState == Player.STATE_NOTHING){
						this.idleTime = 0;
						this.currState = Player.STATE_ATTACKING;
						this.currComboID = 0;
						this.stateFramesLeft = this.currWeapon[this.currComboID].dmgDelay;
						this.nextComboID = -1;
					} else if (this.currState == Player.STATE_ATTACKING_INC_COMBO) {
						this.currState = Player.STATE_ATTACKING_NO_COMBO;
					}
				}
			} catch (Exception e) {
				new Error(e);
				new Error(e.getMessage() + "@Player.attack()");
			}
		}
	}

	public void specialAttack()
	{
		if (this.alive())
		{
			if ( this.currSpecialAttackID >= 0)
			{
				if (this.specialAttackCooldown == 0)
				{
					if ( (this.currState == Player.STATE_ATTACKING)
						|| (this.currState == Player.STATE_ATTACKING_INC_COMBO)
						|| (this.currState == Player.STATE_ATTACKING_NO_COMBO))
					{
						this.currState = Player.STATE_ATTACKING_S_INC;
					} else {
						this.currState = Player.STATE_SPECIAL_ATTACKING;
						this.stateFramesLeft = this.currSpecialAttack.dmgDelay;
						this.specialAttackCooldown = (short)(Player.SPECIAL_ATTACK_COOLDOWN + this.currSpecialAttack.dmgDelay);
						this.idleTime = 0;
					}
				} else {
					//this.sct.addText(IODevice.getInstance().getWidth()/2, IODevice.getInstance().getHeight()/2 - 30, 0,-1,25, Player.STR_S_ATTACK_COOLDOWN, 255, 0, 0);
				}
			} else if( this.currWeaponID >= 0 ) {
				GameManager.getInstance().playComment((byte)27);
			}
		}
	}
	
	public boolean inCombat()
	{
		if   ( (this.currState == Player.STATE_ATTACKING)
			|| (this.currState == Player.STATE_ATTACKING_INC_COMBO)
			|| (this.currState == Player.STATE_ATTACKING_NO_COMBO)
			|| (this.currState == Player.STATE_ATTACKING_S_INC) )
			return true;
		else
			return false;
	}

	public void setWeapon(byte id)
	{
		if (this.currWeaponID == id)
			return;
		setSpecialAttack((byte)-1);
		if ((id <= 2)&&(id >= 0))
		{
			this.currWeaponID =  id;
			this.currWeapon = this.attacks[id];
		//	this.currAttack = this.currWeapon[0];
			this.currComboID = 0;
			try {
				this.imgPlayerWeapon = null;
				switch (this.currWeaponID){
				case 0:
					this.imgPlayerWeapon = Image.createImage(this.sourceComenius);
					break;
				case 1:
					this.imgPlayerWeapon = Image.createImage(this.sourceClub);
					break;
				case 2:
					this.imgPlayerWeapon = Image.createImage(this.sourceSword);
					break;
				}
				this.sprPlayerWeapon.setImage(this.imgPlayerWeapon, 32, 32);
			} catch (Exception e) {
				new Error(e);
				new Error("");
			}
			//this.sprPlayerWeapon.setImage(arg0, arg1, arg2)
		}
		else if( id < 0 )
		{
			this.currWeaponID = -1;
			this.currWeapon = null;
			this.currComboID = 0;
		}
		//throw ();
	}
	public byte getWeapon()
	{
		return this.currWeaponID;
	}
	
	public byte getSpecialAttack()
	{
		return this.currSpecialAttackID;
	}

	public void setSpecialAttack(byte id)
	{
		try {
			if( id < 0) {
				this.currSpecialAttack = null;
				this.currSpecialAttackID = -1;
			}
			else if (id <= this.specialAttacks[this.currWeaponID].length)
			{
				if (this.specialAttacks[this.currWeaponID][id].available)
				{
					this.currSpecialAttack = this.specialAttacks[this.currWeaponID][id];
					this.currSpecialAttackID = id;
				}
			}
		} catch (Exception e){
			new Error("Player.setSpecialAttack()", e);
		}
	}

	/**
	 * returns wether SpecialAttack is available or not
	 * @param weaponId
	 * @param id
	 * @return
	 */
	public boolean isSpecialAttackAvailable(byte weaponId, byte id)
	{
		try {
			if ((weaponId <=2) && (weaponId >= 0))
			{
				if (weaponId == 0)
				{
					if ((id <= 2) && (id >=0))
						return this.specialAttacks[weaponId][id].available;
				} else if (weaponId <= 2)
				{
					return this.specialAttacks[weaponId][id].available;
				}
			}
		} catch (Exception e) {
			new Error("Player.activateSpecialAttack()", e);
		}
		return false;
	}

	/**
	 * activates a specific SpecialAttack
	 * @param weaponId
	 * @param id
	 */
	public void activateSpecialAttack(byte weaponId, byte id)
	{
		try {
			if ((weaponId <=2) && (weaponId >= 0))
			{
				if (weaponId == 0)
				{
					if ((id <= 2) && (id >=0))
						this.specialAttacks[weaponId][id].available = true;
				} else if (weaponId <= 2)
				{
					this.specialAttacks[weaponId][id].available = true;
				}
			}
		} catch (Exception e) {
			new Error("Player.activateSpecialAttack()", e);
		}
	}

	public boolean running()
	{
		return (this.running>0)?true:false;
	}
	/**
	 * 
	 * @param value
	 */
	public void drink(byte value)
	{
		if (this.currAlc + value > this.maxAlc)
			this.currAlc = this.maxAlc;
		else if(this.currAlc + value < this.currAlc) // Byte overflow abfangen
			this.currAlc = this.maxAlc;
		else
			this.currAlc += value;
	}
	
	public byte getAlcLevel()
	{
		return this.currAlc;
	}

	public byte getAlcLevelMaximum()
	{
		return this.maxAlc;
	}
	
	public void setAlcLevel(byte lvl)
	{
		this.currAlc = (lvl < this.maxAlc) ? lvl : this.maxAlc;
	}

	public void setMaxAlcLevel(byte lvl)
	{
		this.maxAlc = lvl;
	}

	public void receiveDamage(byte dmg)
	{
		if (this.shadowDrawing == true)
		{
			ScrollingCombatText.getInstance().addText(IODevice.getInstance().getWidth()/2, IODevice.getInstance().getHeight()/2 - 30, 0,-1,25, "Absorbiert ("+dmg+")", 25, 25, 225);
			this.shadowDrawing = false;
			return;
		}
		ScrollingCombatText.getInstance().addText(IODevice.getInstance().getWidth()/2, IODevice.getInstance().getHeight()/2 - 30, 0,-1,25, "-" + dmg, 255, 0, 0);
		if (this.currAlc != 0)
		{
			this.idleTime = 0;
			if ((this.currAlc - dmg < 0) || (this.currAlc - dmg > this.currAlc))
			{
				this.currAlc = 0;
			} else
				this.currAlc -= dmg;

			// Dead
			if (this.currAlc == 0)
			{
				this.currState = Player.STATE_DEAD;
			}
			else if( this.currAlc < 8 ){
				GameManager.getInstance().playComment((byte)6);
			}
		}
	}

	/**
	 * Removes an Item from Inventory
	 * @param item
	 */	public void removeItemFromInventory(byte item)
	{
		this.inventory.removeElement(new Byte(item));
	}

	public void addItemToInventory(byte item)
	{
		this.inventory.addElement(new Byte(item));
		
		if (item == 4)
			this.loadedWeapons[0] = true;
		if (item == 5)
			this.loadedWeapons[1] = true;
		if (item == 6)
			this.loadedWeapons[2] = true;
	}
	
	public void useItem(byte item)
	{
		StoryManager.getInstance().onItemUsed(item);
		// alcohol potion
		if( item == 1 ) {
			if (this.currAlc < this.maxAlc)
			{
				drink((byte)15);
				removeItemFromInventory(item);
			} else {
				GameManager.getInstance().playComment((byte)29);
			}
		}
		else if( item == 2 ) {
			if (this.currAlc < this.maxAlc)
			{
				drink((byte)30);
				removeItemFromInventory(item);
			} else {
				GameManager.getInstance().playComment((byte)29);
			}
		}
		else if( item == 3 ) {
			if (this.currAlc < this.maxAlc)
			{
				drink((byte)45);
				removeItemFromInventory(item);
			} else {
				GameManager.getInstance().playComment((byte)29);
			}
		}
	}
	
	public Vector getInventory()
	{
		return this.inventory;
	}

	public boolean hasItem(byte item)
	{
		return this.inventory.contains(new Byte(item));
	}
	
	public byte getItemCount(byte item) 
	{
		byte counter = 0;
		for( int i=0; i<inventory.size(); i++ ) {
			if( ((Byte)inventory.elementAt(i)).byteValue() == item )
				counter++;
		}
		return counter;
	}

	public void teleport(Position to) {
		position.set(to);
		Level.activeLevel.onPlayerTeleport();
	}

	public boolean testCollisionWithSprite(Sprite objectSprite) 
	{
		if(objectSprite == sprPlayer) return false;
		return sprPlayer.collidesWith(objectSprite, false);
	}
	
	/**
	 * Saves all attributes to a stream. The stream is given by SaveGameManager.
	 * @param stream
	 * @throws Exception
	 */
	public void saveToStream(ByteArrayOutputStream stream) throws Exception {
		// save position
		StreamOperations.writeShort(stream, position.fieldX);
		StreamOperations.writeShort(stream, position.fieldY);
		
		// save alc
		stream.write(maxAlc);
		stream.write(currAlc);
		stream.write((this.specialAttacks[0][0].available)?1:0);
		stream.write((this.specialAttacks[0][1].available)?1:0);
		stream.write((this.specialAttacks[0][2].available)?1:0);
		stream.write((this.specialAttacks[1][0].available)?1:0);
		stream.write((this.specialAttacks[2][0].available)?1:0);
		stream.write(this.currWeaponID);
		stream.write(this.currSpecialAttackID);
		
		// save inventory
		byte inventorySize = (byte)inventory.size();
		stream.write(inventorySize);
		for(byte i = 0; i < inventorySize; i++) {
			stream.write(((Byte)inventory.elementAt(i)).byteValue());
		}
	}
	
	/**
	 * Loads all attributes from a stream. The stream is given by SaveGameManager.
	 * @param stream
	 * @throws Exception
	 */
	public void loadFromStream(CountingInputStream stream) throws Exception {
		// reset player
		reset();
		
		// load position
		this.position = new Position(StreamOperations.readShort(stream), StreamOperations.readShort(stream));
		
		// load alc
		maxAlc = StreamOperations.readByte(stream);
		currAlc = StreamOperations.readByte(stream);
		this.specialAttacks[0][0].available = (StreamOperations.readByte(stream)==1)?true:false;
		this.specialAttacks[0][1].available = (StreamOperations.readByte(stream)==1)?true:false;
		this.specialAttacks[0][2].available = (StreamOperations.readByte(stream)==1)?true:false;
		this.specialAttacks[1][0].available = (StreamOperations.readByte(stream)==1)?true:false;
		this.specialAttacks[2][0].available = (StreamOperations.readByte(stream)==1)?true:false;
		this.setWeapon(StreamOperations.readByte(stream));
		this.setSpecialAttack(StreamOperations.readByte(stream));
		
		// load inventory
		byte inventorySize = StreamOperations.readByte(stream);
		inventory = new Vector(inventorySize);
		for(byte i = 0; i < inventorySize; i++) {
			inventory.addElement(new Byte(StreamOperations.readByte(stream)));
		}
		
		// go to position
		//teleport(newPosition);
		
		// calculate weapons
		this.loadedWeapons[0] = inventory.contains(new Byte((byte)4));
		this.loadedWeapons[1] = inventory.contains(new Byte((byte)5));
		this.loadedWeapons[2] = inventory.contains(new Byte((byte)6));
	}
	
	/**
	 * Resets the attributes of player. Used by LoadFromStream and by GameManager.newGame
	 *
	 */
	public void reset() {
		
		// general
		this.currState = Player.STATE_NOTHING;
		position = new Position(0,0);
		this.currAlc = 30;
		this.maxAlc = 30;
		
		// moving
		this.currMoveDirection = 0;
		
		// idle
		this.idleTime = 0;

	
		this.loadedWeapons[0] = false;
		this.loadedWeapons[1] = false;
		this.loadedWeapons[2] = false;
		// attacking
		this.stateFramesLeft = 0;
		this.currWeaponID = -1;
		this.currWeapon = null;
		this.currComboID = 0;
		this.nextComboID = -1;
		this.currSpecialAttackID = -1;
		this.currSpecialAttack = null;
		this.specialAttackCooldown = 0;
		this.shadowDrawing = false;
		this.running = 0;

		// Animation
		this.currAnimState = Player.ANIM_NOTHING;
		this.animDelay = Player.NON_CHANGING_FRAMES;
		this.animFrame = 0;
		this.currMovement = 0;

		// Inventory
		this.inventory.removeAllElements();
		/*this.inventory.addElement(new Byte((byte)4));
		this.inventory.addElement(new Byte((byte)5));
		this.inventory.addElement(new Byte((byte)6));
		/*this.setWeapon((byte)2);
		this.setSpecialAttack((byte)0);*/
	}
}
