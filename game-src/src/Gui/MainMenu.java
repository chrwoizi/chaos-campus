package Gui;

import GameMIDlet;
import GameManager;
import IODevice;
import Error;
import SaveGameManager;

import javax.microedition.lcdui.*;


/**
 * 
 */

/**
 * @author Paul ; Bertille
 *draw the main menu
 */
public class MainMenu implements Menu {
    
	private static Image backgroundImage;
    private int currentItem = 0;
    
    private int color2 = 0xFFFF00,
			    color3 = 0x000000, 
			    color4 = 0x000000, 
			    color5 = 0x000000;

	private String[] menuItem = { "Spiel starten", "Einführung", 
			                      "Sound", "Beenden" };
    
	private int coordinateItemX;
	private int coordinateItemY = IODevice.getInstance().getHeight() / 2;

	Manuals introductionManual = null;
	
	/**
	 * returns the background image used by all menus
	 * @return
	 */
	public static Image getBackgroundImage() {
		if( backgroundImage == null ) {
			try {
				backgroundImage = Image.createImage("/MenuBack.png");
			} catch (Exception e) {
				new Error(e);
			}
		}
		return backgroundImage;
	}
	
	public MainMenu() {
		coordinateItemX = IODevice.getInstance().getWidth() / 2 
			- Menu.menuFont.stringWidth(menuItem[0]) / 2;
    }
 
	
	//draw the main menu
	public void renderMenu(Graphics g) {
		// for manual
		if (introductionManual != null) {
			if (introductionManual.isFinished())
				introductionManual = null;
			else {
				introductionManual.render(g);
				return;
			}
		}

		// background
		g.setColor(14,14,56);
		g.fillRect(0, 0, IODevice.getInstance().getWidth(), 
	       IODevice.getInstance().getHeight());
        g.drawImage(getBackgroundImage(), IODevice.getInstance().getWidth()/2, 
	       IODevice.getInstance().getHeight()/2, Graphics.VCENTER|Graphics.HCENTER);

		// titel
		g.setColor(250, 200, 100);
		g.setFont(Menu.titleFont);
		g.drawString("Chaos Campus", IODevice.getInstance().getWidth()/2, 
				coordinateItemY - 40, Graphics.HCENTER| Graphics.BASELINE);

		//d raw menuItem
		g.setColor(250, 200, 100);
		g.setFont(Menu.menuFont);
		g.drawString(menuItem[0], coordinateItemX, coordinateItemY - 5, Graphics.LEFT |Graphics.BASELINE);
		g.drawString(menuItem[1], coordinateItemX, coordinateItemY + 15,Graphics.LEFT |Graphics.BASELINE);
		g.drawString(menuItem[2], coordinateItemX, coordinateItemY + 35, Graphics.LEFT |Graphics.BASELINE);
		g.drawString(menuItem[3], coordinateItemX,coordinateItemY + 55, Graphics.LEFT |Graphics.BASELINE);

		//draw circle in front of menuItem
		g.setColor(color2);//gelb
		g.fillRoundRect(coordinateItemX-12, coordinateItemY - 12, 6, 6, 4, 4);
		g.setColor(50, 0, 0);
		g.drawRoundRect(coordinateItemX-12, coordinateItemY - 12, 6, 6, 4, 4);
		g.setColor(color3);//schwarz
		g.fillRoundRect(coordinateItemX-12, coordinateItemY + 8, 6, 6, 4, 4);
		g.setColor(50, 0, 0);
		g.drawRoundRect(coordinateItemX-12, coordinateItemY + 8, 6, 6, 4, 4);
		g.setColor(color4);
		g.fillRoundRect(coordinateItemX-12, coordinateItemY + 28, 6, 6, 4, 4);
		g.setColor(50, 0, 0);
		g.drawRoundRect(coordinateItemX-12, coordinateItemY + 28, 6, 6, 4, 4);
		g.setColor(color5);
		g.fillRoundRect(coordinateItemX-12, coordinateItemY + 48, 6, 6, 4, 4);
		g.setColor(50, 0, 0);
		g.drawRoundRect(coordinateItemX-12, coordinateItemY + 48, 6, 6, 4, 4);

		//select the active Item
		activeItem(currentItem);

	}
    

	
	//aktiviert ein Objekt
	public void activeItem(int position) {
		switch (position) {
		case 0:
			color2 = 0xFFFF00;
			color3 = 0x000000;
			color4 = 0x000000;
			color5 = 0x000000;
            break;
		case 1:
			color2 = 0x000000;
			color3 = 0xFFFF00;
			color4 = 0x000000;
			color5 = 0x000000;
			break;
		case 2:
			color2 = 0x000000;
			color3 = 0x000000;
			color4 = 0xFFFF00;
			color5 = 0x000000;
			break;
		case 3:
			color2 = 0x000000;
			color3 = 0x000000;
			color4 = 0x000000;
			color5 = 0xFFFF00;
			break;
		default:
			color2 = 0xFFFF00;
			color3 = 0x000000;
			color4 = 0x000000;
			color5 = 0x000000;
			break;
		}

	}

	/* (non-Javadoc)
	 * @see Menu#keyDown(int)
	 */
	public void keyDown(int key) {
		// for manual
		if (introductionManual != null) {
			introductionManual.onKeyDown(key);
			return;
		}

		switch (key) {
		case GameManager.DOWN: {
			currentItem += 1;
			if (currentItem == 4) {
				currentItem = 0;
			}
			activeItem(currentItem);
			break;

		}
		case GameManager.UP:
			currentItem -= 1;
			if (currentItem == -1) {
				currentItem = 3;
			}
			activeItem(currentItem);
			break;

		//select Item
		case GameManager.ATTACK:
		case GameManager.OK: {
			switch (currentItem) {
			case 0:
				// when no game exists just start a new game
				if( !SaveGameManager.getInstance().exists(GameManager.SAVEGAME_NAME) ) {
					GameManager.getInstance().newGame();
				}
				else
					GuiManager.getInstance().setActiveMenu(GuiManager.START_MENU);
				break;
				
			case 1:
				IODevice.getInstance().setLeftCommand(IODevice.COMMANDTYPE_BACK);
				IODevice.getInstance().setRightCommand(IODevice.COMMANDTYPE_NONE);
				introductionManual = new Manuals(Manuals.INTRODUCTION);
				break;

			case 2:
				GuiManager.getInstance().setActiveMenu(GuiManager.SOUND_MENU);
				break;
				
			case 3:
				GameMIDlet.getInstance().shutdownApp();
				break;
			}
		}
			break;

		}

	}

	/* (non-Javadoc)
	 * @see Menu#keyUp(int)
	 */
	public void keyUp(int key) {}

}
