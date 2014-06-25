// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            XSub, Pipe, Msg

static final class bject
    implements rieHandler
{

    public void added(byte abyte0[], int i, Object obj)
    {
        Pipe pipe = (Pipe)obj;
        Msg msg = new Msg(i + 1);
        msg.put((byte)1);
        msg.put(abyte0, 1, i);
        if (!pipe.write(msg))
        {
            msg.close();
        }
    }

    bject()
    {
    }
}
