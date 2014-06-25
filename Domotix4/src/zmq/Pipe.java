// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            ZObject, Config, Msg, YPipe, 
//            Blob

public class Pipe extends ZObject
{
    public static interface IPipeEvents
    {

        public abstract void hiccuped(Pipe pipe);

        public abstract void read_activated(Pipe pipe);

        public abstract void terminated(Pipe pipe);

        public abstract void write_activated(Pipe pipe);
    }

    static final class State extends Enum
    {

        private static final State $VALUES[];
        public static final State active;
        public static final State delimited;
        public static final State double_terminated;
        public static final State pending;
        public static final State terminated;
        public static final State terminating;

        public static State valueOf(String s)
        {
            return (State)Enum.valueOf(zmq/Pipe$State, s);
        }

        public static State[] values()
        {
            return (State[])$VALUES.clone();
        }

        static 
        {
            active = new State("active", 0);
            delimited = new State("delimited", 1);
            pending = new State("pending", 2);
            terminating = new State("terminating", 3);
            terminated = new State("terminated", 4);
            double_terminated = new State("double_terminated", 5);
            State astate[] = new State[6];
            astate[0] = active;
            astate[1] = delimited;
            astate[2] = pending;
            astate[3] = terminating;
            astate[4] = terminated;
            astate[5] = double_terminated;
            $VALUES = astate;
        }

        private State(String s, int i)
        {
            super(s, i);
        }
    }


    static final boolean $assertionsDisabled;
    private boolean delay;
    private int hwm;
    private Blob identity;
    private boolean in_active;
    private YPipe inpipe;
    private int lwm;
    private long msgs_read;
    private long msgs_written;
    private boolean out_active;
    private YPipe outpipe;
    private ZObject parent;
    private Pipe peer;
    private long peers_msgs_read;
    private IPipeEvents sink;
    private State state;

    private Pipe(ZObject zobject, YPipe ypipe, YPipe ypipe1, int i, int j, boolean flag)
    {
        super(zobject);
        inpipe = ypipe;
        outpipe = ypipe1;
        in_active = true;
        out_active = true;
        hwm = j;
        lwm = compute_lwm(i);
        msgs_read = 0L;
        msgs_written = 0L;
        peers_msgs_read = 0L;
        peer = null;
        sink = null;
        state = State.active;
        delay = flag;
        parent = zobject;
    }

    private static int compute_lwm(int i)
    {
        if (i > 2 * Config.max_wm_delta.getValue())
        {
            return i - Config.max_wm_delta.getValue();
        } else
        {
            return (i + 1) / 2;
        }
    }

    private void delimit()
    {
        if (state == State.active)
        {
            state = State.delimited;
        } else
        {
            if (state == State.pending)
            {
                outpipe = null;
                send_pipe_term_ack(peer);
                state = State.terminating;
                return;
            }
            if (!$assertionsDisabled)
            {
                throw new AssertionError();
            }
        }
    }

    private static boolean is_delimiter(Msg msg)
    {
        return msg.is_delimiter();
    }

    public static void pipepair(ZObject azobject[], Pipe apipe[], int ai[], boolean aflag[])
    {
        YPipe ypipe = new YPipe(zmq/Msg, Config.message_pipe_granularity.getValue());
        YPipe ypipe1 = new YPipe(zmq/Msg, Config.message_pipe_granularity.getValue());
        apipe[0] = new Pipe(azobject[0], ypipe, ypipe1, ai[1], ai[0], aflag[0]);
        apipe[1] = new Pipe(azobject[1], ypipe1, ypipe, ai[0], ai[1], aflag[1]);
        apipe[0].set_peer(apipe[1]);
        apipe[1].set_peer(apipe[0]);
    }

    private void set_peer(Pipe pipe)
    {
        if (!$assertionsDisabled && pipe == null)
        {
            throw new AssertionError();
        } else
        {
            peer = pipe;
            return;
        }
    }

    public boolean check_read()
    {
        if (!in_active || state != State.active && state != State.pending)
        {
            return false;
        }
        if (!inpipe.check_read())
        {
            in_active = false;
            return false;
        }
        if (is_delimiter((Msg)inpipe.probe()))
        {
            Msg msg = (Msg)inpipe.read();
            if (!$assertionsDisabled && msg == null)
            {
                throw new AssertionError();
            } else
            {
                delimit();
                return false;
            }
        } else
        {
            return true;
        }
    }

    public boolean check_write()
    {
        if (!out_active || state != State.active)
        {
            return false;
        }
        boolean flag;
        if (hwm > 0 && msgs_written - peers_msgs_read == (long)hwm)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (flag)
        {
            out_active = false;
            return false;
        } else
        {
            return true;
        }
    }

    public void flush()
    {
        while (state == State.terminating || outpipe == null || outpipe.flush()) 
        {
            return;
        }
        send_activate_read(peer);
    }

    public Blob get_identity()
    {
        return identity;
    }

    public void hiccup()
    {
        if (state != State.active)
        {
            return;
        } else
        {
            inpipe = null;
            inpipe = new YPipe(zmq/Msg, Config.message_pipe_granularity.getValue());
            in_active = true;
            send_hiccup(peer, inpipe);
            return;
        }
    }

