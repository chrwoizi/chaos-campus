
import LevelObjects.*;



public class LevelLoader implements Runnable {

	private static final byte CURRENT_MAP_VERSION =1;

	private static final byte MAP_HEADER_SIZE =7;
	
	private static final byte OBJECT_TYPE_STATIC =1;
	private static final byte OBJECT_TYPE_NPC =2;
	private static final byte OBJECT_TYPE_DOOR =4;
	private static final byte OBJECT_TYPE_CONTAINER =5;
	private static final byte OBJECT_TYPE_BREAKABLE =6;
	private static final byte OBJECT_TYPE_MOVEABLE =7;
	private static final byte OBJECT_TYPE_ITEM =8;
	private static final byte OBJECT_TYPE_SOUND =-100;
	private static final byte OBJECT_TYPE_DAMAGER =10;
	private static final byte OBJECT_TYPE_TRIGGER_STORY =100;
	private static final byte OBJECT_TYPE_TRIGGER_ENABLER =101;
	private static final byte OBJECT_TYPE_TRIGGER_DOOR =104;
	private static final byte OBJECT_TYPE_TRIGGER_DOOR_SOUND =105;
	private static final byte OBJECT_TYPE_TRIGGER_CONTAINER =106;
	private static final byte OBJECT_TYPE_TRIGGER_CONTAINER_SOUND =107;
	private static final byte OBJECT_TYPE_TRIGGER_EXIT =110;
	private static final byte OBJECT_TYPE_TRIGGER_TELEPORT =111;
	private static final byte OBJECT_TYPE_TRIGGER_COMMENT =112;
	
	private short[] addBlockIds;
	private short[] removeBlockIds;
	private Block[] blocksRecentlyLoaded;
	
	private boolean loadingNewLevel = false;
	public byte loadNewLevelId;
	public Position loadNewLevelStartPosition;
	
	public LevelLoader() {
		
	}

	/**
	 * loads level size
	 * @return true if succeeded
	 */
	public boolean loadMapHeader() {
		String filename = "/level/" + Level.activeLevel.getName() + ".map";
		// load level size from file
		try {
			CountingInputStream stream = new CountingInputStream(getClass().getResourceAsStream(filename));
			if(stream == null) throw new Exception("Could not open level file " + filename);
			
			// skip header
			byte version = StreamOperations.readByte(stream);
			if(version == CURRENT_MAP_VERSION) {
				Level.activeLevel.widthInBlocks = StreamOperations.readByte(stream);
				Level.activeLevel.heightInBlocks = StreamOperations.readByte(stream);

				Level.activeLevel.blocks = new Block[Level.activeLevel.widthInBlocks][Level.activeLevel.heightInBlocks];
				
				short playerStartX = StreamOperations.readShort(stream);
				short playerStartY = StreamOperations.readShort(stream);
				Level.activeLevel.playerStart = new Position(playerStartX,playerStartY);
			}
			else {
				throw new Exception("Level file " + filename + " has wrong version: " + version + " Should be " + CURRENT_MAP_VERSION);
			}

			stream.close();
		}
		catch(Exception e) {
			new Error("Level file " + filename + " could not be read.", e);
			return false;
		}
		
		return true;
	}

	/**
	 * loads blocks with blockIds
	 * @return true if succeeded
	 */
	private boolean loadMapBlocks() {
		String filename = "/level/" + Level.activeLevel.getName() + ".map";
		// load blocks with blockIds
		try {
			CountingInputStream stream = new CountingInputStream(getClass().getResourceAsStream(filename));
			if(stream == null) throw new Exception("Could not open level file " + filename);
			
			// skip header
			byte version = StreamOperations.readByte(stream);
			if(version == CURRENT_MAP_VERSION) {
				// skip rest of header
				StreamOperations.skipBytes(stream, MAP_HEADER_SIZE-1);
				
				// create & load blocks
				for(int i = 0; i < addBlockIds.length; i++) {
					int x,y;
					//try {
						x = addBlockIds[i] % Level.activeLevel.widthInBlocks;
						y = addBlockIds[i] / Level.activeLevel.widthInBlocks;
					//} catch(Exception e) {throw new Exception("A");}

					Block b = new Block(x, y);
					
					// distance between previous block and current block in the file
					int distance = addBlockIds[i] - (i==0 ? 0 : (addBlockIds[i-1]+1));
					
					if(distance < 0) {
						throw new Exception("addBlockIds must be sorted.");
					}
					
					StreamOperations.skipBytes(stream, 128*distance); // skip blocks

					//try {
						// load one block
						b.load(stream);
					//} catch(Exception e) {throw new Exception("B");}

					//try {
						blocksRecentlyLoaded[i] = b;
					//} catch(Exception e) {throw new Exception("C");}
				}
			}
			else {
				throw new Exception("Level file " + filename + " has wrong version: " + version + " Should be " + CURRENT_MAP_VERSION);
			}
			
			stream.close();
		}
		catch(Exception e) {
			new Error("Level file " + filename + " could not be read.", e);
			return false;
		}
		
		return true;
	}

