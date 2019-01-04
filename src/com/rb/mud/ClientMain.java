package com.rb.mud;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ClientMain implements Runnable {
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final int SCALE = 2;
	private static final String TITLE = "MUD";

	private JFrame frame;
	private Screen screen;
	private Thread thread;
	private boolean running;

	private Game game;

	private ClientMain(JFrame frame) {
		this.frame = frame;

		screen = new Screen(WIDTH / SCALE, HEIGHT / SCALE);
		screen.setSize(WIDTH, HEIGHT);

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				JOptionPane.showMessageDialog(null, t.getName() + ":\n" + e.getMessage());
			}
		});
	}

	private void start() {
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		game = new Game(screen, WIDTH, HEIGHT, frame);

		screen.requestFocus();
		screen.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				game.onKeyRelease(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				game.onKeyPress(e);
			}
		});
		screen.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				game.onMouseReleased(e);
			}

			public void mousePressed(MouseEvent e) {
				game.onMousePressed(e);
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}
		});

		long lastFrame = System.nanoTime();
		long lastSecond = System.nanoTime();
		int frames = 0;
		float delta;

		running = true;
		while (running) {
			long now = System.nanoTime();
			if (now - lastSecond >= 1e9) {
				game.tick1(frames);
				frames = 0;
				lastSecond = now;
			}
			delta = (now - lastFrame) / 1e9f;
			lastFrame = now;

			game.update(delta);
			frames++;
		}

		game.close();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame(TITLE);
		ClientMain main = new ClientMain(frame);

		frame.add(main.screen);
		frame.pack();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				main.running = false;
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		frame.setVisible(true);

		main.start();
	}

}
