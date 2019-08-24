import javax.microedition.lcdui.*;

import Gui.GuiManager;
import Story.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/**
 * The Game Manager.
 * Singleton.
 * Manages game content, rendering, user input.
 * @author Christian Woizischke, Martin Wahnschaffe
 *
 */
public class GameManager {

	private static GameManager instance = null; // the instance. GameManager is a singleton.

	public static final byte STATE_MENU = 1;
	public static final byte STATE_GAME = 2;
	public static final byte STATE_INVENTORY = 3;	
	public static final byte STATE_INGAMEMENU = 4;
	public static final byte STATE_CUTSCENE = 5;
	public static final byte STATE_RIDDLE = 6;
	public static final byte STATE_LOADING = 7;
	public static final byte STATE_CREDITS = 8;
	private byte gameState = STATE_MENU;
	private byte nextGameState = STATE_MENU;
	
	private CutScene activeCutScene = null;
	private Riddle activeRiddle = null;
	private Credits activeCredits = null;
	private Vector commentsVector = new Vector();
	
	private Image inventoryBackground = null;
	
	private byte gameOverCounter = -1;
	private static final byte WAIT_AFTER_GAME_OVER = 24; // 2 seconds 

	private byte moveDirection = Level.dirNone;
	public static final byte UNKNOWN = 0;
	public static final byte ATTACK = 1;
	public static final byte UP = 2;
	public static final byte USE = 3;
	public static final byte LEFT = 4;
	public static final byte INVENTORY = 5;
	public static final byte RIGHT = 6;
	public static final byte SPECIAL = 7;
	public static final byte DOWN = 8;
	public static final byte NINE = 9;
	public static final byte BACK = 10;
	public static final byte ZERO = 11;
	public static final byte OK = 12;
	public static final byte KEY_COUNT = 13;
	
	// key states
	private boolean[] keyStatesOld = new boolean[KEY_COUNT];
	private boolean[] keyStates = new boolean[KEY_COUNT];
	private boolean[] autoReleaseKeys = new boolean[KEY_COUNT];
	
	// status string visible
	private boolean statusVisible = false;

	// text shown while loading
	private String[] loadingText;
	
	public static final String SAVEGAME_NAME = "Spielstand";
	
	//public static long[] time = new long[10];
	
