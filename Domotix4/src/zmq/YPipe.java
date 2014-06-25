// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.util.concurrent.atomic.AtomicInteger;

// Referenced classes of package zmq:
//            YQueue

public class YPipe
{

    static final boolean $assertionsDisabled;
    private final AtomicInteger c;
    private int f;
    private final YQueue queue;
    private int r;
    private int w;

    public YPipe(Class class1, int i)
    {
        queue = new YQueue(class1, i);
        int j = queue.back_pos();
        f = j;
        r = j;
        w = j;
        c = new AtomicInteger(queue.back_pos());
    }

    public final boolean check_read()
    {
        int i = queue.front_pos();
        if (i == r)
        {
            if (!c.compareAndSet(i, -1))
            {
                r = c.get();
            }
            if (i == r || r == -1)
            {
                return false;
            }
        }
        return true;
    }

    public final boolean flush()
    {
        if (w == f)
        {
            return true;
        }
        if (!c.compareAndSet(w, f))
        {
            c.set(f);
            w = f;
            return false;
        } else
        {
            w = f;
            return true;
        }
    }

    public final Object probe()
    {
        boolean flag = check_read();
        if (!$assertionsDisabled && !flag)
        {
            throw new AssertionError();
        } else
        {
            return queue.front();
        }
    }

    public final Object read()
    {
        if (!check_read())
        {
            return null;
        } else
        {
            return queue.pop();
        }
    }

    public Object unwrite()
    {
        if (f == queue.back_pos())
        {
            return null;
        } else
        {
            queue.unpush();
            return queue.back();
        }
    }

    public final void write(Object obj, boolean flag)
    {
        queue.push(obj);
        if (!flag)
        {
            f = queue.back_pos();
        }
    }

    static 
    {
        boolean flag;
        if (!zmq/YPipe.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
