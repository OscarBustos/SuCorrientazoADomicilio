package co.com.oscarbustos.sucorrientazoadomicilio.queuemanager;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class KafkaManagerTest {

	@Test
	void testProduce() throws IOException {
		Path propertiesFile = new File(
				"D:\\Workspaces\\S4N_TecnicalTest\\SuCorrientazoADomicilio\\OrderListener\\src\\main\\resources\\application.properties")
						.toPath();
		KafkaManager k = new KafkaManager(propertiesFile, "in01");
		k.produce("new message 01");
		assertAll(() -> k.produce("new message 01"));

	}

	@Test
	void testLoadProperties() throws IOException {
		Path propertiesFile = new File(
				"D:\\Workspaces\\S4N_TecnicalTest\\SuCorrientazoADomicilio\\OrderListener\\src\\main\\resources\\application.properties")
						.toPath();
		KafkaManager k = new KafkaManager(propertiesFile, "in01");
		assertNotNull(k.getProperties().get("bootstrap.servers"));
	}

}
