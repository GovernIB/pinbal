package es.caib.pinbal.core.dto.arxiu;


import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.DocumentTipus;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaPerfil;
import es.caib.plugins.arxiu.api.FirmaTipus;



public class ArxiuConversions {

	public static DocumentNtiEstadoElaboracionEnumDto getEstatElaboracio(Document document) {
		return getEstatElaboracio(document.getMetadades().getEstatElaboracio());
	}
	
	public static DocumentNtiEstadoElaboracionEnumDto getEstatElaboracio(DocumentEstatElaboracio documentEstatElaboracio) {
		DocumentNtiEstadoElaboracionEnumDto estatElaboracio = null;
	
		switch (documentEstatElaboracio) {
		case ORIGINAL:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE01;
			break;
		case COPIA_CF:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE02;
			break;
		case COPIA_DP:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE03;
			break;
		case COPIA_PR:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE04;
			break;
		case ALTRES:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE99;
			break;
		}
		return estatElaboracio;
	}
	
	public static DocumentEstatElaboracio getDocumentEstatElaboracio(DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
		DocumentEstatElaboracio estatElaboracio = null;
		switch (ntiEstadoElaboracion) {
		case EE01:
			estatElaboracio = DocumentEstatElaboracio.ORIGINAL;
			break;
		case EE02:
			estatElaboracio = DocumentEstatElaboracio.COPIA_CF;
			break;
		case EE03:
			estatElaboracio = DocumentEstatElaboracio.COPIA_DP;
			break;
		case EE04:
			estatElaboracio = DocumentEstatElaboracio.COPIA_PR;
			break;
		case EE99:
			estatElaboracio = DocumentEstatElaboracio.ALTRES;
			break;
		}
		
		return estatElaboracio;
	}

	public static NtiOrigenEnumDto getOrigen(Document documentArxiu) {
		return getOrigen(documentArxiu.getMetadades().getOrigen());
	}
	
	public static NtiOrigenEnumDto getOrigen(ContingutOrigen contingutOrigen) {
		NtiOrigenEnumDto origen = null;
	
		switch (contingutOrigen) {
		case CIUTADA:
			origen = NtiOrigenEnumDto.O0;
			break;
		case ADMINISTRACIO:
			origen = NtiOrigenEnumDto.O1;
			break;
		}
		return origen;
	}
	
	public static ContingutOrigen getOrigen(NtiOrigenEnumDto ntiOrigen) {
		ContingutOrigen origen = null;
		if (ntiOrigen != null) {
			switch (ntiOrigen) {
			case O0:
				origen = ContingutOrigen.CIUTADA;
				break;
			case O1:
				origen = ContingutOrigen.ADMINISTRACIO;
				break;
			}
		}
		return origen;
	}
	
	@SuppressWarnings("incomplete-switch")
	public static DocumentNtiTipoDocumentalEnumDto getTipusDocumentalEnum(DocumentTipus documentTipus) {
		DocumentNtiTipoDocumentalEnumDto tipusDocumental = null;
		
		if (documentTipus != null) {
			switch (documentTipus) {
			case RESOLUCIO:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD01;
				break;
			case ACORD:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD02;
				break;
			case CONTRACTE:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD03;
				break;
			case CONVENI:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD04;
				break;
			case DECLARACIO:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD05;
				break;
			case COMUNICACIO:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD06;
				break;
			case NOTIFICACIO:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD07;
				break;
			case PUBLICACIO:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD08;
				break;
			case JUSTIFICANT_RECEPCIO:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD09;
				break;
			case ACTA:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD10;
				break;
			case CERTIFICAT:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD11;
				break;
			case DILIGENCIA:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD12;
				break;
			case INFORME:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD13;
				break;
			case SOLICITUD:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD14;
				break;
			case DENUNCIA:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD15;
				break;
			case ALEGACIO:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD16;
				break;
			case RECURS:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD17;
				break;
			case COMUNICACIO_CIUTADA:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD18;
				break;
			case FACTURA:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD19;
				break;
			case ALTRES_INCAUTATS:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD20;
				break;
			case ALTRES:
				tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD99;
				break;
			}
		}
	
		return tipusDocumental;
	}