    protected void process_activate_read()
    {
        if (!in_active && (state == State.active || state == State.pending))
        {
            in_active = true;
            sink.read_activated(this);
        }
    }

    protected void process_activate_write(long l)
    {
        peers_msgs_read = l;
        if (!out_active && state == State.active)
        {
            out_active = true;
            sink.write_activated(this);
        }
    }

    protected void process_hiccup(Object obj)
    {
        if (!$assertionsDisabled && outpipe == null)
        {
            throw new AssertionError();
        }
        outpipe.flush();
        while (outpipe.read() != null) ;
        if (!$assertionsDisabled && obj == null)
        {
            throw new AssertionError();
        }
        outpipe = (YPipe)obj;
        out_active = true;
        if (state == State.active)
        {
            sink.hiccuped(this);
        }
    }

    protected void process_pipe_term()
    {
        if (state != State.active) goto _L2; else goto _L1
_L1:
        if (delay) goto _L4; else goto _L3
_L3:
        state = State.terminating;
        outpipe = null;
        send_pipe_term_ack(peer);
_L6:
        return;
_L4:
        state = State.pending;
        return;
_L2:
        if (state == State.delimited)
        {
            state = State.terminating;
            outpipe = null;
            send_pipe_term_ack(peer);
            return;
        }
        if (state == State.terminated)
        {
            state = State.double_terminated;
            outpipe = null;
            send_pipe_term_ack(peer);
            return;
        }
        if (!$assertionsDisabled)
        {
            throw new AssertionError();
        }
        if (true) goto _L6; else goto _L5
_L5:
    }

    protected void process_pipe_term_ack()
    {
        if (!$assertionsDisabled && sink == null)
        {
            throw new AssertionError();
        }
        sink.terminated(this);
        if (state == State.terminated)
        {
            outpipe = null;
            send_pipe_term_ack(peer);
        } else
        if (!$assertionsDisabled && state != State.terminating && state != State.double_terminated)
        {
            throw new AssertionError();
        }
        while (inpipe.read() != null) ;
        inpipe = null;
    }

    public Msg read()
    {
        Msg msg;
        if (!in_active || state != State.active && state != State.pending)
        {
            msg = null;
        } else
        {
            msg = (Msg)inpipe.read();
            if (msg == null)
            {
                in_active = false;
                return null;
            }
            if (msg.is_delimiter())
            {
                delimit();
                return null;
            }
            if (!msg.has_more())
            {
                msgs_read = 1L + msgs_read;
            }
            if (lwm > 0 && msgs_read % (long)lwm == 0L)
            {
                send_activate_write(peer, msgs_read);
                return msg;
            }
        }
        return msg;
    }

    public void rollback()
    {
        Msg msg;
        if (outpipe != null)
        {
            do
            {
                msg = (Msg)outpipe.unwrite();
                if (msg == null)
                {
                    break;
                }
                if (!$assertionsDisabled && (1 & msg.flags()) <= 0)
                {
                    throw new AssertionError();
                }
            } while (true);
        }
    }

    public void set_event_sink(IPipeEvents ipipeevents)
    {
        if (!$assertionsDisabled && sink != null)
        {
            throw new AssertionError();
        } else
        {
            sink = ipipeevents;
            return;
        }
    }

    public void set_identity(Blob blob)
    {
        identity = blob;
    }

    public void terminate(boolean flag)
    {
        delay = flag;
        break MISSING_BLOCK_LABEL_5;
_L2:
        do
        {
            return;
        } while (state == State.terminated || state == State.double_terminated || state == State.terminating);
        if (state != State.active)
        {
            break; /* Loop/switch isn't completed */
        }
        send_pipe_term(peer);
        state = State.terminated;
_L3:
        out_active = false;
        if (outpipe != null)
        {
            rollback();
            Msg msg = new Msg();
            msg.init_delimiter();
            outpipe.write(msg, false);
            flush();
            return;
        }
        if (true) goto _L2; else goto _L1
_L1:
        if (state == State.pending && !delay)
        {
            outpipe = null;
            send_pipe_term_ack(peer);
            state = State.terminating;
        } else
        if (state != State.pending)
        {
            if (state != State.delimited)
            {
                continue; /* Loop/switch isn't completed */
            }
            send_pipe_term(peer);
            state = State.terminated;
        }
          goto _L3
        continue; /* Loop/switch isn't completed */
        if ($assertionsDisabled) goto _L3; else goto _L4
_L4:
        throw new AssertionError();
        if (true) goto _L2; else goto _L5
_L5:
    }

    public String toString()
    {
        return (new StringBuilder()).append(super.toString()).append("[").append(parent).append("]").toString();
    }

    public boolean write(Msg msg)
    {
        if (!check_write())
        {
            return false;
        }
        boolean flag = msg.has_more();
        outpipe.write(msg, flag);
        if (!flag)
        {
            msgs_written = 1L + msgs_written;
        }
        return true;
    }

    static 
    {
        boolean flag;
        if (!zmq/Pipe.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
