// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            XPub, Options, Ctx, Msg, 
//            IOThread, SocketBase, Address

public class Pub extends XPub
{
    public static class PubSession extends XPub.XPubSession
    {

        public PubSession(IOThread iothread, boolean flag, SocketBase socketbase, Options options, Address address)
        {
            super(iothread, flag, socketbase, options, address);
        }
    }


    Pub(Ctx ctx, int i, int j)
    {
        super(ctx, i, j);
        options.type = 1;
    }

    protected boolean xhas_in()
    {
        return false;
    }

    protected Msg xrecv()
    {
        throw new UnsupportedOperationException();
    }
}