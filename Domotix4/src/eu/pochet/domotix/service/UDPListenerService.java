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
import android.preference.PreferenceManager;
import android.util.Log;
import eu.pochet.domotix.Constants;
import eu.pochet.domotix.dao.DomotixDao;
import eu.pochet.domotix.dao.SwapPacket;

public class UDPListenerService extends Service {

	public static final String ACTION = UDPListenerService.class.getName();

	private DatagramSocket socket = null;

	private int domotixBusOutputPort = -1;

	@Override
	public void onCreate() {
		Log.d(ACTION, "Service onCreate");
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(ACTION, "Service onStartCommand");

		this.domotixBusOutputPort = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.DOMOTIX_BUS_OUTPUT_PORT, Integer.toString(Constants.DOMOTIX_BUS_OUTPUT_PORT_DEFAULT)));
		
		new StartListenForBroadcastTask().execute();
		
		return START_REDELIVER_INTENT;
	}
	
	class StartListenForBroadcastTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			//try {
				while (!Thread.currentThread().isInterrupted()) {

					WifiManager wifi = (WifiManager) UDPListenerService.this.getSystemService(Context.WIFI_SERVICE);
					MulticastLock lock = wifi.createMulticastLock(ACTION);
					lock.acquire();

					if (socket == null || socket.isClosed()) {
						try {
							socket = new DatagramSocket(UDPListenerService.this.domotixBusOutputPort);
							// serverSocket.setSoTimeout(25000); //15 sec wait for the client to
							// connect
							socket.setBroadcast(true);
							Log.i(ACTION, "Waiting for UDP broadcast on port " + UDPListenerService.this.domotixBusOutputPort);
						} catch (IOException e) {
							Log.e(ACTION, "Fail to create UDP Socket", e);
						}
					}

					try {
						byte[] data = new byte[1024];
						DatagramPacket packet = new DatagramPacket(data, data.length);
						socket.receive(packet);
						lock.release();
	
						Log.d(ACTION, "UDP packet received");
						broadcastIntent(packet.getData(), packet.getLength());
					} catch (IOException e) {
						Log.e(ACTION, "Fail to process UDP Packet", e);
					}
					socket.close();
				}
			/*} catch (Exception e) {
				Log.e(ACTION, "No longer listening for UDP broadcasts cause of error ", e);
			}*/
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

	private void broadcastIntent(byte[] packet, int packetLength) throws IOException {
		int messageType = 0;
		int messageLength = 0;
		int nbPipe = 0;
		int i = 0;
		int val = 0;
		for (; ; i++) {
			if(packet[i] == '|') {
				nbPipe++;
				if(nbPipe == 2) {
					break;
				}
			} else if(nbPipe == 0)  {
				messageType = messageType * 16 + getVal(packet[i]);
			} else if(nbPipe == 1)  {
				messageLength = messageLength * 16 + getVal(packet[i]);
			}
		}
		int dataOffset = i + 1;
		if(packetLength < messageLength) {
			Log.e(ACTION, "Need more packet to create message");
		}
		ActionBuilder actionBuilder = new ActionBuilder().setAction(ActionBuilder.TYPE_FROM_SWAP).setType(ActionBuilder.TYPE_SWAP_PACKET);
		switch(messageType) {
			case ActionBuilder.TYPE_SWAP_PACKET:
				SwapPacket swapPacket = DomotixDao.readSwapPacket(packet, dataOffset);
				actionBuilder.setSwapPacket(swapPacket);
				break;
			default:
				return;
		}
		actionBuilder.sendMessage(getApplicationContext());
	}

	private int getVal(byte packet) {
		int val = 0;
		switch(packet) {
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
				val = packet - 'a' + 10; 
				break;
			default:
				val = packet - '0'; 
				break;
		}
		return val;
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