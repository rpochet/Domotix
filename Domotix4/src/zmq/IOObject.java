// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.channels.SelectableChannel;

// Referenced classes of package zmq:
//            IPollEvents, Poller, IOThread

public class IOObject
    implements IPollEvents
{

    static final boolean $assertionsDisabled;
    private IPollEvents handler;
    private Poller poller;

    public IOObject(IOThread iothread)
    {
        if (iothread != null)
        {
            plug(iothread);
        }
    }

    public final void accept_event()
    {
        handler.accept_event();
    }

    public final void add_fd(SelectableChannel selectablechannel)
    {
        poller.add_fd(selectablechannel, this);
    }

    public final void add_timer(long l, int i)
    {
        poller.add_timer(l, this, i);
    }

    public void cancel_timer(int i)
    {
        poller.cancel_timer(this, i);
    }

    public final void connect_event()
    {
        handler.connect_event();
    }

    public final void in_event()
    {
        handler.in_event();
    }

    public final void out_event()
    {
        handler.out_event();
    }

    public void plug(IOThread iothread)
    {
        if (!$assertionsDisabled && iothread == null)
        {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && poller != null)
        {
            throw new AssertionError();
        } else
        {
            poller = iothread.get_poller();
            return;
        }
    }

    public final void reset_pollin(SelectableChannel selectablechannel)
    {
        poller.reset_pollin(selectablechannel);
    }

    public final void reset_pollout(SelectableChannel selectablechannel)
    {
        poller.reset_pollout(selectablechannel);
    }

    public final void rm_fd(SelectableChannel selectablechannel)
    {
        poller.rm_fd(selectablechannel);
    }

    public final void set_handler(IPollEvents ipollevents)
    {
        handler = ipollevents;
    }

    public final void set_pollaccept(SelectableChannel selectablechannel)
    {
        poller.set_pollaccept(selectablechannel);
    }

    public final void set_pollconnect(SelectableChannel selectablechannel)
    {
        poller.set_pollconnect(selectablechannel);
    }

    public final void set_pollin(SelectableChannel selectablechannel)
    {
        poller.set_pollin(selectablechannel);
    }

    public final void set_pollout(SelectableChannel selectablechannel)
    {
        poller.set_pollout(selectablechannel);
    }

    public final void timer_event(int i)
    {
        handler.timer_event(i);
    }

    public void unplug()
    {
        if (!$assertionsDisabled && poller == null)
        {
            throw new AssertionError();
        } else
        {
            poller = null;
            handler = null;
            return;
        }
    }

    static 
    {
        boolean flag;
        if (!zmq/IOObject.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
