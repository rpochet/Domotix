// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPListenerService extends Service
{

    public static final String BROADCAST_ACTION = "UDPListenerService";
    public static final int DEFAULT_OUTGOING_BUS_PORT = 54128;
    public static final String OUTGOING_BUS_PORT_KEY = "message.out.bus.port";
    private Thread UDPBroadcastThread;
    private int domotixOutgoingBusPort;
    private Boolean shouldRestartSocketListen;
    private DatagramSocket socket;

    public UDPListenerService()
    {
        socket = null;
        UDPBroadcastThread = null;
        shouldRestartSocketListen = Boolean.valueOf(true);
        domotixOutgoingBusPort = 54128;
    }

    private void broadcastIntent(String s, byte abyte0[], int i)
    {
        Intent intent = new Intent("UDPListenerService");
        intent.putExtra("sender", s);
        intent.putExtra("message", abyte0);
        intent.putExtra("messageLength", i);
        sendBroadcast(intent);
    }

    private InetAddress getBroadcastAddress()
        throws IOException
    {
        DhcpInfo dhcpinfo = ((WifiManager)getApplicationContext().getSystemService("wifi")).getDhcpInfo();
        int i = dhcpinfo.ipAddress & dhcpinfo.netmask | -1 ^ dhcpinfo.netmask;
        byte abyte0[] = new byte[4];
        int j = 0;
        do
        {
            if (j >= 4)
            {
                return InetAddress.getByAddress(abyte0);
            }
            abyte0[j] = (byte)(0xff & i >> j * 8);
            j++;
        } while (true);
    }

    private void listenAndWaitAndThrowIntent(InetAddress inetaddress, Integer integer)
        throws Exception
    {
        Log.i("UDPListenerService", "Waiting for UDP broadcast");
        android.net.wifi.WifiManager.MulticastLock multicastlock = ((WifiManager)getSystemService("wifi")).createMulticastLock("dk.aboaya.pingpong");
        multicastlock.acquire();
        if (socket == null || socket.isClosed())
        {
            socket = new DatagramSocket(54128);
            socket.setBroadcast(true);
        }
        byte abyte0[] = new byte[64];
        DatagramPacket datagrampacket = new DatagramPacket(abyte0, abyte0.length);
        socket.receive(datagrampacket);
        multicastlock.release();
        String s = new String(datagrampacket.getData());
        Log.d("UDPListenerService", (new StringBuilder("UDP packet received: ")).append(s).toString());
        broadcastIntent(datagrampacket.getAddress().getHostAddress(), datagrampacket.getData(), datagrampacket.getLength());
        socket.close();
    }

    private void startListenForUDPBroadcast()
    {
        UDPBroadcastThread = new Thread(new Runnable() {

            final UDPListenerService this$0;

            public void run()
            {
                InetAddress inetaddress = getBroadcastAddress();
_L1:
                if (!shouldRestartSocketListen.booleanValue())
                {
                    return;
                }
                try
                {
                    listenAndWaitAndThrowIntent(inetaddress, Integer.valueOf(domotixOutgoingBusPort));
                }
                catch (Exception exception)
                {
                    Log.e("UDPListenerService", (new StringBuilder("No longer listening for UDP broadcasts cause of error ")).append(exception.getMessage()).toString());
                    return;
                }
                  goto _L1
            }

            
            {
                this$0 = UDPListenerService.this;
                super();
            }
        });
        UDPBroadcastThread.start();
    }

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void onCreate()
    {
        Log.d("UDPListenerService", "Service onCreate");
    }

    public void onDestroy()
    {
        Log.d("UDPListenerService", "Service onDestroy");
        stopListen();
    }

    public int onStartCommand(Intent intent, int i, int j)
    {
        Log.d("UDPListenerService", "Service onStartCommand");
        domotixOutgoingBusPort = intent.getIntExtra("message.out.bus.port", 54128);
        shouldRestartSocketListen = Boolean.valueOf(true);
        startListenForUDPBroadcast();
        return 1;
    }

    void stopListen()
    {
        shouldRestartSocketListen = Boolean.valueOf(false);
        if (socket != null)
        {
            socket.close();
        }
    }




}
