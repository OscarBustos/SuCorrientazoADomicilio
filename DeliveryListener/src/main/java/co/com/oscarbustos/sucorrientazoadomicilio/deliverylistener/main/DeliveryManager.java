package co.com.oscarbustos.sucorrientazoadomicilio.deliverylistener.main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import co.com.oscarbustos.sucorrientazoadomicilio.filemanager.FileManager;
import co.com.oscarbustos.sucorrientazoadomicilio.queuemanager.QueueManager;

public class DeliveryManager implements Runnable {

	private boolean running;
	private QueueManager queueManager;
	private Properties properties;
	private String fileName;
	private Logger logger = LogManager.getLogger(DeliveryManager.class);
	private static String PROCESSED_DIRECTORY = "processed.directory";

	public DeliveryManager(Properties propertiesFile, QueueManager queueManager, String fileName) {
		this.properties = propertiesFile;
		this.queueManager = queueManager;
		this.fileName = fileName;

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		StringBuilder processedOrderDirectory = new StringBuilder(properties.getProperty(PROCESSED_DIRECTORY));
		FileManager.createDirectory(processedOrderDirectory.toString());
		processedOrderDirectory.append("/");
		processedOrderDirectory.append(format.format(new Date()));
		FileManager.createDirectory(processedOrderDirectory.toString());
		this.fileName = processedOrderDirectory.toString() + "/" + fileName;
	}

	public void stop() {
		running = false;
		Thread.currentThread().interrupt();
	}

	public void run() {
		running = true;
		while (running) {
			String line = queueManager.consume();
			if (line != null && !"".contentEquals(line)) {
				System.out.println(line);
				try {
					FileManager.write(line, fileName);
				} catch (IOException e) {
					logger.error("DeliveryManager run() - Error writing file");
				}
				queueManager.commit();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
