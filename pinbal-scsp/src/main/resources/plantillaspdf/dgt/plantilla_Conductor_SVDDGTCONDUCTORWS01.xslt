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
		<xsl:variable name="datosEspecificos" select="//*[local-name()='DatosEspecificos']"/>
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
		

	<!-- DATOS DE LA CONSULTA  -->
		<fo:block text-align="left" margin-top="20pt">
			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
				<fo:inline font-weight="bold">
					<xsl:text>Datos de consulta</xsl:text>
				</fo:inline>
			</fo:block>
		</fo:block>
		<fo:block border-style="solid" border="1px 0 0 0" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-after="1mm">
			<fo:table table-layout="fixed" border-spacing="5pt" space-before="2mm">
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-column column-width="proportional-column-width(0.7)"/>
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-column column-width="proportional-column-width(0.75)"/>	
				<fo:table-column column-width="proportional-column-width(1.1)"/>
				<fo:table-column column-width="proportional-column-width(2.1)"/>
				<fo:table-body start-indent="0pt">
					<fo:table-row>
					   <!-- TIPO DOCUMENTACION -->
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Tipo Doc.:</xsl:text>
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
										<xsl:text>Nombre y apellidos:</xsl:text>
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
									<xsl:choose>
									  <xsl:when test="$consentimiento = 'Si' "> 
											<xsl:text>Sí</xsl:text>
									  </xsl:when>
									  <xsl:otherwise>
											<xsl:value-of select="$consentimiento"/>
									  </xsl:otherwise>
								</xsl:choose>
								</fo:block>							
						</fo:table-cell>
						
						 <!-- NUMERO EXPEDIENTE -->
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Nº expediente:</xsl:text>
									</fo:inline>
								</fo:block>							
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$idExpediente"/>
								</fo:block>							
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<!-- NOMBRE Procedimiento -->
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Procedimiento:     </xsl:text>
									</fo:inline>
									
								</fo:block>													
						</fo:table-cell>		
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$nomProcedimiento"/>
								</fo:block>													
						</fo:table-cell>			
					</fo:table-row>
					
					<fo:table-row> 
						<!-- FINALIDAD -->
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Finalidad:</xsl:text>
									</fo:inline>
								</fo:block>							
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										<xsl:value-of select="$finalidad"/>								
								</fo:block>							
						</fo:table-cell>
					</fo:table-row> 
				</fo:table-body>
			</fo:table>
			
		 
		</fo:block>
		<xsl:variable name="datosEspecificos" select="//*[local-name()='DatosEspecificos']"/>
	   <xsl:variable name="estado" select="//*[local-name()='DatosEspecificos']/*[local-name()='Retorno']/*[local-name()='Estado']"/>
		
	    <!-- DATOS DE LA RESPUESTA -->	
		<xsl:choose>
			<xsl:when test="$estado/*[local-name()='CodigoEstado'] != '0' "> 
				<fo:block margin-top="40pt" text-align="center" font-family="Arial,sans-serif" font-size="10pt" line-height="10pt" space-after.optimum="1pt" display-align="after">
					<fo:inline font-weight="bold">
						<xsl:text>RESULTADO DE LA CONSULTA:      </xsl:text>
						<xsl:value-of select="$estado/*[local-name()='CodigoEstado']"/>
						<xsl:text>  -  </xsl:text>
						<xsl:value-of select="$estado/*[local-name()='Literal']"/>
					</fo:inline>
				</fo:block>
			</xsl:when>
			<xsl:when test="$estado/*[local-name()='CodigoEstado'] = '0' ">
				<fo:block text-align="left" margin-top="25pt">
					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
						<fo:inline font-weight="bold">
							<xsl:text>Datos del conductor</xsl:text>
						</fo:inline>
					</fo:block>
				</fo:block>
				<fo:block border-style="solid" border-width="1px 1px 1px 1px">
					<xsl:variable name="datos" select="$datosEspecificos/*[local-name()='Retorno']/*[local-name()='DatosPersona']"/> 
					<xsl:if test="$datos">
						<xsl:variable name="datosPersona" select="$datos/*[local-name()='datosPersona']/*[local-name()='identificacionPFisica']"/>
						<xsl:if test="$datosPersona/*[local-name()='idDocumento']"> 
						
									<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
											<fo:table-body start-indent="0pt">
												<fo:table-row>
													<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
														<fo:block>
															<fo:inline font-weight="bold" display-align="after">
																<xsl:text>Datos identificativos persona física</xsl:text>
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
																	 <xsl:value-of select="$datosPersona/*[local-name()='idDocumento']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Nombre:</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	 <xsl:value-of select="$datosPersona/*[local-name()='nombre']"/>
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
																	<xsl:text>Primer apellido:</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="$datosPersona/*[local-name()='apellido1']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Segundo apellido:</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="$datosPersona/*[local-name()='apellido2']"/>
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
																	<xsl:text>Fecha nacimiento:</xsl:text>
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
																			<xsl:when test="string($datosPersona/*[local-name()='fechaNacimiento']) = ''"></xsl:when>
																			<xsl:otherwise>
																				<xsl:value-of select="concat(substring(string($datosPersona/*[local-name()='fechaNacimiento']),9,2),'/',substring(string($datosPersona/*[local-name()='fechaNacimiento']),6,2),'/',substring(string($datosPersona/*[local-name()='fechaNacimiento']),1,4))"/>
																			</xsl:otherwise>
																	</xsl:choose> 
																	
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Sexo:</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="$datosPersona/*[local-name()='sexo']"/>
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
																	<xsl:text>Indicador Dev:</xsl:text>
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
																		<xsl:when test="$datosPersona/*[local-name()='indicadorDevPF'] ='true'">Si</xsl:when>
																		<xsl:otherwise>No</xsl:otherwise>
																	</xsl:choose>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Dirección electrónica:</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	 <xsl:value-of select="$datosPersona/*[local-name()='dirElectronicaVial']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
												</fo:table-row> 
											</fo:table-body>
										</fo:table>
									</fo:block>
					</xsl:if>
				
					<xsl:variable name="datosEmpresa" select="$datos/*[local-name()='datosPersona']/*[local-name()='identificacionPJuridica']"/>
					<xsl:if test="$datosEmpresa/*[local-name()='cif']" >	
						
									<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
											<fo:table-body start-indent="0pt">
												<fo:table-row>
													<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
														<fo:block>
															<fo:inline font-weight="bold" display-align="after">
																<xsl:text>Datos identificativos persona jurídica</xsl:text>
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
																	<xsl:text>CIF:</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="$datosEmpresa/*[local-name()='cif']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Razón social</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	 <xsl:value-of select="$datosEmpresa/*[local-name()='razonSocial']"/>
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
																	<xsl:text>Indicador Dev:</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="3">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:choose>
																		<xsl:when test="$datosEmpresa/*[local-name()='indicadorDevPJ'] ='true'">Si</xsl:when>
																		<xsl:otherwise>No</xsl:otherwise>
																	</xsl:choose>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													
												</fo:table-row> 												
												</fo:table-body></fo:table></fo:block>
						</xsl:if>
				
				
				
				
						<xsl:variable name="datosGenerales" select="$datos/*[local-name()='datosGenerales']"/>
						<xsl:if test="$datosGenerales"> 	
								<xsl:variable name="domicilio" select="$datosGenerales/*[local-name()='domicilio']"/>
								<xsl:if test="$domicilio"> 	
				
									<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
											<fo:table-body start-indent="0pt">
												<fo:table-row>
													<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
														<fo:block>
															<fo:inline font-weight="bold" display-align="after">
																<xsl:text>Datos domicilio</xsl:text>
															</fo:inline>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>
									</fo:block>
									<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
											 
											<fo:table-body start-indent="0pt">
												<!-- Fila 1 Hijo -->
												<fo:table-row>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Domicilio:</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="3">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	 <xsl:value-of select="$domicilio/*[local-name()='calle']"/>
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
																	<xsl:text>Código postal:</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	 <xsl:value-of select="$domicilio/*[local-name()='codPostal']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Pueblo:</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="$domicilio/*[local-name()='pueblo']" />
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
																	<xsl:value-of select="$domicilio/*[local-name()='municipio']"/>
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
													<fo:table-cell padding="2pt" display-align="center" >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="$domicilio/*[local-name()='provincia']/*[local-name()='descripcion']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
												 
												</fo:table-row> 
												
											
											</fo:table-body>
										</fo:table>
									</fo:block>
			
								</xsl:if>
							</xsl:if>
							
							
							<xsl:variable name="indicadores" select="$datosGenerales/*[local-name()='indicadores']"/>
							<xsl:if test="$indicadores">
									<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
															<fo:block>
																<fo:inline font-weight="bold" display-align="after">
																	<xsl:text>Indicadores</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block>
										<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
											 <fo:table-column column-width="proportional-column-width(1.5)"/>
											<fo:table-column column-width="proportional-column-width(0.5)"/>
											<fo:table-column column-width="proportional-column-width(1.5)"/>
											<fo:table-column column-width="proportional-column-width(0.5)"/>	
											<fo:table-body start-indent="0pt">
												<!-- Fila 1 Hijo -->
												<fo:table-row>
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Autorizado mercancías peligrosas :</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center" >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																		<xsl:choose>
																		<xsl:when test="$indicadores/*[local-name()='autorizadoMercanciasPeligrosas'] ='true'">Si</xsl:when>
																		<xsl:otherwise>No</xsl:otherwise>
																		</xsl:choose>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>		
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Autorizado transporte escolar :</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center"  >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																		<xsl:choose>
																		<xsl:when test="$indicadores/*[local-name()='autorizadoTransporteEscolar'] ='true'">Si</xsl:when>
																		<xsl:otherwise>No</xsl:otherwise>
																		</xsl:choose>
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
																	<xsl:text>Condición restrictiva :</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center"   >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal"> 
																		<xsl:choose>
																		<xsl:when test="$indicadores/*[local-name()='condicionRestrictiva'] ='true'">Si</xsl:when>
																		<xsl:otherwise>No</xsl:otherwise>
																		</xsl:choose>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>		
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Función autoescuela  :</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center"  >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																		<xsl:value-of select="$indicadores/*[local-name()='funcionAutoescuela']/*[local-name()='descripcion']"/>
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
																	<xsl:text>Lentes :</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center"   >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	 <xsl:choose>
																	<xsl:when test="$indicadores/*[local-name()='lentes'] ='true'">Si</xsl:when>
																	<xsl:otherwise>No</xsl:otherwise>
																	</xsl:choose>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>		
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Indicador Dev :</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center"  >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																		<xsl:choose>
																		<xsl:when test="$indicadores/*[local-name()='indicadorDev'] ='true'">Si</xsl:when>
																		<xsl:otherwise>No</xsl:otherwise>
																		</xsl:choose>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>			
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block>
							</xsl:if>	
			
							<xsl:variable name="permisos" select="$datosGenerales/*[local-name()='listaPermisosVigentes']"/>
							<xsl:if test="$permisos">
							
										<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
															<fo:block>
																<fo:inline font-weight="bold" display-align="after">
																	<xsl:text>Permisos vigentes</xsl:text>
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
																			<xsl:text>Tipo permiso: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Vigencia: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha inicio: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha fin: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<xsl:for-each select="$permisos/*[local-name()='permiso']">
															<fo:table-row>
																			<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='tipoPermiso']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='vigencia']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							 <xsl:choose>
																									<xsl:when test="string(*[local-name()='fechaInicio']) = ''"></xsl:when>
																									<xsl:otherwise>
																										<xsl:value-of select="concat(substring(string(*[local-name()='fechaInicio']),9,2),'/',substring(string(*[local-name()='fechaInicio']),6,2),'/',substring(string(*[local-name()='fechaInicio']),1,4))"/>
																									</xsl:otherwise>
																							</xsl:choose>  
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																			</fo:table-cell>
																			<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							 <xsl:choose>
																									<xsl:when test="string(*[local-name()='fechaFin']) = ''"></xsl:when>
																									<xsl:otherwise>
																										<xsl:value-of select="concat(substring(string(*[local-name()='fechaFin']),9,2),'/',substring(string(*[local-name()='fechaFin']),6,2),'/',substring(string(*[local-name()='fechaFin']),1,4))"/>
																									</xsl:otherwise>
																							</xsl:choose> 
																							
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																			</fo:table-cell>
															</fo:table-row>
													</xsl:for-each>
													
												</fo:table-body>
											</fo:table>
										</fo:block>
							</xsl:if>
						
				 
							<xsl:variable name="vehiculos" select="$datosGenerales/*[local-name()='listaVehiculos']"/>
							<xsl:if test="$vehiculos">				
									<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
											<fo:table-body start-indent="0pt">
												<fo:table-row>
													<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
														<fo:block>
															<fo:inline font-weight="bold" display-align="after">
																<xsl:text>Vehículos</xsl:text>
															</fo:inline>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>
									</fo:block>
									<fo:block text-indent="3mm" margin-top="2pt" margin-left="15pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
												 <xsl:for-each select="$vehiculos/*[local-name()='matricula']">
														<xsl:if test="position() mod 8 = 1">
																<fo:block/>
														  </xsl:if>	
														 	<xsl:if test="position() mod 8 != 1">
																<xsl:text>, </xsl:text>
														  </xsl:if>	 
															 <fo:inline  display-align="after"> 
																 <xsl:value-of select="translate(*[local-name()='matricula'], ' ','')"/>
															</fo:inline>
															
													
												</xsl:for-each>
									</fo:block>									
									
							</xsl:if>
			
			
				</xsl:if>
				
				<xsl:variable name="datosExamen" select="$datos/*[local-name()='listaExamenes']/*[local-name()='datosExamen']"/>
				<xsl:if test="$datosExamen">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Exámenes</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						
						<fo:block margin-top="10pt" margin-left="5pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<xsl:for-each select="$datosExamen">
										<xsl:variable name="datosExamen2" select="$datosExamen/*[local-name()='solicitudExamen']"/>
										<xsl:for-each select="$datosExamen2">
										
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"  border="1pt solid black">
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
																			<xsl:text>Autoescuela: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Centro médico: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="2">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha:</xsl:text>
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
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='autoescuela']"/> 
																						</fo:inline>
																					</fo:block>
																				</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='centroMedico']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="2">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal"> 
																							<xsl:choose>
																									<xsl:when test="string(*[local-name()='fecha']) = ''"></xsl:when>
																									<xsl:otherwise>
																										<xsl:value-of select="concat(substring(string(*[local-name()='fecha']),9,2),'/',substring(string(*[local-name()='fecha']),6,2),'/',substring(string(*[local-name()='fecha']),1,4))"/>
																									</xsl:otherwise>
																							</xsl:choose> 
																							
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
																		<xsl:text>Jefatura: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Situación: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Sucursal: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Tipo permiso: </xsl:text>
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
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='jefatura']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='situacion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='sucursal']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='tipoPermiso']/*[local-name()='descripcion']"/>
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
																		<xsl:text>Calificación: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Tipo examen: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<!-- $datosExamen2 -->
													<xsl:for-each select="*[local-name()='resultados']/*[local-name()='resultadoExamen']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																						<fo:block text-align="left" margin="0pt">
																							<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																								<fo:inline font-style="normal">
																									<xsl:value-of select="*[local-name()='calificacion']/*[local-name()='codigo']"/>
																				 					<xsl:text> - </xsl:text>
																									<xsl:value-of select="*[local-name()='calificacion']/*[local-name()='descripcion']"/>
																								</fo:inline>
																							</fo:block>
																						</fo:block>
																		</fo:table-cell>
																		<fo:table-cell padding="2pt" display-align="center">
																						<fo:block text-align="left" margin="0pt">
																							<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																								<fo:inline font-style="normal">
																									<xsl:choose>
																										<xsl:when test="string(*[local-name()='fecha']) = ''"></xsl:when>
																										<xsl:otherwise>
																											<xsl:value-of select="concat(substring(string(*[local-name()='fecha']),9,2),'/',substring(string(*[local-name()='fecha']),6,2),'/',substring(string(*[local-name()='fecha']),1,4))"/>										
																										</xsl:otherwise>
																									</xsl:choose>
																									
																								</fo:inline>
																							</fo:block>
																						</fo:block>
																		</fo:table-cell>
																		<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
																						<fo:block text-align="left" margin="0pt">
																							<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																								<fo:inline font-style="normal">
																									<xsl:value-of select="*[local-name()='tipoExamen']/*[local-name()='codigo']"/>
																				 					<xsl:text> - </xsl:text>
																									<xsl:value-of select="*[local-name()='tipoExamen']/*[local-name()='descripcion']"/>
																								</fo:inline>
																							</fo:block>
																						</fo:block>
																		</fo:table-cell>
															</fo:table-row>
													</xsl:for-each>
													<fo:table-row>	
																	<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="4">
																		<fo:block>
																				<fo:block text-align="center">
																					<fo:leader leader-pattern="rule" rule-thickness="1" leader-length="100%" color="white"/>
																				</fo:block>
																		</fo:block>
																	</fo:table-cell>
															</fo:table-row>
												</fo:table-body>
											</fo:table>
										</xsl:for-each>
									</xsl:for-each>

										</fo:block>
				</xsl:if>

				<xsl:variable name="tramites" select="$datos/*[local-name()='listaTramites']/*[local-name()='tramite']"/> 
				<xsl:if test="$tramites">
				
					<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Trámites</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
					</fo:block> 
						
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
									<xsl:for-each select="$tramites">
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"  border="1pt solid black">
											<fo:table-column column-width="proportional-column-width(1)"/>
											<fo:table-column column-width="proportional-column-width(1)"/>	
											<fo:table-column column-width="proportional-column-width(1)"/>	
											<fo:table-column column-width="proportional-column-width(1)"/>	
											<fo:table-body start-indent="0pt">
												<!-- Fila 1 Hijo -->
										
												<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Tipo: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Centro médico: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Clase afectación: </xsl:text>
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
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='tipoTramite']/*[local-name()='tipo']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='tipoTramite']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='centroMedico']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='claseAfectacion']"/>
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
																		<xsl:text>Concesión obligada:</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Jefatura: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Sucursal: </xsl:text>
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
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='concesionObligada']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal"> 		 				 
																							<xsl:choose>
																									<xsl:when test="string(*[local-name()='fecha']) = ''"></xsl:when>
																									<xsl:otherwise>
																										<xsl:value-of select="concat(substring(string(*[local-name()='fecha']),9,2),'/',substring(string(*[local-name()='fecha']),6,2),'/',substring(string(*[local-name()='fecha']),1,4))"/>
																									</xsl:otherwise>
																							</xsl:choose>  
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='jefatura']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='sucursal']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
															
															
														</fo:table-row>
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="4">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Tipo permiso: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="4">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='tipoPermiso']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
														
														</fo:table-row>
														<fo:table-row>	
																	<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="4">
																		<fo:block>
																				<fo:block text-align="center">
																					<fo:leader leader-pattern="rule" rule-thickness="1" leader-length="100%" color="white"/>
																				</fo:block>
																		</fo:block>
																	</fo:table-cell>
														</fo:table-row>
													
								</fo:table-body>
							</fo:table>
							</xsl:for-each> 
						</fo:block> 
			 	</xsl:if>
				<xsl:variable name="datosIncidencias" select="$datos/*[local-name()='listaIncidencias']"/>
				<xsl:if test="$datosIncidencias">
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Incidencias</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										<xsl:for-each select="$datosIncidencias/*[local-name()='datosIncidencias']"> 		
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"   border="1pt solid black">
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
																			<xsl:text>Anotación: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Documento: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													 
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha: </xsl:text>
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
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='anotacion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='documento']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																 
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
	  																						<xsl:choose>
																									<xsl:when test="string(*[local-name()='fecha']) = ''"></xsl:when>
																									<xsl:otherwise>
																											<xsl:value-of select="concat(substring(string(*[local-name()='fecha']),9,2),'/',substring(string(*[local-name()='fecha']),6,2),'/',substring(string(*[local-name()='fecha']),1,4))"/>
																									</xsl:otherwise>
																							</xsl:choose> 
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
																		<xsl:text>Jefatura: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Sucursal: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell> 
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Tipo permiso: </xsl:text>
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
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='jefatura']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='sucursal']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell> 
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='tipoPermiso']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
														</fo:table-row>
														
														<fo:table-row>
															<fo:table-cell number-columns-spanned="3"  padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Tipo Incidencia: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
														
														<fo:table-row>
															<fo:table-cell number-columns-spanned="3"  padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			<xsl:value-of select="*[local-name()='tipo']/*[local-name()='codigo']"/>
																			<xsl:text> - </xsl:text> 
																			<xsl:value-of select="*[local-name()='tipo']/*[local-name()='descripcion']"/> 
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
														
														 
											 
													
								</fo:table-body>
							</fo:table>
							</xsl:for-each> 
						</fo:block>
				 		
				</xsl:if>				
				
				<xsl:variable name="datosSanciones" select="$datos/*[local-name()='listaSanciones']"/>
				<xsl:if test="$datosSanciones">
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Sanciones</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
									<xsl:for-each select="$datosSanciones/*[local-name()='datosSanciones']"> 
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm" border="1pt solid black">
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
																			<xsl:text>Anotación: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Autoridad sancionadora: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													 
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Duración </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Expediente: </xsl:text>
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
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='anotacion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='autoridadSancionadora']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																 
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='duracion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='expediente']"/>
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
																		<xsl:text>Fecha inicio: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell> 
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha fin: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Motivo: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Tipo: </xsl:text>
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
																						<fo:inline font-style="normal">
		 																					<xsl:choose>
																									<xsl:when test="string(*[local-name()='fecha']) = ''"></xsl:when>
																									<xsl:otherwise>
																										<xsl:value-of select="concat(substring(string(*[local-name()='fecha']),9,2),'/',substring(string(*[local-name()='fecha']),6,2),'/',substring(string(*[local-name()='fecha']),1,4))"/>
																									</xsl:otherwise>
																							</xsl:choose> 
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell> 
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal"> 
																							<xsl:choose>
																									<xsl:when test="string(*[local-name()='fechaFin']) = ''"></xsl:when>
																									<xsl:otherwise>
																											<xsl:value-of select="concat(substring(string(*[local-name()='fechaFin']),9,2),'/',substring(string(*[local-name()='fechaFin']),6,2),'/',substring(string(*[local-name()='fechaFin']),1,4))"/>
																									</xsl:otherwise>
																							</xsl:choose>  
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='motivo']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='tipoSancion']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
														</fo:table-row>
													
								</fo:table-body>
							</fo:table>
							</xsl:for-each> 
						</fo:block>
				 		
				</xsl:if>			
				
				<!--- tramo-->
			 	</fo:block>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
