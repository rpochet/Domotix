// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            Command, Ctx, Own, IEngine, 
//            Pipe, SocketBase, SessionBase, IOThread

public abstract class ZObject
{

    private final Ctx ctx;
    private final int tid;

    protected ZObject(Ctx ctx1, int i)
    {
        ctx = ctx1;
        tid = i;
    }

    protected ZObject(ZObject zobject)
    {
        this(zobject.ctx, zobject.tid);
    }

    private void send_command(Command command)
    {
        ctx.send_command(command.destination().get_tid(), command);
    }

    protected IOThread choose_io_thread(long l)
    {
        return ctx.choose_io_thread(l);
    }

    protected void destroy_socket(SocketBase socketbase)
    {
        ctx.destroy_socket(socketbase);
    }

    protected Ctx.Endpoint find_endpoint(String s)
    {
        return ctx.find_endpoint(s);
    }

    protected Ctx get_ctx()
    {
        return ctx;
    }

    protected int get_tid()
    {
        return tid;
    }

    protected void process_activate_read()
    {
        throw new UnsupportedOperationException();
    }

    protected void process_activate_write(long l)
    {
        throw new UnsupportedOperationException();
    }

    protected void process_attach(IEngine iengine)
    {
        throw new UnsupportedOperationException();
    }

    protected void process_bind(Pipe pipe)
    {
        throw new UnsupportedOperationException();
    }

