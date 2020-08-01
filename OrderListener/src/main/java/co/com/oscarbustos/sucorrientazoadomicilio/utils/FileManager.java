package co.com.oscarbustos.sucorrientazoadomicilio.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public class FileManager {

	/**
	 * Create an specific directory if it doesn't exists
	 * 
	 * @param directory
	 */
	public static void createDirectory(String directory) {

		File file = new File(directory);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	/**
	 * Generate BufferedReader from file
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static BufferedReader getBufferedReader(Path file) throws FileNotFoundException {
		return new BufferedReader(new FileReader(file.toFile()));
	}

	/**
	 * Move a file to the specified directory, if the file already exist in the
	 * destination directory, its content is appended, and the file is deleted.
	 * 
	 * @param file
	 * @param procecedOrdersDirectory
	 * @return
	 * @throws IOException
	 */
	public static boolean moveFile(Path file, Path procecedOrdersDirectory) throws IOException {

		createDirectory(
				procecedOrdersDirectory.toString().substring(0, procecedOrdersDirectory.toString().lastIndexOf("\\")));

		if (procecedOrdersDirectory.toFile().exists()) {
			BufferedReader reader = getBufferedReader(file);
			String line = null;
			BufferedWriter writer = getBufferedWriter(procecedOrdersDirectory);
			while ((line = reader.readLine()) != null) {
				writer.newLine();
				writer.append(line);
			}

			writer.flush();

			closeBufferedReader(reader);
			closeBufferedWriter(writer);

			return deleteFile(file.toFile());

		} else {
			boolean done = file.toFile().renameTo(procecedOrdersDirectory.toFile());
			return done;
		}

	}

	/**
	 * Delete a specific file, if the file is not deleted the first time, it tries
	 * until it is deleted
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static boolean deleteFile(File file) {
		boolean done = false;
		while (!done) {
			if (file.exists()) {
				if (file.isDirectory()) {
					try {
						done = deleteDirectoryRecursion(file.toPath());
					} catch (IOException e) {

					}
				} else {
					done = file.delete();
				}
			} else {
				done = true;
			}

		}
		return done;
	}

	private static boolean deleteDirectoryRecursion(Path path) throws IOException {
		if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
				for (Path entry : entries) {
					deleteDirectoryRecursion(entry);
				}
			}
		}
		Files.delete(path);
		return path.toFile().exists();
	}

	/**
	 * Generate a BufferedWriter from a specific file
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static BufferedWriter getBufferedWriter(Path file) throws IOException {
		return new BufferedWriter(new FileWriter(file.toFile(), true));
	}

	/**
	 * Clos a specific BufferedWriter
	 * 
	 * @param writer
	 * @throws IOException
	 */
	public static void closeBufferedWriter(BufferedWriter writer) throws IOException {
		writer.close();
	}

	/**
	 * Close a specific BufferedReader
	 * 
	 * @param reader
	 * @throws IOException
	 */
	public static void closeBufferedReader(BufferedReader reader) throws IOException {
		reader.close();
	}

}
