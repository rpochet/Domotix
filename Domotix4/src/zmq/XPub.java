// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.util.ArrayDeque;
import java.util.Deque;

// Referenced classes of package zmq:
//            SocketBase, Options, Mtrie, Dist, 
//            Pipe, Msg, Blob, ValueReference, 
//            Ctx, SessionBase, IOThread, Address

public class XPub extends SocketBase
{
    public static class XPubSession extends SessionBase
    {

        public XPubSession(IOThread iothread, boolean flag, SocketBase socketbase, Options options, Address address)
        {
            super(iothread, flag, socketbase, options, address);
        }
    }


    static final boolean $assertionsDisabled;
    private static Mtrie.IMtrieHandler mark_as_matching = new Mtrie.IMtrieHandler() {

        public void invoke(Pipe pipe, byte abyte0[], int i, Object obj)
        {
            ((XPub)obj).dist.match(pipe);
        }

    };
    private static Mtrie.IMtrieHandler send_unsubscription = new Mtrie.IMtrieHandler() {

        public void invoke(Pipe pipe, byte abyte0[], int i, Object obj)
        {
            XPub xpub = (XPub)obj;
            if (xpub.options.type != 1)
            {
                Blob blob = new Blob(i + 1);
                blob.put(0, (byte)0);
                blob.put(1, abyte0, 0, i);
                xpub.pending.add(blob);
            }
        }

    };
    private final Dist dist = new Dist();
    private boolean more;
    private final Deque pending = new ArrayDeque();
    private final Mtrie subscriptions = new Mtrie();
    boolean verbose;

    public XPub(Ctx ctx, int i, int j)
    {
        super(ctx, i, j);
        options.type = 9;
        verbose = false;
        more = false;
    }

    protected void xattach_pipe(Pipe pipe, boolean flag)
    {
        if (!$assertionsDisabled && pipe == null)
        {
            throw new AssertionError();
        }
        dist.attach(pipe);
        if (flag)
        {
            subscriptions.add(null, pipe);
        }
        xread_activated(pipe);
    }

    protected boolean xhas_in()
    {
        return !pending.isEmpty();
    }

    protected boolean xhas_out()
    {
        return dist.has_out();
    }

    protected void xread_activated(Pipe pipe)
    {
        do
        {
            Msg msg = pipe.read();
            if (msg == null)
            {
                break;
            }
            byte abyte0[] = msg.data();
            if (msg.size() > 0 && (abyte0[0] == 0 || abyte0[0] == 1))
            {
                boolean flag;
                if (abyte0[0] == 0)
                {
                    flag = subscriptions.rm(abyte0, 1, pipe);
                } else
                {
                    flag = subscriptions.add(abyte0, 1, pipe);
                }
                if (options.type == 9 && (flag || abyte0[0] == 1 && verbose))
                {
                    pending.add(new Blob(msg.data()));
                }
            }
        } while (true);
    }

    protected Msg xrecv()
    {
        if (pending.isEmpty())
        {
            errno.set(Integer.valueOf(35));
            return null;
        } else
        {
            return new Msg(((Blob)pending.pollFirst()).data());
        }
    }

    protected boolean xsend(Msg msg)
    {
        boolean flag = msg.has_more();
        if (!more)
        {
            subscriptions.match(msg.data(), msg.size(), mark_as_matching, this);
        }
        if (!dist.send_to_matching(msg))
        {
            return false;
        }
        if (!flag)
        {
            dist.unmatch();
        }
        more = flag;
        return true;
    }

    public boolean xsetsockopt(int i, Object obj)
    {
        if (i != 40)
        {
            return false;
        }
        int j = ((Integer)obj).intValue();
        boolean flag = false;
        if (j == 1)
        {
            flag = true;
        }
        verbose = flag;
        return true;
    }

    protected void xterminated(Pipe pipe)
    {
        subscriptions.rm(pipe, send_unsubscription, this);
        dist.terminated(pipe);
    }

    protected void xwrite_activated(Pipe pipe)
    {
        dist.activated(pipe);
    }

    static 
    {
        boolean flag;
        if (!zmq/XPub.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }


}
