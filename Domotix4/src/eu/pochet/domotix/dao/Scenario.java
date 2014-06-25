// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.dao;


public class Scenario
{

    private int id;
    private int level;
    private String message;
    private String name;

    public Scenario()
    {
    }

    public int getId()
    {
        return id;
    }

    public int getLevel()
    {
        return level;
    }

    public String getMessage()
    {
        return message;
    }

    public String getName()
    {
        return name;
    }

    public void setId(int i)
    {
        id = i;
    }

    public void setLevel(int i)
    {
        level = i;
    }

    public void setMessage(String s)
    {
        message = s;
    }

    public void setName(String s)
    {
        name = s;
    }
}
