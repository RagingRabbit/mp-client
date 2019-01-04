package com.rb.mud;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import com.rb.mud.graphics.Sprite;

public class Screen extends Canvas {
	private static final long serialVersionUID = 1L;

	private static final int CLEAR_COLOR = 0x00ffff;

	private Graphics graphics;
	private BufferStrategy strategy;

	private BufferedImage image;
	private int[] pixels;
	private int width, height;
	private float scrollX, scrollY;

	private BufferedImage uiImage;
	private int[] uiPixels;
	private int uiWidth, uiHeight;

	public Screen(int width, int height) {
		this.width = width;
		this.height = height;
		this.uiWidth = width;
		this.uiHeight = height;

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		uiImage = new BufferedImage(uiWidth, uiHeight, BufferedImage.TYPE_INT_ARGB);
		uiPixels = ((DataBufferInt) uiImage.getRaster().getDataBuffer()).getData();
	}

	public void renderTile(int xpos, int ypos, int color) {
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				int xx = (int) (xpos + x - scrollX);
				int yy = (int) (ypos + y - scrollY);

				if (xx >= 0 && xx < width && yy >= 0 && yy < height) {
					pixels[xx + yy * width] = color;
				}
			}
		}
	}

	public void renderTile(int xpos, int ypos, Sprite sprite) {
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				int xx = (int) (xpos + x - scrollX);
				int yy = (int) (ypos + y - scrollY);

				if (xx >= 0 && xx < width && yy >= 0 && yy < height) {
					pixels[xx + yy * width] = sprite.getPixel(x, y);
				}
			}
		}
	}

	public void renderEntity(float xpos, float ypos, Sprite sprite, boolean flipX, boolean flipY) {
		for (int y = 0; y < sprite.getHeight(); y++) {
			for (int x = 0; x < sprite.getWidth(); x++) {
				int xx = (int) (xpos + x - scrollX);
				int yy = (int) (ypos + y - scrollY);

				int pixelX = flipX ? sprite.getWidth() - x - 1 : x;
				int pixelY = flipY ? sprite.getHeight() - y - 1 : y;

				if (xx >= 0 && xx < width && yy >= 0 && yy < height && (sprite.getPixel(pixelX, pixelY) & 0xff000000) == 0xff000000) {
					pixels[xx + yy * width] = sprite.getPixel(pixelX, pixelY);
				}
			}
		}
	}

	public void renderString(float x, float y, String str, Color color) {
		graphics.setColor(color);
		graphics.drawString(str, (int) ((x - scrollX) * ClientMain.SCALE), (int) ((y - scrollY) * ClientMain.SCALE));
	}

	public void renderUIImage(int xpos, int ypos, Sprite sprite) {
		for (int y = 0; y < sprite.getHeight(); y++) {
			for (int x = 0; x < sprite.getWidth(); x++) {
				int xx = xpos + x;
				int yy = ypos + y;
				if (xx >= 0 && xx < uiWidth && yy >= 0 && yy < uiHeight && (sprite.getPixel(x, y) & 0xff000000) == 0xff000000) {
					uiPixels[xx + yy * width] = sprite.getPixel(x, y);
				}
			}
		}
	}

	public void renderUIString(int xpos, int ypos, String str, Color color) {
		graphics.setColor(color);
		graphics.drawString(str, (int) (xpos * ClientMain.SCALE), (int) (ypos * ClientMain.SCALE));
	}

	public void setFont(Font font) {
		graphics.setFont(font);
	}

	public void setScroll(float x, float y) {
		scrollX = x;
		scrollY = y;
	}

	public void clear() {
		strategy = getBufferStrategy();
		if (strategy == null) {
			createBufferStrategy(3);
			strategy = getBufferStrategy();
		}

		pixels[0] = 0xff00ff;
		graphics = strategy.getDrawGraphics();
		graphics.drawImage(image, 0, 0, getParent().getSize().width, getParent().getSize().height, null);
		graphics.drawImage(uiImage, 0, 0, getParent().getSize().width, getParent().getSize().height, null);

		Arrays.fill(pixels, CLEAR_COLOR);
		Arrays.fill(uiPixels, CLEAR_COLOR);
	}

	public void render() {
		graphics.dispose();
		strategy.show();
	}

	public int getStringWidth(String str) {
		return graphics.getFontMetrics().stringWidth(str) / ClientMain.SCALE;
	}

	public int getScreenWidth() {
		return width;
	}

	public int getScreenHeight() {
		return height;
	}

	public int getMouseX() {
		return super.getMousePosition().x / ClientMain.SCALE;
	}

	public int getMouseY() {
		return super.getMousePosition().y / ClientMain.SCALE;
	}
}
