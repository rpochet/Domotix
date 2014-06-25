// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.service;

import java.net.URI;

public class DownloadableFile
{

    public String file;
    public URI uri;

    public DownloadableFile(URI uri1, String s)
    {
        uri = null;
        file = null;
        uri = uri1;
        file = s;
    }
}
