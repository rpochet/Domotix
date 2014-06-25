// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package zmq;


// Referenced classes of package zmq:
//            Utils

public class Trie
{
    public static interface ITrieHandler
    {

        public abstract void added(byte abyte0[], int i, Object obj);
    }


    static final boolean $assertionsDisabled;
    private int count;
    private int live_nodes;
    private byte min;
    Trie next[];
    private int refcnt;

    public Trie()
    {
        min = 0;
        count = 0;
        live_nodes = 0;
        refcnt = 0;
        next = null;
    }

    private void apply_helper(byte abyte0[], int i, int j, ITrieHandler itriehandler, Object obj)
    {
        if (refcnt > 0)
        {
            itriehandler.added(abyte0, i, obj);
        }
        if (i >= j)
        {
            j = i + 256;
            abyte0 = Utils.realloc(abyte0, j);
            if (!$assertionsDisabled && abyte0 == null)
            {
                throw new AssertionError();
            }
        }
        if (count != 0)
        {
            if (count == 1)
            {
                abyte0[i] = min;
                int i1 = i + 1;
                next[0].apply_helper(abyte0, i1, j, itriehandler, obj);
                return;
            }
            int k = 0;
            while (k != count) 
            {
                abyte0[i] = (byte)(k + min);
                if (next[k] != null)
                {
                    Trie trie = next[k];
                    int l = i + 1;
                    trie.apply_helper(abyte0, l, j, itriehandler, obj);
                }
                k++;
            }
        }
    }

    private boolean is_redundant()
    {
        return refcnt == 0 && live_nodes == 0;
    }

    private Trie[] realloc(Trie atrie[], int i, boolean flag)
    {
        return (Trie[])Utils.realloc(zmq/Trie, atrie, i, flag);
    }

    public boolean add(byte abyte0[])
    {
        return add(abyte0, 0);
    }

    public boolean add(byte abyte0[], int i)
    {
        if (abyte0 == null || abyte0.length == i)
        {
            refcnt = 1 + refcnt;
            return refcnt == 1;
        }
        byte byte0 = abyte0[i];
        if (byte0 < min || byte0 >= min + count)
        {
            if (count == 0)
            {
                min = byte0;
                count = 1;
                next = null;
            } else
            if (count == 1)
            {
                byte byte1 = min;
                Trie trie = next[0];
                int j;
                if (min < byte0)
                {
                    j = byte0 - min;
                } else
                {
                    j = min - byte0;
                }
                count = j + 1;
                next = new Trie[count];
                min = (byte)Math.min(min, byte0);
                next[byte1 - min] = trie;
            } else
            if (min < byte0)
            {
                count = 1 + (byte0 - min);
                next = realloc(next, count, true);
            } else
            {
                count = (min + count) - byte0;
                next = realloc(next, count, false);
                min = byte0;
            }
        }
        if (count == 1)
        {
            if (next == null)
            {
                next = new Trie[1];
                next[0] = new Trie();
                live_nodes = 1 + live_nodes;
            }
            return next[0].add(abyte0, i + 1);
        }
        if (next[byte0 - min] == null)
        {
            next[byte0 - min] = new Trie();
            live_nodes = 1 + live_nodes;
        }
        return next[byte0 - min].add(abyte0, i + 1);
    }

    public void apply(ITrieHandler itriehandler, Object obj)
    {
        apply_helper(null, 0, 0, itriehandler, obj);
    }

    public boolean check(byte abyte0[])
    {
        Trie trie;
        int i;
        trie = this;
        i = 0;
_L2:
        byte byte0;
        if (trie.refcnt > 0)
        {
            return true;
        }
        if (abyte0.length == i)
        {
            return false;
        }
        byte0 = abyte0[i];
        if (byte0 < trie.min || byte0 >= trie.min + trie.count)
        {
            return false;
        }
        if (trie.count != 1)
        {
            break; /* Loop/switch isn't completed */
        }
        trie = trie.next[0];
_L3:
        i++;
        if (true) goto _L2; else goto _L1
_L1:
        trie = trie.next[byte0 - trie.min];
        if (trie == null)
        {
            return false;
        }
          goto _L3
        if (true) goto _L2; else goto _L4
_L4:
    }

