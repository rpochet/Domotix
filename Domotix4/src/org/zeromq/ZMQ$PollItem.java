// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.zeromq;

import java.nio.channels.SelectableChannel;
import zmq.PollItem;

// Referenced classes of package org.zeromq:
//            ZMQ

public static class cess._cls100
{

    private final PollItem base;
    private final base socket;

    protected final PollItem base()
    {
        return base;
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof base)
        {
            base base1 = (base)obj;
            if (socket != null && socket == base1.socket)
            {
                return true;
            }
            if (getRawSocket() != null && getRawSocket() == base1.getRawSocket())
            {
                return true;
            }
        }
        return false;
    }

    public final SelectableChannel getRawSocket()
    {
        return base.getRawSocket();
    }

    public final t getSocket()
    {
        return socket;
    }

    public final boolean isError()
    {
        return base.isError();
    }

    public final boolean isReadable()
    {
        return base.isReadable();
    }

    public final boolean isWritable()
    {
        return base.isWritable();
    }

    public final int readyOps()
    {
        return base.readyOps();
    }


    public ableChannel(SelectableChannel selectablechannel, int i)
    {
        base = new PollItem(selectablechannel, i);
        socket = null;
    }

    public socket(socket socket1, int i)
    {
        socket = socket1;
        base = new PollItem(cess._mth100(socket1), i);
    }
}
