// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.io.IOException;

// Referenced classes of package zmq:
//            ZError

public static class ion extends RuntimeException
{

    private static final long serialVersionUID = 0x7fb5be563a6a4bd6L;

    public ion(IOException ioexception)
    {
        super(ioexception);
    }
}
