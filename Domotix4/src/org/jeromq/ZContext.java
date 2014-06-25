// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

// Referenced classes of package org.jeromq:
//            ZMQ

public class ZContext
{

    private ZMQ.Context context;
    private int hwm;
    private int ioThreads;
    private int linger;
    private boolean main;
    private List sockets;

    public ZContext()
    {
        this(1);
    }

    public ZContext(int i)
    {
        context = null;
        sockets = new CopyOnWriteArrayList();
        ioThreads = i;
        linger = 0;
        main = true;
    }

    public static ZContext shadow(ZContext zcontext)
    {
        ZContext zcontext1 = new ZContext();
        zcontext1.setContext(zcontext.getContext());
        zcontext1.setMain(false);
        return zcontext1;
    }

    public ZMQ.Socket createSocket(int i)
    {
        if (context == null)
        {
            context = ZMQ.context(ioThreads);
        }
        ZMQ.Socket socket = context.socket(i);
        sockets.add(socket);
        return socket;
    }

    public void destroy()
    {
        for (ListIterator listiterator = sockets.listIterator(); listiterator.hasNext(); destroySocket((ZMQ.Socket)listiterator.next())) { }
        sockets.clear();
        if (isMain() && context != null)
        {
            context.term();
        }
    }

    public void destroySocket(ZMQ.Socket socket)
    {
        while (socket == null || !sockets.contains(socket)) 
        {
            return;
        }
        socket.setLinger(linger);
        socket.close();
        sockets.remove(socket);
    }

    public ZMQ.Context getContext()
    {
        return context;
    }

    public int getHWM()
    {
        return hwm;
    }

    public int getIoThreads()
    {
        return ioThreads;
    }

    public int getLinger()
    {
        return linger;
    }

    public List getSockets()
    {
        return sockets;
    }

    public boolean isMain()
    {
        return main;
    }

    public void setContext(ZMQ.Context context1)
    {
        context = context1;
    }

    public void setHWM(int i)
    {
        hwm = i;
    }

    public void setIoThreads(int i)
    {
        ioThreads = i;
    }

    public void setLinger(int i)
    {
        linger = i;
    }

    public void setMain(boolean flag)
    {
        main = flag;
    }
}
