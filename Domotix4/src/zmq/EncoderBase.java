// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

// Referenced classes of package zmq:
//            IEncoder, Msg, Transfer

public abstract class EncoderBase
    implements IEncoder
{

    private boolean beginning;
    private ByteBuffer buf;
    private int buffersize;
    private boolean error;
    private int next;
    private int to_write;
    private byte write_buf[];
    private FileChannel write_channel;
    private int write_pos;

    protected EncoderBase(int i)
    {
        buffersize = i;
        buf = ByteBuffer.allocateDirect(i);
        error = false;
    }

    protected void encoding_error()
    {
        error = true;
    }

    public Transfer get_data(ByteBuffer bytebuffer)
    {
        if (bytebuffer == null)
        {
            bytebuffer = buf;
        }
        bytebuffer.clear();
        do
        {
            int i;
            do
            {
                if (!bytebuffer.hasRemaining() || to_write == 0 && !next())
                {
                    bytebuffer.flip();
                    return new Transfer.ByteBufferTransfer(bytebuffer);
                }
                if (write_channel != null)
                {
                    bytebuffer.flip();
                    FileChannel filechannel = write_channel;
                    long l = write_pos;
                    long l1 = to_write;
                    Transfer.FileChannelTransfer filechanneltransfer = new Transfer.FileChannelTransfer(bytebuffer, filechannel, l, l1);
                    write_pos = 0;
                    to_write = 0;
                    return filechanneltransfer;
                }
                if (buf.position() == 0 && to_write >= buffersize)
                {
                    ByteBuffer bytebuffer1 = ByteBuffer.wrap(write_buf);
                    bytebuffer1.position(write_pos);
                    Transfer.ByteBufferTransfer bytebuffertransfer = new Transfer.ByteBufferTransfer(bytebuffer1);
                    write_pos = 0;
                    to_write = 0;
                    return bytebuffertransfer;
                }
                i = Math.min(to_write, bytebuffer.remaining());
            } while (i <= 0);
            bytebuffer.put(write_buf, write_pos, i);
            write_pos = i + write_pos;
            to_write = to_write - i;
        } while (true);
    }

    public boolean has_data()
    {
        return to_write > 0;
    }

    public final boolean is_error()
    {
        return error;
    }

    protected abstract boolean next();

    protected void next_step(FileChannel filechannel, long l, long l1, int i, boolean flag)
    {
        write_buf = null;
        write_channel = filechannel;
        write_pos = (int)l;
        to_write = (int)l1;
        next = i;
        beginning = flag;
    }

    protected void next_step(Msg msg, int i, boolean flag)
    {
        if (msg == null)
        {
            next_step(null, 0, i, flag);
            return;
        } else
        {
            next_step(msg.data(), msg.size(), i, flag);
            return;
        }
    }

    protected void next_step(byte abyte0[], int i, int j, boolean flag)
    {
        write_buf = abyte0;
        write_channel = null;
        write_pos = 0;
        to_write = i;
        next = j;
        beginning = flag;
    }

    protected int state()
    {
        return next;
    }

    protected void state(int i)
    {
        next = i;
    }
}
