// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LagartoResponse
{
    public class Status
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

        public Status()
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


    private String httpserver;
    private String procname;
    private List status;

    public LagartoResponse()
    {
        procname = null;
        httpserver = null;
        status = new ArrayList();
    }

    public String getHttpserver()
    {
        return httpserver;
    }

    public String getProcname()
    {
        return procname;
    }

    public Status getStatus(String s)
    {
        Iterator iterator = getStatus().iterator();
        Status status1;
        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }
            status1 = (Status)iterator.next();
        } while (!status1.getId().equals(s));
        return status1;
    }

    public List getStatus()
    {
        return status;
    }

    public void setHttpserver(String s)
    {
        httpserver = s;
    }

    public void setProcname(String s)
    {
        procname = s;
    }

    public void setStatus(List list)
    {
        status = list;
    }
}
