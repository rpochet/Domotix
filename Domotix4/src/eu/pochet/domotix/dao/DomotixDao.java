package eu.pochet.domotix.dao;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import eu.pochet.domotix.Constants;
import eu.pochet.domotix.service.DownloadableFile;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.preference.PreferenceManager;
import android.util.JsonReader;
import android.util.Log;

/**
 * List of Level: http://www.pochet-lecomte.eu/www/levels.json List of lights:
 * http://www.pochet-lecomte.eu/www/lights.json List of cards:
 * http://www.pochet-lecomte.eu/www/cards.json
 * 
 * @author romuald
 * 
 */
public class DomotixDao {
	
	private static final String TAG = DomotixDao.class.getName();
	
	private static final String LEVELS_FILE_NAME = "levels.json";
	private static final String LIGHTS_FILE_NAME = "lights.json";
	private static final String CARDS_FILE_NAME = "cards.json";

	private static List<Level> levels = null;
	
	public static void update(Context ctx) {
		String server = PreferenceManager.getDefaultSharedPreferences(ctx).getString(Constants.DOMOTIX_DATA_HOST, Constants.DOMOTIX_DATA_HOST_DEFAULT);
		DownloadableFile[] downloadableFiles = new DownloadableFile[3];
		try {
			downloadableFiles[0] = new DownloadableFile(new URI(server + LEVELS_FILE_NAME), LEVELS_FILE_NAME);
			downloadableFiles[1] = new DownloadableFile(new URI(server + LIGHTS_FILE_NAME), LIGHTS_FILE_NAME);
			downloadableFiles[2] = new DownloadableFile(new URI(server + CARDS_FILE_NAME), CARDS_FILE_NAME);
		} catch (URISyntaxException e1) {
			return;
		}
		
		// create a buffer...
		byte[] buffer = new byte[1024];
		int bufferLength = 0; // used to store a temporary size of the buffer
		
		AndroidHttpClient client = null;
    	FileOutputStream fileOutput = null;
    	InputStream inputStream = null;
    	Long res = 0L;
    	DownloadableFile downloadableFile = null;
        int count = downloadableFiles.length;
        for (int i = 0; i < count; i++) 
        {
        	downloadableFile = downloadableFiles[i];
        	try 
        	{
				client = AndroidHttpClient.newInstance("Domotix");

		        HttpParams params = new BasicHttpParams();
		        HttpConnectionParams.setConnectionTimeout(params, 4000);
		        HttpConnectionParams.setSoTimeout(params, 4000);
		        
				HttpGet request = new HttpGet(downloadableFile.uri);
				HttpResponse response = client.execute(request);
				inputStream = response.getEntity().getContent();
				
				Log.d(TAG, "Getting file from " + downloadableFile.uri + " to " + downloadableFile.file);
				
				fileOutput = ctx.openFileOutput(downloadableFile.file, Context.MODE_PRIVATE);
				
				// now, read through the input buffer and write the contents to the file
				while ((bufferLength = inputStream.read(buffer)) > 0) {
					// add the data in the buffer to the file in the file output stream (the file on the sd card
					fileOutput.write(buffer, 0, bufferLength);
					res += bufferLength;
				}
				
				bufferLength = 0;
			} 
        	catch(Exception e) {
				e.printStackTrace();
			} 
        	finally 
        	{
				if(client != null) {
					client.close();
				}
				if(fileOutput != null) {
					try {fileOutput.close();} catch (IOException e) {e.printStackTrace();}
				}
			}
        }
	}

	public static List<Level> getLevels(Context ctx) {
		if ((levels == null) || (levels.size() == 0)) {
			levels = new ArrayList<Level>();
			try {
				JsonReader reader = new JsonReader(new InputStreamReader(
						ctx.openFileInput(LEVELS_FILE_NAME)));
				reader.setLenient(true);
				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();
					if (name.equals("levels")) {
						reader.beginArray();
						while (reader.hasNext()) {
							levels.add(readLevel(ctx, reader));
						}
						reader.endArray();
					} else {
						reader.skipValue();
					}
				}
				reader.endObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return levels;
	}

	public static List<Light> getLights(Context ctx) {
		List<Light> lights = new ArrayList<Light>();
		for(Level level : getLevels(ctx)) {
			lights.addAll(level.getLights());	
		}
		return lights;
	}
	
	public static void reset() {
		levels = null;
	}
	
	public static SwapPacket readSwapPacket(byte[] jsonData) throws IOException {
		SwapPacket swapPacket = null;
		JsonReader reader = new JsonReader(
				new BufferedReader(new InputStreamReader(
						new ByteArrayInputStream(jsonData))));
		reader.setLenient(true);
		reader.beginObject();
		while(reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("swapPacket")) {
				swapPacket = readSwapPacket(reader);
			} /*else if (name.equals("packetDevice")) {
				swapDevice = readSwapDevice(reader);
			} */else {
				reader.skipValue();
			}
		}
		reader.endObject();
	
		return swapPacket;
	}

