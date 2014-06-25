// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.ByteBuffer;

// Referenced classes of package zmq:
//            Transfer, IMsgSource

public interface IEncoder
{

    public abstract Transfer get_data(ByteBuffer bytebuffer);

    public abstract boolean has_data();

    public abstract void set_msg_source(IMsgSource imsgsource);
}
