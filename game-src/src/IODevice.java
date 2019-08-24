import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;


/**
 * The manager for key input and screen output.
 * Singleton.
 * @author Christian Woizischke
 *
 */
public class IODevice extends GameCanvas implements CommandListener {
	
	private static IODevice instance = null;

	public static final byte COMMANDTYPE_NONE = -1;
	public static final byte COMMANDTYPE_BACK = 0;
	public static final byte COMMANDTYPE_OK = 1;
	public static final byte COMMANDTYPE_MENU = 2;
	public static final byte COMMANDTYPE_SKIP = 3;
	public static final byte COMMANDTYPE_RESET = 4;
	public static final byte COMMANDTYPE_INVENTORY = 5;
	public static final byte COMMANDTYPE_NEXT = 6;

    private Command cmdLeft = null;
    private Command cmdRight = null;

	
	private IODevice() {
		// initialize the GameCanvas. do not suppres key events.
		super(false);

		setCommandListener(this);

		setLeftCommand(COMMANDTYPE_NONE);
		setRightCommand(COMMANDTYPE_OK);
	}

	/**
	 * Returns the instance.
	 * @return The instance
	 */
	public static IODevice getInstance() {
		if(instance == null) {
			instance = new IODevice();
		}
		return instance;
	}
	
	public void commandAction(Command command, Displayable displayable) {
		switch (command.getCommandType()) {
		case Command.OK:
			GameManager.getInstance().keyPressedAndReleased(keyCodeToKey(KEY_POUND));
			break;
		case Command.BACK:
			GameManager.getInstance().keyPressedAndReleased(keyCodeToKey(KEY_STAR));
			break;
		}
	}
	
	public void setLeftCommand(byte type) {
		switch(type) {
		case COMMANDTYPE_NONE:
			if(cmdLeft != null) removeCommand(cmdLeft);
			cmdLeft = null;
			break;
		case COMMANDTYPE_BACK:
			if(cmdLeft != null) removeCommand(cmdLeft);
			cmdLeft = new Command("Zurück", Command.BACK, 1);
			addCommand(cmdLeft);
			break;
		case COMMANDTYPE_OK:
			if(cmdLeft != null) removeCommand(cmdLeft);
			cmdLeft = new Command("Wählen", Command.BACK, 1);
			addCommand(cmdLeft);
			break;
		case COMMANDTYPE_MENU:
			if(cmdLeft != null) removeCommand(cmdLeft);
			cmdLeft = new Command("Menü", Command.BACK, 1);
			addCommand(cmdLeft);
			break;
		case COMMANDTYPE_SKIP:
			if(cmdLeft != null) removeCommand(cmdLeft);
			cmdLeft = new Command("Überspr.", Command.BACK, 1);
			addCommand(cmdLeft);
			break;
		case COMMANDTYPE_RESET:
			if(cmdLeft != null) removeCommand(cmdLeft);
			cmdLeft = new Command("Nochmal", Command.BACK, 1);
			addCommand(cmdLeft);
			break;
		case COMMANDTYPE_INVENTORY:
			if(cmdLeft != null) removeCommand(cmdLeft);
			cmdLeft = new Command("Inventar", Command.BACK, 1);
			addCommand(cmdLeft);
			break;
		case COMMANDTYPE_NEXT:
			if(cmdLeft != null) removeCommand(cmdLeft);
			cmdLeft = new Command("Weiter", Command.BACK, 1);
			addCommand(cmdLeft);
			break;
		default: break;
		}
	}
	
	public void setRightCommand(byte type) {
		switch(type) {
		case COMMANDTYPE_NONE:
			if(cmdRight != null) removeCommand(cmdRight);
			cmdRight = null;
			break;
		case COMMANDTYPE_BACK:
			if(cmdRight != null) removeCommand(cmdRight);
			cmdRight = new Command("Zurück", Command.OK, 1);
			addCommand(cmdRight);
			break;
		case COMMANDTYPE_OK:
			if(cmdRight != null) removeCommand(cmdRight);
			cmdRight = new Command("Wählen", Command.OK, 1);
			addCommand(cmdRight);
			break;
		case COMMANDTYPE_MENU:
			if(cmdRight != null) removeCommand(cmdRight);
			cmdRight = new Command("Menü", Command.OK, 1);
			addCommand(cmdRight);
			break;
		case COMMANDTYPE_SKIP:
			if(cmdRight != null) removeCommand(cmdRight);
			cmdRight = new Command("Überspr.", Command.OK, 1);
			addCommand(cmdRight);
			break;
		case COMMANDTYPE_RESET:
			if(cmdRight != null) removeCommand(cmdRight);
			cmdRight = new Command("Nochmal", Command.OK, 1);
			addCommand(cmdRight);
			break;
		case COMMANDTYPE_INVENTORY:
			if(cmdRight != null) removeCommand(cmdRight);
			cmdRight = new Command("Inventar", Command.OK, 1);
			addCommand(cmdRight);
			break;
		case COMMANDTYPE_NEXT:
			if(cmdRight != null) removeCommand(cmdRight);
			cmdRight = new Command("Weiter", Command.OK, 1);
			addCommand(cmdRight);
			break;
		default: break;
		}
	}
	
	public void paint(Graphics g) {
		// clear the screen
		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		// render the game
		GameManager.getInstance().render(g);
	}

	protected void keyPressed(int keyCode) {
		GameManager.getInstance().keyPressed(keyCodeToKey(keyCode));
	}

	protected void keyReleased(int keyCode) {
		GameManager.getInstance().keyReleased(keyCodeToKey(keyCode));
	}

	protected void keyRepeated(int keyCode) {
		GameManager.getInstance().keyRepeated(keyCodeToKey(keyCode));
	}
	
