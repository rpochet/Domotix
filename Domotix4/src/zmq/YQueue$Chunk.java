// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.lang.reflect.Array;

// Referenced classes of package zmq:
//            YQueue

private class pos
{

    static final boolean $assertionsDisabled;
    ed next;
    final int pos[];
    ed prev;
    final YQueue this$0;
    final Object values[];

    static 
    {
        boolean flag;
        if (!zmq/YQueue.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }

    protected onError(Class class1, int i, int j)
    {
        this$0 = YQueue.this;
        super();
        values = (Object[])(Object[])Array.newInstance(class1, i);
        pos = new int[i];
        if (!$assertionsDisabled && values == null)
        {
            throw new AssertionError();
        }
        next = null;
        prev = null;
        for (int k = 0; k != values.length; k++)
        {
            pos[k] = j;
            j++;
        }

    }
}
