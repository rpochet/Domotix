// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.util.Deque;

// Referenced classes of package zmq:
//            XPub, Options, Blob, Pipe

static final class bject
    implements MtrieHandler
{

    public void invoke(Pipe pipe, byte abyte0[], int i, Object obj)
    {
        XPub xpub = (XPub)obj;
        if (xpub.options.type != 1)
        {
            Blob blob = new Blob(i + 1);
            blob.put(0, (byte)0);
            blob.put(1, abyte0, 0, i);
            XPub.access$100(xpub).add(blob);
        }
    }

    bject()
    {
    }
}
