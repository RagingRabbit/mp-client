package com.rb.mud.graphics;

public class Animation {
	private Sprite	spr;
	
	private int		x, y;
	private int		dx, dy;
	private int		len;
	private float	duration;
	private boolean	loop;
	
	private long	last;
	private float	timer;
	
	
	public Animation(Sprite sprite, int startX, int startY, int deltaX, int deltaY, int length, int fps, boolean loopFrames) {
		spr = sprite;
		x = startX;
		y = startY;
		dx = deltaX;
		dy = deltaY;
		len = length;
		duration = (float) len / fps;
		loop = loopFrames;
		
		last = System.nanoTime();
	}
	
	public void update() {
		long now = System.nanoTime();
		float delta = (now - last) / 1e9f;
		last = now;
		
		timer += delta;
		if (timer > duration * 2) {
			timer -= duration;
		}
		int frameIndex = (int) (timer / duration * len);
		frameIndex = loop ? (frameIndex % len) : (Math.min(frameIndex, len - 1));
		
		spr.setRectPosition(x + frameIndex * dx, y + frameIndex * dy);
	}
}
