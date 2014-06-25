// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.ByteBuffer;

// Referenced classes of package zmq:
//            DecoderBase, Msg, IMsgSink

public class Decoder extends DecoderBase
{

    private static final int eight_byte_size_ready = 1;
    private static final int flags_ready = 2;
    private static final int message_ready = 3;
    private static final int one_byte_size_ready;
    private Msg in_progress;
    private final long maxmsgsize;
    private IMsgSink msg_sink;
    private final byte tmpbuf[] = new byte[8];

    public Decoder(int i, long l)
    {
        super(i);
        maxmsgsize = l;
        next_step(tmpbuf, 1, 0);
    }

    private boolean eight_byte_size_ready()
    {
        long l = ByteBuffer.wrap(tmpbuf).getLong();
        if (l == 0L)
        {
            decoding_error();
            return false;
        }
        if (maxmsgsize >= 0L && l - 1L > maxmsgsize)
        {
            decoding_error();
            return false;
        }
        if (l - 1L > 0x7fffffffL)
        {
            decoding_error();
            return false;
        } else
        {
            in_progress = new Msg((int)(l - 1L));
            next_step(tmpbuf, 1, 2);
            return true;
        }
    }

    private boolean flags_ready()
    {
        byte byte0 = tmpbuf[0];
        in_progress.set_flags(byte0 & 1);
        next_step(in_progress, 3);
        return true;
    }

    private boolean message_ready()
    {
        while (msg_sink == null || msg_sink.push_msg(in_progress) != 0) 
        {
            return false;
        }
        next_step(tmpbuf, 1, 0);
        return true;
    }

    private boolean one_byte_size_ready()
    {
        byte byte0 = tmpbuf[0];
        if (byte0 == -1)
        {
            next_step(tmpbuf, 8, 1);
        } else
        {
            if (byte0 == 0)
            {
                decoding_error();
                return false;
            }
            int i = byte0;
            if (i < 0)
            {
                i = byte0 & 0xff;
            }
            if (maxmsgsize >= 0L && (long)(i - 1) > maxmsgsize)
            {
                decoding_error();
                return false;
            }
            in_progress = new Msg(i - 1);
            next_step(tmpbuf, 1, 2);
        }
        return true;
    }

    protected boolean next()
    {
        switch (state())
        {
        default:
            return false;

        case 0: // '\0'
            return one_byte_size_ready();

        case 1: // '\001'
            return eight_byte_size_ready();

        case 2: // '\002'
            return flags_ready();

        case 3: // '\003'
            return message_ready();
        }
    }

    public void set_msg_sink(IMsgSink imsgsink)
    {
        msg_sink = imsgsink;
    }
}
