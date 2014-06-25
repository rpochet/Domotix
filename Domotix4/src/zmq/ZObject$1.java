// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            ZObject

static class chFieldError
{

    static final int $SwitchMap$zmq$Command$Type[];

    static 
    {
        $SwitchMap$zmq$Command$Type = new int[pe.values().length];
        try
        {
            $SwitchMap$zmq$Command$Type[pe.activate_read.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        try
        {
            $SwitchMap$zmq$Command$Type[pe.activate_write.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            $SwitchMap$zmq$Command$Type[pe.stop.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            $SwitchMap$zmq$Command$Type[pe.plug.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            $SwitchMap$zmq$Command$Type[pe.own.ordinal()] = 5;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            $SwitchMap$zmq$Command$Type[pe.attach.ordinal()] = 6;
        }
        catch (NoSuchFieldError nosuchfielderror5) { }
        try
        {
            $SwitchMap$zmq$Command$Type[pe.bind.ordinal()] = 7;
        }
        catch (NoSuchFieldError nosuchfielderror6) { }
        try
        {
            $SwitchMap$zmq$Command$Type[pe.hiccup.ordinal()] = 8;
        }
        catch (NoSuchFieldError nosuchfielderror7) { }
        try
        {
            $SwitchMap$zmq$Command$Type[pe.pipe_term.ordinal()] = 9;
        }
        catch (NoSuchFieldError nosuchfielderror8) { }
        try
        {
            $SwitchMap$zmq$Command$Type[pe.pipe_term_ack.ordinal()] = 10;
        }
        catch (NoSuchFieldError nosuchfielderror9) { }
        try
        {
            $SwitchMap$zmq$Command$Type[pe.term_req.ordinal()] = 11;
        }
        catch (NoSuchFieldError nosuchfielderror10) { }
        try
        {
            $SwitchMap$zmq$Command$Type[pe.term.ordinal()] = 12;
        }
        catch (NoSuchFieldError nosuchfielderror11) { }
        try
        {
            $SwitchMap$zmq$Command$Type[pe.term_ack.ordinal()] = 13;
        }
        catch (NoSuchFieldError nosuchfielderror12) { }
        try
        {
            $SwitchMap$zmq$Command$Type[pe.reap.ordinal()] = 14;
        }
        catch (NoSuchFieldError nosuchfielderror13) { }
        try
        {
            $SwitchMap$zmq$Command$Type[pe.reaped.ordinal()] = 15;
        }
        catch (NoSuchFieldError nosuchfielderror14)
        {
            return;
        }
    }
}
