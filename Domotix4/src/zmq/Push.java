// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            SocketBase, Options, LB, Ctx, 
//            Pipe, Msg, SessionBase, IOThread, 
//            Address

public class Push extends SocketBase
{
    public static class PushSession extends SessionBase
    {

        public PushSession(IOThread iothread, boolean flag, SocketBase socketbase, Options options, Address address)
        {
            super(iothread, flag, socketbase, options, address);
        }
    }


    static final boolean $assertionsDisabled;
    private final LB lb = new LB();

    public Push(Ctx ctx, int i, int j)
    {
        super(ctx, i, j);
        options.type = 8;
    }

    protected void xattach_pipe(Pipe pipe, boolean flag)
    {
        if (!$assertionsDisabled && pipe == null)
        {
            throw new AssertionError();
        } else
        {
            lb.attach(pipe);
            return;
        }
    }

    protected boolean xhas_out()
    {
        return lb.has_out();
    }

    public boolean xsend(Msg msg)
    {
        return lb.send(msg, errno);
    }

    protected void xterminated(Pipe pipe)
    {
        lb.terminated(pipe);
    }

    protected void xwrite_activated(Pipe pipe)
    {
        lb.activated(pipe);
    }

    static 
    {
        boolean flag;
        if (!zmq/Push.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
