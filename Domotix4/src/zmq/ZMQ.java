// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

// Referenced classes of package zmq:
//            SocketBase, Ctx, Proxy, Msg, 
//            PollItem, Clock

public class ZMQ
{
    public static class Event
    {

        private static final int VALUE_CHANNEL = 2;
        private static final int VALUE_INTEGER = 1;
        public final String addr;
        private final Object arg;
        public final int event;
        private final int flag;

        public static Event read(SocketBase socketbase)
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
            return new Event(i, new String(abyte0), integer);
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

        public Event(int i, String s, Object obj)
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

    private static class PollSelector
    {

        static final boolean $assertionsDisabled;
        private Selector selector;

        public static Selector open()
            throws IOException
        {
            PollSelector pollselector = (PollSelector)ZMQ.POLL_SELECTOR.get();
            if (pollselector != null) goto _L2; else goto _L1
_L1:
            ThreadLocal threadlocal = ZMQ.POLL_SELECTOR;
            threadlocal;
            JVM INSTR monitorenter ;
            pollselector = (PollSelector)ZMQ.POLL_SELECTOR.get();
            if (pollselector != null)
            {
                break MISSING_BLOCK_LABEL_54;
            }
            PollSelector pollselector1 = new PollSelector(Selector.open());
            ZMQ.POLL_SELECTOR.set(pollselector1);
            pollselector = pollselector1;
            threadlocal;
            JVM INSTR monitorexit ;
_L2:
            return pollselector.get();
            IOException ioexception;
            ioexception;
_L4:
            throw new ZError.IOException(ioexception);
_L3:
            threadlocal;
            JVM INSTR monitorexit ;
            Exception exception;
            throw exception;
            exception;
              goto _L3
            ioexception;
              goto _L4
            exception;
              goto _L3
        }

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

        static 
        {
            boolean flag;
            if (!zmq/ZMQ.desiredAssertionStatus())
            {
                flag = true;
            } else
            {
                flag = false;
            }
            $assertionsDisabled = flag;
        }

        private PollSelector(Selector selector1)
        {
            selector = selector1;
        }
    }


