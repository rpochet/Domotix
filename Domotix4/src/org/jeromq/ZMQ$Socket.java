// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;

import java.io.PrintStream;
import java.nio.channels.SelectableChannel;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;

// Referenced classes of package org.jeromq:
//            ZMQException, ZMQ

public static class base
{

    private static final int DYNFROM = 49152;
    private static final int DYNTO = 65535;
    private final SocketBase base;
    private final Ctx ctx;

    private final int bind(String s, int i, int j)
    {
        if (s.endsWith(":*"))
        {
            int l = i;
            String s1 = s.substring(0, 1 + s.lastIndexOf(':'));
            for (; l <= j; l++)
            {
                String s2 = (new StringBuilder()).append(s1).append(l).toString();
                if (base.bind(s2))
                {
                    return l;
                }
            }

            return -1;
        }
        if (base.bind(s))
        {
            int k;
            try
            {
                k = Integer.parseInt(s.substring(1 + s.lastIndexOf(':')));
            }
            catch (NumberFormatException numberformatexception)
            {
                return 0;
            }
            return k;
        } else
        {
            return -1;
        }
    }

    private void mayRaise()
    {
        int i = base.errno();
        if (i == 0 || i == 35)
        {
            return;
        }
        if (i == 0x9523dfd)
        {
            throw new n.CtxTerminated();
        } else
        {
            throw new ZMQException(i);
        }
    }

    public SocketBase base()
    {
        return base;
    }

    public final int bind(String s)
    {
        return bind(s, 49152, 65535);
    }

    public int bindToRandomPort(String s)
    {
        return bind((new StringBuilder()).append(s).append(":*").toString(), 49152, 65535);
    }

    public int bindToRandomPort(String s, int i, int j)
    {
        return bind((new StringBuilder()).append(s).append(":*").toString(), i, j);
    }

    public final void close()
    {
        base.close();
    }

    public final boolean connect(String s)
    {
        boolean flag = base.connect(s);
        mayRaise();
        return flag;
    }

    public void dump()
    {
        System.out.println("----------------------------------------");
        do
        {
            mayRaise mayraise = recvMsg(0);
            PrintStream printstream = System.out;
            Object aobj[] = new Object[2];
            aobj[0] = Integer.valueOf(mayraise.e());
            String s;
            if (mayraise.e() > 0)
            {
                s = new String(mayraise.a());
            } else
            {
                s = "";
            }
            aobj[1] = s;
            printstream.println(String.format("[%03d] %s", aobj));
        } while (hasReceiveMore());
    }

    public final long getAffinity()
    {
        return ((Long)base.getsockoptx(4)).longValue();
    }

    public final long getBacklog()
    {
        return (long)base.getsockopt(19);
    }

    public final int getEvents()
    {
        return base.getsockopt(15);
    }

    public final SelectableChannel getFD()
    {
        return (SelectableChannel)base.getsockoptx(14);
    }

    public final long getHWM()
    {
        return -1L;
    }

    public final byte[] getIdentity()
    {
        return (byte[])(byte[])base.getsockoptx(5);
    }

    public final long getLinger()
    {
        return (long)base.getsockopt(17);
    }

    public final long getMaxMsgSize()
    {
        return ((Long)base.getsockoptx(22)).longValue();
    }

    public final long getMulticastHops()
    {
        return (long)base.getsockopt(25);
    }

    public final long getRate()
    {
        return (long)base.getsockopt(8);
    }

    public final long getRcvHWM()
    {
        return (long)base.getsockopt(24);
    }

    public final long getReceiveBufferSize()
    {
        return (long)base.getsockopt(12);
    }

    public final int getReceiveTimeOut()
    {
        return base.getsockopt(27);
    }

    public final long getReconnectIVL()
    {
        return (long)base.getsockopt(18);
    }

    public final long getReconnectIVLMax()
    {
        return (long)base.getsockopt(21);
    }

    public final long getRecoveryInterval()
    {
        return (long)base.getsockopt(9);
    }

    public final boolean getRouterMandatory()
    {
        return false;
    }

    public final long getSendBufferSize()
    {
        return (long)base.getsockopt(11);
    }

    public final int getSendTimeOut()
    {
        return base.getsockopt(28);
    }

    public final long getSndHWM()
    {
        return (long)base.getsockopt(23);
    }

    public final long getSwap()
    {
        return -1L;
    }

    public final int getType()
    {
        return base.getsockopt(16);
    }

    public final boolean hasMulticastLoop()
    {
        return false;
    }

    public final boolean hasReceiveMore()
    {
        return base.getsockopt(13) == 1;
    }

    public boolean monitor(String s, int i)
    {
        return base.monitor(s, i);
    }

    public final int recv(byte abyte0[], int i, int j, int k)
    {
        Msg msg = base.recv(k);
        if (msg != null)
        {
            System.arraycopy(msg.data(), 0, abyte0, i, j);
            return msg.size();
        } else
        {
            return -1;
        }
    }

    public final byte[] recv()
    {
        return recv(0);
    }

    public final byte[] recv(int i)
    {
        Msg msg = base.recv(i);
        if (msg != null)
        {
            return msg.data();
        } else
        {
            return null;
        }
    }

