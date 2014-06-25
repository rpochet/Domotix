// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

// Referenced classes of package zmq:
//            MultiMap, Clock, IPollEvents

public abstract class PollerBase
{
    private final class TimerInfo
    {

        int id;
        IPollEvents sink;
        final PollerBase this$0;

        public TimerInfo(IPollEvents ipollevents, int i)
        {
            this$0 = PollerBase.this;
            super();
            sink = ipollevents;
            id = i;
        }
    }


    static final boolean $assertionsDisabled;
    private final Map addingTimers = new MultiMap();
    private final AtomicInteger load = new AtomicInteger(0);
    private final Map timers = new MultiMap();

    protected PollerBase()
    {
    }

    public void add_timer(long l, IPollEvents ipollevents, int i)
    {
        long l1 = l + Clock.now_ms();
        TimerInfo timerinfo = new TimerInfo(ipollevents, i);
        addingTimers.put(Long.valueOf(l1), timerinfo);
    }

    protected void adjust_load(int i)
    {
        load.addAndGet(i);
    }

    public void cancel_timer(IPollEvents ipollevents, int i)
    {
        Iterator iterator;
        if (!addingTimers.isEmpty())
        {
            timers.putAll(addingTimers);
            addingTimers.clear();
        }
        iterator = timers.entrySet().iterator();
_L4:
        if (!iterator.hasNext()) goto _L2; else goto _L1
_L1:
        TimerInfo timerinfo = (TimerInfo)((java.util.Map.Entry)iterator.next()).getValue();
        if (timerinfo.sink != ipollevents || timerinfo.id != i) goto _L4; else goto _L3
_L3:
        iterator.remove();
_L6:
        return;
_L2:
        if (!$assertionsDisabled)
        {
            throw new AssertionError();
        }
        if (true) goto _L6; else goto _L5
_L5:
    }

    protected long execute_timers()
    {
        if (!addingTimers.isEmpty())
        {
            timers.putAll(addingTimers);
            addingTimers.clear();
        }
        if (timers.isEmpty())
        {
            return 0L;
        }
        long l = Clock.now_ms();
        for (Iterator iterator = timers.entrySet().iterator(); iterator.hasNext(); iterator.remove())
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            if (((Long)entry.getKey()).longValue() > l)
            {
                return ((Long)entry.getKey()).longValue() - l;
            }
            ((TimerInfo)entry.getValue()).sink.timer_event(((TimerInfo)entry.getValue()).id);
        }

        if (!addingTimers.isEmpty())
        {
            return execute_timers();
        } else
        {
            return 0L;
        }
    }

    public final int get_load()
    {
        return load.get();
    }

    static 
    {
        boolean flag;
        if (!zmq/PollerBase.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