    private static final ThreadLocal POLL_SELECTOR = new ThreadLocal();
    public static final int ZMQ_AFFINITY = 4;
    public static final int ZMQ_BACKLOG = 19;
    public static final int ZMQ_DEALER = 5;
    public static final int ZMQ_DECODER = 1002;
    public static final int ZMQ_DELAY_ATTACH_ON_CONNECT = 39;
    public static final int ZMQ_DONTWAIT = 1;
    public static final int ZMQ_ENCODER = 1001;
    public static final int ZMQ_EVENTS = 15;
    public static final int ZMQ_EVENT_ACCEPTED = 32;
    public static final int ZMQ_EVENT_ACCEPT_FAILED = 64;
    public static final int ZMQ_EVENT_ALL = 1023;
    public static final int ZMQ_EVENT_BIND_FAILED = 16;
    public static final int ZMQ_EVENT_CLOSED = 128;
    public static final int ZMQ_EVENT_CLOSE_FAILED = 256;
    public static final int ZMQ_EVENT_CONNECTED = 1;
    public static final int ZMQ_EVENT_CONNECT_DELAYED = 2;
    public static final int ZMQ_EVENT_CONNECT_FAILED = 1024;
    public static final int ZMQ_EVENT_CONNECT_RETRIED = 4;
    public static final int ZMQ_EVENT_DISCONNECTED = 512;
    public static final int ZMQ_EVENT_LISTENING = 8;
    public static final int ZMQ_FAIL_UNROUTABLE = 33;
    public static final int ZMQ_FD = 14;
    public static final int ZMQ_FORWARDER = 2;
    public static final int ZMQ_IDENTITY = 5;
    public static final int ZMQ_IO_THREADS = 1;
    public static final int ZMQ_IO_THREADS_DFLT = 1;
    public static final int ZMQ_IPV4ONLY = 31;
    public static final int ZMQ_LAST_ENDPOINT = 32;
    public static final int ZMQ_LINGER = 17;
    public static final int ZMQ_MAXMSGSIZE = 22;
    public static final int ZMQ_MAX_SOCKETS = 2;
    public static final int ZMQ_MAX_SOCKETS_DFLT = 1024;
    public static final int ZMQ_MORE = 1;
    public static final int ZMQ_MULTICAST_HOPS = 25;
    public static final int ZMQ_NOBLOCK = 1;
    public static final int ZMQ_PAIR = 0;
    public static final int ZMQ_POLLERR = 4;
    public static final int ZMQ_POLLIN = 1;
    public static final int ZMQ_POLLOUT = 2;
    public static final int ZMQ_PUB = 1;
    public static final int ZMQ_PULL = 7;
    public static final int ZMQ_PUSH = 8;
    public static final int ZMQ_QUEUE = 3;
    public static final int ZMQ_RATE = 8;
    public static final int ZMQ_RCVBUF = 12;
    public static final int ZMQ_RCVHWM = 24;
    public static final int ZMQ_RCVMORE = 13;
    public static final int ZMQ_RCVTIMEO = 27;
    public static final int ZMQ_RECONNECT_IVL = 18;
    public static final int ZMQ_RECONNECT_IVL_MAX = 21;
    public static final int ZMQ_RECOVERY_IVL = 9;
    public static final int ZMQ_REP = 4;
    public static final int ZMQ_REQ = 3;
    public static final int ZMQ_ROUTER = 6;
    public static final int ZMQ_ROUTER_BEHAVIOR = 33;
    public static final int ZMQ_ROUTER_MANDATORY = 33;
    public static final int ZMQ_SNDBUF = 11;
    public static final int ZMQ_SNDHWM = 23;
    public static final int ZMQ_SNDMORE = 2;
    public static final int ZMQ_SNDTIMEO = 28;
    public static final int ZMQ_STREAMER = 1;
    public static final int ZMQ_SUB = 2;
    public static final int ZMQ_SUBSCRIBE = 6;
    public static final int ZMQ_TCP_ACCEPT_FILTER = 38;
    public static final int ZMQ_TCP_KEEPALIVE = 34;
    public static final int ZMQ_TCP_KEEPALIVE_CNT = 35;
    public static final int ZMQ_TCP_KEEPALIVE_IDLE = 36;
    public static final int ZMQ_TCP_KEEPALIVE_INTVL = 37;
    public static final int ZMQ_TYPE = 16;
    public static final int ZMQ_UNSUBSCRIBE = 7;
    public static final int ZMQ_VERSION_MAJOR = 3;
    public static final int ZMQ_VERSION_MINOR = 2;
    public static final int ZMQ_VERSION_PATCH = 2;
    public static final int ZMQ_XPUB = 9;
    public static final int ZMQ_XPUB_VERBOSE = 40;
    public static final int ZMQ_XREP = 6;
    public static final int ZMQ_XREQ = 5;
    public static final int ZMQ_XSUB = 10;

    public ZMQ()
    {
    }

    public static int ZMQ_MAKE_VERSION(int i, int j, int k)
    {
        return k + (i * 10000 + j * 100);
    }

    public static Msg s_recvmsg(SocketBase socketbase, int i)
    {
        return socketbase.recv(i);
    }

    private static int s_sendmsg(SocketBase socketbase, Msg msg, int i)
    {
        int j = zmq_msg_size(msg);
        if (!socketbase.send(msg, i))
        {
            j = -1;
        }
        return j;
    }

    public static boolean zmq_bind(SocketBase socketbase, String s)
    {
        if (socketbase == null || !socketbase.check_tag())
        {
            throw new IllegalStateException();
        } else
        {
            return socketbase.bind(s);
        }
    }

    public static void zmq_close(SocketBase socketbase)
    {
        if (socketbase == null || !socketbase.check_tag())
        {
            throw new IllegalStateException();
        } else
        {
            socketbase.close();
            return;
        }
    }

    public static boolean zmq_connect(SocketBase socketbase, String s)
    {
        if (socketbase == null || !socketbase.check_tag())
        {
            throw new IllegalStateException();
        } else
        {
            return socketbase.connect(s);
        }
    }

