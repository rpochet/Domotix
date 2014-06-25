// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.channels.SelectableChannel;

// Referenced classes of package zmq:
//            ZObject, IPollEvents, Poller, Mailbox, 
//            Command, SocketBase, Ctx

public class Reaper extends ZObject
    implements IPollEvents
{

    private final Mailbox mailbox;
    private SelectableChannel mailbox_handle;
    private String name;
    private final Poller poller;
    private int sockets;
    private volatile boolean terminating;

    public Reaper(Ctx ctx, int i)
    {
        super(ctx, i);
        sockets = 0;
        terminating = false;
        name = (new StringBuilder()).append("reaper-").append(i).toString();
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

    public Mailbox get_mailbox()
    {
        return mailbox;
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

    protected void process_reap(SocketBase socketbase)
    {
        socketbase.start_reaping(poller);
        sockets = 1 + sockets;
    }

    protected void process_reaped()
    {
        sockets = -1 + sockets;
        if (sockets == 0 && terminating)
        {
            send_done();
            poller.rm_fd(mailbox_handle);
            poller.stop();
        }
    }

    protected void process_stop()
    {
        terminating = true;
        if (sockets == 0)
        {
            send_done();
            poller.rm_fd(mailbox_handle);
            poller.stop();
        }
    }

    public void start()
    {
        poller.start();
    }

    public void stop()
    {
        if (!terminating)
        {
            send_stop();
        }
    }

    public void timer_event(int i)
    {
        throw new UnsupportedOperationException();
    }
}
