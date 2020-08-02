package co.com.oscarbustos.sucorrientazoadomicilio.watcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.com.oscarbustos.sucorrientazoadomicilio.filemanager.FileManager;
import co.com.oscarbustos.sucorrientazoadomicilio.queuemanager.QueueManager;

public class OrderManager implements Runnable {

	private Logger logger = LogManager.getLogger(OrderManager.class);
	private Path file;
	private QueueManager queueManager;
	private Path processedOrdersDirectory;

	public OrderManager(Path file, QueueManager queueManager, Path processedOrdersDirectory) {
		this.file = file;
		this.queueManager = queueManager;
		this.processedOrdersDirectory = processedOrdersDirectory;
	}

	@Override
	public void run() {
		BufferedReader reader = null;
		try {
			reader = FileManager.getBufferedReader(file);

			String line = null;
			while ((line = reader.readLine()) != null) {
				queueManager.produce(line);
			}
			FileManager.closeBufferedReader(reader);
			FileManager.moveFile(file, processedOrdersDirectory);
		} catch (IOException e) {
			logger.error("OrderManager - run(): ", e);
		}
	}

}
