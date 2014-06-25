// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.channels.SelectableChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Referenced classes of package zmq:
//            YPipe, Command, Config, Signaler

public class Mailbox
{

    static final boolean $assertionsDisabled;
    private boolean active;
    private final YPipe cpipe;
    private final String name;
    private final Signaler signaler = new Signaler();
    private final Lock sync = new ReentrantLock();

    public Mailbox(String s)
    {
        cpipe = new YPipe(zmq/Command, Config.command_pipe_granularity.getValue());
        Command command = (Command)cpipe.read();
        if (!$assertionsDisabled && command != null)
        {
            throw new AssertionError();
        } else
        {
            active = false;
            name = s;
            return;
        }
    }

    public void close()
    {
        sync.lock();
        sync.unlock();
        signaler.close();
    }

    public SelectableChannel get_fd()
    {
        return signaler.get_fd();
    }

    public Command recv(long l)
    {
        if (active)
        {
            Command command1 = (Command)cpipe.read();
            if (command1 != null)
            {
                return command1;
            }
            active = false;
            signaler.recv();
        }
        if (!signaler.wait_event(l))
        {
            return null;
        }
        active = true;
        Command command = (Command)cpipe.read();
        if (!$assertionsDisabled && command == null)
        {
            throw new AssertionError();
        } else
        {
            return command;
        }
    }

    public void send(Command command)
    {
        sync.lock();
        boolean flag;
        cpipe.write(command, false);
        flag = cpipe.flush();
        sync.unlock();
        if (!flag)
        {
            signaler.send();
        }
        return;
        Exception exception;
        exception;
        sync.unlock();
        throw exception;
    }

    public String toString()
    {
        return (new StringBuilder()).append(super.toString()).append("[").append(name).append("]").toString();
    }

    static 
    {
        boolean flag;
        if (!zmq/Mailbox.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
