// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            SocketBase, Options, FQ, Dist, 
//            Trie, Msg, Pipe, Ctx, 
//            SessionBase, IOThread, Address

public class XSub extends SocketBase
{
    public static class XSubSession extends SessionBase
    {

        public XSubSession(IOThread iothread, boolean flag, SocketBase socketbase, Options options, Address address)
        {
            super(iothread, flag, socketbase, options, address);
        }
    }


    static final boolean $assertionsDisabled;
    private static Trie.ITrieHandler send_subscription = new Trie.ITrieHandler() {

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

    };
    private final Dist dist = new Dist();
    private final FQ fq = new FQ();
    private boolean has_message;
    private Msg message;
    private boolean more;
    private final Trie subscriptions = new Trie();

    public XSub(Ctx ctx, int i, int j)
    {
        super(ctx, i, j);
        options.type = 10;
        has_message = false;
        more = false;
        options.linger = 0;
    }

    private boolean match(Msg msg)
    {
        return subscriptions.check(msg.data());
    }

    protected void xattach_pipe(Pipe pipe, boolean flag)
    {
        if (!$assertionsDisabled && pipe == null)
        {
            throw new AssertionError();
        } else
        {
            fq.attach(pipe);
            dist.attach(pipe);
            subscriptions.apply(send_subscription, pipe);
            pipe.flush();
            return;
        }
    }

    protected boolean xhas_in()
    {
        while (more || has_message) 
        {
            return true;
        }
label0:
        do
        {
            message = fq.recv(errno);
            if (message == null)
            {
                return false;
            }
            if (!options.filter || match(message))
            {
                has_message = true;
                return true;
            }
            do
            {
                if (!message.has_more())
                {
                    continue label0;
                }
                message = fq.recv(errno);
            } while ($assertionsDisabled || message != null);
            break;
        } while (true);
        throw new AssertionError();
    }

    protected boolean xhas_out()
    {
        return true;
    }

    protected void xhiccuped(Pipe pipe)
    {
        subscriptions.apply(send_subscription, pipe);
        pipe.flush();
    }

    protected void xread_activated(Pipe pipe)
    {
        fq.activated(pipe);
    }

    protected Msg xrecv()
    {
        if (has_message)
        {
            Msg msg1 = message;
            has_message = false;
            more = msg1.has_more();
            return msg1;
        }
label0:
        do
        {
            Msg msg = fq.recv(errno);
            if (msg == null)
            {
                return null;
            }
            if (more || !options.filter || match(msg))
            {
                more = msg.has_more();
                return msg;
            }
            do
            {
                if (!msg.has_more())
                {
                    continue label0;
                }
                msg = fq.recv(errno);
            } while ($assertionsDisabled || msg != null);
            break;
        } while (true);
        throw new AssertionError();
    }

    protected boolean xsend(Msg msg)
    {
        boolean flag = true;
        byte abyte0[] = msg.data();
        if (abyte0.length < flag || abyte0[0] != 0 && abyte0[0] != flag)
        {
            throw new IllegalArgumentException("subscription flag");
        }
        if (abyte0[0] == flag)
        {
            subscriptions.add(abyte0, flag);
            flag = dist.send_to_all(msg);
        } else
        if (subscriptions.rm(abyte0, flag))
        {
            return dist.send_to_all(msg);
        }
        return flag;
    }

    protected void xterminated(Pipe pipe)
    {
        fq.terminated(pipe);
        dist.terminated(pipe);
    }

    protected void xwrite_activated(Pipe pipe)
    {
        dist.activated(pipe);
    }

    static 
    {
        boolean flag;
        if (!zmq/XSub.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
