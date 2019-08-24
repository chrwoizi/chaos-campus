import javax.microedition.rms.*;
import java.util.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Manages the savegames
 * @author Christian Woizischke
 *
 */
public class SaveGameManager {
	
	private class RecordNameId {
		public String name;
		public int id;
		public RecordNameId(String name, int id) {
			this.name = name;
			this.id = id;
		}
	}
	
	static private SaveGameManager theInstance = new SaveGameManager();
	static public SaveGameManager getInstance() {
		return theInstance;
	}
	
	private static final int INDEX_RECORD = 1;
	
	private RecordStore recordStore;
	private Vector ids;
	
	private String previousName = null;
	
	private SaveGameManager() {
		try {
			// open/create record store
			recordStore = RecordStore.openRecordStore("javy", true);
		} 
		
		catch(Exception e) {
			new Error("Record Store javy", e);
		}
		
		// get record names from record store
		ids = getRecordNames();
	}
	
	/**
	 * Loads the record id's names from the first record.
	 * @return
	 */
	private Vector getRecordNames() {
		Vector result = null;
		
		try {
			// get RecordNameIds from recordStore.
			byte[] namesAndIds = recordStore.getRecord(INDEX_RECORD);
			CountingInputStream namesAndIdsStream =  new CountingInputStream(new ByteArrayInputStream(namesAndIds));
		
			int saveGameCount = StreamOperations.readByte(namesAndIdsStream);
				
			// create vector. initial capacity is number of savegames
			result = new Vector(saveGameCount);
			
			// read RecordNameIds
			for(int i = 0; i < saveGameCount; i++) {
				int id = StreamOperations.readInt(namesAndIdsStream);
				String name = StreamOperations.readString(namesAndIdsStream);
				result.addElement(new RecordNameId(name, id));	
			}
		} 
		
		catch(InvalidRecordIDException e) {
			// record not found -> no savegames in the store. create record0 and proceed with empty vector
			
			// create index record
			try {
				byte[] saveGameCount = new byte[1];
				saveGameCount[0] = 0;
				int id = recordStore.addRecord(saveGameCount, 0, 1);
				if(id != INDEX_RECORD) {
					throw new Exception("The first Record is not " + INDEX_RECORD);
				}
			}
			catch(Exception f) {
				new Error("getRecordNames create record", f);
			}
			
			// create empty result
			result = new Vector();
		} 
		
		catch(Exception e) {
			// catch other exceptions
			new Error("getRecordNames unknown", e);
		}
		
		return result;
	}
	
	private void loadToCurrentLevelFromStream(CountingInputStream stream) {
		try {			
			// load object states
			Level.activeLevel.objectStates.loadFromStream(stream);
			// load spawned items
			LevelObjects.Item.loadSpawnedItemsFromStream(stream);
		} 
		
		catch(Exception e) {
			new Error("saveGameManager loadLevelFromStream", e);
		}
	}
	
	private void loadLevelFromStream(byte levelId, CountingInputStream stream) {
		try {
			// load level
			Level.activeLevel.loadWithLoadingScreen(levelId, Player.getInstance().getPosition());
			
			// load object states
			Level.activeLevel.objectStates.loadFromStream(stream);
			// load spawned items
			LevelObjects.Item.loadSpawnedItemsFromStream(stream);
		} 
		
		catch(Exception e) {
			new Error("saveGameManager loadLevelFromStream", e);
		}
	}
	
	private void saveLevelToStream(ByteArrayOutputStream stream) {
		try {
			// save object states
			Level.activeLevel.objectStates.saveToStream(stream);
			// save spawned items
			LevelObjects.Item.saveSpawnedItemsToStream(stream);
			
			// flush stream
			stream.flush();
		} 
		
		catch(Exception e) {
			new Error("saveGameManager saveLevelToStream", e);
		}
	}
	
	private boolean loadPlayerAndStoryFromStream(CountingInputStream stream) {
		try {
			// load story
			Story.StoryManager.getInstance().loadFromStream(stream);
			// GameManager
			GameManager.getInstance().loadFromStream(stream);
			// load player
			Player.getInstance().loadFromStream(stream);
			
			return true;
		} 
		
		catch(Exception e) {
			new Error("saveGameManager loadPlayerAndStoryFromStream", e);
			return false;
		}
	}
	
	private void savePlayerAndStoryToStream(ByteArrayOutputStream stream) {
		try {
			// save story
			Story.StoryManager.getInstance().saveToStream(stream);
			// GameManager
			GameManager.getInstance().saveToStream(stream);
			// save player
			Player.getInstance().saveToStream(stream);
			
			// flush stream
			stream.flush();
		} 
		
		catch(Exception e) {
			new Error("saveGameManager savePlayerAndStoryToStream", e);
		}
	}
	
	/**
	 * Deletes a record. id must exist
	 * @param id The record's id in recordStore
	 */
	private void delete(int id) {
		try {
			// delete record from store
			recordStore.deleteRecord(id);

			// delete record id from index
			for(int i = 0; i < ids.size(); i++) {
				RecordNameId ni = (RecordNameId)ids.elementAt(i);
				if(ni.id == id) {
					ids.removeElement(ni);
					break;
				}
			}
			
			saveNameIds();
		} 
		
		catch(Exception e) {
			new Error("SaveGameManager delete " + id, e);
		}
	}
	
