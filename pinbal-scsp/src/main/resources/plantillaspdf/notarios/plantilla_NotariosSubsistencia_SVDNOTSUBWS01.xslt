<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0" xmlns:str="http://www.str.com" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:functx="http://www.functx.com" exclude-result-prefixes="str functx">
	<xsl:param name="SV_OutputFormat" select="'PDF'"/>
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
	<xsl:param name="ConsultaAPODERAMIENTOCSV"/>
	<xsl:param name="ConsultaAPODERAMIENTOALERTAID_APLICACION"/>
	<xsl:param name="ConsultaAPODERAMIENTOALERTAFECHA_AVISO"/>
	<xsl:param name="ConsultaAPODERAMIENTOALERTAEMAIL"/>
	<xsl:param name="ConsultaAPODERAMIENTOALERTASMS"/>
	
	<xsl:param name="ConsultaAPODERAMIENTOID_ESCRITURACOD_NOTARIO"/>
	<xsl:param name="ConsultaAPODERAMIENTOID_ESCRITURACOD_NOTARIA"/>
	<xsl:param name="ConsultaAPODERAMIENTOID_ESCRITURANUM_PROTOCOLO"/>
	<xsl:param name="ConsultaAPODERAMIENTOID_ESCRITURANUM_BIS"/>
	<xsl:param name="ConsultaAPODERAMIENTOID_ESCRITURAFECHA_AUTORIZACION"/>
	
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
						<fo:table-row>
							<fo:table-cell padding="1" display-align="center" text-align="center">
								<fo:block display-align="center" text-align="center">
									<fo:external-graphic content-width="scale-to-fit" content-height="scale-to-fit" width="4cm" height="2cm" scaling="uniform" src="url('data:image/jpeg;base64,{$logoIzquierda}')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell padding="25px 0px 0px 10px" border-width="1px 1px 1px 1px">
								<fo:block font-size="10.5pt" font-family="Arial,Helvetica,sans-serif" font-weight="bold" line-height="13pt" color="#2F4F4F" space-after.optimum="2pt" display-align="center" text-align="center">
									<xsl:value-of select="$justificanteTituloHeader"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell padding="1">
								<fo:block display-align="after" text-align="right">
									<!-- fo:external-graphic content-width="scale-to-fit" content-height="100%" width="100%" scaling="uniform" src="url('data:image/jpeg;base64,{$logoDerecha}')"/ -->
									<fo:external-graphic content-width="scale-to-fit" content-height="scale-to-fit" width="4cm" height="2cm" scaling="uniform" src="url('data:image/jpeg;base64,{$logoDerecha}')"/>
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
						<xsl:text> el día  </xsl:text>
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
		<xsl:variable name="datosEspecificos" select="//*[local-name()='TransmisionDatos']/*[local-name()='DatosEspecificos']/*[local-name()='Retorno']"/>
		<xsl:variable name="titular" select="//*[local-name()='TransmisionDatos']/*[local-name()='DatosGenericos']/*[local-name()='Titular']"/>
		<xsl:variable name="domicilio" select="$datosEspecificos/*[local-name()='Domicilio']"/>
		<xsl:variable name="estado" select="$datosEspecificos/*[local-name()='EstadoResultado']"/>
		<xsl:variable name="protocolo" select="$datosEspecificos/*[local-name()='PROTOCOLO']"/>
		<xsl:variable name="documento" select="$datosEspecificos/*[local-name()='DOCUMENTO']"/>
		<fo:block margin-top="8pt" text-align="justify">
			<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" font-weight="bold" color="#2F4F4F">
				<fo:inline>
					<xsl:text>El organismo </xsl:text>
					<fo:inline font-style="italic">
						<xsl:value-of select="$organismoFuncionario"/>
					</fo:inline>
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
							<fo:inline font-style="italic">
								<xsl:value-of select="$unidadTramitadora"/>
							</fo:inline>
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
				<xsl:text>Identificador de petición:</xsl:text>
			</fo:inline>
			<fo:inline font-weight="bold">
				<xsl:value-of select="$idPeticion"/>
			</fo:inline>
		</fo:block>
		<fo:block text-align="center" font-family="Arial,sans-serif" font-size="8pt" line-height="10pt" space-after.optimum="1pt" display-align="after" color="#2F4F4F">
			<fo:inline font-weight="bold">
				<xsl:text>Identificador de transmisión:</xsl:text>
			</fo:inline>
			<fo:inline font-weight="bold">
				<xsl:value-of select="$idTransmision"/>
			</fo:inline>
		</fo:block>
		<!-- DATOS DE LA CONSULTA -->
		<fo:block text-align="left" margin-top="20pt">
			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
				<fo:inline font-weight="bold">
					<xsl:text>Datos de consulta</xsl:text>
				</fo:inline>
			</fo:block>
		</fo:block>
		
		
		<fo:block border-style="solid" border="1px 0 0 0" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-after="1mm">				
			<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
					<fo:table-body start-indent="0pt">
						<fo:table-row>
							<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
								<fo:block>
									<fo:inline font-weight="bold" display-align="after">
										<xsl:text>Datos del Poder</xsl:text>
									</fo:inline>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
			<xsl:choose> 
				<xsl:when test="$ConsultaAPODERAMIENTOCSV != '' ">
					<fo:table table-layout="fixed" border-spacing="5pt" space-before="0mm">
						<fo:table-column column-width="proportional-column-width(1.5)"/>
						<fo:table-column column-width="proportional-column-width(3.5)"/>
							<fo:table-body start-indent="0pt">
								<fo:table-row>							
									<fo:table-cell padding="2pt" display-align="center">						
											<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
												<fo:inline font-weight="bold">
													<xsl:text>CSV:</xsl:text>
												</fo:inline>
											</fo:block>	
									</fo:table-cell>
									<fo:table-cell padding="2pt" display-align="center">						
											<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >										
													<xsl:value-of select="$ConsultaAPODERAMIENTOCSV"/>
											</fo:block>	
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
				</xsl:when>	
				<xsl:otherwise>
					<fo:table table-layout="fixed" border-spacing="5pt" space-before="0mm">
					<fo:table-column column-width="proportional-column-width(1.5)"/>
					<fo:table-column column-width="proportional-column-width(3.5)"/>
						<fo:table-body start-indent="0pt">
							<fo:table-row>							
								<fo:table-cell padding="2pt" display-align="center">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
											<fo:inline font-weight="bold">
												<xsl:text>Notario:</xsl:text>
											</fo:inline>
										</fo:block>	
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >										
												<xsl:value-of select="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='COD_NOTARIO']"/>
										</fo:block>	
								</fo:table-cell>
							</fo:table-row>
							<!-- Fila -->
							<fo:table-row>
								<fo:table-cell padding="2pt" display-align="center">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
											<fo:inline font-weight="bold">
												<xsl:text>Notaria:</xsl:text>
											</fo:inline>
										</fo:block>	
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >										
											 <xsl:value-of select="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='COD_NOTARIA']"/>
										</fo:block>	
								</fo:table-cell>
							</fo:table-row>	
							<!-- Fila -->
							<fo:table-row>
								<fo:table-cell padding="2pt" display-align="center">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
											<fo:inline font-weight="bold">
												<xsl:text>Num. Protocolo:</xsl:text>
											</fo:inline>									
										</fo:block>	
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
											<xsl:value-of select="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='NUM_PROTOCOLO']"/>
										</fo:block>	
								</fo:table-cell>
							</fo:table-row>
							<!-- Fila -->
							<fo:table-row>
								<fo:table-cell padding="2pt" display-align="center">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
											<fo:inline font-weight="bold">
												<xsl:text>Num. Protocolo Bis:</xsl:text>
											</fo:inline>									
										</fo:block>	
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
											<xsl:value-of select="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='NUM_BIS']"/>
										</fo:block>	
								</fo:table-cell>						
							</fo:table-row>
							<!-- Fila -->
							<fo:table-row>
								<fo:table-cell padding="2pt" display-align="center">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
											<fo:inline font-weight="bold">
												<xsl:text>Fecha autorización:</xsl:text>
											</fo:inline>									
										</fo:block>	
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
											<xsl:if test="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='FECHA_AUTORIZACION'] != ''">
												<xsl:value-of select="concat(substring(string($datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='FECHA_AUTORIZACION']),9,2),'/',substring(string($datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='FECHA_AUTORIZACION']),6,2),'/',substring(string($datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='FECHA_AUTORIZACION']),1,4))"/>
											</xsl:if>
										</fo:block>	
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>						
				</xsl:otherwise>	
			</xsl:choose>		
				<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
					<fo:table-body start-indent="0pt">
						<fo:table-row>
							<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
								<fo:block>
									<fo:inline font-weight="bold" display-align="after">
										<xsl:text>Datos de la suscripción a alertas</xsl:text>
									</fo:inline>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>	
						
				<fo:table table-layout="fixed" border-spacing="5pt" space-before="0mm">				
					<fo:table-body start-indent="0pt">
						<fo:table-row>
							<fo:table-cell padding="2pt" display-align="center">						
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										<fo:inline font-weight="bold">
											<xsl:text>Fecha hasta la que se recibiran alertas:</xsl:text>
										</fo:inline>
									</fo:block>	
							</fo:table-cell>
							<fo:table-cell padding="2pt" display-align="center">						
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										<xsl:value-of select="$ConsultaAPODERAMIENTOALERTAFECHA_AVISO"/>
									</fo:block>	
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>		
							<fo:table-cell padding="2pt" display-align="center">						
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										<fo:inline font-weight="bold">
											<xsl:text>Email:</xsl:text>
										</fo:inline>
									</fo:block>	
							</fo:table-cell>
							<fo:table-cell padding="2pt" display-align="center">						
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										<xsl:value-of select="$ConsultaAPODERAMIENTOALERTAEMAIL"/>
									</fo:block>	
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>	
							<fo:table-cell padding="2pt" display-align="center">						
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										<fo:inline font-weight="bold">
											<xsl:text>Teléfono:</xsl:text>
										</fo:inline>
									</fo:block>	
							</fo:table-cell>
							<fo:table-cell padding="2pt" display-align="center">						
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										<xsl:value-of select="$ConsultaAPODERAMIENTOALERTASMS"/>
									</fo:block>	
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
			
		</fo:block>
		<!-- DATOS DE LA RESPUESTA -->
		<xsl:choose>
			<!-- Miramos el Codigo de Error de la etiqueta Atributos -->
			<xsl:when test="$datosEspecificos/*[local-name()='Estado']/*[local-name()='CodigoEstado'] != '0003' ">
				<xsl:variable name="estado" select="$datosEspecificos/*[local-name()='Estado']"/>
				<fo:block margin-top="40pt" text-align="center" font-family="Arial,sans-serif" font-size="10pt" line-height="10pt" space-after.optimum="1pt" display-align="after">
					<fo:inline font-weight="bold">
						<xsl:text>RESULTADO DE LA CONSULTA:      </xsl:text>
						<xsl:value-of select="$estado/*[local-name()='CodigoEstado']"/>
						<xsl:text>  -  </xsl:text>
						<xsl:value-of select="$estado/*[local-name()='LiteralError']"/>
					</fo:inline>
				</fo:block>
			</xsl:when>
			<!-- FIN Miramos el Codigo de Error de la etiqueta Atributos -->
			<xsl:when test="$datosEspecificos/*[local-name()='Estado']/*[local-name()='CodigoEstado'] = '0003'">
				<fo:block text-align="left" margin-top="25pt">
					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
						<fo:inline font-weight="bold">
							<xsl:text>Datos de respuesta</xsl:text>
						</fo:inline>
					</fo:block>
				</fo:block>
				<!-- APODERAMIENTO -->
				<fo:block border-style="solid" border-width="1px 1px 1px 1px">
					<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="1mm" space-after="4mm">
						<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
							<fo:table-body start-indent="0pt">
								<fo:table-row>
									<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
										<fo:block>
											<fo:inline font-weight="bold" display-align="after">
												<xsl:text>Apoderamiento</xsl:text>
											</fo:inline>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
						
								<xsl:choose> 
									<xsl:when test="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='CSV'] != '' ">
											<!-- Tabla con los datos CSV -->
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(1)"/>
												<fo:table-column column-width="proportional-column-width(4)"/>
												<fo:table-body start-indent="0pt">									
													<!-- Fila 4 Hijo -->
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>CSV:</xsl:text>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="normal">
																		<xsl:value-of select="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='CSV']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
											</fo:table-body>
										</fo:table>
									</xsl:when>
																		
									<xsl:otherwise>
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(0.75)"/>
												<fo:table-column column-width="proportional-column-width(4.25)"/>		
												<fo:table-body start-indent="0pt">	
													<!-- Fila 1 Hijo -->
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Notario:</xsl:text>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			<xsl:value-of select="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='NOTARIO']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
														<!-- Fila  -->
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Notaría:</xsl:text>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			<xsl:value-of select="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='NOTARIA']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
												</fo:table-body>
											</fo:table>
											
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(0.9)"/>
												<fo:table-column column-width="proportional-column-width(0.6)"/>
												<fo:table-column column-width="proportional-column-width(1.5)"/>
												<fo:table-column column-width="proportional-column-width(0.5)"/>
												<fo:table-column column-width="proportional-column-width(1.25)"/>	
												<fo:table-column column-width="proportional-column-width(1.25)"/>									
												<fo:table-body start-indent="0pt">	
														<!-- Fila 2 Hijo -->
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Nº Protocolo: </xsl:text>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>															
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			<xsl:value-of select="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='NUM_PROTOCOLO']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>		
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Nº protocolo bis:</xsl:text>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			<xsl:value-of select="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='NUM_BIS']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																				<xsl:text>Autorización:</xsl:text>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			<xsl:if test="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='FECHA_AUTORIZACION'] != ''">	
																				<xsl:value-of select="concat(substring(string($datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='FECHA_AUTORIZACION']),9,2),'/',substring(string($datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='FECHA_AUTORIZACION']),6,2),'/',substring(string($datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='ID_ESCRITURA']/*[local-name()='FECHA_AUTORIZACION']),1,4))"/>
																			</xsl:if>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</fo:table-body>
												</fo:table>
									</xsl:otherwise>
								</xsl:choose>
								
								<!-- Tabla con los datos genericos de datos protocolo -->
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(4)"/>
									<fo:table-body start-indent="0pt">
									<!-- Fila 5 Hijo -->
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Vigencia del poder:</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="normal">
														<xsl:choose>
															<xsl:when test="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='VIGENCIA_PODER'] = '1'">
																<xsl:text>Subsistente</xsl:text>
															</xsl:when>
															<xsl:when test="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='VIGENCIA_PODER'] = '2'">
																<xsl:text>Parcialmente revocado</xsl:text>
															</xsl:when>
															<xsl:when test="$datosEspecificos/*[local-name()='APODERAMIENTO']/*[local-name()='VIGENCIA_PODER'] = '3'">
																<xsl:text>Totalmente revocado</xsl:text>
															</xsl:when>
														</xsl:choose>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
							</fo:table-body>
						</fo:table>
				
						<!-- FIN APODERAMIENTO -->
						<!-- DATOS PROTOCOLO -->
						<!-- FOR EACH PROTOCOLO  -->
						<xsl:for-each select="$datosEspecificos/*[local-name()='DATOS_PROTOCOLO']/*[local-name()='PROTOCOLO']">
							<xsl:variable name="protocolo" select="current()"/>
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Protocolo</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
							<!-- Tabla con los datos genericos de datos protocolo -->
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-column column-width="proportional-column-width(0.75)"/>
										<fo:table-column column-width="proportional-column-width(4.25)"/>
								<fo:table-body start-indent="0pt">
									<!-- Fila 1 Hijo -->
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>CSV:</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="normal">
														<xsl:value-of select="$protocolo/*[local-name()='CSV']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>									
									</fo:table-row>
									<!-- Fila 2 Hijo -->
									<fo:table-row>						
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Notario:</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="normal">
														<xsl:value-of select="$protocolo/*[local-name()='NOTARIO']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
								</fo:table-row>
								<!-- Fila  -->
								<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Notaria:</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="normal">
														<xsl:value-of select="$protocolo/*[local-name()='NOTARIA']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
									
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-column column-width="proportional-column-width(0.9)"/>
									<fo:table-column column-width="proportional-column-width(0.6)"/>
									<fo:table-column column-width="proportional-column-width(1.5)"/>
									<fo:table-column column-width="proportional-column-width(0.5)"/>
									<fo:table-column column-width="proportional-column-width(1.25)"/>	
									<fo:table-column column-width="proportional-column-width(1.25)"/>								
									<fo:table-body start-indent="0pt">			
									<!-- Fila  -->
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Nº Protocolo:          </xsl:text>
													</fo:inline>													
												</fo:block>
											</fo:block>
										</fo:table-cell>										
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">													
													<fo:inline font-weight="normal">
														<xsl:value-of select="$protocolo/*[local-name()='NUM_PROTOCOLO']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">													
													<fo:inline font-weight="bold">
														<xsl:text>Nº protocolo bis:</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">	
													<fo:inline font-weight="normal">
														<xsl:value-of select="$protocolo/*[local-name()='NUM_BIS']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">													
													<fo:inline font-weight="bold">
														<xsl:text>Fecha autorización: </xsl:text>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">													
													<fo:inline font-weight="normal">
														<xsl:if test="$protocolo/*[local-name()='FECHA_AUTORIZACION'] != ''">
															<xsl:value-of select="concat(substring(string($protocolo/*[local-name()='FECHA_AUTORIZACION']),9,2),'/',substring(string($protocolo/*[local-name()='FECHA_AUTORIZACION']),6,2),'/',substring(string($protocolo/*[local-name()='FECHA_AUTORIZACION']),1,4))"/>
														</xsl:if>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
										
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-column column-width="proportional-column-width(0.75)"/>
								<fo:table-column column-width="proportional-column-width(4.25)"/>
								<fo:table-body start-indent="0pt">		
									<!-- Fila  -->
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Acto jurídico:</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="normal">
														<xsl:value-of select="$protocolo/*[local-name()='DESCRIPCION_ACTO_JURIDICO']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>								
								</fo:table-body>
							</fo:table>
							<!-- FIN DATOS GLOBALES PROTOCOLO -->
							<!-- FOR EACH DE DATOS APODERADOS  APODERADOS -->
								<xsl:for-each select="$protocolo/*[local-name()='DATOS_APODERADO']/*[local-name()='APODERADO']">
									<xsl:variable name="apoderado" select="current()"/>
									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="3pt" display-align="center"  background-color="#C0C0C0">
													<fo:block>
														<fo:inline font-weight="bold" display-align="after">
															<xsl:text>Apoderado</xsl:text>
														</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
									<!-- Tabla con los datos genericos de datos apoderado -->
									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-column column-width="proportional-column-width(4)"/>
										<fo:table-body start-indent="0pt">
											<!-- Fila 5 Hijo -->
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Nombre/Razón social:</xsl:text>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="normal">
																<xsl:if test="$apoderado/*[local-name()='NOM'] != ''">
																	<xsl:value-of select="$apoderado/*[local-name()='NOM']"/>
																	<xsl:text> </xsl:text>
																</xsl:if>
																<xsl:if test="$apoderado/*[local-name()='APE1_RAZ_SOC'] !=''">
																	<xsl:value-of select="$apoderado/*[local-name()='APE1_RAZ_SOC']"/>
																	<xsl:text> </xsl:text>
																</xsl:if>
																<xsl:if test="$apoderado/*[local-name()='APE2'] != ''">
																	<xsl:value-of select="$apoderado/*[local-name()='APE2']"/>
																</xsl:if>
																<xsl:text> - </xsl:text>
																<xsl:if test="$apoderado/*[local-name()='DOC_IDE']/*[local-name()='CIR_AUS']">
																	<xsl:value-of select="$apoderado/*[local-name()='DOC_IDE']/*[local-name()='CIR_AUS']"/>
																</xsl:if>
																<xsl:if test="$apoderado/*[local-name()='DOC_IDE']/*[local-name()='NUM_DOC_IDE']">
																	<xsl:value-of select="$apoderado/*[local-name()='DOC_IDE']/*[local-name()='NUM_DOC_IDE']"/>
																</xsl:if>	
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<!-- Fila 6 Hijo -->
											<fo:table-row>	
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>País:</xsl:text>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="normal">																 
																<xsl:value-of select="$apoderado/*[local-name()='DESCRIPCION_NAC']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>							
								</xsl:for-each>
							<!-- FIN FOR EACH DE DATOS APODERADOS APODERADOS -->
							
							<!-- FOR EACH DE DATOS PODERANTES  PODERDANTE -->
								<xsl:for-each select="$protocolo/*[local-name()='DATOS_PODERDANTE']/*[local-name()='PODERDANTE']">
									<xsl:variable name="poderdante" select="current()"/>
									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="3pt" display-align="center"  background-color="#C0C0C0">
													<fo:block>
														<fo:inline font-weight="bold" display-align="after">
															<xsl:text>Poderdante</xsl:text>
														</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
									<!-- Tabla con los datos genericos de datos apoderado -->
									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-column column-width="proportional-column-width(4)"/>
										<fo:table-body start-indent="0pt">		
											<!-- Fila 3 Hijo -->
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Nombre/Razón social:</xsl:text>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="normal">
																<xsl:if test="$poderdante/*[local-name()='NOM'] != ''">
																	<xsl:value-of select="$poderdante/*[local-name()='NOM']"/>
																	<xsl:text> </xsl:text>
																</xsl:if>
																<xsl:if test="$poderdante/*[local-name()='APE1_RAZ_SOC'] !=''">
																	<xsl:value-of select="$poderdante/*[local-name()='APE1_RAZ_SOC']"/>
																	<xsl:text> </xsl:text>
																</xsl:if>
																<xsl:if test="$poderdante/*[local-name()='APE2'] != ''">
																	<xsl:value-of select="$poderdante/*[local-name()='APE2']"/>
																</xsl:if>
																<xsl:text> - </xsl:text>
																<xsl:if test="$poderdante/*[local-name()='DOC_IDE']/*[local-name()='CIR_AUS']">
																	<xsl:value-of select="$poderdante/*[local-name()='DOC_IDE']/*[local-name()='CIR_AUS']"/>
																</xsl:if>	
																<xsl:if test="$poderdante/*[local-name()='DOC_IDE']/*[local-name()='NUM_DOC_IDE']">
																	<xsl:value-of select="$poderdante/*[local-name()='DOC_IDE']/*[local-name()='NUM_DOC_IDE']"/>
																</xsl:if>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>											
											<!-- Desc Pais -->
											<fo:table-row>	
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>País:</xsl:text>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="normal">																 
																<xsl:value-of select="$poderdante/*[local-name()='DESCRIPCION_NAC']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>							
								</xsl:for-each>
							<!-- FIN FOR EACH DE DATOS PODERANTES PODERANTES -->
						</xsl:for-each>
					</fo:block>
				</fo:block>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
