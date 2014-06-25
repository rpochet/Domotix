package eu.pochet.domotix.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.JsonReader;
import eu.pochet.domotix.service.DownloadFilesTask;
import eu.pochet.domotix.service.DownloadableFile;

/**
 * List of Level: http://www.pochet-lecomte.eu/www/levels.json
 * List of Room by Level: http://www.pochet-lecomte.eu/www/levels/{1,4}.json
 * List of Light by Level: http://www.pochet-lecomte.eu/www/levels/{1,4}/lights.json
 * 
 * @author romuald
 *
 */
public class LevelDao 
{
	private static final String LEVELS_FILE_NAME = "levels.json";
	private static final String ROOMS_FILE_NAME = "levels_{0}.json";
	private static final String LIGHTS_FILE_NAME = "lights.json";
	private static final String CARDS_FILE_NAME = "cards.json";
	
	private static List<Level> levels = null; 
	
    public static List<Level> getLevels(Context ctx) 
	{		
		if((levels == null) || (levels.size() == 0)) 
		{
			levels = new ArrayList<Level>();
			try 
			{
				JsonReader reader = new JsonReader(new InputStreamReader(ctx.openFileInput(LEVELS_FILE_NAME.replace("/", "_"))));
				reader.setLenient(true);
				reader.beginObject();
				while (reader.hasNext()) 
				{
					String name = reader.nextName();
		     		if (name.equals("levels")) 
		     		{
		     			reader.beginArray();
						while (reader.hasNext()) 
						{
							levels.add(readLevel(ctx, reader));
						}
		     			reader.endArray();
		     		} 
		     		else 
		     		{
		     			reader.skipValue();
		     		}
				}
				reader.endObject();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	    return levels;
	}
	
	public static void reset() 
	{
		levels = null;
	}
	
	public static boolean updateFiles(String server, Context ctx, final Runnable postExecute) 
	{
		int j = 0;
		DownloadableFile[] downloadableFiles = new DownloadableFile[7];
		try 
		{
			downloadableFiles[j++] = new DownloadableFile(
				new URI(server + LEVELS_FILE_NAME),
				LEVELS_FILE_NAME
			);
			
			downloadableFiles[j++] = new DownloadableFile(
				new URI(server + LIGHTS_FILE_NAME),
				LIGHTS_FILE_NAME
			);
			
			downloadableFiles[j++] = new DownloadableFile(
				new URI(server + CARDS_FILE_NAME),
				CARDS_FILE_NAME
			);
			
			for(int i = 1; i <= 4; i++) 
			{
				downloadableFiles[j++] = new DownloadableFile(
					new URI(server + ROOMS_FILE_NAME.replace("{0}", Integer.toString(i))),
					ROOMS_FILE_NAME.replace("/", "_").replace("{0}", Integer.toString(i))
				);
			}
			new DownloadFilesTask(ctx) 
			{		
				@Override
				protected void onPostExecute(Long result)
				{
				    super.onPostExecute(result);
			    	LevelDao.reset();
			    	postExecute.run();
				}
			}.execute(downloadableFiles);
		} 
		catch (URISyntaxException e) 
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param ctx
	 * @param levelId
	 * @return
	 */
	public static Level getLevel(Context ctx, int levelId) 
	{
		for (Level level : getLevels(ctx)) 
		{
			if(level.getId() == levelId) 
			{
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
	public static Light getLight(Context ctx, int lightId) 
	{
		for(Level level : getLevels(ctx)) 
		{
			for(Light light : level.getLights()) 
			{
				if(light.getId() == lightId) 
				{
					return light;
				}
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
	public static Card getCard(Context ctx, int cardId) 
	{
		for(Level level : getLevels(ctx)) 
		{
			for(Card card : level.getCards()) 
			{
				if(card.getId() == cardId) 
				{
					return card;
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
    private static Level readLevel(Context ctx, JsonReader reader) throws IOException 
    {
    	Level level = new Level();
     	reader.beginObject();
     	while (reader.hasNext()) 
     	{
     		String name = reader.nextName();
     		if (name.equals("id")) 
     		{
     			level.setId(reader.nextInt());
     		} 
     		else if (name.equals("name")) 
     		{
     			level.setName(reader.nextString());
     		} 
     		else if (name.equals("path"))
     		{
     			level.setPath(reader.nextString());
     		}  
     		else if (name.equals("x")) 
     		{
     			level.setX(reader.nextInt());
     		} 
     		else if (name.equals("y"))
     		{
     			level.setY(reader.nextInt());
     		} 
     		else 
     		{
     			reader.skipValue();
     		}
     	}
     	reader.endObject();
     	readRooms(ctx, level);
     	readLights(ctx, level);
     	readCards(ctx, level);
     	return level;
	}

    /**
     * 
     * @param ctx
     * @param level
     * @return
     * @throws IOException
     */
    private static void readLights(Context ctx, Level level) throws IOException 
    {
    	Light light = null;
    	List<Light> lights = new ArrayList<Light>();
    	JsonReader reader = new JsonReader(new InputStreamReader(ctx.openFileInput(LIGHTS_FILE_NAME)));
		reader.setLenient(true);
		reader.beginObject();
		while (reader.hasNext()) 
		{
			String name = reader.nextName();
     		if (name.equals("lights")) 
     		{
     			reader.beginArray();
				while (reader.hasNext()) 
				{
					light = readLight(ctx, reader, level);
					if(light != null)
					{
						lights.add(light);	
					}
				}
     			reader.endArray();
     		} 
     		else 
     		{
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
    private static Light readLight(Context ctx, JsonReader reader, Level level) throws IOException 
	{
		boolean expectedLevel = false;
    	Light light = new Light();
     	reader.beginObject();
     	while (reader.hasNext()) 
     	{
     		String name = reader.nextName();
     		if (name.equals("id")) 
     		{
     			light.setId(reader.nextInt());
     		} 
     		else if (name.equals("name")) 
     		{
     			light.setName(reader.nextString());
     		} 
     		else if (name.equals("type")) 
     		{
     			light.setType(reader.nextString());
     		} 
     		else if (name.equals("cardAddress")) 
     		{
     			light.setCardAddress(reader.nextString());
     		}
     		else if (name.equals("outputNb")) 
     		{
     			light.setOutputNb(reader.nextString());
     		} 
     		else if (name.equals("x")) 
     		{
     			light.setX(reader.nextInt());
     		} 
     		else if (name.equals("y")) 
     		{
     			light.setY(reader.nextInt());
     		} 
     		else if (name.equals("z")) 
     		{
     			light.setZ(reader.nextInt());
     		}  
     		else if (name.equals("status")) 
     		{
     			light.setStatus(reader.nextString());
     		} 
     		else if (name.equals("room_id")) 
     		{
     			int lightRoomId = reader.nextInt();
     			for (Room levelRoom : level.getRooms()) 
     			{
					if(levelRoom.getId() == lightRoomId) 
					{
		     			light.setRoom(levelRoom);
		     			expectedLevel = true;
		     			break;
					}
				}
     		} 
     		else if (name.equals("level_id")) 
     		{
     			int lightLevelId = reader.nextInt();
     			if(lightLevelId == level.getId())
     			{
     				expectedLevel = true;
     			}
     		} 
     		else 
     		{
     			reader.skipValue();
     		}
     	}
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
    private static void readCards(Context ctx, Level level) throws IOException 
    {
    	Card card = null;
    	List<Card> cards = new ArrayList<Card>();
    	JsonReader reader = new JsonReader(new InputStreamReader(ctx.openFileInput(CARDS_FILE_NAME.replace("/", "_").replace("{0}", Integer.toString(level.getId())))));
		reader.setLenient(true);
		reader.beginObject();
		while (reader.hasNext()) 
		{
			String name = reader.nextName();
     		if (name.equals("cards")) 
     		{
     			reader.beginArray();
				while (reader.hasNext()) 
				{
					card = readCard(ctx, reader, level);
					if(card != null)
					{
						cards.add(card);	
					}
				}
     			reader.endArray();
     		} 
     		else 
     		{
     			reader.skipValue();
     		}
		}
		reader.endObject();
		level.setCards(cards);
    }
    
    /**
     * 
     * @param ctx
     * @param reader
     * @param level
     * @return
     * @throws IOException
     */
    private static Card readCard(Context ctx, JsonReader reader, Level level) throws IOException 
	{
		boolean expectedCard = false;
		Card card = new Card();
     	reader.beginObject();
     	while (reader.hasNext()) 
     	{
     		String name = reader.nextName();
     		if (name.equals("id")) 
     		{
     			card.setId(reader.nextInt());
     		} 
     		else if (name.equals("name")) 
     		{
     			card.setName(reader.nextString());
     		} 
     		else if (name.equals("type")) 
     		{
     			card.setType(reader.nextString());
     		} 
     		else if (name.equals("cardAddress")) 
     		{
     			card.setCardAddress(reader.nextString());
     		}
     		else if (name.equals("x")) 
     		{
     			card.setX(reader.nextInt());
     		} 
     		else if (name.equals("y")) 
     		{
     			card.setY(reader.nextInt());
     		} 
     		else if (name.equals("z")) 
     		{
     			card.setZ(reader.nextInt());
     		} 
     		else if (name.equals("room_id")) 
     		{
     			int cardRoomId = reader.nextInt();
     			for (Room levelRoom : level.getRooms()) 
     			{
					if(levelRoom.getId() == cardRoomId) 
					{
		     			card.setRoom(levelRoom);
		     			break;
					}
				}
     		} 
     		else if (name.equals("level_id")) 
     		{
     			int cardLevelId = reader.nextInt();
     			if(cardLevelId == level.getId())
     			{
     				expectedCard = true;
     			}
     		} 
     		else 
     		{
     			reader.skipValue();
     		}
     	}
     	reader.endObject();    	
    	return expectedCard ? card : null;
    }

    /**
     * 
     * @param ctx
     * @param level
     * @return
     * @throws IOException
     */
    private static void readRooms(Context ctx, Level level) throws IOException 
    {
    	List<Room> rooms = new ArrayList<Room>();
    	JsonReader reader = new JsonReader(new InputStreamReader(ctx.openFileInput(ROOMS_FILE_NAME.replace("/", "_").replace("{0}", Integer.toString(level.getId())))));
		reader.setLenient(true);
		reader.beginObject();
		while (reader.hasNext()) 
		{
			String name = reader.nextName();
     		if (name.equals("rooms")) 
     		{
     			reader.beginArray();
				while (reader.hasNext()) 
				{
					rooms.add(readRoom(ctx, reader, level));
				}
     			reader.endArray();
     		} 
     		else 
     		{
     			reader.skipValue();
     		}
		}
		reader.endObject();
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
    private static Room readRoom(Context ctx, JsonReader reader, Level level) throws IOException 
	{
		Room room = new Room();
     	reader.beginObject();
     	while (reader.hasNext()) 
     	{
     		String name = reader.nextName();
     		if (name.equals("id")) 
     		{
     			room.setId(reader.nextInt());
     		} 
     		else if (name.equals("name")) 
     		{
     			room.setName(reader.nextString());
     		} 
     		else if (name.equals("path")) 
     		{
     			room.setPath(reader.nextString());
     		}  
     		else if (name.equals("x")) 
     		{
     			room.setX(reader.nextInt());
     		} 
     		else if (name.equals("y"))
     		{
     			room.setY(reader.nextInt());
     		} 
     		else 
     		{
     			reader.skipValue();
     		}
     	}
     	room.setLevel(level);
     	reader.endObject();    	
    	return room;
    }

}
