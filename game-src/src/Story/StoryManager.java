package Story;

import java.io.*;

import Position;
import GameManager;
import Gui.*;
import Level;
import Player;
import LevelObjects.*;
import StreamOperations;
import CountingInputStream;

/**
 * 
 * @author Martin Wahnschaffe
 *
 */
public class StoryManager 
{
	private short storyState;
	private short killedWithCormenius;
	private short killedWithKeule;
	private short killedWithDrinkalibur;

	private static StoryManager instance = null;
	
	private StoryManager() {
		reset();
	}
	
	public static StoryManager getInstance() {
		if( instance == null )
			instance = new StoryManager();
		return instance;
	}
	
	public void loadFromStream(CountingInputStream stream) throws Exception {
		storyState = StreamOperations.readShort(stream);
		killedWithCormenius = StreamOperations.readShort(stream);
		killedWithKeule = StreamOperations.readShort(stream);
		killedWithDrinkalibur = StreamOperations.readShort(stream);
	}
	
	public void saveToStream(ByteArrayOutputStream stream) throws Exception {
		StreamOperations.writeShort(stream, storyState);
		StreamOperations.writeShort(stream, killedWithCormenius);
		StreamOperations.writeShort(stream, killedWithKeule);
		StreamOperations.writeShort(stream, killedWithDrinkalibur);
	}
	
	public void reset() {
		storyState = 0;
		killedWithCormenius = 0;
		killedWithKeule = 0;
		killedWithDrinkalibur = 0;
	}
	
	public void setPlayerName(String name) {
		System.err.println("setPlayerName() ist not implemented");
	}
	
	public String getPlayerName() {
		System.err.println("getPlayerName() ist not implemented");
		return null;
	}
	
	public void onStoryTrigger(byte storyId) {
		switch ( Level.activeLevel.getLevelId() ) {
		case 1:	onStoryTriggerStudentsCamp(storyId); break;
		case 2:	onStoryTriggerForest(storyId); break;
		case 4:
		case 5:
		case 6:	onStoryTriggerBlacksmith(storyId); break;
		case 7:	onStoryTriggerAlchemie(storyId); break;
		case 8:	onStoryTriggerMightAndMagic(storyId); break;
		case 9:	onStoryTriggerMainComplex(storyId); break;
		}
	}
	
	public void onLevelStart(byte levelId) {
		// students camp - intro and first comment
		if( levelId == 1 && storyState == 0 ) {
			GameManager.getInstance().playCutScene((byte)0);
			GameManager.getInstance().playComment((byte)0);
			storyState = 1;
		}	
	}
	
	public void onRiddleSolved(byte riddleId) {
		// solved mosaik riddle in the schmiedekunst
		if( riddleId == 1 ) {
			// open the secret passage
			((Door)Level.activeLevel.getObject((short)40)).open();
			Player.getInstance().removeItemFromInventory((byte)12);
			GameManager.getInstance().playComment((byte)40);
			storyState = 50;
		}
		// solved destiller riddle in the Alchemie
		if( riddleId == 2 ) {
			Player.getInstance().setMaxAlcLevel((byte)50); 
			Player.getInstance().setAlcLevel((byte)50);
			Player.getInstance().removeItemFromInventory((byte)13);
			Player.getInstance().removeItemFromInventory((byte)13);
			Player.getInstance().removeItemFromInventory((byte)13);
			Player.getInstance().removeItemFromInventory((byte)13);
			GameManager.getInstance().playComment((byte)68);
		}
		// solved hanoi riddle in the macht und magie fakultät
		if( riddleId == 3 ) {
			Player.getInstance().activateSpecialAttack(Player.WEAPON_COMENIUS, Player.WEAPON_SPECIAL_HASTE);
			Player.getInstance().setWeapon(Player.WEAPON_COMENIUS);
			Player.getInstance().setSpecialAttack(Player.WEAPON_SPECIAL_HASTE);
			((HeadUpDisplay)GuiManager.getInstance().getActiveMenu()).updateWeaponSpecialImages();
			GameManager.getInstance().playComment((byte)78);
		}
		// solved number riddle in the mensa
		if( riddleId == 4 ) {
			// open the secret passage
			((Door)Level.activeLevel.getObject((short)504)).open();
			storyState = 91;
		}
		// solved mastermind riddle in the main complex
		if( riddleId == 5 ) {
			// open the secret passage
			((Door)Level.activeLevel.getObject((short)103)).open();
			((Door)Level.activeLevel.getObject((short)104)).open();
			((Door)Level.activeLevel.getObject((short)105)).open();
			GameManager.getInstance().playComment((byte)80);
			storyState = 100;
		}
	}
	
