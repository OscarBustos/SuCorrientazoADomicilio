package co.com.oscarbustos.sucorrientazoadomicilio.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.com.oscarbustos.sucorrientazoadomicilio.utils.FileManager;
import co.com.oscarbustos.sucorrientazoadomicilio.watcher.DirectoryWatcher;

public class OrderListener {

	public static void main(String[] args) {

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		StringBuilder procecedOrderDirectory = new StringBuilder("./procecedOrders");
		procecedOrderDirectory.append("/");
		procecedOrderDirectory.append(format.format(new Date()));
		FileManager.createDirectory(procecedOrderDirectory.toString());
		DirectoryWatcher watcher = new DirectoryWatcher();
		watcher.startWatcher(new File("./orders"), procecedOrderDirectory.toString());
		System.out.println("End");
	}
}
