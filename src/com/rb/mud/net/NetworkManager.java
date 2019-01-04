package com.rb.mud.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.rb.mud.Game;
import com.rb.shared.entity.EntityType;
import com.rb.shared.packets.ChatMessagePacket;
import com.rb.shared.packets.EntityRemovePacket;
import com.rb.shared.packets.LevelSwitchPacket;
import com.rb.shared.packets.PlayerConnectPacket;
import com.rb.shared.packets.PlayerDataPacket;
import com.rb.shared.packets.PlayerSpawnPacket;
import com.rb.shared.packets.ShootPacket;
import com.rb.shared.packets.SnapshotPacket;
import com.rb.shared.packets.SnapshotPacket.EntityData;
import com.rb.shared.packets.TestPacket;
import com.rb.shared.packets.LevelDataPacket;

public class NetworkManager {
	private static final String IP = "80.133.249.151";
	private static final int PORT_TCP = 4444;
	private static final int PORT_UDP = 4445;

	private Game game;
	private Client client;
	private Kryo kryo;

	public NetworkManager(Game game) {
		this.game = game;
	}

	public void connect(String username, String host) throws IOException {
		client = new Client(15000, 15000);
		client.start();

		kryo = client.getKryo();

		kryo.register(EntityType.class);

		kryo.register(TestPacket.class);
		kryo.register(PlayerConnectPacket.class);
		kryo.register(PlayerDataPacket.class);
		kryo.register(SnapshotPacket.class);
		kryo.register(EntityData.class);
		kryo.register(PlayerSpawnPacket.class);
		kryo.register(EntityRemovePacket.class);
		kryo.register(LevelSwitchPacket.class);

		kryo.register(LevelDataPacket.class);

		kryo.register(ChatMessagePacket.class);

		kryo.register(ShootPacket.class);

		kryo.register(int[].class);
		kryo.register(byte[].class);
		kryo.register(boolean[].class);
		kryo.register(ArrayList.class);
		kryo.register(HashMap.class);

		client.connect(5000, host, PORT_TCP, PORT_UDP);
		client.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				game.onPacketReceived(connection, object);
			}
		});

		PlayerConnectPacket connectPacket = new PlayerConnectPacket();
		connectPacket.username = username;
		client.sendTCP(connectPacket);
	}

	public void sendUDP(Object object) {
		client.sendUDP(object);
	}

	public void sendTCP(Object object) {
		client.sendTCP(object);
	}

	public void close() {
	}
}
