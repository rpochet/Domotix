package eu.pochet.domotix.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import org.acra.ACRA;

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

    private String domotixMqUri = null;

    private String domotixMqClientId = null;

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

        this.domotixMqUri = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.DOMOTIX_MQ_URI, Constants.DOMOTIX_MQ_URI_DEFAULT);
        this.domotixMqClientId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.DOMOTIX_MQ_CIENT_ID, Constants.DOMOTIX_MQ_CIENT_ID_DEFAULT);

        try {
            this.connectionFactory = new ConnectionFactory();
            this.connectionFactory.setUri(this.domotixMqUri);
        } catch (URISyntaxException e) {
            Log.e(ACTION, "Unable to create MQ connection factory", e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(ACTION, "Unable to create MQ connection factory", e);
        } catch (KeyManagementException e) {
            Log.e(ACTION, "Unable to create MQ connection factory", e);
        }

        new SubscribeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ActionBuilder.ActionType.MANAGEMENT.name(), domotixMqClientId);
        new SubscribeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ActionBuilder.ActionType.SWAP_PACKET.name(), domotixMqClientId);
        new SubscribeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ActionBuilder.ActionType.LIGHT_STATUS.name(), domotixMqClientId);
        new SubscribeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ActionBuilder.ActionType.TEMPERATURE.name(), domotixMqClientId);

        return START_REDELIVER_INTENT;
    }

    class SubscribeTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Connection connection = null;
            Channel channel = null;
            try {
                while (true) {
                    Log.i(ACTION, "Connecting to MQ " + params[0] + "...");
                    try {
                        connection = connectionFactory.newConnection();
                        channel = connection.createChannel();
                        channel.queueDeclare(params[0] + "-" + params[1], true, false, false, null);
                        channel.queueBind(params[0] + "-" + params[1], params[0], "");
                        QueueingConsumer consumer = new QueueingConsumer(channel);
                        channel.basicConsume(params[0] + "-" + params[1], false, consumer);
                        Log.i(ACTION, "Waiting for messages from MQ " + params[0] + "...");

                        while (true) {
                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                            broadcastIntent(delivery.getEnvelope().getExchange(), delivery.getBody());
                            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (IOException e) {
                        ACRA.getErrorReporter().handleException(e);
                        Log.w(ACTION, "Connection broken", e);
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException ee) {
                            break;
                        }
                    } finally {
                        if (channel != null && channel.isOpen()) {
                            try {
                                channel.close();
                            } catch (IOException e) {
                                Log.e(ACTION, "Unable to close channel", e);
                            }
                        }
                        if (connection != null && connection.isOpen()) {
                            try {
                                connection.close();
                            } catch (IOException e) {
                                Log.e(ACTION, "Unable to close connection", e);
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                System.out.println(e);
             }
            return null;
        }
    }

    private void broadcastIntent(String topic, byte[] packet) throws IOException {
        ActionBuilder.ActionType actionType = ActionBuilder.ActionType.valueOf(topic);
        ActionBuilder actionBuilder = new ActionBuilder().setAction(ActionBuilder.INTENT_FROM_SWAP).setType(actionType);
        switch(actionType) {
            case SWAP_PACKET:
            case LIGHT_STATUS:
            case TEMPERATURE:
                SwapPacket swapPacket = null;
                try {
                    swapPacket = DomotixDao.readSwapPacket(packet, 0);
                } catch (IllegalStateException e) {
                }
                if(swapPacket == null) {
                    ACRA.getErrorReporter().handleException(new IllegalArgumentException("Unable to get SWAP Packet from " + new String(packet)));
                    return;
                }
                actionBuilder.setSwapPacket(swapPacket);
                break;
            case MANAGEMENT:
                actionBuilder.setManagementData(packet);
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
