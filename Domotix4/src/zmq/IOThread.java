// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.channels.SelectableChannel;

// Referenced classes of package zmq:
//            ZObject, IPollEvents, Poller, Mailbox, 
//            Command, Ctx

public class IOThread extends ZObject
    implements IPollEvents
{

    static final boolean $assertionsDisabled;
    private final Mailbox mailbox;
    private final SelectableChannel mailbox_handle;
    final String name;
    private final Poller poller;

    public IOThread(Ctx ctx, int i)
    {
        super(ctx, i);
        name = (new StringBuilder()).append("iothread-").append(i).toString();
        poller = new Poller(name);
        mailbox = new Mailbox(name);
        mailbox_handle = mailbox.get_fd();
        poller.add_fd(mailbox_handle, this);
        poller.set_pollin(mailbox_handle);
    }

    public void accept_event()
    {
        throw new UnsupportedOperationException();
    }

    public void connect_event()
    {
        throw new UnsupportedOperationException();
    }

    public void destroy()
    {
        poller.destroy();
        mailbox.close();
    }

    public int get_load()
    {
        return poller.get_load();
    }

    public Mailbox get_mailbox()
    {
        return mailbox;
    }

    public Poller get_poller()
    {
        if (!$assertionsDisabled && poller == null)
        {
            throw new AssertionError();
        } else
        {
            return poller;
        }
    }

    public void in_event()
    {
        do
        {
            Command command = mailbox.recv(0L);
            if (command == null)
            {
                return;
            }
            command.destination().process_command(command);
        } while (true);
    }

    public void out_event()
    {
        throw new UnsupportedOperationException();
    }

    protected void process_stop()
    {
        poller.rm_fd(mailbox_handle);
        poller.stop();
    }

    public void start()
    {
        poller.start();
    }

    public void stop()
    {
        send_stop();
    }

    public void timer_event(int i)
    {
        throw new UnsupportedOperationException();
    }

    static 
    {
        boolean flag;
        if (!zmq/IOThread.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
