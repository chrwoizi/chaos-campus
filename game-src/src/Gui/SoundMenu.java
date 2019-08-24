/**
 * 
 */
package Gui;

import GameManager;
import IODevice;
import SoundManager;

import javax.microedition.lcdui.*;

/**
 * @author Paul,  Bertille
 *
 */
public class SoundMenu implements Menu {
	
	private int currentItem = 0;
	
	private String[] menuItem = { "Ein", "Aus" };

	private int coordinateItemX;
	private int coordinateItemY = IODevice.getInstance().getHeight() / 2;
	
	private int color1 = 0xFFFF00;
	private int color2 = 0x000000;

	public SoundMenu() {
		coordinateItemX = IODevice.getInstance().getWidth() / 2 
			- Menu.menuFont.stringWidth(menuItem[0]) / 2;
	}
	
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
		g.drawString("Sound", IODevice.getInstance().getWidth()/2,
				coordinateItemY - 40, Graphics.HCENTER| Graphics.BASELINE);

		//draw menuItem
		g.setFont(Menu.menuFont);
		g.drawString(menuItem[0], coordinateItemX, coordinateItemY + 10, 
				     Graphics.LEFT| Graphics.BASELINE);
		g.drawString(menuItem[1], coordinateItemX, coordinateItemY + 30, 
				     Graphics.LEFT| Graphics.BASELINE);

		//  	   draw circle in front of menuItem
		g.setColor(color1);//weiﬂ
		g.fillRoundRect(coordinateItemX-12, coordinateItemY + 2, 6, 6, 4, 4);
		g.setColor(50, 0, 0);
		g.drawRoundRect(coordinateItemX-12, coordinateItemY + 2, 6, 6, 4, 4);
		g.setColor(color2);//weiﬂ
		g.fillRoundRect(coordinateItemX-12, coordinateItemY + 22, 6, 6, 4, 4);
		g.setColor(50, 0, 0);
		g.drawRoundRect(coordinateItemX-12, coordinateItemY + 22, 6, 6, 4, 4);

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
			color2 = 0x000000;

			break;
		case 1:
			color1 = 0x000000;
			color2 = 0xFFFF00;

			break;

		}

	}

	/* (non-Javadoc)
	 * @see Gui.Menu#keyDown(int)
	 */
	public void keyDown(int key) {
		switch (key) {
		case GameManager.DOWN: {
			currentItem += 1;
			if (currentItem == 2) {
				currentItem = 0;
			}
			activeItem(currentItem);
			break;

		}
		case GameManager.UP:
			currentItem -= 1;
			if (currentItem == -1) {
				currentItem = 1;
			}
			activeItem(currentItem);
			break;

		//select Item
		case GameManager.ATTACK:
		case GameManager.OK: {
			switch (currentItem) {
			case 0:
				// Sound aktivieren
				SoundManager.getInstance().setMute(false);
				GuiManager.getInstance().setActiveMenu(GuiManager.MAIN_MENU);
				break;
			case 1:
				//Sound deaktivieren;
				SoundManager.getInstance().setMute(true);
				GuiManager.getInstance().setActiveMenu(GuiManager.MAIN_MENU);
				break;

			}
		}
			break;
			
		case GameManager.BACK:
			GuiManager.getInstance().setActiveMenu(GuiManager.MAIN_MENU);
			break;

		}

	}

	/* (non-Javadoc)
	 * @see Gui.Menu#keyUp(int)
	 */
	public void keyUp(int key) {}

	
}
