package com.rb.mud;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.esotericsoftware.kryonet.Connection;
import com.rb.mud.entity.Bullet;
import com.rb.mud.entity.Entity;
import com.rb.mud.entity.scenery.Diner;
import com.rb.mud.net.NetworkManager;
import com.rb.mud.ui.Chat;
import com.rb.mud.ui.Chat.ChatListener;
import com.rb.shared.packets.ChatMessagePacket;
import com.rb.shared.packets.EntityRemovePacket;
import com.rb.shared.packets.LevelDataPacket;
import com.rb.shared.packets.LevelSwitchPacket;
import com.rb.shared.packets.PlayerDataPacket;
import com.rb.shared.packets.PlayerSpawnPacket;
import com.rb.shared.packets.ReceivedPacket;
import com.rb.shared.packets.ShootPacket;
import com.rb.shared.packets.SnapshotPacket;
import com.rb.shared.packets.SnapshotPacket.EntityData;

public class Game {
	public static Game instance;

	private JFrame frame;
	private Screen screen;

	private WorldData world;
	private Map<Integer, Player> players;
	private Map<Integer, Entity> entities;
	private List<Entity> sortedEntityList;

	private String username, host;
	private NetworkManager network;
	private List<ReceivedPacket> packetQueue;

	private Player localPlayer;
	private float cameraX, cameraY;

	private Font font;

	private Chat chat;

	public Game(Screen screen, int width, int height, JFrame frame) {
		this.frame = frame;
		this.screen = screen;

		players = new HashMap<Integer, Player>();
		entities = new HashMap<Integer, Entity>();
		sortedEntityList = new ArrayList<Entity>();

		username = JOptionPane.showInputDialog("Username: ");
		host = JOptionPane.showInputDialog("IP: ");

		network = new NetworkManager(this);
		try {
			network.connect(username, host);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to connect to the server");
			System.exit(-1);
		}
		packetQueue = new ArrayList<ReceivedPacket>();

		font = new Font("Consolas", Font.BOLD, 13);

		chat = new Chat();
		chat.addChatListener(new ChatListener() {
			public void onChatMessage(String msg) {
				sendChatMessage(msg);
			}
		});

		instance = this;
	}

	public void sendChatMessage(String msg) {
		ChatMessagePacket chatMessage = new ChatMessagePacket();
		chatMessage.message = msg;
		network.sendTCP(chatMessage);
	}

	public void shootBullet(float angle, float speed, Player player) {
		ShootPacket shootPacket = new ShootPacket();
		shootPacket.x = player.x;
		shootPacket.y = player.y - 10;
		shootPacket.angle = angle;
		shootPacket.speed = speed;
		shootPacket.sender = player.getId();
		network.sendTCP(shootPacket);
	}

	public void onPacketReceived(Connection connection, Object object) {
		synchronized (packetQueue) {
			packetQueue.add(new ReceivedPacket(connection, object));
		}
	}

