// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            XSub, Options, Msg, Ctx, 
//            IOThread, SocketBase, Address

public class Sub extends XSub
{
    public static class SubSession extends XSub.XSubSession
    {

        public SubSession(IOThread iothread, boolean flag, SocketBase socketbase, Options options, Address address)
        {
            super(iothread, flag, socketbase, options, address);
        }
    }


    public Sub(Ctx ctx, int i, int j)
    {
        super(ctx, i, j);
        options.type = 2;
        options.filter = true;
    }

    protected boolean xhas_out()
    {
        return false;
    }

    protected boolean xsend(Msg msg)
    {
        throw new UnsupportedOperationException();
    }

    public boolean xsetsockopt(int i, Object obj)
    {
        if (i != 6 && i != 7)
        {
            return false;
        }
        byte abyte0[];
        Msg msg;
        if (obj instanceof String)
        {
            abyte0 = ((String)obj).getBytes();
        } else
        if (obj instanceof byte[])
        {
            abyte0 = (byte[])(byte[])obj;
        } else
        {
            throw new IllegalArgumentException();
        }
        msg = new Msg(1 + abyte0.length);
        if (i == 6)
        {
            msg.put((byte)1);
        } else
        if (i == 7)
        {
            msg.put((byte)0);
        }
        msg.put(abyte0, 1);
        if (!super.xsend(msg))
        {
            throw new IllegalStateException("Failed to send subscribe/unsubscribe message");
        } else
        {
            return true;
        }
    }
}
