package com.rb.mud.graphics;

public class Sprite {
	private Texture texture;
	private int x, y;
	private int w, h;

	public Sprite(Texture texture, int x0, int y0, int width, int height) {
		this.texture = texture;
		this.x = x0;
		this.y = y0;
		this.w = width;
		this.h = height;
	}

	public void setRectPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getPixel(int x, int y) {
		return texture.getPixel(this.x + x, this.y + y);
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}
}