	private void onPacketReceived(ReceivedPacket packet) {
		Connection connection = packet.connection;
		Object object = packet.object;

		if (object instanceof SnapshotPacket) {
			SnapshotPacket snapshot = (SnapshotPacket) object;

			for (int removed : snapshot.removedEntities) {
				if (entities.containsKey(removed)) {
					entities.get(removed).onRemoved();
					entities.remove(removed);
				}
			}

			for (int id : snapshot.entities.keySet()) {
				EntityData entityData = snapshot.entities.get(id);
				if (entities.containsKey(id)) {
					Entity entity = entities.get(id);
					entity.setSnapshot(entityData);
				} else {
					switch (snapshot.entities.get(id).getType()) {
					case PLAYER: {
						Player player = new Player(entityData.getString("username"), id, false, entityData.getFloat("x"), entityData.getFloat("y"));
						players.put(id, player);
						entities.put(id, player);
						System.out.println(player.getUsername() + " has joined the game");
						// Player has connected
						break;
					}
					case BULLET: {
						Entity entity = new Bullet(id, entityData.getFloat("x"), entityData.getFloat("y"), entityData.getFloat("dx"), entityData.getFloat("dy"));
						entities.put(id, entity);
						break;
					}
					case DINER: {
						Entity entity = new Diner(id, entityData.getFloat("x"), entityData.getFloat("y"));
						entities.put(id, entity);
						break;
					}
					default:
						break;
					}
				}
			}
		} else if (object instanceof PlayerSpawnPacket) {
			PlayerSpawnPacket spawnPacket = (PlayerSpawnPacket) object;

			localPlayer = new Player(username, spawnPacket.id, true, spawnPacket.x, spawnPacket.y);
			players.put(localPlayer.getId(), localPlayer);
			entities.put(localPlayer.getId(), localPlayer);
		} else if (object instanceof EntityRemovePacket) {
			EntityRemovePacket removePacket = (EntityRemovePacket) object;

			if (players.containsKey(removePacket.id)) {
				players.remove(removePacket.id);
			}
			if (entities.containsKey(removePacket.id)) {
				entities.remove(removePacket.id);
			}
		} else if (object instanceof LevelSwitchPacket) {
			LevelSwitchPacket switchPacket = (LevelSwitchPacket) object;
			String levelName = switchPacket.levelName;
			chat.addChatMessage(null, "Switched level to " + levelName);

			// reload all entity information
			entities.entrySet().removeIf(entry -> entry.getKey() != localPlayer.getId());
			localPlayer.x = switchPacket.x;
			localPlayer.y = switchPacket.y;
			cameraX = switchPacket.x;
			cameraY = switchPacket.y;
		} else if (object instanceof LevelDataPacket) {
			LevelDataPacket worldData = (LevelDataPacket) object;

			world = new WorldData();
			world.x0 = worldData.x;
			world.y0 = worldData.y;
			world.width = worldData.width;
			world.height = worldData.height;
			world.data = worldData.tiles;
		} else if (object instanceof ChatMessagePacket) {
			ChatMessagePacket messagePacket = (ChatMessagePacket) object;
			chat.addChatMessage(messagePacket.sender, messagePacket.message);
		} else {
			System.err.println("Unknown packet type");
		}
	}

	public void onKeyPress(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_T: {
			if (!chat.isOpen()) {
				chat.open();
			} else {
				chat.onKeyPress(e);
			}
			break;
		}
		default: {
			if (chat.isOpen()) {
				chat.onKeyPress(e);
			} else {
				if (localPlayer != null) {
					localPlayer.onKeyPress(e);
				}
			}
			break;
		}
		}
	}

	public void onKeyRelease(KeyEvent e) {
		if (localPlayer != null) {
			localPlayer.onKeyRelease(e);
		}
	}

	public void onMousePressed(MouseEvent e) {
		if (localPlayer != null) {
			localPlayer.onMousePressed(e);
		}
	}

	public void onMouseReleased(MouseEvent e) {
		if (localPlayer != null) {
			localPlayer.onMouseRelease(e);
		}
	}

	public void tick1(int frames) {
		frame.setTitle("<" + username + "> - " + frames);
	}

	public void update(float dt) {

		// UPDATE

		updateNetworking();

		//for (int id : players.keySet()) {
		//	players.get(id).update(dt);
		//}
		for (int id : entities.keySet()) {
			entities.get(id).update(dt);
		}

		if (localPlayer != null) {
			cameraX = lerp(cameraX, localPlayer.x, 2.0f * dt);
			cameraY = lerp(cameraY, localPlayer.y, 2.0f * dt);
		}
		float scrollX = cameraX - (ClientMain.WIDTH / ClientMain.SCALE / 2);
		float scrollY = cameraY - 10 - (ClientMain.HEIGHT / ClientMain.SCALE / 2);
		screen.setScroll(scrollX, scrollY);

		// RENDER

		screen.clear();
		screen.setFont(font);

		if (world != null) {
			world.render(screen);
		}

		sortedEntityList.clear();
		sortedEntityList.addAll(entities.values());
		Collections.sort(sortedEntityList, new Comparator<Entity>() {
			public int compare(Entity o1, Entity o2) {
				return o1.y > o2.y ? 1 : -1;
			}
		});
		for (Entity e : sortedEntityList) {
			e.render(screen);
		}

		chat.render(screen);

		screen.render();

		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private float lerp(float f0, float f1, float blend) {
		return f0 + (f1 - f0) * blend;
	}

	private void updateNetworking() {
		while (!packetQueue.isEmpty()) {
			ReceivedPacket packet = packetQueue.get(0);
			onPacketReceived(packet);
			synchronized (packetQueue) {
				packetQueue.remove(0);
			}
		}

		if (localPlayer != null) {
			PlayerDataPacket playerData = new PlayerDataPacket();
			localPlayer.getDataPacket(playerData);
			network.sendUDP(playerData);
		}
	}

	public void close() {
		network.close();
	}
}
