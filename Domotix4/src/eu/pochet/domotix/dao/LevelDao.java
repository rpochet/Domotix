// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.dao;

import android.content.Context;
import android.util.JsonReader;
import eu.pochet.domotix.service.DownloadFilesTask;
import eu.pochet.domotix.service.DownloadableFile;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package eu.pochet.domotix.dao:
//            Level, Card, Light, Room

public class LevelDao
{

    private static final String CARDS_FILE_NAME = "cards.json";
    private static final String LEVELS_FILE_NAME = "levels.json";
    private static final String LIGHTS_FILE_NAME = "lights.json";
    private static final String ROOMS_FILE_NAME = "levels_{0}.json";
    private static List levels = null;

    public LevelDao()
    {
    }

    public static Card getCard(Context context, int i)
    {
        Iterator iterator = getLevels(context).iterator();
        Card card;
label0:
        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }
            Iterator iterator1 = ((Level)iterator.next()).getCards().iterator();
            do
            {
                if (!iterator1.hasNext())
                {
                    continue label0;
                }
                card = (Card)iterator1.next();
            } while (card.getId() != i);
            break;
        } while (true);
        return card;
    }

    public static Level getLevel(Context context, int i)
    {
        Iterator iterator = getLevels(context).iterator();
        Level level;
        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }
            level = (Level)iterator.next();
        } while (level.getId() != i);
        return level;
    }

    public static List getLevels(Context context)
    {
        if (levels != null && levels.size() != 0) goto _L2; else goto _L1
_L1:
        levels = new ArrayList();
        JsonReader jsonreader;
        jsonreader = new JsonReader(new InputStreamReader(context.openFileInput("levels.json".replace("/", "_"))));
        jsonreader.setLenient(true);
        jsonreader.beginObject();
_L5:
        if (jsonreader.hasNext()) goto _L4; else goto _L3
_L3:
        jsonreader.endObject();
_L2:
        return levels;
_L4:
        if (!jsonreader.nextName().equals("levels"))
        {
            break MISSING_BLOCK_LABEL_134;
        }
        jsonreader.beginArray();
_L6:
        if (jsonreader.hasNext())
        {
            break MISSING_BLOCK_LABEL_117;
        }
        jsonreader.endArray();
          goto _L5
        Exception exception;
        exception;
        exception.printStackTrace();
          goto _L2
        levels.add(readLevel(context, jsonreader));
          goto _L6
        jsonreader.skipValue();
          goto _L5
    }

    public static Light getLight(Context context, int i)
    {
        Iterator iterator = getLevels(context).iterator();
        Light light;
label0:
        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }
            Iterator iterator1 = ((Level)iterator.next()).getLights().iterator();
            do
            {
                if (!iterator1.hasNext())
                {
                    continue label0;
                }
                light = (Light)iterator1.next();
            } while (light.getId() != i);
            break;
        } while (true);
        return light;
    }

    private static Card readCard(Context context, JsonReader jsonreader, Level level)
        throws IOException
    {
        boolean flag;
        Card card;
        flag = false;
        card = new Card();
        jsonreader.beginObject();
_L2:
        do
        {
            if (!jsonreader.hasNext())
            {
                jsonreader.endObject();
                String s;
                int i;
                Iterator iterator;
                Room room;
                if (flag)
                {
                    return card;
                } else
                {
                    return null;
                }
            }
            s = jsonreader.nextName();
            if (s.equals("id"))
            {
                card.setId(jsonreader.nextInt());
            } else
            if (s.equals("name"))
            {
                card.setName(jsonreader.nextString());
            } else
            if (s.equals("type"))
            {
                card.setType(jsonreader.nextString());
            } else
            if (s.equals("cardAddress"))
            {
                card.setCardAddress(jsonreader.nextString());
            } else
            if (s.equals("x"))
            {
                card.setX(jsonreader.nextInt());
            } else
            if (s.equals("y"))
            {
                card.setY(jsonreader.nextInt());
            } else
            {
label0:
                {
                    if (!s.equals("z"))
                    {
                        break label0;
                    }
                    card.setZ(jsonreader.nextInt());
                }
            }
        } while (true);
        if (!s.equals("room_id"))
        {
            break MISSING_BLOCK_LABEL_262;
        }
        i = jsonreader.nextInt();
        iterator = level.getRooms().iterator();
_L4:
        if (!iterator.hasNext()) goto _L2; else goto _L1
_L1:
        room = (Room)iterator.next();
        if (room.getId() != i) goto _L4; else goto _L3
_L3:
        card.setRoom(room);
          goto _L2
        if (s.equals("level_id"))
        {
            if (jsonreader.nextInt() == level.getId())
            {
                flag = true;
            }
        } else
        {
            jsonreader.skipValue();
        }
          goto _L2
    }

    private static void readCards(Context context, Level level)
        throws IOException
    {
        ArrayList arraylist = new ArrayList();
        JsonReader jsonreader = new JsonReader(new InputStreamReader(context.openFileInput("cards.json".replace("/", "_").replace("{0}", Integer.toString(level.getId())))));
        jsonreader.setLenient(true);
        jsonreader.beginObject();
label0:
        do
        {
            if (!jsonreader.hasNext())
            {
                jsonreader.endObject();
                level.setCards(arraylist);
                return;
            }
            if (jsonreader.nextName().equals("cards"))
            {
                jsonreader.beginArray();
                do
                {
                    if (!jsonreader.hasNext())
                    {
                        jsonreader.endArray();
                        continue label0;
                    }
                    Card card = readCard(context, jsonreader, level);
                    if (card != null)
                    {
                        arraylist.add(card);
                    }
                } while (true);
            }
            jsonreader.skipValue();
        } while (true);
    }

    private static Level readLevel(Context context, JsonReader jsonreader)
        throws IOException
    {
        Level level = new Level();
        jsonreader.beginObject();
        do
        {
            if (!jsonreader.hasNext())
            {
                jsonreader.endObject();
                readRooms(context, level);
                readLights(context, level);
                readCards(context, level);
                return level;
            }
            String s = jsonreader.nextName();
            if (s.equals("id"))
            {
                level.setId(jsonreader.nextInt());
            } else
            if (s.equals("name"))
            {
                level.setName(jsonreader.nextString());
            } else
            if (s.equals("path"))
            {
                level.setPath(jsonreader.nextString());
            } else
            if (s.equals("x"))
            {
                level.setX(jsonreader.nextInt());
            } else
            if (s.equals("y"))
            {
                level.setY(jsonreader.nextInt());
            } else
            {
                jsonreader.skipValue();
            }
        } while (true);
    }

    private static Light readLight(Context context, JsonReader jsonreader, Level level)
        throws IOException
    {
        boolean flag;
        Light light;
        flag = false;
        light = new Light();
        jsonreader.beginObject();
_L2:
        do
        {
            if (!jsonreader.hasNext())
            {
                jsonreader.endObject();
                String s;
                int i;
                Iterator iterator;
                Room room;
                if (flag)
                {
                    return light;
                } else
                {
                    return null;
                }
            }
            s = jsonreader.nextName();
            if (s.equals("id"))
            {
                light.setId(jsonreader.nextInt());
            } else
            if (s.equals("name"))
            {
                light.setName(jsonreader.nextString());
            } else
            if (s.equals("type"))
            {
                light.setType(jsonreader.nextString());
            } else
            if (s.equals("cardAddress"))
            {
                light.setCardAddress(jsonreader.nextString());
            } else
            if (s.equals("outputNb"))
            {
                light.setOutputNb(jsonreader.nextString());
            } else
            if (s.equals("x"))
            {
                light.setX(jsonreader.nextInt());
            } else
            if (s.equals("y"))
            {
                light.setY(jsonreader.nextInt());
            } else
            if (s.equals("z"))
            {
                light.setZ(jsonreader.nextInt());
            } else
            {
label0:
                {
                    if (!s.equals("status"))
                    {
                        break label0;
                    }
                    light.setStatus(jsonreader.nextString());
                }
            }
        } while (true);
        if (!s.equals("room_id"))
        {
            break MISSING_BLOCK_LABEL_309;
        }
        i = jsonreader.nextInt();
        iterator = level.getRooms().iterator();
_L4:
        if (!iterator.hasNext()) goto _L2; else goto _L1
_L1:
        room = (Room)iterator.next();
        if (room.getId() != i) goto _L4; else goto _L3
_L3:
        light.setRoom(room);
        flag = true;
          goto _L2
        if (s.equals("level_id"))
        {
            if (jsonreader.nextInt() == level.getId())
            {
                flag = true;
            }
        } else
        {
            jsonreader.skipValue();
        }
          goto _L2
    }

    private static void readLights(Context context, Level level)
        throws IOException
    {
        ArrayList arraylist = new ArrayList();
        JsonReader jsonreader = new JsonReader(new InputStreamReader(context.openFileInput("lights.json")));
        jsonreader.setLenient(true);
        jsonreader.beginObject();
label0:
        do
        {
            if (!jsonreader.hasNext())
            {
                jsonreader.endObject();
                level.setLights(arraylist);
                return;
            }
            if (jsonreader.nextName().equals("lights"))
            {
                jsonreader.beginArray();
                do
                {
                    if (!jsonreader.hasNext())
                    {
                        jsonreader.endArray();
                        continue label0;
                    }
                    Light light = readLight(context, jsonreader, level);
                    if (light != null)
                    {
                        arraylist.add(light);
                    }
                } while (true);
            }
            jsonreader.skipValue();
        } while (true);
    }

    private static Room readRoom(Context context, JsonReader jsonreader, Level level)
        throws IOException
    {
        Room room = new Room();
        jsonreader.beginObject();
        do
        {
            if (!jsonreader.hasNext())
            {
                room.setLevel(level);
                jsonreader.endObject();
                return room;
            }
            String s = jsonreader.nextName();
            if (s.equals("id"))
            {
                room.setId(jsonreader.nextInt());
            } else
            if (s.equals("name"))
            {
                room.setName(jsonreader.nextString());
            } else
            if (s.equals("path"))
            {
                room.setPath(jsonreader.nextString());
            } else
            if (s.equals("x"))
            {
                room.setX(jsonreader.nextInt());
            } else
            if (s.equals("y"))
            {
                room.setY(jsonreader.nextInt());
            } else
            {
                jsonreader.skipValue();
            }
        } while (true);
    }

    private static void readRooms(Context context, Level level)
        throws IOException
    {
        ArrayList arraylist = new ArrayList();
        JsonReader jsonreader = new JsonReader(new InputStreamReader(context.openFileInput("levels_{0}.json".replace("/", "_").replace("{0}", Integer.toString(level.getId())))));
        jsonreader.setLenient(true);
        jsonreader.beginObject();
label0:
        do
        {
            if (!jsonreader.hasNext())
            {
                jsonreader.endObject();
                level.setRooms(arraylist);
                return;
            }
            if (jsonreader.nextName().equals("rooms"))
            {
                jsonreader.beginArray();
                do
                {
                    if (!jsonreader.hasNext())
                    {
                        jsonreader.endArray();
                        continue label0;
                    }
                    arraylist.add(readRoom(context, jsonreader, level));
                } while (true);
            }
            jsonreader.skipValue();
        } while (true);
    }

    public static void reset()
    {
        levels = null;
    }

    public static boolean updateFiles(String s, final Context final_context, Runnable runnable)
    {
        DownloadableFile adownloadablefile[];
        int i;
        adownloadablefile = new DownloadableFile[7];
        i = 0 + 1;
        adownloadablefile[0] = new DownloadableFile(new URI((new StringBuilder(String.valueOf(s))).append("levels.json").toString()), "levels.json");
        int j = i + 1;
        adownloadablefile[i] = new DownloadableFile(new URI((new StringBuilder(String.valueOf(s))).append("lights.json").toString()), "lights.json");
        i = j + 1;
        adownloadablefile[j] = new DownloadableFile(new URI((new StringBuilder(String.valueOf(s))).append("cards.json").toString()), "cards.json");
        int k = 1;
_L2:
        if (k <= 4)
        {
            break MISSING_BLOCK_LABEL_167;
        }
        (new DownloadFilesTask(runnable) {

            private final Runnable val$postExecute;

            protected void onPostExecute(Long long1)
            {
                super.onPostExecute(long1);
                LevelDao.reset();
                postExecute.run();
            }

            
            {
                postExecute = runnable;
                super(final_context);
            }
        }).execute(adownloadablefile);
        i;
        return true;
        int l = i + 1;
        adownloadablefile[i] = new DownloadableFile(new URI((new StringBuilder(String.valueOf(s))).append("levels_{0}.json".replace("{0}", Integer.toString(k))).toString()), "levels_{0}.json".replace("/", "_").replace("{0}", Integer.toString(k)));
        k++;
        i = l;
        if (true) goto _L2; else goto _L1
_L1:
        URISyntaxException urisyntaxexception;
        urisyntaxexception;
        i;
_L4:
        urisyntaxexception.printStackTrace();
        return false;
        urisyntaxexception;
        if (true) goto _L4; else goto _L3
_L3:
    }

}
