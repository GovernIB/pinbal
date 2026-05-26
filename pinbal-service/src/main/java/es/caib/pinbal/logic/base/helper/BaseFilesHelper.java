package es.caib.pinbal.logic.base.helper;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Mètodes per a gestionar arxius a dins la carpeta files de l'aplicació.
 *
 * @author Límit Tecnologies
 */
public abstract class BaseFilesHelper {

	/**
	 * Desa un fitxer donat el seu contingut en un forma d'array de bytes.
	 *
	 * @param folder
	 *            la carpeta pel nou fitxer (pot ser null).
	 * @param name
	 *            el nom del fitxer.
	 * @param content
	 *            el contingut del fitxer.
	 * @throws IOException si hi ha algun problema desant el fitxer.
	 */
	public void save(
			String folder,
			String name,
			byte[] content) throws IOException {
		File fitxer = new File(newFolderFile(folder, true), name);
		try (FileOutputStream fos = new FileOutputStream(fitxer)) {
			fos.write(content);
		}
	}

	/**
	 * Desa un fitxer donat el seu contingut en forma de ByteArrayOutputStream.
	 *
	 * @param folder
	 *            la carpeta pel nou fitxer (pot ser null).
	 * @param name
	 *            el nom del fitxer.
	 * @param content
	 *            el contingut del fitxer.
	 * @throws IOException si hi ha algun problema desant el fitxer.
	 */
	public void save(
			String folder,
			String name,
			ByteArrayOutputStream content) throws IOException {
		File fitxer = new File(newFolderFile(folder, true), name);
		try (FileOutputStream fos = new FileOutputStream(fitxer)) {
			content.writeTo(fos);
		}
	}

	/**
	 * Llegeix el contingut d'un fitxer.
	 *
	 * @param folder
	 *            la carpeta a on es troba fitxer (pot ser null).
	 * @param name
	 *            el nom del fitxer.
	 * @return true si existeix o false en cas contrari.
	 * @throws IOException si hi ha algun problema llegint el fitxer.
	 */
	public byte[] read(
			String folder,
			String name) throws IOException {
		File fitxer = new File(newFolderFile(folder, false), name);
		try (FileInputStream fis = new FileInputStream(fitxer)) {
			return fis.readAllBytes();
		}
	}

	/**
	 * Comprova si el fitxer especificat existeix.
	 *
	 * @param folder
	 *            la carpeta a on es troba fitxer (pot ser null).
	 * @param name
	 *            el nom del fitxer.
	 * @return true si existeix o false en cas contrari.
	 */
	public boolean exists(
			String folder,
			String name) {
		File fitxer = new File(newFolderFile(folder, false), name);
		return fitxer.exists();
	}

	/**
	 * Copia un fitxer.
	 *
	 * @param sourceFolder
	 *            la carpeta a on es troba fitxer (pot ser null).
	 * @param sourceName
	 *            el nom del fitxer existent.
	 * @param targetFolder
	 *            la carpeta a on es vol moure el fitxer (pot ser null).
	 * @param targetName
	 *            el nom del nou fitxer.
	 * @param replace
	 *            indica si s'ha de sobreescriure el fitxer destí.
	 * @throws IOException si hi ha algun problema movent el fitxer.
	 */
	public void copy(
			String sourceFolder,
			String sourceName,
			String targetFolder,
			String targetName,
			boolean replace) throws IOException {
		File source = new File(newFolderFile(sourceFolder, false), sourceName);
		File target = new File(newFolderFile(targetFolder, false), targetName);
		CopyOption[] options = replace ? new CopyOption[] { StandardCopyOption.REPLACE_EXISTING } : null;
		Files.copy(
				source.toPath(),
				target.toPath(),
				options);
	}

	/**
	 * Mou un fitxer.
	 *
	 * @param sourceFolder
	 *            la carpeta a on es troba fitxer (pot ser null).
	 * @param sourceName
	 *            el nom del fitxer existent.
	 * @param targetFolder
	 *            la carpeta a on es vol moure el fitxer (pot ser null).
	 * @param targetName
	 *            el nom del nou fitxer.
	 * @param replace
	 *            indica si s'ha de sobreescriure el fitxer destí.
	 * @throws IOException si hi ha algun problema movent el fitxer.
	 */
	public void move(
			String sourceFolder,
			String sourceName,
			String targetFolder,
			String targetName,
			boolean replace) throws IOException {
		File source = new File(newFolderFile(sourceFolder, false), sourceName);
		File target = new File(newFolderFile(targetFolder, false), targetName);
		CopyOption[] options = replace ? new CopyOption[] { StandardCopyOption.REPLACE_EXISTING } : null;
		Files.move(
				source.toPath(),
				target.toPath(),
				options);
	}

	/**
	 * Indica si la funcionalitat està activa (si el mètode getFilesPath retorna alguna cosa).
	 *
	 * @return true si està activa o false en cas contrari.
	 */
	public boolean isFilesActive() {
		String files = getFilesPath();
		return files != null && !files.isEmpty();
	}

	protected File newFolderFile(String folder, boolean createIfNotExists) {
		String files = getFilesPath();
		String path;
		if (folder != null) {
			if (files.endsWith("/")) {
				path = files + folder;
			} else {
				path = files + "/" + folder;
			}
		} else {
			path = files;
		}
		File target = new File(path);
		if (createIfNotExists) target.mkdirs();
		return target;
	}

	protected abstract String getFilesPath();

}
