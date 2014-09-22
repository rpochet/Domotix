package eu.pochet.domotix.service;

import java.io.IOException;

import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import eu.pochet.domotix.Constants;
import eu.pochet.domotix.dao.DomotixDao;
import eu.pochet.domotix.dao.SwapPacket;

/**
 * 
 * @author romuald
 * 
 */
public class ZMQListenerService extends Service {

	public static final String ACTION = ZMQListenerService.class.getName();

	public static final Object SWAP_PACKET = "SWAP_PACKET";

	private String domotixBusOutputHost = Constants.DOMOTIX_BUS_OUTPUT_HOST_DEFAULT;

	private int domotixBusOutputPort = Constants.DOMOTIX_BUS_OUTPUT_PORT_DEFAULT;

	private Ctx ctx = null;

	private SocketBase socket = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(ACTION, "Service onStartCommand");
		
		this.domotixBusOutputHost = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.DOMOTIX_BUS_OUTPUT_HOST, Constants.DOMOTIX_BUS_OUTPUT_HOST_DEFAULT);
		this.domotixBusOutputPort = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(Constants.DOMOTIX_BUS_OUTPUT_PORT, Constants.DOMOTIX_BUS_OUTPUT_PORT_DEFAULT);
		
		new StartListenForBroadcastTask().execute();
	}
	
	class StartListenForBroadcastTask extends AsyncTask<Void, Void, Void> {
		
		@Override
	    protected Void doInBackground(Void... params) {
	    	ZMQListenerService.this.ctx = ZMQ.zmq_init(1);
	    	ZMQListenerService.this.socket = ZMQ.zmq_socket(ZMQListenerService.this.ctx, ZMQ.ZMQ_SUB);
	    	ZMQListenerService.this.socket.setsockopt(ZMQ.ZMQ_SUBSCRIBE, SWAP_PACKET);
			boolean rc = ZMQ.zmq_connect(ZMQListenerService.this.socket, "tcp://"
					+ ZMQListenerService.this.domotixBusOutputHost + ":" + ZMQListenerService.this.domotixBusOutputPort);

			SwapPacket swapPacket = null;
			Msg msg = null;
			while (!Thread.currentThread().isInterrupted()) {
				ZMQListenerService.this.socket.recv(0); // Topic name
				msg = ZMQListenerService.this.socket.recv(0);
				Log.d(ACTION, new String(msg.data()));
				try {
					swapPacket = DomotixDao.readSwapPacket(msg.data());
					broadcastSwapPacket(swapPacket);
				} catch (IOException e) {
					e.printStackTrace();
					Log.e(ACTION, "Unable to read message", e);
				}				
			}    
			
			return null;
	    }

	}

	private void broadcastSwapPacket(SwapPacket swapPacket) {
		new ActionBuilder()
				.setAction(ActionBuilder.ACTION_IN)
				.setType(ActionBuilder.TYPE_SWAP_PACKET)
				.setSwapPacket(swapPacket)
			.sendMessage(getBaseContext());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				if(ZMQListenerService.this.socket != null) {
					ZMQListenerService.this.socket.close();
				}
				if(ZMQListenerService.this.ctx != null) {
					ZMQListenerService.this.ctx.terminate();
				}
				return null;
			};
		};
	}

}
