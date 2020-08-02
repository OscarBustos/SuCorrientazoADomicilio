package co.com.oscarbustos.sucorrientazoadomicilio.deliverylistener.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import co.com.oscarbustos.sucorrientazoadomicilio.queuemanager.KafkaManager;
import co.com.oscarbustos.sucorrientazoadomicilio.queuemanager.QueueManager;

public class DeliveryListener {

	private static final String OUT_PREFIX = "out.prefix";
	private static final String FILE_EXTENSION = "file.extension";
	private static final Logger logger = LogManager.getLogger(DeliveryListener.class);
	private Properties properties;

	public DeliveryListener(Path propertiesFile) throws IOException {
		loadProperties(propertiesFile);
	}

	private void loadProperties(Path propertiesFile) throws IOException {
		try {
			properties = new Properties();
			FileReader reader;
			reader = new FileReader(propertiesFile.toFile());
			properties.load(reader);
			reader.close();
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("DronController - Error: missing properties file");

		} catch (IOException e) {
			throw new IOException("DronController - Error: couldn't load properties file", e);
		}
	}

	public static void main(String args[]) {
		try {
			if (args[0] != null && args[1] != null) {
				Path propertiesFile = new File(args[0]).toPath();
				if (propertiesFile.toFile().exists()) {

					DeliveryListener listener = new DeliveryListener(propertiesFile);
					listener.start(propertiesFile, args[1]);
				} else {
					logger.info("application.properties and topic number");
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}

	}

	public void start(Path propertiesFile, String topicNumber) {
		QueueManager kafkaManager;
		try {
			StringBuilder topicName = new StringBuilder(properties.getProperty(OUT_PREFIX));
			topicName.append(topicNumber);
			kafkaManager = new KafkaManager(propertiesFile, null, topicName.toString());

			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.execute(new DeliveryManager(properties, kafkaManager,
					topicName.toString() + properties.getProperty(FILE_EXTENSION)));
		} catch (IOException e) {
			logger.error(e);
		}

	}

}
