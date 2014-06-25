// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq;

import java.io.IOException;
import java.nio.channels.Selector;

// Referenced classes of package org.jeromq:
//            ZMQ

protected static class selector
{

    static final boolean $assertionsDisabled;
    private Selector selector;

    public void finalize()
    {
        try
        {
            selector.close();
        }
        catch (IOException ioexception) { }
        try
        {
            super.finalize();
            return;
        }
        catch (Throwable throwable)
        {
            return;
        }
    }

    public Selector get()
    {
        if (!$assertionsDisabled && selector == null)
        {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && !selector.isOpen())
        {
            throw new AssertionError();
        } else
        {
            return selector;
        }
    }

    public Selector open()
        throws IOException
    {
        selector = Selector.open();
        return selector;
    }

    static 
    {
        boolean flag;
        if (!org/jeromq/ZMQ.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }

    protected ()
    {
        selector = null;
    }
}
