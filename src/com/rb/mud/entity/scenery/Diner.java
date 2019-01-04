package com.rb.mud.entity.scenery;

import com.rb.mud.ResourceRepo;
import com.rb.mud.Screen;
import com.rb.mud.graphics.Sprite;

public class Diner extends Scenery {

	public Diner(int id, float x, float y) {
		super(id, x, y);

		this.sprite = new Sprite(ResourceRepo.getTexture("/diner.png"), 0, 0, 128, 64);
	}

	@Override
	public void render(Screen screen) {
		screen.renderEntity(x, y - sprite.getHeight(), sprite, false, false);
	}

}
