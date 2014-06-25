// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;


// Referenced classes of package org.jeromq:
//            ZThread, ZContext

private static class args extends Thread
{

    static final boolean $assertionsDisabled;
    private Object args[];
    private nnable attachedRunnable;
    private ZContext ctx;
    private nnable detachedRunnable;
    private nnable pipe;

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
        if (!org/jeromq/ZThread.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }

    protected nnable(ZContext zcontext, nnable nnable, Object aobj[], nnable nnable1)
    {
        if (!$assertionsDisabled && zcontext == null)
        {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && nnable1 == null)
        {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && nnable == null)
        {
            throw new AssertionError();
        } else
        {
            ctx = zcontext;
            attachedRunnable = nnable;
            args = aobj;
            pipe = nnable1;
            return;
        }
    }

    public nnable(nnable nnable, Object aobj[])
    {
        if (!$assertionsDisabled && nnable == null)
        {
            throw new AssertionError();
        } else
        {
            detachedRunnable = nnable;
            args = aobj;
            return;
        }
    }
}