	@SuppressWarnings("incomplete-switch")
	public static String getTipusDocumental(Document document) {
		String tipusDocumental = null;
	
		if (document.getMetadades().getTipusDocumental() != null) {
			switch (document.getMetadades().getTipusDocumental()) {
			case RESOLUCIO:
				tipusDocumental = "TD01";
				break;
			case ACORD:
				tipusDocumental = "TD02";
				break;
			case CONTRACTE:
				tipusDocumental = "TD03";
				break;
			case CONVENI:
				tipusDocumental = "TD04";
				break;
			case DECLARACIO:
				tipusDocumental = "TD05";
				break;
			case COMUNICACIO:
				tipusDocumental = "TD06";
				break;
			case NOTIFICACIO:
				tipusDocumental = "TD07";
				break;
			case PUBLICACIO:
				tipusDocumental = "TD08";
				break;
			case JUSTIFICANT_RECEPCIO:
				tipusDocumental = "TD09";
				break;
			case ACTA:
				tipusDocumental = "TD10";
				break;
			case CERTIFICAT:
				tipusDocumental = "TD11";
				break;
			case DILIGENCIA:
				tipusDocumental = "TD12";
				break;
			case INFORME:
				tipusDocumental = "TD13";
				break;
			case SOLICITUD:
				tipusDocumental = "TD14";
				break;
			case DENUNCIA:
				tipusDocumental = "TD15";
				break;
			case ALEGACIO:
				tipusDocumental = "TD16";
				break;
			case RECURS:
				tipusDocumental = "TD17";
				break;
			case COMUNICACIO_CIUTADA:
				tipusDocumental = "TD18";
				break;
			case FACTURA:
				tipusDocumental = "TD19";
				break;
			case ALTRES_INCAUTATS:
				tipusDocumental = "TD20";
				break;
			case ALTRES:
				tipusDocumental = "TD99";
				break;
			}
		} else if (document.getMetadades().getTipusDocumentalAddicional() != null) {
			tipusDocumental = document.getMetadades().getTipusDocumentalAddicional();
		}
		if (tipusDocumental == null && document.getMetadades().getTipusDocumental() != null)
			tipusDocumental = document.getMetadades().getTipusDocumental().toString();
	
		return tipusDocumental;
	}
	
	public static void setTipusDocumental(DocumentMetadades metadades, String ntiTipoDocumental) {
		DocumentTipus tipusDocumental = null;
		String tipusDocumentalAddicional = null;
		switch (ntiTipoDocumental) {
		case "TD01":
			tipusDocumental = DocumentTipus.RESOLUCIO;
			break;
		case "TD02":
			tipusDocumental = DocumentTipus.ACORD;
			break;
		case "TD03":
			tipusDocumental = DocumentTipus.CONTRACTE;
			break;
		case "TD04":
			tipusDocumental = DocumentTipus.CONVENI;
			break;
		case "TD05":
			tipusDocumental = DocumentTipus.DECLARACIO;
			break;
		case "TD06":
			tipusDocumental = DocumentTipus.COMUNICACIO;
			break;
		case "TD07":
			tipusDocumental = DocumentTipus.NOTIFICACIO;
			break;
		case "TD08":
			tipusDocumental = DocumentTipus.PUBLICACIO;
			break;
		case "TD09":
			tipusDocumental = DocumentTipus.JUSTIFICANT_RECEPCIO;
			break;
		case "TD10":
			tipusDocumental = DocumentTipus.ACTA;
			break;
		case "TD11":
			tipusDocumental = DocumentTipus.CERTIFICAT;
			break;
		case "TD12":
			tipusDocumental = DocumentTipus.DILIGENCIA;
			break;
		case "TD13":
			tipusDocumental = DocumentTipus.INFORME;
			break;
		case "TD14":
			tipusDocumental = DocumentTipus.SOLICITUD;
			break;
		case "TD15":
			tipusDocumental = DocumentTipus.DENUNCIA;
			break;
		case "TD16":
			tipusDocumental = DocumentTipus.ALEGACIO;
			break;
		case "TD17":
			tipusDocumental = DocumentTipus.RECURS;
			break;
		case "TD18":
			tipusDocumental = DocumentTipus.COMUNICACIO_CIUTADA;
			break;
		case "TD19":
			tipusDocumental = DocumentTipus.FACTURA;
			break;
		case "TD20":
			tipusDocumental = DocumentTipus.ALTRES_INCAUTATS;
			break;
		case "TD99":
			tipusDocumental = DocumentTipus.ALTRES;
			break;
		default:
			tipusDocumentalAddicional = ntiTipoDocumental;
		}
		metadades.setTipusDocumental(tipusDocumental);
		metadades.setTipusDocumentalAddicional(tipusDocumentalAddicional);
		
	}


