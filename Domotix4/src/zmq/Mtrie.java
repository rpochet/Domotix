// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// Referenced classes of package zmq:
//            Utils, Pipe

public class Mtrie
{
    public static interface IMtrieHandler
    {

        public abstract void invoke(Pipe pipe, byte abyte0[], int i, Object obj);
    }


    static final boolean $assertionsDisabled;
    private int count;
    private int live_nodes;
    private int min;
    private Mtrie next[];
    private Set pipes;

    public Mtrie()
    {
        min = 0;
        count = 0;
        live_nodes = 0;
        pipes = null;
        next = null;
    }

    private boolean add_helper(byte abyte0[], int i, Pipe pipe)
    {
        boolean flag = true;
        if (abyte0 == null || abyte0.length == i)
        {
            if (pipes != null)
            {
                flag = false;
            }
            if (pipes == null)
            {
                pipes = new HashSet();
            }
            pipes.add(pipe);
            return flag;
        }
        int j = abyte0[i];
        if (j < min || j >= min + count)
        {
            if (count == 0)
            {
                min = j;
                count = ((flag) ? 1 : 0);
                next = null;
            } else
            if (count == flag)
            {
                int k = min;
                Mtrie mtrie = next[0];
                int l;
                if (min < j)
                {
                    l = j - min;
                } else
                {
                    l = min - j;
                }
                count = l + 1;
                next = new Mtrie[count];
                min = Math.min(min, j);
                next[k - min] = mtrie;
            } else
            if (min < j)
            {
                count = 1 + (j - min);
                next = realloc(next, count, flag);
            } else
            {
                count = (min + count) - j;
                next = realloc(next, count, false);
                min = j;
            }
        }
        if (count == flag)
        {
            if (next == null)
            {
                next = new Mtrie[flag];
                next[0] = new Mtrie();
                live_nodes = 1 + live_nodes;
            }
            return next[0].add_helper(abyte0, i + 1, pipe);
        }
        if (next[j - min] == null)
        {
            next[j - min] = new Mtrie();
            live_nodes = 1 + live_nodes;
        }
        return next[j - min].add_helper(abyte0, i + 1, pipe);
    }

    private boolean is_redundant()
    {
        return pipes == null && live_nodes == 0;
    }

    private Mtrie[] realloc(Mtrie amtrie[], int i, boolean flag)
    {
        return (Mtrie[])Utils.realloc(zmq/Mtrie, amtrie, i, flag);
    }

    private boolean rm_helper(Pipe pipe, byte abyte0[], int i, int j, IMtrieHandler imtriehandler, Object obj)
    {
        int k;
        int l;
        if (pipes != null && pipes.remove(pipe) && pipes.isEmpty())
        {
            imtriehandler.invoke(null, abyte0, i, obj);
            pipes = null;
        }
        if (i >= j)
        {
            j = i + 256;
            abyte0 = Utils.realloc(abyte0, j);
        }
        if (count == 0)
        {
            return true;
        }
        if (count == 1)
        {
            abyte0[i] = (byte)min;
            int k1 = i + 1;
            next[0].rm_helper(pipe, abyte0, k1, j, imtriehandler, obj);
            if (next[0].is_redundant())
            {
                next = null;
                count = 0;
                live_nodes = -1 + live_nodes;
                if (!$assertionsDisabled && live_nodes != 0)
                {
                    throw new AssertionError();
                }
            }
            return true;
        }
        k = -1 + (min + count);
        l = min;
        int i1 = 0;
        while (i1 != count) 
        {
            abyte0[i] = (byte)(i1 + min);
            if (next[i1] != null)
            {
                Mtrie mtrie1 = next[i1];
                int j1 = i + 1;
                mtrie1.rm_helper(pipe, abyte0, j1, j, imtriehandler, obj);
                if (next[i1].is_redundant())
                {
                    next[i1] = null;
                    if (!$assertionsDisabled && live_nodes <= 0)
                    {
                        throw new AssertionError();
                    }
                    live_nodes = -1 + live_nodes;
                } else
                {
                    if (i1 + min < k)
                    {
                        k = i1 + min;
                    }
                    if (i1 + min > l)
                    {
                        l = i1 + min;
                    }
                }
            }
            i1++;
        }
        if (!$assertionsDisabled && count <= 1)
        {
            throw new AssertionError();
        }
        if (live_nodes != 0) goto _L2; else goto _L1
_L1:
        next = null;
        count = 0;
_L4:
        return true;
_L2:
        if (live_nodes == 1)
        {
            if (!$assertionsDisabled && k != l)
            {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && (k < min || k >= min + count))
            {
                throw new AssertionError();
            }
            Mtrie mtrie = next[k - min];
            if (!$assertionsDisabled && mtrie == null)
            {
                throw new AssertionError();
            }
            next = null;
            next = (new Mtrie[] {
                mtrie
            });
            count = 1;
            min = k;
        } else
        if (k > min || l < -1 + (min + count))
        {
            if (!$assertionsDisabled && 1 + (l - k) <= 1)
            {
                throw new AssertionError();
            }
            Mtrie amtrie[] = next;
            if (!$assertionsDisabled && k <= min && l >= -1 + (min + count))
            {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && k < min)
            {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && l > -1 + (min + count))
            {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && 1 + (l - k) >= count)
            {
                throw new AssertionError();
            }
            count = 1 + (l - k);
            next = new Mtrie[count];
            System.arraycopy(amtrie, k - min, next, 0, count);
            min = k;
        }
        if (true) goto _L4; else goto _L3
_L3:
    }

