// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq.codec;

import zmq.DecoderBase;
import zmq.IMsgSink;
import zmq.Msg;

// Referenced classes of package org.jeromq.codec:
//            Proxy

public static abstract class bottom extends DecoderBase
{

    private static final int read_body = 1;
    private static final int read_header;
    private Msg bottom;
    private byte header[];
    private boolean identity_sent;
    private Msg msg;
    private IMsgSink msg_sink;
    private int size;

    private boolean readBody()
    {
        if (msg_sink == null)
        {
            return false;
        }
        if (!parseBody(msg.data()))
        {
            decoding_error();
            return false;
        }
        if (!identity_sent)
        {
            Msg msg1 = new Msg(getIdentity());
            msg_sink.push_msg(msg1);
            identity_sent = true;
        }
        msg_sink.push_msg(bottom);
        if (preserveHeader())
        {
            Msg msg2 = new Msg(header, true);
            msg2.set_flags(1);
            msg_sink.push_msg(msg2);
        }
        msg_sink.push_msg(msg);
        next_step(header, headerSize(), 0);
        return true;
    }

    private boolean readHeader()
    {
        size = parseHeader(header);
        if (size < 0)
        {
            decoding_error();
            return false;
        } else
        {
            msg = new Msg(size);
            next_step(msg, 1);
            return true;
        }
    }

    protected byte[] getIdentity()
    {
        return null;
    }

    protected abstract int headerSize();

    protected boolean next()
    {
        switch (state())
        {
        default:
            return false;

        case 0: // '\0'
            return readHeader();

        case 1: // '\001'
            return readBody();
        }
    }

    protected abstract boolean parseBody(byte abyte0[]);

    protected abstract int parseHeader(byte abyte0[]);

    protected boolean preserveHeader()
    {
        return false;
    }

    public void set_msg_sink(IMsgSink imsgsink)
    {
        msg_sink = imsgsink;
    }

    public boolean stalled()
    {
        return state() == 1;
    }

    public (int i, long l)
    {
        super(i);
        size = -1;
        identity_sent = false;
        header = new byte[headerSize()];
        next_step(header, header.length, 0);
        bottom = new Msg();
        bottom.set_flags(1);
    }
}
