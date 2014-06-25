// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.zeromq;

import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import zmq.Ctx;
import zmq.Msg;
import zmq.PollItem;
import zmq.SocketBase;

// Referenced classes of package org.zeromq:
//            ZMQException

public class ZMQ
{
    public static class Context
    {

        private final Ctx ctx;

        public Poller poller()
        {
            return new Poller(this);
        }

        public Poller poller(int i)
        {
            return new Poller(this, i);
        }

        public Socket socket(int i)
        {
            return new Socket(this, i);
        }

        public void term()
        {
            ctx.terminate();
        }


        protected Context(int i)
        {
            ctx = zmq.ZMQ.zmq_init(i);
        }
    }

    public static final class Error extends Enum
    {

        private static final Error $VALUES[];
        public static final Error EADDRINUSE;
        public static final Error EADDRNOTAVAIL;
        public static final Error ECONNREFUSED;
        public static final Error EFSM;
        public static final Error EINPROGRESS;
        public static final Error EMTHREAD;
        public static final Error ENETDOWN;
        public static final Error ENOBUFS;
        public static final Error ENOCOMPATPROTO;
        public static final Error ENOTSUP;
        public static final Error EPROTONOSUPPORT;
        public static final Error ETERM;
        private final long code;

        public static Error findByCode(int i)
        {
            Error aerror[] = (Error[])org/zeromq/ZMQ$Error.getEnumConstants();
            int j = aerror.length;
            for (int k = 0; k < j; k++)
            {
                Error error = aerror[k];
                if (error.getCode() == (long)i)
                {
                    return error;
                }
            }

            throw new IllegalArgumentException((new StringBuilder()).append("Unknown ").append(org/zeromq/ZMQ$Error.getName()).append(" enum code:").append(i).toString());
        }

        public static Error valueOf(String s)
        {
            return (Error)Enum.valueOf(org/zeromq/ZMQ$Error, s);
        }

        public static Error[] values()
        {
            return (Error[])$VALUES.clone();
        }

        public long getCode()
        {
            return code;
        }

        static 
        {
            ENOTSUP = new Error("ENOTSUP", 0, 45L);
            EPROTONOSUPPORT = new Error("EPROTONOSUPPORT", 1, 43L);
            ENOBUFS = new Error("ENOBUFS", 2, 55L);
            ENETDOWN = new Error("ENETDOWN", 3, 50L);
            EADDRINUSE = new Error("EADDRINUSE", 4, 48L);
            EADDRNOTAVAIL = new Error("EADDRNOTAVAIL", 5, 49L);
            ECONNREFUSED = new Error("ECONNREFUSED", 6, 61L);
            EINPROGRESS = new Error("EINPROGRESS", 7, 36L);
            EMTHREAD = new Error("EMTHREAD", 8, 0x9523dfeL);
            EFSM = new Error("EFSM", 9, 0x9523dfbL);
            ENOCOMPATPROTO = new Error("ENOCOMPATPROTO", 10, 0x9523dfcL);
            ETERM = new Error("ETERM", 11, 0x9523dfdL);
            Error aerror[] = new Error[12];
            aerror[0] = ENOTSUP;
            aerror[1] = EPROTONOSUPPORT;
            aerror[2] = ENOBUFS;
            aerror[3] = ENETDOWN;
            aerror[4] = EADDRINUSE;
            aerror[5] = EADDRNOTAVAIL;
            aerror[6] = ECONNREFUSED;
            aerror[7] = EINPROGRESS;
            aerror[8] = EMTHREAD;
            aerror[9] = EFSM;
            aerror[10] = ENOCOMPATPROTO;
            aerror[11] = ETERM;
            $VALUES = aerror;
        }

        private Error(String s, int i, long l)
        {
            super(s, i);
            code = l;
        }
    }

    public static class PollItem
    {

        private final zmq.PollItem base;
        private final Socket socket;

        protected final zmq.PollItem base()
        {
            return base;
        }

