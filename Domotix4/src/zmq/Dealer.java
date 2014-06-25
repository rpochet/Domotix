// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            SocketBase, Options, FQ, LB, 
//            Msg, Ctx, Pipe, SessionBase, 
//            IOThread, Address

public class Dealer extends SocketBase
{
    public static class DealerSession extends SessionBase
    {

        public DealerSession(IOThread iothread, boolean flag, SocketBase socketbase, Options options, Address address)
        {
            super(iothread, flag, socketbase, options, address);
        }
    }


    static final boolean $assertionsDisabled;
    private final FQ fq = new FQ();
    private final LB lb = new LB();
    private boolean prefetched;
    private Msg prefetched_msg;

    public Dealer(Ctx ctx, int i, int j)
    {
        super(ctx, i, j);
        prefetched = false;
        options.type = 5;
        options.recv_identity = true;
    }

    private Msg xxrecv()
    {
        Msg msg1;
        if (prefetched)
        {
            Msg msg2 = prefetched_msg;
            prefetched = false;
            prefetched_msg = null;
            msg1 = msg2;
        } else
        {
            do
            {
                msg = fq.recv(errno);
                msg1 = null;
                if (msg == null)
                {
                    continue; /* Loop/switch isn't completed */
                }
                if ((0x40 & msg.flags()) == 0)
                {
                    return msg;
                }
            } while (true);
        }
_L2:
        Msg msg;
        return msg1;
        if (true) goto _L2; else goto _L1
_L1:
    }

    protected void xattach_pipe(Pipe pipe, boolean flag)
    {
        if (!$assertionsDisabled && pipe == null)
        {
            throw new AssertionError();
        } else
        {
            fq.attach(pipe);
            lb.attach(pipe);
            return;
        }
    }

    protected boolean xhas_in()
    {
        if (prefetched)
        {
            return true;
        }
        prefetched_msg = xxrecv();
        if (prefetched_msg == null)
        {
            return false;
        } else
        {
            prefetched = true;
            return true;
        }
    }

    protected boolean xhas_out()
    {
        return lb.has_out();
    }

    protected void xread_activated(Pipe pipe)
    {
        fq.activated(pipe);
    }

    protected Msg xrecv()
    {
        return xxrecv();
    }

    protected boolean xsend(Msg msg)
    {
        return lb.send(msg, errno);
    }

    protected void xterminated(Pipe pipe)
    {
        fq.terminated(pipe);
        lb.terminated(pipe);
    }

    protected void xwrite_activated(Pipe pipe)
    {
        lb.activated(pipe);
    }

    static 
    {
        boolean flag;
        if (!zmq/Dealer.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