    public final base recvMsg()
    {
        return recvMsg(0);
    }

    public final recvMsg recvMsg(int i)
    {
        Msg msg = base.recv(i);
        if (msg != null)
        {
            return new it>(msg);
        } else
        {
            return null;
        }
    }

    public final String recvStr()
    {
        return recvStr(0);
    }

    public final String recvStr(int i)
    {
        Msg msg = base.recv(i);
        if (msg != null)
        {
            return new String(msg.data());
        } else
        {
            return null;
        }
    }

    public final boolean send(String s)
    {
        Msg msg = new Msg(s);
        return base.send(msg, 0);
    }

    public final boolean send(String s, int i)
    {
        Msg msg = new Msg(s);
        return base.send(msg, i);
    }

    public final boolean send(base base1)
    {
        return base.send(ess._mth100(base1), 0);
    }

    public final boolean send(ess._cls100 _pcls100, int i)
    {
        return base.send(ess._mth100(_pcls100), i);
    }

    public final boolean send(byte abyte0[])
    {
        Msg msg = new Msg(abyte0);
        return base.send(msg, 0);
    }

    public final boolean send(byte abyte0[], int i)
    {
        Msg msg = new Msg(abyte0);
        return base.send(msg, i);
    }

    public final boolean sendMore(String s)
    {
        Msg msg = new Msg(s);
        return base.send(msg, 2);
    }

    public final boolean sendMore(base base1)
    {
        return base.send(ess._mth100(base1), 2);
    }

    public final boolean sendMore(byte abyte0[])
    {
        Msg msg = new Msg(abyte0);
        return base.send(msg, 2);
    }

    public final void setAffinity(long l)
    {
        base.setsockopt(4, Long.valueOf(l));
        mayRaise();
    }

    public final void setBacklog(long l)
    {
        base.setsockopt(19, Integer.valueOf((int)l));
        mayRaise();
    }

    public final void setDecoder(Class class1)
    {
        base.setsockopt(1002, class1);
    }

    public final void setEncoder(Class class1)
    {
        base.setsockopt(1001, class1);
    }

    public final void setHWM(long l)
    {
        setSndHWM(l);
        setRcvHWM(l);
    }

    public final void setIdentity(String s)
    {
        setIdentity(s.getBytes());
    }

    public final void setIdentity(byte abyte0[])
    {
        base.setsockopt(5, abyte0);
        mayRaise();
    }

    public final void setLinger(long l)
    {
        base.setsockopt(17, Integer.valueOf((int)l));
    }

    public final void setMaxMsgSize(long l)
    {
        base.setsockopt(22, Long.valueOf(l));
        mayRaise();
    }

    public final void setMulticastHops(long l)
    {
        base.setsockopt(25, Integer.valueOf((int)l));
        mayRaise();
    }

    public final void setMulticastLoop(boolean flag)
    {
    }

    public final void setRate(long l)
    {
        base.setsockopt(8, Integer.valueOf((int)l));
        mayRaise();
    }

    public final void setRcvHWM(long l)
    {
        base.setsockopt(24, Integer.valueOf((int)l));
        mayRaise();
    }

    public final void setReceiveBufferSize(long l)
    {
        base.setsockopt(12, Integer.valueOf((int)l));
        mayRaise();
    }

    public final void setReceiveTimeOut(int i)
    {
        base.setsockopt(27, Integer.valueOf(i));
        mayRaise();
    }

    public final void setReconnectIVL(long l)
    {
        base.setsockopt(18, Integer.valueOf((int)l));
        mayRaise();
    }

    public final void setReconnectIVLMax(long l)
    {
        base.setsockopt(21, Integer.valueOf((int)l));
        mayRaise();
    }

    public final void setRecoveryInterval(long l)
    {
        base.setsockopt(9, Integer.valueOf((int)l));
        mayRaise();
    }

    public final void setRouterMandatory(boolean flag)
    {
        SocketBase socketbase = base;
        int i;
        if (flag)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        socketbase.setsockopt(33, Integer.valueOf(i));
    }

    public final void setSendBufferSize(long l)
    {
        base.setsockopt(11, Integer.valueOf((int)l));
        mayRaise();
    }

    public final void setSendTimeOut(int i)
    {
        base.setsockopt(28, Integer.valueOf(i));
        mayRaise();
    }

    public final void setSndHWM(long l)
    {
        base.setsockopt(23, Integer.valueOf((int)l));
        mayRaise();
    }

    public final void setSwap(long l)
    {
    }

    public final void subscribe(String s)
    {
        subscribe(s.getBytes());
    }

    public final void subscribe(byte abyte0[])
    {
        base.setsockopt(6, abyte0);
        mayRaise();
    }

    public final void unsubscribe(String s)
    {
        unsubscribe(s.getBytes());
    }

    public final void unsubscribe(byte abyte0[])
    {
        base.setsockopt(7, abyte0);
        mayRaise();
    }


    protected ( , int i)
    {
        ctx = .access._mth000();
        base = ctx.create_socket(i);
        mayRaise();
    }

    protected mayRaise(SocketBase socketbase)
    {
        ctx = null;
        base = socketbase;
    }
}
