// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            Dealer, Options, Msg, ValueReference, 
//            Ctx, IOThread, SocketBase, Address

public class Req extends Dealer
{
    public static class ReqSession extends Dealer.DealerSession
    {

        State state;

        public int push_msg(Msg msg)
        {
            static class _cls1
            {

                static final int $SwitchMap$zmq$Req$ReqSession$State[];

                static 
                {
                    $SwitchMap$zmq$Req$ReqSession$State = new int[ReqSession.State.values().length];
                    try
                    {
                        $SwitchMap$zmq$Req$ReqSession$State[ReqSession.State.bottom.ordinal()] = 1;
                    }
                    catch (NoSuchFieldError nosuchfielderror) { }
                    try
                    {
                        $SwitchMap$zmq$Req$ReqSession$State[ReqSession.State.body.ordinal()] = 2;
                    }
                    catch (NoSuchFieldError nosuchfielderror1) { }
                    try
                    {
                        $SwitchMap$zmq$Req$ReqSession$State[ReqSession.State.identity.ordinal()] = 3;
                    }
                    catch (NoSuchFieldError nosuchfielderror2)
                    {
                        return;
                    }
                }
            }

            _cls1..SwitchMap.zmq.Req.ReqSession.State[state.ordinal()];
            JVM INSTR tableswitch 1 3: default 36
        //                       1 51
        //                       2 79
        //                       3 113;
               goto _L1 _L2 _L3 _L4
_L1:
            throw new IllegalStateException(state.toString());
_L2:
            if (msg.flags() == 1 && msg.size() == 0)
            {
                state = State.body;
                return super.push_msg(msg);
            }
            continue; /* Loop/switch isn't completed */
_L3:
            if (msg.flags() == 1)
            {
                return super.push_msg(msg);
            }
            if (msg.flags() == 0)
            {
                state = State.bottom;
                return super.push_msg(msg);
            }
            continue; /* Loop/switch isn't completed */
_L4:
            if (msg.flags() == 0)
            {
                state = State.bottom;
                return super.push_msg(msg);
            }
            if (true) goto _L1; else goto _L5
_L5:
        }

        public void reset()
        {
            super.reset();
            state = State.identity;
        }

        public ReqSession(IOThread iothread, boolean flag, SocketBase socketbase, Options options, Address address)
        {
            super(iothread, flag, socketbase, options, address);
            state = State.identity;
        }
    }

    static final class ReqSession.State extends Enum
    {

        private static final ReqSession.State $VALUES[];
        public static final ReqSession.State body;
        public static final ReqSession.State bottom;
        public static final ReqSession.State identity;

        public static ReqSession.State valueOf(String s)
        {
            return (ReqSession.State)Enum.valueOf(zmq/Req$ReqSession$State, s);
        }

        public static ReqSession.State[] values()
        {
            return (ReqSession.State[])$VALUES.clone();
        }

        static 
        {
            identity = new ReqSession.State("identity", 0);
            bottom = new ReqSession.State("bottom", 1);
            body = new ReqSession.State("body", 2);
            ReqSession.State astate[] = new ReqSession.State[3];
            astate[0] = identity;
            astate[1] = bottom;
            astate[2] = body;
            $VALUES = astate;
        }

        private ReqSession.State(String s, int i)
        {
            super(s, i);
        }
    }


    static final boolean $assertionsDisabled;
    private boolean message_begins;
    private boolean receiving_reply;

    public Req(Ctx ctx, int i, int j)
    {
        super(ctx, i, j);
        receiving_reply = false;
        message_begins = true;
        options.type = 3;
    }

    public boolean xhas_in()
    {
        if (!receiving_reply)
        {
            return false;
        } else
        {
            return super.xhas_in();
        }
    }

    public boolean xhas_out()
    {
        if (receiving_reply)
        {
            return false;
        } else
        {
            return super.xhas_out();
        }
    }

    protected Msg xrecv()
    {
        if (!receiving_reply)
        {
            throw new IllegalStateException("Cannot wait before send");
        }
        if (!message_begins) goto _L2; else goto _L1
_L1:
        Msg msg1 = super.xrecv();
        if (msg1 != null) goto _L4; else goto _L3
_L3:
        return null;
_L4:
        if (!msg1.has_more() || msg1.size() != 0)
        {
            Msg msg2;
            do
            {
                msg2 = super.xrecv();
                if (!$assertionsDisabled && msg2 == null)
                {
                    throw new AssertionError();
                }
            } while (msg2.has_more());
            errno.set(Integer.valueOf(35));
            return null;
        }
        message_begins = false;
_L2:
        Msg msg = super.xrecv();
        if (msg != null)
        {
            if (!msg.has_more())
            {
                receiving_reply = false;
                message_begins = true;
            }
            return msg;
        }
        if (true) goto _L3; else goto _L5
_L5:
    }

    public boolean xsend(Msg msg)
    {
        boolean flag;
        flag = true;
        if (receiving_reply)
        {
            throw new IllegalStateException("Cannot send another request");
        }
        if (!message_begins) goto _L2; else goto _L1
_L1:
        boolean flag1;
        Msg msg1 = new Msg();
        msg1.set_flags(flag);
        flag1 = super.xsend(msg1);
        if (flag1) goto _L4; else goto _L3
_L3:
        flag = flag1;
_L6:
        return flag;
_L4:
        message_begins = false;
_L2:
        boolean flag2 = msg.has_more();
        boolean flag3 = super.xsend(msg);
        if (!flag3)
        {
            return flag3;
        }
        if (!flag2)
        {
            receiving_reply = flag;
            message_begins = flag;
            return flag;
        }
        if (true) goto _L6; else goto _L5
_L5:
    }

    static 
    {
        boolean flag;
        if (!zmq/Req.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
