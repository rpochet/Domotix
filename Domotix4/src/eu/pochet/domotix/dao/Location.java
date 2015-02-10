package eu.pochet.domotix.dao;

public class Location {

	public int room_id = 0;
	
	private Room room = null;
	
	private int x;

	private int dx;
	
	private int y;

	private int dy;
	
	private int z;
	
	private int dz;

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	public int getRoom_id() {
		return room_id;
	}
	
	public void setRoom_id(int room_id) {
		this.room_id = room_id;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getDx() {
		return dx;
	}

	public void setDx(int dx) {
		this.dx = dx;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getDy() {
		return dy;
	}

	public void setDy(int dy) {
		this.dy = dy;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getDz() {
		return dz;
	}

	public void setDz(int dz) {
		this.dz = dz;
	}

	public float getAbsoluteX() {
		return this.getRoom().getX() + this.getX() + this.getDx();
	}

	public float getAbsoluteY() {
		return this.getRoom().getY() + this.getY() + this.getDy();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Room: ").append(room != null ? room : room_id);
		sb.append("(");
		sb.append(x);
		if(dx != 0) {
			sb.append(" +/- ");
			sb.append(dx);
		}
		sb.append(", ");
		sb.append(y);
		if(dy != 0) {
			sb.append(" +/- ");
			sb.append(dy);
		}
		sb.append(", ");
		sb.append(z);
		if(dz != 0) {
			sb.append(" +/- ");
			sb.append(dz);
		}
		sb.append(")");
		return sb.toString();
	}
}
