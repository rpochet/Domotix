// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.ByteBuffer;

// Referenced classes of package zmq:
//            EncoderBase, IMsgSource, Msg

public class Encoder extends EncoderBase
{

    private static final int message_ready = 1;
    private static final int size_ready;
    private Msg in_progress;
    private IMsgSource msg_source;
    private final byte tmpbuf[] = new byte[10];

    public Encoder(int i)
    {
        super(i);
        next_step((byte[])null, 0, 1, true);
    }

    private final boolean message_ready()
    {
        if (msg_source != null)
        {
            in_progress = msg_source.pull_msg();
            if (in_progress != null)
            {
                int i = 1 + in_progress.size();
                if (i < 255)
                {
                    tmpbuf[0] = (byte)i;
                    tmpbuf[1] = (byte)(1 & in_progress.flags());
                    next_step(tmpbuf, 2, 0, false);
                } else
                {
                    ByteBuffer bytebuffer = ByteBuffer.wrap(tmpbuf);
                    bytebuffer.put((byte)-1);
                    bytebuffer.putLong(i);
                    bytebuffer.put((byte)(1 & in_progress.flags()));
                    next_step(tmpbuf, 10, 0, false);
                }
                return true;
            }
        }
        return false;
    }

    private final boolean size_ready()
    {
        byte abyte0[] = in_progress.data();
        int i = in_progress.size();
        boolean flag;
        if (!in_progress.has_more())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        next_step(abyte0, i, 1, flag);
        return true;
    }

    protected boolean next()
    {
        switch (state())
        {
        default:
            return false;

        case 0: // '\0'
            return size_ready();

        case 1: // '\001'
            return message_ready();
        }
    }

    public void set_msg_source(IMsgSource imsgsource)
    {
        msg_source = imsgsource;
    }
}
