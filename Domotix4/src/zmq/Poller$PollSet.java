// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.channels.SelectionKey;

// Referenced classes of package zmq:
//            Poller, IPollEvents

private static class ops
{

    protected boolean cancelled;
    protected IPollEvents handler;
    protected SelectionKey key;
    protected int ops;

    protected electionKey(IPollEvents ipollevents)
    {
        handler = ipollevents;
        key = null;
        cancelled = false;
        ops = 0;
    }
}
