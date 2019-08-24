import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream that counts the read bytes
 * @author Christian Woizischke
 *
 */
public class CountingInputStream extends InputStream {
	private InputStream stream;
	private long position;
	public long tell() {
		return position;
	}
	public void resetPosition() {
		position = 0;
	}
	
	public CountingInputStream(InputStream s) {
		stream = s;
		position = 0;
	}

	public int read() throws IOException {
		int data = stream.read();
		if(data != -1) position++;
		return data;
	}

	public long skip(long bytes) throws IOException {
		long skipped = stream.skip(bytes);
		position += skipped;
		return skipped;
	}
	
	
	/** Tests **/
	public static void test() {
		System.out.println("CountingInputStream.testConstructor(): " + testConstructor());
		System.out.println("CountingInputStream.testRead(): " + testRead());
		System.out.println("CountingInputStream.testTell(): " + testTell());
		System.out.println("CountingInputStream.testSkip(): " + testSkip());
	}
	private static String testConstructor() {
		try {
			InputStream is = Class.forName("CountingInputStream").getResourceAsStream("/testData/CountingInputStream/test.data");
			if(is == null) throw new Exception("");
			
			try {
				CountingInputStream cis = new CountingInputStream(is);
				
				// test member variable "position"
				long fileposAtCreation = cis.tell();
				if(fileposAtCreation != 0) {
					return "Wrong file position after creation: " + fileposAtCreation + ". Expected 0";
				}
				
				// test member variable "stream"
				try {
					cis.read();
				}
				catch(Exception e) {
					return "Member variable \"stream\" not set.";
				}
				
				return "Success";
			}
			catch(Exception e) {
				return "Exception: " + e.toString();
			}
		}
		catch(Exception e) {
			return "Could not open test data: " + "/testData/CountingInputStream/test.data";
		}
	}
	private static String testRead() {
		try {
			InputStream is = Class.forName("CountingInputStream").getResourceAsStream("/testData/CountingInputStream/test.data");
			if(is == null) throw new Exception("");
			
			try {
				CountingInputStream cis = new CountingInputStream(is);
				
				// test read()'s return
				int firstByte = cis.read();
				if(firstByte != 5) {
					return "read() returns wrong data: " + firstByte + ". Expected 5";
				}
				
				return "Success";
			}
			catch(Exception e) {
				return "Exception: " + e.toString();
			}
		}
		catch(Exception e) {
			return "Could not open test data: " + "/testData/CountingInputStream/test.data";
		}
	}
	private static String testTell() {
		try {
			InputStream is = Class.forName("CountingInputStream").getResourceAsStream("/testData/CountingInputStream/test.data");
			if(is == null) throw new Exception("");
			
			try {
				CountingInputStream cis = new CountingInputStream(is);
				
				// test tell after initialization
				long fileposAtCreation = cis.tell();
				if(fileposAtCreation != 0) {
					return "Wrong file position after creation: " + fileposAtCreation + ". Expected 0";
				}
				
				// modify file position
				int firstByte = cis.read();
				if(firstByte != 5) {
					return "read() returns wrong data: " + firstByte + ". Expected 5";
				}
				
				// test tell after read()
				long fileposAfter1ByteRead = cis.tell();
				if(fileposAtCreation != 0) {
					return "Wrong file position after 1 read(): " + fileposAfter1ByteRead + ". Expected 1";
				}
				
				return "Success";
			}
			catch(Exception e) {
				return "Exception: " + e.toString();
			}
		}
		catch(Exception e) {
			return "Could not open test data: " + "/testData/CountingInputStream/test.data";
		}
	}
	private static String testSkip() {
		try {
			InputStream is = Class.forName("CountingInputStream").getResourceAsStream("/testData/CountingInputStream/testSkip.data");
			if(is == null) throw new Exception("");
			
			try {
				CountingInputStream cis = new CountingInputStream(is);
				
				// check filepos before skip 
				long fileposAtCreation = cis.tell();
				if(fileposAtCreation != 0) {
					return "Wrong file position after creation: " + fileposAtCreation + ". Expected 0";
				}
				
				// skip 1 byte
				cis.skip(1);
				
				// file position should be 1 now
				long fileposAfter1ByteSkipped = cis.tell();
				if(fileposAfter1ByteSkipped != 1) {
					return "Wrong file position after 1 skip(): " + fileposAfter1ByteSkipped + ". Expected 1";
				}
				
				// skip 2 more bytes
				cis.skip(2);
				
				// file position should be 3 (=1+2)
				long fileposAfter1And2ByteSkipped = cis.tell();
				if(fileposAfter1And2ByteSkipped != 3) {
					return "Wrong file position after 1+2 skip(): " + fileposAfter1And2ByteSkipped + ". Expected 3";
				}
				
				return "Success";
			}
			catch(Exception e) {
				return "Exception: " + e.toString();
			}
		}
		catch(Exception e) {
			return "Could not open test data: " + "/testData/CountingInputStream/testSkip.data";
		}
	}

}
