// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;

import java.nio.channels.SelectableChannel;
import zmq.PollItem;
import zmq.SocketBase;

// Referenced classes of package org.jeromq:
//            ZMQ

public static class cess._cls200
{

    private final PollItem base;

    public final PollItem base()
    {
        return base;
    }

    public final SelectableChannel getChannel()
    {
        return base.getRawSocket();
    }

    public final SocketBase getSocket()
    {
        return base.getSocket();
    }

    public final int interestOps(int i)
    {
        return base.interestOps(i);
    }

    public final boolean isReadable()
    {
        return base.isReadable();
    }

    public final boolean isWritable()
    {
        return base.isWritable();
    }


    public ableChannel(SelectableChannel selectablechannel, int i)
    {
        base = new PollItem(selectablechannel, i);
    }

    public ableChannel(ableChannel ablechannel, int i)
    {
        base = new PollItem(cess._mth200(ablechannel), i);
    }
}
