// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.io.IOException;
import java.nio.channels.Selector;

// Referenced classes of package zmq:
//            ZMQ

private static class selector
{

    static final boolean $assertionsDisabled;
    private Selector selector;

    public static Selector open()
        throws IOException
    {
        ector ector = (ector)ZMQ.access$000().get();
        if (ector != null) goto _L2; else goto _L1
_L1:
        ThreadLocal threadlocal = ZMQ.access$000();
        threadlocal;
        JVM INSTR monitorenter ;
        ector = (.get)ZMQ.access$000().get();
        if (ector != null)
        {
            break MISSING_BLOCK_LABEL_54;
        }
        ector ector1 = new <init>(Selector.open());
        ZMQ.access$000().set(ector1);
        ector = ector1;
        threadlocal;
        JVM INSTR monitorexit ;
_L2:
        return ector.get();
        IOException ioexception;
        ioexception;
_L4:
        throw new n(ioexception);
_L3:
        threadlocal;
        JVM INSTR monitorexit ;
        Exception exception;
        throw exception;
        exception;
          goto _L3
        ioexception;
          goto _L4
        exception;
          goto _L3
    }

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

    static 
    {
        boolean flag;
        if (!zmq/ZMQ.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }

    private ector(Selector selector1)
    {
        selector = selector1;
    }
}
