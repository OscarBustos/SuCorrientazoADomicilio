package co.com.oscarbustos.sucorrientazoadomicilio.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

import co.com.oscarbustos.sucorrientazoadomicilio.filemanager.FileManager;

class FileManagerTest {

	@Test
	void testCreateDirectory() {
		String directory = "./orders";
		FileManager.createDirectory(directory);
		File file = new File(directory);
		assertTrue(file.exists());
	}

	@Test
	void testGetBufferedReaderFileNotFoundException() {
		Path file = new File("./src/test/resources/FileTexto.txt").toPath();
		assertThrows(FileNotFoundException.class, () -> FileManager.getBufferedReader(file));
	}

	@Test
	void testGetBufferedReaderFileExists() {
		Path file = new File("./src/test/resources/File01.txt").toPath();
		try {
			assertNotNull(FileManager.getBufferedReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	@Test
	void testMoveFile() {
		try {
			Path file = new File("./orders/Order1.txt").toPath();

			BufferedWriter writer = FileManager.getBufferedWriter(file);
			for (int i = 0; i < 5; i++) {
				writer.append("aaaabac");
				writer.newLine();
				writer.append("asdfaerw");
				writer.newLine();
			}
			writer.append("ASDFSADF");
			writer.flush();
			FileManager.closeBufferedWriter(writer);
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			StringBuilder processedOrderDirectory = new StringBuilder("./processedOrders");
			processedOrderDirectory.append("/");
			processedOrderDirectory.append(format.format(new Date()));

			Path processedOrdersDirectory = new File(processedOrderDirectory.toString() + "/" + file.getFileName())
					.toPath();

			boolean done = FileManager.moveFile(file, processedOrdersDirectory);
			assertTrue(done);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

		
	@Test
	void testGetBufferedWriter() {
		Path file = new File("./src/test/resources/File01.txt").toPath();
		try {
			assertNotNull(FileManager.getBufferedWriter(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
