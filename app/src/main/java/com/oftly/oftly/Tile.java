package com.oftly.oftly;

public class Tile {
	int loc_x, loc_y;
	int width, height;

	public Tile(int loc_x, int loc_y, int width, int height) {
		setLocation(loc_x, loc_y);
		setDimension(width, height);
	}
	public void setLocation(int loc_x, int loc_y) {
		this.loc_x = loc_x;
		this.loc_y = loc_y;
	}
	public void setDimension(int width, int height) {
		this.width = width;
		this.height = height;
	}
	public int getX() {
		return this.loc_x;
	}
	public int getY() {
		return this.loc_y;
	}
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}
}
