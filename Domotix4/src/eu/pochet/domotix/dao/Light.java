// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.dao;

import java.io.Serializable;

// Referenced classes of package eu.pochet.domotix.dao:
//            Room

public class Light
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    private String cardAddress;
    private int id;
    private String name;
    private String outputNb;
    private Room room;
    private String status;
    private String type;
    private int x;
    private int y;
    private int z;

    public Light()
    {
    }

    public String getCardAddress()
    {
        return cardAddress;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getOutputNb()
    {
        return outputNb;
    }

    public Room getRoom()
    {
        return room;
    }

    public String getStatus()
    {
        return status;
    }

    public String getType()
    {
        return type;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public void setCardAddress(String s)
    {
        cardAddress = s;
    }

    public void setId(int i)
    {
        id = i;
    }

    public void setName(String s)
    {
        name = s;
    }

    public void setOutputNb(String s)
    {
        outputNb = s;
    }

    public void setRoom(Room room1)
    {
        room = room1;
    }

    public void setStatus(String s)
    {
        status = s;
    }

    public void setType(String s)
    {
        type = s;
    }

    public void setX(int i)
    {
        x = i;
    }

    public void setY(int i)
    {
        y = i;
    }

    public void setZ(int i)
    {
        z = i;
    }

    public String toString()
    {
        return (new StringBuilder()).append(name).append(", Status: ").append(status).append("\nRoom: ").append(getRoom().getName()).toString();
    }
}
