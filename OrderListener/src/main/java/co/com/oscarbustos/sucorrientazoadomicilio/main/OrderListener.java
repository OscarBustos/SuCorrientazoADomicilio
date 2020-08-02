package co.com.oscarbustos.sucorrientazoadomicilio.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.com.oscarbustos.sucorrientazoadomicilio.filemanager.FileManager;
import co.com.oscarbustos.sucorrientazoadomicilio.watcher.DirectoryWatcher;

public class OrderListener {

	private static final String ORDERS_DIRECTORY = "orders.directory";
	public static Logger logger = LogManager.getLogger(OrderListener.class);
	private static String PROCESSED_DIRECTORY = "processed.directory";

	public static void main(String[] args) {
		try {
			if (args[0] != null) {
				Path propertiesFile = new File(args[0]).toPath();
				if (propertiesFile.toFile().exists()) {
					Properties properties = new Properties();
					FileReader reader = new FileReader(propertiesFile.toFile());
					properties.load(reader);
					reader.close();
					SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
					StringBuilder processedOrderDirectory = new StringBuilder(
							properties.getProperty(PROCESSED_DIRECTORY));
					FileManager.createDirectory(processedOrderDirectory.toString());
					processedOrderDirectory.append("/");
					processedOrderDirectory.append(format.format(new Date()));
					FileManager.createDirectory(processedOrderDirectory.toString());
					DirectoryWatcher watcher = new DirectoryWatcher();

					watcher.startWatcher(new File(properties.getProperty(ORDERS_DIRECTORY)),
							processedOrderDirectory.toString(), propertiesFile);
				}
			} else {
				logger.info("application.properties missing");
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
