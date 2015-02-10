package eu.pochet.domotix.dao;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class DomotixDaoTestCase {

	@Test
	public void testLevel2() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		Gson gson = new GsonBuilder().create();
		ViewResult<String, Level, Level> o = gson.fromJson(new FileReader("test/levels.js"), new TypeToken<ViewResult<String, Level, Level>>() {}.getType());
		System.out.println(o.getRows());
	}

	@Test
	public void testLevel() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		Gson gson = new GsonBuilder().create();
		Level l = gson.fromJson(new FileReader("test/levels_2.js"), new TypeToken<Level>() {}.getType());
		System.out.println(l);
	}

}
