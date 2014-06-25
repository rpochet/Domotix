// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            SocketBase, Options, FQ, Ctx, 
//            Pipe, Msg, SessionBase, IOThread, 
//            Address

public class Pull extends SocketBase
{
    public static class PullSession extends SessionBase
    {

        public PullSession(IOThread iothread, boolean flag, SocketBase socketbase, Options options, Address address)
        {
            super(iothread, flag, socketbase, options, address);
        }
    }


    static final boolean $assertionsDisabled;
    private final FQ fq = new FQ();

    public Pull(Ctx ctx, int i, int j)
    {
        super(ctx, i, j);
        options.type = 7;
    }

    protected void xattach_pipe(Pipe pipe, boolean flag)
    {
        if (!$assertionsDisabled && pipe == null)
        {
            throw new AssertionError();
        } else
        {
            fq.attach(pipe);
            return;
        }
    }

    protected boolean xhas_in()
    {
        return fq.has_in();
    }

    protected void xread_activated(Pipe pipe)
    {
        fq.activated(pipe);
    }

    public Msg xrecv()
    {
        return fq.recv(errno);
    }

    protected void xterminated(Pipe pipe)
    {
        fq.terminated(pipe);
    }

    static 
    {
        boolean flag;
        if (!zmq/Pull.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
