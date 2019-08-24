package Gui;

import java.util.Vector;

import Error;
import GameManager;
import IODevice;
import Player;
import LevelObjects.Item;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

/**
 * used to draw the inventory 
 * @author Paul/Bertille, MartinW
 *
 */
class InventoryGUI implements Menu {
	
	private static final short halfDisplayWidth = (short)(IODevice.getInstance().getWidth() / 2),
                             halfDisplayHeigth = (short)(IODevice.getInstance().getHeight() / 2);

	private int mainAngle = 90,
                targetAngle = 90;

	private int itemRadiusX = 50,
                itemRadiusY = 35;

	private byte itemsCount = 0;

	private byte currentItem = 0;

	private byte[] itemTypeCounts = new byte[15];

	private static final byte SUB_NONE = 0,
                              SUB_CORMENIUS = 1;

	private byte activeSubInventory = SUB_NONE;

	boolean rotateRight = true;

	private static final byte inventoryItemSize = 16,
                              inventoryItemBigSize = 24;
	
	//public static boolean InventoryActive=false;

	private Image inventoryItems,
                  inventoryBigItems;

	private String[] itemStrings = { "Bier trinken", "Wein trinken", "Met trinken",
			                         "Waffe: Cormenius", "Waffe: Keule", "Waffe: Drinkalibur",
			                         "Schwertspitze", "Parierstange", "Schwertheft",
			                         "kleiner Schlüssel", "großer Schlüssel", 
			                         "kleine Steintafel", "Destilliererteil"
	                               };
	private String[] specialStrings = { "'Geschwind' wählen", "'Schattenbild' wählen", "'Einfrieren' wählen"};
	
	//konstruktor
	public InventoryGUI() {
		for (int i = 0; i < 15; i++)
			itemTypeCounts[i] = 0;
		Vector inventory = Player.getInstance().getInventory();
		for (int i = 0; i < inventory.size(); i++) {
			Byte item = (Byte) inventory.elementAt(i);
			itemTypeCounts[item.byteValue()]++;
			if (itemTypeCounts[item.byteValue()] == 1)
				itemsCount++;
		}
		// create item image
		try {
			inventoryItems = Item.getItemsImage();
			inventoryBigItems = Image.createImage("/InventoryItems.png");
		} catch (Exception e) {
			new Error("InventoryGUI inventoryItems+inventoryBigItems", e);
		}
	}
	
	/*
	 * return the  current Object
	 */
	public  int getCurrentItem(){
		return currentItem;
	}


	private void enterSubInventory(byte subInventory) {
		activeSubInventory = subInventory;
		for (int i = 0; i < 3; i++)
			itemTypeCounts[i] = 0;

		itemsCount = 0;
		byte activeItem = 0;
		for( byte special = 0; special < 3; special++ ) {
			if( Player.getInstance().isSpecialAttackAvailable(Player.WEAPON_COMENIUS, special) ) {
				itemTypeCounts[special] = 1;
				itemsCount++;
				if( Player.getInstance().getSpecialAttack() == special )
					activeItem = (byte)(itemsCount-1);
			}
		}
		if( itemsCount == 0 ) {
			Player.getInstance().setSpecialAttack((byte)-1);
			GameManager.getInstance().continueGame();
			return;
		}
		else if( itemsCount == 1 ) {
			Player.getInstance().setSpecialAttack(getItemType((byte)(activeItem-1)));
			GameManager.getInstance().continueGame();
			return;
		}
		changeCurrentItem(activeItem);
		mainAngle = targetAngle;
		// create special attacks image
		try {
			inventoryItems = Image.createImage("/Specials.png");
			inventoryBigItems = Image.createImage("/SpecialsBig.png");
		} catch (Exception e) {
			new Error("InventoryGUI enterSubInventory inventoryItems+inventoryBigItems", e);
		}
	}

