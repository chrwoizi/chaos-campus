package Gui;

import IODevice;

import javax.microedition.lcdui.*;



/**
 * The graphical user interface. Can display all menus and cutscenes.
 * 
 * @author Paul/Bertille
 * 
 */
public class GuiManager {
	private static GuiManager instance = null; // the instance. GuiManager is a
												// singleton.

	public static final int MAIN_MENU = 0,
	                        INVENTORY = 1,
                            SKILLS = 2,
                            SOUND_MENU = 3,
                            INITIATION_MENU = 4,
                            START_MENU = 5,
	                        PAUSE_MENU = 6;

	private int activeMenuType = MAIN_MENU;

	Menu activeMenu;

	private GuiManager() {
		setActiveMenu(getActiveMenuType());
	}

	public Menu getActiveMenu() {
		return activeMenu;
	}
	
	public int getActiveMenuType() {
		return activeMenuType;
	}
    
	private static  String testgetActiveMenuTyp(int counter){
			GuiManager.getInstance().setActiveMenu(counter);
			if((counter>=7)||(counter<0)){
				if(GuiManager.getInstance().getActiveMenuType()== 0){
					return(" Menu number not assign, active Menu : Main Menu ");
				}
				else{
				return(" Menu number not assign, but ERROR  ");
				}
			}
			if (GuiManager.getInstance().getActiveMenuType() != counter){
				return ("ERROR :Setmenu different to getMenu");
			}	
				else return("OK :setmenu equal to getMenu");	
	
	}
	
	/*private static  String testgetActiveMenu(int counter){
		
		GuiManager.getInstance().setActiveMenu(counter);
		if((counter>=7)||(counter<0)){
			if((GuiManager.getInstance().getActiveMenuType()== 0)&
					( GuiManager.getInstance().getActiveMenu().getClass() == MainMenu.class)){
				return(" Menu number not assign, active Menu : Main Menu ");
			}
			else{
			return(" Menu number not assign, but ERROR  ");
			}
		}
		else{
			
		
			if(counter==0){
				if (GuiManager.getInstance().getActiveMenu().getClass()!=MainMenu.class){
					return ("ERROR :Setmenu different to getMenu");
				}	
				else{
					return("OK :setmenu equal to getMenu");
				}
			}	
			
			if(counter==1){
				if (GuiManager.getInstance().getActiveMenu().getClass()!=InventoryGUI.class){
					return ("ERROR :Setmenu different to getMenu");
				}	
				else{
					return("OK :setmenu equal to getMenu");
				}
			}	
			
			if(counter==2){
				if (GuiManager.getInstance().getActiveMenu().getClass()!=HeadUpDisplay.class){
					return ("ERROR :Setmenu different to getMenu");
				}	
				else{
					return("OK :setmenu equal to getMenu");
				}
			}	
				
			if(counter==3){
				if (GuiManager.getInstance().getActiveMenu().getClass()!=SoundMenu.class){
					return ("ERROR :Setmenu different to getMenu");
				}	
				else{
					return("OK :setmenu equal to getMenu");
				}
			}	
			if(counter==0){
				
					return("OK :setmenu equal to getMenu");
				
			}
			if(counter==0){
				if (GuiManager.getInstance().getActiveMenu().getClass()!=StartMenu.class){
					return ("ERROR :Setmenu different to getMenu");
				}	
				else{
					return("OK :setmenu equal to getMenu");
				}
			}	
	
			if(counter==0){
				if (GuiManager.getInstance().getActiveMenu().getClass()!=PauseMenu.class){
					return ("ERROR :Setmenu different to getMenu");
				}	
				else{
					return("OK :setmenu equal to getMenu");
				}
			}
				
		}
	}	
*/
	public static void test() {
		for(int i=0;i<=10;i++){
			System.out.println("testgetActiveMenu(): " + testgetActiveMenuTyp(i));
		}
	}

	public static GuiManager getInstance() {

		if (instance == null) {
			instance = new GuiManager();
		}

		return instance;
	}

	/**
	 * Called by GameManager when a key is pressed.
	 * 
	 * @param key
	 *            The key which was pressed.
	 */
	public void keyDown(int key) {
		activeMenu.keyDown(key);
	}

	/**
	 * Called by GameManager when a key is released.
	 * 
	 * @param key
	 *            The key which was released.
	 */
	public void keyUp(int key) {
		activeMenu.keyUp(key);
	}

	/**
	 * Draws the current menu to the Graphics context g
	 * 
	 * @param g
	 *            The destination for rendering
	 */
	public void render(Graphics g) {
		activeMenu.renderMenu(g);
	}

	/**
	 * sets the current menu to main menu, inventory, ....
	 * 
	 * @param menu
	 */
	public void setActiveMenu(int menuType) {
		// TODO check menu for wrong data (i.e. -2)
		activeMenuType = menuType;

		switch (activeMenuType) {
		case MAIN_MENU:
			IODevice.getInstance().setLeftCommand(IODevice.COMMANDTYPE_NONE);
			IODevice.getInstance().setRightCommand(IODevice.COMMANDTYPE_OK);
			this.activeMenu = new MainMenu();
			break;
		case INVENTORY:
			IODevice.getInstance().setLeftCommand(IODevice.COMMANDTYPE_BACK);
			IODevice.getInstance().setRightCommand(IODevice.COMMANDTYPE_OK);
			this.activeMenu = new InventoryGUI();
			break;
		case SKILLS:
			IODevice.getInstance().setLeftCommand(IODevice.COMMANDTYPE_MENU);
			IODevice.getInstance().setRightCommand(IODevice.COMMANDTYPE_INVENTORY);
			this.activeMenu = new HeadUpDisplay();
			break;
		
		case START_MENU:
			IODevice.getInstance().setLeftCommand(IODevice.COMMANDTYPE_BACK);
			IODevice.getInstance().setRightCommand(IODevice.COMMANDTYPE_OK);
			this.activeMenu = new StartMenu();
			break;
		case INITIATION_MENU:
		
		case SOUND_MENU:
			IODevice.getInstance().setLeftCommand(IODevice.COMMANDTYPE_BACK);
			IODevice.getInstance().setRightCommand(IODevice.COMMANDTYPE_OK);
			this.activeMenu = new SoundMenu();
			break;
		
		case PAUSE_MENU:
			IODevice.getInstance().setLeftCommand(IODevice.COMMANDTYPE_BACK);
			IODevice.getInstance().setRightCommand(IODevice.COMMANDTYPE_OK);
			this.activeMenu = new PauseMenu();
			break;
		default:
			this.activeMenu = new MainMenu();
			break;
		}
	}
}