    private boolean rm_helper(byte abyte0[], int i, Pipe pipe)
    {
        boolean flag = true;
        if (abyte0 != null && abyte0.length != i) goto _L2; else goto _L1
_L1:
        boolean flag1;
        if (pipes != null)
        {
            boolean flag2 = pipes.remove(pipe);
            if (!$assertionsDisabled && !flag2)
            {
                throw new AssertionError();
            }
            if (pipes.isEmpty())
            {
                pipes = null;
            }
        }
        if (pipes != null)
        {
            flag = false;
        }
        flag1 = flag;
_L4:
        return flag1;
_L2:
        byte byte0;
        int j;
        byte0 = abyte0[i];
        j = count;
        flag1 = false;
        if (j == 0) goto _L4; else goto _L3
_L3:
        int k;
        k = min;
        flag1 = false;
        if (byte0 < k) goto _L4; else goto _L5
_L5:
        int l;
        l = min + count;
        flag1 = false;
        if (byte0 >= l) goto _L4; else goto _L6
_L6:
        boolean flag3;
        Mtrie mtrie;
        if (count == flag)
        {
            mtrie = next[0];
        } else
        {
            mtrie = next[byte0 - min];
        }
        flag1 = false;
        if (mtrie == null) goto _L4; else goto _L7
_L7:
        flag3 = mtrie.rm_helper(abyte0, i + 1, pipe);
        if (!mtrie.is_redundant()) goto _L9; else goto _L8
_L8:
        if (!$assertionsDisabled && count <= 0)
        {
            throw new AssertionError();
        }
        if (count != flag) goto _L11; else goto _L10
_L10:
        next = null;
        count = 0;
        live_nodes = -1 + live_nodes;
        if (!$assertionsDisabled && live_nodes != 0)
        {
            throw new AssertionError();
        }
          goto _L9
_L11:
        next[byte0 - min] = null;
        if (!$assertionsDisabled && live_nodes <= flag)
        {
            throw new AssertionError();
        }
        live_nodes = -1 + live_nodes;
        if (live_nodes != flag) goto _L13; else goto _L12
_L12:
        int k1 = 0;
        do
        {
            if (k1 >= count || next[k1] != null)
            {
                if (!$assertionsDisabled && k1 >= count)
                {
                    throw new AssertionError();
                }
                break;
            }
            k1++;
        } while (true);
        min = k1 + min;
        count = ((flag) ? 1 : 0);
        Mtrie mtrie1 = next[k1];
        Mtrie amtrie[] = new Mtrie[flag];
        amtrie[0] = mtrie1;
        next = amtrie;
_L9:
        return flag3;
_L13:
        if (byte0 == min)
        {
            int j1 = 1;
            do
            {
                if (j1 >= count || next[j1] != null)
                {
                    if (!$assertionsDisabled && j1 >= count)
                    {
                        throw new AssertionError();
                    }
                    break;
                }
                j1++;
            } while (true);
            min = j1 + min;
            count = count - j1;
            next = realloc(next, count, flag);
        } else
        if (byte0 == -1 + (min + count))
        {
            int i1 = 1;
            do
            {
                if (i1 >= count || next[(-1 + count) - i1] != null)
                {
                    if (!$assertionsDisabled && i1 >= count)
                    {
                        throw new AssertionError();
                    }
                    break;
                }
                i1++;
            } while (true);
            count = count - i1;
            next = realloc(next, count, false);
        }
        if (true) goto _L9; else goto _L14
_L14:
    }

    public boolean add(byte abyte0[], int i, Pipe pipe)
    {
        return add_helper(abyte0, i, pipe);
    }

    public boolean add(byte abyte0[], Pipe pipe)
    {
        return add_helper(abyte0, 0, pipe);
    }

    public void match(byte abyte0[], int i, IMtrieHandler imtriehandler, Object obj)
    {
        Mtrie mtrie;
        int j;
        mtrie = this;
        j = 0;
_L2:
        if (mtrie.pipes != null)
        {
            for (Iterator iterator = mtrie.pipes.iterator(); iterator.hasNext(); imtriehandler.invoke((Pipe)iterator.next(), null, 0, obj)) { }
        }
        break MISSING_BLOCK_LABEL_59;
        byte byte0;
label0:
        while (byte0 < mtrie.min || byte0 >= mtrie.min + mtrie.count || mtrie.next[byte0 - mtrie.min] == null) 
        {
            do
            {
                do
                {
                    return;
                } while (i == 0 || mtrie.count == 0);
                byte0 = abyte0[j];
                if (mtrie.count != 1)
                {
                    continue label0;
                }
            } while (byte0 != mtrie.min);
            mtrie = mtrie.next[0];
            j++;
            i--;
            continue; /* Loop/switch isn't completed */
        }
        mtrie = mtrie.next[byte0 - mtrie.min];
        j++;
        i--;
        if (true) goto _L2; else goto _L1
_L1:
    }

    public boolean rm(Pipe pipe, IMtrieHandler imtriehandler, Object obj)
    {
        return rm_helper(pipe, new byte[0], 0, 0, imtriehandler, obj);
    }

    public boolean rm(byte abyte0[], int i, Pipe pipe)
    {
        return rm_helper(abyte0, i, pipe);
    }

    static 
    {
        boolean flag;
        if (!zmq/Mtrie.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
