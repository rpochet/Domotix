// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public interface Transfer
{
    public static class ByteBufferTransfer
        implements Transfer
    {

        private ByteBuffer buf;

        public final int remaining()
        {
            return buf.remaining();
        }

        public final int transferTo(WritableByteChannel writablebytechannel)
            throws IOException
        {
            return writablebytechannel.write(buf);
        }

        public ByteBufferTransfer(ByteBuffer bytebuffer)
        {
            buf = bytebuffer;
        }
    }

    public static class FileChannelTransfer
        implements Transfer
    {

        private FileChannel ch;
        private long count;
        private Transfer parent;
        private long position;
        private int remaining;

        public final int remaining()
        {
            return remaining;
        }

        public final int transferTo(WritableByteChannel writablebytechannel)
            throws IOException
        {
            int i = parent.remaining();
            int j = 0;
            if (i > 0)
            {
                j = parent.transferTo(writablebytechannel);
            }
            if (parent.remaining() == 0)
            {
                long l = ch.transferTo(position, count, writablebytechannel);
                position = l + position;
                count = count - l;
                j = (int)(l + (long)j);
            }
            remaining = remaining - j;
            if (remaining == 0)
            {
                ch.close();
            }
            return j;
        }

        public FileChannelTransfer(ByteBuffer bytebuffer, FileChannel filechannel, long l, long l1)
        {
            parent = new ByteBufferTransfer(bytebuffer);
            ch = filechannel;
            position = l;
            count = l1;
            remaining = parent.remaining() + (int)count;
        }
    }


    public abstract int remaining();

    public abstract int transferTo(WritableByteChannel writablebytechannel)
        throws IOException;
}