	/**
	 * loads object states from dat
	 * @return true if succeeded
	 */
	public boolean loadObjectStates() {
		String filename = "/level/" + Level.activeLevel.getName() + ".dat";
		try {
			CountingInputStream stream = new CountingInputStream(getClass().getResourceAsStream(filename));
			if(stream == null) throw new Exception("Could not open level file " + filename);

			// level size
			byte width = Level.activeLevel.widthInBlocks;
			byte height = Level.activeLevel.heightInBlocks;
			
			// the block offsets
			int[] offsets = new int[width*height];
			
			// load all offsets
			for(int i = 0; i < width*height; i++) {
				// get offset from file
				offsets[i] = StreamOperations.readInt(stream);
			}

			// load object states
			for(short i = 0; i < width*height; i++) {
				if(offsets[i] == 0) continue;
				
				// distance between current position and current block in the file
				int distance = offsets[i] - (int)stream.tell();
				
				// go to current block
				StreamOperations.skipBytes(stream, distance);
				
				byte objtype;
				if(i == width*height-1) {
					try {
						while(true) {
							// try to load object type
							objtype = StreamOperations.readByte(stream);
							// load the object
							loadObject(i, stream, objtype);
						}
					} 
					catch(Exception e) {
						// could not read -> file end reached -> no object in the last block
					}
				}
				else {
					while(stream.tell() != offsets[i+1])
					{
						// falscher offset oder falsche objektlänge in der datei
						//if(stream.tell() > offsets[i+1]) throw new Exception("Inkompatible Daten in " + filename);
						
						// load object type
						objtype = StreamOperations.readByte(stream);
	
						// load the object
						loadObjectState(i, stream, objtype);
					}
				}
			
			}

			stream.close();
		}
		catch(Exception e) {
			new Error("Level file " + filename + " could not be read.", e);
			return false;
		}
		
		return true;
	}
	
	/**
	 * loads objects of blocks with blockIds
	 * @return true if succeeded
	 */
	private boolean loadDatBlocks() {		
		String filename = "/level/" + Level.activeLevel.getName() + ".dat";
		try {
			CountingInputStream stream = new CountingInputStream(getClass().getResourceAsStream(filename));
			if(stream == null) throw new Exception("Could not open level file " + filename);

			// level size
			byte width = Level.activeLevel.widthInBlocks;
			byte height = Level.activeLevel.heightInBlocks;
			
			// the block offsets
			int[] offsets = new int[width*height];
			
			//try {
				// load all offsets
				for(int i = 0; i < width*height; i++) {
					// get offset from file
					offsets[i] = StreamOperations.readInt(stream);
				}
			//} catch(Exception e) {throw new Exception("A");}
			
			// load objects
			for(short i = 0; i < addBlockIds.length; i++) {
				//try {
					if(offsets[addBlockIds[i]] == 0) continue;
				//} catch(Exception e) {throw new Exception("B");}
				
				//try {
					// distance between current position and current block in the file
					int distance = offsets[addBlockIds[i]] - (int)stream.tell();
					
					// go to current block
					StreamOperations.skipBytes(stream, distance);
				//} catch(Exception e) {throw new Exception("C");}
				
				byte objtype;
				if(addBlockIds[i] == width*height-1) {
					try {
						while(true) {
							// try to load object type
							objtype = StreamOperations.readByte(stream);
							// load the object
							loadObject(i, stream, objtype);
						}
					} 
					catch(Exception e) {
						// could not read -> file end reached -> no object in the last block
					}
				}
				else {
					while(stream.tell() != offsets[addBlockIds[i]+1])
					{	
						// falscher offset oder falsche objektlänge in der datei
						if(stream.tell() > offsets[addBlockIds[i]+1]) throw new Exception("Inkompatible Daten in " + filename);
						
						// load object type
						objtype = StreamOperations.readByte(stream);
	
						// load the object
						loadObject(i, stream, objtype);
					}
				}
			
			}

			stream.close();
		}
		catch(Exception e) {
			new Error("Level file " + filename + " could not be read.", e);
			return false;
		}
		
		return true;
	}
	
