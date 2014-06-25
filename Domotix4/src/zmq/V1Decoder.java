// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.ByteBuffer;

// Referenced classes of package zmq:
//            DecoderBase, Msg, IMsgSink

public class V1Decoder extends DecoderBase
{

    private static final int eight_byte_size_ready = 1;
    private static final int flags_ready = 2;
    private static final int message_ready = 3;
    private static final int one_byte_size_ready;
    private Msg in_progress;
    private final long maxmsgsize;
    private int msg_flags;
    private IMsgSink msg_sink;
    private final byte tmpbuf[] = new byte[8];

    public V1Decoder(int i, long l, IMsgSink imsgsink)
    {
        super(i);
        maxmsgsize = l;
        msg_sink = imsgsink;
        next_step(tmpbuf, 1, 2);
    }

    private boolean eight_byte_size_ready()
    {
        long l = ByteBuffer.wrap(tmpbuf).getLong();
        if (maxmsgsize >= 0L && l > maxmsgsize)
        {
            decoding_error();
            return false;
        }
        if (l > 0x7fffffffL)
        {
            decoding_error();
            return false;
        } else
        {
            in_progress = new Msg((int)l);
            in_progress.set_flags(msg_flags);
            next_step(in_progress.data(), in_progress.size(), 3);
            return true;
        }
    }

    private boolean flags_ready()
    {
        msg_flags = 0;
        byte byte0 = tmpbuf[0];
        if ((byte0 & 1) > 0)
        {
            msg_flags = 1 | msg_flags;
        }
        if ((byte0 & 2) > 0)
        {
            next_step(tmpbuf, 8, 1);
            return true;
        } else
        {
            next_step(tmpbuf, 1, 0);
            return true;
        }
    }

    private boolean message_ready()
    {
        if (msg_sink != null)
        {
            int i = msg_sink.push_msg(in_progress);
            if (i != 0)
            {
                if (i != 35)
                {
                    decoding_error();
                    return false;
                }
            } else
            {
                next_step(tmpbuf, 1, 2);
                return true;
            }
        }
        return false;
    }

    private boolean one_byte_size_ready()
    {
        int i = tmpbuf[0];
        if (i < 0)
        {
            i &= 0xff;
        }
        if (maxmsgsize >= 0L && (long)i > maxmsgsize)
        {
            decoding_error();
            return false;
        } else
        {
            in_progress = new Msg(i);
            in_progress.set_flags(msg_flags);
            next_step(in_progress.data(), in_progress.size(), 3);
            return true;
        }
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
