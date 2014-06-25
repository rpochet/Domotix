// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.zeromq;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ZLoop
{
    public static interface IZLoopHandler
    {

        public abstract int handle(ZLoop zloop, ZMQ.PollItem pollitem, Object obj);
    }

    private class SPoller
    {

        Object arg;
        int errors;
        IZLoopHandler handler;
        ZMQ.PollItem item;
        final ZLoop this$0;

        protected SPoller(ZMQ.PollItem pollitem, IZLoopHandler izloophandler, Object obj)
        {
            this$0 = ZLoop.this;
            super();
            item = pollitem;
            handler = izloophandler;
            arg = obj;
            errors = 0;
        }
    }

    private class STimer
    {

        Object arg;
        int delay;
        IZLoopHandler handler;
        final ZLoop this$0;
        int times;
        long when;

        public STimer(int i, int j, IZLoopHandler izloophandler, Object obj)
        {
            this$0 = ZLoop.this;
            super();
            delay = i;
            times = j;
            handler = izloophandler;
            arg = obj;
            when = -1L;
        }
    }


    static final boolean $assertionsDisabled;
    private boolean dirty;
    private final List newTimers = new ArrayList();
    private int pollSize;
    private SPoller pollact[];
    private final List pollers = new ArrayList();
    private ZMQ.Poller pollset;
    private final List timers = new ArrayList();
    private boolean verbose;
    private final List zombies = new ArrayList();

    public ZLoop()
    {
    }

    private void rebuild()
    {
        pollact = null;
        pollSize = pollers.size();
        pollset = new ZMQ.Poller(pollSize);
        pollact = new SPoller[pollSize];
        int i = 0;
        for (Iterator iterator = pollers.iterator(); iterator.hasNext();)
        {
            SPoller spoller = (SPoller)iterator.next();
            pollset.register(spoller.item);
            pollact[i] = spoller;
            i++;
        }

        dirty = false;
    }

    private long ticklessTimer()
    {
        long l = 0x36ee80L + System.currentTimeMillis();
        Iterator iterator = timers.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            STimer stimer = (STimer)iterator.next();
            if (stimer.when == -1L)
            {
                stimer.when = (long)stimer.delay + System.currentTimeMillis();
            }
            if (l > stimer.when)
            {
                l = stimer.when;
            }
        } while (true);
        long l1 = l - System.currentTimeMillis();
        if (l1 < 0L)
        {
            l1 = 0L;
        }
        if (verbose)
        {
            PrintStream printstream = System.out;
            Object aobj[] = new Object[1];
            aobj[0] = Long.valueOf(l1);
            printstream.printf("I: zloop: polling for %d msec\n", aobj);
        }
        return l1;
    }

    public int addPoller(ZMQ.PollItem pollitem, IZLoopHandler izloophandler, Object obj)
    {
        if (pollitem.getRawSocket() == null && pollitem.getSocket() == null)
        {
            return -1;
        }
        SPoller spoller = new SPoller(pollitem, izloophandler, obj);
        pollers.add(spoller);
        dirty = true;
        if (verbose)
        {
            PrintStream printstream = System.out;
            Object aobj[] = new Object[3];
            Object obj1;
            if (pollitem.getSocket() != null)
            {
                obj1 = Integer.valueOf(pollitem.getSocket().getType());
            } else
            {
                obj1 = "RAW";
            }
            aobj[0] = obj1;
            aobj[1] = pollitem.getSocket();
            aobj[2] = pollitem.getRawSocket();
            printstream.printf("I: zloop: register %s poller (%s, %s)\n", aobj);
        }
        return 0;
    }

    public int addTimer(int i, int j, IZLoopHandler izloophandler, Object obj)
    {
        STimer stimer = new STimer(i, j, izloophandler, obj);
        newTimers.add(stimer);
        if (verbose)
        {
            PrintStream printstream = System.out;
            Object aobj[] = new Object[2];
            aobj[0] = Integer.valueOf(i);
            aobj[1] = Integer.valueOf(j);
            printstream.printf("I: zloop: register timer delay=%d times=%d\n", aobj);
        }
        return 0;
    }

    public void destroy()
    {
    }

    public void removePoller(ZMQ.PollItem pollitem)
    {
        Iterator iterator = pollers.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            if (pollitem.equals(((SPoller)iterator.next()).item))
            {
                iterator.remove();
                dirty = true;
            }
        } while (true);
        if (verbose)
        {
            PrintStream printstream = System.out;
            Object aobj[] = new Object[3];
            Object obj;
            if (pollitem.getSocket() != null)
            {
                obj = Integer.valueOf(pollitem.getSocket().getType());
            } else
            {
                obj = "RAW";
            }
            aobj[0] = obj;
            aobj[1] = pollitem.getSocket();
            aobj[2] = pollitem.getRawSocket();
            printstream.printf("I: zloop: cancel %s poller (%s, %s)", aobj);
        }
    }

    public int removeTimer(Object obj)
    {
        if (!$assertionsDisabled && obj == null)
        {
            throw new AssertionError();
        }
        zombies.add(obj);
        if (verbose)
        {
            System.out.printf("I: zloop: cancel timer\n", new Object[0]);
        }
        return 0;
    }

    public int start()
    {
        int i;
        timers.addAll(newTimers);
        newTimers.clear();
        Iterator iterator = timers.iterator();
        do
        {
            boolean flag = iterator.hasNext();
            i = 0;
            if (!flag)
            {
                break;
            }
            STimer stimer1 = (STimer)iterator.next();
            stimer1.when = (long)stimer1.delay + System.currentTimeMillis();
        } while (true);
_L11:
        if (Thread.currentThread().isInterrupted()) goto _L2; else goto _L1
_L1:
        if (dirty)
        {
            rebuild();
        }
        long l = ticklessTimer();
        i = pollset.poll(l);
        if (i != -1) goto _L4; else goto _L3
_L3:
        if (verbose)
        {
            PrintStream printstream2 = System.out;
            Object aobj2[] = new Object[1];
            aobj2[0] = Integer.valueOf(i);
            printstream2.printf("I: zloop: interrupted (%d)\n", aobj2);
        }
        i = 0;
_L2:
        return i;
_L4:
        Iterator iterator1 = timers.iterator();
_L8:
        STimer stimer;
        do
        {
            if (!iterator1.hasNext())
            {
                continue; /* Loop/switch isn't completed */
            }
            stimer = (STimer)iterator1.next();
        } while (System.currentTimeMillis() < stimer.when || stimer.when == -1L);
        if (verbose)
        {
            System.out.println("I: zloop: call timer handler");
        }
        i = stimer.handler.handle(this, null, stimer.arg);
        if (i != -1) goto _L6; else goto _L5
_L5:
        if (i == -1) goto _L2; else goto _L7
_L7:
        int j = 0;
_L9:
        if (j < pollSize)
        {
            SPoller spoller = pollact[j];
            if (pollset.getItem(j).isError())
            {
                if (verbose)
                {
                    PrintStream printstream1 = System.out;
                    Object aobj1[] = new Object[3];
                    Iterator iterator2;
                    Object obj;
                    Iterator iterator3;
                    PrintStream printstream;
                    Object aobj[];
                    int k;
                    Object obj2;
                    int i1;
                    if (spoller.item.getSocket() != null)
                    {
                        obj2 = Integer.valueOf(spoller.item.getSocket().getType());
                    } else
                    {
                        obj2 = "RAW";
                    }
                    aobj1[0] = obj2;
                    aobj1[1] = spoller.item.getSocket();
                    aobj1[2] = spoller.item.getRawSocket();
                    printstream1.printf("I: zloop: can't poll %s socket (%s, %s)\n", aobj1);
                }
                k = spoller.errors;
                spoller.errors = k + 1;
                if (k > 0)
                {
                    removePoller(spoller.item);
                }
            } else
            {
                spoller.errors = 0;
            }
            if (pollset.getItem(j).readyOps() <= 0)
            {
                break MISSING_BLOCK_LABEL_704;
            }
            if (verbose)
            {
                printstream = System.out;
                aobj = new Object[3];
                Object obj1;
                if (spoller.item.getSocket() != null)
                {
                    obj1 = Integer.valueOf(spoller.item.getSocket().getType());
                } else
                {
                    obj1 = "RAW";
                }
                aobj[0] = obj1;
                aobj[1] = spoller.item.getSocket();
                aobj[2] = spoller.item.getRawSocket();
                printstream.printf("I: zloop: call %s socket handler (%s, %s)\n", aobj);
            }
            i = spoller.handler.handle(this, spoller.item, spoller.arg);
            if (i != -1)
            {
                break MISSING_BLOCK_LABEL_704;
            }
        }
        for (iterator2 = zombies.iterator(); iterator2.hasNext();)
        {
            obj = iterator2.next();
            iterator3 = timers.iterator();
            while (iterator3.hasNext()) 
            {
                if (((STimer)iterator3.next()).arg == obj)
                {
                    iterator3.remove();
                }
            }
        }

        break MISSING_BLOCK_LABEL_710;
_L6:
label0:
        {
            if (stimer.times == 0)
            {
                break label0;
            }
            i1 = -1 + stimer.times;
            stimer.times = i1;
            if (i1 != 0)
            {
                break label0;
            }
            iterator1.remove();
        }
          goto _L8
        stimer.when = (long)stimer.delay + System.currentTimeMillis();
          goto _L8
        j++;
          goto _L9
        timers.addAll(newTimers);
        newTimers.clear();
        if (i == -1)
        {
            return i;
        }
        if (true) goto _L11; else goto _L10
_L10:
    }

    public void verbose(boolean flag)
    {
        verbose = flag;
    }

    static 
    {
        boolean flag;
        if (!org/zeromq/ZLoop.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
