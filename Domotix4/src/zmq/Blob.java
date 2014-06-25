// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.nio.ByteBuffer;
import java.util.Arrays;

// Referenced classes of package zmq:
//            Utils

public class Blob
{

    private byte buf[];
    private int hash;

    public Blob(int i)
    {
        hash = 0;
        buf = new byte[i];
    }

    public Blob(ByteBuffer bytebuffer)
    {
        hash = 0;
        buf = Utils.bytes(bytebuffer);
    }

    public Blob(byte abyte0[])
    {
        hash = 0;
        buf = Arrays.copyOf(abyte0, abyte0.length);
    }

    public byte[] data()
    {
        return buf;
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof Blob)
        {
            return Arrays.equals(buf, ((Blob)obj).buf);
        } else
        {
            return false;
        }
    }

    public int hashCode()
    {
        if (hash == 0)
        {
            byte abyte0[] = buf;
            int i = abyte0.length;
            for (int j = 0; j < i; j++)
            {
                hash = abyte0[j] + 31 * hash;
            }

        }
        return hash;
    }

    public Blob put(int i, byte byte0)
    {
        buf[i] = byte0;
        hash = 0;
        return this;
    }

    public Blob put(int i, byte abyte0[])
    {
        System.arraycopy(abyte0, 0, buf, i, abyte0.length);
        hash = 0;
        return this;
    }

    public Blob put(int i, byte abyte0[], int j, int k)
    {
        System.arraycopy(abyte0, j, buf, i, k);
        hash = 0;
        return this;
    }

    public int size()
    {
        return buf.length;
    }
}
