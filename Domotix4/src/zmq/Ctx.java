// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Referenced classes of package zmq:
//            Mailbox, IOThread, Reaper, SocketBase, 
//            Options, Command, ZObject

public class Ctx
{
    public static class Endpoint
    {

        Options options;
        SocketBase socket;

        public Endpoint(SocketBase socketbase, Options options1)
        {
            socket = socketbase;
            options = options1;
        }
    }


    static final boolean $assertionsDisabled = false;
    private static AtomicInteger max_socket_id = new AtomicInteger(0);
    public static final int reaper_tid = 1;
    public static final int term_tid;
    private final Deque empty_slots = new ArrayDeque();
    private final Map endpoints = new HashMap();
    private final Lock endpoints_sync = new ReentrantLock();
    private int io_thread_count;
    private final List io_threads = new ArrayList();
    private int max_sockets;
    private final Lock opt_sync = new ReentrantLock();
    private Reaper reaper;
    private int slot_count;
    private final Lock slot_sync = new ReentrantLock();
    private Mailbox slots[];
    private final List sockets = new ArrayList();
    private volatile boolean starting;
    private int tag;
    private final Mailbox term_mailbox = new Mailbox("terminater");
    private boolean terminating;

    public Ctx()
    {
        tag = 0xabadcafe;
        starting = true;
        terminating = false;
        reaper = null;
        slot_count = 0;
        slots = null;
        max_sockets = 1024;
        io_thread_count = 1;
    }

    public boolean check_tag()
    {
        return tag == 0xabadcafe;
    }

    public IOThread choose_io_thread(long l)
    {
        IOThread iothread;
        if (io_threads.isEmpty())
        {
            iothread = null;
        } else
        {
            int i = -1;
            iothread = null;
            int j = 0;
            while (j != io_threads.size()) 
            {
                if (l == 0L || (l & 1L << j) > 0L)
                {
                    int k = ((IOThread)io_threads.get(j)).get_load();
                    if (iothread == null || k < i)
                    {
                        i = k;
                        iothread = (IOThread)io_threads.get(j);
                    }
                }
                j++;
            }
        }
        return iothread;
    }

    public SocketBase create_socket(int i)
    {
        slot_sync.lock();
        if (!starting) goto _L2; else goto _L1
_L1:
        int l;
        starting = false;
        opt_sync.lock();
        int k = max_sockets;
        l = io_thread_count;
        opt_sync.unlock();
        slot_count = 2 + (k + l);
        slots = new Mailbox[slot_count];
        slots[0] = term_mailbox;
        reaper = new Reaper(this, 1);
        slots[1] = reaper.get_mailbox();
        reaper.start();
        int i1 = 2;
_L4:
        if (i1 == l + 2)
        {
            break; /* Loop/switch isn't completed */
        }
        IOThread iothread = new IOThread(this, i1);
        io_threads.add(iothread);
        slots[i1] = iothread.get_mailbox();
        iothread.start();
        i1++;
        if (true) goto _L4; else goto _L3
_L3:
        int j1 = -1 + slot_count;
_L5:
        if (j1 < l + 2)
        {
            break; /* Loop/switch isn't completed */
        }
        empty_slots.add(Integer.valueOf(j1));
        slots[j1] = null;
        j1--;
        if (true) goto _L5; else goto _L2
_L2:
        if (terminating)
        {
            throw new ZError.CtxTerminatedException();
        }
        break MISSING_BLOCK_LABEL_248;
        Exception exception;
        exception;
        slot_sync.unlock();
        throw exception;
        int j;
        SocketBase socketbase;
        if (empty_slots.isEmpty())
        {
            throw new IllegalStateException("EMFILE");
        }
        j = ((Integer)empty_slots.pollLast()).intValue();
        socketbase = SocketBase.create(i, this, j, max_socket_id.incrementAndGet());
        if (socketbase != null)
        {
            break MISSING_BLOCK_LABEL_329;
        }
        empty_slots.addLast(Integer.valueOf(j));
        slot_sync.unlock();
        return null;
        sockets.add(socketbase);
        slots[j] = socketbase.get_mailbox();
        slot_sync.unlock();
        return socketbase;
    }

