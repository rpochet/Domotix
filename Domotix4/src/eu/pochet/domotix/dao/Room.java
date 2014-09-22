package eu.pochet.domotix.dao;

import java.util.List;

public class Room 
{
	private int id;
	
	private String name;
	
	private String path;
	
	private List<Light> lights;
	
	private Level level;
	
	private int x;
	
	private int y;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Light> getLights() {
		return lights;
	}

	public void setLights(List<Light> lights) {
		this.lights = lights;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public String toString()
	{
	    return new StringBuilder().append(this.name)
	    	.append(" (")
	    	.append(this.getLevel().getName())
	    	.append(")")
	    .toString();
	}

}
