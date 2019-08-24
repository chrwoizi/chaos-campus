
import java.io.InputStream;
import java.util.Vector;
import java.io.ByteArrayOutputStream;

import javax.microedition.lcdui.Font;


public class StreamOperations {

	public static void skipBytes(CountingInputStream stream, long count) throws Exception {
		if(count == 0) return;
		if(count != stream.skip(count)) throw new Exception("End of File");
	}
	
	public static byte readByte(CountingInputStream stream) throws Exception {
		int i = stream.read();
		if(i == -1) throw new Exception("End of File");
		return (byte)i;
	}
	
	public static short readShort(CountingInputStream stream) throws Exception {
		byte b1 = readByte(stream);
		byte b2 = readByte(stream);
		// make them unsigned
		short ub1 = (short)(b1 < 0 ? 256 + b1 : b1);
		short ub2 = (short)(b2 < 0 ? 256 + b2 : b2);
		return (short)((ub2<<8) | (ub1));
	}
	
	public static void writeShort(ByteArrayOutputStream stream, short i) throws Exception {
		byte b1 = (byte)(((i & 0x000000FF)      )-256);
		byte b2 = (byte)(((i & 0x0000FF00) >>  8)-256);
		stream.write(b1);
		stream.write(b2);
	}
	
	public static int readInt(CountingInputStream stream) throws Exception {
		byte b1 = readByte(stream);
		byte b2 = readByte(stream);
		byte b3 = readByte(stream);
		byte b4 = readByte(stream);
		// make them unsigned
		short ub1 = (short)(b1 < 0 ? 256 + b1 : b1);
		short ub2 = (short)(b2 < 0 ? 256 + b2 : b2);
		short ub3 = (short)(b3 < 0 ? 256 + b3 : b3);
		short ub4 = (short)(b4 < 0 ? 256 + b4 : b4);
		return (ub4<<24) | (ub3<<16) | (ub2<<8) | (ub1);
	}
	
	public static void writeInt(ByteArrayOutputStream stream, int i) throws Exception {
		byte b1 = (byte)(((i & 0x000000FF)      )-256);
		byte b2 = (byte)(((i & 0x0000FF00) >>  8)-256);
		byte b3 = (byte)(((i & 0x00FF0000) >> 16)-256);
		byte b4 = (byte)(((i & 0xFF000000) >> 24)-256);
		stream.write(b1);
		stream.write(b2);
		stream.write(b3);
		stream.write(b4);
	}
	
	public static String readString(CountingInputStream stream) throws Exception {
		byte length = readByte(stream);
		
		byte[] result = new byte[length];
		
		for(int i = 0; i < length; i++) {
			result[i] = readByte(stream);
		}
		
		return new String(result);
	}
	
	public static void writeString(ByteArrayOutputStream stream, String s) throws Exception {		
		byte length = (byte)s.length();
		stream.write(length);
		
		byte[] asBytes = s.getBytes();
		
		for(int i = 0; i < length; i++) {
			stream.write(asBytes[i]);
		}
	}

	public static String[] readFileLinesIntoStringArray(String filename) throws Exception {
		InputStream is = StreamOperations.class.getResourceAsStream(filename);
		Vector textLines = new Vector();

		StringBuffer stringBuffer = new StringBuffer();
		short b;
		while ( (b = (short)is.read()) != -1 ) {
			if( (char)b == '\n' ) {
				textLines.addElement(new String(stringBuffer));
				stringBuffer = new StringBuffer();
			}
			else if( (char)b != '\t' && (char)b != '\r' )
				stringBuffer.append((char)b);
		}
		textLines.addElement(new String(stringBuffer));
        is.close(); 

        String[] outputLines = new String[textLines.size()];
		textLines.copyInto(outputLines);
		return outputLines;
	}

