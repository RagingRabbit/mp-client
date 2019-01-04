package com.rb.mud.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Texture {
	private int width, height;
	private int[] pixels;

	public Texture(String filepath) {
		try {
			BufferedImage image = ImageIO.read(Texture.class.getResourceAsStream(filepath));
			width = image.getWidth();
			height = image.getHeight();
			pixels = image.getRGB(0, 0, width, height, null, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getPixel(int x, int y) {
		return pixels[x + y * width];
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