	/*
	 * change the current Item
	 */
	private void changeCurrentItem(byte item) {
		if (item < 0) {
			item = (byte) (itemsCount - 1);
		}
		else if (item >= itemsCount) {
			item = 0;
		}
		currentItem = item;
		targetAngle = 9 + item * 36 / itemsCount;
		targetAngle *= 10;
		if (targetAngle >= 360)
			targetAngle -= 360;
	}

	/*
	 * get the current Item typ
	 */
	private byte getItemType(byte item) {
		byte itemType = -1;
		byte counter = 0;
		while (counter <= item) {
			itemType++;
			if (itemTypeCounts[itemType] > 0)
				counter++;
		}
		return itemType;
	}

	/*
	 * use the Item
	 */
	private void useItem(byte item) {
		byte itemType = getItemType(item);
		Player.getInstance().useItem(itemType);
	}
	
	/* (non-Javadoc)
	 * @see Menu#keyDown(int)
	 */
	public void keyDown(int key) {

		switch (key) {
		case GameManager.RIGHT: {
			if (itemsCount > 0) {
				currentItem += 1;
				rotateRight = true;
				changeCurrentItem(currentItem);
			}
			break;

		}
		case GameManager.LEFT: {
			if (itemsCount > 0) {
				currentItem -= 1;
				rotateRight = false;
				changeCurrentItem(currentItem);
			}
			break;
		}
		case GameManager.INVENTORY:
		case GameManager.BACK:
			GameManager.getInstance().continueGame();
			break;

		case GameManager.ATTACK:
		case GameManager.OK:
			if (itemsCount > 0) {
				if (activeSubInventory == SUB_NONE) {
					useItem(currentItem);
					byte currentItemType = getItemType(currentItem);
					if (currentItemType == Item.ITEM_CORMENIUS) {
						Player.getInstance().setWeapon(Player.WEAPON_COMENIUS);
						enterSubInventory(SUB_CORMENIUS);
					} else if (currentItemType == Item.ITEM_KEULE) {
						Player.getInstance().setWeapon(Player.WEAPON_CLUB);
						Player.getInstance().setSpecialAttack(Player.WEAPON_SPECIAL_CLUB);
						GameManager.getInstance().continueGame();
					} else if (currentItemType == Item.ITEM_DRINKALIBUR) {
						Player.getInstance().setWeapon(Player.WEAPON_SWORD);
						Player.getInstance().setSpecialAttack(Player.WEAPON_SPECIAL_SWORD);
						GameManager.getInstance().continueGame();
					} else
						GameManager.getInstance().continueGame();
				} else {
					Player.getInstance().setSpecialAttack(getItemType((byte)currentItem));
					GameManager.getInstance().continueGame();
				}
			} else
				GameManager.getInstance().continueGame();
			break;

		}

	}

	/* (non-Javadoc)
	 * @see Menu#keyUp(int)
	 */
	public void keyUp(int key) {}
	
	
    /*
     * draw the inventory Menu
     */
	private void renderInventory(Graphics g) 
	{
		// background text
		g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
		g.setColor(255,255,255);
		g.drawString("Inventar", halfDisplayWidth, halfDisplayHeigth-itemRadiusY-14, 
				Graphics.BASELINE|Graphics.HCENTER);
		g.setFont(Font.getDefaultFont());
		
		// items
		int angle = mainAngle;
		int x;
		for (int u = 0; u < itemsCount; u++) {
			byte itemType = getItemType((byte) u);
			int itemX = calculatecoordinateX(angle, itemRadiusX);
			int itemY = calculatecoordinateY(angle, itemRadiusY);
			if (u == currentItem && mainAngle == targetAngle) {
				x = inventoryItemBigSize * (itemType - 1);
				g.drawRegion(inventoryBigItems, x, 0, inventoryItemBigSize,
						inventoryItemBigSize, Sprite.TRANS_NONE, itemX, itemY,
						Graphics.HCENTER | Graphics.VCENTER);
				g.setColor(255,255,255);
				g.drawString(itemStrings[itemType-1], 
						halfDisplayWidth, halfDisplayHeigth+itemRadiusY-16, 
						Graphics.BASELINE|Graphics.HCENTER);
			} else {
				x = inventoryItemSize * (itemType - 1);
				g.drawRegion(inventoryItems, x, 0, inventoryItemSize,
						inventoryItemSize, Sprite.TRANS_NONE, itemX, itemY,
						Graphics.HCENTER | Graphics.VCENTER);
			}
			if (itemTypeCounts[itemType] > 1) {
				g.setColor(0, 0, 0);
				g.drawString(String.valueOf(itemTypeCounts[itemType]),
						itemX + 1, itemY + 1, Graphics.LEFT | Graphics.TOP);
				g.setColor(255,255,255);
				g.drawString(String.valueOf(itemTypeCounts[itemType]), itemX,
						itemY, Graphics.LEFT | Graphics.TOP);
			}
			angle -= 360 / itemsCount;
		}
	}