	public static String[] readFileLinesWrappedIntoStringArray(
			String filename, Font font, int width) throws Exception {
		InputStream is = StreamOperations.class.getResourceAsStream(filename);
		Vector textLines = new Vector();

		StringBuffer stringBuffer = new StringBuffer();
		short b;
		while ( (b = (short)is.read()) != -1 ) {
			if( (char)b == '\n' ) {
				wrapTextlines(new String(stringBuffer), font, width, textLines);
				stringBuffer = new StringBuffer();
			}
			else if( (char)b != '\t' && (char)b != '\r' )
				stringBuffer.append((char)b);
		}
		wrapTextlines(new String(stringBuffer), font, width, textLines);
        is.close(); 

        String[] outputLines = new String[textLines.size()];
		textLines.copyInto(outputLines);
		return outputLines;
	}

	public static Vector wrapTextlines(String text, Font font, int width, Vector result) 
	{
		if( result == null )
			result = new Vector ();
		if (text == null) return result;
		
		boolean hasMore = true;
		// The current index of the cursor
		int current = 0;
		// The next line break index
		int lineBreak = -1;
		// The space after line break
		int nextSpace = -1;
		
		while (hasMore) {
			// Find the line break
			while (true) {
				lineBreak = nextSpace;
				if (lineBreak == text.length() - 1) {
					// We have reached the last line
					hasMore = false;
					break;
				} 
				else {
					nextSpace = text.indexOf(' ', lineBreak+1);
					if (nextSpace == -1)
					  nextSpace = text.length() -1;
					int linewidth = font.substringWidth(text,
					                  current, nextSpace-current);
					// If too long, break out of the find loop
					if (linewidth > width) break;
				}
			}
			String line = text.substring(current, lineBreak + 1);
			result.addElement(line);
			current = lineBreak + 1;
		}
		return result;
	}
	
	/** Tests **/
	public static void test() {
		System.out.println("StreamOperations.testSkipBytes(): " + testSkipBytes());
		System.out.println("StreamOperations.testReadByte(): " + testReadByte());
	}
	private static String testSkipBytes() {
		try {
			InputStream is = Class.forName("CountingInputStream").getResourceAsStream("/testData/StreamOperations/10bytes.data"); // 10 bytes
			if(is == null) throw new Exception("");
			
			try {
				CountingInputStream cis = new CountingInputStream(is);
				
				StreamOperations.skipBytes(cis, 0);
				if(cis.tell() != 0) return "skipBytes(0) skipped more than 0 bytes: " + cis.tell();
				
				StreamOperations.skipBytes(cis, 5);
				if(cis.tell() != 5) return "skipBytes(5) did not skip 5 bytes: " + cis.tell();
				
				StreamOperations.skipBytes(cis, 5);
				if(cis.tell() != 10) return "The file position should be at 10 but it is at " + cis.tell();
				
				try {
					StreamOperations.skipBytes(cis, 1);	
				}
				catch(Exception e) {
					if(0 == e.getMessage().compareTo("End of File")) {
						return "Success";
					}
					return "Exception: " + e.toString();
				}
				
				return "The file position should be at eof but it is at " + cis.tell();
			}
			catch(Exception e) {
				if(0 == e.getMessage().compareTo("End of File")) {
					return "File end reached too early.";
				}
				return "Exception: " + e.toString();
			}
		}
		catch(Exception e) {
			return "Could not open test data: " + "/testData/StreamOperations/10bytes.data";
		}
	}
	private static String testReadByte() {
		try {
			InputStream is = Class.forName("CountingInputStream").getResourceAsStream("/testData/StreamOperations/10bytes.data"); // 10 bytes
			if(is == null) throw new Exception("");
			
			try {
				CountingInputStream cis = new CountingInputStream(is);
				
				byte b;
				
				for(int i = 0; i <= 9; i++)
				{
					b = StreamOperations.readByte(cis);
					if(b != 48+i) return "readByte returned wrong data: " + b + ". Should be " + (48+i);
				}
				
				try {
					b = StreamOperations.readByte(cis);
					return "readByte read after file end.";
				}
				catch(Exception e) {
					if(0 == e.getMessage().compareTo("End of File")) {
						return "Success";
					}
					return "Exception: " + e.toString();
				}
			}
			catch(Exception e) {
				if(0 == e.getMessage().compareTo("End of File")) {
					return "File end reached too early.";
				}
				return "Exception: " + e.toString();
			}
		}
		catch(Exception e) {
			return "Could not open test data: " + "/testData/StreamOperations/10bytes.data";
		}
	}
}
