// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

// Referenced classes of package zmq:
//            SocketBase

public class PollItem
{

    private SelectableChannel c;
    private int interest;
    private int ready;
    private SocketBase s;
    private int zinterest;

    public PollItem(SelectableChannel selectablechannel, int i)
    {
        s = null;
        c = selectablechannel;
        init(i);
    }

    public PollItem(SocketBase socketbase)
    {
        s = socketbase;
        c = null;
        interest = -1;
        zinterest = -1;
    }

    public PollItem(SocketBase socketbase, int i)
    {
        s = socketbase;
        c = null;
        init(i);
    }

    private void init(int i)
    {
        zinterest = i;
        int j = i & 1;
        int k = 0;
        if (j > 0)
        {
            k = false | true;
        }
        if ((i & 2) > 0)
        {
            if (s != null)
            {
                k |= 1;
            } else
            {
                k |= 4;
            }
        }
        interest = k;
        ready = 0;
    }

    protected final SelectableChannel getChannel()
    {
        if (s != null)
        {
            return s.get_fd();
        } else
        {
            return c;
        }
    }

    public final SelectableChannel getRawSocket()
    {
        return c;
    }

    public final SocketBase getSocket()
    {
        return s;
    }

    public final int interestOps()
    {
        return interest;
    }

    public final int interestOps(int i)
    {
        init(i);
        return interest;
    }

    public final boolean isError()
    {
        return (4 & ready) > 0;
    }

    public final boolean isReadable()
    {
        return (1 & ready) > 0;
    }

    public final boolean isWritable()
    {
        return (2 & ready) > 0;
    }

    public final int readyOps()
    {
        return ready;
    }

    public final int readyOps(SelectionKey selectionkey, int i)
    {
        ready = 0;
        if (s == null) goto _L2; else goto _L1
_L1:
        int j = s.getsockopt(15);
        if (j < 0)
        {
            return -1;
        }
        if ((2 & zinterest) > 0 && (j & 2) > 0)
        {
            ready = 2 | ready;
        }
        if ((1 & zinterest) > 0 && (j & 1) > 0)
        {
            ready = 1 | ready;
        }
_L4:
        return ready;
_L2:
        if (i > 0)
        {
            if (selectionkey.isReadable())
            {
                ready = 1 | ready;
            }
            if (selectionkey.isWritable())
            {
                ready = 2 | ready;
            }
            if (!selectionkey.isValid() || selectionkey.isAcceptable() || selectionkey.isConnectable())
            {
                ready = 4 | ready;
            }
        }
        if (true) goto _L4; else goto _L3
_L3:
    }
}
