// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import org.zeromq.ZMQ;

public class LagartoEventService extends Service
{

    public static final String ACTION = "LagartoEventService";
    private org.zeromq.ZMQ.Socket socket;

    public LagartoEventService()
    {
        socket = null;
    }

    private void broadcastIntent(String s)
    {
        Intent intent = new Intent("LagartoEventService");
        intent.putExtra("message", s);
        sendBroadcast(intent);
    }

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void onCreate()
    {
        Log.d("LagartoEventService", "Service onCreate");
    }

    public void onDestroy()
    {
        Log.d("LagartoEventService", "Service onDestroy");
        if (socket != null)
        {
            socket.close();
        }
    }

    public int onStartCommand(Intent intent, int i, int j)
    {
        Log.d("LagartoEventService", "Service onStartCommand");
        socket = ZMQ.context(1).socket(4);
        socket.bind("tcp://*:5555");
        (new Thread(new Runnable() {

            final LagartoEventService this$0;

            public void run()
            {
                broadcastIntent("{\"lagarto\":{\"procname\": \"SWAP network\",\"httpserver\": \"192.168.1.34:8001\",\"status\":[{\"id\": \"10.12.0\",\"location\": \"garden\",\"name\": \"relay1\",\"value\": \"ON\",\"type\": \"bin\",\"direction\": \"out\",\"timestamp\": \"19 Feb 2012 16:15:17\"},{\"id\": \"10.12.1\",\"location\": \"garden\",\"name\": \"relay2\",\"value\": \"OFF\",\"type\": \"bin\",\"direction\": \"out\",\"timestamp\": \"19 Feb 2012 16:15:17\"}]}}");
            }

            
            {
                this$0 = LagartoEventService.this;
                super();
            }
        })).start();
        return 1;
    }

}
