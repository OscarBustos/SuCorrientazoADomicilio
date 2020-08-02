package co.com.oscarbustos.sucorrientazoadomicilio.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import co.com.oscarbustos.sucorrientazoadomicilio.queuemanager.KafkaManager;
import co.com.oscarbustos.sucorrientazoadomicilio.queuemanager.QueueManager;

public class DroneController {

	private static final String DRONE_CAPACITY = "drone.capacity";
	private static final String IN_PREFIX = "in.prefix";
	private static final String OUT_PREFIX = "out.prefix";
	private static final String HOME_LOCATION = "home.location";
	private static final Logger logger = LogManager.getLogger(DroneController.class);

	private Properties properties;
	private String consumeTopicName;
	private String produceTopicName;
	private QueueManager kafkaManager;

	public DroneController(Path propertiesFile, String droneName) throws IOException {
		loadProperties(propertiesFile);
		consumeTopicName = properties.getProperty(IN_PREFIX) + droneName;
		produceTopicName = properties.getProperty(OUT_PREFIX) + droneName;

		kafkaManager = new KafkaManager(propertiesFile, produceTopicName, consumeTopicName);

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

	public void start() {
		int droneCapacity = Integer.parseInt((String) properties.get(DRONE_CAPACITY));
		String currentLocation = properties.getProperty(HOME_LOCATION);
		for (int i = 0; i < droneCapacity; i++) {
			String value = kafkaManager.consume();
			if (value != null && !"".equals(value)) {
				String location = move(value, currentLocation);
				kafkaManager.produce(location);
				currentLocation = location;
			}
		}
		kafkaManager.closeConsumer();
	}

	public String move(String value, String currentLocation) {
		int currentX = Integer.parseInt(currentLocation.split(",")[0]);
		int currentY = Integer.parseInt(currentLocation.split(",")[1]);
		String direction = currentLocation.split(",")[2];

		String movements[] = value.split("|");

		for (String movement : movements) {
			if (!movement.contentEquals("A")) {
				direction = defineDirection(direction, movement);
			} else {

				switch (defineAxis(direction)) {
				case "X":
					currentX += defineMovement(direction);
					break;
				case "Y":
					currentY += defineMovement(direction);
					break;
				}
			}

		}

		StringBuilder location = new StringBuilder();
		location.append(currentX);
		location.append(",");
		location.append(currentY);
		location.append(",");
		location.append(direction);

		return location.toString();
	}

	private String defineDirection(String direction, String movement) {
		String newDirection = "";
		switch (movement) {
		case "D":
			if ("N".contentEquals(direction)) {
				return "E";
			} else if ("E".contentEquals(direction)) {
				return "S";
			} else if ("S".contentEquals(direction)) {
				return "W";
			} else if ("W".contentEquals(direction)) {
				return "N";
			}
			break;
		case "I":
			if ("N".contentEquals(direction)) {
				return "W";
			} else if ("W".contentEquals(direction)) {
				return "S";
			} else if ("S".contentEquals(direction)) {
				return "E";
			} else if ("E".contentEquals(direction)) {
				return "N";
			}
			break;
		}
		return newDirection;
	}

	private String defineAxis(String direction) {
		switch (direction) {
		case "W":
		case "E":
			return "X";
		case "S":
		case "N":
			return "Y";
		}
		return "";
	}

	private int defineMovement(String direction) {
		switch (direction) {
		case "N":
		case "E":
			return 1;
		case "S":
		case "W":
			return -1;
		}
		return 0;
	}

	private void goHome() {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		try {
			if (args[0] != null && args[1] != null) {
				Path propertiesFile = new File(args[0]).toPath();
				if (propertiesFile.toFile().exists()) {
					DroneController dronController = new DroneController(propertiesFile, args[1]);
					dronController.start();
				} else {
					logger.info("application.properties or dron name missing");
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}

	}
}
