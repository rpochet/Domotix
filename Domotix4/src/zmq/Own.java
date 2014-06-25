// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

// Referenced classes of package zmq:
//            ZObject, Options, Ctx, IOThread

public abstract class Own extends ZObject
{

    static final boolean $assertionsDisabled;
    protected final Options options;
    private final Set owned;
    private Own owner;
    private long processed_seqnum;
    private final AtomicLong sent_seqnum;
    private int term_acks;
    private boolean terminating;

    public Own(Ctx ctx, int i)
    {
        super(ctx, i);
        terminating = false;
        sent_seqnum = new AtomicLong(0L);
        processed_seqnum = 0L;
        owner = null;
        term_acks = 0;
        options = new Options();
        owned = new HashSet();
    }

    public Own(IOThread iothread, Options options1)
    {
        super(iothread);
        options = options1;
        terminating = false;
        sent_seqnum = new AtomicLong(0L);
        processed_seqnum = 0L;
        owner = null;
        term_acks = 0;
        owned = new HashSet();
    }

    private void check_term_acks()
    {
        if (terminating && processed_seqnum == sent_seqnum.get() && term_acks == 0)
        {
            if (!$assertionsDisabled && !owned.isEmpty())
            {
                throw new AssertionError();
            }
            if (owner != null)
            {
                send_term_ack(owner);
            }
            process_destroy();
        }
    }

    private void set_owner(Own own)
    {
        if (!$assertionsDisabled && owner != null)
        {
            throw new AssertionError();
        } else
        {
            owner = own;
            return;
        }
    }

    public abstract void destroy();

    public void inc_seqnum()
    {
        sent_seqnum.incrementAndGet();
    }

    protected boolean is_terminating()
    {
        return terminating;
    }

    protected void launch_child(Own own)
    {
        own.set_owner(this);
        send_plug(own);
        send_own(this, own);
    }

    protected void process_destroy()
    {
        destroy();
    }

    protected void process_own(Own own)
    {
        if (terminating)
        {
            register_term_acks(1);
            send_term(own, 0);
            return;
        } else
        {
            owned.add(own);
            return;
        }
    }

    protected void process_seqnum()
    {
        processed_seqnum = 1L + processed_seqnum;
        check_term_acks();
    }

    protected void process_term(int i)
    {
        if (!$assertionsDisabled && terminating)
        {
            throw new AssertionError();
        }
        for (Iterator iterator = owned.iterator(); iterator.hasNext(); send_term((Own)iterator.next(), i)) { }
        register_term_acks(owned.size());
        owned.clear();
        terminating = true;
        check_term_acks();
    }

    protected void process_term_ack()
    {
        unregister_term_ack();
    }

    protected void process_term_req(Own own)
    {
        while (terminating || !owned.contains(own)) 
        {
            return;
        }
        owned.remove(own);
        register_term_acks(1);
        send_term(own, options.linger);
    }

    public void register_term_acks(int i)
    {
        term_acks = i + term_acks;
    }

    protected void term_child(Own own)
    {
        process_term_req(own);
    }

    protected void terminate()
    {
        if (terminating)
        {
            return;
        }
        if (owner == null)
        {
            process_term(options.linger);
            return;
        } else
        {
            send_term_req(owner, this);
            return;
        }
    }

    public void unregister_term_ack()
    {
        if (!$assertionsDisabled && term_acks <= 0)
        {
            throw new AssertionError();
        } else
        {
            term_acks = -1 + term_acks;
            check_term_acks();
            return;
        }
    }

    static 
    {
        boolean flag;
        if (!zmq/Own.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
