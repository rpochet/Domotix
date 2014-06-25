// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package zmq:
//            Own, IPollEvents, IOObject, TcpAddress, 
//            Options, SocketBase, ZError, Utils, 
//            StreamEngine, Address, SessionBase, IOThread

public class TcpListener extends Own
    implements IPollEvents
{

    static final boolean $assertionsDisabled;
    private final TcpAddress address = new TcpAddress();
    private String endpoint;
    private ServerSocketChannel handle;
    private final IOObject io_object;
    private SocketBase socket;

    public TcpListener(IOThread iothread, SocketBase socketbase, Options options)
    {
        super(iothread, options);
        io_object = new IOObject(iothread);
        handle = null;
        socket = socketbase;
    }

    private SocketChannel accept()
    {
        Socket socket1;
        try
        {
            socket1 = handle.socket().accept();
        }
        catch (IOException ioexception)
        {
            return null;
        }
        if (!options.tcp_accept_filters.isEmpty())
        {
            Iterator iterator = options.tcp_accept_filters.iterator();
            boolean flag1;
            do
            {
                boolean flag = iterator.hasNext();
                flag1 = false;
                if (!flag)
                {
                    break;
                }
                if (!((TcpAddress.TcpAddressMask)iterator.next()).match_address(address.address()))
                {
                    continue;
                }
                flag1 = true;
                break;
            } while (true);
            if (!flag1)
            {
                try
                {
                    socket1.close();
                }
                catch (IOException ioexception1)
                {
                    return null;
                }
                return null;
            }
        }
        return socket1.getChannel();
    }

    private void close()
    {
        if (handle == null)
        {
            return;
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

    public void accept_event()
    {
        SocketChannel socketchannel;
        StreamEngine streamengine;
        SessionBase sessionbase;
        try
        {
            socketchannel = accept();
            Utils.tune_tcp_socket(socketchannel);
            Utils.tune_tcp_keepalives(socketchannel, options.tcp_keepalive, options.tcp_keepalive_cnt, options.tcp_keepalive_idle, options.tcp_keepalive_intvl);
        }
        catch (IOException ioexception)
        {
            socket.event_accept_failed(endpoint, ZError.exccode(ioexception));
            return;
        }
        try
        {
            streamengine = new StreamEngine(socketchannel, options, endpoint);
        }
        catch (ZError.InstantiationException instantiationexception)
        {
            socket.event_accept_failed(endpoint, 22);
            return;
        }
        sessionbase = SessionBase.create(choose_io_thread(options.affinity), false, socket, options, new Address(socketchannel.socket().getRemoteSocketAddress()));
        sessionbase.inc_seqnum();
        launch_child(sessionbase);
        send_attach(sessionbase, streamengine, false);
        socket.event_accepted(endpoint, socketchannel);
    }

    public void connect_event()
    {
        throw new UnsupportedOperationException();
    }

    public void destroy()
    {
        if (!$assertionsDisabled && handle != null)
        {
            throw new AssertionError();
        } else
        {
            return;
        }
    }

    public String get_address()
    {
        return address.toString();
    }

    public void in_event()
    {
        throw new UnsupportedOperationException();
    }

    public void out_event()
    {
        throw new UnsupportedOperationException();
    }

    protected void process_plug()
    {
        io_object.set_handler(this);
        io_object.add_fd(handle);
        io_object.set_pollaccept(handle);
    }

    protected void process_term(int i)
    {
        io_object.set_handler(this);
        io_object.rm_fd(handle);
        close();
        super.process_term(i);
    }

    public int set_address(String s)
    {
        boolean flag = true;
        TcpAddress tcpaddress = address;
        if (options.ipv4only <= 0)
        {
            flag = false;
        }
        tcpaddress.resolve(s, flag);
        endpoint = address.toString();
        try
        {
            handle = ServerSocketChannel.open();
            handle.configureBlocking(false);
            handle.socket().setReuseAddress(true);
            handle.socket().bind(address.address(), options.backlog);
        }
        catch (IOException ioexception)
        {
            close();
            return 48;
        }
        socket.event_listening(endpoint, handle);
        return 0;
    }

    public void timer_event(int i)
    {
        throw new UnsupportedOperationException();
    }

    static 
    {
        boolean flag;
        if (!zmq/TcpListener.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
