package co.com.oscarbustos.sucorrientazoadomicilio.watcher;

import java.io.File;

public class DirectoryWatcherRunnable implements Runnable {

	private boolean running;
	private DirectoryWatcher watcher;
	private File directory;
	private String procecedOrdersDirectory;

	public DirectoryWatcherRunnable(boolean running, File directory, String procecedOrdersDirectory) {
		this.running = running;
		this.watcher = new DirectoryWatcher();
		this.directory = directory;
		this.procecedOrdersDirectory = procecedOrdersDirectory;
	}

	public void stop() {
		this.running = false;
		Thread.currentThread().interrupt();

	}

	@Override
	public void run() {
		watcher.startWatcher(directory, procecedOrdersDirectory);
	}

	public DirectoryWatcher getWatcher() {
		return watcher;
	}

	public void setWatcher(DirectoryWatcher watcher) {
		this.watcher = watcher;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
