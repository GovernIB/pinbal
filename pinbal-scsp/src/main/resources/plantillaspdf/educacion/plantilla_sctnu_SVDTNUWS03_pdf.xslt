<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0"  xmlns:xs="http://www.w3.org/2001/XMLSchema" >
	
	<xsl:output version="1.0" method="xml" encoding="UTF-8" indent="no"/>
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
    
    
	<xsl:template match="/">
		<xsl:variable name="datosEspecificos" select="//*[local-name()='DatosEspecificos']/*[local-name()='Retorno']"/>
		<xsl:variable name="datosTitularAdicionales" select="$datosEspecificos/*[local-name()='DatosTitular']"/>
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
		<xsl:variable name="datosEspecificos" select="//*[local-name()='TransmisionDatos']/*[local-name()='DatosEspecificos']/*[local-name()='Retorno']"/>
		<xsl:variable name="datosTitular" select="//*[local-name()='TransmisionDatos']/*[local-name()='DatosGenericos']/*[local-name()='Titular']"/>
		<xsl:variable name="estado" select="$datosEspecificos/*[local-name()='EstadoResultado']"/>
		<xsl:variable name="listaTitulos" select="$datosEspecificos/*[local-name()='ListaTitulos']"/>
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
		

	<!-- DATOS DE LA CONSULTA  -->
		<fo:block text-align="left" margin-top="20pt">
			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
				<fo:inline font-weight="bold">
					<xsl:text>Datos de Consulta</xsl:text>
				</fo:inline>
			</fo:block>
		</fo:block>
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
						<fo:table-cell padding="2pt" display-align="center" >							
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Tipo Doc:</xsl:text>
									</fo:inline>
								</fo:block>							
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" >							
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
						<fo:table-cell padding="2pt" display-align="center" >							
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$docPeticion"/>	
								</fo:block>							
						</fo:table-cell>
						
						<!-- NOMBRE Y APELLIDOS -->
						<fo:table-cell padding="2pt" display-align="center" >							
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Nombre y Apellidos:</xsl:text>
									</fo:inline>
								</fo:block>							
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" >							
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
						<fo:table-cell padding="2pt" display-align="center" >							
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$consentimiento"/>
								</fo:block>							
						</fo:table-cell>
						
						 <!-- NUMERO EXPEDIENTE -->
						<fo:table-cell padding="2pt" display-align="center" >							
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Nº Expediente:</xsl:text>
									</fo:inline>
								</fo:block>							
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" >							
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$idExpediente"/>
								</fo:block>							
						</fo:table-cell>
						
						<!-- FINALIDAD -->
						<fo:table-cell padding="2pt" display-align="center" >							
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Finalidad:</xsl:text>
									</fo:inline>
								</fo:block>							
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" >							
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
						<fo:table-cell padding="2pt" display-align="center" >							
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
		</fo:block>

	    <!-- DATOS DE LA RESPUESTA -->	
		<xsl:choose>
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
			<xsl:when test="$datosEspecificos/*[local-name()='Estado']/*[local-name()='CodigoEstado'] = '0003'">
				<fo:block text-align="left" margin-top="25pt">
					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
						<fo:inline font-weight="bold">
							<xsl:text>Datos de Respuesta</xsl:text>
						</fo:inline>
					</fo:block>
				</fo:block>
				<fo:block border-style="solid" border-width="1px 1px 1px 1px">
					<xsl:if test="$datosTitular">
						<!-- Panel Datos Titular -->
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos Titular</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-column column-width="proportional-column-width(1)"/>
								<fo:table-column column-width="proportional-column-width(1)"/>
								<fo:table-column column-width="proportional-column-width(1)"/>
								<fo:table-column column-width="proportional-column-width(1)"/>
								<fo:table-body start-indent="0pt">
									<!-- Fila 1 Hijo -->
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Documentación:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:text>(</xsl:text>
														<xsl:value-of select="$datosTitular/*[local-name()='TipoDocumentacion']"/>
														<xsl:text>)  </xsl:text>
														<xsl:value-of select="$datosTitular/*[local-name()='Documentacion']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Nombre y Apellidos:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:choose>
															<xsl:when test="not($datosTitular/*[local-name()='NombreCompleto'])">
																<xsl:value-of select="$datosTitular/*[local-name()='Nombre']"/><xsl:text> </xsl:text>
																<xsl:value-of select="$datosTitular/*[local-name()='Apellido1']"/><xsl:text> </xsl:text>
																<xsl:value-of select="$datosTitular/*[local-name()='Apellido2']"/>
															</xsl:when>
															<xsl:otherwise>
																<xsl:value-of select="$datosTitular/*[local-name()='NombreCompleto']"/>
															</xsl:otherwise>
														</xsl:choose> 
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<!-- Fila 2 --> 
									<xsl:variable name="datosTitularAdicionales" select="$datosEspecificos/*[local-name()='DatosTitular']"/>

									<xsl:variable name="fechaNacimiento" select="$datosTitularAdicionales/*[local-name()='FechaNacimiento']"/>
									<xsl:variable name="lugarNacimiento" select="$datosTitularAdicionales/*[local-name()='LugarNacimiento']"/>
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Fecha de Nacimiento:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$datosTitularAdicionales/*[local-name()='FechaNacimiento']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:if test="$lugarNacimiento/*[local-name()='Pais']">			
															<xsl:text>País de Nacimiento:</xsl:text>
														    <fo:leader leader-pattern="space"/>									
														 </xsl:if>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<xsl:if test="$lugarNacimiento/*[local-name()='Pais']">			
																<xsl:value-of select="$lugarNacimiento/*[local-name()='Pais']"/>							
														 </xsl:if>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Código Provincia:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
													<xsl:value-of select="$lugarNacimiento/*[local-name()='CodigoProvincia']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold"> 
															<xsl:text>Provincia:</xsl:text>
														    <fo:leader leader-pattern="space"/>									
														  
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													  <xsl:value-of select="$lugarNacimiento/*[local-name()='Provincia']"/>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									
									
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Municipio:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$lugarNacimiento/*[local-name()='Municipio']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														 
															<xsl:text>Localidad</xsl:text>
														    <fo:leader leader-pattern="space"/>									
														  
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													 <xsl:value-of select="$lugarNacimiento/*[local-name()='Localidad']"/>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									
								</fo:table-body>
							</fo:table>
						</fo:block>
					</xsl:if>
					<xsl:if test="$listaTitulos/*[local-name()='DatosTitulacion']">
						<!-- Panel Datos Titulos Universitarios -->
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos Títulos No Universitarios</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						<xsl:for-each select="$listaTitulos/*[local-name()='DatosTitulacion']">
							<xsl:variable name="listaTitulosCount" select="position()"/>
 
							<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" space-after="3mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="1pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after" font-size="9pt" font-family="Arial,Helvetica,sans-serif">
														<xsl:text>Datos Centro</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-column column-width="proportional-column-width(0.5)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<!-- <fo:table-column column-width="proportional-column-width(1.5)" />
									<fo:table-column column-width="proportional-column-width(0.25)" />	-->
									<fo:table-body start-indent="0pt">
										<!-- Fila 1 Hijo -->
										 
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Nombre</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosCentro']/*[local-name()='Centro']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Código Centro</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosCentro']/*[local-name()='CodigoCentro']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Provincia</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosCentro']/*[local-name()='Provincia']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Código Provincia</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosCentro']/*[local-name()='CodigoProvincia']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										
									</fo:table-body>
								</fo:table>
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="1pt" display-align="center" background-color="#B0C4DE">
												<fo:block margin-left="2pt" margin-right="2pt">
													<fo:inline font-weight="bold" display-align="after" font-size="9pt" font-family="Arial,Helvetica,sans-serif">
														<xsl:text>Datos Título</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-before="2mm">
									<fo:table-column column-width="proportional-column-width(0.75)"/>
									<fo:table-column column-width="proportional-column-width(0.5)"/>
									<fo:table-column column-width="proportional-column-width(0.75)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-body start-indent="0pt">
										<!-- Fila 1 Hijo -->
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Código Titulación:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosTitulo']/*[local-name()='CodigoTitulacion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Titulación:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosTitulo']/*[local-name()='Titulacion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Código Tipo Título:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosTitulo']/*[local-name()='CodigoTipoTitulo']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Tipo Título</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosTitulo']/*[local-name()='TipoTitulo']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										
										
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Tipo Estudio:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="*[local-name()='DatosTitulo']/*[local-name()='TipoEstudio']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Nivel</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="*[local-name()='DatosTitulo']/*[local-name()='Nivel']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>										
										
										
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Fecha Finalización:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosTitulo']/*[local-name()='FechaFinalizacion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Fecha Expedición</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosTitulo']/*[local-name()='FechaExpedicion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										
										
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Codigo País Expedición</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosTitulo']/*[local-name()='CodigoPaisExpedicion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Pais Expedición</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosTitulo']/*[local-name()='PaisExpedicion']"/>
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
															<xsl:text>Número Registro Autonómico:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosTitulo']/*[local-name()='NumeroRegistroAutonomico']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											 <fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Número Registro Mec:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosTitulo']/*[local-name()='NumeroRegistroMec']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<xsl:variable name="datosRegistro" select="./*[local-name()='DatosTitulo']/*[local-name()='Registro']"/>
										 <xsl:if test="$datosRegistro">
										 <fo:table-row>
											<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="4">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="11pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Registro:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>	 
										</fo:table-row> 
										 <fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Número Orden Libro:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosTitulo']/*[local-name()='Registro']/*[local-name()='NumeroOrdenLibro']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											 <fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Número Libro:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosTitulo']/*[local-name()='Registro']/*[local-name()='NumeroLibro']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Número Orden Folio:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="./*[local-name()='DatosTitulo']/*[local-name()='Registro']/*[local-name()='NumeroFolio']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											 <fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
														 
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															 
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</xsl:if >
									</fo:table-body>
								</fo:table>
							</fo:block>
						</xsl:for-each>
					</xsl:if>
				</fo:block>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>