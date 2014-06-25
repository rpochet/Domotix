// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            Msg, Req, IOThread, SocketBase, 
//            Options, Address

public static class State.identity extends ssion
{
    static final class State extends Enum
    {

        private static final State $VALUES[];
        public static final State body;
        public static final State bottom;
        public static final State identity;

        public static State valueOf(String s)
        {
            return (State)Enum.valueOf(zmq/Req$ReqSession$State, s);
        }

        public static State[] values()
        {
            return (State[])$VALUES.clone();
        }

        static 
        {
            identity = new State("identity", 0);
            bottom = new State("bottom", 1);
            body = new State("body", 2);
            State astate[] = new State[3];
            astate[0] = identity;
            astate[1] = bottom;
            astate[2] = body;
            $VALUES = astate;
        }

        private State(String s, int i)
        {
            super(s, i);
        }
    }


    State state;

    public int push_msg(Msg msg)
    {
        p.zmq.Req.ReqSession.State[state.ordinal()];
        JVM INSTR tableswitch 1 3: default 36
    //                   1 51
    //                   2 79
    //                   3 113;
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

    public t>(IOThread iothread, boolean flag, SocketBase socketbase, Options options, Address address)
    {
        super(iothread, flag, socketbase, options, address);
        state = State.identity;
    }
}