	public void onObjectEnabled(short objectSerial) {
	
	}
	
	public void onEnemyKilled(short objectSerial, byte enemyType) {
		// killed the first two zombies in students camp
		if( (objectSerial == 16 || objectSerial == 17)
				&& Level.activeLevel.getLevelId() == 1 ) {
			if( storyState < 3 )
				storyState = 3;
			else
				GameManager.getInstance().playComment((byte)5);
		}
		// nearly dead
		if( storyState < 5 && Player.getInstance().getAlcLevel() <= 15 ) {
			GameManager.getInstance().playComment((byte)24);
			storyState = 5;
		}
		// killed the Mensamonster
		if( enemyType == 101 ) {
			GameManager.getInstance().playComment((byte)59);
			storyState = 90;
		}
		// killed the Schmelzgolem
		if( enemyType == 102 ) {
			GameManager.getInstance().playComment((byte)69);
		}
		// killed Walpurga
		else if( enemyType == 103 ) {
			GameManager.getInstance().playComment((byte)82);
			storyState = 101;
		}
		
		// count killed enemys and give player new special attack abilities
		switch( Player.getInstance().getWeapon() ) {
		case Player.WEAPON_COMENIUS:
			killedWithCormenius++;
			if( killedWithCormenius == 5 )
				GameManager.getInstance().playComment((byte)74);
			else if( killedWithCormenius == 10 ) {
				Player.getInstance().activateSpecialAttack(Player.WEAPON_COMENIUS, Player.WEAPON_SPECIAL_HOLD);
				Player.getInstance().setSpecialAttack((byte)Player.WEAPON_SPECIAL_HOLD);
				((HeadUpDisplay)GuiManager.getInstance().getActiveMenu()).updateWeaponSpecialImages();
				GameManager.getInstance().playComment((byte)75);
			}
			else if( killedWithCormenius == 20 ) {
				Player.getInstance().activateSpecialAttack(Player.WEAPON_COMENIUS, Player.WEAPON_SPECIAL_SHADOW_DRAWING);
				Player.getInstance().setSpecialAttack((byte)Player.WEAPON_SPECIAL_SHADOW_DRAWING);
				((HeadUpDisplay)GuiManager.getInstance().getActiveMenu()).updateWeaponSpecialImages();
				GameManager.getInstance().playComment((byte)76);
			}
			break;
		case Player.WEAPON_CLUB:
			killedWithKeule++;
			if( killedWithKeule == 10 )
				GameManager.getInstance().playComment((byte)74);
			else if( killedWithKeule == 20 ) {
				Player.getInstance().activateSpecialAttack(Player.WEAPON_CLUB, Player.WEAPON_SPECIAL_CLUB);
				Player.getInstance().setSpecialAttack(Player.WEAPON_SPECIAL_CLUB);
				((HeadUpDisplay)GuiManager.getInstance().getActiveMenu()).updateWeaponSpecialImages();
				GameManager.getInstance().playComment((byte)77);
			}
			break;
		case Player.WEAPON_SWORD:
			killedWithDrinkalibur++;
			if( killedWithDrinkalibur == 15 )
				GameManager.getInstance().playComment((byte)74);
			else if( killedWithDrinkalibur == 30 ) {
				Player.getInstance().activateSpecialAttack(Player.WEAPON_SWORD, Player.WEAPON_SPECIAL_SWORD);
				Player.getInstance().setSpecialAttack(Player.WEAPON_SPECIAL_SWORD);
				((HeadUpDisplay)GuiManager.getInstance().getActiveMenu()).updateWeaponSpecialImages();
				GameManager.getInstance().playComment((byte)77);
			}
			break;
		}

	}
	
	public void onDoorOpened(short objectSerial) {
	
	}
	
	public void onContainerOpened(short objectSerial) {
	
	}
	
	public void onBreakableDestroyed(short objectSerial) {
	
	}
	
	public void onMoveableMoved(short objectSerial) {
	
	}
	
	public void onDamagerHit(short objectSerial) {
	
	}
	
	public void onTeleported(Position target) {
		
	}
	
