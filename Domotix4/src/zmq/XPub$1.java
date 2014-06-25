// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            XPub, Dist, Pipe

static final class bject
    implements MtrieHandler
{

    public void invoke(Pipe pipe, byte abyte0[], int i, Object obj)
    {
        XPub.access$000((XPub)obj).match(pipe);
    }

    bject()
    {
    }
}
