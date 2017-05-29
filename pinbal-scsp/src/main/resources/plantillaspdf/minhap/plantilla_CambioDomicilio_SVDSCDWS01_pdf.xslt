<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0" xmlns:str="http://www.str.com" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:functx="http://www.functx.com" exclude-result-prefixes="str functx">

	<xsl:output version="1.0" method="xml" encoding="UTF-8"		indent="no" />
	<xsl:param name="SV_OutputFormat" select="'PDF'" />
	<xsl:param name="tituloConsulta"/>
	<xsl:param name="justificanteTituloHeader"/>
	<xsl:param name="justificanteTexto"/>
	<xsl:param name="responsabilidadTituloFooter"/>
	<xsl:param name="responsabilidadTexto"/>
	<xsl:param name="acreditacionElectronicaTexto"/>
	<xsl:param name="nombreCertificadoAplicacion"/>
	<xsl:param name="idPeticion"/>
	<xsl:param name="descripcionServicio"/>
	<xsl:param name="fechaStr"/>
	<xsl:param name="nifFuncionario"/>
	<xsl:param name="nombreFuncionario"/>
	<xsl:param name="organismoFuncionario"/>
	
	<xsl:param name="codProcedimiento"/>
	<xsl:param name="nomProcedimiento"/>	
	<xsl:param name="unidadTramitadora"/>
	<xsl:param name="fechaRespuestaServicio"/>
	<xsl:param name="horaRespuestaServicio"/>
	<xsl:param name="fechaGeneracionPDF"/>
	<xsl:param name="horaGeneracionPDF"/>
	<xsl:param name="idTransmision"/>
	<xsl:param name="logoDerecha"/>
	<xsl:param name="logoIzquierda"/>
	
	<xsl:param name="nomApellidosPeticion"/>
	<xsl:param name="tipoDocPeticion"/>
	<xsl:param name="docPeticion"/>
	<xsl:param name="consentimiento"/>
	<xsl:param name="idExpediente"/>
	<xsl:param name="finalidad"/>
 
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoLocalidadCodigoProvincia"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoLocalidadCodigoMunicipio"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoLocalidadCodigoUnidadPoblacional"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoCodigoPostal"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoViaTipoVia"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoViaNombreVia"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionNumero"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumero"/>

	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionNumeroSuperior"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumeroSuperior"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionKilometro"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionHectometro"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionBloque"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionPortal"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionEscalera"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionPlanta"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionPuerta"/>
	<xsl:param name="ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionTipoLocal"/> 
		

	<xsl:template match="/">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="default-page" page-height="11in" page-width="9.5in" margin-top="0.2in" margin-right="0.9in" margin-left="0.8in">
					<fo:region-body margin-top="82" margin-bottom="120" margin-left="0.2in"/>
					<fo:region-before extent="300"/>
					<fo:region-after region-name="xsl-region-after" extent="125"/>
					<fo:region-start region-name="my-left-sidebar" reference-orientation="90" extent="0.2in"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="default-page" initial-page-number="1" format="1">
				<xsl:call-template name="header"/>
				<xsl:call-template name="footer"/>
				<xsl:call-template name="left"/>
				<xsl:call-template name="body"/>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template name="header">
		<fo:static-content flow-name="xsl-region-before">
			<fo:block border-style="solid" border-width="1px" border-color="#2F4F4F">
				<fo:table table-layout="fixed" border-width="1px 1px 1px 1px" width="100%" border-spacing="2pt">
					<fo:table-column column-number="1" column-width="5cm"/>
					<fo:table-column column-number="2" column-width="proportional-column-width(2)"/>
					<fo:table-column column-number="3" column-width="5cm"/>
					<fo:table-body start-indent="0pt">
						<fo:table-row >
							<fo:table-cell padding="1" display-align="center" text-align="center">
								<fo:block display-align="center" text-align="center" >
									<fo:external-graphic content-width="scale-to-fit" content-height="scale-to-fit" width="4cm" height="2cm" scaling="uniform" src="url('data:image/jpeg;base64,{$logoIzquierda}')"/>			
								</fo:block>
								
							</fo:table-cell>
							<fo:table-cell padding="25px 0px 0px 10px" border-width="1px 1px 1px 1px">
								<fo:block font-size="10.5pt" font-family="Arial,Helvetica,sans-serif" font-weight="bold" line-height="13pt" color="#2F4F4F" space-after.optimum="2pt" display-align="center" text-align="center">
									<xsl:value-of select="$justificanteTituloHeader"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell padding="1" >
								<fo:block display-align="after" text-align="right">
									<!-- fo:external-graphic content-width="scale-to-fit" content-height="100%" width="100%" scaling="uniform" src="url('data:image/jpeg;base64,{$logoDerecha}')"/ -->
									<fo:external-graphic  content-width="scale-to-fit" content-height="scale-to-fit" width="4cm" height="2cm"  scaling="uniform" src="url('data:image/jpeg;base64,{$logoDerecha}')"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
			</fo:block>
		</fo:static-content>
	</xsl:template>
	<xsl:template name="footer">
		<fo:static-content flow-name="xsl-region-after">
			<fo:block text-align="left" margin-top="25pt">
				<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" text-align="left" line-height="10pt" space-after.optimum="1pt" display-align="after" color="#2F4F4F">
					<fo:inline font-weight="bold">
						<xsl:text>Justificante firmado electrónicamente con el certificado </xsl:text>
						<xsl:value-of select="$nombreCertificadoAplicacion"/>
						<xsl:text> el día </xsl:text>
						<xsl:value-of select="$fechaGeneracionPDF"/>
						<xsl:text> a las </xsl:text>
						<xsl:value-of select="$horaGeneracionPDF"/>
						<xsl:text>.</xsl:text>
						<fo:leader leader-pattern="space"/>
					</fo:inline>
				</fo:block>
			</fo:block>
			<fo:block>
				<fo:leader leader-pattern="space"/>
			</fo:block>
			<fo:table>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="2" padding="0" display-align="center">
							<fo:block>
								<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" font-style="italic" text-align="justify" color="#2F4F4F">
									<fo:inline font-weight="bold">
										<xsl:value-of select="$responsabilidadTituloFooter"/>
									</fo:inline>
									<fo:inline font-weight="normal">
										<xsl:value-of select="$responsabilidadTexto"/>
									</fo:inline>
								</fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="2" padding="0" display-align="center">
							<fo:block>
								<fo:block text-align="center">
									<fo:leader leader-pattern="rule" rule-thickness="1" leader-length="100%" color="#2F4F4F"/>
								</fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" text-align="center" color="#2F4F4F">
				Página 
				<fo:page-number/>
				de
				<fo:page-number-citation ref-id="last-page"/>
			</fo:block>
		</fo:static-content>
	</xsl:template>
	<xsl:template name="left">
		<fo:static-content flow-name="my-left-sidebar">
			<fo:block text-align="end" font-size="7pt" font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="100pt" font-weight="bold" color="#DC143C">
				<fo:inline>
					<xsl:value-of select="$justificanteTexto"/>
				</fo:inline>
			</fo:block>
		</fo:static-content>
	</xsl:template>
	<xsl:template name="body">
		<fo:flow flow-name="xsl-region-body">
			<xsl:apply-templates select="//*[local-name()='DatosEspecificos']"/>
			<fo:block id="last-page"/>
		</fo:flow>
	</xsl:template>
	<xsl:template match="//*[local-name()='DatosEspecificos']">
		<xsl:variable name="datosEspecificos" select="//*[local-name()='TransmisionDatos']/*[local-name()='DatosEspecificos']"/>
		<xsl:variable name="titular" select="//*[local-name()='TransmisionDatos']/*[local-name()='DatosGenericos']/*[local-name()='Titular']"/> 
		<fo:block margin-top="8pt" text-align="justify">
			<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" font-weight="bold" color="#2F4F4F">
				<fo:inline>
					<xsl:text>El organismo </xsl:text>
					<fo:inline font-style="italic"><xsl:value-of select="$organismoFuncionario"/></fo:inline>
					<xsl:text> realizó la siguente consulta al servicio </xsl:text>
					<fo:inline font-style="italic">
						<xsl:value-of select="$descripcionServicio"/>
					</fo:inline>
					<xsl:text> el día  </xsl:text>
					<xsl:value-of select="$fechaRespuestaServicio"/>
					<xsl:text> a las </xsl:text>
					<xsl:value-of select="$horaRespuestaServicio"/>
					<xsl:text>. </xsl:text>
				</fo:inline>
			</fo:block>
			<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" font-weight="bold" color="#2F4F4F">
				<fo:inline>
					<xsl:if test="string($nifFuncionario) != ''">
						<xsl:text>Realizada por el funcionario </xsl:text>
						<xsl:value-of select="$nombreFuncionario"/>
						<xsl:text> con DNI </xsl:text>
						<xsl:value-of select="$nifFuncionario"/>
						<xsl:if test="string($unidadTramitadora) != ''">
							<xsl:text> y perteneciente a la unidad tramitadora </xsl:text>
							<fo:inline font-style="italic"><xsl:value-of select="$unidadTramitadora"/></fo:inline>
						</xsl:if>
						<xsl:text>.</xsl:text>
					</xsl:if>
				</fo:inline>
			</fo:block>
		</fo:block>
		<fo:block>
			<fo:leader leader-pattern="space"/>
		</fo:block>
		<fo:block margin-top="9pt" text-align="center">
			<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" color="#2F4F4F">
				<fo:inline font-weight="bold">
					<xsl:value-of select="$acreditacionElectronicaTexto"/>
				</fo:inline>
				<fo:leader leader-pattern="space"/>
			</fo:block>
		</fo:block>
		<fo:block margin-top="2pt" text-align="center" font-family="Arial,sans-serif" font-size="8pt" line-height="10pt" space-after.optimum="1pt" display-align="after" color="#2F4F4F">
			<fo:inline font-weight="bold">
				<xsl:text>Identificador de Petición:</xsl:text>
			</fo:inline>
			<fo:inline font-weight="bold">
				<xsl:value-of select="$idPeticion"/>
			</fo:inline>
		</fo:block>
		<fo:block text-align="center" font-family="Arial,sans-serif" font-size="8pt" line-height="10pt" space-after.optimum="1pt" display-align="after" color="#2F4F4F">
			<fo:inline font-weight="bold">
				<xsl:text>Identificador de Transmisión:</xsl:text>
			</fo:inline>
			<fo:inline font-weight="bold">
				<xsl:value-of select="$idTransmision"/>
			</fo:inline>
		</fo:block>
		<fo:block text-align="left" margin-top="20pt">
			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
				<fo:inline font-weight="bold">
					<xsl:text>Datos de Consulta</xsl:text>
				</fo:inline>
			</fo:block>
		</fo:block>
			<!-- DATOS DE LA CONSULTA DE IDENTIDAD -->
		<fo:block border-style="solid" border="1px 0 0 0" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-after="1mm">
			<fo:table table-layout="fixed" border-spacing="5pt" space-before="2mm">
				<fo:table-column column-width="proportional-column-width(0.8)"/>
				<fo:table-column column-width="proportional-column-width(0.8)"/>
				<fo:table-column column-width="proportional-column-width(0.85)"/>
				<fo:table-column column-width="proportional-column-width(0.75)"/>	
				<fo:table-column column-width="proportional-column-width(1.15)"/>
				<fo:table-column column-width="proportional-column-width(2.3)"/>
				<fo:table-body start-indent="0pt">
					<fo:table-row>
					   <!-- TIPO DOCUMENTACION -->
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Tipo Doc:</xsl:text>
									</fo:inline>
								</fo:block>							
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$tipoDocPeticion"/>
								</fo:block>							
						</fo:table-cell>
						
						 <!-- DOCUMENTACION -->
						<fo:table-cell padding="2pt" display-align="center">							
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Documentación:</xsl:text>
									</fo:inline>
								</fo:block>							
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$docPeticion"/>	
								</fo:block>							
						</fo:table-cell>
						
						<!-- NOMBRE Y APELLIDOS -->
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Nombre y Apellidos:</xsl:text>
									</fo:inline>
								</fo:block>							
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										<xsl:value-of select="$nomApellidosPeticion"/>								
								</fo:block>							
						</fo:table-cell>
					</fo:table-row>
					
					<fo:table-row>
					   <!-- CONSENTIMIENTO -->
						<fo:table-cell padding="2pt" display-align="center"  >							
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Consentimiento:</xsl:text>
									</fo:inline>
								</fo:block>							
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$consentimiento"/>
								</fo:block>							
						</fo:table-cell>
						
						 <!-- NUMERO EXPEDIENTE -->
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Nº Expediente:</xsl:text>
									</fo:inline>
								</fo:block>							
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$idExpediente"/>
								</fo:block>							
						</fo:table-cell>
						
						<!-- FINALIDAD -->
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Finalidad:</xsl:text>
									</fo:inline>
								</fo:block>							
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										<xsl:value-of select="$finalidad"/>								
								</fo:block>							
						</fo:table-cell>
					</fo:table-row>					
				</fo:table-body>
			</fo:table>
			
			<fo:table table-layout="auto" border-spacing="5pt" space-before="0mm">
				<fo:table-body start-indent="0pt">
					<fo:table-row>
					   <!-- Código Procedimiento -->
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Cod. Procedimiento:     </xsl:text>
									</fo:inline>
									<xsl:value-of select="$codProcedimiento"/>
								</fo:block>													
						</fo:table-cell>							
					</fo:table-row>	
				</fo:table-body>
			</fo:table>	
				
			<fo:block margin-top="40pt" text-align="left" font-family="Arial,sans-serif" font-size="10pt" line-height="10pt" space-after.optimum="1pt" display-align="after">
						<fo:inline font-weight="bold">
							<xsl:text>Domicilio Comunicado </xsl:text> 
						</fo:inline>
					</fo:block>
			<fo:table table-layout="fixed" border-spacing="5pt" space-before="0mm">
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-column column-width="proportional-column-width(1)"/>	
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-body start-indent="0pt">
					<fo:table-row> 
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Código Provincia</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoLocalidadCodigoProvincia"/>
								</fo:block>	
						</fo:table-cell> 
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Código Municipio:</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoLocalidadCodigoMunicipio"/>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Código Unidad Poblacional:</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoLocalidadCodigoUnidadPoblacional"/>
								</fo:block>	
						</fo:table-cell>
					</fo:table-row> 
				</fo:table-body>
			</fo:table>
			<fo:table table-layout="fixed" border-spacing="5pt" space-before="2mm">
								<fo:table-column column-width="proportional-column-width(0.9)"/>
								<fo:table-column column-width="proportional-column-width(4.1)"/>
								<fo:table-body start-indent="0pt">
									<!-- Fila1 Hijo -->
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Dirección:</xsl:text>
														<!-- fo:leader leader-pattern="space" / -->
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoViaTipoVia"/>
														<xsl:text> </xsl:text>
														<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoViaNombreVia"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<!-- Fila2 Hijo -->
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text> </xsl:text>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell> 
										 
								
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:if test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionNumero != '0'" >
															<xsl:text> </xsl:text>
															<fo:inline font-weight="bold">
																<xsl:text>Nº: </xsl:text>
															</fo:inline>
															<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionNumero"/>
															<xsl:choose>
																	<xsl:when test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumero ='A' 
																	or $ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumero ='a'">
																		<xsl:text>Anterior</xsl:text>
																	</xsl:when>
																	<xsl:when test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumero='B' or
																	 $ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumero ='b'">
																		<xsl:text>Bis</xsl:text>
																	</xsl:when>
																	<xsl:when test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumero ='T' or
																	 $ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumero ='t'">
																		<xsl:text>Tribis</xsl:text>
																	</xsl:when>
																	<xsl:when test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumero ='M' or
																	 $ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumero ='m'">
																		<xsl:text>Modificado</xsl:text>
																	</xsl:when>
																	<xsl:otherwise>
																		<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumero"/> 
																	</xsl:otherwise>
														</xsl:choose>
															
														</xsl:if>
														<xsl:if test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionNumeroSuperior != '0' ">
															<xsl:text> </xsl:text>
															<fo:inline font-weight="bold">
																<xsl:text>NºSuperior : </xsl:text>
															</fo:inline>
															<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionNumeroSuperior"/>
															 <xsl:text> </xsl:text>
															 <xsl:choose>
																	<xsl:when test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumeroSuperior ='A' 
																	or $ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumeroSuperior ='a'">
																		<xsl:text>Anterior</xsl:text>
																	</xsl:when>
																	<xsl:when test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumeroSuperior='B' or
																	 $ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumeroSuperior ='b'">
																		<xsl:text>Bis</xsl:text>
																	</xsl:when>
																	<xsl:when test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumeroSuperior ='T' or
																	 $ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumeroSuperior ='t'">
																		<xsl:text>Tribis</xsl:text>
																	</xsl:when>
																	<xsl:when test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumeroSuperior ='M' or
																	 $ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumeroSuperior ='m'">
																		<xsl:text>Modificado</xsl:text>
																	</xsl:when>
																	<xsl:otherwise>
																		<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionCalificadorNumeroSuperior"/> 
																	</xsl:otherwise>
														</xsl:choose>
														</xsl:if>
														<xsl:if test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionBloque != ''">
															<xsl:text> </xsl:text>
															<fo:inline font-weight="bold">
																<xsl:text>Bloque: </xsl:text>
															</fo:inline>
															<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionBloque"/>
														</xsl:if>
														<xsl:if test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionPortal != ''">
															<xsl:text> </xsl:text>
															<fo:inline font-weight="bold">
																<xsl:text>Portal: </xsl:text>
															</fo:inline>
															<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionPortal"/>
														</xsl:if>
														<xsl:if test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionEscalera != ''">
															<xsl:text> </xsl:text>
															<fo:inline font-weight="bold">
																<xsl:text>Escalera: </xsl:text>
															</fo:inline>
															<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionEscalera"/>
														</xsl:if>
														<xsl:if test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionPlanta != ''">
															<xsl:text> </xsl:text>
															<fo:inline font-weight="bold">
																<xsl:text>Planta: </xsl:text>
															</fo:inline>
															<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionPlanta"/>
														</xsl:if>
														<xsl:if test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionPuerta != ''">
															<xsl:text> </xsl:text>
															<fo:inline font-weight="bold">
																<xsl:text>Puerta: </xsl:text>
															</fo:inline>
															<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionPuerta"/>
														</xsl:if>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<!-- Fila3 Hijo -->
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text> </xsl:text>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<xsl:if test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoCodigoPostal != ''">
														<xsl:text> </xsl:text>
														<fo:inline font-weight="bold">
															<xsl:text>Cod Postal: </xsl:text>
														</fo:inline>
														<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoCodigoPostal"/>
													</xsl:if>
													<xsl:if  test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionKilometro != '0' ">
														<xsl:text> </xsl:text>
														<fo:inline font-weight="bold">
															<xsl:text>Kmt: </xsl:text>
														</fo:inline>
														<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionKilometro"/>
													</xsl:if>
													<xsl:if  test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionHectometro != '0' ">
														<xsl:text> </xsl:text>
														<fo:inline font-weight="bold">
															<xsl:text>Hmt: </xsl:text>
														</fo:inline>
														<xsl:value-of select="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionHectometro"/>
													</xsl:if>
													
													<xsl:if  test="$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionHectometro = '0' and 
													$ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionKilometro = '0' and
													   $ComunicacionDatosVolanteVolanteEmpadronamientoVolanteEmpadronamientoDatosFirmadosDireccionEmpadronamientoNumeracionNumero=''">
														<xsl:text> </xsl:text>
														<fo:inline font-weight="bold">
															<xsl:text>S/N</xsl:text>
														</fo:inline>
													</xsl:if>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
		</fo:block>
	<xsl:variable name="respuesta" select="$datosEspecificos/*[local-name()='Respuesta']"/>
	    <!-- DATOS DE LA RESPUESTA -->
	   	<fo:block margin-top="40pt" text-align="left" font-family="Arial,sans-serif" font-size="10pt" line-height="10pt" space-after.optimum="1pt" display-align="after">
						<fo:inline font-weight="bold">
							<xsl:text>Respuestas de los Organismos Comunicados</xsl:text> 
						</fo:inline>
			</fo:block>
			<xsl:for-each select="$respuesta/*[local-name()='Organismo']"> 
				<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
					<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-body start-indent="0pt"> 
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Nombre:</xsl:text>															
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="*[local-name()='Nombre']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Código Estado:</xsl:text>															
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="*[local-name()='CodigoEstado']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Código Secundario:</xsl:text>															
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="*[local-name()='CodigoEstadoSecundario']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Respuesta:</xsl:text>															
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="5">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="*[local-name()='LiteralError']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
									</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
				</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