	public static SwapDevice readSwapDevice(JsonReader reader) throws IOException {
		SwapDevice swapDevice = new SwapDevice();
		reader.beginObject();
		while(reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("product")) {
				swapDevice.setName(reader.nextString());
			} else if (name.equals("productCode")) {
				swapDevice.setProductCode(reader.nextString());
			} else if (name.equals("address")) {
				swapDevice.setAddress(reader.nextInt());
			} else if (name.equals("regularRegisters")) {
				reader.beginArray();
				swapDevice.setAddress(reader.nextInt());
				reader.endArray();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return swapDevice;
	}

	public static SwapPacket readSwapPacket(JsonReader reader) throws IOException {
		SwapPacket swapPacket = new SwapPacket();
		reader.beginObject();
		while(reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("dest")) {
				swapPacket.setDest(reader.nextInt());
			} else if (name.equals("source")) {
				swapPacket.setSource(reader.nextInt());
			} else if (name.equals("func")) {
				swapPacket.setFunc(reader.nextInt());
			} else if (name.equals("regId")) {
				swapPacket.setRegId(reader.nextInt());
			} else if (name.equals("regAddress")) {
				swapPacket.setRegAddress(reader.nextInt());
			} else if (name.equals("value")) {
				reader.beginArray();
				List<Byte> values = new ArrayList<Byte>();
				while (reader.hasNext()) {
					values.add((byte) reader.nextInt());
				}
				byte[] tmp = new byte[values.size()];
				int i = 0;
				for (byte b : values) {
					tmp[i++] = b;
				}
				swapPacket.setRegValue(tmp);
				reader.endArray();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return swapPacket;
	}

	/**
	 * 
	 * @param ctx
	 * @param levelId
	 * @return
	 */
	public static Level getLevel(Context ctx, int levelId) {
		for (Level level : getLevels(ctx)) {
			if (level.getId() == levelId) {
				return level;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param ctx
	 * @param lightId
	 * @return
	 */
	public static Light getLight(Context ctx, int lightId) {
		for (Level level : getLevels(ctx)) {
			for (Light light : level.getLights()) {
				if (light.getId() == lightId) {
					return light;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param ctx
	 * @param cardAdress
	 * @param outputNb
	 * @return
	 */
	public static Light getLight(Context ctx, int swapDeviceAdress, int outputNb) {
		for (Level level : getLevels(ctx)) {
			for (Light light : level.getLights()) {
				if (light.getSwapDeviceAddress() == swapDeviceAdress && light.getOutputNb() == outputNb) {
					return light;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param ctx
	 * @param address
	 * @return
	 */
	public static SwapDevice getSwapDevice(Context ctx, int address) {
		for (Level level : getLevels(ctx)) {
			for (SwapDevice swapDevice : level.getSwapDevices()) {
				if (swapDevice.getAddress() == address) {
					return swapDevice;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param ctx
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private static Level readLevel(Context ctx, JsonReader reader)
			throws IOException {
		Level level = new Level();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				level.setId(reader.nextInt());
			} else if (name.equals("level")) {
				level.setLevel(reader.nextInt());
			} else if (name.equals("name")) {
				level.setName(reader.nextString());
			} else if (name.equals("path")) {
				level.setPath(reader.nextString());
			} else if (name.equals("x")) {
				level.setX(reader.nextInt());
			} else if (name.equals("y")) {
				level.setY(reader.nextInt());
			} else if (name.equals("rooms")) {
				readRooms(ctx, reader, level);
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		readLights(ctx, level);
		readSwapDevices(ctx, level);
		return level;
	}

	/**
	 * 
	 * @param ctx
	 * @param level
	 * @return
	 * @throws IOException
	 */
	private static void readLights(Context ctx, Level level) throws IOException {
		Light light = null;
		List<Light> lights = new ArrayList<Light>();
		JsonReader reader = new JsonReader(new InputStreamReader(
				ctx.openFileInput(LIGHTS_FILE_NAME)));
		reader.setLenient(true);
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("lights")) {
				reader.beginArray();
				while (reader.hasNext()) {
					light = readLight(ctx, reader, level);
					if (light != null) {
						lights.add(light);
					}
				}
				reader.endArray();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		level.setLights(lights);
	}

	/**
	 * 
	 * @param ctx
	 * @param reader
	 * @param level
	 * @return
	 * @throws IOException
	 */
	private static Light readLight(Context ctx, JsonReader reader, Level level)
			throws IOException {
		int dx = 0;
		int dy = 0;
		boolean expectedLevel = false;
		Light light = new Light();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				light.setId(reader.nextInt());
			} else if (name.equals("name")) {
				light.setName(reader.nextString());
			} else if (name.equals("type")) {
				light.setType(reader.nextString());
			} else if (name.equals("cardAddress")) {
				light.setSwapDeviceAddress(reader.nextInt());
			} else if (name.equals("outputNb")) {
				light.setOutputNb(reader.nextInt());
			} else if (name.equals("x")) {
				light.setX(reader.nextInt());
			} else if (name.equals("dx")) {
				dx = reader.nextInt();
			} else if (name.equals("y")) {
				light.setY(reader.nextInt());
			} else if (name.equals("dy")) {
				dy = reader.nextInt();
			} else if (name.equals("z")) {
				light.setZ(reader.nextInt());
			} else if (name.equals("status")) {
				light.setStatus(reader.nextInt());
			} else if (name.equals("room_id")) {
				int lightRoomId = reader.nextInt();
				for (Room levelRoom : level.getRooms()) {
					if (levelRoom.getId() == lightRoomId) {
						light.setRoom(levelRoom);
						expectedLevel = true;
						break;
					}
				}
			} else {
				reader.skipValue();
			}
		}
		light.setX(light.getX() + dx);
		light.setY(light.getY() + dy);
		reader.endObject();
		return expectedLevel ? light : null;
	}

	/**
	 * 
	 * @param ctx
	 * @param level
	 * @return
	 * @throws IOException
	 */
	private static void readSwapDevices(Context ctx, Level level) throws IOException {
		SwapDevice swapDevice = null;
		List<SwapDevice> swapDevices = new ArrayList<SwapDevice>();
		JsonReader reader = new JsonReader(new InputStreamReader(
				ctx.openFileInput(CARDS_FILE_NAME)));
		reader.setLenient(true);
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("swapDevices")) {
				reader.beginArray();
				while (reader.hasNext()) {
					swapDevice = readSwapDevice(ctx, reader, level);
					if (swapDevice != null) {
						swapDevices.add(swapDevice);
					}
				}
				reader.endArray();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		level.setSwapDevices(swapDevices);
	}

	/**
	 * 
	 * @param ctx
	 * @param reader
	 * @param level
	 * @return
	 * @throws IOException
	 */
	private static SwapDevice readSwapDevice(Context ctx, JsonReader reader, Level level)
			throws IOException {
		int dx = 0;
		int dy = 0;
		boolean expectedSwapDevice = false;
		SwapDevice swapDevice = new SwapDevice();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				swapDevice.setId(reader.nextString());
			} else if (name.equals("product")) {
				swapDevice.setName(reader.nextString());
			} else if (name.equals("productCode")) {
				swapDevice.setProductCode(reader.nextString());
			} else if (name.equals("address")) {
				swapDevice.setAddress(reader.nextInt());
			} else if (name.equals("x")) {
				swapDevice.setX(reader.nextInt());
			} else if (name.equals("dx")) {
				dx = reader.nextInt();
			} else if (name.equals("y")) {
				swapDevice.setY(reader.nextInt());
			} else if (name.equals("dy")) {
				dy = reader.nextInt();
			} else if (name.equals("z")) {
				swapDevice.setZ(reader.nextInt());
			} else if (name.equals("room_id")) {
				int cardRoomId = reader.nextInt();
				for (Room levelRoom : level.getRooms()) {
					if (levelRoom.getId() == cardRoomId) {
						swapDevice.setRoom(levelRoom);
						expectedSwapDevice = true;
						break;
					}
				}
			} else {
				reader.skipValue();
			}
		}
		swapDevice.setX(swapDevice.getX() + dx);
		swapDevice.setY(swapDevice.getY() + dy);
		reader.endObject();
		return expectedSwapDevice ? swapDevice : null;
	}

	/**
	 * 
	 * @param ctx
	 * @param reader
	 * @param level
	 * @return
	 * @throws IOException
	 */
	private static void readRooms(Context ctx, JsonReader reader, Level level)
			throws IOException {
		List<Room> rooms = new ArrayList<Room>();
		reader.beginArray();
		while (reader.hasNext()) {
			rooms.add(readRoom(ctx, reader, level));
		}
		reader.endArray();
		level.setRooms(rooms);
	}

	/**
	 * 
	 * @param ctx
	 * @param reader
	 * @param level
	 * @return
	 * @throws IOException
	 */
	private static Room readRoom(Context ctx, JsonReader reader, Level level)
			throws IOException {
		Room room = new Room();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				room.setId(reader.nextInt());
			} else if (name.equals("name")) {
				room.setName(reader.nextString());
			} else if (name.equals("path")) {
				room.setPath(reader.nextString());
			} else if (name.equals("x")) {
				room.setX(reader.nextInt());
			} else if (name.equals("y")) {
				room.setY(reader.nextInt());
			} else {
				reader.skipValue();
			}
		}
		room.setLevel(level);
		reader.endObject();
		return room;
	}

}
