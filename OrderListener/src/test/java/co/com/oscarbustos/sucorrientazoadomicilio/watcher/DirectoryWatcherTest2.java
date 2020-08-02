package co.com.oscarbustos.sucorrientazoadomicilio.watcher;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

import co.com.oscarbustos.sucorrientazoadomicilio.filemanager.FileManager;

class DirectoryWatcherTest2 {

	@Test
	void testStartWatcher() {
		ExecutorService executor1 = null;
		ExecutorService executor2 = null;
		try {
			{
				boolean borrado = false;
				File file = new File("./src/test/resources/processedOrders");
				if (file.exists()) {
					borrado = FileManager.deleteFile(file);
				}
				file = new File("./src/test/resources/orders");
				if (file.exists()) {
					borrado = FileManager.deleteFile(file);
				}

				file = new File("./src/test/resources/newOrders");
				if (file.exists()) {
					borrado = FileManager.deleteFile(file);
				}

			}
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			StringBuilder processedOrderDirectory = new StringBuilder("./src/test/resources/processedOrders");
			processedOrderDirectory.append("/");
			processedOrderDirectory.append(format.format(new Date()));

			File directory = new File("./src/test/resources/orders");
			FileManager.createDirectory("./src/test/resources/orders");
			FileManager.createDirectory("./src/test/resources/newOrders");
			FileManager.createDirectory("./src/test/resources/processedOrders");
			FileManager.createDirectory("./src/test/resources/processedOrders/" + format.format(new Date()));

			executor1 = Executors.newSingleThreadExecutor();

			Path propertiesPath = new File(
					"D:/Workspaces/S4N_TecnicalTest/SuCorrientazoADomicilio/OrderListener/src/main/resources/application.properties")
							.toPath();

			DirectoryWatcherRunnable runnable = new DirectoryWatcherRunnable(true, directory,
					processedOrderDirectory.toString(), propertiesPath);

			executor1.execute(runnable);
			Thread.sleep(5000);
			executor2 = Executors.newSingleThreadExecutor();
			executor2.execute(() -> {
				try {

					for (int i = 1; i <= 10; i++) {
						String fileName = "";
						if (i / 10 == 0) {
							fileName += "0";
						}
						fileName += i;
						Path file = new File("./src/test/resources/newOrders/in" + fileName + ".txt").toPath();
						BufferedWriter writer = FileManager.getBufferedWriter(file);

						writer.append("AAAAIAA");
						writer.newLine();
						writer.append("DDDAIAD");
						writer.newLine();
						writer.append("AAIADAD");

						writer.close();
						File newFile = new File("./src/test/resources/orders/in" + fileName + ".txt");
						file.toFile().renameTo(newFile);
						Thread.sleep(1000);
					}

				} catch (Exception e) {
				}

			});

			int prev = 0;
			while (!executor1.isShutdown()) {
				if (prev != runnable.getWatcher().getCount()) {
					prev = runnable.getWatcher().getCount();
				}
				if (runnable.getWatcher().getCount() >= 10) {

					Thread.sleep(5000);
					runnable.stop();
					executor1.shutdownNow();

				}
			}

			boolean result = false;
			int count = 0;

			Thread.sleep(1000);
			for (int i = 1; i <= 10; i++) {

				String fileName = "";
				if (i / 10 == 0) {
					fileName += "0";
				}
				fileName += i;
				File newFile = new File(processedOrderDirectory.toString() + "/in" + fileName + ".txt");
				if (newFile.exists()) {
					count++;
					result = true;
				} else {
					result = false;
					break;
				}
			}
			assertTrue(result);
		} catch (Exception e) {

		} finally {
			executor2.shutdownNow();
			executor1.shutdownNow();
		}

	}

}
