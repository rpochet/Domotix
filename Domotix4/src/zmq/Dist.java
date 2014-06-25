// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.util.ArrayList;
import java.util.List;

// Referenced classes of package zmq:
//            Pipe, Utils, Msg

public class Dist
{

    private int active;
    private int eligible;
    private int matching;
    private boolean more;
    private final List pipes = new ArrayList();

    public Dist()
    {
        matching = 0;
        active = 0;
        eligible = 0;
        more = false;
    }

    private void distribute(Msg msg)
    {
        if (matching != 0)
        {
            int i = 0;
            while (i < matching) 
            {
                if (!write((Pipe)pipes.get(i), msg))
                {
                    i--;
                }
                i++;
            }
        }
    }

    private boolean write(Pipe pipe, Msg msg)
    {
        if (!pipe.write(msg))
        {
            Utils.swap(pipes, pipes.indexOf(pipe), -1 + matching);
            matching = -1 + matching;
            Utils.swap(pipes, pipes.indexOf(pipe), -1 + active);
            active = -1 + active;
            Utils.swap(pipes, active, -1 + eligible);
            eligible = -1 + eligible;
            return false;
        }
        if (!msg.has_more())
        {
            pipe.flush();
        }
        return true;
    }

    public void activated(Pipe pipe)
    {
        Utils.swap(pipes, pipes.indexOf(pipe), eligible);
        eligible = 1 + eligible;
        if (!more)
        {
            Utils.swap(pipes, -1 + eligible, active);
            active = 1 + active;
        }
    }

    public void attach(Pipe pipe)
    {
        if (more)
        {
            pipes.add(pipe);
            Utils.swap(pipes, eligible, -1 + pipes.size());
            eligible = 1 + eligible;
            return;
        } else
        {
            pipes.add(pipe);
            Utils.swap(pipes, active, -1 + pipes.size());
            active = 1 + active;
            eligible = 1 + eligible;
            return;
        }
    }

    public boolean has_out()
    {
        return true;
    }

    public void match(Pipe pipe)
    {
        int i;
        for (i = pipes.indexOf(pipe); i < matching || i >= eligible;)
        {
            return;
        }

        Utils.swap(pipes, i, matching);
        matching = 1 + matching;
    }

    public boolean send_to_all(Msg msg)
    {
        matching = active;
        return send_to_matching(msg);
    }

    public boolean send_to_matching(Msg msg)
    {
        boolean flag = msg.has_more();
        distribute(msg);
        if (!flag)
        {
            active = eligible;
        }
        more = flag;
        return true;
    }

    public void terminated(Pipe pipe)
    {
        if (pipes.indexOf(pipe) < matching)
        {
            Utils.swap(pipes, pipes.indexOf(pipe), -1 + matching);
            matching = -1 + matching;
        }
        if (pipes.indexOf(pipe) < active)
        {
            Utils.swap(pipes, pipes.indexOf(pipe), -1 + active);
            active = -1 + active;
        }
        if (pipes.indexOf(pipe) < eligible)
        {
            Utils.swap(pipes, pipes.indexOf(pipe), -1 + eligible);
            eligible = -1 + eligible;
        }
        pipes.remove(pipe);
    }

    public void unmatch()
    {
        matching = 0;
    }
}
