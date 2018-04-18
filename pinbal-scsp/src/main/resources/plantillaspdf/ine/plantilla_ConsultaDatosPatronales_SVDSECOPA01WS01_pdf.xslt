<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0" xmlns:str="http://www.str.com" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:functx="http://www.functx.com" exclude-result-prefixes="str functx">
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
	

	<xsl:param name="SolicitudEspanol"/>
	<xsl:param name="SolicitudNacimientoFecha"/>
	<xsl:param name="SolicitudNacimientoProvincia"/>
	<xsl:param name="SolicitudNacimientoMunicipio"/>
	<xsl:param name="SolicitudResidenciaMunicipio"/>
	<xsl:param name="SolicitudResidenciaProvincia"/>
	
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
		<!-- DATOS DE LA CONSULTA -->
		
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
					
					<fo:table-row>
					   <!-- Español -->
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Español:     </xsl:text>
									</fo:inline>
								</fo:block>													
						</fo:table-cell>	
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									 
									<xsl:if test="$SolicitudEspanol = 's'">
										<xsl:text>Sí</xsl:text>
									</xsl:if>
									<xsl:if test="$SolicitudEspanol = 'n'">
										<xsl:text>No</xsl:text>
									</xsl:if>
								</fo:block>							
						</fo:table-cell>						
					</fo:table-row> 
				</fo:table-body>
			</fo:table>
		    
		    <fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="1mm" space-after="4mm">
				<xsl:if test="$SolicitudNacimientoFecha != '' or $SolicitudNacimientoProvincia != ''   or $SolicitudNacimientoMunicipio != '' ">				
					<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
						<fo:table-body start-indent="0pt">
							<fo:table-row>
								<fo:table-cell padding="1pt" display-align="center" background-color="#B0C4DE">
									<fo:block>
										<fo:inline font-weight="bold" display-align="after">
											<xsl:text>Datos de nacimiento</xsl:text>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					<fo:table table-layout="fixed" border-spacing="5pt" space-before="0mm">
						<fo:table-column column-width="proportional-column-width(1)"/>
						<fo:table-column column-width="proportional-column-width(1)"/>>
						<fo:table-column column-width="proportional-column-width(1)"/>
						<fo:table-column column-width="proportional-column-width(1)"/>	
						<fo:table-column column-width="proportional-column-width(1)"/>
						<fo:table-column column-width="proportional-column-width(1)"/>
						<fo:table-body start-indent="0pt"> 
							<fo:table-row>
								<!-- Provincia Nacimiento -->
								<fo:table-cell padding="2pt" display-align="center">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
											<fo:inline font-weight="bold">
												<xsl:text>Provincia:</xsl:text>
											</fo:inline>
										</fo:block>	
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">							
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
											<xsl:value-of select="$SolicitudNacimientoProvincia"/>
										</fo:block>	
								</fo:table-cell>
								<!-- Municipio Nacimiento -->
								<fo:table-cell padding="2pt" display-align="center">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
											<fo:inline font-weight="bold">
												<xsl:text>Municipio:     </xsl:text>
											</fo:inline>
											
										</fo:block>	
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
											<xsl:value-of select="$SolicitudNacimientoMunicipio"/>
										</fo:block>	
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								 <!-- Fecha nacimiento -->
								<fo:table-cell padding="2pt" display-align="center">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
											<fo:inline font-weight="bold">
												<xsl:text>Fecha:</xsl:text>
											</fo:inline>
										</fo:block>	
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5">						
										<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
											<xsl:if test="$SolicitudNacimientoFecha != '' ">
												<xsl:value-of select="concat(substring(string($SolicitudNacimientoFecha),7,2),'/',substring(string($SolicitudNacimientoFecha),5,2),'/',substring(string($SolicitudNacimientoFecha),1,4))"/>
											</xsl:if>
										</fo:block>	
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</xsl:if>
				<xsl:if test="$SolicitudResidenciaProvincia != '' or $SolicitudResidenciaMunicipio != ''">
					<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
						<fo:table-body start-indent="0pt">
							<fo:table-row>
								<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
									<fo:block>
										<fo:inline font-weight="bold" display-align="after">
											<xsl:text>Datos de residencia</xsl:text>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				
				 
					<fo:table table-layout="fixed" border-spacing="5pt" space-before="0mm">
						<fo:table-column column-width="proportional-column-width(1)"/>
						<fo:table-column column-width="proportional-column-width(1)"/>>
						<fo:table-column column-width="proportional-column-width(1)"/>
						<fo:table-column column-width="proportional-column-width(1)"/>	
						<fo:table-column column-width="proportional-column-width(1)"/>
						<fo:table-column column-width="proportional-column-width(1)"/>
							<fo:table-body start-indent="0pt">
								<fo:table-row>
									 <!-- Fecha Residencia -->
									<fo:table-cell padding="2pt" display-align="center">						
											<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
												<fo:inline font-weight="bold">
													<xsl:text>Provincia:</xsl:text>
												</fo:inline>
											</fo:block>	
									</fo:table-cell>
									<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">						
											<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
												<xsl:value-of select="$SolicitudResidenciaProvincia"/>
											</fo:block>	
									</fo:table-cell>
									<!-- Municipio Residencia -->
									<fo:table-cell padding="2pt" display-align="center">						
											<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
												<fo:inline font-weight="bold">
													<xsl:text>Municipio:</xsl:text>
												</fo:inline>
											</fo:block>	
									</fo:table-cell>
									<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">							
											<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
												<xsl:value-of select="$SolicitudResidenciaMunicipio"/>
											</fo:block>	
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
				</xsl:if>
		
			</fo:block>
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
					<!-- Panel DatosRESIDENDICA -->
					<fo:block border-style="solid" border-width="1px 1px 1px 1px">
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="1mm" space-after="4mm">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="1mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos titular</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
								<!-- titular -->
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-column column-width="proportional-column-width(1.2)"/>
									<fo:table-column column-width="proportional-column-width(1.4)"/>
									<fo:table-column column-width="proportional-column-width(1.1)"/>
									<fo:table-column column-width="proportional-column-width(1.3)"/>
									<fo:table-body start-indent="0pt">
										<!-- Fila 1 Hijo -->
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Documentación:</xsl:text>
															<!-- fo:leader leader-pattern="space"/ -->
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:if test="$titular/*[local-name()='TipoDocumentacion'] != ''">
																(<xsl:value-of select="$titular/*[local-name()='TipoDocumentacion']"/>)
															</xsl:if>
															<xsl:value-of select="$titular/*[local-name()='Documentacion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Nombre y apellidos:</xsl:text>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:choose>
																<xsl:when test="not($titular/*[local-name()='NombreCompleto'])">
																	<xsl:value-of select="$titular/*[local-name()='Nombre']"/><xsl:text> </xsl:text>
																	<xsl:value-of select="$titular/*[local-name()='Apellido1']"/><xsl:text> </xsl:text>
																	<xsl:value-of select="$titular/*[local-name()='Apellido2']"/>
																</xsl:when>
																<xsl:otherwise>
																	<xsl:value-of select="$titular/*[local-name()='NombreCompleto']"/>
																</xsl:otherwise>
															</xsl:choose>   
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
								
								<!-- Datos Residencia -->
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Domicilio</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
							<fo:table table-layout="fixed" border-spacing="5pt" space-before="2mm">
								<fo:table-column column-width="proportional-column-width(1.2)"/>
								<fo:table-column column-width="proportional-column-width(1.4)"/>
								<fo:table-column column-width="proportional-column-width(1.1)"/>
								<fo:table-column column-width="proportional-column-width(1.3)"/>
								<fo:table-body start-indent="0pt">
									<!-- Fila2 Hijo -->
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Provincia:</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$domicilio/*[local-name()='ProvinciaRespuesta']/*[local-name()='Nombre']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Municipio:</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$domicilio/*[local-name()='MunicipioRespuesta']/*[local-name()='Nombre']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<!-- Fila3Hijo -->
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Entidad colectiva:</xsl:text>
														<!-- fo:leader leader-pattern="space" / -->
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$domicilio/*[local-name()='EntColectiva']/*[local-name()='Nombre']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Entidad singular:</xsl:text>
														<!-- fo:leader leader-pattern="space" / -->
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$domicilio/*[local-name()='EntSingular']/*[local-name()='Nombre']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<!-- Fila4Hijo -->
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Núcleo:</xsl:text>
														<!-- fo:leader leader-pattern="space" / -->
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$domicilio/*[local-name()='Nucleo']/*[local-name()='Nombre']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Cod. unidad poblacional:</xsl:text>
														<!-- fo:leader leader-pattern="space" / -->
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$domicilio/*[local-name()='CodUnidadPoblacional']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<!-- Fila1 Hijo -->
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Última modificación:</xsl:text>
														<!-- fo:leader leader-pattern="space" / -->
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:text>(</xsl:text>
														<xsl:value-of select="$domicilio/*[local-name()='UltimaVariacion']/*[local-name()='Codigo']"/>
														<xsl:text>) </xsl:text>
														<xsl:value-of select="$domicilio/*[local-name()='UltimaVariacion']/*[local-name()='Descripcion']"/>
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
														<xsl:text>Fecha última modificación:</xsl:text>
														<!-- fo:leader leader-pattern="space" / -->
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="concat(substring(string($domicilio/*[local-name()='UltimaVariacion']/*[local-name()='Fecha']),7,2),'/',substring(string($domicilio/*[local-name()='UltimaVariacion']/*[local-name()='Fecha']),5,2),'/',substring(string($domicilio/*[local-name()='UltimaVariacion']/*[local-name()='Fecha']),1,4))"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
							<!-- *************************************************************   -->
							<fo:table table-layout="fixed" border-spacing="5pt" space-before="2mm"> 
								<fo:table-column column-width="proportional-column-width(1.2)"/>
								<fo:table-column column-width="proportional-column-width(4.8)"/>
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
														<xsl:value-of select="$domicilio/*[local-name()='Direccion']/*[local-name()='Via']/*[local-name()='Tipo']"/>
														<xsl:text> </xsl:text>
														<xsl:value-of select="$domicilio/*[local-name()='Direccion']/*[local-name()='Via']/*[local-name()='Nombre']"/>
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
										<xsl:variable name="calificador" select="$domicilio/*[local-name()='Direccion']/*[local-name()='Numero']/*[local-name()='Calificador']"/>
										<xsl:variable name="calificadorSup" select="$domicilio/*[local-name()='Direccion']/*[local-name()='NumeroSuperior']/*[local-name()='Calificador']"/>
								
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:if test="string($domicilio/*[local-name()='Direccion']/*[local-name()='Numero']/*[local-name()='Valor']) != ''">
															<xsl:text> </xsl:text>
															<fo:inline font-weight="bold">
																<xsl:text>Nº: </xsl:text>
															</fo:inline>
															<xsl:value-of select="$domicilio/*[local-name()='Direccion']/*[local-name()='Numero']/*[local-name()='Valor']"/>
															<xsl:choose>
																<xsl:when test="$calificador ='A' or $calificador ='a'">
																	<xsl:text> Anterior</xsl:text>
																</xsl:when>
																<xsl:when test="$calificador  ='B' or $calificador ='b'">
																	<xsl:text> Bis</xsl:text>
																</xsl:when>
																<xsl:when test="$calificador  ='T' or $calificador ='t'">
																	<xsl:text> Tribis</xsl:text>
																</xsl:when>
																<xsl:when test="$calificador  ='M' or $calificador ='m'">
																	<xsl:text> Modificado</xsl:text>
																</xsl:when>
																<xsl:otherwise>
																	<xsl:text> </xsl:text><xsl:value-of select="$domicilio/*[local-name()='Direccion']/*[local-name()='Numero']/*[local-name()='Calificador'] "/> 
																</xsl:otherwise>
															</xsl:choose>
															
														</xsl:if>
														<xsl:if test="string($domicilio/*[local-name()='Direccion']/*[local-name()='NumeroSuperior']/*[local-name()='Valor']) != ''">
															<xsl:text> </xsl:text>
															<fo:inline font-weight="bold">
																<xsl:text>Nº superior : </xsl:text>
															</fo:inline>
															<xsl:value-of select="$domicilio/*[local-name()='Direccion']/*[local-name()='NumeroSuperior']/*[local-name()='Valor']"/>
															 <xsl:text> </xsl:text>
															<xsl:choose>
																		<xsl:when test="$calificadorSup ='A' or $calificadorSup ='a'">
																			<xsl:text>Anterior</xsl:text>
																		</xsl:when>
																		<xsl:when test="$calificadorSup ='B' or $calificadorSup ='b'">
																			<xsl:text>Bis</xsl:text>
																		</xsl:when>
																		<xsl:when test="$calificadorSup ='T' or $calificadorSup ='t'">
																			<xsl:text>Tribis</xsl:text>
																		</xsl:when>
																		<xsl:when test="$calificadorSup ='M' or $calificadorSup ='m'">
																			<xsl:text>Modificado</xsl:text>
																		</xsl:when>
																		<xsl:otherwise>
																			<xsl:value-of select="$domicilio/*[local-name()='Direccion']/*[local-name()='NumeroSuperior']/*[local-name()='Calificador']"/> 
																		</xsl:otherwise>
															</xsl:choose>
														</xsl:if>
														<xsl:if test="string($domicilio/*[local-name()='Direccion']/*[local-name()='Bloque']) != ''">
															<xsl:text> </xsl:text>
															<fo:inline font-weight="bold">
																<xsl:text>Bloque: </xsl:text>
															</fo:inline>
															<xsl:value-of select="$domicilio/*[local-name()='Direccion']/*[local-name()='Bloque']"/>
														</xsl:if>
														<xsl:if test="string($domicilio/*[local-name()='Direccion']/*[local-name()='Portal']) != ''">
															<xsl:text> </xsl:text>
															<fo:inline font-weight="bold">
																<xsl:text>Portal: </xsl:text>
															</fo:inline>
															<xsl:value-of select="$domicilio/*[local-name()='Direccion']/*[local-name()='Portal']"/>
														</xsl:if>
														<xsl:if test="string($domicilio/*[local-name()='Direccion']/*[local-name()='Escalera']) != ''">
															<xsl:text> </xsl:text>
															<fo:inline font-weight="bold">
																<xsl:text>Escalera: </xsl:text>
															</fo:inline>
															<xsl:value-of select="$domicilio/*[local-name()='Direccion']/*[local-name()='Escalera']"/>
														</xsl:if>
														<xsl:if test="string($domicilio/*[local-name()='Direccion']/*[local-name()='Planta']) != ''">
															<xsl:text> </xsl:text>
															<fo:inline font-weight="bold">
																<xsl:text>Planta: </xsl:text>
															</fo:inline>
															<xsl:value-of select="$domicilio/*[local-name()='Direccion']/*[local-name()='Planta']"/>
														</xsl:if>
														<xsl:if test="string($domicilio/*[local-name()='Direccion']/*[local-name()='Puerta']) != ''">
															<xsl:text> </xsl:text>
															<fo:inline font-weight="bold">
																<xsl:text>Puerta: </xsl:text>
															</fo:inline>
															<xsl:value-of select="$domicilio/*[local-name()='Direccion']/*[local-name()='Puerta']"/>
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
													<xsl:if test="string($domicilio/*[local-name()='Direccion']/*[local-name()='CodPostal']) != ''">
														<xsl:text> </xsl:text>
														<fo:inline font-weight="bold">
															<xsl:text>Cod. postal: </xsl:text>
														</fo:inline>
														<xsl:value-of select="$domicilio/*[local-name()='Direccion']/*[local-name()='CodPostal']"/>
													</xsl:if>
													<xsl:if  test="$domicilio/*[local-name()='Direccion']/*[local-name()='Kmt'] != '000'">
														<xsl:text> </xsl:text>
														<fo:inline font-weight="bold">
															<xsl:text>Kmt: </xsl:text>
														</fo:inline>
														<xsl:value-of select="$domicilio/*[local-name()='Direccion']/*[local-name()='Kmt']"/>
													</xsl:if>
													<xsl:if  test="$domicilio/*[local-name()='Direccion']/*[local-name()='Hmt'] != '0'">
														<xsl:text> </xsl:text>
														<fo:inline font-weight="bold">
															<xsl:text>Hmt: </xsl:text>
														</fo:inline>
														<xsl:value-of select="$domicilio/*[local-name()='Direccion']/*[local-name()='Hmt']"/>
													</xsl:if>
													
													<xsl:if  test="$domicilio/*[local-name()='Direccion']/*[local-name()='Hmt'] = '0' and $domicilio/*[local-name()='Direccion']/*[local-name()='Kmt'] = '000' and   $domicilio/*[local-name()='Direccion']/*[local-name()='Numero']=''">
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
					</fo:block>
				</xsl:when>
			</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
