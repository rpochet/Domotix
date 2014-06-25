package eu.pochet.domotix.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.JsonReader;
import eu.pochet.domotix.service.DownloadFilesTask;
import eu.pochet.domotix.service.DownloadableFile;

public class ScenarioDao 
{
	private static final String SCENARIOS_FILE_NAME = "scenarios.json";
	
	private static List<Scenario> scenarios = null; 
	
	public static List<Scenario> getScenarios(Context ctx) 
	{		
		if(scenarios == null) 
		{
			scenarios = new ArrayList<Scenario>();
			try 
			{
				JsonReader reader = new JsonReader(new InputStreamReader(ctx.openFileInput(SCENARIOS_FILE_NAME)));
				reader.setLenient(true);
				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();
		     		if (name.equals("scenarios")) 
		     		{
		     			reader.beginArray();
						while (reader.hasNext()) 
						{
							scenarios.add(readScenario(ctx, reader));
						}
		     			reader.endArray();
		     		} 
		     		else 
		     		{
		     			reader.skipValue();
		     		}
				}
				reader.endObject();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	    return scenarios;
	}
	
	public static void reset() 
	{
		scenarios = null;
	}

    private static Scenario readScenario(Context ctx, JsonReader reader) throws IOException 
    {
    	Scenario scenario = new Scenario();
     	reader.beginObject();
     	while (reader.hasNext()) {
     		String name = reader.nextName();
     		if (name.equals("id")) {
     			scenario.setId(reader.nextInt());
     		} else if (name.equals("name")) {
     			scenario.setName(reader.nextString());
     		} else if (name.equals("level")) {
     			scenario.setLevel(reader.nextInt());
     		} else if (name.equals("message")) {
     			scenario.setMessage(reader.nextString());
     		} else {
     			reader.skipValue();
     		}
     	}
     	reader.endObject();
     	return scenario;
	}
	
	public static boolean updateFiles(String server, Context ctx, final Runnable postExecute) 
	{
		int j = 0;
		DownloadableFile[] downloadableFiles = new DownloadableFile[1];
		try 
		{
			downloadableFiles[j++] = new DownloadableFile(
			    new URI(server + "domotix/" + SCENARIOS_FILE_NAME),
			    SCENARIOS_FILE_NAME
			);
			new DownloadFilesTask(ctx) 
			{		
				@Override
				protected void onPostExecute(Long result)
				{
				    super.onPostExecute(result);
			    	ScenarioDao.reset();
			    	postExecute.run();
				}
			}.execute(downloadableFiles);
		} 
		catch (URISyntaxException e) 
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
