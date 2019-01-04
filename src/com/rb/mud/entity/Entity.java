package com.rb.mud.entity;

import com.rb.mud.Screen;
import com.rb.mud.graphics.Sprite;
import com.rb.shared.packets.SnapshotPacket.EntityData;

public class Entity {
	private final int id;

	public float x, y;
	public float tmpX, tmpY;

	protected Sprite sprite;

	public Entity(int id, float x, float y) {
		this.id = id;
		this.x = this.tmpX = x;
		this.y = this.tmpY = y;
	}

	public void update(float delta) {
	}

	protected void updateInterpolatedPosition(float delta) {
		tmpX = lerp(tmpX, x, 10.0f * delta);
		tmpY = lerp(tmpY, y, 10.0f * delta);
	}

	private float lerp(float f0, float f1, float blend) {
		return f0 + (f1 - f0) * blend;
	}

	public void setSnapshot(EntityData data) {
		x = data.getFloat("x");
		y = data.getFloat("y");
	}

	public void render(Screen screen) {
	}

	public void onRemoved() {
	}

	public int getId() {
		return id;
	}
}
