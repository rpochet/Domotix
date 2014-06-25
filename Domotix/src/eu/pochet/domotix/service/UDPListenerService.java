package eu.pochet.domotix.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.IBinder;
import android.util.Log;

public class UDPListenerService extends Service 
{
	public static final String BROADCAST_ACTION = "UDPListenerService";
	
	private DatagramSocket socket = null;
	
	private Thread UDPBroadcastThread = null;
	
	private Boolean shouldRestartSocketListen = true;

	private int domotixOutgoingBusPort = DEFAULT_OUTGOING_BUS_PORT;
	
	public static final String OUTGOING_BUS_PORT_KEY = "message.out.bus.port";
	
	public static final int DEFAULT_OUTGOING_BUS_PORT = 54128;
	
	private void listenAndWaitAndThrowIntent(InetAddress broadcastIP, Integer port) throws Exception {
		Log.i(BROADCAST_ACTION, "Waiting for UDP broadcast");
		
		WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		MulticastLock lock = wifi.createMulticastLock("dk.aboaya.pingpong");
		lock.acquire();
		
		if(socket == null || socket.isClosed()) {
			socket = new DatagramSocket(54128);
			//serverSocket.setSoTimeout(25000); //15 sec wait for the client to connect
			socket.setBroadcast(true);
		}
		
		byte[] data = new byte[64]; 
		DatagramPacket packet = new DatagramPacket(data, data.length);
		socket.receive(packet);
		lock.release();
		
		String s = new String(packet.getData());
		Log.d(BROADCAST_ACTION, "UDP packet received: " + s);
		
		String senderIP = packet.getAddress().getHostAddress();
		broadcastIntent(senderIP, packet.getData(), packet.getLength());
		socket.close();
	}

	private void broadcastIntent(String senderIP, byte[] message, int messageLength) {
		Intent intent = new Intent(UDPListenerService.BROADCAST_ACTION);
		intent.putExtra("sender", senderIP);
		intent.putExtra("message", message);
		intent.putExtra("messageLength", messageLength);
		sendBroadcast(intent);
	}
	
	private InetAddress getBroadcastAddress() throws IOException {
	    WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	    DhcpInfo dhcp = wifi.getDhcpInfo();
	    // handle null somehow

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    return InetAddress.getByAddress(quads);
	}
	
	private void startListenForUDPBroadcast() {
		UDPBroadcastThread = new Thread(new Runnable()  {
			public void run() {
				try {
					//InetAddress broadcastIP = InetAddress.getByName("192.168.1.9"); // 192.168.1.255, 0.0.0.0, 255.255.255.255
					InetAddress broadcastIP = getBroadcastAddress();
					while (shouldRestartSocketListen) {
						listenAndWaitAndThrowIntent(broadcastIP, UDPListenerService.this.domotixOutgoingBusPort);
					}
					//if (!shouldListenForUDPBroadcast) throw new ThreadDeath();
				} catch (Exception e) {
					Log.e(BROADCAST_ACTION, "No longer listening for UDP broadcasts cause of error " + e.getMessage());
				}
			}
		});
		UDPBroadcastThread.start();
	}
	
	void stopListen() {
		shouldRestartSocketListen = false;
		if(socket != null) {
			socket.close();
		}
	}
	
	@Override
	public void onCreate() {
		Log.d(BROADCAST_ACTION, "Service onCreate");
	};
	
	@Override
	public void onDestroy() {
		Log.d(BROADCAST_ACTION, "Service onDestroy");
		
		stopListen();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(BROADCAST_ACTION, "Service onStartCommand");
		
		this.domotixOutgoingBusPort = intent.getIntExtra(OUTGOING_BUS_PORT_KEY, DEFAULT_OUTGOING_BUS_PORT);
		
		shouldRestartSocketListen = true;
		startListenForUDPBroadcast();
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
}