    private static void zmq_ctx_destroy(Ctx ctx)
    {
        if (ctx == null || !ctx.check_tag())
        {
            throw new IllegalStateException();
        } else
        {
            ctx.terminate();
            return;
        }
    }

    public static int zmq_ctx_get(Ctx ctx, int i)
    {
        if (ctx == null || !ctx.check_tag())
        {
            throw new IllegalStateException();
        } else
        {
            return ctx.get(i);
        }
    }

    public static Ctx zmq_ctx_new()
    {
        return new Ctx();
    }

    public static void zmq_ctx_set(Ctx ctx, int i, int j)
    {
        if (ctx == null || !ctx.check_tag())
        {
            throw new IllegalStateException();
        } else
        {
            ctx.set(i, j);
            return;
        }
    }

    public static boolean zmq_device(int i, SocketBase socketbase, SocketBase socketbase1)
    {
        return Proxy.proxy(socketbase, socketbase1, null);
    }

    public static boolean zmq_disconnect(SocketBase socketbase, String s)
    {
        if (socketbase == null || !socketbase.check_tag())
        {
            throw new IllegalStateException();
        } else
        {
            return socketbase.term_endpoint(s);
        }
    }

    public static int zmq_getsockopt(SocketBase socketbase, int i)
    {
        return socketbase.getsockopt(i);
    }

    public static Object zmq_getsockoptx(SocketBase socketbase, int i)
    {
        if (socketbase == null || !socketbase.check_tag())
        {
            throw new IllegalStateException();
        } else
        {
            return socketbase.getsockoptx(i);
        }
    }

    public static Ctx zmq_init(int i)
    {
        if (i >= 0)
        {
            Ctx ctx = zmq_ctx_new();
            zmq_ctx_set(ctx, 1, i);
            return ctx;
        } else
        {
            throw new IllegalArgumentException("io_threds must not be negative");
        }
    }

    public static int zmq_msg_get(Msg msg)
    {
        return zmq_msg_get(msg, 1);
    }

    public static int zmq_msg_get(Msg msg, int i)
    {
        switch (i)
        {
        default:
            throw new IllegalArgumentException();

        case 1: // '\001'
            break;
        }
        return !msg.has_more() ? 0 : 1;
    }

    public static Msg zmq_msg_init()
    {
        return new Msg();
    }

    public static Msg zmq_msg_init_size(int i)
    {
        return new Msg(i);
    }

    public static int zmq_msg_size(Msg msg)
    {
        return msg.size();
    }

