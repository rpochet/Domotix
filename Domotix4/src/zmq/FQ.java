// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.util.ArrayList;
import java.util.List;

// Referenced classes of package zmq:
//            Utils, Pipe, ValueReference, Msg

public class FQ
{

    static final boolean $assertionsDisabled;
    private int active;
    private int current;
    private boolean more;
    private final List pipes = new ArrayList();

    public FQ()
    {
        active = 0;
        current = 0;
        more = false;
    }

    public void activated(Pipe pipe)
    {
        Utils.swap(pipes, pipes.indexOf(pipe), active);
        active = 1 + active;
    }

    public void attach(Pipe pipe)
    {
        pipes.add(pipe);
        Utils.swap(pipes, active, -1 + pipes.size());
        active = 1 + active;
    }

    public boolean has_in()
    {
        if (more)
        {
            return true;
        }
_L3:
        if (active > 0)
        {
            if (((Pipe)pipes.get(current)).check_read())
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

    public Msg recv(ValueReference valuereference)
    {
        return recvpipe(valuereference, null);
    }

    public Msg recvpipe(ValueReference valuereference, ValueReference valuereference1)
    {
        do
        {
            if (active <= 0)
            {
                break;
            }
            Msg msg = ((Pipe)pipes.get(current)).read();
            boolean flag;
            if (msg != null)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            if (flag)
            {
                if (valuereference1 != null)
                {
                    valuereference1.set(pipes.get(current));
                }
                more = msg.has_more();
                if (!more)
                {
                    current = (1 + current) % active;
                }
                return msg;
            }
            if (!$assertionsDisabled && more)
            {
                throw new AssertionError();
            }
            active = -1 + active;
            Utils.swap(pipes, current, active);
            if (current == active)
            {
                current = 0;
            }
        } while (true);
        valuereference.set(Integer.valueOf(35));
        return null;
    }

    public void terminated(Pipe pipe)
    {
        int i = pipes.indexOf(pipe);
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
        if (!zmq/FQ.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
