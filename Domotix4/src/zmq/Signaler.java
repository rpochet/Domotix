// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

// Referenced classes of package zmq:
//            Utils

public class Signaler
{

    static final boolean $assertionsDisabled;
    private java.nio.channels.Pipe.SourceChannel r;
    private int rcursor;
    private ByteBuffer rdummy;
    private Selector selector;
    private java.nio.channels.Pipe.SinkChannel w;
    private final AtomicInteger wcursor = new AtomicInteger(0);

    public Signaler()
    {
        rcursor = 0;
        make_fdpair();
        try
        {
            Utils.unblock_socket(w);
            Utils.unblock_socket(r);
        }
        catch (IOException ioexception)
        {
            throw new ZError.IOException(ioexception);
        }
        try
        {
            selector = Selector.open();
            r.register(selector, 1);
        }
        catch (IOException ioexception1)
        {
            throw new ZError.IOException(ioexception1);
        }
        rdummy = ByteBuffer.allocate(1);
    }

    private void make_fdpair()
    {
        Pipe pipe;
        try
        {
            pipe = Pipe.open();
        }
        catch (IOException ioexception)
        {
            throw new ZError.IOException(ioexception);
        }
        r = pipe.source();
        w = pipe.sink();
    }

    public void close()
    {
        try
        {
            r.close();
            w.close();
            selector.close();
            return;
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
        }
    }

    public SelectableChannel get_fd()
    {
        return r;
    }

    public void recv()
    {
        for (int i = 0; i == 0;)
        {
            try
            {
                i = r.read(rdummy);
                rdummy.rewind();
            }
            catch (IOException ioexception)
            {
                throw new ZError.IOException(ioexception);
            }
        }

        rcursor = 1 + rcursor;
    }

    public void send()
    {
        ByteBuffer bytebuffer = ByteBuffer.allocate(1);
        bytebuffer.limit(1);
        int i;
        do
        {
            try
            {
                i = w.write(bytebuffer);
            }
            catch (IOException ioexception)
            {
                throw new ZError.IOException(ioexception);
            }
        } while (i == 0);
        wcursor.incrementAndGet();
        if (!$assertionsDisabled && i != 1)
        {
            throw new AssertionError();
        } else
        {
            return;
        }
    }

    public boolean wait_event(long l)
    {
        if (l != 0L)
        {
            break MISSING_BLOCK_LABEL_22;
        }
        int i;
        return rcursor < wcursor.get();
        if (l < 0L)
        {
            int j;
            try
            {
                i = selector.select(0L);
            }
            catch (IOException ioexception)
            {
                throw new ZError.IOException(ioexception);
            }
            break MISSING_BLOCK_LABEL_68;
        }
        j = selector.select(l);
        i = j;
        while (i != 0) 
        {
            selector.selectedKeys().clear();
            return true;
        }
        return false;
    }

    static 
    {
        boolean flag;
        if (!zmq/Signaler.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
