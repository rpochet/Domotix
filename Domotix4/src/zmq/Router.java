// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

// Referenced classes of package zmq:
//            SocketBase, Utils, Options, FQ, 
//            Msg, Pipe, Blob, ValueReference, 
//            Ctx, SessionBase, IOThread, Address

public class Router extends SocketBase
{
    class Outpipe
    {

        private boolean active;
        private Pipe pipe;
        final Router this$0;




/*
        static boolean access$102(Outpipe outpipe, boolean flag)
        {
            outpipe.active = flag;
            return flag;
        }

*/

        public Outpipe(Pipe pipe1, boolean flag)
        {
            this$0 = Router.this;
            super();
            pipe = pipe1;
            active = flag;
        }
    }

    public static class RouterSession extends SessionBase
    {

        public RouterSession(IOThread iothread, boolean flag, SocketBase socketbase, Options options, Address address)
        {
            super(iothread, flag, socketbase, options, address);
        }
    }


    static final boolean $assertionsDisabled;
    private final Set anonymous_pipes = new HashSet();
    private Pipe current_out;
    private final FQ fq = new FQ();
    private boolean identity_sent;
    private boolean mandatory;
    private boolean more_in;
    private boolean more_out;
    private int next_peer_id;
    private final Map outpipes = new HashMap();
    private boolean prefetched;
    private Msg prefetched_id;
    private Msg prefetched_msg;

    public Router(Ctx ctx, int i, int j)
    {
        super(ctx, i, j);
        prefetched = false;
        identity_sent = false;
        more_in = false;
        current_out = null;
        more_out = false;
        next_peer_id = Utils.generate_random();
        mandatory = false;
        options.type = 6;
        prefetched_id = new Msg();
        prefetched_msg = new Msg();
        options.recv_identity = true;
    }

    private boolean identify_peer(Pipe pipe)
    {
        Msg msg = pipe.read();
        if (msg == null)
        {
            return false;
        }
        Blob blob;
        Outpipe outpipe;
        if (msg.size() == 0)
        {
            ByteBuffer bytebuffer = ByteBuffer.allocate(5);
            bytebuffer.put((byte)0);
            int i = next_peer_id;
            next_peer_id = i + 1;
            bytebuffer.putInt(i);
            bytebuffer.flip();
            blob = new Blob(bytebuffer);
        } else
        {
            blob = new Blob(msg.data());
            if (outpipes.containsKey(blob))
            {
                return false;
            }
        }
        pipe.set_identity(blob);
        outpipe = new Outpipe(pipe, true);
        outpipes.put(blob, outpipe);
        return true;
    }

    protected void rollback()
    {
        if (current_out != null)
        {
            current_out.rollback();
            current_out = null;
            more_out = false;
        }
    }

    public void xattach_pipe(Pipe pipe, boolean flag)
    {
        if (!$assertionsDisabled && pipe == null)
        {
            throw new AssertionError();
        }
        if (identify_peer(pipe))
        {
            fq.attach(pipe);
            return;
        } else
        {
            anonymous_pipes.add(pipe);
            return;
        }
    }

    protected boolean xhas_in()
    {
        if (more_in)
        {
            return true;
        }
        if (prefetched)
        {
            return true;
        }
        ValueReference valuereference = new ValueReference();
        for (prefetched_msg = fq.recvpipe(errno, valuereference); prefetched_msg != null && prefetched_msg.is_identity(); prefetched_msg = fq.recvpipe(errno, valuereference)) { }
        if (prefetched_msg == null)
        {
            return false;
        }
        if (!$assertionsDisabled && valuereference.get() == null)
        {
            throw new AssertionError();
        } else
        {
            prefetched_id = new Msg(((Pipe)valuereference.get()).get_identity().data());
            prefetched_id.set_flags(1);
            prefetched = true;
            identity_sent = false;
            return true;
        }
    }

    protected boolean xhas_out()
    {
        return true;
    }