	/*
	 * draw the inventory sub Menu
	 */
	private void renderSubInventory(Graphics g) {
		
		// background text
		g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
		g.setColor(255,255,255);
		g.drawString("Cormenius-Zauber", halfDisplayWidth, halfDisplayHeigth-itemRadiusY-14, 
				Graphics.BASELINE|Graphics.HCENTER);
		g.setFont(Font.getDefaultFont());
		
		// specials
		int angle = mainAngle;
		int x;
		for (int u = 0; u < itemsCount; u++) {
			byte itemType = getItemType((byte)u);
			if (u == currentItem && mainAngle == targetAngle) {
				x = inventoryItemBigSize * itemType;
				g.drawRegion(inventoryBigItems, x, 0, inventoryItemBigSize,
						inventoryItemBigSize, Sprite.TRANS_NONE,
						calculatecoordinateX(angle, itemRadiusX),
						calculatecoordinateY(angle, itemRadiusY),
						Graphics.HCENTER | Graphics.VCENTER);
				g.setColor(255,255,255);
				g.drawString(specialStrings[itemType], 
						halfDisplayWidth, halfDisplayHeigth+itemRadiusY-16, 
						Graphics.BASELINE|Graphics.HCENTER);
			} else {
				x = inventoryItemSize * (itemType);
				g.drawRegion(inventoryItems, x, 0, inventoryItemSize,
						inventoryItemSize, Sprite.TRANS_NONE,
						calculatecoordinateX(angle, itemRadiusX),
						calculatecoordinateY(angle, itemRadiusY),
						Graphics.HCENTER | Graphics.VCENTER);
			}
			angle -= 360 / itemsCount;
		}
	}

	/* (non-Javadoc)
	 * @see Menu#renderMenu(javax.microedition.lcdui.Graphics)
	 */
	public void renderMenu(Graphics g) {
		if (itemsCount > 0) {
			if (mainAngle != targetAngle) {
				if (rotateRight) {
					mainAngle += 10;
					if (mainAngle > 360)
						mainAngle = 0;
				} else {
					mainAngle -= 10;
					if (mainAngle < 0) {
						mainAngle = 360;
					}
				}
			}

			if (activeSubInventory == SUB_NONE)
				renderInventory(g);
			else
				renderSubInventory(g);
		}
		// leave if inventory is empty
		else {
			GameManager.getInstance().continueGame();
		}
	}

	/*
	 * 
	 */
	public int calculatecoordinateX(int angle, int radius) {
		double radian = Math.PI * angle / 180.0;
		return (int) (halfDisplayWidth + radius * Math.cos(radian));

	}
    /*
     * 
     */
	public int calculatecoordinateY(int angle, int radius) {
		double radian = Math.PI * angle / 180.0;
		return (int) (halfDisplayHeigth + radius * Math.sin(radian));
	}

	public void activeItem(int position) {}

}