    protected void process_command(Command command)
    {
        static class _cls1
        {

            static final int $SwitchMap$zmq$Command$Type[];

            static 
            {
                $SwitchMap$zmq$Command$Type = new int[Command.Type.values().length];
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.activate_read.ordinal()] = 1;
                }
                catch (NoSuchFieldError nosuchfielderror) { }
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.activate_write.ordinal()] = 2;
                }
                catch (NoSuchFieldError nosuchfielderror1) { }
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.stop.ordinal()] = 3;
                }
                catch (NoSuchFieldError nosuchfielderror2) { }
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.plug.ordinal()] = 4;
                }
                catch (NoSuchFieldError nosuchfielderror3) { }
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.own.ordinal()] = 5;
                }
                catch (NoSuchFieldError nosuchfielderror4) { }
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.attach.ordinal()] = 6;
                }
                catch (NoSuchFieldError nosuchfielderror5) { }
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.bind.ordinal()] = 7;
                }
                catch (NoSuchFieldError nosuchfielderror6) { }
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.hiccup.ordinal()] = 8;
                }
                catch (NoSuchFieldError nosuchfielderror7) { }
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.pipe_term.ordinal()] = 9;
                }
                catch (NoSuchFieldError nosuchfielderror8) { }
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.pipe_term_ack.ordinal()] = 10;
                }
                catch (NoSuchFieldError nosuchfielderror9) { }
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.term_req.ordinal()] = 11;
                }
                catch (NoSuchFieldError nosuchfielderror10) { }
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.term.ordinal()] = 12;
                }
                catch (NoSuchFieldError nosuchfielderror11) { }
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.term_ack.ordinal()] = 13;
                }
                catch (NoSuchFieldError nosuchfielderror12) { }
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.reap.ordinal()] = 14;
                }
                catch (NoSuchFieldError nosuchfielderror13) { }
                try
                {
                    $SwitchMap$zmq$Command$Type[Command.Type.reaped.ordinal()] = 15;
                }
                catch (NoSuchFieldError nosuchfielderror14)
                {
                    return;
                }
            }
        }

        switch (_cls1..SwitchMap.zmq.Command.Type[command.type().ordinal()])
        {
        default:
            throw new IllegalArgumentException();

        case 1: // '\001'
            process_activate_read();
            return;

        case 2: // '\002'
            process_activate_write(((Long)command.arg).longValue());
            return;

        case 3: // '\003'
            process_stop();
            return;

        case 4: // '\004'
            process_plug();
            process_seqnum();
            return;

        case 5: // '\005'
            process_own((Own)command.arg);
            process_seqnum();
            return;

        case 6: // '\006'
            process_attach((IEngine)command.arg);
            process_seqnum();
            return;

        case 7: // '\007'
            process_bind((Pipe)command.arg);
            process_seqnum();
            return;

        case 8: // '\b'
            process_hiccup(command.arg);
            return;

        case 9: // '\t'
            process_pipe_term();
            return;

        case 10: // '\n'
            process_pipe_term_ack();
            return;

        case 11: // '\013'
            process_term_req((Own)command.arg);
            return;

        case 12: // '\f'
            process_term(((Integer)command.arg).intValue());
            return;

        case 13: // '\r'
            process_term_ack();
            return;

        case 14: // '\016'
            process_reap((SocketBase)command.arg);
            return;

        case 15: // '\017'
            process_reaped();
            break;
        }
    }

    protected void process_hiccup(Object obj)
    {
        throw new UnsupportedOperationException();
    }

    protected void process_own(Own own)
    {
        throw new UnsupportedOperationException();
    }

    protected void process_pipe_term()
    {
        throw new UnsupportedOperationException();
    }

    protected void process_pipe_term_ack()
    {
        throw new UnsupportedOperationException();
    }

    protected void process_plug()
    {
        throw new UnsupportedOperationException();
    }

    protected void process_reap(SocketBase socketbase)
    {
        throw new UnsupportedOperationException();
    }

    protected void process_reaped()
    {
        throw new UnsupportedOperationException();
    }

    protected void process_seqnum()
    {
        throw new UnsupportedOperationException();
    }

    protected void process_stop()
    {
        throw new UnsupportedOperationException();
    }

    protected void process_term(int i)
    {
        throw new UnsupportedOperationException();
    }

    protected void process_term_ack()
    {
        throw new UnsupportedOperationException();
    }

    protected void process_term_req(Own own)
    {
        throw new UnsupportedOperationException();
    }

    protected boolean register_endpoint(String s, Ctx.Endpoint endpoint)
    {
        return ctx.register_endpoint(s, endpoint);
    }

    protected void send_activate_read(Pipe pipe)
    {
        send_command(new Command(pipe, Command.Type.activate_read));
    }

    protected void send_activate_write(Pipe pipe, long l)
    {
        send_command(new Command(pipe, Command.Type.activate_write, Long.valueOf(l)));
    }

    protected void send_attach(SessionBase sessionbase, IEngine iengine)
    {
        send_attach(sessionbase, iengine, true);
    }

    protected void send_attach(SessionBase sessionbase, IEngine iengine, boolean flag)
    {
        if (flag)
        {
            sessionbase.inc_seqnum();
        }
        send_command(new Command(sessionbase, Command.Type.attach, iengine));
    }

    protected void send_bind(Own own, Pipe pipe)
    {
        send_bind(own, pipe, true);
    }

    protected void send_bind(Own own, Pipe pipe, boolean flag)
    {
        if (flag)
        {
            own.inc_seqnum();
        }
        send_command(new Command(own, Command.Type.bind, pipe));
    }

    protected void send_done()
    {
        Command command = new Command(null, Command.Type.done);
        ctx.send_command(0, command);
    }

    protected void send_hiccup(Pipe pipe, Object obj)
    {
        send_command(new Command(pipe, Command.Type.hiccup, obj));
    }

    protected void send_own(Own own, Own own1)
    {
        own.inc_seqnum();
        send_command(new Command(own, Command.Type.own, own1));
    }

    protected void send_pipe_term(Pipe pipe)
    {
        send_command(new Command(pipe, Command.Type.pipe_term));
    }

    protected void send_pipe_term_ack(Pipe pipe)
    {
        send_command(new Command(pipe, Command.Type.pipe_term_ack));
    }

    protected void send_plug(Own own)
    {
        send_plug(own, true);
    }

    protected void send_plug(Own own, boolean flag)
    {
        if (flag)
        {
            own.inc_seqnum();
        }
        send_command(new Command(own, Command.Type.plug));
    }

    protected void send_reap(SocketBase socketbase)
    {
        send_command(new Command(ctx.get_reaper(), Command.Type.reap, socketbase));
    }

    protected void send_reaped()
    {
        send_command(new Command(ctx.get_reaper(), Command.Type.reaped));
    }

    protected void send_stop()
    {
        Command command = new Command(this, Command.Type.stop);
        ctx.send_command(tid, command);
    }

    protected void send_term(Own own, int i)
    {
        send_command(new Command(own, Command.Type.term, Integer.valueOf(i)));
    }

    protected void send_term_ack(Own own)
    {
        send_command(new Command(own, Command.Type.term_ack));
    }

    protected void send_term_req(Own own, Own own1)
    {
        send_command(new Command(own, Command.Type.term_req, own1));
    }

    protected void unregister_endpoints(SocketBase socketbase)
    {
        ctx.unregister_endpoints(socketbase);
    }
}
