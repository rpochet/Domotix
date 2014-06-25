// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.dao;

import java.util.ArrayList;
import java.util.List;

// Referenced classes of package eu.pochet.domotix.dao:
//            Card, Light, Room

public class Level
{

    private List cards;
    private int id;
    private int level;
    private List lights;
    private String name;
    private String path;
    private List rooms;
    private int x;
    private int y;

    public Level()
    {
        rooms = new ArrayList();
        cards = new ArrayList();
        lights = new ArrayList();
    }

    public void addCard(Card card)
    {
        getCards().add(card);
    }

    public void addLight(Light light)
    {
        getLights().add(light);
    }

    public void addRoom(Room room)
    {
        getRooms().add(room);
    }

    public List getCards()
    {
        return cards;
    }

    public int getId()
    {
        return id;
    }

    public int getLevel()
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

    public List getRooms()
    {
        return rooms;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public void setCards(List list)
    {
        cards = list;
    }

    public void setId(int i)
    {
        id = i;
    }

    public void setLevel(int i)
    {
        level = i;
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

    public void setRooms(List list)
    {
        rooms = list;
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
        return (new StringBuilder()).append(name).toString();
    }
}