	private byte keyCodeToKey(int keyCode) {
		switch(keyCode) {
		case KEY_NUM1: return GameManager.ATTACK;
		case KEY_NUM2: return GameManager.UP;
		case KEY_NUM3: return GameManager.USE;
		case KEY_NUM4: return GameManager.LEFT;
		case KEY_NUM5: return GameManager.INVENTORY;
		case KEY_NUM6: return GameManager.RIGHT;
		case KEY_NUM7: return GameManager.SPECIAL;
		case KEY_NUM8: return GameManager.DOWN;
		case KEY_NUM9: return GameManager.NINE;
		case KEY_STAR: return GameManager.BACK;
		case KEY_NUM0: return GameManager.ZERO;
		case KEY_POUND: return GameManager.OK;
		}
		keyCode = getGameAction(keyCode);
		switch(keyCode) {
		case UP: return GameManager.UP;
		case DOWN: return GameManager.DOWN;
		case LEFT: return GameManager.LEFT;
		case RIGHT: return GameManager.RIGHT;
		case FIRE: return GameManager.ATTACK;
		}
		return GameManager.UNKNOWN;
	}
	
	
	/** Tests **/
	public static void test() {
		System.out.println("IODevice.testKeyCodeToKey(): " + testKeyCodeToKey());
		System.out.println("IODevice.testSetRightCommand(): " + testSetRightCommand());
		System.out.println("IODevice.testSetLeftCommand(): " + testSetLeftCommand());
	}
	private static String testKeyCodeToKeyHelper(int c, int d) {
		int k = IODevice.getInstance().keyCodeToKey(c);
		if(k != d) return "Key should be " + d + " but it is " + k;
		return null;
	}
	private static String testKeyCodeToKey() {
		try {
			String s;

			s = testKeyCodeToKeyHelper(KEY_NUM1, GameManager.ATTACK); if(s != null) return s;
			s = testKeyCodeToKeyHelper(KEY_NUM2, GameManager.UP); if(s != null) return s;
			s = testKeyCodeToKeyHelper(KEY_NUM3, GameManager.USE); if(s != null) return s;
			s = testKeyCodeToKeyHelper(KEY_NUM4, GameManager.LEFT); if(s != null) return s;
			s = testKeyCodeToKeyHelper(KEY_NUM5, GameManager.INVENTORY); if(s != null) return s;
			s = testKeyCodeToKeyHelper(KEY_NUM6, GameManager.RIGHT); if(s != null) return s;
			s = testKeyCodeToKeyHelper(KEY_NUM7, GameManager.SPECIAL); if(s != null) return s;
			s = testKeyCodeToKeyHelper(KEY_NUM8, GameManager.DOWN); if(s != null) return s;
			s = testKeyCodeToKeyHelper(KEY_NUM9, GameManager.NINE); if(s != null) return s;
			s = testKeyCodeToKeyHelper(KEY_STAR, GameManager.BACK); if(s != null) return s;
			s = testKeyCodeToKeyHelper(KEY_NUM0, GameManager.ZERO); if(s != null) return s;
			s = testKeyCodeToKeyHelper(KEY_POUND, GameManager.OK); if(s != null) return s;
			
			return "Success";
		}
		catch(Exception e) {
			return "Exception: " + e.toString();
		}
	}
	private static String testSetRightCommandHelper(byte a, String s) {
		IODevice.getInstance().setRightCommand(a);
		try {
			if(0 != IODevice.getInstance().cmdRight.getLabel().compareTo(s)) return "Right command button should be " + s + " but it is " + IODevice.getInstance().cmdRight.getLabel();
		}
		catch(Exception e) {
			return "Right command button should not be null but it is.";
		}
		return null;
	}
	private static String testSetRightCommand() {
		try {
			String s;

			IODevice.getInstance().setRightCommand(COMMANDTYPE_NONE);
			if(IODevice.getInstance().cmdRight != null) return "Right command button should be null but it is " + IODevice.getInstance().cmdRight;

			s = testSetRightCommandHelper(COMMANDTYPE_BACK, "Zurück"); if(s != null) return s;
			s = testSetRightCommandHelper(COMMANDTYPE_SKIP, "Überspr."); if(s != null) return s;
			s = testSetRightCommandHelper(COMMANDTYPE_NEXT, "Weiter"); if(s != null) return s;
			
			return "Success";
		}
		catch(Exception e) {
			return "Exception: " + e.toString();
		}
	}
	private static String testSetLeftCommandHelper(byte a, String s) {
		IODevice.getInstance().setLeftCommand(a);
		try {
			if(0 != IODevice.getInstance().cmdLeft.getLabel().compareTo(s)) return "Left command button should be " + s + " but it is " + IODevice.getInstance().cmdLeft.getLabel();
		}
		catch(Exception e) {
			return "Right command button should not be null but it is.";
		}
		return null;
	}
	private static String testSetLeftCommand() {
		try {
			String s;

			IODevice.getInstance().setLeftCommand(COMMANDTYPE_NONE);
			if(IODevice.getInstance().cmdLeft != null) return "Left command button should be null but it is " + IODevice.getInstance().cmdLeft;

			s = testSetLeftCommandHelper(COMMANDTYPE_BACK, "Zurück"); if(s != null) return s;
			s = testSetLeftCommandHelper(COMMANDTYPE_SKIP, "Überspr."); if(s != null) return s;
			s = testSetLeftCommandHelper(COMMANDTYPE_NEXT, "Weiter"); if(s != null) return s;
			
			return "Success";
		}
		catch(Exception e) {
			return "Exception: " + e.toString();
		}
	}
}