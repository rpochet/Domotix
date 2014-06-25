// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;

import zmq.ZMQ;

public class ZMQQueue
    implements Runnable
{

    private final ZMQ.Socket inSocket;
    private final ZMQ.Socket outSocket;

    public ZMQQueue(ZMQ.Context context, ZMQ.Socket socket, ZMQ.Socket socket1)
    {
        inSocket = socket;
        outSocket = socket1;
    }

    public void run()
    {
        ZMQ.zmq_proxy(inSocket.base(), outSocket.base(), null);
    }
}
