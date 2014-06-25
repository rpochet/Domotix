// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Referenced classes of package zmq:
//            Own, IPollEvents, Options, MultiMap, 
//            Mailbox, ValueReference, Pipe, Poller, 
//            Pair, Pub, Sub, Req, 
//            Rep, Dealer, Router, Pull, 
//            Push, XPub, XSub, Msg, 
//            Config, Command, ZObject, TcpListener, 
//            IpcListener, Address, TcpAddress, SessionBase, 
//            IpcAddress, Ctx, Clock

public abstract class SocketBase extends Own
    implements IPollEvents, Pipe.IPipeEvents
{

    static final boolean $assertionsDisabled;
    private boolean ctx_terminated;
    private boolean destroyed;
    private final Map endpoints = new MultiMap();
    protected ValueReference errno;
    private SelectableChannel handle;
    private final Map inprocs = new MultiMap();
    private long last_tsc;
    private final Mailbox mailbox;
    private int monitor_events;
    private SocketBase monitor_socket;
    private final List pipes = new ArrayList();
    private Poller poller;
    private boolean rcvmore;
    private int tag;
    private int ticks;

    protected SocketBase(Ctx ctx, int i, int j)
    {
        super(ctx, i);
        tag = 0xbaddecaf;
        ctx_terminated = false;
        destroyed = false;
        last_tsc = 0L;
        ticks = 0;
        rcvmore = false;
        monitor_socket = null;
        monitor_events = 0;
        options.socket_id = j;
        mailbox = new Mailbox((new StringBuilder()).append("socket-").append(j).toString());
        errno = new ValueReference(Integer.valueOf(0));
    }

    private void add_endpoint(String s, Own own)
    {
        launch_child(own);
        endpoints.put(s, own);
    }

    private void attach_pipe(Pipe pipe)
    {
        attach_pipe(pipe, false);
    }

    private void attach_pipe(Pipe pipe, boolean flag)
    {
        pipe.set_event_sink(this);
        pipes.add(pipe);
        xattach_pipe(pipe, flag);
        if (is_terminating())
        {
            register_term_acks(1);
            pipe.terminate(false);
        }
    }

    private void check_destroy()
    {
        if (destroyed)
        {
            poller.rm_fd(handle);
            destroy_socket(this);
            send_reaped();
            super.process_destroy();
        }
    }

    private void check_protocol(String s)
    {
        if (!s.equals("inproc") && !s.equals("ipc") && !s.equals("tcp"))
        {
            throw new UnsupportedOperationException(s);
        }
        if ((s.equals("pgm") || s.equals("epgm")) && options.type != 1 && options.type != 2 && options.type != 9 && options.type != 10)
        {
            throw new UnsupportedOperationException((new StringBuilder()).append(s).append(",type=").append(options.type).toString());
        } else
        {
            return;
        }
    }

    public static SocketBase create(int i, Ctx ctx, int j, int k)
    {
        switch (i)
        {
        default:
            throw new IllegalArgumentException((new StringBuilder()).append("type=").append(i).toString());

        case 0: // '\0'
            return new Pair(ctx, j, k);

        case 1: // '\001'
            return new Pub(ctx, j, k);

        case 2: // '\002'
            return new Sub(ctx, j, k);

        case 3: // '\003'
            return new Req(ctx, j, k);

        case 4: // '\004'
            return new Rep(ctx, j, k);

        case 5: // '\005'
            return new Dealer(ctx, j, k);

        case 6: // '\006'
            return new Router(ctx, j, k);

        case 7: // '\007'
            return new Pull(ctx, j, k);

        case 8: // '\b'
            return new Push(ctx, j, k);

        case 9: // '\t'
            return new XPub(ctx, j, k);

        case 10: // '\n'
            return new XSub(ctx, j, k);
        }
    }

    private void extract_flags(Msg msg)
    {
        if ((0x40 & msg.flags()) > 0 && !$assertionsDisabled && !options.recv_identity)
        {
            throw new AssertionError();
        } else
        {
            rcvmore = msg.has_more();
            return;
        }
    }

    private boolean process_commands(int i, boolean flag)
    {
        boolean flag1 = true;
        Command command;
        if (i != 0)
        {
            command = mailbox.recv(i);
        } else
        {
            if (0L != 0L && flag)
            {
                if (0L >= last_tsc && 0L - last_tsc <= (long)Config.max_command_delay.getValue())
                {
                    return true;
                }
                last_tsc = 0L;
            }
            command = mailbox.recv(0L);
        }
        do
        {
            if (command == null)
            {
                if (ctx_terminated)
                {
                    errno.set(Integer.valueOf(0x9523dfd));
                    flag1 = false;
                }
                return flag1;
            }
            command.destination().process_command(command);
            command = mailbox.recv(0L);
        } while (true);
    }

    public void accept_event()
    {
        throw new UnsupportedOperationException();
    }

    public boolean bind(String s)
    {
        if (ctx_terminated)
        {
            throw new ZError.CtxTerminatedException();
        }
        boolean flag;
        if (!process_commands(0, false))
        {
            flag = false;
        } else
        {
            URI uri;
            String s1;
            String s2;
            String s3;
            try
            {
                uri = new URI(s);
            }
            catch (URISyntaxException urisyntaxexception)
            {
                throw new IllegalArgumentException(urisyntaxexception);
            }
            s1 = uri.getScheme();
            s2 = uri.getAuthority();
            s3 = uri.getPath();
            if (s2 == null)
            {
                s2 = s3;
            }
            check_protocol(s1);
            if (s1.equals("inproc"))
            {
                flag = register_endpoint(s, new Ctx.Endpoint(this, options));
                if (flag)
                {
                    options.last_endpoint = s;
                    return flag;
                }
            } else
            {
                if (s1.equals("pgm") || s1.equals("epgm"))
                {
                    return connect(s);
                }
                IOThread iothread = choose_io_thread(options.affinity);
                if (iothread == null)
                {
                    throw new IllegalStateException("EMTHREAD");
                }
                if (s1.equals("tcp"))
                {
                    TcpListener tcplistener = new TcpListener(iothread, this, options);
                    int i = tcplistener.set_address(s2);
                    if (i != 0)
                    {
                        tcplistener.destroy();
                        event_bind_failed(s2, i);
                        return false;
                    } else
                    {
                        options.last_endpoint = tcplistener.get_address();
                        add_endpoint(s, tcplistener);
                        return true;
                    }
                }
                if (s1.equals("ipc"))
                {
                    IpcListener ipclistener = new IpcListener(iothread, this, options);
                    int j = ipclistener.set_address(s2);
                    if (j != 0)
                    {
                        ipclistener.destroy();
                        event_bind_failed(s2, j);
                        return false;
                    } else
                    {
                        options.last_endpoint = ipclistener.get_address();
                        add_endpoint(s, ipclistener);
                        return true;
                    }
                }
                if (!$assertionsDisabled)
                {
                    throw new AssertionError();
                } else
                {
                    return false;
                }
            }
        }
        return flag;
    }

    public boolean check_tag()
    {
        return tag == 0xbaddecaf;
    }

    public void close()
    {
        tag = 0xdeadbeef;
        send_reap(this);
    }

    public boolean connect(String s)
    {
        String s1;
        String s2;
        IOThread iothread;
        Address address;
        if (ctx_terminated)
        {
            throw new ZError.CtxTerminatedException();
        }
        if (!process_commands(0, false))
        {
            return false;
        }
        URI uri;
        String s3;
        try
        {
            uri = new URI(s);
        }
        catch (URISyntaxException urisyntaxexception)
        {
            IllegalArgumentException illegalargumentexception = new IllegalArgumentException(urisyntaxexception);
            throw illegalargumentexception;
        }
        s1 = uri.getScheme();
        s2 = uri.getAuthority();
        s3 = uri.getPath();
        if (s2 == null)
        {
            s2 = s3;
        }
        check_protocol(s1);
        if (s1.equals("inproc"))
        {
            Ctx.Endpoint endpoint = find_endpoint(s);
            if (endpoint.socket == null)
            {
                return false;
            }
            int i = options.sndhwm;
            int j = 0;
            if (i != 0)
            {
                int j1 = endpoint.options.rcvhwm;
                j = 0;
                if (j1 != 0)
                {
                    j = options.sndhwm + endpoint.options.rcvhwm;
                }
            }
            int k = options.rcvhwm;
            int l = 0;
            if (k != 0)
            {
                int i1 = endpoint.options.sndhwm;
                l = 0;
                if (i1 != 0)
                {
                    l = options.rcvhwm + endpoint.options.sndhwm;
                }
            }
            ZObject azobject1[] = new ZObject[2];
            azobject1[0] = this;
            azobject1[1] = endpoint.socket;
            Pipe apipe1[] = {
                null, null
            };
            int ai1[] = {
                j, l
            };
            boolean aflag1[] = new boolean[2];
            aflag1[0] = options.delay_on_disconnect;
            aflag1[1] = options.delay_on_close;
            Pipe.pipepair(azobject1, apipe1, ai1, aflag1);
            attach_pipe(apipe1[0]);
            if (endpoint.options.recv_identity)
            {
                Msg msg = new Msg(options.identity_size);
                msg.put(options.identity, 0, options.identity_size);
                msg.set_flags(64);
                boolean flag3 = apipe1[0].write(msg);
                if (!$assertionsDisabled && !flag3)
                {
                    throw new AssertionError();
                }
                apipe1[0].flush();
            }
            if (options.recv_identity)
            {
                Msg msg1 = new Msg(endpoint.options.identity_size);
                msg1.put(endpoint.options.identity, 0, endpoint.options.identity_size);
                msg1.set_flags(64);
                boolean flag4 = apipe1[1].write(msg1);
                if (!$assertionsDisabled && !flag4)
                {
                    throw new AssertionError();
                }
                apipe1[1].flush();
            }
            send_bind(endpoint.socket, apipe1[1], false);
            options.last_endpoint = s;
            inprocs.put(s, apipe1[0]);
            return true;
        }
        iothread = choose_io_thread(options.affinity);
        if (iothread == null)
        {
            throw new IllegalStateException("Empty IO Thread");
        }
        address = new Address(s1, s2);
        if (!s1.equals("tcp")) goto _L2; else goto _L1
_L1:
        SessionBase sessionbase;
        address.resolved(new TcpAddress());
        Address.IZAddress izaddress = address.resolved();
        boolean flag2;
        if (options.ipv4only != 0)
        {
            flag2 = true;
        } else
        {
            flag2 = false;
        }
        izaddress.resolve(s2, flag2);
_L4:
        sessionbase = SessionBase.create(iothread, true, this, options, address);
        if (!$assertionsDisabled && sessionbase == null)
        {
            throw new AssertionError();
        }
        break; /* Loop/switch isn't completed */
_L2:
        if (s1.equals("ipc"))
        {
            address.resolved(new IpcAddress());
            address.resolved().resolve(s2, true);
        }
        if (true) goto _L4; else goto _L3
_L3:
        boolean flag;
label0:
        {
            if (!s1.equals("pgm"))
            {
                boolean flag1 = s1.equals("epgm");
                flag = false;
                if (!flag1)
                {
                    break label0;
                }
            }
            flag = true;
        }
        if (options.delay_attach_on_connect != 1 || flag)
        {
            ZObject azobject[] = {
                this, sessionbase
            };
            Pipe apipe[] = {
                null, null
            };
            int ai[] = new int[2];
            ai[0] = options.sndhwm;
            ai[1] = options.rcvhwm;
            boolean aflag[] = new boolean[2];
            aflag[0] = options.delay_on_disconnect;
            aflag[1] = options.delay_on_close;
            Pipe.pipepair(azobject, apipe, ai, aflag);
            attach_pipe(apipe[0], flag);
            sessionbase.attach_pipe(apipe[1]);
        }
        options.last_endpoint = address.toString();
        add_endpoint(s, sessionbase);
        return true;
    }

    public void connect_event()
    {
        throw new UnsupportedOperationException();
    }

    public void destroy()
    {
        stop_monitor();
        if (!$assertionsDisabled && !destroyed)
        {
            throw new AssertionError();
        } else
        {
            return;
        }
    }

    public int errno()
    {
        return ((Integer)errno.get()).intValue();
    }

    public void event_accept_failed(String s, int i)
    {
        if ((0x40 & monitor_events) == 0)
        {
            return;
        } else
        {
            monitor_event(new ZMQ.Event(64, s, Integer.valueOf(i)));
            return;
        }
    }

    public void event_accepted(String s, SelectableChannel selectablechannel)
    {
        if ((0x20 & monitor_events) == 0)
        {
            return;
        } else
        {
            monitor_event(new ZMQ.Event(32, s, selectablechannel));
            return;
        }
    }

    public void event_bind_failed(String s, int i)
    {
        if ((0x10 & monitor_events) == 0)
        {
            return;
        } else
        {
            monitor_event(new ZMQ.Event(16, s, Integer.valueOf(i)));
            return;
        }
    }

    public void event_close_failed(String s, int i)
    {
        if ((0x100 & monitor_events) == 0)
        {
            return;
        } else
        {
            monitor_event(new ZMQ.Event(256, s, Integer.valueOf(i)));
            return;
        }
    }

    public void event_closed(String s, SelectableChannel selectablechannel)
    {
        if ((0x80 & monitor_events) == 0)
        {
            return;
        } else
        {
            monitor_event(new ZMQ.Event(128, s, selectablechannel));
            return;
        }
    }

    public void event_connect_delayed(String s, int i)
    {
        if ((2 & monitor_events) == 0)
        {
            return;
        } else
        {
            monitor_event(new ZMQ.Event(2, s, Integer.valueOf(i)));
            return;
        }
    }

    public void event_connect_retried(String s, int i)
    {
        if ((4 & monitor_events) == 0)
        {
            return;
        } else
        {
            monitor_event(new ZMQ.Event(4, s, Integer.valueOf(i)));
            return;
        }
    }

    public void event_connected(String s, SelectableChannel selectablechannel)
    {
        if ((1 & monitor_events) == 0)
        {
            return;
        } else
        {
            monitor_event(new ZMQ.Event(1, s, selectablechannel));
            return;
        }
    }

    public void event_disconnected(String s, SelectableChannel selectablechannel)
    {
        if ((0x200 & monitor_events) == 0)
        {
            return;
        } else
        {
            monitor_event(new ZMQ.Event(512, s, selectablechannel));
            return;
        }
    }

    public void event_listening(String s, SelectableChannel selectablechannel)
    {
        if ((8 & monitor_events) == 0)
        {
            return;
        } else
        {
            monitor_event(new ZMQ.Event(8, s, selectablechannel));
            return;
        }
    }

    public SelectableChannel get_fd()
    {
        return mailbox.get_fd();
    }

    public Mailbox get_mailbox()
    {
        return mailbox;
    }

    public int getsockopt(int i)
    {
        if (i != 15 && ctx_terminated)
        {
            throw new ZError.CtxTerminatedException();
        }
        if (i == 13)
        {
            boolean flag2 = rcvmore;
            int k = 0;
            if (flag2)
            {
                k = 1;
            }
            return k;
        }
        if (i == 15)
        {
            boolean flag = process_commands(0, false);
            if (!flag && ((Integer)errno.get()).intValue() == 0x9523dfd)
            {
                return -1;
            }
            if (!$assertionsDisabled && !flag)
            {
                throw new AssertionError();
            }
            boolean flag1 = has_out();
            int j = 0;
            if (flag1)
            {
                j = 0 | 2;
            }
            if (has_in())
            {
                j |= 1;
            }
            return j;
        } else
        {
            return ((Integer)getsockoptx(i)).intValue();
        }
    }

    public Object getsockoptx(int i)
    {
        if (ctx_terminated)
        {
            throw new ZError.CtxTerminatedException();
        }
        if (i == 13)
        {
            boolean flag2 = rcvmore;
            int k = 0;
            if (flag2)
            {
                k = 1;
            }
            return Integer.valueOf(k);
        }
        if (i == 14)
        {
            return mailbox.get_fd();
        }
        if (i == 15)
        {
            boolean flag = process_commands(0, false);
            if (!flag && ((Integer)errno.get()).intValue() == 0x9523dfd)
            {
                return Integer.valueOf(-1);
            }
            if (!$assertionsDisabled && !flag)
            {
                throw new AssertionError();
            }
            boolean flag1 = has_out();
            int j = 0;
            if (flag1)
            {
                j = 0 | 2;
            }
            if (has_in())
            {
                j |= 1;
            }
            return Integer.valueOf(j);
        } else
        {
            return options.getsockopt(i);
        }
    }

    public boolean has_in()
    {
        return xhas_in();
    }

    public boolean has_out()
    {
        return xhas_out();
    }

    public void hiccuped(Pipe pipe)
    {
        if (options.delay_attach_on_connect == 1)
        {
            pipe.terminate(false);
            return;
        } else
        {
            xhiccuped(pipe);
            return;
        }
    }

    public void in_event()
    {
        try
        {
            process_commands(0, false);
        }
        catch (ZError.CtxTerminatedException ctxterminatedexception) { }
        check_destroy();
    }

    public boolean monitor(String s, int i)
    {
        if (ctx_terminated)
        {
            throw new ZError.CtxTerminatedException();
        }
        boolean flag;
        if (s == null)
        {
            stop_monitor();
            flag = true;
        } else
        {
            URI uri;
            String s1;
            String s2;
            try
            {
                uri = new URI(s);
            }
            catch (URISyntaxException urisyntaxexception)
            {
                throw new IllegalArgumentException(urisyntaxexception);
            }
            s1 = uri.getScheme();
            s2 = uri.getAuthority();
            uri.getPath();
            if (s2 != null);
            check_protocol(s1);
            if (!s1.equals("inproc"))
            {
                stop_monitor();
                throw new IllegalArgumentException("inproc socket required");
            }
            monitor_events = i;
            monitor_socket = get_ctx().create_socket(0);
            SocketBase socketbase = monitor_socket;
            flag = false;
            if (socketbase != null)
            {
                try
                {
                    monitor_socket.setsockopt(17, Integer.valueOf(0));
                }
                catch (IllegalArgumentException illegalargumentexception)
                {
                    stop_monitor();
                    throw illegalargumentexception;
                }
                flag = monitor_socket.bind(s);
                if (!flag)
                {
                    stop_monitor();
                    return flag;
                }
            }
        }
        return flag;
    }

    protected void monitor_event(ZMQ.Event event)
    {
        if (monitor_socket == null)
        {
            return;
        } else
        {
            event.write(monitor_socket);
            return;
        }
    }

    public void out_event()
    {
        throw new UnsupportedOperationException();
    }

    protected void process_bind(Pipe pipe)
    {
        attach_pipe(pipe);
    }

    protected void process_destroy()
    {
        destroyed = true;
    }

    protected void process_stop()
    {
        stop_monitor();
        ctx_terminated = true;
    }

    protected void process_term(int i)
    {
        unregister_endpoints(this);
        for (int j = 0; j != pipes.size(); j++)
        {
            ((Pipe)pipes.get(j)).terminate(false);
        }

        register_term_acks(pipes.size());
        super.process_term(i);
    }

    public void read_activated(Pipe pipe)
    {
        xread_activated(pipe);
    }

    public Msg recv(int i)
    {
        if (ctx_terminated)
        {
            errno.set(Integer.valueOf(0x9523dfd));
            return null;
        }
        int j = 1 + ticks;
        ticks = j;
        if (j == Config.inbound_poll_rate.getValue())
        {
            if (!process_commands(0, false))
            {
                return null;
            }
            ticks = 0;
        }
        Msg msg = xrecv();
        if (msg == null && ((Integer)errno.get()).intValue() != 35)
        {
            return null;
        }
        if (msg != null)
        {
            extract_flags(msg);
            return msg;
        }
        if ((i & 1) > 0 || options.rcvtimeo == 0)
        {
            if (!process_commands(0, false))
            {
                return null;
            }
            ticks = 0;
            Msg msg1 = xrecv();
            if (msg1 == null)
            {
                return null;
            } else
            {
                extract_flags(msg1);
                return msg1;
            }
        }
        int k = options.rcvtimeo;
        long l;
        boolean flag;
        if (k < 0)
        {
            l = 0L;
        } else
        {
            l = Clock.now_ms() + (long)k;
        }
        if (ticks != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        do
        {
            do
            {
                int i1;
                if (flag)
                {
                    i1 = k;
                } else
                {
                    i1 = 0;
                }
                if (!process_commands(i1, false))
                {
                    return null;
                }
                Msg msg2 = xrecv();
                if (msg2 != null)
                {
                    ticks = 0;
                    extract_flags(msg2);
                    return msg2;
                }
                if (((Integer)errno.get()).intValue() != 35)
                {
                    return null;
                }
                flag = true;
            } while (k <= 0);
            k = (int)(l - Clock.now_ms());
        } while (k > 0);
        errno.set(Integer.valueOf(35));
        return null;
    }

    public boolean send(Msg msg, int i)
    {
        if (ctx_terminated)
        {
            errno.set(Integer.valueOf(0x9523dfd));
            return false;
        }
        if (msg == null)
        {
            throw new IllegalArgumentException();
        }
        if (!process_commands(0, true))
        {
            return false;
        }
        msg.reset_flags(1);
        if ((i & 2) > 0)
        {
            msg.set_flags(1);
        }
        if (xsend(msg))
        {
            return true;
        }
        if (((Integer)errno.get()).intValue() != 35)
        {
            return false;
        }
        if ((i & 1) > 0 || options.sndtimeo == 0)
        {
            return false;
        }
        int j = options.sndtimeo;
        long l;
        if (j < 0)
        {
            l = 0L;
        } else
        {
            l = Clock.now_ms() + (long)j;
        }
        do
        {
            do
            {
                if (!process_commands(j, false))
                {
                    return false;
                }
                if (xsend(msg))
                {
                    return true;
                }
                if (((Integer)errno.get()).intValue() != 35)
                {
                    return false;
                }
            } while (j <= 0);
            j = (int)(l - Clock.now_ms());
        } while (j > 0);
        errno.set(Integer.valueOf(35));
        return false;
    }

    public void setsockopt(int i, Object obj)
    {
        if (ctx_terminated)
        {
            throw new ZError.CtxTerminatedException();
        }
        if (xsetsockopt(i, obj))
        {
            return;
        } else
        {
            options.setsockopt(i, obj);
            return;
        }
    }

    public void start_reaping(Poller poller1)
    {
        poller = poller1;
        handle = mailbox.get_fd();
        poller.add_fd(handle, this);
        poller.set_pollin(handle);
        terminate();
        check_destroy();
    }

    public void stop()
    {
        send_stop();
    }

    protected void stop_monitor()
    {
        if (monitor_socket != null)
        {
            monitor_socket.close();
            monitor_socket = null;
            monitor_events = 0;
        }
    }

    public boolean term_endpoint(String s)
    {
        if (ctx_terminated)
        {
            throw new ZError.CtxTerminatedException();
        }
        if (s == null)
        {
            throw new IllegalArgumentException();
        }
        boolean flag = process_commands(0, false);
        if (!flag)
        {
            return flag;
        }
        URI uri;
        try
        {
            uri = new URI(s);
        }
        catch (URISyntaxException urisyntaxexception)
        {
            throw new IllegalArgumentException(urisyntaxexception);
        }
        if (uri.getScheme().equals("inproc"))
        {
            if (!inprocs.containsKey(s))
            {
                return false;
            }
            for (Iterator iterator1 = inprocs.entrySet().iterator(); iterator1.hasNext(); iterator1.remove())
            {
                ((Pipe)((java.util.Map.Entry)iterator1.next()).getValue()).terminate(true);
            }

            return true;
        }
        if (!endpoints.containsKey(s))
        {
            return false;
        }
        Iterator iterator = endpoints.entrySet().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            if (((String)entry.getKey()).equals(s))
            {
                term_child((Own)entry.getValue());
                iterator.remove();
            }
        } while (true);
        return true;
    }

    public void terminated(Pipe pipe)
    {
        xterminated(pipe);
        Iterator iterator = inprocs.entrySet().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            if (((java.util.Map.Entry)iterator.next()).getValue() != pipe)
            {
                continue;
            }
            iterator.remove();
            break;
        } while (true);
        pipes.remove(pipe);
        if (is_terminating())
        {
            unregister_term_ack();
        }
    }

    public void timer_event(int i)
    {
        throw new UnsupportedOperationException();
    }

    public String toString()
    {
        return (new StringBuilder()).append(super.toString()).append("[").append(options.socket_id).append("]").toString();
    }

    public String typeString()
    {
        switch (options.type)
        {
        default:
            return "UNKOWN";

        case 0: // '\0'
            return "PAIR";

        case 1: // '\001'
            return "PUB";

        case 2: // '\002'
            return "SUB";

        case 3: // '\003'
            return "REQ";

        case 4: // '\004'
            return "REP";

        case 5: // '\005'
            return "DEALER";

        case 6: // '\006'
            return "ROUTER";

        case 7: // '\007'
            return "PULL";

        case 8: // '\b'
            return "PUSH";
        }
    }

    public void write_activated(Pipe pipe)
    {
        xwrite_activated(pipe);
    }

    protected abstract void xattach_pipe(Pipe pipe, boolean flag);

    protected boolean xhas_in()
    {
        return false;
    }

    protected boolean xhas_out()
    {
        return false;
    }

    protected void xhiccuped(Pipe pipe)
    {
        throw new UnsupportedOperationException("Must override");
    }

    protected void xread_activated(Pipe pipe)
    {
        throw new UnsupportedOperationException("Must Override");
    }

    protected Msg xrecv()
    {
        throw new UnsupportedOperationException("Must Override");
    }

    protected boolean xsend(Msg msg)
    {
        throw new UnsupportedOperationException("Must Override");
    }

    protected boolean xsetsockopt(int i, Object obj)
    {
        return false;
    }

    protected abstract void xterminated(Pipe pipe);

    protected void xwrite_activated(Pipe pipe)
    {
        throw new UnsupportedOperationException("Must Override");
    }

    static 
    {
        boolean flag;
        if (!zmq/SocketBase.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
