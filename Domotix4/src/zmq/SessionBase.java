// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.util.HashSet;
import java.util.Set;

// Referenced classes of package zmq:
//            Own, IPollEvents, IMsgSink, IMsgSource, 
//            IOObject, Pipe, Msg, Options, 
//            Address, TcpConnecter, IpcConnecter, IEngine, 
//            ZObject, IOThread, SocketBase

public class SessionBase extends Own
    implements Pipe.IPipeEvents, IPollEvents, IMsgSink, IMsgSource
{

    static final boolean $assertionsDisabled;
    private static int linger_timer_id = 32;
    private final Address addr;
    private boolean connect;
    private IEngine engine;
    private boolean has_linger_timer;
    private boolean identity_received;
    private boolean identity_sent;
    private boolean incomplete_in;
    private IOObject io_object;
    private IOThread io_thread;
    private boolean pending;
    private Pipe pipe;
    private SocketBase socket;
    private final Set terminating_pipes = new HashSet();

    public SessionBase(IOThread iothread, boolean flag, SocketBase socketbase, Options options, Address address)
    {
        super(iothread, options);
        io_object = new IOObject(iothread);
        connect = flag;
        pipe = null;
        incomplete_in = false;
        pending = false;
        engine = null;
        socket = socketbase;
        io_thread = iothread;
        has_linger_timer = false;
        identity_sent = false;
        identity_received = false;
        addr = address;
    }

    private void clean_pipes()
    {
        if (pipe != null)
        {
            pipe.rollback();
            pipe.flush();
            do
            {
                if (!incomplete_in)
                {
                    break;
                }
                Msg msg = pull_msg();
                if (msg == null)
                {
                    if (!$assertionsDisabled && incomplete_in)
                    {
                        throw new AssertionError();
                    }
                    break;
                }
                msg.close();
            } while (true);
        }
    }

    public static SessionBase create(IOThread iothread, boolean flag, SocketBase socketbase, Options options, Address address)
    {
        switch (options.type)
        {
        default:
            throw new IllegalArgumentException((new StringBuilder()).append("type=").append(options.type).toString());

        case 3: // '\003'
            return new Req.ReqSession(iothread, flag, socketbase, options, address);

        case 5: // '\005'
            return new Dealer.DealerSession(iothread, flag, socketbase, options, address);

        case 4: // '\004'
            return new Rep.RepSession(iothread, flag, socketbase, options, address);

        case 6: // '\006'
            return new Router.RouterSession(iothread, flag, socketbase, options, address);

        case 1: // '\001'
            return new Pub.PubSession(iothread, flag, socketbase, options, address);

        case 9: // '\t'
            return new XPub.XPubSession(iothread, flag, socketbase, options, address);

        case 2: // '\002'
            return new Sub.SubSession(iothread, flag, socketbase, options, address);

        case 10: // '\n'
            return new XSub.XSubSession(iothread, flag, socketbase, options, address);

        case 8: // '\b'
            return new Push.PushSession(iothread, flag, socketbase, options, address);

        case 7: // '\007'
            return new Pull.PullSession(iothread, flag, socketbase, options, address);

        case 0: // '\0'
            return new Pair.PairSession(iothread, flag, socketbase, options, address);
        }
    }

    private void detached()
    {
        if (!connect)
        {
            terminate();
        } else
        {
            if (pipe != null && options.delay_attach_on_connect == 1 && addr.protocol() != "pgm" && addr.protocol() != "epgm")
            {
                pipe.hiccup();
                pipe.terminate(false);
                terminating_pipes.add(pipe);
                pipe = null;
            }
            reset();
            if (options.reconnect_ivl != -1)
            {
                start_connecting(true);
            }
            if (pipe != null && (options.type == 2 || options.type == 10))
            {
                pipe.hiccup();
                return;
            }
        }
    }

    private void proceed_with_term()
    {
        pending = false;
        super.process_term(0);
    }

    private void start_connecting(boolean flag)
    {
        if (!$assertionsDisabled && !connect)
        {
            throw new AssertionError();
        }
        IOThread iothread = choose_io_thread(options.affinity);
        if (!$assertionsDisabled && iothread == null)
        {
            throw new AssertionError();
        }
        if (addr.protocol().equals("tcp"))
        {
            launch_child(new TcpConnecter(iothread, this, options, addr, flag));
        } else
        {
            if (addr.protocol().equals("ipc"))
            {
                launch_child(new IpcConnecter(iothread, this, options, addr, flag));
                return;
            }
            if (!$assertionsDisabled)
            {
                throw new AssertionError();
            }
        }
    }

    public void accept_event()
    {
        throw new UnsupportedOperationException();
    }

    public void attach_pipe(Pipe pipe1)
    {
        if (!$assertionsDisabled && is_terminating())
        {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && pipe != null)
        {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && pipe1 == null)
        {
            throw new AssertionError();
        } else
        {
            pipe = pipe1;
            pipe.set_event_sink(this);
            return;
        }
    }

    public void connect_event()
    {
        throw new UnsupportedOperationException();
    }

    public void destroy()
    {
        if (!$assertionsDisabled && pipe != null)
        {
            throw new AssertionError();
        }
        if (has_linger_timer)
        {
            io_object.cancel_timer(linger_timer_id);
            has_linger_timer = false;
        }
        if (engine != null)
        {
            engine.terminate();
        }
    }

    public void detach()
    {
        engine = null;
        clean_pipes();
        detached();
        if (pipe != null)
        {
            pipe.check_read();
        }
    }

    public void flush()
    {
        if (pipe != null)
        {
            pipe.flush();
        }
    }

    public SocketBase get_soket()
    {
        return socket;
    }

    public void hiccuped(Pipe pipe1)
    {
        throw new UnsupportedOperationException("Must Override");
    }

    public void in_event()
    {
        throw new UnsupportedOperationException();
    }

    public void out_event()
    {
        throw new UnsupportedOperationException();
    }

    protected void process_attach(IEngine iengine)
    {
        if (!$assertionsDisabled && iengine == null)
        {
            throw new AssertionError();
        }
        if (pipe == null && !is_terminating())
        {
            ZObject azobject[] = new ZObject[2];
            azobject[0] = this;
            azobject[1] = socket;
            Pipe apipe[] = {
                null, null
            };
            int ai[] = new int[2];
            ai[0] = options.rcvhwm;
            ai[1] = options.sndhwm;
            boolean aflag[] = new boolean[2];
            aflag[0] = options.delay_on_close;
            aflag[1] = options.delay_on_disconnect;
            Pipe.pipepair(azobject, apipe, ai, aflag);
            apipe[0].set_event_sink(this);
            if (!$assertionsDisabled && pipe != null)
            {
                throw new AssertionError();
            }
            pipe = apipe[0];
            send_bind(socket, apipe[1]);
        }
        if (!$assertionsDisabled && engine != null)
        {
            throw new AssertionError();
        } else
        {
            engine = iengine;
            engine.plug(io_thread, this);
            return;
        }
    }

    protected void process_plug()
    {
        io_object.set_handler(this);
        if (connect)
        {
            start_connecting(false);
        }
    }

    protected void process_term(int i)
    {
        boolean flag = true;
        if (!$assertionsDisabled && pending)
        {
            throw new AssertionError();
        }
        if (pipe == null)
        {
            proceed_with_term();
            return;
        }
        pending = flag;
        if (i > 0)
        {
            if (!$assertionsDisabled && has_linger_timer)
            {
                throw new AssertionError();
            }
            io_object.add_timer(i, linger_timer_id);
            has_linger_timer = flag;
        }
        Pipe pipe1 = pipe;
        if (i == 0)
        {
            flag = false;
        }
        pipe1.terminate(flag);
        pipe.check_read();
    }

    public Msg pull_msg()
    {
        Msg msg1;
label0:
        {
            if (!identity_sent)
            {
                Msg msg = new Msg(options.identity_size);
                msg.put(options.identity, 0, options.identity_size);
                identity_sent = true;
                incomplete_in = false;
                return msg;
            }
            if (pipe != null)
            {
                msg1 = pipe.read();
                if (msg1 != null)
                {
                    break label0;
                }
            }
            return null;
        }
        incomplete_in = msg1.has_more();
        return msg1;
    }

    public int push_msg(Msg msg)
    {
        if (identity_received) goto _L2; else goto _L1
_L1:
        msg.set_flags(64);
        identity_received = true;
        if (options.recv_identity) goto _L2; else goto _L3
_L3:
        return 0;
_L2:
        if (pipe == null || !pipe.write(msg))
        {
            return 35;
        }
        if (true) goto _L3; else goto _L4
_L4:
    }

    public void read_activated(Pipe pipe1)
    {
label0:
        {
            if (pipe != pipe1)
            {
                if (!$assertionsDisabled && !terminating_pipes.contains(pipe1))
                {
                    throw new AssertionError();
                }
            } else
            {
                if (engine == null)
                {
                    break label0;
                }
                engine.activate_out();
            }
            return;
        }
        pipe.check_read();
    }

    protected void reset()
    {
        identity_sent = false;
        identity_received = false;
    }

    public void terminated(Pipe pipe1)
    {
        if (!$assertionsDisabled && pipe != pipe1 && !terminating_pipes.contains(pipe1))
        {
            throw new AssertionError();
        }
        if (pipe == pipe1)
        {
            pipe = null;
        } else
        {
            terminating_pipes.remove(pipe1);
        }
        if (pending && pipe == null && terminating_pipes.size() == 0)
        {
            proceed_with_term();
        }
    }

    public void timer_event(int i)
    {
        if (!$assertionsDisabled && i != linger_timer_id)
        {
            throw new AssertionError();
        }
        has_linger_timer = false;
        if (!$assertionsDisabled && pipe == null)
        {
            throw new AssertionError();
        } else
        {
            pipe.terminate(false);
            return;
        }
    }

    public String toString()
    {
        return (new StringBuilder()).append(super.toString()).append("[").append(options.socket_id).append("]").toString();
    }

    public void write_activated(Pipe pipe1)
    {
        if (pipe != pipe1)
        {
            if (!$assertionsDisabled && !terminating_pipes.contains(pipe1))
            {
                throw new AssertionError();
            }
        } else
        if (engine != null)
        {
            engine.activate_in();
        }
    }

    static 
    {
        boolean flag;
        if (!zmq/SessionBase.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
