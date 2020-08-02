package co.com.oscarbustos.sucorrientazoadomicilio.main;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.com.oscarbustos.sucorrientazoadomicilio.utils.FileManager;
import co.com.oscarbustos.sucorrientazoadomicilio.watcher.DirectoryWatcher;

public class OrderListener {

	public static Logger logger = LogManager.getLogger(OrderListener.class);

	public static void main(String[] args) {

		if (args[0] != null) {
			Path propertiesFile = new File(args[0]).toPath();
			if (propertiesFile.toFile().exists()) {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				StringBuilder procecedOrderDirectory = new StringBuilder("./procecedOrders");
				procecedOrderDirectory.append("/");
				procecedOrderDirectory.append(format.format(new Date()));
				FileManager.createDirectory(procecedOrderDirectory.toString());
				DirectoryWatcher watcher = new DirectoryWatcher();
				watcher.startWatcher(new File("./orders"), procecedOrderDirectory.toString(), propertiesFile);
			}
		} else {
			logger.info("application.properties missing");
		}
	}
}
