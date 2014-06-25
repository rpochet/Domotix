// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.util.ArrayList;
import java.util.List;

// Referenced classes of package zmq:
//            Utils, Pipe, Msg, ValueReference

public class LB
{

    static final boolean $assertionsDisabled;
    private int active;
    private int current;
    private boolean dropping;
    private boolean more;
    private final List pipes = new ArrayList();

    public LB()
    {
        active = 0;
        current = 0;
        more = false;
        dropping = false;
    }

    public void activated(Pipe pipe)
    {
        Utils.swap(pipes, pipes.indexOf(pipe), active);
        active = 1 + active;
    }

    public void attach(Pipe pipe)
    {
        pipes.add(pipe);
        activated(pipe);
    }

    public boolean has_out()
    {
        if (more)
        {
            return true;
        }
_L3:
        if (active > 0)
        {
            if (((Pipe)pipes.get(current)).check_write())
            {
                return true;
            }
        } else
        {
            return false;
        }
        if (true) goto _L2; else goto _L1
_L2:
        active = -1 + active;
        Utils.swap(pipes, current, active);
        if (current == active)
        {
            current = 0;
        }
        if (true) goto _L3; else goto _L1
_L1:
    }

    public boolean send(Msg msg, ValueReference valuereference)
    {
        if (dropping)
        {
            more = msg.has_more();
            dropping = more;
            msg.close();
            return true;
        }
        break MISSING_BLOCK_LABEL_65;
        do
        {
label0:
            {
                if (active <= 0 || ((Pipe)pipes.get(current)).write(msg))
                {
                    if (active == 0)
                    {
                        valuereference.set(Integer.valueOf(35));
                        return false;
                    }
                    break label0;
                }
                if (!$assertionsDisabled && more)
                {
                    throw new AssertionError();
                }
            }
            active = -1 + active;
            if (current < active)
            {
                Utils.swap(pipes, current, active);
            } else
            {
                current = 0;
            }
        } while (true);
        more = msg.has_more();
        if (!more)
        {
            ((Pipe)pipes.get(current)).flush();
            if (active > 1)
            {
                current = (1 + current) % active;
            }
        }
        return true;
    }

    public void terminated(Pipe pipe)
    {
        int i = pipes.indexOf(pipe);
        if (i == current && more)
        {
            dropping = true;
        }
        if (i < active)
        {
            active = -1 + active;
            Utils.swap(pipes, i, active);
            if (current == active)
            {
                current = 0;
            }
        }
        pipes.remove(pipe);
    }

    static 
    {
        boolean flag;
        if (!zmq/LB.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