	public void onItemUsed(byte itemId) {
		// part of Drinkalibur
		if( itemId == 8 ) {
			GameManager.getInstance().playComment((byte)71);
		}
		// another part of Drinkalibur
		else if( itemId == 9 ) {
			GameManager.getInstance().playComment((byte)72);
		}
		// a key
		else if( itemId == 10 || itemId == 11 ) {
			GameManager.getInstance().playComment((byte)70);
		}
		// mosaik part
		else if( itemId == 12 ) {
			GameManager.getInstance().playComment((byte)31);
		}
		// destiller part
		else if( itemId == 13 ) {
			GameManager.getInstance().playComment((byte)32);
		}
	}
	
	public void onItemCollected(byte itemId) {
		// got Bier
		if( itemId == 1) { GameManager.getInstance().playComment((byte)7); }
		// got Wein
		else if( itemId == 2) { GameManager.getInstance().playComment((byte)33); }
		// got Met
		else if( itemId == 3) { GameManager.getInstance().playComment((byte)34); }
		// got the Cormenius
		else if( itemId == 4 ) {
			Player.getInstance().setWeapon((byte)0);
			Player.getInstance().setSpecialAttack((byte)-1);
			((HeadUpDisplay)GuiManager.getInstance().getActiveMenu()).updateWeaponSpecialImages();
			GameManager.getInstance().playComment((byte)3);
		}
		// got the Keule
		else if( itemId == 5 ) {
			GameManager.getInstance().playComment((byte)30);
		}
		// last part of Drinkalibur
		else if( itemId == 7 ) {
			GameManager.getInstance().changeLevel((byte)3, new Position(53,31));
			Player.getInstance().removeItemFromInventory((byte)7);
			Player.getInstance().removeItemFromInventory((byte)8);
			Player.getInstance().removeItemFromInventory((byte)9);
			Player.getInstance().addItemToInventory((byte)6);
			Player.getInstance().setWeapon(Player.WEAPON_SWORD);
			Player.getInstance().setSpecialAttack((byte)-1);
			GameManager.getInstance().playCutScene((byte)3);
			GameManager.getInstance().playComment((byte)57);
		}
		// part of Drinkalibur
		else if( itemId == 8 ) {
			GameManager.getInstance().playComment((byte)71);
		}
		// another part of Drinkalibur
		else if( itemId == 9 ) {
			GameManager.getInstance().playComment((byte)72);
		}
		// a key
		else if( itemId == 10 || itemId == 11 ) {
			GameManager.getInstance().playComment((byte)70);
		}
		// mosaik part
		else if( itemId == 12 ) {
			GameManager.getInstance().playComment((byte)31);
		}
		// destiller part
		else if( itemId == 13 ) {
			GameManager.getInstance().playComment((byte)32);
		}
	}
	
	public void onStoryTriggerStudentsCamp(byte storyId) {
		switch( storyId ) {
		// before leaving his home without the cormenius
		case 2: {
			if( !Player.getInstance().hasItem(Item.ITEM_CORMENIUS) )
				GameManager.getInstance().playComment((byte)2);
			// or when standing before the door
			else if( !((Door)Level.activeLevel.getObject((short)1)).isOpen() )
				GameManager.getInstance().playComment((byte)26);
		} break;
		// when meeting the first zombies
		case 3: {
			if( storyState < 3 )
				GameManager.getInstance().playComment((byte)4);
		} break;
		}
	}
	
	public void onStoryTriggerForest(byte storyId) {
		switch( storyId ) {
		// standing in front of the mensa
		case 1: {
			if( storyState < 10 ) {
				GameManager.getInstance().playCutScene((byte)1);
				GameManager.getInstance().playComment((byte)8);
				storyState = 10;
			}			
		} break;
		// standing in front of the number riddle block
		case 10: {
			if( storyState == 90) {
				GameManager.getInstance().playRiddle(new NumberRiddle());
				Player.getInstance().teleport(new Position(161,56));
			}
			else if( storyState < 90 )
				GameManager.getInstance().playComment((byte)58);
		} break;
		// go to main complex
		case 11: {
			GameManager.getInstance().changeLevel((byte)9, new Position(17,153));
			GameManager.getInstance().playCutScene((byte)4);
		} break;
		}
	}
	
