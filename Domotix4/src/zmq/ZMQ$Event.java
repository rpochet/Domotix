// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;

// Referenced classes of package zmq:
//            SocketBase, Msg, ZMQ

public static class flag
{

    private static final int VALUE_CHANNEL = 2;
    private static final int VALUE_INTEGER = 1;
    public final String addr;
    private final Object arg;
    public final int event;
    private final int flag;

    public static  read(SocketBase socketbase)
    {
        Msg msg = socketbase.recv(0);
        if (msg == null)
        {
            return null;
        }
        ByteBuffer bytebuffer = msg.buf();
        int i = bytebuffer.getInt();
        byte abyte0[] = new byte[bytebuffer.get()];
        bytebuffer.get(abyte0);
        byte byte0 = bytebuffer.get();
        Integer integer = null;
        if (byte0 == 1)
        {
            integer = Integer.valueOf(bytebuffer.getInt());
        }
        return new <init>(i, new String(abyte0), integer);
    }

    public boolean write(SocketBase socketbase)
    {
        int i = 1 + (5 + addr.length());
        if (flag == 1)
        {
            i += 4;
        }
        ByteBuffer bytebuffer = ByteBuffer.allocate(i);
        bytebuffer.putInt(event);
        bytebuffer.put((byte)addr.length());
        bytebuffer.put(addr.getBytes());
        bytebuffer.put((byte)flag);
        if (flag == 1)
        {
            bytebuffer.putInt(((Integer)arg).intValue());
        }
        return socketbase.send(new Msg(bytebuffer), 0);
    }

    public ct(int i, String s, Object obj)
    {
        event = i;
        addr = s;
        arg = obj;
        if (obj instanceof Integer)
        {
            flag = 1;
            return;
        }
        if (obj instanceof SelectableChannel)
        {
            flag = 2;
            return;
        } else
        {
            flag = 0;
            return;
        }
    }
}
