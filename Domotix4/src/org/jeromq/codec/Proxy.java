// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.jeromq.codec;

import java.nio.ByteBuffer;
import zmq.DecoderBase;
import zmq.EncoderBase;
import zmq.IMsgSink;
import zmq.IMsgSource;
import zmq.Msg;

public class Proxy
{
    public static abstract class ProxyDecoder extends DecoderBase
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

        public ProxyDecoder(int i, long l)
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

    public static abstract class ProxyEncoder extends EncoderBase
    {

        public static final boolean RAW_ENCODER = true;
        private static final int write_body = 1;
        private static final int write_header;
        private ByteBuffer header;
        private boolean identity_received;
        private boolean message_ready;
        private Msg msg;
        private IMsgSource msg_source;

        private boolean write_body()
        {
            Msg msg1 = msg;
            boolean flag;
            if (!msg.has_more())
            {
                flag = true;
            } else
            {
                flag = false;
            }
            next_step(msg1, 0, flag);
            return true;
        }

        private boolean write_header()
        {
            if (msg_source != null) goto _L2; else goto _L1
_L1:
            return false;
_L2:
            msg = msg_source.pull_msg();
            if (msg == null) goto _L1; else goto _L3
_L3:
            if (identity_received)
            {
                break; /* Loop/switch isn't completed */
            }
            identity_received = true;
            msg = msg_source.pull_msg();
            if (msg == null) goto _L1; else goto _L4
_L4:
            if (message_ready)
            {
                break; /* Loop/switch isn't completed */
            }
            message_ready = true;
            msg = msg_source.pull_msg();
            if (msg == null) goto _L1; else goto _L5
_L5:
            message_ready = false;
            byte abyte0[] = getHeader(msg.data());
            if (abyte0 != null)
            {
                header.clear();
                header.put(abyte0);
                header.flip();
                next_step(header.array(), header.remaining(), 1, false);
            } else
            {
                next_step(abyte0, 0, 1, false);
            }
            return true;
        }

        protected abstract byte[] getHeader(byte abyte0[]);

        protected abstract int headerSize();

        protected boolean next()
        {
            switch (state())
            {
            default:
                return false;

            case 0: // '\0'
                return write_header();

            case 1: // '\001'
                return write_body();
            }
        }

        public void set_msg_source(IMsgSource imsgsource)
        {
            msg_source = imsgsource;
        }

        public ProxyEncoder(int i)
        {
            super(i);
            next_step(null, 0, true);
            message_ready = false;
            identity_received = false;
            header = ByteBuffer.allocate(headerSize());
        }
    }


    public Proxy()
    {
    }
}
