package LevelObjects;
import Position;
import StreamOperations;
import Level;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import CountingInputStream;


/**
 * ObjectStates saves information about any LevelObject for later restore. 
 * It is needed because a Block releases all containig LevelObjects when it is released itself.
 * To restore the properties of any LevelObject (e.g. Enemy, Door, Chest) when the Block is being loaded again,
 * we need the old properties (e.g. dead, open, empty) to prevent cheating. 
 * @author Christian Woizischke
 *
 */
public class LevelObjectStates {
	
	private Hashtable states;
	
	public LevelObjectStates(int capacity) {
		states = new Hashtable(capacity);
	}
	
	/**
	 * Saves the state of object o.
	 * @param o
	 */
	public void saveState(LevelObject o) {
		if(o.getId() != LevelObject.NO_ID) {
			LevelObjectState state = o.getState();
			if(state != null) {
				// object has non-default properties -> save.
				states.put(new Short(o.getId()), state);
			}
		}
	}
	
	/**
	 * Sets the properties of obj from a saved state.
	 * @param obj The object for output
	 * @return The LevelObjectState for obj
	 */
	public LevelObjectState loadState(LevelObject obj) {
		Short key = new Short(obj.getId());
		LevelObjectState state = (LevelObjectState)states.get(key);
		if(state != null) {
			obj.setState(state);
			// do not remove..... states.remove(key);
		}
		return state;
	}
	
	/**
	 * Returns the properties of obj. This state may be modified
	 * @return The LevelObjectState for obj
	 */
	public LevelObjectState getState(short id) {
		LevelObjectState state = (LevelObjectState)states.get(new Short(id));
		return state;
	}
	
	/**
	 * Sets & saves the properties of obj.
	 */
	public void setState(short id, LevelObjectState state) {
		states.put(new Short(id), state);
		LevelObject obj = Level.activeLevel.getObject(id);
		
		if(obj != null) {
			obj.setState(state);
		}
	}
	
	/**
	 * Saves all states to a stream. The stream is given by SaveGameManager.
	 * @param stream
	 * @throws Exception
	 */
	public void saveToStream(ByteArrayOutputStream stream) throws Exception {
		Level level = Level.activeLevel;

		short objectCount = (short)(level.getBiggestObjectIdAround()+1);

		// save object count
		StreamOperations.writeShort(stream, objectCount);
		
		// save all states
		for(short id = 0; id < objectCount; id++) {
			
			// get the object's state
			LevelObjectState state = level.getObjectState(id);
			if(state == null) {
				// no object with the id i. save NO_ID and proceed with the next object:
				StreamOperations.writeShort(stream, LevelObject.NO_ID);
				continue;
			}
			
			// save object id
			StreamOperations.writeShort(stream, id);
			
			// save the state if the object has an id
			if(id != LevelObject.NO_ID) {
				state.saveToStream(stream);
			}
		}
	}
	
	/**
	 * Loads all states from a stream. The stream is given by SaveGameManager.
	 * @param stream
	 * @throws Exception
	 */
	public void loadFromStream(CountingInputStream stream) throws Exception {
		// load object count
		int objectCount = StreamOperations.readShort(stream);

		// load each state
		for(int i = 0; i < objectCount; i++) {
			// get id
			short objId = StreamOperations.readShort(stream);
			
			if(objId != LevelObject.NO_ID) {
				// get state
				LevelObjectState state = new LevelObjectState();
				state.loadFromStream(stream);
				
				// set object state
				this.setState(objId, state);
			}
		}
	}
	
	
	
	/** TESTS **/
	public static void test() {
		System.out.println("LevelObjectStates.testLoadSave(): " + testLoadSave());
	}
	private static String testLoadSave() {
		try {
			LevelObject o = new LevelObject((short)5, new Position(2,4), null);
			LevelObjectState originalState = o.getState();
			
			LevelObjectStates s = new LevelObjectStates(10);
			
			s.saveState(o);
			
			LevelObjectState savedState = s.getState(o.getId());
			
			// test saved state
			if(!originalState.position.equals(savedState.position) || !(originalState.state == savedState.state)) {
				return "saved state is different from the original state.";
			}

			// create other test state
			LevelObjectState otherState = new LevelObjectState();
			otherState.position = new Position(1,2);
			otherState.state = (byte)(LevelObjectState.ALIVE | LevelObjectState.ENABLED | LevelObjectState.OPEN | LevelObjectState.VISIBLE);
			o.setState(otherState);
			
			// set other state
			LevelObjectState returnedState = s.loadState(o);
			LevelObjectState loadedState = o.getState();
			
			// do not save other state
			
			// test returned state
			if(!returnedState.position.equals(loadedState.position) || !(returnedState.state == loadedState.state)) {
				return "returnedState is different from loadedState.";
			}
			
			return "Success";
		}
		catch(Exception e) {
			return "Exception: " + e.toString();
		}
	}
}
