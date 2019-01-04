package com.rb.mud.tile;

import java.util.HashMap;
import java.util.Map;

import com.rb.mud.graphics.Sprite;
import com.rb.mud.graphics.Texture;

public class Tile {

	private static final Texture SPRITE_TEXTURE;

	static {
		SPRITE_TEXTURE = new Texture("/tiles.png");
	}

	private static final Map<Byte, Tile> tiles = new HashMap<Byte, Tile>();

	public static final Tile voidT = new Tile(0, 10, 0);
	public static final Tile grass = new Tile(20, 0, 0);
	public static final Tile stone = new Tile(30, 1, 0);

	public final byte id;
	public final Sprite sprite;

	public Tile(int id, int x, int y) {
		this.id = (byte) id;
		this.sprite = new Sprite(SPRITE_TEXTURE, x * 16, y * 16, 16, 16);
		tiles.put(this.id, this);
	}

	public static Tile getTile(byte id) {
		return tiles.get(id);
	}

}