	public void onStoryTriggerBlacksmith(byte storyId) {
		switch( storyId ) {
		// container with part of drinkalibur
		case 0: {
			// open the secret passage
			if( !((Container)Level.activeLevel.getObject((short)26)).isOpen() 
					&& !Player.getInstance().hasItem((byte)10) )
				GameManager.getInstance().playComment((byte)47);
		} break;
		// start of mosaik riddle
		case 1: {
			if( storyState < 50 ) {
				// must have all sword parts
				if( !Player.getInstance().hasItem(Item.ITEM_DRINK_PART2) || !Player.getInstance().hasItem(Item.ITEM_DRINK_PART3) ) {
					GameManager.getInstance().playComment((byte)17);
				}
				// must have the missing mosaik part
				else if( !Player.getInstance().hasItem(Item.ITEM_MOSAIK) ) {
					GameManager.getInstance().playComment((byte)18);
				}
				else {
					GameManager.getInstance().playRiddle(new MosaikRiddle());
					Player.getInstance().teleport(new Position(52,17));
				}
			}
		} break;
		// seeing the mosaik on the wall
		case 2: {
			if( storyState < 50 ) {
				GameManager.getInstance().playComment((byte)19);
			}
		} break;
		}
		
	}
	
	public void onStoryTriggerAlchemie(byte storyId) {
		switch( storyId ) {
		// seeing the destiller from right
		case 1: {
			if( Player.getInstance().getAlcLevelMaximum() < 50 
					&& Player.getInstance().getDirection() == Level.dirLeft)
				GameManager.getInstance().playComment((byte)67);
			break;
		} 
		// seeing the destiller from left
		case 3: {
			if( Player.getInstance().getAlcLevelMaximum() < 50 
					&& Player.getInstance().getDirection() == Level.dirRight)
				GameManager.getInstance().playComment((byte)67);
			break;
		} 
		// the bridge: left to right
		case 2: {
			if( Player.getInstance().getAlcLevelMaximum() < 50 ) {
				if( Player.getInstance().getItemCount(Item.ITEM_DISTILLER) == 4 ) {
					GameManager.getInstance().playRiddle(new DestillierRiddle());
					Player.getInstance().teleport(new Position(26,48));
				}
				else
					GameManager.getInstance().playComment((byte)66);
				
			}
			break;
		} 
		}
	}

	public void onStoryTriggerMightAndMagic(byte storyId) {
		switch( storyId ) {
		// the bridge: right to left
		case 1: {
			if( Player.getInstance().running() && Player.getInstance().getDirection() == Level.dirLeft) {
				Player.getInstance().teleport(new Position(11,36));
			} else {
				GameManager.getInstance().playComment((byte)35);
			}
			break;
		} 
		// the bridge: left to right
		case 2: {
			if( Player.getInstance().running() && Player.getInstance().getDirection() == Level.dirRight) {
				Player.getInstance().teleport(new Position(13,36));
			} else {
				GameManager.getInstance().playComment((byte)35);
			}
			break;
		} 
		// entering the room with the hanoi riddle
		case 3: {
			if( !Player.getInstance().isSpecialAttackAvailable(Player.WEAPON_COMENIUS, (byte)0) )
				GameManager.getInstance().playComment((byte)36);
			break;
		} 
		// the hanoi riddle
		case 4: {
			if( !Player.getInstance().isSpecialAttackAvailable(Player.WEAPON_COMENIUS, (byte)0) ) {
				GameManager.getInstance().playRiddle(new HanoiRiddle());
				Player.getInstance().teleport(new Position(66,33));
			}
			break;
		} 
		
		}
	}
	
	public void onStoryTriggerMainComplex(byte storyId) {
		switch( storyId ) {
		// entering the room with the mastermind riddle
		case 1: {
			if( storyState < 100 )
				GameManager.getInstance().playComment((byte)79);
			break;
		} 
		// the mastermind riddle
		case 2: {
			if( storyState < 100 ) {
				GameManager.getInstance().playRiddle(new MastermindRiddle());
				Player.getInstance().teleport(new Position(22,58));
			}
			break;
		}
		// wants to go to the president
		case 3: {
			// not killed walpurga yet
			if( storyState < 101 )
				GameManager.getInstance().playComment((byte)81);
			// the BIG FINAL
			else {
				GameManager.getInstance().playCutScene((byte)5);
				GameManager.getInstance().playCredits();
				Player.getInstance().teleport(new Position(73,11));
			}
			break;
		} 
		// go back to forest
		case 4: {
			GameManager.getInstance().changeLevel((byte)2, new Position(161,58));
			GameManager.getInstance().playCutScene((byte)4);
		} break;
		}
	}
}