    protected void destroy()
    {
        for (Iterator iterator = io_threads.iterator(); iterator.hasNext(); ((IOThread)iterator.next()).stop()) { }
        for (Iterator iterator1 = io_threads.iterator(); iterator1.hasNext(); ((IOThread)iterator1.next()).destroy()) { }
        if (reaper != null)
        {
            reaper.destroy();
        }
        term_mailbox.close();
        tag = 0xdeadbeef;
    }

    public void destroy_socket(SocketBase socketbase)
    {
        slot_sync.lock();
        int i = socketbase.get_tid();
        empty_slots.add(Integer.valueOf(i));
        slots[i].close();
        slots[i] = null;
        sockets.remove(socketbase);
        if (terminating && sockets.isEmpty())
        {
            reaper.stop();
        }
        slot_sync.unlock();
        return;
        Exception exception;
        exception;
        slot_sync.unlock();
        throw exception;
    }

    public Endpoint find_endpoint(String s)
    {
        endpoints_sync.lock();
        Endpoint endpoint = (Endpoint)endpoints.get(s);
        if (endpoint != null)
        {
            break MISSING_BLOCK_LABEL_56;
        }
        Endpoint endpoint1 = new Endpoint(null, new Options());
        endpoints_sync.unlock();
        return endpoint1;
        endpoint.socket.inc_seqnum();
        endpoints_sync.unlock();
        return endpoint;
        Exception exception;
        exception;
        endpoints_sync.unlock();
        throw exception;
    }

    public int get(int i)
    {
        if (i == 2)
        {
            return max_sockets;
        }
        if (i == 1)
        {
            return io_thread_count;
        } else
        {
            throw new IllegalArgumentException((new StringBuilder()).append("option = ").append(i).toString());
        }
    }

    public ZObject get_reaper()
    {
        return reaper;
    }

    public boolean register_endpoint(String s, Endpoint endpoint)
    {
        endpoints_sync.lock();
        Endpoint endpoint1 = (Endpoint)endpoints.put(s, endpoint);
        endpoints_sync.unlock();
        Exception exception;
        return endpoint1 == null;
        exception;
        endpoints_sync.unlock();
        throw exception;
    }

    public void send_command(int i, Command command)
    {
        slots[i].send(command);
    }

    public void set(int i, int j)
    {
        if (i == 2 && j >= 1)
        {
            opt_sync.lock();
            max_sockets = j;
            opt_sync.unlock();
            return;
        }
        if (i == 1 && j >= 0)
        {
            opt_sync.lock();
            io_thread_count = j;
            opt_sync.unlock();
            return;
        } else
        {
            throw new IllegalArgumentException((new StringBuilder()).append("option = ").append(i).toString());
        }
    }

    public void terminate()
    {
        int i;
        tag = 0xdeadbeef;
        slot_sync.lock();
        if (starting)
        {
            break MISSING_BLOCK_LABEL_215;
        }
        boolean flag = terminating;
        terminating = true;
        slot_sync.unlock();
        if (flag)
        {
            break MISSING_BLOCK_LABEL_119;
        }
        slot_sync.lock();
        i = 0;
_L2:
        if (i == sockets.size())
        {
            break; /* Loop/switch isn't completed */
        }
        ((SocketBase)sockets.get(i)).stop();
        i++;
        if (true) goto _L2; else goto _L1
_L1:
        if (sockets.isEmpty())
        {
            reaper.stop();
        }
        slot_sync.unlock();
        Command command;
        command = term_mailbox.recv(-1L);
        if (command == null)
        {
            throw new IllegalStateException();
        }
        break MISSING_BLOCK_LABEL_156;
        Exception exception;
        exception;
        slot_sync.unlock();
        throw exception;
        if (!$assertionsDisabled && command.type() != Command.Type.done)
        {
            throw new AssertionError();
        }
        slot_sync.lock();
        if (!$assertionsDisabled && !sockets.isEmpty())
        {
            throw new AssertionError();
        }
        slot_sync.unlock();
        destroy();
        return;
    }

    public void unregister_endpoints(SocketBase socketbase)
    {
        endpoints_sync.lock();
        Iterator iterator = endpoints.entrySet().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            if (((Endpoint)((java.util.Map.Entry)iterator.next()).getValue()).socket == socketbase)
            {
                iterator.remove();
            }
        } while (true);
        break MISSING_BLOCK_LABEL_78;
        Exception exception;
        exception;
        endpoints_sync.unlock();
        throw exception;
        endpoints_sync.unlock();
        return;
    }

    static 
    {
        boolean flag;
        if (!zmq/Ctx.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
