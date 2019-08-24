package Gui;

import javax.microedition.lcdui.*;


/**
 * 
 */

/**
 * @author Paul
 * interface for all menu
 */
interface Menu {
	public static final Font titleFont = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_LARGE);
	public static final Font menuFont = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
	
	public void keyDown(int key);
	public void keyUp(int key);
	public void activeItem(int position);
	public void renderMenu(Graphics  g);
}
