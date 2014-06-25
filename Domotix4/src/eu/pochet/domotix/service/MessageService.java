// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.LevelDao;
import eu.pochet.domotix.dao.Light;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package eu.pochet.domotix.service:
//            Message

public class MessageService extends IntentService
{

    public static final String ACTION = "Domotix.MessageService.ACTION";
    public static final String LEVEL_ID = "Domotix.MessageService.LEVEL";
    public static final String LIGHT_ID = "Domotix.MessageService.LIGHT";
    private static final String TAG = eu/pochet/domotix/service/MessageService.getName();

    public MessageService()
    {
        super("Domotix.MessageService.ACTION");
    }

    private void sendMessage(Message message, String s, int i)
    {
        try
        {
            String s1 = message.getData();
            InetAddress inetaddress = InetAddress.getByName(s);
            DatagramPacket datagrampacket = new DatagramPacket(s1.getBytes(), s1.length(), inetaddress, i);
            (new DatagramSocket()).send(datagrampacket);
            Log.i(TAG, (new StringBuilder("Message ")).append(s1).append(" sent to Domotix bus").toString());
            return;
        }
        catch (Exception exception)
        {
            Log.e(TAG, "Failed to send message to Domotix bus", exception);
        }
    }

    protected void onHandleIntent(Intent intent)
    {
        String s;
        int i;
        Message message;
        String s1;
        s = (String)intent.getCharSequenceExtra("domotixIncomingBusHost");
        if (s == null)
        {
            s = "192.168.1.4";
        }
        i = intent.getIntExtra("domotixIncomingBusPort", 5555);
        message = null;
        s1 = intent.getStringExtra("Domotix.MessageService.ACTION");
        if (!"switch_off_all".equals(s1)) goto _L2; else goto _L1
_L1:
        Iterator iterator1;
        int j1 = intent.getIntExtra("Domotix.MessageService.LEVEL", 0);
        iterator1 = LevelDao.getLevel(getApplicationContext(), j1).getLights().iterator();
_L5:
        if (iterator1.hasNext()) goto _L4; else goto _L3
_L3:
        sendMessage(message, s, i);
_L7:
        return;
_L4:
        Light light4 = (Light)iterator1.next();
        message = new Message(light4.getCardAddress(), "02");
        message.setRegisterNb(light4.getOutputNb());
        message.setRegisterValue("00");
          goto _L5
_L2:
        if ("switch_on_all".equals(s1))
        {
            int i1 = intent.getIntExtra("Domotix.MessageService.LEVEL", 0);
            Iterator iterator = LevelDao.getLevel(getApplicationContext(), i1).getLights().iterator();
            while (iterator.hasNext()) 
            {
                Light light3 = (Light)iterator.next();
                message = new Message(light3.getCardAddress(), "02");
                message.setRegisterNb(light3.getOutputNb());
                message.setRegisterValue("FF");
            }
        } else
        if ("switch_on_light".equals(s1))
        {
            int l = intent.getIntExtra("Domotix.MessageService.LIGHT", 0);
            Light light2 = LevelDao.getLight(getApplicationContext(), l);
            message = new Message(light2.getCardAddress(), "02");
            message.setRegisterNb(light2.getOutputNb());
            message.setRegisterValue("FF");
        } else
        {
            if (!"switch_off_light".equals(s1))
            {
                continue; /* Loop/switch isn't completed */
            }
            int k = intent.getIntExtra("Domotix.MessageService.LIGHT", 0);
            Light light1 = LevelDao.getLight(getApplicationContext(), k);
            message = new Message(light1.getCardAddress(), "02");
            message.setRegisterNb(light1.getOutputNb());
            message.setRegisterValue("00");
        }
          goto _L3
        if (!"toggle_light".equals(s1)) goto _L7; else goto _L6
_L6:
        int j = intent.getIntExtra("Domotix.MessageService.LIGHT", 0);
        Light light = LevelDao.getLight(getApplicationContext(), j);
        message = new Message(light.getCardAddress(), "02");
        message.setRegisterNb(light.getOutputNb());
        message.setRegisterValue("00");
          goto _L3
    }

}