    public boolean rm(byte abyte0[], int i)
    {
        boolean flag = true;
        if (abyte0 != null && abyte0.length != i) goto _L2; else goto _L1
_L1:
        if (refcnt != 0) goto _L4; else goto _L3
_L3:
        return false;
_L4:
        refcnt = -1 + refcnt;
        if (refcnt != 0)
        {
            flag = false;
        }
        return flag;
_L2:
        byte byte0 = abyte0[i];
        if (count == 0 || byte0 < min || byte0 >= min + count) goto _L3; else goto _L5
_L5:
        Trie trie;
        boolean flag1;
        if (count == flag)
        {
            trie = next[0];
        } else
        {
            trie = next[byte0 - min];
        }
        if (trie == null) goto _L3; else goto _L6
_L6:
        flag1 = trie.rm(abyte0, i + 1);
        if (!trie.is_redundant()) goto _L8; else goto _L7
_L7:
        if (!$assertionsDisabled && count <= 0)
        {
            throw new AssertionError();
        }
        if (count != flag) goto _L10; else goto _L9
_L9:
        next = null;
        count = 0;
        live_nodes = -1 + live_nodes;
        if (!$assertionsDisabled && live_nodes != 0)
        {
            throw new AssertionError();
        }
          goto _L8
_L10:
        next[byte0 - min] = null;
        if (!$assertionsDisabled && live_nodes <= flag)
        {
            throw new AssertionError();
        }
        live_nodes = -1 + live_nodes;
        if (live_nodes != flag) goto _L12; else goto _L11
_L11:
        int i1 = 0;
        Trie trie1;
label0:
        do
        {
label1:
            {
                int j1 = count;
                trie1 = null;
                if (i1 < j1)
                {
                    if (next[i1] == null)
                    {
                        break label1;
                    }
                    trie1 = next[i1];
                    min = (byte)(i1 + min);
                }
                if (!$assertionsDisabled && trie1 == null)
                {
                    throw new AssertionError();
                }
                break label0;
            }
            i1++;
        } while (true);
        next = null;
        Trie atrie[] = new Trie[flag];
        atrie[0] = trie1;
        next = atrie;
        count = ((flag) ? 1 : 0);
_L8:
        return flag1;
_L12:
label2:
        {
            if (byte0 != min)
            {
                break label2;
            }
            byte byte1 = min;
            int l = 1;
label3:
            do
            {
label4:
                {
                    if (l < count)
                    {
                        if (next[l] == null)
                        {
                            break label4;
                        }
                        byte1 = (byte)(l + min);
                    }
                    if (!$assertionsDisabled && byte1 == min)
                    {
                        throw new AssertionError();
                    }
                    break label3;
                }
                l++;
            } while (true);
            if (!$assertionsDisabled && byte1 <= min)
            {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && count <= byte1 - min)
            {
                throw new AssertionError();
            }
            count = count - (byte1 - min);
            next = realloc(next, count, flag);
            min = byte1;
            continue; /* Loop/switch isn't completed */
        }
        if (byte0 != -1 + (min + count))
        {
            continue; /* Loop/switch isn't completed */
        }
        int j = count;
        int k = 1;
label5:
        do
        {
label6:
            {
                if (k < count)
                {
                    if (next[(-1 + count) - k] == null)
                    {
                        break label6;
                    }
                    j = count - k;
                }
                if (!$assertionsDisabled && j == count)
                {
                    throw new AssertionError();
                }
                break label5;
            }
            k++;
        } while (true);
        count = j;
        next = realloc(next, count, false);
        if (true) goto _L8; else goto _L13
_L13:
    }

    static 
    {
        boolean flag;
        if (!zmq/Trie.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }
}
