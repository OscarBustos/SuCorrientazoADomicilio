package co.com.oscarbustos.sucorrientazoadomicilio.watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.com.oscarbustos.sucorrientazoadomicilio.queuemanager.KafkaManager;
import co.com.oscarbustos.sucorrientazoadomicilio.queuemanager.QueueManager;

public class DirectoryWatcher {

	private Logger logger = LogManager.getLogger(DirectoryWatcher.class);
	private WatchService watcher;
	private boolean running;
	private int count;

	public void startWatcher(File directory, String procecedOrdersDirectory, Path propertiesFile) {
		try {
			watcher = FileSystems.getDefault().newWatchService();
			Path dir = (directory).toPath();
			WatchKey key = dir.register(watcher, ENTRY_CREATE);
			run(dir, key, procecedOrdersDirectory, propertiesFile);

		} catch (IOException e) {
			logger.error("Couldn't register directory " + directory.getName(), e);
		}
	}

	public synchronized void stop() {
		running = false;
	}

	private synchronized void run(Path dir, WatchKey key, String procecedOrdersDirectory, Path propertiesFile) {
		running = true;
		while (running) {

			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				logger.error("DirectoryWatcher.startWatcher(): InterruptedException", x);
				return;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				if (kind == OVERFLOW) {
					continue;
				}

				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path filename = ev.context();

				Path file = null;
				try {
					file = dir.resolve(filename);
					if (!Files.probeContentType(file).equals("text/plain")) {
						logger.info("DirectoryWatcher - run(): Found file is not a txt file");
						continue;
					}
				} catch (IOException x) {
					logger.info("Error: DirectoryWatcher - run() | error resolving file", x);
					continue;
				}

				String topicName = file.getFileName().toString().substring(0,
						file.getFileName().toString().lastIndexOf("."));
				QueueManager queueManager = null;
				try {
					queueManager = new KafkaManager(propertiesFile, topicName);
				} catch (IOException e) {
					logger.error(e);
					return;
				}
				ExecutorService executor = Executors.newSingleThreadExecutor();

				Path procecedOrdersPath = new File(procecedOrdersDirectory + "/" + file.getFileName()).toPath();
				executor.execute(new OrderManager(file, queueManager, procecedOrdersPath));
				count++;
			}

			boolean valid = key.reset();
			if (!valid) {
				break;
			}
		}

	}

	public int getCount() {
		return count;
	};
}
