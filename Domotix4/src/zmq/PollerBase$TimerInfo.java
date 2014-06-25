// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            PollerBase, IPollEvents

private final class id
{

    int id;
    IPollEvents sink;
    final PollerBase this$0;

    public (IPollEvents ipollevents, int i)
    {
        this$0 = PollerBase.this;
        super();
        sink = ipollevents;
        id = i;
    }
}
