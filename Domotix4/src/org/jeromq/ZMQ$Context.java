// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;

import zmq.Ctx;
import zmq.ZMQ;

// Referenced classes of package org.jeromq:
//            ZMQ

public static class ctx
{

    private final Ctx ctx;

    public ctx poller()
    {
        return new init>(this);
    }

    public init> poller(int i)
    {
        return new init>(this, i);
    }

    public init> socket(int i)
    {
        return new init>(this, i);
    }

    public void term()
    {
        ctx.terminate();
    }


    protected (int i)
    {
        ctx = ZMQ.zmq_init(i);
    }
}
