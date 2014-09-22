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
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import eu.pochet.domotix.Constants;
import eu.pochet.domotix.service.ZMQListenerService.StartListenForBroadcastTask;

public class UDPListenerService extends Service {

	public static final String ACTION = UDPListenerService.class.getName();

	private DatagramSocket socket = null;

	private Thread UDPBroadcastThread = null;

	private Boolean shouldRestartSocketListen = true;

	private int domotixBusOutputPort = Constants.DOMOTIX_BUS_OUTPUT_PORT_DEFAULT;

	@Override
	public void onCreate() {
		Log.d(ACTION, "Service onCreate");
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(ACTION, "Service onStartCommand");
		this.domotixBusOutputPort = intent.getIntExtra(
				Constants.DOMOTIX_BUS_OUTPUT_PORT,
				Constants.DOMOTIX_BUS_OUTPUT_PORT_DEFAULT);
		
		new StartListenForBroadcastTask().execute();
	}
	
	class StartListenForBroadcastTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				// InetAddress broadcastIP =
				// InetAddress.getByName("192.168.1.9"); // 192.168.1.255,
				// 0.0.0.0, 255.255.255.255
				InetAddress broadcastIP = getBroadcastAddress();
				while (!Thread.currentThread().isInterrupted()) {
					Log.i(ACTION, "Waiting for UDP broadcast");

					WifiManager wifi = (WifiManager) UDPListenerService.this.getSystemService(Context.WIFI_SERVICE);
					MulticastLock lock = wifi.createMulticastLock("dk.aboaya.pingpong");
					lock.acquire();

					if (socket == null || socket.isClosed()) {
						socket = new DatagramSocket(UDPListenerService.this.domotixBusOutputPort);
						// serverSocket.setSoTimeout(25000); //15 sec wait for the client to
						// connect
						socket.setBroadcast(true);
					}

					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					socket.receive(packet);
					lock.release();

					String s = new String(packet.getData());
					Log.d(ACTION, "UDP packet received: " + s);

					String senderIP = packet.getAddress().getHostAddress();
					broadcastIntent(senderIP, packet.getData(), packet.getLength());
					socket.close();
				}
				// if (!shouldListenForUDPBroadcast) throw new
				// ThreadDeath();
			} catch (Exception e) {
				Log.e(ACTION, "No longer listening for UDP broadcasts cause of error ", e);
			}
			return null;
		}
	}

	@Override
	public void onDestroy() {
		Log.d(ACTION, "Service onDestroy");

		if (socket != null) {
			socket.close();
		}
	}

	private void broadcastIntent(String senderIP, byte[] message, int messageLength) {
		Intent intent = new Intent(UDPListenerService.ACTION);
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
		for (int k = 0; k < 4; k++) {
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		}
		return InetAddress.getByAddress(quads);
	}

}