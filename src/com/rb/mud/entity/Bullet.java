package com.rb.mud.entity;

import com.rb.mud.ResourceRepo;
import com.rb.mud.Screen;
import com.rb.mud.graphics.Sprite;
import com.rb.shared.packets.SnapshotPacket.EntityData;

public class Bullet extends Entity {

	private float dx, dy;

	public Bullet(int id, float x, float y, float dx, float dy) {
		super(id, x, y);

		this.dx = dx;
		this.dy = dy;
		this.sprite = new Sprite(ResourceRepo.getTexture("/snowball.png"), 0, 0, 6, 6);
	}

	public void update(float delta) {
		x += dx * delta;
		y += dy * delta;
		super.updateInterpolatedPosition(delta);
	}

	public void render(Screen screen) {
		screen.renderEntity(tmpX - sprite.getWidth() / 2, tmpY - sprite.getHeight() / 2, sprite, false, false);
	}

	@Override
	public void setSnapshot(EntityData data) {
		super.setSnapshot(data);
		//x = data.getFloat("x");
		//y = data.getFloat("y");
	}

}
