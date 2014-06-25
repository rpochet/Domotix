// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.dao;


// Referenced classes of package eu.pochet.domotix.dao:
//            LagartoResponse

public class timestamp
{

    private String direction;
    private String id;
    private String location;
    private String name;
    final LagartoResponse this$0;
    private String timestamp;
    private String type;
    private String value;

    public String getDirection()
    {
        return direction;
    }

    public String getId()
    {
        return id;
    }

    public String getLocation()
    {
        return location;
    }

    public String getName()
    {
        return name;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public String getType()
    {
        return type;
    }

    public String getValue()
    {
        return value;
    }

    public void setDirection(String s)
    {
        direction = s;
    }

    public void setId(String s)
    {
        id = s;
    }

    public void setLocation(String s)
    {
        location = s;
    }

    public void setName(String s)
    {
        name = s;
    }

    public void setTimestamp(String s)
    {
        timestamp = s;
    }

    public void setType(String s)
    {
        type = s;
    }

    public void setValue(String s)
    {
        value = s;
    }

    public ()
    {
        this$0 = LagartoResponse.this;
        super();
        id = null;
        location = null;
        name = null;
        value = null;
        type = null;
        direction = null;
        timestamp = null;
    }
}
