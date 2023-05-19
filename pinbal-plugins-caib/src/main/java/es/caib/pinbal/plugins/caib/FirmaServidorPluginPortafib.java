/**
 * 
 */
package es.caib.pinbal.plugins.caib;

import es.caib.pinbal.plugin.PropertiesHelper;
import es.caib.pinbal.plugins.FirmaServidorPlugin;
import es.caib.pinbal.plugins.SignaturaDades;
import es.caib.pinbal.plugins.SignaturaResposta;
import es.caib.pinbal.plugins.SistemaExternException;
import org.apache.commons.io.FileUtils;
import org.fundaciobit.plugins.signature.api.CommonInfoSignature;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signature.api.ITimeStampGenerator;
import org.fundaciobit.plugins.signature.api.PdfVisibleSignature;
import org.fundaciobit.plugins.signature.api.PolicyInfoSignature;
import org.fundaciobit.plugins.signature.api.SecureVerificationCodeStampInfo;
import org.fundaciobit.plugins.signature.api.SignaturesSet;
import org.fundaciobit.plugins.signature.api.SignaturesTableHeader;
import org.fundaciobit.plugins.signature.api.StatusSignature;
import org.fundaciobit.plugins.signature.api.StatusSignaturesSet;
import org.fundaciobit.plugins.signatureserver.api.ISignatureServerPlugin;
import org.fundaciobit.plugins.signatureserver.portafib.PortaFIBSignatureServerPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

