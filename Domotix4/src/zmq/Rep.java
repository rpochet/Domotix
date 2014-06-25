// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            Router, Options, Msg, Ctx, 
//            IOThread, SocketBase, Address

public class Rep extends Router
{
    public static class RepSession extends Router.RouterSession
    {

        public RepSession(IOThread iothread, boolean flag, SocketBase socketbase, Options options, Address address)
        {
            super(iothread, flag, socketbase, options, address);
        }
    }


    static final boolean $assertionsDisabled;
    private boolean request_begins;
    private boolean sending_reply;

    public Rep(Ctx ctx, int i, int j)
    {
        super(ctx, i, j);
        sending_reply = false;
        request_begins = true;
        options.type = 4;
    }

    protected boolean xhas_in()
    {
        if (sending_reply)
        {
            return false;
        } else
        {
            return super.xhas_in();
        }
    }

    protected boolean xhas_out()
    {
        if (!sending_reply)
        {
            return false;
        } else
        {
            return super.xhas_out();
        }
    }

    protected Msg xrecv()
    {
        if (sending_reply)
        {
            throw new IllegalStateException("Cannot receive another request");
        }
        if (!request_begins) goto _L2; else goto _L1
_L1:
        Msg msg1;
        msg1 = super.xrecv();
        if (msg1 == null)
        {
            return null;
        }
        if (!msg1.has_more()) goto _L4; else goto _L3
_L3:
        boolean flag;
        boolean flag1;
        if (msg1.size() == 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        flag1 = super.xsend(msg1);
        if (!$assertionsDisabled && !flag1)
        {
            throw new AssertionError();
        }
        if (!flag)
        {
            continue; /* Loop/switch isn't completed */
        }
        request_begins = false;
_L2:
        Msg msg;
        msg = super.xrecv();
        if (msg == null)
        {
            return null;
        }
        break; /* Loop/switch isn't completed */
_L4:
        super.rollback();
        if (true) goto _L1; else goto _L5
_L5:
        if (!msg.has_more())
        {
            sending_reply = true;
            request_begins = true;
        }
        return msg;
    }

    protected boolean xsend(Msg msg)
    {
        if (!sending_reply)
        {
            throw new IllegalStateException("Cannot send another reply");
        }
        boolean flag = msg.has_more();
        boolean flag1 = super.xsend(msg);
        if (!flag1)
        {
            return flag1;
        }
        if (!flag)
        {
            sending_reply = false;
        }
        return true;
    }

    static 
    {
        boolean flag;
        if (!zmq/Rep.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
