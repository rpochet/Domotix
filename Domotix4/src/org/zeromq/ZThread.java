// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.zeromq;


// Referenced classes of package org.zeromq:
//            ZContext

public class ZThread
{
    public static interface IAttachedRunnable
    {

        public abstract void run(Object aobj[], ZContext zcontext, ZMQ.Socket socket);
    }

    public static interface IDetachedRunnable
    {

        public abstract void run(Object aobj[]);
    }

    private static class ShimThread extends Thread
    {

        static final boolean $assertionsDisabled;
        private Object args[];
        private IAttachedRunnable attachedRunnable;
        private ZContext ctx;
        private IDetachedRunnable detachedRunnable;
        private ZMQ.Socket pipe;

        public void run()
        {
            if (attachedRunnable != null)
            {
                attachedRunnable.run(args, ctx, pipe);
                ctx.destroy();
                return;
            } else
            {
                detachedRunnable.run(args);
                return;
            }
        }

        static 
        {
            boolean flag;
            if (!org/zeromq/ZThread.desiredAssertionStatus())
            {
                flag = true;
            } else
            {
                flag = false;
            }
            $assertionsDisabled = flag;
        }

        protected ShimThread(ZContext zcontext, IAttachedRunnable iattachedrunnable, Object aobj[], ZMQ.Socket socket)
        {
            if (!$assertionsDisabled && zcontext == null)
            {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && socket == null)
            {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && iattachedrunnable == null)
            {
                throw new AssertionError();
            } else
            {
                ctx = zcontext;
                attachedRunnable = iattachedrunnable;
                args = aobj;
                pipe = socket;
                return;
            }
        }

        public ShimThread(IDetachedRunnable idetachedrunnable, Object aobj[])
        {
            if (!$assertionsDisabled && idetachedrunnable == null)
            {
                throw new AssertionError();
            } else
            {
                detachedRunnable = idetachedrunnable;
                args = aobj;
                return;
            }
        }
    }


    public ZThread()
    {
    }

    public static transient ZMQ.Socket fork(ZContext zcontext, IAttachedRunnable iattachedrunnable, Object aobj[])
    {
        ZMQ.Socket socket = zcontext.createSocket(0);
        if (socket != null)
        {
            Object aobj1[] = new Object[1];
            aobj1[0] = Integer.valueOf(socket.hashCode());
            socket.bind(String.format("inproc://zctx-pipe-%d", aobj1));
            ZContext zcontext1 = ZContext.shadow(zcontext);
            ZMQ.Socket socket1 = zcontext1.createSocket(0);
            if (socket1 == null)
            {
                return null;
            } else
            {
                Object aobj2[] = new Object[1];
                aobj2[0] = Integer.valueOf(socket.hashCode());
                socket1.connect(String.format("inproc://zctx-pipe-%d", aobj2));
                (new ShimThread(zcontext1, iattachedrunnable, aobj, socket1)).start();
                return socket;
            }
        } else
        {
            return null;
        }
    }

    public static transient void start(IDetachedRunnable idetachedrunnable, Object aobj[])
    {
        (new ShimThread(idetachedrunnable, aobj)).start();
    }
}
