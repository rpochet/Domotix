// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.service;

import android.os.Parcel;
import android.os.Parcelable;

// Referenced classes of package eu.pochet.domotix.service:
//            Message

public static final class _cls1 extends Enum
    implements Parcelable
{

    public static final android.os.tor CREATOR = new android.os.Parcelable.Creator() {

        public Message.Action createFromParcel(Parcel parcel)
        {
            return Message.Action.values()[parcel.readInt()];
        }

        public volatile Object createFromParcel(Parcel parcel)
        {
            return createFromParcel(parcel);
        }

        public Message.Action[] newArray(int i)
        {
            return new Message.Action[i];
        }

        public volatile Object[] newArray(int i)
        {
            return newArray(i);
        }

    };
    private static final _cls1.newArray ENUM$VALUES[];
    public static final _cls1.newArray STATUS;
    public static final _cls1.newArray SWITCH_OFF_ALL;
    public static final _cls1.newArray TOGGLE;

    public static _cls1 valueOf(String s)
    {
        return (_cls1)Enum.valueOf(eu/pochet/domotix/service/Message$Action, s);
    }

    public static _cls1[] values()
    {
        _cls1 a_lcls1[] = ENUM$VALUES;
        int i = a_lcls1.length;
        _cls1 a_lcls1_1[] = new ENUM.VALUES[i];
        System.arraycopy(a_lcls1, 0, a_lcls1_1, 0, i);
        return a_lcls1_1;
    }

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(ordinal());
    }

    static 
    {
        STATUS = new <init>("STATUS", 0);
        TOGGLE = new <init>("TOGGLE", 1);
        SWITCH_OFF_ALL = new <init>("SWITCH_OFF_ALL", 2);
        ordinal aordinal[] = new <init>[3];
        aordinal[0] = STATUS;
        aordinal[1] = TOGGLE;
        aordinal[2] = SWITCH_OFF_ALL;
        ENUM$VALUES = aordinal;
    }

    private _cls1(String s, int i)
    {
        super(s, i);
    }
}