	/**
	 * Returns the id of the record. Returns 0 if the record does not exist.
	 * @param name
	 * @return
	 */
	private int getId(String name) {
		for(int i = 0; i < ids.size(); i++) {
			RecordNameId ni = (RecordNameId)ids.elementAt(i);
			if(ni.name.compareTo(name) == 0) {
				return ni.id;
			}
		}
		return 0;
	}
	
	private boolean saveNameIds() {
		try {
			// make byte stream
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			
			// fill stream
			stream.write(ids.size());
			for(int i = 0; i < ids.size(); i++) {
				RecordNameId ni = (RecordNameId)ids.elementAt(i);
				StreamOperations.writeInt(stream, ni.id);
				StreamOperations.writeString(stream, ni.name);
			}
	
			// save stream
			return setRecord(INDEX_RECORD, stream);	
		}
		catch(Exception e) {
			new Error("SaveGameManager saveNameIds", e);
			return false;
		}
	}
	
	/**
	 * Adds a stream to the records and saves the name+id
	 * @param stream
	 * @return true on success
	 */
	private boolean addRecord(String name, ByteArrayOutputStream stream) {
		try {
			byte[] asBytes = stream.toByteArray();
			int id = recordStore.addRecord(asBytes, 0, asBytes.length);
			ids.addElement(new RecordNameId(name, id));
			boolean result = saveNameIds();
			return result;
		} 
		
		catch(Exception e) {
			new Error("SaveGameManager addRecord " + name, e);
			return false;
		}
	}
	
	/**
	 * Sets an existing record to stream
	 * @param id An existing record
	 * @param stream
	 * @return true on success
	 */
	private boolean setRecord(int id, ByteArrayOutputStream stream) {
		try {
			byte[] asBytes = stream.toByteArray();
			recordStore.setRecord(id, asBytes, 0, asBytes.length);
			return true;
		} 
		
		catch(Exception e) {
			new Error("SaveGameManager setRecord " + id, e);
			return false;
		}
	}
	
	/**
	 * Loads the level state
	 * @param levelId the level id.
	 * @param name
	 * @return
	 */
	private boolean loadLevelAndState(int levelId, String name) {
		
		String recordName = levelId + "_" + name; 
		
		try {
			int id = getId(recordName);
			if(id > 0) {
				byte[] asBytes = recordStore.getRecord(id);
				CountingInputStream stream = new CountingInputStream(new ByteArrayInputStream(asBytes));
				loadLevelFromStream((byte)levelId, stream);
				return true;
			}
			else {
				return false;
			}
		}
		
		catch(Exception e) {
			new Error("SaveGameManager loadLevelState " + recordName, e);
			return false;
		}
	}
	
	/**
	 * Loads the state of the current level
	 * @param name
	 * @return
	 */
	public boolean loadCurrentLevelState(String name) {
		
		previousName = name;
		
		String recordName = Level.activeLevel.getLevelId() + "_" + name;
		
		try {
			int id = getId(recordName);
			if(id > 0) {
				byte[] asBytes = recordStore.getRecord(id);
				CountingInputStream stream = new CountingInputStream(new ByteArrayInputStream(asBytes));
				loadToCurrentLevelFromStream(stream);
				return true;
			}
			else {
				return false;
			}
		}
		
		catch(Exception e) {
			new Error("SaveGameManager loadCurrentLevelState " + recordName, e);
			return false;
		}
	}
	
	/**
	 * Loads the state of the current level from the previous used name
	 * @param name
	 * @return
	 */
	public boolean loadCurrentLevelState() {
		if(previousName == null) {
			new Error("SaveGameManager loadCurrentLevelState: no previous save");
			return false;
		}
			
		String name = previousName;
		
		String recordName = Level.activeLevel.getLevelId() + "_" + name; 
		
		try {
			int id = getId(recordName);
			if(id > 0) {
				byte[] asBytes = recordStore.getRecord(id);
				CountingInputStream stream = new CountingInputStream(new ByteArrayInputStream(asBytes));
				loadToCurrentLevelFromStream(stream);
				return true;
			}
			else {
				return false;
			}
		}
		
		catch(Exception e) {
			new Error("SaveGameManager loadCurrentLevelState " + recordName, e);
			return false;
		}
	}
	
	/**
	 * Saves the current level's state
	 * @param name
	 * @return
	 */
	public boolean saveCurrentLevelState(String name) {
		// handle input errors
		if(name.length() == 0) {
			return false;
		}
		
		previousName = name;
		
		// make byte stream
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		// fill stream
		saveLevelToStream(stream);
		
		String recordName = Level.activeLevel.getLevelId() + "_" + name;  

		int id = getId(recordName);
		if(id > 0) {
			// save stream to existing record
			return setRecord(id, stream);
		}
		else {
			// save stream to new record
			return addRecord(recordName, stream);	
		}
	}
	