    public void xread_activated(Pipe pipe)
    {
        if (!anonymous_pipes.contains(pipe))
        {
            fq.activated(pipe);
        } else
        if (identify_peer(pipe))
        {
            anonymous_pipes.remove(pipe);
            fq.attach(pipe);
            return;
        }
    }

    protected Msg xrecv()
    {
        Msg msg1;
        if (prefetched)
        {
            Msg msg2;
            if (!identity_sent)
            {
                msg2 = prefetched_id;
                prefetched_id = null;
                identity_sent = true;
            } else
            {
                msg2 = prefetched_msg;
                prefetched_msg = null;
                prefetched = false;
            }
            more_in = msg2.has_more();
            msg1 = msg2;
        } else
        {
            ValueReference valuereference = new ValueReference();
            Msg msg;
            for (msg = fq.recvpipe(errno, valuereference); msg != null && msg.is_identity(); msg = fq.recvpipe(errno, valuereference)) { }
            msg1 = null;
            if (msg != null)
            {
                if (!$assertionsDisabled && valuereference.get() == null)
                {
                    throw new AssertionError();
                }
                if (more_in)
                {
                    more_in = msg.has_more();
                } else
                {
                    prefetched_msg = msg;
                    prefetched = true;
                    msg = new Msg(((Pipe)valuereference.get()).get_identity().data());
                    msg.set_flags(1);
                    identity_sent = true;
                }
                return msg;
            }
        }
        return msg1;
    }

    protected boolean xsend(Msg msg)
    {
        if (!more_out)
        {
            if (!$assertionsDisabled && current_out != null)
            {
                throw new AssertionError();
            }
            if (msg.has_more())
            {
                more_out = true;
                Blob blob = new Blob(msg.data());
                Outpipe outpipe = (Outpipe)outpipes.get(blob);
                if (outpipe != null)
                {
                    current_out = outpipe.pipe;
                    if (!current_out.check_write())
                    {
                        outpipe.active = false;
                        current_out = null;
                        if (mandatory)
                        {
                            more_out = false;
                            errno.set(Integer.valueOf(35));
                            return false;
                        }
                    }
                } else
                if (mandatory)
                {
                    more_out = false;
                    errno.set(Integer.valueOf(65));
                    return false;
                }
            }
            return true;
        }
        more_out = msg.has_more();
        if (current_out == null) goto _L2; else goto _L1
_L1:
        if (current_out.write(msg)) goto _L4; else goto _L3
_L3:
        current_out = null;
_L2:
        return true;
_L4:
        if (!more_out)
        {
            current_out.flush();
            current_out = null;
        }
        if (true) goto _L2; else goto _L5
_L5:
    }

    public boolean xsetsockopt(int i, Object obj)
    {
        if (i != 33)
        {
            return false;
        }
        int j = ((Integer)obj).intValue();
        boolean flag = false;
        if (j == 1)
        {
            flag = true;
        }
        mandatory = flag;
        return true;
    }

    public void xterminated(Pipe pipe)
    {
        if (!anonymous_pipes.remove(pipe))
        {
            Outpipe outpipe = (Outpipe)outpipes.remove(pipe.get_identity());
            if (!$assertionsDisabled && outpipe == null)
            {
                throw new AssertionError();
            }
            fq.terminated(pipe);
            if (pipe == current_out)
            {
                current_out = null;
            }
        }
    }

    public void xwrite_activated(Pipe pipe)
    {
        Iterator iterator = outpipes.entrySet().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            if (((Outpipe)entry.getValue()).pipe != pipe)
            {
                continue;
            }
            if (!$assertionsDisabled && ((Outpipe)entry.getValue()).active)
            {
                throw new AssertionError();
            }
            ((Outpipe)entry.getValue()).active = true;
            break;
        } while (true);
        if (!$assertionsDisabled)
        {
            throw new AssertionError();
        } else
        {
            return;
        }
    }

    static 
    {
        boolean flag;
        if (!zmq/Router.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
