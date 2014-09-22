package eu.pochet.domotix.dao;

import java.util.ArrayList;
import java.util.List;

public class Level {
	
	private int id;

	private int level;

	private String name;

	private String path;

	private List<Room> rooms = new ArrayList<Room>();

	private List<SwapDevice> swapDevices = new ArrayList<SwapDevice>();

	private List<Light> lights = new ArrayList<Light>();

	private int x;

	private int y;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
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

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public void addRoom(Room room) {
		getRooms().add(room);
	}

	public List<Light> getLights() {
		return lights;
	}

	public void setLights(List<Light> lights) {
		this.lights = lights;
	}

	public void addLight(Light light) {
		getLights().add(light);
	}

	public List<SwapDevice> getSwapDevices() {
		return swapDevices;
	}

	public void setSwapDevices(List<SwapDevice> swapDevices) {
		this.swapDevices = swapDevices;
	}

	public void addSwapDevice(SwapDevice swapDevice) {
		getSwapDevices().add(swapDevice);
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
	public String toString() {
		return new StringBuilder().append(this.name).toString();
	}

}
