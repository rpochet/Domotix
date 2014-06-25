// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

// Referenced classes of package zmq:
//            PollerBase, IPollEvents

public class Poller extends PollerBase
    implements Runnable
{
    private static class PollSet
    {

        protected boolean cancelled;
        protected IPollEvents handler;
        protected SelectionKey key;
        protected int ops;

        protected PollSet(IPollEvents ipollevents)
        {
            handler = ipollevents;
            key = null;
            cancelled = false;
            ops = 0;
        }
    }


    private final Map fd_table;
    private final String name;
    private boolean retired;
    private Selector selector;
    private volatile boolean stopped;
    private volatile boolean stopping;
    private Thread worker;

    public Poller()
    {
        this("poller");
    }

    public Poller(String s)
    {
        name = s;
        retired = false;
        stopping = false;
        stopped = false;
        fd_table = new HashMap();
        try
        {
            selector = Selector.open();
            return;
        }
        catch (IOException ioexception)
        {
            throw new ZError.IOException(ioexception);
        }
    }

    private void rebuildSelector()
    {
        Selector selector1;
        try
        {
            selector1 = Selector.open();
        }
        catch (IOException ioexception)
        {
            throw new ZError.IOException(ioexception);
        }
        try
        {
            selector.close();
        }
        catch (IOException ioexception1) { }
        selector = selector1;
        for (Iterator iterator = fd_table.values().iterator(); iterator.hasNext();)
        {
            ((PollSet)iterator.next()).key = null;
        }

        retired = true;
    }

    private final void register(SelectableChannel selectablechannel, int i, boolean flag)
    {
        PollSet pollset = (PollSet)fd_table.get(selectablechannel);
        if (flag)
        {
            pollset.ops = pollset.ops & ~i;
        } else
        {
            pollset.ops = i | pollset.ops;
        }
        if (pollset.key != null)
        {
            pollset.key.interestOps(pollset.ops);
            return;
        } else
        {
            retired = true;
            return;
        }
    }

    public final void add_fd(SelectableChannel selectablechannel, IPollEvents ipollevents)
    {
        fd_table.put(selectablechannel, new PollSet(ipollevents));
        adjust_load(1);
    }

    public void destroy()
    {
        if (!stopped)
        {
            try
            {
                worker.join();
            }
            catch (InterruptedException interruptedexception) { }
        }
        try
        {
            selector.close();
            return;
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
        }
    }

    public final void reset_pollin(SelectableChannel selectablechannel)
    {
        register(selectablechannel, 1, true);
    }

    public final void reset_pollout(SelectableChannel selectablechannel)
    {
        register(selectablechannel, 4, true);
    }

    public final void rm_fd(SelectableChannel selectablechannel)
    {
        ((PollSet)fd_table.get(selectablechannel)).cancelled = true;
        retired = true;
        adjust_load(-1);
    }

    public void run()
    {
        int i = 0;
_L9:
        if (stopping) goto _L2; else goto _L1
_L1:
        long l = execute_timers();
        if (retired)
        {
            Iterator iterator1 = fd_table.entrySet().iterator();
            do
            {
                if (!iterator1.hasNext())
                {
                    break;
                }
                java.util.Map.Entry entry = (java.util.Map.Entry)iterator1.next();
                SelectableChannel selectablechannel = (SelectableChannel)entry.getKey();
                PollSet pollset = (PollSet)entry.getValue();
                long l1;
                IOException ioexception;
                ZError.IOException ioexception1;
                int j;
                Iterator iterator;
                SelectionKey selectionkey;
                IPollEvents ipollevents;
                if (pollset.key == null)
                {
                    try
                    {
                        pollset.key = selectablechannel.register(selector, pollset.ops, pollset.handler);
                    }
                    catch (ClosedChannelException closedchannelexception) { }
                }
                if (pollset.cancelled || !selectablechannel.isOpen())
                {
                    if (pollset.key != null)
                    {
                        pollset.key.cancel();
                    }
                    iterator1.remove();
                }
            } while (true);
            retired = false;
        }
        l1 = System.currentTimeMillis();
        try
        {
            j = selector.select(l);
        }
        // Misplaced declaration of an exception variable
        catch (IOException ioexception)
        {
            ioexception1 = new ZError.IOException(ioexception);
            throw ioexception1;
        }
        if (j == 0)
        {
            if (l == 0L || System.currentTimeMillis() - l1 < l / 2L)
            {
                i++;
            } else
            {
                i = 0;
            }
            if (i > 10)
            {
                rebuildSelector();
                i = 0;
            }
            continue; /* Loop/switch isn't completed */
        }
        iterator = selector.selectedKeys().iterator();
_L7:
        if (!iterator.hasNext())
        {
            continue; /* Loop/switch isn't completed */
        }
        selectionkey = (SelectionKey)iterator.next();
        ipollevents = (IPollEvents)selectionkey.attachment();
        iterator.remove();
        if (!selectionkey.isReadable()) goto _L4; else goto _L3
_L3:
        ipollevents.in_event();
_L5:
        if (selectionkey.isWritable())
        {
            ipollevents.out_event();
        }
        break; /* Loop/switch isn't completed */
_L4:
        if (selectionkey.isAcceptable())
        {
            ipollevents.accept_event();
            continue; /* Loop/switch isn't completed */
        }
        if (selectionkey.isConnectable())
        {
            ipollevents.connect_event();
        }
        if (true) goto _L5; else goto _L2
_L2:
        stopped = true;
        return;
        CancelledKeyException cancelledkeyexception;
        cancelledkeyexception;
        if (true) goto _L7; else goto _L6
_L6:
        if (true) goto _L9; else goto _L8
_L8:
    }

    public final void set_pollaccept(SelectableChannel selectablechannel)
    {
        register(selectablechannel, 16, false);
    }

    public final void set_pollconnect(SelectableChannel selectablechannel)
    {
        register(selectablechannel, 8, false);
    }

    public final void set_pollin(SelectableChannel selectablechannel)
    {
        register(selectablechannel, 1, false);
    }

    public final void set_pollout(SelectableChannel selectablechannel)
    {
        register(selectablechannel, 4, false);
    }

    public void start()
    {
        worker = new Thread(this, name);
        worker.start();
    }

    public void stop()
    {
        stopping = true;
        selector.wakeup();
    }
}
