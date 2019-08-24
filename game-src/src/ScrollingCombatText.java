import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Font;
import java.util.*;

public class ScrollingCombatText {
	private Vector textObject;
	
	private static ScrollingCombatText instance = null;

	public static ScrollingCombatText getInstance() {
		if(instance == null) {
			instance = new ScrollingCombatText();
		}
		return instance;
	}

	public ScrollingCombatText() {
		this.textObject = new Vector();
	}
	
	public void addText(int _posX, int _posY, int _dirX, int _dirY, int _ftl, String _text, int _R, int _G, int _B)
	{
		ScrollingText tmp = new ScrollingText(_posX, _posY, _dirX, _dirY, _ftl, _text, ((_R << 16) + (_G<<8) + _B));
		
		try {
			this.textObject.addElement(tmp);
		} catch (Exception e){
			new Error("ScrollingCombatText.addText(..) Out of Memory");
		}
	}
	
	public void addText(int _posX, int _posY, int _dirX, int _dirY, int _ftl, String _text, int _RGB)
	{
		ScrollingText tmp = new ScrollingText(_posX, _posY, _dirX, _dirY, _ftl, _text, _RGB);
		
		try {
			this.textObject.addElement(tmp);
		} catch (Exception e){
			new Error("ScrollingCombatText.addText(..) Out of Memory");
		}
	}
	
	public void update()
	{
		for (Enumeration el=this.textObject.elements(); el.hasMoreElements(); ) {
			ScrollingText sc = (ScrollingText)el.nextElement();
			if (sc.framesToLive > 0)
			{
				// Update framesToLive & position
				sc.framesToLive--;
				sc.posX += sc.dirX;
				sc.posY += sc.dirY;
			} else {
				this.textObject.removeElement(sc);
			}
		}
	}
	
	public void render(Graphics _gra)
	{
		Font tmp = _gra.getFont();
		for (Enumeration el=this.textObject.elements(); el.hasMoreElements(); ) {
			ScrollingText sc = (ScrollingText)el.nextElement();
			
			_gra.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE));
			_gra.setColor(sc.color);
			_gra.drawChars(sc.text.toCharArray(), 0, sc.text.length(), sc.posX, sc.posY, 65);
		}
		_gra.setFont(tmp);
	}
	
	public static void test()
	{
		System.err.println(testAddText());
	}
	
	private static String testAddText()
	{
		ScrollingCombatText.getInstance().addText(50, 50, 1,1, 20, "Test", 255,255,0);
		ScrollingCombatText.getInstance().update();
		if (ScrollingCombatText.getInstance().textObject.isEmpty() == false)
			return "testAddText(): Success";
		return "testAddText(): Failed";
	}

}