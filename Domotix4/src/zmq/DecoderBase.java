// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.ByteBuffer;

// Referenced classes of package zmq:
//            IDecoder, Msg

public abstract class DecoderBase
    implements IDecoder
{

    private ByteBuffer buf;
    private int bufsize;
    private byte read_buf[];
    private int read_pos;
    private int state;
    protected int to_read;
    boolean zero_copy;

    public DecoderBase(int i)
    {
        state = -1;
        to_read = 0;
        bufsize = i;
        if (i > 0)
        {
            buf = ByteBuffer.allocateDirect(i);
        }
        read_buf = null;
        zero_copy = false;
    }

    protected void decoding_error()
    {
        state(-1);
    }

    public ByteBuffer get_buffer()
    {
        if (to_read >= bufsize)
        {
            zero_copy = true;
            ByteBuffer bytebuffer1 = ByteBuffer.wrap(read_buf);
            bytebuffer1.position(read_pos);
            return bytebuffer1;
        } else
        {
            zero_copy = false;
            ByteBuffer bytebuffer = buf;
            bytebuffer.clear();
            return bytebuffer;
        }
    }

    protected abstract boolean next();

    protected void next_step(Msg msg, int i)
    {
        next_step(msg.data(), msg.size(), i);
    }

    protected void next_step(byte abyte0[], int i, int j)
    {
        read_buf = abyte0;
        read_pos = 0;
        to_read = i;
        state = j;
    }

    public int process_buffer(ByteBuffer bytebuffer, int i)
    {
        if (state() >= 0) goto _L2; else goto _L1
_L1:
        i = -1;
_L4:
        return i;
_L2:
        if (!zero_copy)
        {
            break; /* Loop/switch isn't completed */
        }
        read_pos = i + read_pos;
        to_read = to_read - i;
        do
        {
            if (to_read != 0)
            {
                continue; /* Loop/switch isn't completed */
            }
        } while (next());
        if (state() < 0)
        {
            return -1;
        }
        if (true) goto _L4; else goto _L3
_L3:
        int j = 0;
        do
        {
            if (to_read == 0)
            {
                if (!next())
                {
                    if (state() < 0)
                    {
                        return -1;
                    } else
                    {
                        return j;
                    }
                }
            } else
            {
                if (j == i)
                {
                    return j;
                }
                int k = Math.min(to_read, i - j);
                bytebuffer.get(read_buf, read_pos, k);
                read_pos = k + read_pos;
                j += k;
                to_read = to_read - k;
            }
        } while (true);
    }

    public boolean stalled()
    {
        if (next()) goto _L2; else goto _L1
_L1:
        return false;
_L2:
        do
        {
            if (to_read != 0)
            {
                continue; /* Loop/switch isn't completed */
            }
        } while (next());
        if (next())
        {
            return true;
        }
        if (true) goto _L1; else goto _L3
_L3:
    }

    protected int state()
    {
        return state;
    }

    protected void state(int i)
    {
        state = i;
    }
}
