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

import co.com.oscarbustos.sucorrientazoadomicilio.utils.FileManager;

class DirectoryWatcherTest {

	@Test
	void testStartWatcher() {
		ExecutorService executor1 = null;
		ExecutorService executor2 = null;
		try {
			{
				boolean borrado = false;
				File file = new File("./src/test/resources/procecedOrders");
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
			StringBuilder procecedOrderDirectory = new StringBuilder("./src/test/resources/procecedOrders");
			procecedOrderDirectory.append("/");
			procecedOrderDirectory.append(format.format(new Date()));

			File directory = new File("./src/test/resources/orders");
			FileManager.createDirectory("./src/test/resources/orders");
			FileManager.createDirectory("./src/test/resources/newOrders");
			FileManager.createDirectory("./src/test/resources/procecedOrders");
			FileManager.createDirectory("./src/test/resources/procecedOrders/" + format.format(new Date()));

			executor1 = Executors.newSingleThreadExecutor();

			DirectoryWatcherRunnable runnable = new DirectoryWatcherRunnable(true, directory,
					procecedOrderDirectory.toString());

			executor1.execute(runnable);
			Thread.sleep(5000);
			executor2 = Executors.newSingleThreadExecutor();
			executor2.execute(() -> {
				try {

					for (int i = 0; i < 10; i++) {
						Path file = new File("./src/test/resources/newOrders/file" + i + ".txt").toPath();
						BufferedWriter writer = FileManager.getBufferedWriter(file);
						for (int j = 0; j < 3; j++) {
							writer.append("ABCDE");
							writer.newLine();
						}
						writer.append("ABCDE");
						writer.close();
						File newFile = new File("./src/test/resources/orders/file" + i + ".txt");
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
					runnable.stop();
					executor1.shutdownNow();

				}
			}

			boolean result = false;
			int count = 0;

			Thread.sleep(1000);
			for (int i = 0; i < 10; i++) {
				File newFile = new File(procecedOrderDirectory.toString() + "/file" + i + ".txt");
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
