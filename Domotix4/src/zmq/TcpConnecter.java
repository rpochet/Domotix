// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.SocketChannel;

// Referenced classes of package zmq:
//            Own, IPollEvents, IOObject, Options, 
//            Address, SessionBase, SocketBase, ZError, 
//            Utils, StreamEngine, IOThread

public class TcpConnecter extends Own
    implements IPollEvents
{

    static final boolean $assertionsDisabled = false;
    private static final int reconnect_timer_id = 1;
    private final Address addr;
    private int current_reconnect_ivl;
    private boolean delayed_start;
    private String endpoint;
    private SocketChannel handle;
    private boolean handle_valid;
    private final IOObject io_object;
    private SessionBase session;
    private SocketBase socket;
    private boolean timer_started;

    public TcpConnecter(IOThread iothread, SessionBase sessionbase, Options options, Address address, boolean flag)
    {
        super(iothread, options);
        io_object = new IOObject(iothread);
        addr = address;
        handle = null;
        handle_valid = false;
        delayed_start = flag;
        timer_started = false;
        session = sessionbase;
        current_reconnect_ivl = this.options.reconnect_ivl;
        if (!$assertionsDisabled && addr == null)
        {
            throw new AssertionError();
        } else
        {
            endpoint = addr.toString();
            socket = sessionbase.get_soket();
            return;
        }
    }

    private void add_reconnect_timer()
    {
        int i = get_new_reconnect_ivl();
        io_object.add_timer(i, 1);
        socket.event_connect_retried(endpoint, i);
        timer_started = true;
    }

    private void close()
    {
        if (!$assertionsDisabled && handle == null)
        {
            throw new AssertionError();
        }
        try
        {
            handle.close();
            socket.event_closed(endpoint, handle);
        }
        catch (IOException ioexception)
        {
            socket.event_close_failed(endpoint, ZError.exccode(ioexception));
        }
        handle = null;
    }

    private SocketChannel connect()
        throws IOException
    {
        boolean flag = handle.finishConnect();
        if (!$assertionsDisabled && !flag)
        {
            throw new AssertionError();
        } else
        {
            return handle;
        }
    }

    private int get_new_reconnect_ivl()
    {
        int i = current_reconnect_ivl + Utils.generate_random() % options.reconnect_ivl;
        if (options.reconnect_ivl_max > 0 && options.reconnect_ivl_max > options.reconnect_ivl)
        {
            current_reconnect_ivl = 2 * current_reconnect_ivl;
            if (current_reconnect_ivl >= options.reconnect_ivl_max)
            {
                current_reconnect_ivl = options.reconnect_ivl_max;
            }
        }
        return i;
    }

    private boolean open()
        throws IOException
    {
        if (!$assertionsDisabled && handle != null)
        {
            throw new AssertionError();
        } else
        {
            handle = SocketChannel.open();
            Utils.unblock_socket(handle);
            return handle.connect(addr.resolved().address());
        }
    }

    private void start_connecting()
    {
        if (open())
        {
            io_object.add_fd(handle);
            handle_valid = true;
            io_object.connect_event();
            return;
        }
        try
        {
            io_object.add_fd(handle);
            handle_valid = true;
            io_object.set_pollconnect(handle);
            socket.event_connect_delayed(endpoint, -1);
            return;
        }
        catch (IOException ioexception) { }
        if (handle != null)
        {
            close();
        }
        add_reconnect_timer();
        return;
    }

    public void accept_event()
    {
        throw new UnsupportedOperationException();
    }

    public void connect_event()
    {
        boolean flag = false;
        SocketChannel socketchannel1 = connect();
        SocketChannel socketchannel = socketchannel1;
_L2:
        io_object.rm_fd(handle);
        handle_valid = false;
        if (flag)
        {
            close();
            add_reconnect_timer();
            return;
        }
        break; /* Loop/switch isn't completed */
        ConnectException connectexception;
        connectexception;
        flag = true;
        socketchannel = null;
        continue; /* Loop/switch isn't completed */
        SocketException socketexception1;
        socketexception1;
        flag = true;
        socketchannel = null;
        continue; /* Loop/switch isn't completed */
        SocketTimeoutException sockettimeoutexception;
        sockettimeoutexception;
        flag = true;
        socketchannel = null;
        if (true) goto _L2; else goto _L1
        IOException ioexception;
        ioexception;
        throw new ZError.IOException(ioexception);
_L1:
        handle = null;
        StreamEngine streamengine;
        try
        {
            Utils.tune_tcp_socket(socketchannel);
            Utils.tune_tcp_keepalives(socketchannel, options.tcp_keepalive, options.tcp_keepalive_cnt, options.tcp_keepalive_idle, options.tcp_keepalive_intvl);
        }
        catch (SocketException socketexception)
        {
            throw new RuntimeException(socketexception);
        }
        try
        {
            streamengine = new StreamEngine(socketchannel, options, endpoint);
        }
        catch (ZError.InstantiationException instantiationexception)
        {
            socket.event_connect_delayed(endpoint, -1);
            return;
        }
        send_attach(session, streamengine);
        terminate();
        socket.event_connected(endpoint, socketchannel);
        return;
    }

    public void destroy()
    {
        if (!$assertionsDisabled && timer_started)
        {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && handle_valid)
        {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && handle != null)
        {
            throw new AssertionError();
        } else
        {
            return;
        }
    }

    public void in_event()
    {
    }

    public void out_event()
    {
    }

    protected void process_plug()
    {
        io_object.set_handler(this);
        if (delayed_start)
        {
            add_reconnect_timer();
            return;
        } else
        {
            start_connecting();
            return;
        }
    }

    public void process_term(int i)
    {
        if (timer_started)
        {
            io_object.cancel_timer(1);
            timer_started = false;
        }
        if (handle_valid)
        {
            io_object.rm_fd(handle);
            handle_valid = false;
        }
        if (handle != null)
        {
            close();
        }
        super.process_term(i);
    }

    public void timer_event(int i)
    {
        timer_started = false;
        start_connecting();
    }

    static 
    {
        boolean flag;
        if (!zmq/TcpConnecter.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