	private void loadObjectState(short blockId, CountingInputStream stream, short objtype) throws Exception {
		LevelObject obj = null;
		// load object
		switch(objtype) {
		case OBJECT_TYPE_STATIC: 
		{
			obj = Static.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_NPC:
		{
			obj = NpcCreator.loadNpcFromStream(null, blockId, stream);
		} break;
		case OBJECT_TYPE_DOOR:
		{
			obj = Door.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_CONTAINER:
		{
			obj = Container.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_BREAKABLE:
		{
			obj = Breakable.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_MOVEABLE:
		{
			obj = Moveable.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_ITEM:
		{
			obj = Item.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_SOUND:
		{
			new Error("sound object is no longer supported");
			//obj = SoundObject.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_DAMAGER:
		{
			obj = Damager.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_TRIGGER_STORY:
		{
			obj = StoryTrigger.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_TRIGGER_ENABLER:
		{
			obj = EnablerTrigger.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_TRIGGER_DOOR:
		{
			obj = DoorTrigger.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_TRIGGER_DOOR_SOUND:
		{
			new Error("door sound trigger is no longer supported");
			//obj = DoorSoundTrigger.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_TRIGGER_CONTAINER:
		{
			obj = ContainerTrigger.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_TRIGGER_CONTAINER_SOUND:
		{
			new Error("container sound trigger is no longer supported");
			//obj = ContainerSoundTrigger.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_TRIGGER_EXIT:
		{
			obj = ExitTrigger.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_TRIGGER_TELEPORT:
		{
			obj = TeleportTrigger.load(null, blockId, stream);
		} break;
		case OBJECT_TYPE_TRIGGER_COMMENT:
		{
			obj = CommentTrigger.load(null, blockId, stream);
		} break;
		default:
			new Error("unknown object type: " + objtype);
		}
		
		// save state
		Level.activeLevel.objectStates.saveState(obj);
		// free object
		obj = null;
	}
	
	private void loadObject(short blockId, CountingInputStream stream, short objtype) throws Exception {		
		// load object, 
		LevelObject obj = null;
		switch(objtype) {
		case OBJECT_TYPE_STATIC: 
		{
			obj = Static.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_NPC:
		{
			obj = NpcCreator.loadNpcFromStream(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_DOOR:
		{
			obj = Door.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_CONTAINER:
		{
			obj = Container.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_BREAKABLE:
		{
			obj = Breakable.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_MOVEABLE:
		{
			obj = Moveable.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_ITEM:
		{
			obj = Item.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_SOUND:
		{
			new Error("sound object is no longer supported");
			//obj = SoundObject.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_DAMAGER:
		{
			obj = Damager.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_TRIGGER_STORY:
		{
			obj = StoryTrigger.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_TRIGGER_ENABLER:
		{
			obj = EnablerTrigger.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_TRIGGER_DOOR:
		{
			obj = DoorTrigger.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_TRIGGER_DOOR_SOUND:
		{
			new Error("door sound trigger is no longer supported");
			//obj = DoorSoundTrigger.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_TRIGGER_CONTAINER:
		{
			obj = ContainerTrigger.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_TRIGGER_CONTAINER_SOUND:
		{
			new Error("container sound trigger is no longer supported");
			//obj = ContainerSoundTrigger.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_TRIGGER_EXIT:
		{
			obj = ExitTrigger.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_TRIGGER_TELEPORT:
		{
			obj = TeleportTrigger.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		case OBJECT_TYPE_TRIGGER_COMMENT:
		{
			obj = CommentTrigger.load(blocksRecentlyLoaded[blockId], addBlockIds[blockId], stream);
		} break;
		}

		if(Level.activeLevel.objectExists(obj.getId())) return;
		
		// add to block
		if(objtype >= 100) {
			// Trigger
			blocksRecentlyLoaded[blockId].addTrigger((Trigger)obj);
		} 
		else {
			if(obj.isDynamicLevelObject()) {
				blocksRecentlyLoaded[blockId].addDynamicObject(obj);
			}
			else {
				blocksRecentlyLoaded[blockId].addStaticObject(obj);
			}
		}
		// add to level
		Level.activeLevel.addObjectReference(obj);
		// load state if existing
		Level.activeLevel.objectStates.loadState(obj);
	}
	
	/**
	 * loads and removes the blocks from the array ids.
	 *
	 */
	private synchronized void doit() {
		if(loadingNewLevel) {
			loadingNewLevel = false;
			if(!Level.activeLevel.load(loadNewLevelId, loadNewLevelStartPosition)) {
				new Error("loading failed on level " + loadNewLevelId);
			}
		}
		else {
			if(removeBlockIds != null && removeBlockIds.length > 0)
			{
				// remove blocks from block matrix 
				for(int i = 0; i < removeBlockIds.length; i++) {
					int id = removeBlockIds[i];
					int x = id % Level.activeLevel.widthInBlocks;
					int y = id / Level.activeLevel.widthInBlocks;
					
					// notify the block that it will be deleted
					if(Level.activeLevel.blocks[x][y] != null) Level.activeLevel.blocks[x][y].onDelete();
					
					// delete the block (the garbage collector will do that)
					Level.activeLevel.blocks[x][y] = null;
				}
				removeBlockIds = null;
			}
			
			if(addBlockIds != null && addBlockIds.length > 0)
			{			
				// load blocks to temporary array
				blocksRecentlyLoaded = new Block[addBlockIds.length];
	
				if(loadMapBlocks() && loadDatBlocks()) {
					// add loaded blocks to block matrix
					for(int i = 0; i < addBlockIds.length; i++) {
						int id = addBlockIds[i];
						int x = id % Level.activeLevel.widthInBlocks;
						int y = id / Level.activeLevel.widthInBlocks;
						Level.activeLevel.blocks[x][y] = blocksRecentlyLoaded[i];
					}
					addBlockIds = null;
					blocksRecentlyLoaded = null; // free memory because there may be no reference to blocks but in Level.
				}
				else {
					String err = "Could not load blocks for level " + Level.activeLevel.getName() + ":";
					for(int i = 0; i < addBlockIds.length; i++) {
						err += " " + addBlockIds[i];
					}
					err += "\n";
					
					new Error(err);
				}
			}
	
			// collect garbage
			System.gc();
			
			// print block allocation 
			/*for(int y = 0; y < Level.activeLevel.heightInBlocks; y++) {
				for(int x = 0; x < Level.activeLevel.widthInBlocks; x++) {
					System.out.print(Level.activeLevel.blocks[x][y] == null ? "O " : "X ");				
				}	
				System.out.println();
			}
			System.out.println();*/
		}
	}
	
	/**
	 * calls doit(). used by the thread
	 */
	public void run() {
		try {
			doit();
		}
		catch(Exception e) {
			new Error("Uncatched Exception", e);
		}
	}

	/**
	 * Loads Blocks to / removes Blocks from  "blocks" matrix (public attribute)
	 * @param addIds Load Blocks with addIds from file "fileName" and add them to "blocks"
	 * @param removeIds Remove Blocks with removeIds from to "blocks"
	 * @param multithreaded True if you want to load the blocks in a seperate thread.
	 */
	public void updateBlocks(short[] addIds, short[] removeIds, boolean multithreaded) 
	{
		// save ids
		if(removeIds != null) {
			int len = removeIds.length;
			removeBlockIds = new short[len];
			for(int i = 0; i < len; i++) {
				removeBlockIds[i] = removeIds[i];
			}
		}
		else {
			removeBlockIds = null;
		}
		
		if(addIds != null) {
			int len = addIds.length;
			addBlockIds = new short[len];
			for(int i = 0; i < len; i++) {
				addBlockIds[i] = addIds[i];
			}
		}
		else {
			addBlockIds = null;
		}

		if(multithreaded) {
			// run thread
			try {
				new Thread(this).start();
			}
			catch(Exception e) {
				new Error("loadBlocks: ", e);
			}
		}
		else
		{
			doit();
		}
	}
	
	public void loadNewLevel(byte id, Position startPos) {
		loadingNewLevel = true;
		loadNewLevelId = id;
		loadNewLevelStartPosition = startPos;
		
		// run thread
		try {
			/*Thread th = new Thread(this);
			th.start();
			th.join();*/
			this.run();
		}
		catch(Exception e) {
			new Error("loadNewLevel: ", e);
		}
	}
}
