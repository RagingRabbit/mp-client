package com.rb.mud;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Random;

import com.rb.mud.entity.Entity;
import com.rb.mud.graphics.Animation;
import com.rb.mud.graphics.Sprite;
import com.rb.shared.packets.PlayerDataPacket;
import com.rb.shared.packets.SnapshotPacket.EntityData;

public class Player extends Entity {
	private static final Color NAMETAG_COLOR = new Color(51, 51, 51);

	private static final float BULLET_SPEED = 200.0f;

	private String username;
	private int color;
	private boolean local;

	private Screen screen;

	private Animation idle;
	private Animation run;
	private Sprite heartSprite;

	private boolean mouseDown;

	private boolean left, right, down, up;
	private boolean direction;

	public boolean running;
	public float speedX, speedY;

	private int health;

	public Player(String username, int id, boolean local, float xpos, float ypos) {
		super(id, xpos, ypos);
		this.username = username;
		this.local = local;
		this.color = new Random(this.getId()).nextInt(0xffffff);

		this.sprite = new Sprite(ResourceRepo.getTexture("/player.png"), 0, 0, 16, 16);

		idle = new Animation(sprite, 0, 0, 16, 0, 4, 8, true);
		run = new Animation(sprite, 0, 16, 16, 0, 4, 8, true);

		if (local) {
			initLocal();
		}
	}

	private void initLocal() {
		this.heartSprite = new Sprite(ResourceRepo.getTexture("/heart.png"), 0, 0, 8, 8);
	}

	public void onKeyPress(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
			left = true;
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
			right = true;
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:
			down = true;
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
			up = true;
			break;
		}
	}

	public void onKeyRelease(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
			left = false;
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
			right = false;
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:
			down = false;
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
			up = false;
			break;
		}
	}

	public void onMousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			mouseDown = true;
		}
	}

	public void onMouseRelease(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			mouseDown = false;
		}
	}

	public void update(float dt) {
		if (health <= 0) {
			return;
		}

		float beforeX = tmpX;
		float beforeY = tmpY;
		if (!local) {
			super.updateInterpolatedPosition(dt);
		} else {
			int dir = 0;
			if (left) {
				x -= dt * 100;
				dir--;
			}
			if (right) {
				x += dt * 100;
				dir++;
			}
			if (down) {
				y += dt * 100;
			}
			if (up) {
				y -= dt * 100;
			}
			if (dir > 0) {
				direction = true;
			} else if (dir < 0) {
				direction = false;
			}

			// tmpX = lerp(tmpX, x, 100.0f * dt);
			// tmpY = lerp(tmpY, y, 100.0f * dt);
			tmpX = x;
			tmpY = y;
		}
		speedX = (tmpX - beforeX) / dt;
		speedY = (tmpY - beforeY) / dt;

		if (mouseDown) {
			int dx = screen.getMouseX() - screen.getScreenWidth() / 2;
			int dy = screen.getMouseY() - screen.getScreenHeight() / 2;
			float l = (float) Math.sqrt(dx * dx + dy * dy);
			float vecX = dx / l * BULLET_SPEED;
			float vecY = dy / l * BULLET_SPEED;
			vecX += speedX;
			vecY += speedY;

			float angle = (float) Math.atan2(vecY, vecX);
			float speed = (float) Math.sqrt(vecX * vecX + vecY * vecY);
			Game.instance.shootBullet(angle, speed, this);
			mouseDown = false;
		}
	}

	public void render(Screen screen) {
		this.screen = screen;
		if (local) {
			running = left || right || up || down;
		}
		if (running) {
			run.update();
		} else {
			idle.update();
		}

		screen.renderEntity(tmpX - sprite.getWidth() / 2, tmpY - sprite.getHeight(), sprite, !direction, false);
		screen.renderString(tmpX - screen.getStringWidth(username) / 2, tmpY - 20, username, NAMETAG_COLOR);

		if (local) {
			for (int i = 0; i < health; i++) {
				screen.renderUIImage(screen.getScreenWidth() / 2 + (int) ((i * 1.0f) * heartSprite.getWidth() - 2.0f * 1.0f * heartSprite.getWidth()), screen.getScreenHeight() - heartSprite.getHeight() - 30, heartSprite);
			}
			if (health <= 0) {
				screen.renderUIString(screen.getScreenWidth() / 2 - screen.getStringWidth("ur ded") / 2, screen.getScreenHeight() / 2, "ur ded", new Color(0x770000));
			}
		}
	}

	public void setSnapshot(EntityData data) {
		if (!local) {
			super.setSnapshot(data);
			direction = data.getBoolean("direction");
			running = data.getBoolean("running");
		}
		health = data.getInt("health");
	}

	public void getDataPacket(PlayerDataPacket data) {
		data.x = x;
		data.y = y;
		data.direction = direction;
		data.running = running;
	}

	public String getUsername() {
		return username;
	}

	public int getColor() {
		return color;
	}

	public boolean isLocal() {
		return local;
	}
}
