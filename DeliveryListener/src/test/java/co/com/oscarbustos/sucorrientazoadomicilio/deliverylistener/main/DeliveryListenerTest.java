package co.com.oscarbustos.sucorrientazoadomicilio.deliverylistener.main;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

class DeliveryListenerTest {

	@Test
	void testStart() {

		String args[] = {
				"D:/Workspaces/S4N_TecnicalTest/SuCorrientazoADomicilio/OrderListener/src/test/resources/application.properties",
				"01" };
		if (args[0] != null && args[1] != null) {
			Path propertiesFile = new File(args[0]).toPath();
			if (propertiesFile.toFile().exists()) {

				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.execute(new Runnable() {
					@Override
					public void run() {
						DeliveryListener listener;
						try {
							listener = new DeliveryListener(propertiesFile);

							listener.start(propertiesFile, args[1]);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				String fileName = "D:/Workspaces/S4N_TecnicalTest/SuCorrientazoADomicilio/OrderListener/src/test/resources/processedOrders/";
				fileName += format.format(new Date()) + "/out";
				fileName += args[1] + ".txt";
				File file = new File(fileName);
				while (!file.exists()) {

				}
				assertTrue(file.exists());
			} else {
				System.out.println("application.properties and topic number");
			}
		}
	}

}
