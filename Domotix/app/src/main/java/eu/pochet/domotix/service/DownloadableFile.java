package eu.pochet.domotix.service;

import java.net.URI;

public class DownloadableFile 
{
	public URI uri = null;
	
	public String file = null;
	
	public DownloadableFile(URI uri, String file) 
	{
		this.uri = uri;
		this.file = file;
	}
}