	public static DocumentNtiTipoFirmaEnumDto getNtiTipoFirma(Document documentArxiu) {
		DocumentNtiTipoFirmaEnumDto ntiTipoFirma = null;
		if (documentArxiu.getFirmes() != null && !documentArxiu.getFirmes().isEmpty()) {
			FirmaTipus firmaTipus = null;
			for (Firma firma: documentArxiu.getFirmes()) {
				if (firma.getTipus() != FirmaTipus.CSV) {
					firmaTipus = firma.getTipus();
					break;
				}
			}
			if (firmaTipus != null) {
				switch (firmaTipus) {
				case CSV:
					ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF01;
					break;
				case XADES_DET:
					ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF02;
					break;
				case XADES_ENV:
					ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF03;
					break;
				case CADES_DET:
					ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF04;
					break;
				case CADES_ATT:
					ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF05;
					break;
				case PADES:
					ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF06;
					break;
				case SMIME:
					ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF07;
					break;
				case ODT:
					ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF08;
					break;
				case OOXML:
					ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF09;
					break;
				}
			}
		}
		return ntiTipoFirma;
	}
	
	public static ArxiuFirmaTipusEnumDto toArxiuFirmaTipus(String tipusFirmaEni) {
		
		switch (tipusFirmaEni) {
			case "TF01":
				return ArxiuFirmaTipusEnumDto.CSV;
			case "TF02":
				return ArxiuFirmaTipusEnumDto.XADES_DET;
			case "TF03":
				return ArxiuFirmaTipusEnumDto.XADES_ENV;
			case "TF04":
				return ArxiuFirmaTipusEnumDto.CADES_DET;
			case "TF05":
				return ArxiuFirmaTipusEnumDto.CADES_ATT;
			case "TF06":
				return ArxiuFirmaTipusEnumDto.PADES;
			case "TF07":
				return ArxiuFirmaTipusEnumDto.SMIME;
			case "TF08":
				return ArxiuFirmaTipusEnumDto.ODT;
			case "TF09":
				return ArxiuFirmaTipusEnumDto.OOXML;
			default:
				return null;
		}
	}



	public static FirmaTipus getFirmaTipus(ArxiuFirmaTipusEnumDto arxiuFirmaTipusEnum) {
	
		FirmaTipus firmaTipus = null;
		
		if (arxiuFirmaTipusEnum != null) {
			switch(arxiuFirmaTipusEnum) {
			case CSV:
				firmaTipus = FirmaTipus.CSV;
				break;
			case XADES_DET:
				firmaTipus = FirmaTipus.XADES_DET;
				break;
			case XADES_ENV:
				firmaTipus = FirmaTipus.XADES_ENV;
				break;
			case CADES_DET:
				firmaTipus = FirmaTipus.CADES_DET;
				break;
			case CADES_ATT:
				firmaTipus = FirmaTipus.CADES_ATT;
				break;
			case PADES:
				firmaTipus = FirmaTipus.PADES;
				break;
			case SMIME:
				firmaTipus = FirmaTipus.SMIME;
				break;
			case ODT:
				firmaTipus = FirmaTipus.ODT;
				break;
			case OOXML:
				firmaTipus = FirmaTipus.OOXML;
				break;
			}
		}
		
		return firmaTipus;
	
	}

