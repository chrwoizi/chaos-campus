/**
 * 
 */
package Gui;

import GameManager;
import IODevice;


import javax.microedition.lcdui.Graphics;

/**
 * @author PAUL
 *
 */
public class PauseMenu implements Menu {
	
	private int currentItem = 0;

	private int coordinateItemX;
    private int coordinateItemY = IODevice.getInstance().getHeight() / 2;

	private String[] menuItem = { "Fortsetzen", "Speichern",
			                      "Sound", "Speichern & beenden", "Spiel beenden" };
    private int color1 = 0xFFFF00, 
    			color2 = 0x000000, 
	            color3 = 0x000000, 
	            color4 = 0x000000,
    			color5 = 0x000000;

	
	public PauseMenu() {
		coordinateItemX = IODevice.getInstance().getWidth() / 2 
			- Menu.menuFont.stringWidth(menuItem[3]) / 2;
		coordinateItemX += 10;
		coordinateItemY -= 10;
	}

	
	/* (non-Javadoc)
	 * @see Gui.Menu#keyDown(int)
	 */
	public void keyDown(int key) {
		switch (key) {
		case GameManager.DOWN: {
			currentItem += 1;
			if (currentItem > 4) {
				currentItem = 0;
			}
			activeItem(currentItem);
			break;

		}
		case GameManager.UP:
			currentItem -= 1;
			if (currentItem < 0) {
				currentItem = 4;
			}
			activeItem(currentItem);
			break;

		//select Item
		case GameManager.ATTACK:
		case GameManager.OK: {
			switch (currentItem) {
			case 0:
				GameManager.getInstance().continueGame();
				break;
			case 1:
				if (!GameManager.getInstance().saveGame(GameManager.SAVEGAME_NAME)) {
					new Error("Could not save game: " + GameManager.SAVEGAME_NAME);
				}
				GameManager.getInstance().continueGame();
				break;
			case 2:
				GuiManager.getInstance().setActiveMenu(GuiManager.SOUND_MENU);
                break;
			case 3:
				if (!GameManager.getInstance().saveGame(GameManager.SAVEGAME_NAME)) {
					new Error("Could not save game: " + GameManager.SAVEGAME_NAME);
				}
				GameManager.getInstance().leaveGame();
				break;
			case 4:
				GameManager.getInstance().leaveGame();
				break;
			}
			break;
		}
		}

	}

	/* (non-Javadoc)
	 * @see Gui.Menu#keyUp(int)
	 */
	public void keyUp(int key) {}

	/* (non-Javadoc)
	 * @see Gui.Menu#renderMenu(javax.microedition.lcdui.Graphics)
	 */
	public void renderMenu(Graphics g) 
	{
		// background
		g.setColor(14,14,56);
		g.fillRect(0, 0, IODevice.getInstance().getWidth(), 
	       IODevice.getInstance().getHeight());
        g.drawImage(MainMenu.getBackgroundImage(), IODevice.getInstance().getWidth()/2, 
	       IODevice.getInstance().getHeight()/2, Graphics.VCENTER|Graphics.HCENTER);

		// titel
		g.setColor(250, 200, 100);
		g.setFont(Menu.titleFont);
		g.drawString("Pause", IODevice.getInstance().getWidth()/2, 
				coordinateItemY - 40, Graphics.HCENTER| Graphics.BASELINE);

		//draw menuItem
		g.setFont(Menu.menuFont);
		g.drawString(menuItem[0], coordinateItemX, coordinateItemY - 5, Graphics.LEFT |Graphics.BASELINE);
		g.drawString(menuItem[1], coordinateItemX, coordinateItemY + 15, Graphics.LEFT |Graphics.BASELINE);
		g.drawString(menuItem[2], coordinateItemX, coordinateItemY + 35, Graphics.LEFT |Graphics.BASELINE);
		g.drawString(menuItem[3], coordinateItemX, coordinateItemY + 55, Graphics.LEFT |Graphics.BASELINE);
		g.drawString(menuItem[4], coordinateItemX, coordinateItemY + 75, Graphics.LEFT |Graphics.BASELINE);

		

		//draw circle in front of menuItem
		g.setColor(color1);
		g.fillRoundRect(coordinateItemX-12, coordinateItemY - 12, 6, 6, 4, 4);
		g.setColor(50, 0, 0);
		g.drawRoundRect(coordinateItemX-12, coordinateItemY - 12, 6, 6, 4, 4);
		g.setColor(color2);
		g.fillRoundRect(coordinateItemX-12, coordinateItemY + 8, 6, 6, 4, 4);
		g.setColor(50, 0, 0);
		g.drawRoundRect(coordinateItemX-12, coordinateItemY + 8, 6, 6, 4, 4);
		g.setColor(color3);
		g.fillRoundRect(coordinateItemX-12, coordinateItemY + 28, 6, 6, 4, 4);
		g.setColor(50, 0, 0);
		g.drawRoundRect(coordinateItemX-12, coordinateItemY + 28, 6, 6, 4, 4);
		g.setColor(color4);
		g.fillRoundRect(coordinateItemX-12, coordinateItemY + 48, 6, 6, 4, 4);
		g.setColor(50, 0, 0);
		g.drawRoundRect(coordinateItemX-12, coordinateItemY + 48, 6, 6, 4, 4);
		g.setColor(color5);
		g.fillRoundRect(coordinateItemX-12, coordinateItemY + 68, 6, 6, 4, 4);
		g.setColor(50, 0, 0);
		g.drawRoundRect(coordinateItemX-12, coordinateItemY + 68, 6, 6, 4, 4);

		//select the active Item
		activeItem(currentItem);

	}
	/* (non-Javadoc)
	 * @see Gui.Menu#activeItem(int)
	 */
	public void activeItem(int position) {
		switch (position) {
		case 0:
			color1 = 0xFFFF00;
			color2 = 0x00000;
			color3 = 0x000000;
			color4 = 0x000000;
			color5 = 0x000000;
			break;
		case 1:
			color1 = 0x000000;
			color2 = 0xFFFF00;
			color3 = 0x000000;
			color4 = 0x000000;
			color5 = 0x000000;
			break;
		case 2:
			color1 = 0x000000;
			color2 = 0x000000;
			color3 = 0xFFFF00;
			color4 = 0x000000;
			color5 = 0x000000;
			break;
		case 3:
			color1 = 0x000000;
			color2 = 0x000000;
			color3 = 0x000000;
			color4 = 0xFFFF00;
			color5 = 0x000000;
			break;
		case 4:
			color1 = 0x000000;
			color2 = 0x000000;
			color3 = 0x000000;
			color4 = 0x000000;
			color5 = 0xFFFF00;
			break;
		}

	}


}
