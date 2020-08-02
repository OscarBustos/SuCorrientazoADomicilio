package co.com.oscarbustos.sucorrientazoadomicilio.main;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class DroneControllerTest {

	@Test
	void testStart() {
		try {
			String args[] = {
					"D:/Workspaces/S4N_TecnicalTest/SuCorrientazoADomicilio/OrderListener/src/test/resources/application.properties",
					"01" };
			if (args[0] != null && args[1] != null) {
				Path propertiesFile = new File(args[0]).toPath();
				if (propertiesFile.toFile().exists()) {
					DroneController dronController = new DroneController(propertiesFile, args[1]);
					assertAll(() -> dronController.start());
				} else {
					System.out.println("application.properties or dron name missing");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	void testMoveSingle() {
		Path propertiesFile = new File(
				"D:\\Workspaces\\S4N_TecnicalTest\\SuCorrientazoADomicilio\\OrderListener\\src\\main\\resources\\application.properties")
						.toPath();
		DroneController controller;
		try {
			controller = new DroneController(propertiesFile, "01");
			String location = controller.move("AAAAIAA", "0,0,N");
			assertEquals(location, "-2,4,W");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void testMoveMultiple() {
		Path propertiesFile = new File(
				"D:\\Workspaces\\S4N_TecnicalTest\\SuCorrientazoADomicilio\\OrderListener\\src\\main\\resources\\application.properties")
						.toPath();
		DroneController controller;
		try {
			controller = new DroneController(propertiesFile, "01");
			String location = controller.move("AAAAIAA", "0,0,N");
			assertEquals(location, "-2,4,W");
			location = controller.move("DDDAIAD", location);
			assertEquals(location, "-1,3,S");
			location = controller.move("AAIADAD", location);

			assertEquals(location, "0,0,W");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
