
/**
 * A short rect. Used by Level.
 * @author Christian Woizischke
 *
 */
public class Rect {
	public short left;
	public short right;
	public short top;
	public short bottom;
	
	public Rect() {
		left = right = top = bottom = 0;
	}	
	
	public Rect(short left, short right, short top, short bottom) {
		set(left, right, top, bottom);
	}	
	
	public Rect(int left, int right, int top, int bottom) {
		set(left, right, top, bottom);
	}	
	
	public void set(short left, short right, short top, short bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}
	
	public void set(int left, int right, int top, int bottom) {
		this.left = (short)left;
		this.right = (short)right;
		this.top = (short)top;
		this.bottom = (short)bottom;
	}
	
	public String toString() {
		return new String("Rect("+left+";"+top+";"+right+";"+bottom+")");
	}
	
	
	/** Tests **/
	public static void test() {
		System.out.println("Rect.testAll(): " + testAll());
	}
	private static String testAll() {
		try {
			Rect r = new Rect();
			if((r.left != 0) || (r.right != 0) || (r.top != 0) || (r.bottom != 0)) return "Rect should be (0,0,0,0) but it is " + r;
			r.set(4, 3, 2, 1);
			if((r.left != 4) || (r.right != 3) || (r.top != 2) || (r.bottom != 1)) return "Rect should be (4,3,2,1) but it is " + r;
			
			r = new Rect(1,2,3,4);
			if((r.left != 1) || (r.right != 2) || (r.top != 3) || (r.bottom != 4)) return "Rect should be (1,2,3,4) but it is " + r;
			r.set(5, 6, 7, 8);
			if((r.left != 5) || (r.right != 6) || (r.top != 7) || (r.bottom != 8)) return "Rect should be (5,6,7,8) but it is " + r;
			
			return "Success";
		}
		catch(Exception e) {
			return "Exception: " + e.toString();
		}
	}
}
