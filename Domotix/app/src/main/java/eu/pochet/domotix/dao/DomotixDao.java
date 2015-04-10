package eu.pochet.domotix.dao;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.preference.PreferenceManager;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import eu.pochet.domotix.Constants;
import eu.pochet.domotix.service.DownloadableFile;

/**
 * 
 * @author romuald
 * 
 */
public class DomotixDao {
	
	private static final String TAG = DomotixDao.class.getName();
	
	// http://192.168.1.4:5984/panstamp/_design/devices/_view/levels
	// http://192.168.1.4:5984/panstamp/_design/devices/_view/lights
	// http://192.168.1.4:5984/panstamp/_design/devices/_view/devices
	
	private static final String LEVELS_FILE_NAME = "levels";
	private static final String LIGHTS_FILE_NAME = "lights";
	private static final String SWAP_DEVICES_FILE_NAME = "devices";

	private static List<Level> levels = null;

	private static List<Light> lights = null;
	
	private static List<SwapDevice> swapDevices = null;
	
	public static void update(Context ctx) {
		String server = PreferenceManager.getDefaultSharedPreferences(ctx).getString(Constants.DOMOTIX_DATA_HOST, Constants.DOMOTIX_DATA_HOST_DEFAULT);
		DownloadableFile[] downloadableFiles = new DownloadableFile[3];
		try {
			downloadableFiles[0] = new DownloadableFile(new URI(server + LEVELS_FILE_NAME), LEVELS_FILE_NAME);
			downloadableFiles[1] = new DownloadableFile(new URI(server + LIGHTS_FILE_NAME), LIGHTS_FILE_NAME);
			downloadableFiles[2] = new DownloadableFile(new URI(server + SWAP_DEVICES_FILE_NAME), SWAP_DEVICES_FILE_NAME);
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
        reset();
	}

    /**
     *
     * @param ctx
     * @return List of Levels
     */
	public static List<Level> getLevels(Context ctx) {
		if ((levels == null) || (levels.size() == 0)) {
			levels = new ArrayList<Level>();
			try {
				readLevels(ctx);
				
				readLights(ctx);
				Collections.sort(lights, new Comparator<Light>() {
					public int compare(Light l1, Light l2) {
						return Integer.valueOf(l1.getId()).compareTo(Integer.valueOf(l2.getId()));
					}
				});
				
				readSwapDevices(ctx);
				Collections.sort(swapDevices, new Comparator<SwapDevice>() {
					public int compare(SwapDevice d1, SwapDevice d2) {
						return Integer.valueOf(d1.getAddress()).compareTo(Integer.valueOf(d2.getAddress()));
					}
				});
				
				for (Level level : levels) {
					for (Room room : level.getRooms()) {
						for (Light light : lights) {
							if(light.getLocation().getRoom_id() == room.getId()) {
								light.getLocation().setRoom(room);
								room.addLight(light);
							}
						}
						for (SwapDevice swapDevice : swapDevices) {
							if(swapDevice.getLocation().getRoom_id() == room.getId()) {
								swapDevice.getLocation().setRoom(room);
								room.addSwapDevice(swapDevice);
							}
						}
					}
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (JsonIOException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return levels;
	}
	
	private static void readLevels(Context ctx) throws IOException {
		levels = new ArrayList<Level>();
		JsonReader jsonReader = new JsonReader(new InputStreamReader(ctx.openFileInput(LEVELS_FILE_NAME)));
		jsonReader.setLenient(true);
		jsonReader.beginObject();
		String name = null;
		while(jsonReader.hasNext()) {
			name = jsonReader.nextName(); 
			if (name.equals("rows")) {
				jsonReader.beginArray();
				while(jsonReader.hasNext()) {
					jsonReader.beginObject();
					while(jsonReader.hasNext()) {
						String name2 = jsonReader.nextName();
						if (name2.equals("value")) {
							levels.add(readLevel(ctx, jsonReader));
						} else {
							jsonReader.skipValue();
						}
					}
					jsonReader.endObject();
				}
				jsonReader.endArray();
			} else {
				jsonReader.skipValue();
			}
		}
	}

	/**
	 * 
	 * @param ctx
	 * @param reader
	 * @return Level
	 * @throws IOException
	 */
	private static Level readLevel(Context ctx, JsonReader reader) throws IOException {
		Level level = new Level();
		reader.beginObject();
		String name = null;
		while (reader.hasNext()) {
			name = reader.nextName();
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
		return level;
	}
	
	public static List<Light> getLights(Context ctx) {
		return lights;
	}
	
	public static void reset() {
		levels = null;
	}
	
	/*private static Room getRoomById(int roomId) {
		for (Level level : levels) {
			for (Room room : level.getRooms()) {
				if(room.getId() == roomId) {
					return room;					
				}
			}
		}
		return null;
	}*/

    /**
     * dest-source-HOP|NONCE-function-regAddress-regId-regValue
     * @param rawData
     * @return
     * @throws IOException
     */
	public static SwapPacket readSwapPacket(byte[] rawData) throws IOException {
        SwapPacket swapPacket = new SwapPacket();
        swapPacket.setDest(rawData[0]);
        swapPacket.setSource(rawData[1]);
        swapPacket.setFunc(rawData[3]);
        swapPacket.setRegAddress(rawData[4]);
        swapPacket.setRegId(rawData[5]);
        swapPacket.setRegValue(Arrays.copyOfRange(rawData, 6, rawData.length - 1));
        return swapPacket;
	}
	
	public static SwapPacket readSwapPacket(byte[] jsonData, int offset) throws IOException {
		SwapPacket swapPacket = null;
		JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(jsonData, offset, jsonData.length))));
		reader.setLenient(true);
		reader.beginObject();
		String name = null;
		while(reader.hasNext()) {
			name = reader.nextName();
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

    public static SwapPacket readSwapPacket(JsonReader reader) throws IOException {
        SwapPacket swapPacket = new SwapPacket();
        reader.beginObject();
        String name = null;
        while(reader.hasNext()) {
            name = reader.nextName();
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
            } else if (name.equals("time")) {
                swapPacket.setTime(readDate(reader.nextString()));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return swapPacket;
    }

    /**
     *
     * @param sDate
     * @return
     * @throws IOException
     */
    public static Date readDate(String sDate) throws IOException {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss").parse(sDate);
        } catch (ParseException e) {
            throw new IOException("Unable to parse date", e);
        }
    }

    /**
     *
     * @param jsonData
     * @return
     * @throws IOException
     */
	public static List<Integer> readOutputs(byte[] jsonData) throws IOException {
		List<Integer> outputs = new ArrayList<Integer>();
		JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(jsonData))));
		reader.setLenient(true);
		reader.beginObject();
		String name = null;
		while(reader.hasNext()) {
			name = reader.nextName();
			if (name.equals("rows")) {
				reader.beginArray();
				while(reader.hasNext()) {
					reader.beginObject();
					while(reader.hasNext()) {
						String name2 = reader.nextName();
						if (name2.equals("value")) {
							reader.beginObject();
							while(reader.hasNext()) {
								String name3 = reader.nextName();
								if (name3.equals("endpoints")) {
									reader.beginArray();
									while(reader.hasNext()) {
										reader.beginObject();
										while(reader.hasNext()) {
											String name4 = reader.nextName();
											if (name4.equals("value")) {
												outputs.add(reader.nextInt());
											} else {
												reader.skipValue();
											}
										}
										reader.endObject();
									}
									reader.endArray();
								} else {
									reader.skipValue();
								}
							}
							reader.endObject();
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				}
				reader.endArray();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
	
		return outputs;
	}

	/**
	 * 
	 * @param ctx
	 * @param levelId
	 * @return Level
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
	 * @return Light
	 */
	public static Light getLight(Context ctx, int lightId) {
		for (Level level : getLevels(ctx)) {
			for(Room room : level.getRooms()) {
				for (Light light : room.getLights()) {
					if (light.getId() == lightId) {
						return light;
					}
				}	
			}
		}
		return null;
	}

	/**
	 * 
	 * @param ctx
	 * @param address
	 * @return SwapDevice
	 */
	public static SwapDevice getSwapDevice(Context ctx, int address) {
        if(swapDevices == null) {
            try {
                readSwapDevices(ctx);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to read SWAP devices", e);
            }
        }
		for (SwapDevice swapDevice : swapDevices) {
			if (swapDevice.getAddress() == address) {
				return swapDevice;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param ctx
	 * @param reader
	 * @param level
	 * @return
	 * @throws IOException
	 */
	private static void readRooms(Context ctx, JsonReader reader, Level level) throws IOException {
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
	private static Room readRoom(Context ctx, JsonReader reader, Level level) throws IOException {
		Room room = new Room();
		reader.beginObject();
		String name = null;
		while (reader.hasNext()) {
			name = reader.nextName();
			if (name.equals("id")) {
				room.setId(reader.nextInt());
			} else if (name.equals("name")) {
				room.setName(reader.nextString());
			} else if (name.equals("path")) {
				room.setPath(reader.nextString());
			} else if (name.equals("location")) {
				room.setLocation(readLocation(ctx, reader));
			} else {
				reader.skipValue();
			}
		}
		room.setLevel(level);
		reader.endObject();
		return room;
	}

	/**
	 * 
	 * @param ctx
	 * @throws IOException
	 */
	private static void readLights(Context ctx) throws IOException {
		Light light = null;
		lights = new ArrayList<Light>();
		JsonReader reader = new JsonReader(new InputStreamReader(ctx.openFileInput(LIGHTS_FILE_NAME)));
		reader.setLenient(true);
		reader.beginObject();
		String name = null;
		while (reader.hasNext()) {
			name = reader.nextName();
			if (name.equals("rows")) {
				reader.beginArray();
				while(reader.hasNext()) {
					reader.beginObject();
					while(reader.hasNext()) {
						String name2 = reader.nextName();
						if (name2.equals("value")) {
							light = readLight(ctx, reader);
							if (light != null) {
								lights.add(light);
							}
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				}
				reader.endArray();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
	}

	/**
	 * 
	 * @param ctx
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private static Light readLight(Context ctx, JsonReader reader) throws IOException {
		Light light = new Light();
		reader.beginObject();
		String name = null;
		while (reader.hasNext()) {
			name = reader.nextName();
			if (name.equals("id")) {
				light.setId(reader.nextInt());
			} else if (name.equals("name")) {
				light.setName(reader.nextString());
			} else if (name.equals("type")) {
				light.setType(reader.nextString());
			} else if (name.equals("swapDeviceAddress")) {
				light.setSwapDeviceAddress(reader.nextInt());
			} else if (name.equals("outputNb")) {
				light.setOutputNb(reader.nextInt());
			} else if (name.equals("location")) {
				light.setLocation(readLocation(ctx, reader));
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return light;
	}

	private static Location readLocation(Context ctx, JsonReader reader) throws IOException {
		Location location = new Location();
		reader.beginObject();
		String name = null;
		while (reader.hasNext()) {
			name = reader.nextName();
			if (name.equals("x")) {
				location.setX(reader.nextInt());
			} else if (name.equals("dx")) {
				location.setDx(reader.nextInt());
			} else if (name.equals("y")) {
				location.setY(reader.nextInt());
			} else if (name.equals("dy")) {
				location.setDy(reader.nextInt());
			} else if (name.equals("z")) {
				location.setZ(reader.nextInt());
			} else if (name.equals("room_id")) {
				location.setRoom_id(reader.nextInt());
			}  else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return location;
	}
	
	/**
	 * 
	 * @param ctx
	 * @return
	 * @throws IOException
	 */
	private static void readSwapDevices(Context ctx) throws IOException {
		SwapDevice swapDevice = null;
		swapDevices = new ArrayList<SwapDevice>();
		JsonReader reader = new JsonReader(new InputStreamReader(ctx.openFileInput(SWAP_DEVICES_FILE_NAME)));
		reader.setLenient(true);
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("rows")) {
				reader.beginArray();
				while(reader.hasNext()) {
					reader.beginObject();
					while(reader.hasNext()) {
						String name2 = reader.nextName();
						if (name2.equals("value")) {
							swapDevice = readSwapDevice(ctx, reader);
							if (swapDevice != null) {
								swapDevices.add(swapDevice);
							}
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				}
				reader.endArray();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
	}

	/**
	 * 
	 * @param ctx
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private static SwapDevice readSwapDevice(Context ctx, JsonReader reader) throws IOException {
		SwapDevice swapDevice = new SwapDevice();
		reader.beginObject();
		String name = null;
		while(reader.hasNext()) {
			name = reader.nextName();
			if (name.equals("product")) {
				swapDevice.setProduct(reader.nextString());
			} else if (name.equals("address")) {
				swapDevice.setAddress(reader.nextInt());
			} else if (name.equals("location")) {
				swapDevice.setLocation(readLocation(ctx, reader));
			} else if (name.equals("regularRegisters")) {
				reader.beginArray();
				while (reader.hasNext()) {
					swapDevice.addSwapResister(readSwapRegister(ctx, reader));
				}
				reader.endArray();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return swapDevice;
	}
	
	/**
	 * 
	 * @param ctx
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private static SwapRegister readSwapRegister(Context ctx, JsonReader reader) throws IOException {
		SwapRegister swapRegister = new SwapRegister();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("name")) {
				swapRegister.setName(reader.nextString());
			} else if (name.equals("id")) {
				swapRegister.setId(reader.nextInt());
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
				swapRegister.setValue(tmp);
				reader.endArray();
			} else if (name.equals("endpoints")) {
				reader.beginArray();
				while (reader.hasNext()) {
					swapRegister.addSwapResisterEndpoint(readSwapRegisterEndpoint(ctx, reader));
				}
				reader.endArray();
			} else {
				reader.skipValue();
			}
		}
		if(swapRegister.getValue() == null) {
			int len = 0;
			for (SwapRegisterEndpoint swapRegisterEndpoint : swapRegister.getSwapRegisterEndpoints()) {
				len += Integer.parseInt(swapRegisterEndpoint.getSize());
			}
			swapRegister.setValue(new byte[len]);
		}
		reader.endObject();
		return swapRegister;
	}
	
	/**
	 * 
	 * @param ctx
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private static SwapRegisterEndpoint readSwapRegisterEndpoint(Context ctx, JsonReader reader) throws IOException {
		SwapRegisterEndpoint swapRegisterEndpoint = new SwapRegisterEndpoint();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("name")) {
				swapRegisterEndpoint.setName(reader.nextString());
			} else if (name.equals("position")) {
				swapRegisterEndpoint.setPosition(reader.nextString());
			} else if (name.equals("size")) {
				swapRegisterEndpoint.setSize(reader.nextString());
			} else if (name.equals("type")) {
				swapRegisterEndpoint.setType(reader.nextString());
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return swapRegisterEndpoint;
	}

}
