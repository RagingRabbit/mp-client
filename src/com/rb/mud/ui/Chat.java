package com.rb.mud.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import com.rb.mud.Screen;

public class Chat {
	private static final Color NO_SENDER_MSG_COLOR = new Color(0xFFD877);

	private List<ChatMessage> messages;
	private StringBuilder current;

	private boolean open;

	private List<ChatListener> listeners;

	public Chat() {
		messages = new ArrayList<ChatMessage>();
		current = new StringBuilder();
		open = false;

		listeners = new ArrayList<ChatListener>();
	}

	public void addChatListener(ChatListener listener) {
		listeners.add(listener);
	}

	public void addChatMessage(String sender, String msg) {
		messages.add(new ChatMessage(sender, msg, Color.WHITE));
	}

	public void open() {
		open = true;
	}

	public void close() {
		open = false;
	}

	public void onKeyPress(KeyEvent e) {
		if (!open) {
			return;
		}
		char c = e.getKeyChar();
		if (c != '?') {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (current.length() > 0) {
					String msg = current.toString();
					//messages.add(new ChatMessage(msg, Color.WHITE));
					for (ChatListener listener : listeners) {
						listener.onChatMessage(msg);
					}
				}
				close();
				current = new StringBuilder();
			} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && current.length() > 0) {
				current.deleteCharAt(current.length() - 1);
			} else if (isPrintableChar(c)) {
				current.append(c);
			}
		}
	}

	private boolean isPrintableChar(char c) {
		Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
		return (!Character.isISOControl(c)) &&
				c != KeyEvent.CHAR_UNDEFINED &&
				block != null &&
				block != Character.UnicodeBlock.SPECIALS;
	}

	public void render(Screen screen) {
		if (open) {
			screen.renderUIString(10, screen.getScreenHeight() - 10, current.toString() + "_", Color.WHITE);
		}
		int pointer = 0;
		for (int i = messages.size() - 1; i >= 0; i--) {
			ChatMessage currentMessage = messages.get(i);
			String messageString = (currentMessage.sender != null ? "<" + currentMessage.sender + "> " : "") + currentMessage.text;
			Color messageColor = currentMessage.sender != null ? currentMessage.color : NO_SENDER_MSG_COLOR;
			screen.renderUIString(10, screen.getScreenHeight() - 10 - 10 - pointer++ * 10, messageString, messageColor);
		}
	}

	public boolean isOpen() {
		return open;
	}

	static class ChatMessage {
		String sender;
		String text;
		Color color;

		ChatMessage(String sender, String text, Color color) {
			this.sender = sender;
			this.text = text;
			this.color = color;
		}
	}

	public static interface ChatListener {
		void onChatMessage(String msg);
	}

}