/**
 * Implementació del plugin de firma en servidor emprant PortaFIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaServidorPluginPortafib implements FirmaServidorPlugin {

	private static final String PROPERTIES_BASE = "es.caib.pinbal.plugin.firmaservidor.portafib.";
	private static final String FIRMASERVIDOR_TMPDIR = "avacat_firmaservidor";

	private ISignatureServerPlugin plugin;
	private String tempDirPath;

	public FirmaServidorPluginPortafib() {
		super();
		Properties prop = PropertiesHelper.getProperties();
		plugin = new PortaFIBSignatureServerPlugin(PROPERTIES_BASE, prop);
		String tempDir = System.getProperty("java.io.tmpdir");
		final File base = new File(tempDir, FIRMASERVIDOR_TMPDIR);
		base.mkdirs();
		tempDirPath = base.getAbsolutePath();
	}

	@Override
	public SignaturaResposta signar(SignaturaDades dades) throws SistemaExternException {
		File sourceFile = null;
		File destFile = null;
		String uuid = UUID.randomUUID().toString();
		try {
			// Guarda el contingut en un arxiu temporal
			sourceFile = getArxiuTemporal(uuid, dades.getContingut());
			String sourcePath = sourceFile.getAbsolutePath();
			String destPath = sourcePath + "_PADES.pdf";
			String signType;
			int signMode;
			if (dades.getTipusFirma() == TipusFirma.CADES) {
				signType = FileInfoSignature.SIGN_TYPE_CADES;
				signMode = FileInfoSignature.SIGN_MODE_EXPLICIT; // Detached
			} else if (dades.getTipusFirma() == TipusFirma.XADES) {
				signType = FileInfoSignature.SIGN_TYPE_XADES;
				signMode = FileInfoSignature.SIGN_MODE_EXPLICIT; // Detached
			} else {
				// Per defecte es suposa el tipus de firma PAdES
				signType = FileInfoSignature.SIGN_TYPE_PADES;
				signMode = FileInfoSignature.SIGN_MODE_IMPLICIT; // Attached
			}
			boolean userRequiresTimeStamp = false;
			signFile(uuid, sourcePath, destPath, signType, signMode, dades.getMotiu(), dades.getIdioma(), userRequiresTimeStamp);
			destFile = new File(destPath);
			SignaturaResposta resposta = SignaturaResposta.builder()
					.contingut(FileUtils.readFileToByteArray(destFile))
					.nom(destFile.getName())
					.mime("application/pdf")
					.build();
			return resposta;
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		} finally {
			// Esborra els arxius temporals
			if (sourceFile != null && sourceFile.exists())
				sourceFile.delete();
			if (destFile != null && destFile.exists())
				destFile.delete();
		}
	}

	private File getArxiuTemporal(String uuid, byte[] contingut) throws IOException {
		// Crea l'arxiu temporal
		File fitxerTmp = new File(tempDirPath, uuid + "_original");
		fitxerTmp.getParentFile().mkdirs();
		// Escriu el contingut al fitxer temporal
		FileUtils.writeByteArrayToFile(fitxerTmp, contingut);
		return fitxerTmp;
	}

	private void signFile(
			String uuid,
			String sourcePath,
			String destPath,
			String signType,
			int signMode,
			String reason,
			String language,
			boolean userRequiresTimeStamp) throws Exception, FileNotFoundException, IOException {
		// Informació comú per a totes les signatures
		String filtreCertificats = "";
		String username = PropertiesHelper.getProperties().getProperty(PROPERTIES_BASE + "username", null);
		String administrationID = null; // No te sentit en API Firma En Servidor
		PolicyInfoSignature policyInfoSignature = null;
		CommonInfoSignature commonInfoSignature = new CommonInfoSignature(
				language,
				filtreCertificats,
				username,
				administrationID,
				policyInfoSignature);
		File source = new File(sourcePath);
		String fileName = source.getName();
		String location = PropertiesHelper.getProperties().getProperty(PROPERTIES_BASE + "location", "Palma");
		String signerEmail = PropertiesHelper.getProperties().getProperty(
				PROPERTIES_BASE + "signerEmail",
				"suport@caib.es");
		int signNumber = 1;
		String signAlgorithm = FileInfoSignature.SIGN_ALGORITHM_SHA1;
		int signaturesTableLocation = FileInfoSignature.SIGNATURESTABLELOCATION_WITHOUT;
		PdfVisibleSignature pdfInfoSignature = null;
		/*
		 * IRubricGenerator rubricGenerator = null; if
		 * (FileInfoSignature.SIGN_TYPE_PADES.equals(signType) && rubricGenerator !=
		 * null) { signaturesTableLocation =
		 * FileInfoSignature.SIGNATURESTABLELOCATION_LASTPAGE; PdfRubricRectangle
		 * pdfRubricRectangle = new PdfRubricRectangle(106, 650, 555, 710);
		 * pdfInfoSignature = new PdfVisibleSignature(pdfRubricRectangle,
		 * rubricGenerator); }
		 */
		final ITimeStampGenerator timeStampGenerator = null;
		// Valors per defecte
		final SignaturesTableHeader signaturesTableHeader = null;
		final SecureVerificationCodeStampInfo csvStampInfo = null;
		String signId = "999";
		FileInfoSignature fileInfo = new FileInfoSignature(
				signId,
				source,
				FileInfoSignature.PDF_MIME_TYPE,
				fileName,
				reason,
				location,
				signerEmail,
				signNumber,
				language,
				signType,
				signAlgorithm,
				signMode,
				signaturesTableLocation,
				signaturesTableHeader,
				pdfInfoSignature,
				csvStampInfo,
				userRequiresTimeStamp,
				timeStampGenerator);
		final String signaturesSetID = String.valueOf(System.currentTimeMillis());
		SignaturesSet signaturesSetRequest = new SignaturesSet(
				signaturesSetID + "_" + uuid,
				commonInfoSignature,
				new FileInfoSignature[] { fileInfo });
		// Signa el document
		String timestampUrlBase = null;
		SignaturesSet signaturesSetResponse = plugin.signDocuments(signaturesSetRequest, timestampUrlBase);
		StatusSignaturesSet signaturesSetStatus = signaturesSetResponse.getStatusSignaturesSet();
		if (signaturesSetStatus.getStatus() != StatusSignaturesSet.STATUS_FINAL_OK) {
			// Error en el procés de firma
			String exceptionMessage = "Error en la firma de servidor: [" + signaturesSetStatus.getStatus() + "] " +
					signaturesSetStatus.getErrorMsg();
			if (signaturesSetStatus.getErrorException() != null) {
				throw new SistemaExternException(exceptionMessage, signaturesSetStatus.getErrorException());
			} else {
				throw new SistemaExternException(exceptionMessage);
			}
		} else {
			FileInfoSignature fis = signaturesSetResponse.getFileInfoSignatureArray()[0];
			StatusSignature status = fis.getStatusSignature();
			if (status.getStatus() != StatusSignaturesSet.STATUS_FINAL_OK) {
				// Error en el document a firmar
				String exceptionMessage = "Error al firmar en servidor el document (status=" + status.getStatus() +
						"): " + status.getErrorMsg();
				if (signaturesSetStatus.getErrorException() != null) {
					throw new SistemaExternException(exceptionMessage, signaturesSetStatus.getErrorException());
				} else {
					throw new SistemaExternException(exceptionMessage);
				}
			} else {
				// Document firmat correctament
				status.getSignedData().renameTo(new File(destPath));
			}
		}
	}

}
