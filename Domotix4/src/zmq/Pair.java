// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            SocketBase, Options, Pipe, ValueReference, 
//            Msg, Ctx, SessionBase, IOThread, 
//            Address

public class Pair extends SocketBase
{
    public static class PairSession extends SessionBase
    {

        public PairSession(IOThread iothread, boolean flag, SocketBase socketbase, Options options, Address address)
        {
            super(iothread, flag, socketbase, options, address);
        }
    }


    static final boolean $assertionsDisabled;
    private Pipe pipe;

    Pair(Ctx ctx, int i, int j)
    {
        super(ctx, i, j);
        options.type = 0;
    }

    protected void xattach_pipe(Pipe pipe1, boolean flag)
    {
        if (!$assertionsDisabled && pipe1 == null)
        {
            throw new AssertionError();
        }
        if (pipe == null)
        {
            pipe = pipe1;
            return;
        } else
        {
            pipe1.terminate(false);
            return;
        }
    }

    protected boolean xhas_in()
    {
        if (pipe == null)
        {
            return false;
        } else
        {
            return pipe.check_read();
        }
    }

    protected boolean xhas_out()
    {
        if (pipe == null)
        {
            return false;
        } else
        {
            return pipe.check_write();
        }
    }

    protected void xread_activated(Pipe pipe1)
    {
    }

    protected Msg xrecv()
    {
        Msg msg;
label0:
        {
            if (pipe != null)
            {
                msg = pipe.read();
                if (msg != null)
                {
                    break label0;
                }
            }
            errno.set(Integer.valueOf(35));
            return null;
        }
        return msg;
    }

    protected boolean xsend(Msg msg)
    {
        if (pipe == null || !pipe.write(msg))
        {
            errno.set(Integer.valueOf(35));
            return false;
        }
        if ((2 & msg.flags()) == 0)
        {
            pipe.flush();
        }
        return true;
    }

    protected void xterminated(Pipe pipe1)
    {
        if (pipe1 == pipe)
        {
            pipe = null;
        }
    }

    protected void xwrite_activated(Pipe pipe1)
    {
    }

    static 
    {
        boolean flag;
        if (!zmq/Pair.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