	/**
	 * Saves the current level's state to the previous used name
	 * @param name
	 * @return
	 */
	public boolean saveCurrentLevelState() {
		if(previousName == null) {
			new Error("SaveGameManager saveCurrentLevelState: no previous save");
			return false;
		}
			
		String name = previousName;
		
		// handle input errors
		if(name.length() == 0) {
			return false;
		}
		
		// make byte stream
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		// fill stream
		saveLevelToStream(stream);
		
		String recordName = Level.activeLevel.getLevelId() + "_" + name;  

		int id = getId(recordName);
		if(id > 0) {
			// save stream to existing record
			return setRecord(id, stream);
		}
		else {
			// save stream to new record
			return addRecord(recordName, stream);	
		}
	}
	
	/**
	 * Loads the player and story states
	 * @param name
	 * @return false on error
	 */
	private boolean loadPlayerAndStoryState(String name) { 
		try {
			int id = getId(name);
			if(id > 0) {
				byte[] asBytes = recordStore.getRecord(id);
				CountingInputStream stream = new CountingInputStream(new ByteArrayInputStream(asBytes));
				return loadPlayerAndStoryFromStream(stream);
			}
			else {
				return false;
			}
		}
		
		catch(Exception e) {
			new Error("SaveGameManager load " + name, e);
			return false;
		}
	}
	
	/**
	 * Saves the player and story states
	 * @param name
	 * @return false on error
	 */
	private boolean savePlayerAndStoryState(String name) { 
		// handle input errors
		if(name.length() == 0) {
			return false;
		}
		
		// make byte stream
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		// fill stream
		savePlayerAndStoryToStream(stream);

		int id = getId(name);
		if(id > 0) {
			// save stream to existing record
			return setRecord(id, stream);
		}
		else {
			// save stream to new record
			return addRecord(name, stream);	
		}
	}
	
	/**
	 * Loads the level id
	 * @param name
	 * @return level id or -1 on error
	 */
	private int loadLevelId(String name) {
		String recordName = "LevelId_" + name; 
		
		try {
			int id = getId(recordName);
			if(id > 0) {
				byte[] asBytes = recordStore.getRecord(id);
				CountingInputStream stream = new CountingInputStream(new ByteArrayInputStream(asBytes));
				return StreamOperations.readByte(stream);
			}
			else {
				return -1;
			}
		}
		
		catch(Exception e) {
			new Error("SaveGameManager load " + recordName, e);
			return -1;
		}
	}
	
	/**
	 * Saves the level id
	 * @param name
	 * @return
	 */
	private boolean saveCurrentLevelId(String name) {
		// handle input errors
		if(name.length() == 0) {
			return false;
		}
		
		String recordName = "LevelId_" + name; 
		
		// make byte stream
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		// fill stream
		stream.write(Level.activeLevel.getLevelId());

		int id = getId(recordName);
		if(id > 0) {
			// save stream to existing record
			return setRecord(id, stream);
		}
		else {
			// save stream to new record
			return addRecord(recordName, stream);	
		}
	}
	
	/**
	 * Loads the game
	 * @param name
	 * @return
	 */
	public boolean load(String name) {
		// handle input errors
		if(name.length() == 0) {
			return false;
		}
		
		previousName = name;
		
		int levelId = loadLevelId(name);
		if(levelId == -1) return false;
		if( !loadPlayerAndStoryState(name) )
			return false;
		if( !loadLevelAndState(levelId, name) )
			return false;
		return true;
	}
	
	/**
	 * Saves the game
	 * @param name
	 * @return
	 */
	public boolean save(String name) {
		// handle input errors
		if(name.length() == 0) {
			return false;
		}
		
		previousName = name;
		
		return saveCurrentLevelId(name) && saveCurrentLevelState(name) && savePlayerAndStoryState(name);
	}
	
	/**
	 * Returns true if the savegame exists
	 * @param name
	 * @return
	 */
	public boolean exists(String name) {
		return getId(name) > 0;
	}
	
	/**
	 * Deletes a savegame.
	 * @param name
	 */
	public void delete(String name) {
		int MAX_LEVEL_ID = 10;
		for(int i = 0; i < MAX_LEVEL_ID; i++) {
			String recordName = i + "_" + name;
			int id = getId(recordName);
			if(id > 0) {
				delete(id);
			}
		}

		int id = getId(name);
		if(id > 0) {
			delete(id);
		}

		id = getId("LevelId_" + name);
		if(id > 0) {
			delete(id);
		}
	}
	
	
	/** Tests **/
	public static void test() {
		System.out.println("SaveGameManager.testAll(): " + testAll());
	}
	private static String testAll() {
		try {
			if(!GameManager.getInstance().saveGame("test")) return "Save failed.";
			if(!SaveGameManager.getInstance().exists("test")) return "Exists failed.";
			if(!GameManager.getInstance().loadGame("test")) return "Load failed.";
			
			return "Success";
		}
		catch(Exception e) {
			return "Exception: " + e.toString();
		}
	}
}
