// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.dao;

import java.util.List;

// Referenced classes of package eu.pochet.domotix.dao:
//            Level

public class Room
{

    private int id;
    private Level level;
    private List lights;
    private String name;
    private String path;
    private int x;
    private int y;

    public Room()
    {
    }

    public int getId()
    {
        return id;
    }

    public Level getLevel()
    {
        return level;
    }

    public List getLights()
    {
        return lights;
    }

    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        return path;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public void setId(int i)
    {
        id = i;
    }

    public void setLevel(Level level1)
    {
        level = level1;
    }

    public void setLights(List list)
    {
        lights = list;
    }

    public void setName(String s)
    {
        name = s;
    }

    public void setPath(String s)
    {
        path = s;
    }

    public void setX(int i)
    {
        x = i;
    }

    public void setY(int i)
    {
        y = i;
    }

    public String toString()
    {
        return (new StringBuilder()).append(name).append(getLevel().getName()).toString();
    }
}
