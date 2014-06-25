// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

// Referenced classes of package zmq:
//            Transfer

public static class buf
    implements Transfer
{

    private ByteBuffer buf;

    public final int remaining()
    {
        return buf.remaining();
    }

    public final int transferTo(WritableByteChannel writablebytechannel)
        throws IOException
    {
        return writablebytechannel.write(buf);
    }

    public annel(ByteBuffer bytebuffer)
    {
        buf = bytebuffer;
    }
}