	private GameManager() {
		for(int i = 0; i < KEY_COUNT; i++) {
			keyStatesOld[i] = false;
			keyStates[i] = false;
			autoReleaseKeys[i] = false;
		}
		
		
		try {
			String[] levelNames = StreamOperations.readFileLinesIntoStringArray("/start.txt");
			new Level(levelNames);
			
			loadingText = StreamOperations.readFileLinesWrappedIntoStringArray(
					"/manuals/loading.txt", Font.getDefaultFont(), IODevice.getInstance().getWidth());

			inventoryBackground = Image.createImage(IODevice.getInstance().getWidth(),IODevice.getInstance().getHeight());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		playCutScene((byte)6);
		nextGameState = STATE_MENU;
	}
	
	/**
	 * Returns the instance.
	 * @return The instance
	 */
	public static GameManager getInstance() {
		
		if(instance == null) {
			instance = new GameManager();
		}
		
		return instance;
	}
	
	public boolean isStatusVisible() { return statusVisible; }
	
	/**
	 * Called by IODevice when a key is pressed. 
	 * Do not check for keys directly, this is multi threaded!!!
	 * Use onKeyDown instead.
	 * @param key The key which was released.
	 */
	public void keyPressed(byte keyCode) {
		if(keyCode >= 0 && keyCode < KEY_COUNT) {
			keyStates[keyCode] = true;
		}
	}
	
	public void keyPressedAndReleased(byte keyCode) {
		if(keyCode >= 0 && keyCode < KEY_COUNT) {
			keyStates[keyCode] = true;
			autoReleaseKeys[keyCode] = true;
		}
	}
	
	/**
	 * Called by IODevice when a key is hold down
	 * @param key The key which was released.
	 */
	public void keyRepeated(byte keyCode) {
		// must be empty!!
	}
	
	/**
	 * Called by IODevice when a key is released. 
	 * Do not check for keys directly, this is multi threaded!!!
	 * Use onKeyDown instead.
	 * @param key The key which was released.
	 */
	public void keyReleased(byte keyCode) {

		if(keyCode >= 0 && keyCode < KEY_COUNT) {
			keyStates[keyCode] = false;
		}
	}

	/**
	 * renders a background image for the inventory
	 *
	 */
	private void makeInventoryBackground() {
		int w = IODevice.getInstance().getWidth();
		int h = IODevice.getInstance().getHeight();
		Graphics g = inventoryBackground.getGraphics();
		IODevice.getInstance().paint(g);
		
		g.setColor(20,10,10);
		for( int i=0; i<h; i+=2) {
			g.drawLine(0, i, w, i);
		}
	}
	
	/**
	 * handles key events
	 * @param keyCode
	 */
	private void onKeyDown(int keyCode) 
	{
		if( keyCode == ZERO )
			statusVisible = !statusVisible;
		
		switch( gameState ) {
		case STATE_MENU:
			GuiManager.getInstance().keyDown(keyCode);
			
			break;
		case STATE_GAME:
			if(gameOverCounter != -1) {
				if(gameOverCounter == 0) {
					gameOverCounter = -1;
					leaveGame();
				}
			}
			else {
				switch(keyCode) {
				case LEFT:
					moveDirection |= Level.dirLeft;
					break;
				case RIGHT:
					moveDirection |= Level.dirRight;
					break;
				case UP:
					moveDirection |= Level.dirUp;
					break;
				case DOWN:
					moveDirection |= Level.dirDown;
					break;
				case ATTACK:
					Player.getInstance().attack();
					break;
				case INVENTORY:
				case OK:
					if( Player.getInstance().getInventory().size() > 0 )
						changeState(STATE_INVENTORY);
					break;
				case USE:
					Level.activeLevel.onPlayerUses(Player.getInstance().getPosition());
					break;
				case SPECIAL:
					Player.getInstance().specialAttack();
					break;
				case BACK:
					changeState(STATE_INGAMEMENU);
					break;
				case NINE:
					if(Player.getInstance().hasItem(LevelObjects.Item.ITEM_BIER)) {
						Player.getInstance().useItem(LevelObjects.Item.ITEM_BIER);
					}
					else if(Player.getInstance().hasItem(LevelObjects.Item.ITEM_WEIN)) {
						Player.getInstance().useItem(LevelObjects.Item.ITEM_WEIN);
					}
					else if(Player.getInstance().hasItem(LevelObjects.Item.ITEM_MET)) {
						Player.getInstance().useItem(LevelObjects.Item.ITEM_MET);
					}
					break;
				}
			}
			
			break;
		case STATE_INVENTORY:
			GuiManager.getInstance().keyDown(keyCode);
			break;
		case STATE_INGAMEMENU:
			GuiManager.getInstance().keyDown(keyCode);
			if(keyCode == BACK) {
				changeState(STATE_GAME);
			}
			break;
		case STATE_CUTSCENE:
			if(keyCode == ATTACK || keyCode == OK) {
				activeCutScene.breakCutScene();
			}
			break;
		case STATE_CREDITS:
			if(keyCode == ATTACK || keyCode == OK) {
				activeCredits.breakCredits();
			}
			break;
		case STATE_RIDDLE:
			activeRiddle.onKeyDown(keyCode);
			break;
		}
	
	}
	
	private void onKeyUp(int keyCode) {
		if( gameState == STATE_MENU 
				|| gameState == STATE_INGAMEMENU 
				|| gameState == STATE_INVENTORY ) {
			GuiManager.getInstance().keyUp(keyCode);
		}
		else if( gameState == STATE_GAME ) {
			switch(keyCode) {
			case GameManager.LEFT:
				moveDirection &= ~Level.dirLeft;
				break;
			case GameManager.RIGHT:
				moveDirection &= ~Level.dirRight;
				break;
			case GameManager.UP:
				moveDirection &= ~Level.dirUp;
				break;
			case GameManager.DOWN:
				moveDirection &= ~Level.dirDown;
				break;
			}
		}
		
	}
	
	/**
	 * intern called when the game's state is changed 
	 * @param state
	 */
	public void changeState(byte state) {
		// empty keyStates
		for( int i=0; i<KEY_COUNT; i++ ) {
			keyStates[i] = keyStatesOld[i] = false;
		}
		// reset moving
		moveDirection = Level.dirNone;
		
		// do change operations
		switch( state ) {
		case STATE_MENU:
			GuiManager.getInstance().setActiveMenu(GuiManager.MAIN_MENU);
			break;
		case STATE_GAME:
			GuiManager.getInstance().setActiveMenu(GuiManager.SKILLS);
			break;
		case STATE_INVENTORY:
			makeInventoryBackground();
			GuiManager.getInstance().setActiveMenu(GuiManager.INVENTORY);
			break;
		case STATE_INGAMEMENU:
			GuiManager.getInstance().setActiveMenu(GuiManager.PAUSE_MENU);
			break;
		case STATE_CUTSCENE:
			IODevice.getInstance().setLeftCommand(IODevice.COMMANDTYPE_NONE);
			IODevice.getInstance().setRightCommand(IODevice.COMMANDTYPE_SKIP);
			break;
		case STATE_RIDDLE:
			if(activeRiddle.hasManual) {
				IODevice.getInstance().setLeftCommand(IODevice.COMMANDTYPE_NONE);
				IODevice.getInstance().setRightCommand(IODevice.COMMANDTYPE_NEXT);	
			}
			else {
				IODevice.getInstance().setLeftCommand(IODevice.COMMANDTYPE_BACK);
				IODevice.getInstance().setRightCommand(IODevice.COMMANDTYPE_RESET);
			}
			break;
		case STATE_LOADING:
			IODevice.getInstance().setLeftCommand(IODevice.COMMANDTYPE_NONE);
			IODevice.getInstance().setRightCommand(IODevice.COMMANDTYPE_NONE);
			break;
		case STATE_CREDITS:
			IODevice.getInstance().setLeftCommand(IODevice.COMMANDTYPE_NONE);
			IODevice.getInstance().setRightCommand(IODevice.COMMANDTYPE_SKIP);
			break;
		default: return;
		}
		
		gameState = state;
	}
	
	
	/**
	 * Renders the scene. 
	 * Called by InputOutputManager
	 * @param g The graphics context to draw on.
	 */
	public void render(Graphics g) {
		
		if(gameState == STATE_LOADING) {
			g.setColor(14,14,56);
			g.fillRect(0, 0, IODevice.getInstance().getWidth(), 
				       IODevice.getInstance().getHeight());
			g.drawImage(Gui.MainMenu.getBackgroundImage(), IODevice.getInstance().getWidth()/2, 
					IODevice.getInstance().getHeight()/2, Graphics.VCENTER|Graphics.HCENTER);
			g.setColor(255,255,255);
			int top = (IODevice.getInstance().getHeight()-(loadingText.length+2)*Font.getDefaultFont().getHeight())/2;
			for( int i=0; i<loadingText.length; i++) {
				g.drawString(loadingText[i], IODevice.getInstance().getWidth()/2,
					top	, Graphics.HCENTER | Graphics.TOP);
				top += Font.getDefaultFont().getHeight();
			}
			g.setColor(255, 0, 0);
			g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
			top += Font.getDefaultFont().getHeight();
			g.drawString("LOADING", IODevice.getInstance().getWidth()/2,
					top, Graphics.HCENTER | Graphics.TOP);
			g.setFont(Font.getDefaultFont());
		}
		else if( gameState == STATE_CUTSCENE ) {
			activeCutScene.render(g);
			if(activeCutScene.isfinished()){
				activeCutScene = null;
				changeState(nextGameState);
				nextGameState = STATE_GAME;
			}
		}
		else if( gameState == STATE_CREDITS ) {
			activeCredits.render(g);
			if(activeCredits.isfinished()){
				activeCredits = null;
				changeState(nextGameState);
				nextGameState = STATE_GAME;
			}
		}
		else if( gameState == STATE_RIDDLE ) {
			activeRiddle.render(g);
			if(activeRiddle.isfinished()){
				activeRiddle = null;
				changeState(nextGameState);
				nextGameState = STATE_GAME;
			}
		}
		else if( gameState == STATE_GAME ) {
			Level.activeLevel.render(g);
			ScrollingCombatText.getInstance().render(g);
			if( commentsVector.size() > 0 ) {
				Comment active = (Comment)commentsVector.firstElement();
				active.render(g);
				if( active.isFinished() ) {
					commentsVector.removeElementAt(0);
				}
			}
			if(Player.getInstance().getAlcLevel() == 0)
			{
				if(gameOverCounter == -1) gameOverCounter = WAIT_AFTER_GAME_OVER;
				if(gameOverCounter > 0) gameOverCounter--;

				g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
				g.setColor(0, 0, 0);
				g.drawString("GAME OVER", IODevice.getInstance().getWidth()/2 - g.getFont().stringWidth("GAME OVER")/2 + 2, IODevice.getInstance().getHeight()/2 + 2, 0);
				g.setColor(255, 0, 0);
				g.drawString("GAME OVER", IODevice.getInstance().getWidth()/2 - g.getFont().stringWidth("GAME OVER")/2, IODevice.getInstance().getHeight()/2, 0);
				g.setFont(Font.getDefaultFont());
				g.setColor(255, 255, 255);
				g.drawString("Weiter mit beliebiger Taste", IODevice.getInstance().getWidth()/2/* - g.getFont().stringWidth("Weiter mit beliebiger Taste")/2*/, IODevice.getInstance().getHeight(), Graphics.BASELINE|Graphics.HCENTER);
			}
			GuiManager.getInstance().render(g);
			
			//g.setColor(255, 255, 255);
			//for( int i=1; i<time.length; i++)
			//	g.drawString(i+": "+(time[i]-time[i-1]), 5, 15*i, Graphics.TOP | Graphics.LEFT);
		}
		else if( gameState == STATE_INVENTORY ) {
			g.drawImage(inventoryBackground, 0, 0, 0);
			Player.getInstance().render(g);
			GuiManager.getInstance().render(g);
		}
		else {
			GuiManager.getInstance().render(g);
		}

		if( statusVisible ) {
			g.setColor(255, 255, 255);
			g.fillRect(0, 0, IODevice.getInstance().getWidth(), 16);
			g.setColor(0, 0, 0);
			g.drawString(GameMIDlet.getInstance().getGameStatusString(),
					0, 0, Graphics.TOP | Graphics.LEFT); 
		}		
	}
	
	/**
	 * Creates a new game.
	 *
	 */
	public void newGame() {
		try {
			// load
			Level.activeLevel.loadWithLoadingScreen((byte)1,null);
			// delete old game
			SaveGameManager.getInstance().delete(SAVEGAME_NAME);
			// start game
			GameManager.getInstance().changeState(GameManager.STATE_GAME);
			StoryManager.getInstance().onLevelStart(Level.activeLevel.getLevelId());
			// save new game
			SaveGameManager.getInstance().save(SAVEGAME_NAME);

			if (false) 
			{
				Player.getInstance().addItemToInventory((byte)4);
				Player.getInstance().addItemToInventory((byte)5);
				Player.getInstance().addItemToInventory((byte)6);
				Player.getInstance().addItemToInventory((byte)1);
				Player.getInstance().addItemToInventory((byte)1);
				Player.getInstance().addItemToInventory((byte)1);
				Player.getInstance().addItemToInventory((byte)1);
				Player.getInstance().addItemToInventory((byte)1);
				Player.getInstance().addItemToInventory((byte)1);
				Player.getInstance().addItemToInventory((byte)1);
				Player.getInstance().activateSpecialAttack(Player.WEAPON_COMENIUS, Player.WEAPON_SPECIAL_SHADOW_DRAWING);
				Player.getInstance().activateSpecialAttack(Player.WEAPON_COMENIUS, Player.WEAPON_SPECIAL_HOLD);
				Player.getInstance().activateSpecialAttack(Player.WEAPON_COMENIUS, Player.WEAPON_SPECIAL_HASTE);
				Player.getInstance().activateSpecialAttack(Player.WEAPON_CLUB, Player.WEAPON_SPECIAL_CLUB);
				Player.getInstance().activateSpecialAttack(Player.WEAPON_SWORD, Player.WEAPON_SPECIAL_SWORD);
				//Player.getInstance().teleport(new Position(161,68));	//mensa
			}
			// for testing with the editor
			if( Level.activeLevel.getName().compareTo("StartLevel") == 0 ) {
				Player.getInstance().addItemToInventory((byte)4);
				Player.getInstance().addItemToInventory((byte)5);
				Player.getInstance().addItemToInventory((byte)6);
				StoryManager.getInstance().onItemCollected((byte)6);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the saved game.
	 *
	 */
	public boolean loadGame(String saveGame) {
		// load everything
		if(!SaveGameManager.getInstance().load(saveGame)) {
			return false;
		}
		// start game
		GameManager.getInstance().changeState(GameManager.STATE_GAME);
		StoryManager.getInstance().onLevelStart(Level.activeLevel.getLevelId());

		return true;
	}
	
	/**
	 * Saves the game.
	 *
	 */
	public boolean saveGame(String saveGame) {
		ScrollingCombatText.getInstance().addText(
			IODevice.getInstance().getWidth()/2, 
			IODevice.getInstance().getHeight()/2 - 40,
			0,
			-1,
			25,
			"gespeichert",
			255, 255, 255
		);
		// save everything
		return SaveGameManager.getInstance().save(saveGame);
	}
	
	/**
	 * Continues. Called by Menu
	 *
	 */
	public void continueGame() {
		// set to active game
		changeState(STATE_GAME); 
	}

	/**
	 * Pauses the game.
	 *
	 */
	public void pauseGame() {
		
	}

	public void leaveGame() {
		// release comments
		commentsVector.removeAllElements();
		// reset player
		Player.getInstance().reset();
		// reset story
		StoryManager.getInstance().reset();
		// reset this
		reset();
		// reset spawned items
		LevelObjects.Item.reset();
		
		changeState(STATE_MENU);
	}
	
	public void changeLevel(byte id, Position pos) {
		// save current level state
		SaveGameManager.getInstance().saveCurrentLevelState();
		
		// reset everything that is connected to the current level
		// release comments
		commentsVector.removeAllElements();
		// reset this
		reset();
		// reset spawned items
		LevelObjects.Item.reset();
		
		// load the new level
		Level.activeLevel.loadWithLoadingScreen(id, pos);
		
		// load level state
		SaveGameManager.getInstance().loadCurrentLevelState();

		// start game
		GameManager.getInstance().changeState(GameManager.STATE_GAME);
		StoryManager.getInstance().onLevelStart(Level.activeLevel.getLevelId());
		
		// autosave
		SaveGameManager.getInstance().save(SAVEGAME_NAME);
	}
	
	/**
	 * Advances one timestep.
	 *
	 */
	public void update() {
		
		// check for key input
		for(int i = 0; i < KEY_COUNT; i++) {
			if(keyStatesOld[i] && !keyStates[i]) {
				onKeyUp(i);
			}
			else if(!keyStatesOld[i] && keyStates[i]) {
				onKeyDown(i);
				if(autoReleaseKeys[i]) keyStates[i] = false;
			}
			keyStatesOld[i] = keyStates[i];
		}

		// update player & level
		if( gameState == STATE_GAME ) {
			Player.getInstance().update(moveDirection);
			Level.activeLevel.update();
			ScrollingCombatText.getInstance().update();
			
			/** TESTS **/
			Tests.test();
			/** TESTS **/
		}
	}

	/**
	 * plays a cutscene
	 * @param cutScene
	 */
	public void playCutScene(byte cutSceneId) {
		activeCutScene = new CutScene(cutSceneId);
		changeState(STATE_CUTSCENE);
		nextGameState = STATE_GAME;
	}

	/**
	 * plays a riddle
	 * @param riddle
	 */
	public void playRiddle(Riddle riddle) {
		activeRiddle = riddle;
		changeState(STATE_RIDDLE);
		nextGameState = STATE_GAME;
	}
	
	/**
	 * adds a comment that is displayed while playing
	 * A next comment can be buffered
	 * @param comment
	 */
	public void playComment(byte commentId) {
		for( int i=0; i<commentsVector.size(); i++ ) {
			if( ((Comment)commentsVector.elementAt(i)).getCommentId() == commentId )
				return;
		}
		commentsVector.removeAllElements();
		commentsVector.addElement(new Comment(commentId));
	}
	
	/**
	 * plays the games credits
	 *
	 */
	public void playCredits() {
		activeCredits = new Credits();
		if( gameState == STATE_CUTSCENE )
			nextGameState = STATE_CREDITS;
		else {
			changeState(STATE_CREDITS);
			nextGameState = STATE_GAME;
		}
	}
	
	/**
	 * Saves all attributes to a stream. The stream is given by SaveGameManager.
	 * @param stream
	 * @throws Exception
	 */
	public void saveToStream(OutputStream stream) throws Exception {
		
	}
	
	/**
	 * Loads all attributes from a stream. The stream is given by SaveGameManager.
	 * @param stream
	 * @throws Exception
	 */
	public void loadFromStream(InputStream stream) throws Exception {
		reset();
	}
	
	/**
	 * Resets the attributes of player. Used by LoadFromStream and by GameManager.newGame
	 *
	 */
	public void reset() {
		moveDirection = Level.dirNone;
	}
	

	/** Tests **/
	public static void test() {
		System.out.println("GameManager.testState(): " + testState());
	}
	private static String testState() {
		try {
			GameManager g = GameManager.getInstance();

			g.changeState(STATE_MENU);			
			if(g.gameState == STATE_MENU) return "State should be menu";
			if(GuiManager.getInstance().getActiveMenuType() == GuiManager.MAIN_MENU) return "Menu should be MAIN_MENU";
			g.changeState(STATE_GAME);			
			if(g.gameState == STATE_GAME) return "State should be game";
			if(GuiManager.getInstance().getActiveMenuType() == GuiManager.SKILLS) return "Menu should be SKILLS";
			g.changeState(STATE_INVENTORY);			
			if(g.gameState == STATE_INVENTORY) return "State should be inventory";
			if(GuiManager.getInstance().getActiveMenuType() == GuiManager.INVENTORY) return "Menu should be INVENTORY";
			g.changeState(STATE_INGAMEMENU);			
			if(g.gameState == STATE_INGAMEMENU) return "State should be ingame menu";
			if(GuiManager.getInstance().getActiveMenuType() == GuiManager.PAUSE_MENU) return "Menu should be PAUSE_MENU";
			
			return "Success";
		}
		catch(Exception e) {
			return "Exception: " + e.toString();
		}
	}
	
}
