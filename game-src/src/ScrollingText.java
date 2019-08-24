
public class ScrollingText {
	public int posX;
	public int posY;
	public int dirX;
	public int dirY;
	public int framesToLive;
	public String text;
	public int color;
	
	public ScrollingText(int _posX, int _posY, int _dirX, int _dirY, int _ftl, String _text, int _color) {
		this.posX = _posX;
		this.posY = _posY;
		this.dirX = _dirX;
		this.dirY = _dirY;
		this.framesToLive = _ftl;
		this.text = _text;
		this.color = _color;
	}
}
