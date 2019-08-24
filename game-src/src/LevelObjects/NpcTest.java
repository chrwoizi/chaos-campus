package LevelObjects;

import Block;
import Level;
import LevelObjects.NPC;
import LevelObjects.DynamicLevelObject;
import Position;

/**
 * The class NpcTest which tests NPC over its child Zombie, ZombieMaster, Werewolf, Skeleton
 * @author Malte Mauritz
 *
 */
public class NpcTest {
	
	/**
	 * The main test routine - initializing and running the NPC test
	 * @param enemy The NPC which the method has to use
	 */	
	public static void test() {
		try {
			System.out.println("--- Starting NPC-Test ---");
			Zombie zombie = new Zombie((short)1, new Position(1,1),new Block(1,1), false, 0, (byte)0);
			zombie.invStunPossibility=1; //Set to 100% that the Stun, Receive and Dazed Tests work
			System.out.print(" Zombie-Test: "+ zombie.imageTest(zombie,0) + animationTest(zombie) + attackTest(zombie) + receiveTest(zombie));
			System.out.println("Zonbie-Test finished");
			ZombieMaster zombiemaster = new ZombieMaster((short)1, new Position(1,1),new Block(1,1), false, 3, (byte)0);
			zombiemaster.invStunPossibility=1; //Set to 100% that the Stun, Receive and Dazed Tests work
			System.out.println(" ZombieMaster-Test: "+ zombiemaster.imageTest(zombiemaster,0) + animationTest(zombiemaster) + attackTest(zombiemaster) + receiveTest(zombiemaster));
			System.out.println("ZonbieMaster-Test finished");
			Werewolf wolf = new Werewolf((short)1, new Position(1,1),new Block(1,1), false, 2, (byte)0);
			wolf.invStunPossibility=1; //Set to 100% that the Stun, Receive and Dazed Tests work
			System.out.println(" Werewolf-Test: "+ wolf.imageTest(0) + animationTest(wolf) + attackTest(wolf) + receiveTest(wolf));
			System.out.println("Werewolf-Test finished");
			Skeleton skeleton= new Skeleton((short)1, new Position(1,1),new Block(1,1), false, 1, (byte)0);
			skeleton.invStunPossibility=1; //Set to 100% that the Stun, Receive and Dazed Tests work
			System.out.println(" Skeleton-Test: "+ skeleton.imageTest(0) + animationTest(skeleton) + attackTest(skeleton) + receiveTest(skeleton));
			System.out.println("Skeleton-Test finished");
			System.out.println(" NpcCreator-Test: " + NpcCreator.test());
			System.out.println("NPCCreator-Test finished");
			System.out.println("---Overall Npc-Test finished---");
		}
		catch(Exception e) {
			System.out.println("Exception in Npc Test: "+e.toString());
		}
	}
		
	/**
	 * Tests the animations of the classes
	 * @param enemy The NPC which the method has to use
	 */
	private static String animationTest(NPC enemy) {
		String result = "";
		try {
		  if (enemy.testAnimation(DynamicLevelObject.ANIM_NONE)== false) result = result + "Animation should be 'Idle', but is " + enemy.getAnimation();
		  enemy.move(Level.dirUp,(byte)1);
		  if (enemy.testAnimation(DynamicLevelObject.ANIM_UP) == false) result = result + "Animation should be 'UP', but is " + enemy.getAnimation();
		  enemy.move(Level.dirLeft,(byte)1);
		  if (enemy.testAnimation(DynamicLevelObject.ANIM_LEFT) == false) result = result + "Animation should be 'Left', but is " + enemy.getAnimation();
		  enemy.move(Level.dirRight,(byte)1); 
		  if (enemy.testAnimation(DynamicLevelObject.ANIM_RIGHT) == false) result = result + "Animation should be 'Right', but is " + enemy.getAnimation();
		  enemy.move(Level.dirDown,(byte)1);
		  if (enemy.testAnimation(DynamicLevelObject.ANIM_DOWN) == false) result = result + "Animation should be 'Down', but is " + enemy.getAnimation();
		  enemy.receiveAttack((byte)1,(byte)2,(byte)0);
		  if (enemy.testAnimation(DynamicLevelObject.ANIM_RECEIVE) == false) result = result + "Animation should be 'Receiving', but is " + enemy.getAnimation();
		  enemy.receiveAttack((byte)1,(byte)7,(byte)4);
		  if (enemy.testAnimation(DynamicLevelObject.ANIM_DAZED) == false) result = result + "Animation should be 'Dazed', but is " + enemy.getAnimation();
		  enemy.receiveAttack((byte)enemy.getLifepoints(),(byte)0,(byte)4);
		  if (enemy.testAnimation(DynamicLevelObject.ANIM_DIE) == false) result = result + "Animation should be 'Dieing', but is " + enemy.getAnimation();
		  enemy.setLifepoints(10);
		  if (result.equals("")) return "Animation Test - Success; ";		  
		  else return result;
		}
		catch(Exception e) {
			return("Exception in animationtest: "+e.toString());
		}		
	}	
	
	/**
	 * Tests the behaviour within an attack
	 * @param enemy The NPC which the method has to use
	 */
 private static String attackTest(NPC enemy) {
	 enemy.setAttackTime(0); //set to 0 to be able to perform an attack
	 enemy.setAttackDuration((short)0); //set to 0 to be able to perform an attack
	 enemy.attack();
	 if(enemy.testAnimation(DynamicLevelObject.ANIM_ATTACK)==false) return "Animation should be 'Attacking', but is " + enemy.getAnimation();
	 if(enemy.getAttackDuration() == 0) return "AttackDuration has not been resetted! ";
	 if(enemy.getAttackTime() == 0) return "Attacktime has not been resetted! ";
	 return "Attack Test - Success; ";
 }
 
	/**
	 * Tests the behaviour within receiving an attack
	 * @param enemy The NPC which the method has to use
	 */
 private static String receiveTest(NPC enemy) {
	 String result ="";
	 enemy.receiveAttack((byte)1,(byte)2,(byte)0);
	 if(enemy.getStunDuration()==0) result = result +"Stun is not been used! ";
	 enemy.receiveAttack((byte)1,(byte)2,(byte)3);
	 if(enemy.getUnmoveableFrames()==0) result = result + "Special Freezing is not been used!";
	 if(enemy.getAttackTime()==0) result = result + "AttackTime has not been resetted!";
	 if(enemy.getAttackDuration()==0) result = result + "AttackDuration has not been resetted!";
	 enemy.receiveAttack((byte)enemy.getLifepoints(),(byte)2,(byte)3);
	 if(enemy.getLifepoints() != 0) result = result + "Lifepoints should be 0 and the enemymy dead, but he has" +enemy.getLifepoints();
	 enemy.setAttackDuration((short)10);enemy.setAttackTime(10);
	 enemy.receiveAttack((byte)4,(byte)2,(byte)3);
	 if(enemy.getLifepoints() != 0 && enemy.getAttackDuration() != 10 && enemy.getAttackTime() != 10) result = result + "The Enemy still receive Damage though he has to be dead!";
	  if (result.equals("")) return "Receive Test - Success; ";		  
	  else return result; 
 }
	
}
