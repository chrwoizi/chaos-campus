

public class Field {

	public static final byte COLLISION_LEFT = 1;
	public static final byte COLLISION_BOTTOM = 2;
	public static final byte COLLISION_RIGHT = 4;
	public static final byte COLLISION_TOP = 8;

	// collision on field borders. if(collision & COLLISION_LEFT) {can not go left from field}
	public byte collision;
	
	public Field() {
		collision = 0;
	}
	
	public boolean testCollision(byte where) {
		return (collision & where) != 0;
	}
	
	
	/** Tests **/
	public static void test() {
		System.out.println("Field.testConstructor(): " + testConstructor());
	}
	private static String testConstructor() {
		try {
			Field f = new Field();
			
			if(f.collision != 0) return "Collision should be 0 but it is " + f.collision;
			
			return "Success";
		}
		catch(Exception e) {
			return "Exception: " + e.toString();
		}
	}

}