        public boolean equals(Object obj)
        {
            if (obj instanceof PollItem)
            {
                PollItem pollitem = (PollItem)obj;
                if (socket != null && socket == pollitem.socket)
                {
                    return true;
                }
                if (getRawSocket() != null && getRawSocket() == pollitem.getRawSocket())
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

        public final Socket getSocket()
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


        public PollItem(SelectableChannel selectablechannel, int i)
        {
            base = new zmq.PollItem(selectablechannel, i);
            socket = null;
        }

        public PollItem(Socket socket1, int i)
        {
            socket = socket1;
            base = new zmq.PollItem(socket1.base, i);
        }
    }

    public static class Poller
    {

        public static final int POLLERR = 4;
        public static final int POLLIN = 1;
        public static final int POLLOUT = 2;
        private static final int SIZE_DEFAULT = 32;
        private static final int SIZE_INCREMENT = 16;
        private PollItem items[];
        private int next;
        private long timeout;

        private int insert(PollItem pollitem)
        {
            int i = next;
            next = i + 1;
            if (i == items.length)
            {
                PollItem apollitem[] = new PollItem[16 + items.length];
                System.arraycopy(items, 0, apollitem, 0, items.length);
                items = apollitem;
            }
            items[i] = pollitem;
            return i;
        }

        private void remove(int i)
        {
            next = -1 + next;
            if (i != next)
            {
                items[i] = items[next];
            }
            items[next] = null;
        }

        public PollItem getItem(int i)
        {
            if (i < 0 || i >= next)
            {
                return null;
            } else
            {
                return items[i];
            }
        }

        public int getNext()
        {
            return next;
        }

        public int getSize()
        {
            return items.length;
        }

        public Socket getSocket(int i)
        {
            if (i < 0 || i >= next)
            {
                return null;
            } else
            {
                return items[i].getSocket();
            }
        }

        public long getTimeout()
        {
            return timeout;
        }

        public int poll()
        {
            long l = -1L;
            if (timeout > -1L)
            {
                l = timeout;
            }
            return poll(l);
        }

        public int poll(long l)
        {
            zmq.PollItem apollitem[] = new zmq.PollItem[next];
            for (int i = 0; i < next; i++)
            {
                apollitem[i] = items[i].base();
            }

            return zmq.ZMQ.zmq_poll(apollitem, next, l);
        }

        public boolean pollerr(int i)
        {
            if (i < 0 || i >= next)
            {
                return false;
            } else
            {
                return items[i].isError();
            }
        }

        public boolean pollin(int i)
        {
            if (i < 0 || i >= next)
            {
                return false;
            } else
            {
                return items[i].isReadable();
            }
        }

        public boolean pollout(int i)
        {
            if (i < 0 || i >= next)
            {
                return false;
            } else
            {
                return items[i].isWritable();
            }
        }

        public int register(SelectableChannel selectablechannel, int i)
        {
            return insert(new PollItem(selectablechannel, i));
        }

        public int register(PollItem pollitem)
        {
            return insert(pollitem);
        }

        public int register(Socket socket)
        {
            return register(socket, 7);
        }

        public int register(Socket socket, int i)
        {
            return insert(new PollItem(socket, i));
        }

        public void setTimeout(long l)
        {
            if (l < -1L)
            {
                return;
            } else
            {
                timeout = l;
                return;
            }
        }

        public void unregister(SelectableChannel selectablechannel)
        {
            int i = 0;
            do
            {
label0:
                {
                    if (i < items.length)
                    {
                        if (items[i].getRawSocket() != selectablechannel)
                        {
                            break label0;
                        }
                        remove(i);
                    }
                    return;
                }
                i++;
            } while (true);
        }

        public void unregister(Socket socket)
        {
            int i = 0;
            do
            {
label0:
                {
                    if (i < items.length)
                    {
                        if (items[i].getSocket() != socket)
                        {
                            break label0;
                        }
                        remove(i);
                    }
                    return;
                }
                i++;
            } while (true);
        }

        public Poller(int i)
        {
            this(null, i);
        }

        protected Poller(Context context1)
        {
            this(context1, 32);
        }

        protected Poller(Context context1, int i)
        {
            items = new PollItem[i];
            timeout = -1L;
            next = 0;
        }
    }

    public static class Socket
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

            } else
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
            }
            return -1;
        }

        private void mayRaise()
        {
            int i = base.errno();
            if (i != 0 && i != 35)
            {
                throw new ZMQException(i);
            } else
            {
                return;
            }
        }

        private void setsockopt(int i, Object obj)
        {
            try
            {
                base.setsockopt(i, obj);
                return;
            }
            catch (zmq.ZError.CtxTerminatedException ctxterminatedexception)
            {
                return;
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

        public final void connect(String s)
        {
            base.connect(s);
        }

        public final boolean disconnect(String s)
        {
            return base.term_endpoint(s);
        }

        public final long getAffinity()
        {
            return ((Long)base.getsockoptx(4)).longValue();
        }

        public final long getBacklog()
        {
            return (long)base.getsockopt(19);
        }

        public boolean getDelayAttachOnConnect()
        {
            return base.getsockopt(39) == 1;
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

        public final boolean getIPv4Only()
        {
            return base.getsockopt(31) == 1;
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

        public int getTCPKeepAlive()
        {
            return base.getsockopt(34);
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
                int l = Math.min(msg.size(), j);
                System.arraycopy(msg.data(), 0, abyte0, i, l);
                return l;
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
                mayRaise();
                return null;
            }
        }

        public final int recvByteBuffer(ByteBuffer bytebuffer, int i)
        {
            Msg msg = base.recv(i);
            if (msg != null)
            {
                bytebuffer.put(msg.data());
                return msg.size();
            } else
            {
                mayRaise();
                return -1;
            }
        }

        public final String recvStr()
        {
            return recvStr(0);
        }

        public final String recvStr(int i)
        {
            byte abyte0[] = recv(i);
            if (abyte0 != null)
            {
                return new String(abyte0);
            } else
            {
                return null;
            }
        }

        public final boolean send(String s)
        {
            return send(s.getBytes(), 0);
        }

        public final boolean send(String s, int i)
        {
            return send(s.getBytes(), i);
        }

        public final boolean send(byte abyte0[])
        {
            return send(abyte0, 0);
        }

        public final boolean send(byte abyte0[], int i)
        {
            Msg msg = new Msg(abyte0);
            if (base.send(msg, i))
            {
                return true;
            } else
            {
                mayRaise();
                return false;
            }
        }

        public final boolean sendByteBuffer(ByteBuffer bytebuffer, int i)
        {
            Msg msg = new Msg(bytebuffer);
            if (base.send(msg, i))
            {
                return true;
            } else
            {
                mayRaise();
                return false;
            }
        }

        public final boolean sendMore(String s)
        {
            return send(s.getBytes(), 2);
        }

        public final boolean sendMore(byte abyte0[])
        {
            return send(abyte0, 2);
        }

        public final void setAffinity(long l)
        {
            setsockopt(4, Long.valueOf(l));
        }

        public final void setBacklog(long l)
        {
            setsockopt(19, Integer.valueOf((int)l));
        }

        public final void setDecoder(Class class1)
        {
            base.setsockopt(1002, class1);
        }

        public void setDelayAttachOnConnect(boolean flag)
        {
            int i;
            if (flag)
            {
                i = 1;
            } else
            {
                i = 0;
            }
            setsockopt(39, Integer.valueOf(i));
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

        public void setIPv4Only(boolean flag)
        {
            int i;
            if (flag)
            {
                i = 1;
            } else
            {
                i = 0;
            }
            setsockopt(31, Integer.valueOf(i));
        }

        public final void setIdentity(byte abyte0[])
        {
            setsockopt(5, abyte0);
        }

        public final void setLinger(long l)
        {
            base.setsockopt(17, Integer.valueOf((int)l));
        }

        public final void setMaxMsgSize(long l)
        {
            setsockopt(22, Long.valueOf(l));
        }

        public final void setMulticastHops(long l)
        {
            throw new UnsupportedOperationException();
        }

        public final void setMulticastLoop(boolean flag)
        {
            throw new UnsupportedOperationException();
        }

        public final void setRate(long l)
        {
            throw new UnsupportedOperationException();
        }

        public final void setRcvHWM(long l)
        {
            setsockopt(24, Integer.valueOf((int)l));
        }

        public final void setReceiveBufferSize(long l)
        {
            setsockopt(12, Integer.valueOf((int)l));
        }

        public final void setReceiveTimeOut(int i)
        {
            setsockopt(27, Integer.valueOf(i));
        }

        public final void setReconnectIVL(long l)
        {
            base.setsockopt(18, Integer.valueOf((int)l));
        }

        public final void setReconnectIVLMax(long l)
        {
            setsockopt(21, Integer.valueOf((int)l));
        }

        public final void setRecoveryInterval(long l)
        {
            throw new UnsupportedOperationException();
        }

        public final void setRouterMandatory(boolean flag)
        {
            int i;
            if (flag)
            {
                i = 1;
            } else
            {
                i = 0;
            }
            setsockopt(33, Integer.valueOf(i));
        }

        public final void setSendBufferSize(long l)
        {
            setsockopt(11, Integer.valueOf((int)l));
        }

        public final void setSendTimeOut(int i)
        {
            setsockopt(28, Integer.valueOf(i));
        }

        public final void setSndHWM(long l)
        {
            setsockopt(23, Integer.valueOf((int)l));
        }

        public final void setSwap(long l)
        {
            throw new UnsupportedOperationException();
        }

        public void setTCPKeepAlive(int i)
        {
            setsockopt(34, Integer.valueOf(i));
        }

        public final void setXpubVerbose(boolean flag)
        {
            int i;
            if (flag)
            {
                i = 1;
            } else
            {
                i = 0;
            }
            setsockopt(40, Integer.valueOf(i));
        }

        public final void subscribe(byte abyte0[])
        {
            setsockopt(6, abyte0);
        }

        public final void unsubscribe(byte abyte0[])
        {
            setsockopt(7, abyte0);
        }


        protected Socket(Context context1, int i)
        {
            ctx = context1.ctx;
            base = ctx.create_socket(i);
        }

        protected Socket(SocketBase socketbase)
        {
            ctx = null;
            base = socketbase;
        }
    }


    public static final int DEALER = 5;
    public static final int DONTWAIT = 1;
    public static final int DOWNSTREAM = 8;
    public static final int EVENT_ACCEPTED = 32;
    public static final int EVENT_ACCEPT_FAILED = 64;
    public static final int EVENT_ALL = 1023;
    public static final int EVENT_BIND_FAILED = 16;
    public static final int EVENT_CLOSED = 128;
    public static final int EVENT_CLOSE_FAILED = 256;
    public static final int EVENT_CONNECTED = 1;
    public static final int EVENT_CONNECT_FAILED = 1024;
    public static final int EVENT_DELAYED = 2;
    public static final int EVENT_DISCONNECTED = 512;
    public static final int EVENT_LISTENING = 8;
    public static final int EVENT_RETRIED = 4;
    public static final int FORWARDER = 2;
    public static final int NOBLOCK = 1;
    public static final int PAIR = 0;
    public static final int PUB = 1;
    public static final int PULL = 7;
    public static final int PUSH = 8;
    public static final int QUEUE = 3;
    public static final int REP = 4;
    public static final int REQ = 3;
    public static final int ROUTER = 6;
    public static final int SNDMORE = 2;
    public static final int STREAMER = 1;
    public static final int SUB = 2;
    public static final int UPSTREAM = 7;
    public static final int XPUB = 9;
    public static final int XREP = 6;
    public static final int XREQ = 5;
    public static final int XSUB = 10;

    public ZMQ()
    {
    }

    public static Context context(int i)
    {
        return new Context(i);
    }

    public static boolean device(int i, Socket socket, Socket socket1)
    {
        return zmq.ZMQ.zmq_proxy(socket.base, socket1.base, null);
    }

    public static int getFullVersion()
    {
        return zmq.ZMQ.ZMQ_MAKE_VERSION(3, 2, 2);
    }

    public static int getMajorVersion()
    {
        return 3;
    }

    public static int getMinorVersion()
    {
        return 2;
    }

    public static int getPatchVersion()
    {
        return 2;
    }

    public static String getVersionString()
    {
        return "3.2.2";
    }

    public static int makeVersion(int i, int j, int k)
    {
        return zmq.ZMQ.ZMQ_MAKE_VERSION(i, j, k);
    }

    public static int poll(PollItem apollitem[], int i, long l)
    {
        zmq.PollItem apollitem1[] = new zmq.PollItem[i];
        for (int j = 0; j < i; j++)
        {
            apollitem1[j] = apollitem[j].base;
        }

        return zmq.ZMQ.zmq_poll(apollitem1, i, l);
    }

    public static int poll(PollItem apollitem[], long l)
    {
        return poll(apollitem, apollitem.length, l);
    }

    public static boolean proxy(Socket socket, Socket socket1, Socket socket2)
    {
        SocketBase socketbase = socket.base;
        SocketBase socketbase1 = socket1.base;
        SocketBase socketbase2;
        if (socket2 != null)
        {
            socketbase2 = socket2.base;
        } else
        {
            socketbase2 = null;
        }
        return zmq.ZMQ.zmq_proxy(socketbase, socketbase1, socketbase2);
    }
}
