package com.rb.mud;

import com.rb.mud.tile.Tile;

public class WorldData {

	public int x0, y0;
	public int width, height;
	public byte[] data;

	public void render(Screen screen) {
		for (int y = y0; y < y0 + height; y++) {
			for (int x = x0; x < x0 + width; x++) {
				//screen.renderTile(x * 16, y * 16, data[(x - x0) + (y - y0) * width] == 1 ? 0xFFFFFF : 0x000000);
				screen.renderTile(x * 16, y * 16, Tile.getTile(data[(x - x0) + (y - y0) * width]).sprite);
			}
		}
	}

}
