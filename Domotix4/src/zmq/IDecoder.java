// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.ByteBuffer;

// Referenced classes of package zmq:
//            IMsgSink

public interface IDecoder
{

    public abstract ByteBuffer get_buffer();

    public abstract int process_buffer(ByteBuffer bytebuffer, int i);

    public abstract void set_msg_sink(IMsgSink imsgsink);

    public abstract boolean stalled();
}
