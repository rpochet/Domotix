// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.android;


public class Util
{

    public Util()
    {
    }

    public static String ByteArrayToHexString(byte abyte0[])
    {
        int i = abyte0.length;
        String s = new String();
        int j = 0;
        do
        {
            if (j >= i)
            {
                return s;
            }
            s = (new StringBuilder(String.valueOf((new StringBuilder(String.valueOf(s))).append(Integer.toHexString(0xf & abyte0[j] >> 4)).toString()))).append(Integer.toHexString(0xf & abyte0[j])).toString();
            j++;
        } while (true);
    }

    public static byte[] HexStringToByteArray(String s)
    {
        int i = s.length();
        byte abyte0[] = new byte[i / 2];
        int j = 0;
        do
        {
            if (j >= i)
            {
                return abyte0;
            }
            abyte0[j / 2] = (byte)((Character.digit(s.charAt(j), 16) << 4) + Character.digit(s.charAt(j + 1), 16));
            j += 2;
        } while (true);
    }
}
