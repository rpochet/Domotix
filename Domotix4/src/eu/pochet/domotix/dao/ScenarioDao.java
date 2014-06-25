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
import java.util.List;

// Referenced classes of package eu.pochet.domotix.dao:
//            Scenario

public class ScenarioDao
{

    private static final String SCENARIOS_FILE_NAME = "scenarios.json";
    private static List scenarios = null;

    public ScenarioDao()
    {
    }

    public static List getScenarios(Context context)
    {
        if (scenarios != null) goto _L2; else goto _L1
_L1:
        scenarios = new ArrayList();
        JsonReader jsonreader;
        jsonreader = new JsonReader(new InputStreamReader(context.openFileInput("scenarios.json")));
        jsonreader.setLenient(true);
        jsonreader.beginObject();
_L5:
        if (jsonreader.hasNext()) goto _L4; else goto _L3
_L3:
        jsonreader.endObject();
_L2:
        return scenarios;
_L4:
        if (!jsonreader.nextName().equals("scenarios"))
        {
            break MISSING_BLOCK_LABEL_116;
        }
        jsonreader.beginArray();
_L6:
        if (jsonreader.hasNext())
        {
            break MISSING_BLOCK_LABEL_99;
        }
        jsonreader.endArray();
          goto _L5
        Exception exception;
        exception;
        exception.printStackTrace();
          goto _L2
        scenarios.add(readScenario(context, jsonreader));
          goto _L6
        jsonreader.skipValue();
          goto _L5
    }

    private static Scenario readScenario(Context context, JsonReader jsonreader)
        throws IOException
    {
        Scenario scenario = new Scenario();
        jsonreader.beginObject();
        do
        {
            if (!jsonreader.hasNext())
            {
                jsonreader.endObject();
                return scenario;
            }
            String s = jsonreader.nextName();
            if (s.equals("id"))
            {
                scenario.setId(jsonreader.nextInt());
            } else
            if (s.equals("name"))
            {
                scenario.setName(jsonreader.nextString());
            } else
            if (s.equals("level"))
            {
                scenario.setLevel(jsonreader.nextInt());
            } else
            if (s.equals("message"))
            {
                scenario.setMessage(jsonreader.nextString());
            } else
            {
                jsonreader.skipValue();
            }
        } while (true);
    }

    public static void reset()
    {
        scenarios = null;
    }

    public static boolean updateFiles(String s, final Context final_context, Runnable runnable)
    {
        DownloadableFile adownloadablefile[] = new DownloadableFile[1];
        int _tmp = 0 + 1;
        try
        {
            adownloadablefile[0] = new DownloadableFile(new URI((new StringBuilder(String.valueOf(s))).append("domotix/").append("scenarios.json").toString()), "scenarios.json");
            (new DownloadFilesTask(runnable) {

                private final Runnable val$postExecute;

                protected void onPostExecute(Long long1)
                {
                    super.onPostExecute(long1);
                    ScenarioDao.reset();
                    postExecute.run();
                }

            
            {
                postExecute = runnable;
                super(final_context);
            }
            }).execute(adownloadablefile);
        }
        catch (URISyntaxException urisyntaxexception)
        {
            urisyntaxexception.printStackTrace();
            return false;
        }
        return true;
    }

}
