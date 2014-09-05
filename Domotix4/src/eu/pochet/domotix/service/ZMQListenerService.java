package eu.pochet.domotix.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import eu.pochet.domotix.Constants;

import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.JsonReader;
import android.util.Log;

public class ZMQListenerService extends Service {
	
	public static final String ACTION = ZMQListenerService.class.getName();

	private String domotixBusOutputHost = Constants.DOMOTIX_BUS_OUTPUT_HOST;

	private int domotixBusOutputPort = Constants.DOMOTIX_BUS_OUTPUT_PORT_DEFAULT;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(ACTION, "Service onStartCommand");
		domotixBusOutputHost = intent.getStringExtra(Constants.DOMOTIX_BUS_OUTPUT_HOST);
		domotixBusOutputPort = intent.getIntExtra(Constants.DOMOTIX_BUS_OUTPUT_PORT, Constants.DOMOTIX_BUS_OUTPUT_PORT_DEFAULT);
		startListenForBroadcast();
	}

	private void startListenForBroadcast() {
		Ctx ctx = ZMQ.zmq_init(1);
		SocketBase socket = ZMQ.zmq_socket(ctx, ZMQ.ZMQ_SUB);
		socket.setsockopt(ZMQ.ZMQ_SUBSCRIBE, "topic");
		boolean rc = ZMQ.zmq_connect(socket, "tcp://"
				+ this.domotixBusOutputHost + ":"
				+ this.domotixBusOutputPort);

		Msg msg = null;
		while (!Thread.currentThread().isInterrupted()) {
			msg = socket.recv(0);
			Log.d(ACTION, new String(msg.data()));
			JsonReader reader = new JsonReader(
					new BufferedReader(new InputStreamReader(
							new ByteArrayInputStream(msg.data()))));
			reader.setLenient(true);
			try {
				reader.beginObject();

			} catch (IOException e) {
				e.printStackTrace();
				Log.e(ACTION, "Unable to read message", e);
			}

		}
		socket.close();
		ctx.terminate();
	}

}
