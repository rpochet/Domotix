// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.dao;

import android.util.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

// Referenced classes of package eu.pochet.domotix.dao:
//            LagartoResponse

public class LagartoResponseDao
{

    public LagartoResponseDao()
    {
    }

    public static LagartoResponse getLagartoResponse(String s)
    {
        LagartoResponse lagartoresponse = new LagartoResponse();
        JsonReader jsonreader;
        jsonreader = new JsonReader(new StringReader(s));
        jsonreader.setLenient(true);
        jsonreader.beginObject();
        if (!jsonreader.nextName().equals("lagarto")) goto _L2; else goto _L1
_L1:
        jsonreader.beginObject();
_L5:
        if (jsonreader.hasNext()) goto _L4; else goto _L3
_L3:
        jsonreader.endObject();
_L2:
        jsonreader.endObject();
        return lagartoresponse;
_L4:
        Exception exception;
        String s1;
label0:
        {
            s1 = jsonreader.nextName();
            if (!s1.equals("procname"))
            {
                break label0;
            }
            lagartoresponse.setProcname(jsonreader.nextString());
        }
          goto _L5
label1:
        {
            if (!s1.equals("httpserver"))
            {
                break label1;
            }
            lagartoresponse.setHttpserver(jsonreader.nextString());
        }
          goto _L5
        if (!s1.equals("status"))
        {
            break MISSING_BLOCK_LABEL_167;
        }
        jsonreader.beginArray();
_L6:
        if (jsonreader.hasNext())
        {
            break MISSING_BLOCK_LABEL_149;
        }
        jsonreader.endArray();
          goto _L5
        try
        {
            lagartoresponse.getStatus().add(readStatus(lagartoresponse, jsonreader));
        }
        // Misplaced declaration of an exception variable
        catch (Exception exception)
        {
            exception.printStackTrace();
            return lagartoresponse;
        }
          goto _L6
        jsonreader.skipValue();
          goto _L5
    }

    private static LagartoResponse.Status readStatus(LagartoResponse lagartoresponse, JsonReader jsonreader)
        throws IOException
    {
        lagartoresponse.getClass();
        LagartoResponse.Status status = new LagartoResponse.Status(lagartoresponse);
        jsonreader.beginObject();
        do
        {
            if (!jsonreader.hasNext())
            {
                jsonreader.endObject();
                return status;
            }
            String s = jsonreader.nextName();
            if (s.equals("id"))
            {
                status.setId(jsonreader.nextString());
            } else
            if (s.equals("location"))
            {
                status.setLocation(jsonreader.nextString());
            } else
            if (s.equals("name"))
            {
                status.setName(jsonreader.nextString());
            } else
            if (s.equals("value"))
            {
                status.setValue(jsonreader.nextString());
            } else
            if (s.equals("type"))
            {
                status.setType(jsonreader.nextString());
            } else
            if (s.equals("direction"))
            {
                status.setDirection(jsonreader.nextString());
            } else
            if (s.equals("timestamp"))
            {
                status.setTimestamp(jsonreader.nextString());
            } else
            {
                jsonreader.skipValue();
            }
        } while (true);
    }
}
