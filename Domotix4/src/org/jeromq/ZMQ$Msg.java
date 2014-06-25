// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;

import java.nio.ByteBuffer;
import zmq.Msg;

// Referenced classes of package org.jeromq:
//            ZMQ

public static class base
{

    public static final int MORE = 1;
    private final Msg base;

    public ByteBuffer buf()
    {
        return base.buf();
    }

    public void close()
    {
        base.close();
    }

    public byte[] data()
    {
        return base.data();
    }

    public int getFlags()
    {
        return base.flags();
    }

    public boolean hasMore()
    {
        return base.has_more();
    }

    public void put(String s, int i)
    {
        base.put(s, i);
    }

    public void put(base base1, int i)
    {
        base.put(base1.base, i);
    }

    public void put(byte abyte0[], int i)
    {
        base.put(abyte0, i);
    }

    public void setFlags(int i)
    {
        base.set_flags(i);
    }

    public int size()
    {
        return base.size();
    }


    public ()
    {
        base = new Msg();
    }

    public base(int i)
    {
        base = new Msg(i);
    }

    public base(String s)
    {
        base = new Msg(s);
    }

    public (ByteBuffer bytebuffer)
    {
        base = new Msg(bytebuffer);
    }

    public (Msg msg)
    {
        base = msg;
    }
}
