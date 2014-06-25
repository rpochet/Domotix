// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            Router, Pipe

class active
{

    private boolean active;
    private Pipe pipe;
    final Router this$0;




/*
    static boolean access$102( , boolean flag)
    {
        .active = flag;
        return flag;
    }

*/

    public active(Pipe pipe1, boolean flag)
    {
        this$0 = Router.this;
        super();
        pipe = pipe1;
        active = flag;
    }
}
