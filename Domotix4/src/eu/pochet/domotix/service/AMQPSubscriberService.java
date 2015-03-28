package eu.pochet.domotix.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import eu.pochet.domotix.Constants;
import eu.pochet.domotix.dao.DomotixDao;
import eu.pochet.domotix.dao.SwapPacket;

/**
 * Created by rpochet on 3/28/15.
 */
public class AMQPSubscriberService extends Service {

    public static final String ACTION = AMQPSubscriberService.class.getName();

    private String domotixMQ = null;

    private ConnectionFactory connectionFactory = null;

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

        this.domotixMQ = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.DOMOTIX_MQ_URI, Constants.DOMOTIX_MQ_URI_DEFAULT);

        try {
            this.connectionFactory = new ConnectionFactory();
            this.connectionFactory.setUri(this.domotixMQ);
        } catch (URISyntaxException e) {
            Log.e(ACTION, "Unable to create MQ connection factory", e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(ACTION, "Unable to create MQ connection factory", e);
        } catch (KeyManagementException e) {
            Log.e(ACTION, "Unable to create MQ connection factory", e);
        }
        new SubscribeTask().execute();

        return START_REDELIVER_INTENT;
    }

    class SubscribeTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Connection connection = null;
            Channel channel = null;
            while(true) {
                try {
                    connection = connectionFactory.newConnection();
                    channel = connection.createChannel();
                    QueueingConsumer consumer = new QueueingConsumer(channel);
                    channel.basicConsume("SWAP_PACKET", true, consumer);

                    while (true) {
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        String message = new String(delivery.getBody());
                        Log.d("","[r] " + message);
                        broadcastIntent(delivery.getEnvelope().getExchange(), delivery.getBody());
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    Log.w(ACTION, "Connection broken: ", e);
                    try {
                        Thread.sleep(5000); //sleep and then try again
                    } catch (InterruptedException ee) {
                        break;
                    }
                } finally {
                    if(channel != null) {
                        try {
                            channel.close();
                        } catch (IOException e) {
                            Log.e(ACTION, "Connection broken: ", e);
                        }
                    }
                    if(connection != null) {
                        try {
                            connection.close();
                        } catch (IOException e) {
                            Log.e(ACTION, "Connection broken: ", e);
                        }
                    }
                }
            }
            return null;
        }
    }

    private void broadcastIntent(String topic, byte[] packet) throws IOException {
        ActionBuilder.ActionType actionType = ActionBuilder.ActionType.TYPE_SWAP_PACKET;
        ActionBuilder actionBuilder = new ActionBuilder().setAction(ActionBuilder.INTENT_FROM_SWAP)
                .setType(actionType);
        switch(actionType) {
            case TYPE_SWAP_PACKET:
            case TYPE_LIGHT_STATUS:
            case TYPE_PRESSURE:
            case TYPE_TEMPERATURE:
                SwapPacket swapPacket = DomotixDao.readSwapPacket(packet, 0);
                actionBuilder.setSwapPacket(swapPacket);
                break;
            default:
                return;
        }
        actionBuilder.sendMessage(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        Log.d(ACTION, "Service onDestroy");
    }

}
