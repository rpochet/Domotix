// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

// Referenced classes of package zmq:
//            IEngine, IPollEvents, IMsgSink, Utils, 
//            Options, SocketBase, SessionBase, Config, 
//            EncoderBase, DecoderBase, IOObject, V1Decoder, 
//            Decoder, V1Encoder, Encoder, IMsgSource, 
//            Transfer, Msg, IOThread

public class StreamEngine
    implements IEngine, IPollEvents, IMsgSink
{

    static final boolean $assertionsDisabled = false;
    private static final int GREETING_SIZE = 12;
    private DecoderBase decoder;
    private EncoderBase encoder;
    private String endpoint;
    private final ByteBuffer greeting = ByteBuffer.allocate(12);
    private final ByteBuffer greeting_output_buffer = ByteBuffer.allocate(12);
    private SocketChannel handle;
    private boolean handshaking;
    private ByteBuffer inbuf;
    private int insize;
    private boolean io_enabled;
    private IOObject io_object;
    private Options options;
    private Transfer outbuf;
    private int outsize;
    private boolean plugged;
    private SessionBase session;
    private SocketBase socket;
    private boolean terminating;

    public StreamEngine(SocketChannel socketchannel, Options options1, String s)
    {
        handle = socketchannel;
        inbuf = null;
        insize = 0;
        io_enabled = false;
        outbuf = null;
        outsize = 0;
        handshaking = true;
        session = null;
        options = options1;
        plugged = false;
        terminating = false;
        endpoint = s;
        socket = null;
        encoder = null;
        decoder = null;
        try
        {
            Utils.unblock_socket(handle);
            if (options.sndbuf != 0)
            {
                handle.socket().setSendBufferSize(options.sndbuf);
            }
            if (options.rcvbuf != 0)
            {
                handle.socket().setReceiveBufferSize(options.rcvbuf);
            }
            return;
        }
        catch (IOException ioexception)
        {
            throw new ZError.IOException(ioexception);
        }
    }

    private void error()
    {
        if (!$assertionsDisabled && session == null)
        {
            throw new AssertionError();
        } else
        {
            socket.event_disconnected(endpoint, handle);
            session.detach();
            unplug();
            destroy();
            return;
        }
    }

    private boolean handshake()
    {
        if (!$assertionsDisabled && !handshaking)
        {
            throw new AssertionError();
        }
          goto _L1
_L4:
        if (greeting.position() >= 10) goto _L2; else goto _L1
_L1:
        if (greeting.position() >= 12)
        {
            break; /* Loop/switch isn't completed */
        }
        int i = read(greeting);
        if (i == -1)
        {
            error();
            return false;
        }
        if (i == 0)
        {
            return false;
        }
        if (greeting.array()[0] == -1) goto _L4; else goto _L3
_L3:
        if (greeting.array()[0] == -1 && (1 & greeting.array()[9]) != 0) goto _L6; else goto _L5
_L2:
        if ((1 & greeting.array()[9]) == 0) goto _L3; else goto _L7
_L7:
        if (greeting_output_buffer.limit() < 12)
        {
            if (outsize == 0)
            {
                io_object.set_pollout(handle);
            }
            int j = greeting_output_buffer.position();
            greeting_output_buffer.position(10).limit(12);
            greeting_output_buffer.put((byte)1);
            greeting_output_buffer.put((byte)options.type);
            greeting_output_buffer.position(j);
            outsize = 2 + outsize;
        }
          goto _L1
_L5:
        encoder = new_encoder(Config.out_batch_size.getValue(), null, 0);
        encoder.set_msg_source(session);
        decoder = new_decoder(Config.in_batch_size.getValue(), options.maxmsgsize, null, 0);
        decoder.set_msg_sink(session);
        byte byte0;
        ByteBuffer bytebuffer;
        if (1 + options.identity_size >= 255)
        {
            byte0 = 10;
        } else
        {
            byte0 = 2;
        }
        bytebuffer = ByteBuffer.allocate(byte0);
        encoder.get_data(bytebuffer);
        if (!$assertionsDisabled && bytebuffer.remaining() != byte0)
        {
            throw new AssertionError();
        }
        inbuf = greeting;
        greeting.flip();
        insize = greeting.remaining();
        if (options.type == 1 || options.type == 9)
        {
            decoder.set_msg_sink(this);
        }
_L9:
        if (outsize == 0)
        {
            io_object.set_pollout(handle);
        }
        handshaking = false;
        return true;
_L6:
        if (greeting.array()[10] == 0)
        {
            encoder = new_encoder(Config.out_batch_size.getValue(), null, 0);
            encoder.set_msg_source(session);
            decoder = new_decoder(Config.in_batch_size.getValue(), options.maxmsgsize, null, 0);
            decoder.set_msg_sink(session);
        } else
        {
            encoder = new_encoder(Config.out_batch_size.getValue(), session, 1);
            decoder = new_decoder(Config.in_batch_size.getValue(), options.maxmsgsize, session, 1);
        }
        if (true) goto _L9; else goto _L8
_L8:
    }

    private DecoderBase new_decoder(int i, long l, SessionBase sessionbase, int j)
    {
        if (options.decoder == null)
        {
            if (j == 1)
            {
                return new V1Decoder(i, l, sessionbase);
            } else
            {
                return new Decoder(i, l);
            }
        }
        if (j != 0)
        {
            break MISSING_BLOCK_LABEL_114;
        }
        Class class2 = options.decoder;
        Class aclass1[] = new Class[2];
        aclass1[0] = Integer.TYPE;
        aclass1[1] = Long.TYPE;
        Constructor constructor1 = class2.getConstructor(aclass1);
        Object aobj1[] = new Object[2];
        aobj1[0] = Integer.valueOf(i);
        aobj1[1] = Long.valueOf(l);
        return (DecoderBase)constructor1.newInstance(aobj1);
        DecoderBase decoderbase;
        Class class1 = options.decoder;
        Class aclass[] = new Class[4];
        aclass[0] = Integer.TYPE;
        aclass[1] = Long.TYPE;
        aclass[2] = zmq/IMsgSink;
        aclass[3] = Integer.TYPE;
        Constructor constructor = class1.getConstructor(aclass);
        Object aobj[] = new Object[4];
        aobj[0] = Integer.valueOf(i);
        aobj[1] = Long.valueOf(l);
        aobj[2] = sessionbase;
        aobj[3] = Integer.valueOf(j);
        decoderbase = (DecoderBase)constructor.newInstance(aobj);
        return decoderbase;
        SecurityException securityexception;
        securityexception;
        throw new ZError.InstantiationException(securityexception);
        NoSuchMethodException nosuchmethodexception;
        nosuchmethodexception;
        throw new ZError.InstantiationException(nosuchmethodexception);
        InvocationTargetException invocationtargetexception;
        invocationtargetexception;
        throw new ZError.InstantiationException(invocationtargetexception);
        IllegalAccessException illegalaccessexception;
        illegalaccessexception;
        throw new ZError.InstantiationException(illegalaccessexception);
        InstantiationException instantiationexception;
        instantiationexception;
        throw new ZError.InstantiationException(instantiationexception);
    }

    private EncoderBase new_encoder(int i, SessionBase sessionbase, int j)
    {
        if (options.encoder == null)
        {
            if (j == 1)
            {
                return new V1Encoder(i, sessionbase);
            } else
            {
                return new Encoder(i);
            }
        }
        if (j != 0)
        {
            break MISSING_BLOCK_LABEL_94;
        }
        Class class2 = options.encoder;
        Class aclass1[] = new Class[1];
        aclass1[0] = Integer.TYPE;
        Constructor constructor1 = class2.getConstructor(aclass1);
        Object aobj1[] = new Object[1];
        aobj1[0] = Integer.valueOf(i);
        return (EncoderBase)constructor1.newInstance(aobj1);
        EncoderBase encoderbase;
        Class class1 = options.encoder;
        Class aclass[] = new Class[3];
        aclass[0] = Integer.TYPE;
        aclass[1] = zmq/IMsgSource;
        aclass[2] = Integer.TYPE;
        Constructor constructor = class1.getConstructor(aclass);
        Object aobj[] = new Object[3];
        aobj[0] = Integer.valueOf(i);
        aobj[1] = sessionbase;
        aobj[2] = Integer.valueOf(j);
        encoderbase = (EncoderBase)constructor.newInstance(aobj);
        return encoderbase;
        SecurityException securityexception;
        securityexception;
        throw new ZError.InstantiationException(securityexception);
        NoSuchMethodException nosuchmethodexception;
        nosuchmethodexception;
        throw new ZError.InstantiationException(nosuchmethodexception);
        InvocationTargetException invocationtargetexception;
        invocationtargetexception;
        throw new ZError.InstantiationException(invocationtargetexception);
        IllegalAccessException illegalaccessexception;
        illegalaccessexception;
        throw new ZError.InstantiationException(illegalaccessexception);
        InstantiationException instantiationexception;
        instantiationexception;
        throw new ZError.InstantiationException(instantiationexception);
    }

    private int read(ByteBuffer bytebuffer)
    {
        int i;
        try
        {
            i = handle.read(bytebuffer);
        }
        catch (IOException ioexception)
        {
            return -1;
        }
        return i;
    }

    private void unplug()
    {
        if (!$assertionsDisabled && !plugged)
        {
            throw new AssertionError();
        }
        plugged = false;
        if (io_enabled)
        {
            io_object.rm_fd(handle);
            io_enabled = false;
        }
        io_object.unplug();
        if (encoder != null)
        {
            encoder.set_msg_source(null);
        }
        if (decoder != null)
        {
            decoder.set_msg_sink(null);
        }
        session = null;
    }

    private int write(Transfer transfer)
    {
        int i;
        try
        {
            i = transfer.transferTo(handle);
        }
        catch (IOException ioexception)
        {
            return -1;
        }
        return i;
    }

    public void accept_event()
    {
        throw new UnsupportedOperationException();
    }

    public void activate_in()
    {
        if (!io_enabled)
        {
            decoder.process_buffer(inbuf, 0);
            if (!$assertionsDisabled && decoder.stalled())
            {
                throw new AssertionError();
            } else
            {
                session.flush();
                error();
                return;
            }
        } else
        {
            io_object.set_pollin(handle);
            io_object.in_event();
            return;
        }
    }

    public void activate_out()
    {
        io_object.set_pollout(handle);
        out_event();
    }

    public void connect_event()
    {
        throw new UnsupportedOperationException();
    }

    public void destroy()
    {
        if (!$assertionsDisabled && plugged)
        {
            throw new AssertionError();
        }
        if (handle != null)
        {
            try
            {
                handle.close();
            }
            catch (IOException ioexception) { }
            handle = null;
        }
    }

    public void in_event()
    {
        if (!handshaking || handshake())
        {
            if (!$assertionsDisabled && decoder == null)
            {
                throw new AssertionError();
            }
            int i = insize;
            boolean flag = false;
            if (i == 0)
            {
                inbuf = decoder.get_buffer();
                insize = read(inbuf);
                inbuf.flip();
                int k = insize;
                flag = false;
                if (k == -1)
                {
                    insize = 0;
                    flag = true;
                }
            }
            int j = decoder.process_buffer(inbuf, insize);
            if (j == -1)
            {
                flag = true;
            } else
            {
                if (j < insize)
                {
                    io_object.reset_pollin(handle);
                }
                insize = insize - j;
            }
            session.flush();
            if (flag)
            {
                if (decoder.stalled())
                {
                    io_object.rm_fd(handle);
                    io_enabled = false;
                    return;
                } else
                {
                    error();
                    return;
                }
            }
        }
    }

    public void out_event()
    {
        if (outsize != 0) goto _L2; else goto _L1
_L1:
        if (encoder != null) goto _L4; else goto _L3
_L3:
        if (!$assertionsDisabled && !handshaking)
        {
            throw new AssertionError();
        }
          goto _L5
_L4:
        outbuf = encoder.get_data(null);
        outsize = outbuf.remaining();
        if (outbuf.remaining() != 0) goto _L2; else goto _L6
_L6:
        io_object.reset_pollout(handle);
        if (encoder.is_error())
        {
            error();
        }
_L5:
        return;
_L2:
        int i;
        i = write(outbuf);
        if (i != -1)
        {
            break; /* Loop/switch isn't completed */
        }
        io_object.reset_pollout(handle);
        if (terminating)
        {
            terminate();
            return;
        }
        if (true) goto _L5; else goto _L7
_L7:
        outsize = outsize - i;
        if (handshaking && outsize == 0)
        {
            io_object.reset_pollout(handle);
        }
        if (outsize == 0)
        {
            if (encoder != null && encoder.is_error())
            {
                error();
                return;
            }
            if (terminating)
            {
                terminate();
                return;
            }
        }
        if (true) goto _L5; else goto _L8
_L8:
    }

    public void plug(IOThread iothread, SessionBase sessionbase)
    {
        if (!$assertionsDisabled && plugged)
        {
            throw new AssertionError();
        }
        plugged = true;
        if (!$assertionsDisabled && session != null)
        {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && sessionbase == null)
        {
            throw new AssertionError();
        }
        session = sessionbase;
        socket = session.get_soket();
        io_object = new IOObject(null);
        io_object.set_handler(this);
        io_object.plug(iothread);
        io_object.add_fd(handle);
        io_enabled = true;
        greeting_output_buffer.put((byte)-1);
        greeting_output_buffer.putLong(1 + options.identity_size);
        greeting_output_buffer.put((byte)127);
        io_object.set_pollin(handle);
        if (options.encoder == null) goto _L2; else goto _L1
_L1:
        java.lang.reflect.Field field = options.encoder.getDeclaredField("RAW_ENCODER");
        if (field == null) goto _L2; else goto _L3
_L3:
        boolean flag = true;
_L5:
        if (!flag)
        {
            outsize = greeting_output_buffer.position();
            outbuf = new Transfer.ByteBufferTransfer((ByteBuffer)greeting_output_buffer.flip());
            io_object.set_pollout(handle);
        }
        in_event();
        return;
_L2:
        flag = false;
        continue; /* Loop/switch isn't completed */
        NoSuchFieldException nosuchfieldexception;
        nosuchfieldexception;
        flag = false;
        continue; /* Loop/switch isn't completed */
        SecurityException securityexception;
        securityexception;
        flag = false;
        if (true) goto _L5; else goto _L4
_L4:
    }

    public int push_msg(Msg msg)
    {
        if (!$assertionsDisabled && options.type != 1 && options.type != 9)
        {
            throw new AssertionError();
        }
        int i = session.push_msg(msg);
        if (!$assertionsDisabled && i != 0)
        {
            throw new AssertionError();
        }
        Msg msg1 = new Msg(1);
        msg1.put((byte)1);
        int j = session.push_msg(msg1);
        session.flush();
        if (!$assertionsDisabled && decoder == null)
        {
            throw new AssertionError();
        } else
        {
            decoder.set_msg_sink(session);
            return j;
        }
    }

    public void terminate()
    {
        if (!terminating && encoder != null && encoder.has_data())
        {
            terminating = true;
            return;
        } else
        {
            unplug();
            destroy();
            return;
        }
    }

    public void timer_event(int i)
    {
        throw new UnsupportedOperationException();
    }

    static 
    {
        boolean flag;
        if (!zmq/StreamEngine.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
