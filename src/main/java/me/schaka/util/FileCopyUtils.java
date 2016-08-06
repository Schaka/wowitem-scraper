package me.schaka.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

public final class FileCopyUtils {
	private FileCopyUtils() {
	}

	/**
	 * Only takes the folder in your classpath to copy. <br/>
	 * If you want to keep the root foldername, you will need to add it to your target path
	 *
	 * @param source
	 * @param target
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static void copyFolderContentFromClasspath(String source, final Path target) throws URISyntaxException, IOException {
		final Path jarPath = getPath(source);
		if (isJarFile()) {
			Files.createDirectories(target);
			Files.list(jarPath).forEach(p -> copy(jarPath, p, target));
		} else {
			Files.walkFileTree(jarPath, new SimpleFileVisitor<Path>() {

				private Path currentTarget;

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					currentTarget = target.resolve(jarPath.relativize(dir).toString());
					Files.createDirectories(currentTarget);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.copy(file, target.resolve(jarPath.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
					return FileVisitResult.CONTINUE;
				}

			});
		}
	}

	private static void copy(Path basePath, Path path, Path target) {
		try {
			Path resolvedTarget = target.resolve(basePath.relativize(path).toString());
			Files.copy(FileCopyUtils.class.getResourceAsStream("/" + path.toString().replace("/BOOT-INF/classes/", "")), resolvedTarget, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static Path getPath(String uri) throws IOException, URISyntaxException {
		URI resource = FileCopyUtils.class.getResource("/config").toURI();
		if (resource.getScheme().equals("jar")) {
			return getOrCreateFilesystem(resource).getPath("/BOOT-INF/classes/" + uri);
		}
		return Paths.get(FileCopyUtils.class.getResource("/" + uri).toURI());
	}

	private static FileSystem getOrCreateFilesystem(URI baseUri) throws IOException {
		FileSystem result;
		try {
			result = FileSystems.newFileSystem(baseUri, Collections.emptyMap());
		} catch (FileSystemAlreadyExistsException e) {
			result = FileSystems.getFileSystem(baseUri);
		}
		return result;
	}

	private static boolean isJarFile() throws URISyntaxException {
		return FileCopyUtils.class.getResource("").toURI().getScheme().equals("jar");
	}
}