    public static int zmq_poll(Selector selector, PollItem apollitem[], int i, long l)
    {
        if (apollitem == null)
        {
            throw new IllegalArgumentException();
        }
        if (i != 0) goto _L2; else goto _L1
_L1:
        if (l != 0L) goto _L4; else goto _L3
_L3:
        int k = 0;
_L11:
        return k;
_L4:
        long l1;
        long l2;
        long l3;
        int j1;
        HashMap hashmap;
        Iterator iterator;
        int j;
        boolean flag;
        IOException ioexception;
        ZError.IOException ioexception1;
        int i1;
        Iterator iterator1;
        SelectionKey selectionkey;
        Iterator iterator2;
        PollItem pollitem;
        SelectableChannel selectablechannel;
        SelectionKey selectionkey1;
        ClosedChannelException closedchannelexception;
        ZError.IOException ioexception2;
        SelectionKey selectionkey2;
        try
        {
            Thread.sleep(l);
        }
        catch (InterruptedException interruptedexception) { }
        return 0;
_L2:
        l1 = 0L;
        l2 = 0L;
        hashmap = new HashMap();
        iterator = selector.keys().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            selectionkey2 = (SelectionKey)iterator.next();
            if (selectionkey2.isValid())
            {
                hashmap.put(selectionkey2.channel(), selectionkey2);
            }
        } while (true);
        j = 0;
        while (j < i) 
        {
            pollitem = apollitem[j];
            if (pollitem != null)
            {
                selectablechannel = pollitem.getChannel();
                selectionkey1 = (SelectionKey)hashmap.remove(selectablechannel);
                if (selectionkey1 != null)
                {
                    if (selectionkey1.interestOps() != pollitem.interestOps())
                    {
                        selectionkey1.interestOps(pollitem.interestOps());
                    }
                    selectionkey1.attach(pollitem);
                } else
                {
                    try
                    {
                        selectablechannel.register(selector, pollitem.interestOps(), pollitem);
                    }
                    // Misplaced declaration of an exception variable
                    catch (ClosedChannelException closedchannelexception)
                    {
                        ioexception2 = new ZError.IOException(closedchannelexception);
                        throw ioexception2;
                    }
                }
            }
            j++;
        }
        if (!hashmap.isEmpty())
        {
            for (iterator2 = hashmap.values().iterator(); iterator2.hasNext(); ((SelectionKey)iterator2.next()).cancel()) { }
        }
        flag = true;
        k = 0;
_L12:
        if (flag)
        {
            l3 = 0L;
        } else
        if (l < 0L)
        {
            l3 = -1L;
        } else
        {
            l3 = l2 - l1;
        }
        if (l3 >= 0L) goto _L6; else goto _L5
_L5:
        i1 = selector.select(0L);
_L9:
        iterator1 = selector.keys().iterator();
_L14:
        if (!iterator1.hasNext()) goto _L8; else goto _L7
_L7:
        selectionkey = (SelectionKey)iterator1.next();
        j1 = ((PollItem)selectionkey.attachment()).readyOps(selectionkey, i1);
        if (j1 < 0)
        {
            return -1;
        }
        break MISSING_BLOCK_LABEL_510;
_L6:
label0:
        {
            if (l3 != 0L)
            {
                break label0;
            }
            try
            {
                i1 = selector.selectNow();
            }
            // Misplaced declaration of an exception variable
            catch (IOException ioexception)
            {
                ioexception1 = new ZError.IOException(ioexception);
                throw ioexception1;
            }
        }
          goto _L9
        i1 = selector.select(l3);
          goto _L9
_L8:
        selector.selectedKeys().clear();
        if (l == 0L || k > 0) goto _L11; else goto _L10
_L10:
label1:
        {
            if (l >= 0L)
            {
                break label1;
            }
            if (flag)
            {
                flag = false;
            }
        }
          goto _L12
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_465;
        }
        l1 = Clock.now_ms();
        l2 = l1 + l;
        if (l1 == l2) goto _L11; else goto _L13
_L13:
        flag = false;
          goto _L12
        l1 = Clock.now_ms();
        if (l1 >= l2)
        {
            return k;
        }
          goto _L12
        if (j1 > 0)
        {
            k++;
        }
          goto _L14
    }

    public static int zmq_poll(Selector selector, PollItem apollitem[], long l)
    {
        return zmq_poll(selector, apollitem, apollitem.length, l);
    }

    public static int zmq_poll(PollItem apollitem[], int i, long l)
    {
        Selector selector;
        try
        {
            selector = PollSelector.open();
        }
        catch (IOException ioexception)
        {
            throw new ZError.IOException(ioexception);
        }
        return zmq_poll(selector, apollitem, i, l);
    }

    public static int zmq_poll(PollItem apollitem[], long l)
    {
        return zmq_poll(apollitem, apollitem.length, l);
    }

    public static boolean zmq_proxy(SocketBase socketbase, SocketBase socketbase1, SocketBase socketbase2)
    {
        if (socketbase == null || socketbase1 == null)
        {
            throw new IllegalArgumentException();
        } else
        {
            return Proxy.proxy(socketbase, socketbase1, socketbase2);
        }
    }

    public static Msg zmq_recv(SocketBase socketbase, int i)
    {
        if (socketbase == null || !socketbase.check_tag())
        {
            throw new IllegalStateException();
        }
        Msg msg = s_recvmsg(socketbase, i);
        if (msg == null)
        {
            msg = null;
        }
        return msg;
    }

    public static Msg zmq_recvmsg(SocketBase socketbase, int i)
    {
        return zmq_recv(socketbase, i);
    }

    public static int zmq_send(SocketBase socketbase, String s, int i)
    {
        byte abyte0[] = s.getBytes();
        return zmq_send(socketbase, abyte0, abyte0.length, i);
    }

    public static int zmq_send(SocketBase socketbase, Msg msg, int i)
    {
        int j = s_sendmsg(socketbase, msg, i);
        if (j < 0)
        {
            j = -1;
        }
        return j;
    }

    public static int zmq_send(SocketBase socketbase, byte abyte0[], int i, int j)
    {
        if (socketbase == null || !socketbase.check_tag())
        {
            throw new IllegalStateException();
        }
        Msg msg = new Msg(i);
        msg.put(abyte0, 0, i);
        int k = s_sendmsg(socketbase, msg, j);
        if (k < 0)
        {
            k = -1;
        }
        return k;
    }

    public static int zmq_sendmsg(SocketBase socketbase, Msg msg, int i)
    {
        return zmq_send(socketbase, msg, i);
    }

    public static void zmq_setsockopt(SocketBase socketbase, int i, Object obj)
    {
        if (socketbase == null || !socketbase.check_tag())
        {
            throw new IllegalStateException();
        } else
        {
            socketbase.setsockopt(i, obj);
            return;
        }
    }

    public static void zmq_sleep(int i)
    {
        long l = 1000L * (long)i;
        try
        {
            Thread.sleep(l);
            return;
        }
        catch (InterruptedException interruptedexception)
        {
            return;
        }
    }

    public static SocketBase zmq_socket(Ctx ctx, int i)
    {
        if (ctx == null || !ctx.check_tag())
        {
            throw new IllegalStateException();
        } else
        {
            return ctx.create_socket(i);
        }
    }

    public static boolean zmq_socket_monitor(SocketBase socketbase, String s, int i)
    {
        if (socketbase == null || !socketbase.check_tag())
        {
            throw new IllegalStateException();
        } else
        {
            return socketbase.monitor(s, i);
        }
    }

    public static long zmq_stopwatch_start()
    {
        return System.nanoTime();
    }

    public static long zmq_stopwatch_stop(long l)
    {
        return (System.nanoTime() - l) / 1000L;
    }

    public static String zmq_strerror(int i)
    {
        return (new StringBuilder()).append("Errno = ").append(i).toString();
    }

    public static void zmq_term(Ctx ctx)
    {
        zmq_ctx_destroy(ctx);
    }

    public static boolean zmq_unbind(SocketBase socketbase, String s)
    {
        if (socketbase == null || !socketbase.check_tag())
        {
            throw new IllegalStateException();
        } else
        {
            return socketbase.term_endpoint(s);
        }
    }

    public int zmq_recviov(SocketBase socketbase, byte abyte0[][], int i, int j)
    {
        if (socketbase == null || !socketbase.check_tag())
        {
            throw new IllegalStateException();
        }
        boolean flag = true;
        int k = 0;
        do
        {
            Msg msg;
label0:
            {
                byte byte0 = 0;
                if (flag)
                {
                    byte0 = 0;
                    if (k < i)
                    {
                        msg = s_recvmsg(socketbase, j);
                        if (msg != null)
                        {
                            break label0;
                        }
                        byte0 = -1;
                    }
                }
                return byte0;
            }
            abyte0[k] = msg.data();
            flag = msg.has_more();
            k++;
        } while (true);
    }

    public int zmq_sendiov(SocketBase socketbase, byte abyte0[][], int i, int j)
    {
        if (socketbase == null || !socketbase.check_tag())
        {
            throw new IllegalStateException();
        }
        int k = 0;
        int l = 0;
        do
        {
label0:
            {
                if (l < i)
                {
                    Msg msg = new Msg(abyte0[l]);
                    if (l == i - 1)
                    {
                        j &= -3;
                    }
                    k = s_sendmsg(socketbase, msg, j);
                    if (k >= 0)
                    {
                        break label0;
                    }
                    k = -1;
                }
                return k;
            }
            l++;
        } while (true);
    }


}