	public static FirmaPerfil getFirmaPerfil(ArxiuFirmaPerfilEnumDto arxiuFirmaPerfilEnum) {
	
	
		FirmaPerfil firmaPerfil  = null;
		
		if (arxiuFirmaPerfilEnum != null) {
			switch(arxiuFirmaPerfilEnum) {
			case BES:
				firmaPerfil = FirmaPerfil.BES;
				break;
			case EPES:
				firmaPerfil = FirmaPerfil.EPES;
				break;
			case LTV:
				firmaPerfil = FirmaPerfil.LTV;
				break;
			case T:
				firmaPerfil = FirmaPerfil.T;
				break;
			case C:
				firmaPerfil = FirmaPerfil.C;
				break;
			case X:
				firmaPerfil = FirmaPerfil.X;
				break;
			case XL:
				firmaPerfil = FirmaPerfil.XL;
				break;
			case A:
				firmaPerfil = FirmaPerfil.A;
				break;
			case BASIC:
				firmaPerfil = FirmaPerfil.BES;
				break;
			case Basic:
				firmaPerfil = FirmaPerfil.BES;
				break;
			case BASELINE_B_LEVEL:
				firmaPerfil = FirmaPerfil.BASELINE_B_LEVEL;
				break;
			case BASELINE_LTA_LEVEL:
				firmaPerfil = FirmaPerfil.BASELINE_LTA_LEVEL;
				break;
			case BASELINE_LT_LEVEL:
				firmaPerfil = FirmaPerfil.BASELINE_LT_LEVEL;
				break;
			case BASELINE_T:
				firmaPerfil = FirmaPerfil.BASELINE_T;
				break;
			case BASELINE_T_LEVEL:
				firmaPerfil = FirmaPerfil.BASELINE_T_LEVEL;
				break;
			case LTA:
				firmaPerfil = FirmaPerfil.LTA;
				break;
			}
		}
		
		return firmaPerfil;
	}








	public static ArxiuFirmaPerfilEnumDto toArxiuFirmaPerfilEnum(String perfil) {
	
		ArxiuFirmaPerfilEnumDto perfilFirma = null;
		switch (perfil) {
		case "AdES-BES":
			perfilFirma = ArxiuFirmaPerfilEnumDto.BES;
			break;
		case "AdES-EPES":
			perfilFirma = ArxiuFirmaPerfilEnumDto.EPES;
			break;
		case "AdES-T":
			perfilFirma = ArxiuFirmaPerfilEnumDto.T;
			break;
		case "AdES-C":
			perfilFirma = ArxiuFirmaPerfilEnumDto.C;
			break;
		case "AdES-X":
			perfilFirma = ArxiuFirmaPerfilEnumDto.X;
			break;
		case "AdES-XL":
			perfilFirma = ArxiuFirmaPerfilEnumDto.XL;
			break;
		case "AdES-A":
			perfilFirma = ArxiuFirmaPerfilEnumDto.A;
			break;
		case "PAdES-LTV":
			perfilFirma = ArxiuFirmaPerfilEnumDto.LTV;
			break;
		case "PAdES-Basic":
			perfilFirma = ArxiuFirmaPerfilEnumDto.Basic;
			break;
		}
		return perfilFirma;
	}

	public static ArxiuFirmaTipusEnumDto toArxiuFirmaTipusEnum(String tipus, String format) {
	
		ArxiuFirmaTipusEnumDto tipusFirma = null;
		if (tipus.equals("PAdES") || format.equals("implicit_enveloped/attached")) {
			tipusFirma = ArxiuFirmaTipusEnumDto.PADES;
		} else if (tipus.equals("XAdES") && format.equals("explicit/detached")) {
			tipusFirma = ArxiuFirmaTipusEnumDto.XADES_DET;
		} else if (tipus.equals("XAdES") && format.equals("implicit_enveloping/attached")) {
			tipusFirma = ArxiuFirmaTipusEnumDto.XADES_ENV;
		} else if (tipus.equals("CAdES") && format.equals("explicit/detached")) {
			tipusFirma = ArxiuFirmaTipusEnumDto.CADES_DET;
		} else if (tipus.equals("CAdES") && format.equals("implicit_enveloping/attached")) {
			tipusFirma = ArxiuFirmaTipusEnumDto.CADES_ATT;
		}
		return tipusFirma;
	}



	public static String[] getNtiCsv(Document documentArxiu) {
		String [] ntiCsv = new String[2]; 
		if (documentArxiu.getFirmes() != null && !documentArxiu.getFirmes().isEmpty()) {
			for (Firma firma : documentArxiu.getFirmes()) {
				if (firma.getTipus() == FirmaTipus.CSV) {
					ntiCsv[0] = firma.getCsvRegulacio();
					ntiCsv[1] = firma.getContingut() != null ? new String(firma.getContingut()) : null;
				}
			}
		}
		return ntiCsv;
	}
	
	
	



}
