// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import zmq.PollItem;
import zmq.SocketBase;
import zmq.ZMQ;

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
    private static ThreadLocal initialized = new ThreadLocal();
    private static ZLoop instance = null;
    private boolean dirty;
    private final List newTimers = new ArrayList();
    private int poll_size;
    private SPoller pollact[];
    private final List pollers = new ArrayList();
    private PollItem pollset[];
    private final List timers = new ArrayList();
    private boolean verbose;
    private final List zombies = new ArrayList();

    private ZLoop()
    {
    }

    public static ZLoop instance()
    {
        if (initialized.get() == null)
        {
            synchronized (initialized)
            {
                if (instance == null)
                {
                    instance = new ZLoop();
                }
                initialized.set(Boolean.TRUE);
            }
        }
        return instance;
        exception;
        threadlocal;
        JVM INSTR monitorexit ;
        throw exception;
    }

    private void rebuild()
    {
        pollset = null;
        pollact = null;
        poll_size = pollers.size();
        pollset = new PollItem[poll_size];
        pollact = new SPoller[poll_size];
        int i = 0;
        for (Iterator iterator = pollers.iterator(); iterator.hasNext();)
        {
            SPoller spoller = (SPoller)iterator.next();
            pollset[i] = spoller.item.base();
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

    public void destory()
    {
    }

    public int poller(ZMQ.PollItem pollitem, IZLoopHandler izloophandler, Object obj)
    {
        PollItem pollitem1 = pollitem.base();
        if (pollitem1.getRawSocket() == null && pollitem1.getSocket() == null)
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
            String s;
            if (pollitem1.getSocket() != null)
            {
                s = pollitem1.getSocket().typeString();
            } else
            {
                s = "FD";
            }
            aobj[0] = s;
            aobj[1] = pollitem1.getSocket();
            aobj[2] = pollitem1.getRawSocket();
            printstream.printf("I: zloop: register %s poller (%s, %s)\n", aobj);
        }
        return 0;
    }

    public void pollerEnd(ZMQ.PollItem pollitem)
    {
        PollItem pollitem1 = pollitem.base();
        if (!$assertionsDisabled && pollitem1.getRawSocket() == null && pollitem1.getSocket() == null)
        {
            throw new AssertionError();
        }
        Iterator iterator = pollers.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            SPoller spoller = (SPoller)iterator.next();
            if (pollitem1.getSocket() != null && pollitem1.getSocket() == spoller.item.getSocket())
            {
                iterator.remove();
                dirty = true;
            }
            if (pollitem1.getRawSocket() != null && pollitem1.getRawSocket() == spoller.item.getChannel())
            {
                iterator.remove();
                dirty = true;
            }
        } while (true);
        if (verbose)
        {
            PrintStream printstream = System.out;
            Object aobj[] = new Object[3];
            String s;
            if (pollitem1.getSocket() != null)
            {
                s = pollitem1.getSocket().typeString();
            } else
            {
                s = "FD";
            }
            aobj[0] = s;
            aobj[1] = pollitem1.getSocket();
            aobj[2] = pollitem1.getRawSocket();
            printstream.printf("I: zloop: cancel %s poller (%s, %s)", aobj);
        }
    }

    public int start()
    {
        int i;
        i = 0;
        timers.addAll(newTimers);
        newTimers.clear();
        for (Iterator iterator = timers.iterator(); iterator.hasNext();)
        {
            STimer stimer1 = (STimer)iterator.next();
            stimer1.when = (long)stimer1.delay + System.currentTimeMillis();
        }

        Selector selector;
        long l;
        PrintStream printstream2;
        Object aobj2[];
        try
        {
            selector = Selector.open();
        }
        catch (IOException ioexception)
        {
            System.err.println(ioexception.getMessage());
            return -1;
        }
_L11:
        if (Thread.currentThread().isInterrupted()) goto _L2; else goto _L1
_L1:
        if (dirty)
        {
            rebuild();
        }
        l = ticklessTimer();
        i = ZMQ.zmq_poll(selector, pollset, l);
        if (i != -1) goto _L4; else goto _L3
_L3:
        if (verbose)
        {
            printstream2 = System.out;
            aobj2 = new Object[1];
            aobj2[0] = Integer.valueOf(i);
            printstream2.printf("I: zloop: interrupted\n", aobj2);
        }
        i = 0;
_L2:
        Iterator iterator1;
        int j;
        Iterator iterator2;
        Object obj;
        Iterator iterator3;
        SPoller spoller;
        PrintStream printstream;
        Object aobj[];
        String s;
        int k;
        PrintStream printstream1;
        Object aobj1[];
        String s1;
        STimer stimer;
        int i1;
        try
        {
            selector.close();
        }
        catch (IOException ioexception1) { }
        return i;
_L4:
        iterator1 = timers.iterator();
_L8:
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
        j = 0;
        break MISSING_BLOCK_LABEL_284;
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
_L10:
        if (j < poll_size)
        {
            spoller = pollact[j];
            if (!$assertionsDisabled && pollset[j].getSocket() != spoller.item.getSocket())
            {
                throw new AssertionError();
            }
            if (pollset[j].isError())
            {
                if (verbose)
                {
                    printstream1 = System.out;
                    aobj1 = new Object[3];
                    if (spoller.item.getSocket() != null)
                    {
                        s1 = spoller.item.getSocket().typeString();
                    } else
                    {
                        s1 = "FD";
                    }
                    aobj1[0] = s1;
                    aobj1[1] = spoller.item.getSocket();
                    aobj1[2] = spoller.item.getChannel();
                    printstream1.printf("I: zloop: can't poll %s socket (%s, %s)", aobj1);
                }
                k = spoller.errors;
                spoller.errors = k + 1;
                if (k > 0)
                {
                    pollerEnd(spoller.item);
                }
            } else
            {
                spoller.errors = 0;
            }
            if (pollset[j].readyOps() <= 0)
            {
                break MISSING_BLOCK_LABEL_745;
            }
            if (verbose)
            {
                printstream = System.out;
                aobj = new Object[3];
                if (spoller.item.getSocket() != null)
                {
                    s = spoller.item.getSocket().typeString();
                } else
                {
                    s = "FD";
                }
                aobj[0] = s;
                aobj[1] = spoller.item.getSocket();
                aobj[2] = spoller.item.getChannel();
                printstream.printf("I: zloop: call %s socket handler (%s, %s)\n", aobj);
            }
            i = spoller.handler.handle(this, spoller.item, spoller.arg);
            if (i != -1)
            {
                break MISSING_BLOCK_LABEL_745;
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

        break MISSING_BLOCK_LABEL_751;
        j++;
        if (true) goto _L10; else goto _L9
_L9:
        timers.addAll(newTimers);
        newTimers.clear();
        if (i != -1) goto _L11; else goto _L2
    }

    public int timer(int i, int j, IZLoopHandler izloophandler, Object obj)
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

    public int timerEnd(Object obj)
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

    public void verbose(boolean flag)
    {
        verbose = flag;
    }

    static 
    {
        boolean flag;
        if (!org/jeromq/ZLoop.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
