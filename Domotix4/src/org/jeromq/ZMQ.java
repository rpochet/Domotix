// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import zmq.Ctx;
import zmq.PollItem;
import zmq.SocketBase;

// Referenced classes of package org.jeromq:
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
            Error aerror[] = (Error[])org/jeromq/ZMQ$Error.getEnumConstants();
            int j = aerror.length;
            for (int k = 0; k < j; k++)
            {
                Error error = aerror[k];
                if (error.getCode() == (long)i)
                {
                    return error;
                }
            }

            throw new IllegalArgumentException((new StringBuilder()).append("Unknown ").append(org/jeromq/ZMQ$Error.getName()).append(" enum code:").append(i).toString());
        }

        public static Error valueOf(String s)
        {
            return (Error)Enum.valueOf(org/jeromq/ZMQ$Error, s);
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

    public static class Msg
    {

        public static final int MORE = 1;
        private final zmq.Msg base;

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

        public void put(Msg msg, int i)
        {
            base.put(msg.base, i);
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


        public Msg()
        {
            base = new zmq.Msg();
        }

        public Msg(int i)
        {
            base = new zmq.Msg(i);
        }

        public Msg(String s)
        {
            base = new zmq.Msg(s);
        }

        public Msg(ByteBuffer bytebuffer)
        {
            base = new zmq.Msg(bytebuffer);
        }

        public Msg(zmq.Msg msg)
        {
            base = msg;
        }
    }

    public static class PollItem
    {

        private final zmq.PollItem base;

        public final zmq.PollItem base()
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


        public PollItem(SelectableChannel selectablechannel, int i)
        {
            base = new zmq.PollItem(selectablechannel, i);
        }

        public PollItem(Socket socket, int i)
        {
            base = new zmq.PollItem(socket.base, i);
        }
    }

    public static class Poller
    {

        public static final int POLLERR = 4;
        public static final int POLLIN = 1;
        public static final int POLLOUT = 2;
        private static final int SIZE_DEFAULT = 32;
        private static final int SIZE_INCREMENT = 16;
        private static final ThreadLocal holder = new ThreadLocal();
        private zmq.PollItem items[];
        private int next;
        private Selector selector;
        private long timeout;

        private int insert(zmq.PollItem pollitem)
        {
            int i = next;
            next = i + 1;
            if (i == items.length)
            {
                zmq.PollItem apollitem[] = new zmq.PollItem[16 + items.length];
                System.arraycopy(items, 0, apollitem, 0, items.length);
                items = apollitem;
            }
            items[i] = pollitem;
            return i;
        }

        private void open()
        {
            if (holder.get() == null)
            {
                synchronized (holder)
                {
                    if (selector == null)
                    {
                        ReuseableSelector reuseableselector = new ReuseableSelector();
                        selector = reuseableselector.open();
                        holder.set(reuseableselector);
                    }
                }
            }
            if (false) goto _L2; else goto _L1
_L2:
            threadlocal;
            JVM INSTR monitorenter ;
_L1:
            selector = ((ReuseableSelector)holder.get()).get();
            return;
            IOException ioexception;
            ioexception;
            throw new zmq.ZError.IOException(ioexception);
            exception;
            threadlocal;
            JVM INSTR monitorexit ;
            throw exception;
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
                return new Socket(items[i].getSocket());
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
            return zmq.ZMQ.zmq_poll(selector, items, l);
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
            return insert(new zmq.PollItem(selectablechannel, i));
        }

        public int register(Socket socket)
        {
            return register(socket, 7);
        }

        public int register(Socket socket, int i)
        {
            return insert(new zmq.PollItem(socket.base, i));
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
                        if (items[i].getSocket() != socket.base)
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


        protected Poller(Context context1)
        {
            this(context1, 32);
        }

        protected Poller(Context context1, int i)
        {
            items = new zmq.PollItem[i];
            timeout = -1L;
            next = 0;
            open();
        }
    }

    protected static class ReuseableSelector
    {

        static final boolean $assertionsDisabled;
        private Selector selector;

        public void finalize()
        {
            try
            {
                selector.close();
            }
            catch (IOException ioexception) { }
            try
            {
                super.finalize();
                return;
            }
            catch (Throwable throwable)
            {
                return;
            }
        }

        public Selector get()
        {
            if (!$assertionsDisabled && selector == null)
            {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && !selector.isOpen())
            {
                throw new AssertionError();
            } else
            {
                return selector;
            }
        }

        public Selector open()
            throws IOException
        {
            selector = Selector.open();
            return selector;
        }

        static 
        {
            boolean flag;
            if (!org/jeromq/ZMQ.desiredAssertionStatus())
            {
                flag = true;
            } else
            {
                flag = false;
            }
            $assertionsDisabled = flag;
        }

        protected ReuseableSelector()
        {
            selector = null;
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
                throw new ZMQException.CtxTerminated();
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
                Msg msg = recvMsg(0);
                PrintStream printstream = System.out;
                Object aobj[] = new Object[2];
                aobj[0] = Integer.valueOf(msg.size());
                String s;
                if (msg.size() > 0)
                {
                    s = new String(msg.data());
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
            zmq.Msg msg = base.recv(k);
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
            zmq.Msg msg = base.recv(i);
            if (msg != null)
            {
                return msg.data();
            } else
            {
                return null;
            }
        }

        public final Msg recvMsg()
        {
            return recvMsg(0);
        }

        public final Msg recvMsg(int i)
        {
            zmq.Msg msg = base.recv(i);
            if (msg != null)
            {
                return new Msg(msg);
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
            zmq.Msg msg = base.recv(i);
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
            zmq.Msg msg = new zmq.Msg(s);
            return base.send(msg, 0);
        }

        public final boolean send(String s, int i)
        {
            zmq.Msg msg = new zmq.Msg(s);
            return base.send(msg, i);
        }

        public final boolean send(Msg msg)
        {
            return base.send(msg.base, 0);
        }

        public final boolean send(Msg msg, int i)
        {
            return base.send(msg.base, i);
        }

        public final boolean send(byte abyte0[])
        {
            zmq.Msg msg = new zmq.Msg(abyte0);
            return base.send(msg, 0);
        }

        public final boolean send(byte abyte0[], int i)
        {
            zmq.Msg msg = new zmq.Msg(abyte0);
            return base.send(msg, i);
        }

        public final boolean sendMore(String s)
        {
            zmq.Msg msg = new zmq.Msg(s);
            return base.send(msg, 2);
        }

        public final boolean sendMore(Msg msg)
        {
            return base.send(msg.base, 2);
        }

        public final boolean sendMore(byte abyte0[])
        {
            zmq.Msg msg = new zmq.Msg(abyte0);
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


        protected Socket(Context context1, int i)
        {
            ctx = context1.ctx;
            base = ctx.create_socket(i);
            mayRaise();
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
    public static final int POLLERR = 4;
    public static final int POLLIN = 1;
    public static final int POLLOUT = 2;
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

    public static Context context()
    {
        return new Context(1);
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

    public static int poll(PollItem apollitem[], long l)
    {
        zmq.PollItem apollitem1[] = new zmq.PollItem[apollitem.length];
        for (int i = 0; i < apollitem.length; i++)
        {
            apollitem1[i] = apollitem[i].base;
        }

        return zmq.ZMQ.zmq_poll(apollitem1, l);
    }
}
