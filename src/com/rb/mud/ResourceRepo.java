package com.rb.mud;

import java.util.HashMap;
import java.util.Map;

import com.rb.mud.graphics.Texture;

public class ResourceRepo {
	private static Map<String, Texture> textures;
	
	static {
		textures = new HashMap<String, Texture>();
	}
	
	
	public static Texture getTexture(String filepath) {
		if (!textures.containsKey(filepath)) {
			Texture texture = new Texture(filepath);
			textures.put(filepath, texture);
		}
		return textures.get(filepath);
	}
}
