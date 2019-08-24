package LevelObjects;
import Player;
import Position;

import java.util.*;


public class Broccoli extends Projectile {

	private byte damage = 5;
	private byte stun = 0;
	private byte specialId = 0;
	private boolean tripmine = false;

	public Broccoli(Position launchPosition, byte direction, byte speed, short maxDistance) {
		super((byte)0, launchPosition, direction, speed, maxDistance, 16, 16);
	}
	
	protected int[] getFrameSequence(byte animation) {
		switch(animation) {
			case DynamicLevelObject.ANIM_RIGHT:
			case DynamicLevelObject.ANIM_LEFT:
			{
				int[] i = {0,1};
				return i;
			}
			case DynamicLevelObject.ANIM_DOWN:
			case DynamicLevelObject.ANIM_UP:
			{
				int[] i = {0,1};
				return i;
			}
			case DynamicLevelObject.ANIM_DIE:
			{
				int[] i = {2};
				return i;
			}
			case DynamicLevelObject.ANIM_DEAD:
			{
				int[] i = {2};
				return i;
			}
		}
		// else
		int[] i = {0};
		return i;
	}
	
	protected byte getFrameDuration(byte animation, byte frame) {
		return 1;
	}
	
	protected void onGroundImpact() {
		// projectile stops movement by itself.
	}
	
	protected void onPlayerImpact() {
		if(tripmine) {
			Player.getInstance().receiveDamage(damage);
		}
		else {
			Player.getInstance().receiveDamage(damage);
		}
		
		delete();
	}
	
	protected void onNpcImpact(Vector objects) {
		for(int i = 0; i < objects.size(); i++) {
			DynamicLevelObject obj = (DynamicLevelObject)objects.elementAt(i);
			try {
				if(obj.getClass() == Class.forName("NPC")) {
					NPC n = (NPC)obj;
					n.receiveAttack(damage, stun, specialId);					
				}
			}
			catch(Exception e) {
				
			}
		}
		
		delete();
	}
	
	protected void onProjectileDead() {
		
	}
}
