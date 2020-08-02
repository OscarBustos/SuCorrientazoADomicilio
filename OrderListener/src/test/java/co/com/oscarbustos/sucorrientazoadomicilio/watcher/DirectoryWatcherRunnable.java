package co.com.oscarbustos.sucorrientazoadomicilio.watcher;

import java.io.File;
import java.nio.file.Path;

public class DirectoryWatcherRunnable implements Runnable {

	private boolean running;
	private DirectoryWatcher watcher;
	private File directory;
	private String procecedOrdersDirectory;
	private Path propertiesPath;

	public DirectoryWatcherRunnable(boolean running, File directory, String procecedOrdersDirectory,
			Path propertiesPath) {
		this.running = running;
		this.watcher = new DirectoryWatcher();
		this.directory = directory;
		this.procecedOrdersDirectory = procecedOrdersDirectory;
		this.propertiesPath = propertiesPath;
	}

	public void stop() {
		this.running = false;
		Thread.currentThread().interrupt();

	}

	@Override
	public void run() {
		watcher.startWatcher(directory, procecedOrdersDirectory, propertiesPath);
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
