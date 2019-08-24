package Story;

import IODevice;
import Error;
import StreamOperations;

import javax.microedition.lcdui.*;




/**
 * 
 * @author Martin Wahnschaffe
 *
 */
public class Comment {
	private static final byte TEXT_MOVEMENT_DELAY = 2;

	private byte commentId;
	private boolean finished;
	private String[] commentLines;
	private byte textLine = 0;
	private short pixelLine = 0;
	private short delayTime = 40;	
	private short commentRemainingTime = -1;
	private short boxHeight;
	private short lineHeight = (short)Font.getDefaultFont().getHeight();
	private short lineWidth = (short)(IODevice.getInstance().getWidth()-6);
	
	public byte getCommentId() { return commentId; }
	
	public Comment(byte commentId) {
		this.commentId = commentId;
		try {
			// load whole text of the comment
			commentLines = StreamOperations.readFileLinesWrappedIntoStringArray(
				"/comments/comment" + commentId + ".txt", 
				Font.getDefaultFont(), lineWidth);
		}
		catch( Exception e ) {
			new Error(e);
		}

		if( commentLines.length > 2 )
			boxHeight = (short)(2*lineHeight);
		else {
			boxHeight = (short)(commentLines.length*lineHeight);
			commentRemainingTime = (short)(commentLines.length*15);
		}
	}
	
	public void render(Graphics g) {
		if( !finished )
		{
			short left = (short)((IODevice.getInstance().getWidth()-lineWidth)/2);
			short top = 3;
			g.setColor(60,30,15);
			g.fillRoundRect(left-3, top-3, lineWidth+5, boxHeight+5, 10, 8);
			g.setColor(200,100,50);
			g.drawRoundRect(left-3, top-3, lineWidth+5, boxHeight+5, 10, 8);
			
			g.setClip(left, top, lineWidth, boxHeight);
			g.setColor(255, 255, 200);
			g.drawString(commentLines[textLine],
					left+3, 3 - pixelLine, Graphics.LEFT|Graphics.TOP);
			// draw second line
			if( commentLines.length > 1 ) {
				g.setColor(255, 255, 200);
				g.drawString(commentLines[textLine+1], 
						left+3, 3 - pixelLine + lineHeight, Graphics.LEFT|Graphics.TOP);
				// draw third line
				if( commentLines.length > 2 ) {
					g.setColor(255, 255, 200);
					g.drawString(commentLines[textLine+2], 
							left+3, 3 - pixelLine + 2*lineHeight, Graphics.LEFT|Graphics.TOP);
				}
			}
			g.setClip(0, 0, IODevice.getInstance().getWidth(), IODevice.getInstance().getHeight());

			// update line and text position
			if( commentRemainingTime > 0 ) {
				commentRemainingTime--;
				if( commentRemainingTime == 0 ) {
					finished = true;
				}
			}
			else {
				delayTime--;
				if( delayTime <= 0 ) {
					delayTime = TEXT_MOVEMENT_DELAY;
					// place text one pixel higher
					pixelLine++;
				}
	
				if( pixelLine == lineHeight )
				{
					if( textLine+3 == commentLines.length )					
						commentRemainingTime = (short)(delayTime + 40);
					else {
						pixelLine -= lineHeight;
						textLine++;
					}
				}
			}
		}
	}
	
	public boolean isFinished() {
		return finished;
	}
}
