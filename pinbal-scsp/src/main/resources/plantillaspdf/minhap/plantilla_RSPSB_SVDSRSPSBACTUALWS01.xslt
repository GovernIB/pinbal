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
	
	<xsl:param name="ConsultaCodigoComunidadAutonoma"/>
	<xsl:param name="ConsultaDatosAdicionalesTitularFechaNacimiento"/>
    
    
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
		<xsl:variable name="estado" select="$datosEspecificos/*[local-name()='Estado']"/>
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
				<fo:table-column column-width="proportional-column-width(1.3)"/>
				<fo:table-column column-width="proportional-column-width(0.7)"/>
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-column column-width="proportional-column-width(0.75)"/>	
				<fo:table-column column-width="proportional-column-width(1.1)"/>
				<fo:table-column column-width="proportional-column-width(1.85)"/>
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
						<fo:table-cell padding="2pt" display-align="center" >							
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Comunidad Autónoma:</xsl:text>
									</fo:inline> 
								</fo:block>													
						</fo:table-cell> 
						
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5" >						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										<xsl:value-of select="$ConsultaCodigoComunidadAutonoma"/>					
								</fo:block>							
						</fo:table-cell>
					</fo:table-row>
					 
					<xsl:if test="$ConsultaDatosAdicionalesTitularFechaNacimiento != ''">
					<fo:table-row>
						<fo:table-cell padding="2pt" display-align="center" >							
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">										
											<xsl:text>Fecha nacimiento:</xsl:text> 
									</fo:inline>
								</fo:block>													
						</fo:table-cell>	
						<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="3">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" > 
										<xsl:value-of select="$ConsultaDatosAdicionalesTitularFechaNacimiento"/>		 
								</fo:block>							
						</fo:table-cell>					
					</fo:table-row>
					</xsl:if>				 
				</fo:table-body>
			</fo:table>  
		</fo:block>

	    <!-- DATOS DE LA RESPUESTA -->	
		<xsl:choose>  
			<xsl:when test="$estado/*[local-name()='CodigoEstado'] != '0'">
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
			<xsl:when test="$estado/*[local-name()='CodigoEstado'] = '0'">
				<fo:block text-align="left" margin-top="25pt">
					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
						<fo:inline font-weight="bold">
							<xsl:text>Datos de respuesta</xsl:text>
						</fo:inline>
					</fo:block>
				</fo:block>
				<fo:block border-style="solid" border-width="1px 1px 1px 1px"> 
						<!-- Panel Datos titular -->
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
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
						</fo:block>
						<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-column column-width="proportional-column-width(0.9)"/>
								<fo:table-column column-width="proportional-column-width(1)"/>
								<fo:table-column column-width="proportional-column-width(1)"/>
								<fo:table-column column-width="proportional-column-width(1.2)"/> 
								<fo:table-column column-width="proportional-column-width(1)"/>
								<fo:table-column column-width="proportional-column-width(0.9)"/>
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
										<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
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
														<xsl:text>Nombre y apellidos:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
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
								</fo:table-body>
							</fo:table>
						</fo:block>
					 
					 
					 <xsl:if test="$datosEspecificos/*[local-name()='DatosImportes']">
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos Importes</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
					
					 
						<xsl:variable name="datosImportes" select="$datosEspecificos/*[local-name()='DatosImportes']"/>
					 
						<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-column column-width="proportional-column-width(1)"/>
								<fo:table-column column-width="proportional-column-width(1)"/>
								<fo:table-column column-width="proportional-column-width(1)"/>
								<fo:table-column column-width="proportional-column-width(1)"/>  
								<fo:table-body start-indent="0pt">
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
										
										<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$datosImportes/*[local-name()='FechaNacimiento']"/> 
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
														<xsl:text>Tipo miembro:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$datosImportes/*[local-name()='TipoMiembro']"/> 
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									 									 
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Tipo prestación:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$datosImportes/*[local-name()='TipoPrestacion']"/> 
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
														<xsl:text>Pensionista:</xsl:text>
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
															<xsl:when test="$datosImportes/*[local-name()='Pensionista'] = 'S'"><xsl:text>Es pensionista</xsl:text></xsl:when>
															<xsl:when test="$datosImportes/*[local-name()='Pensionista'] = 'N' "><xsl:text>NO es pensionista</xsl:text></xsl:when>
															<xsl:otherwise>
																<xsl:value-of select="$datosImportes/*[local-name()='Pensionista']"/>
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
														<xsl:text>Ingresos Trabajo:</xsl:text>
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
																	<xsl:when test="$datosImportes/*[local-name()='IngresosTrabajo']='S'"><xsl:text>Sí tiene ingresos por rendimientos de trabajo</xsl:text></xsl:when>
																	<xsl:when test="$datosImportes/*[local-name()='IngresosTrabajo']='N'"><xsl:text>NO tiene ingresos por rendimientos de trabajo</xsl:text></xsl:when>
																	<xsl:otherwise>
																		<xsl:value-of select="$datosImportes/*[local-name()='IngresosTrabajo']"/>
																	</xsl:otherwise>
																</xsl:choose> 
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
														<xsl:text>Mes último pago:</xsl:text>
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
															<xsl:when test="$datosImportes/*[local-name()='MesUltimoPago'] = '1'"><xsl:text>Enero</xsl:text></xsl:when>
															<xsl:when test="$datosImportes/*[local-name()='MesUltimoPago'] = '2'"><xsl:text>Febrero</xsl:text></xsl:when>
															<xsl:when test="$datosImportes/*[local-name()='MesUltimoPago'] = '3'"><xsl:text>Marzo</xsl:text></xsl:when>
															<xsl:when test="$datosImportes/*[local-name()='MesUltimoPago'] = '4'"><xsl:text>Abril</xsl:text></xsl:when>
															<xsl:when test="$datosImportes/*[local-name()='MesUltimoPago'] = '5'"><xsl:text>Mayo</xsl:text></xsl:when>
															<xsl:when test="$datosImportes/*[local-name()='MesUltimoPago'] = '6'"><xsl:text>Junio</xsl:text></xsl:when>
															<xsl:when test="$datosImportes/*[local-name()='MesUltimoPago'] = '7'"><xsl:text>Julio</xsl:text></xsl:when>
															<xsl:when test="$datosImportes/*[local-name()='MesUltimoPago'] = '8'"><xsl:text>Agosto</xsl:text></xsl:when>
															<xsl:when test="$datosImportes/*[local-name()='MesUltimoPago'] = '9'"><xsl:text>Septiembre</xsl:text></xsl:when>
															<xsl:when test="$datosImportes/*[local-name()='MesUltimoPago'] = '10'"><xsl:text>Octubre</xsl:text></xsl:when>
															<xsl:when test="$datosImportes/*[local-name()='MesUltimoPago'] = '11'"><xsl:text>Noviembre</xsl:text></xsl:when>
															<xsl:when test="$datosImportes/*[local-name()='MesUltimoPago'] = '12'"><xsl:text>Diciembre</xsl:text></xsl:when>
														</xsl:choose> 
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell> 
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Año último pago:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$datosImportes/*[local-name()='AnioUltimoPago']"/> 
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell> 
									</fo:table-row>
									
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="2">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Fecha efectiva de concesión del expediente RSPSB:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="2">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$datosImportes/*[local-name()='FechaInicioEfectosRSPSB']"/> 
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell> 
									</fo:table-row>
									
									<fo:table-row>	
										<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Fecha efectiva de concesión del expediente PCV:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$datosImportes/*[local-name()='FechaInicioEfectosPCV']"/> 
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell> 
									</fo:table-row>
									 
									<fo:table-row> 
										<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Fecha de inicio de la situación actual y de pago de RSPSB:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$datosImportes/*[local-name()='FechaInicioRSPSB']"/> 
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>  
									</fo:table-row>
									
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="2">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Fecha de inicio de la situación actual y de pago de PCV:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="2">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$datosImportes/*[local-name()='FechaInicioPCV']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell> 
									</fo:table-row>
											 
									
									
									<fo:table-row>	
										<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Número miembros unidad familiar o domicilio:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$datosImportes/*[local-name()='NumeroMiembros']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell> 
									</fo:table-row>
									
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Nº prestaciones domicilio:</xsl:text>
														<fo:leader leader-pattern="space"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
											<fo:block text-align="left" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$datosImportes/*[local-name()='NumeroPrestacionesDomicilio']"/>
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell> 
									</fo:table-row> 
								</fo:table-body>
							</fo:table>
						</fo:block>
					</xsl:if>
					
					<xsl:variable name="importes" select="$datosEspecificos/*[local-name()='DatosImportes']/*[local-name()='Importes']"/>
					<xsl:if test="$datosEspecificos/*[local-name()='DatosImportes']/*[local-name()='Importes'] != ''">
						
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Importes</xsl:text>
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
										<xsl:if test="$importes/*[local-name()='ImportePCV'] != ''">
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Importe del pago de la prestación complementaria de vivienda:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$importes/*[local-name()='ImportePCV']"/><xsl:text>€</xsl:text>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell> 
											</fo:table-row>  
										</xsl:if>
										 
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Importe de pago de la renta salario prestación social básica:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											
											<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$importes/*[local-name()='ImporteRSPSB']"/><xsl:text>€</xsl:text>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell> 
										</fo:table-row> 
										
										<xsl:if test="$importes/*[local-name()='ImporteSUM'] != ''">
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Importe de pago del complemento monoparental:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$importes/*[local-name()='ImporteSUM']"/><xsl:text>€</xsl:text>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell> 
											</fo:table-row>  
										</xsl:if>  
									</fo:table-body>
								</fo:table>
							</fo:block> 
					</xsl:if>

					<xsl:variable name="importesUltimoPago" select="$datosEspecificos/*[local-name()='DatosImportes']/*[local-name()='ImportesUltimoPago']"/>
					<xsl:if test="$importesUltimoPago != ''">
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Importes último pago</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-column column-width="proportional-column-width(1.2)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(0.8)"/>  
									<fo:table-body start-indent="0pt">
										<xsl:if test="$importesUltimoPago/*[local-name()='ImporteUltimoPagoPCV'] != ''">
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="1">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Importe PCV del último pago:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:if test="$importesUltimoPago/*[local-name()='ImporteUltimoPagoPCV']">
																	<xsl:value-of select="$importesUltimoPago/*[local-name()='ImporteUltimoPagoPCV']"/>
																	<xsl:text>€</xsl:text>
																</xsl:if>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell> 
											</fo:table-row>  
										</xsl:if>
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="1">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Importe RSPSB del último pago:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											
											<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															 <xsl:if test="$importesUltimoPago/*[local-name()='ImporteUltimoPagoRSPSB']">
																	<xsl:value-of select="$importesUltimoPago/*[local-name()='ImporteUltimoPagoRSPSB']"/>
																	<xsl:text>€</xsl:text>
																</xsl:if>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell> 
										</fo:table-row> 
										
										<xsl:if test="$importesUltimoPago/*[local-name()='ImporteUltimoPagoSUM'] != ''">
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="1">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Importe SUM del último pago:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:if test="$importesUltimoPago/*[local-name()='ImporteUltimoPagoSUM']">
																	<xsl:value-of select="$importesUltimoPago/*[local-name()='ImporteUltimoPagoSUM']"/>
																	<xsl:text>€</xsl:text>
																</xsl:if>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell> 
											</fo:table-row>  
										</xsl:if>    
									</fo:table-body>
								</fo:table>
							</fo:block> 
					</xsl:if>	  
					
					<xsl:variable name="importesAnuales" select="$datosEspecificos/*[local-name()='DatosImportes']/*[local-name()='ImportesAnuales']"/>
					<xsl:if test="$importesAnuales != ''">
					
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Importes Acumulados</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						
						<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-column column-width="proportional-column-width(1.2)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(0.8)"/>  
									<fo:table-body start-indent="0pt">
										<xsl:if test="$importesAnuales/*[local-name()='ImporteAnualPCV'] != ''">
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="1">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Importe PCV acumulado:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal"> 
																<xsl:if test="$importesAnuales/*[local-name()='ImporteAnualPCV']">
																	<xsl:value-of select="$importesAnuales/*[local-name()='ImporteAnualPCV']"/>
																	<xsl:text>€</xsl:text>
																</xsl:if>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell> 
											</fo:table-row>  
										</xsl:if>
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="1">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Importe RSPSB acumulado:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											
											<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:if test="$importesAnuales/*[local-name()='ImporteAnualRSPSB']">
																	<xsl:value-of select="$importesAnuales/*[local-name()='ImporteAnualRSPSB']"/>
																	<xsl:text>€</xsl:text>
																</xsl:if>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell> 
										</fo:table-row> 
										
										<xsl:if test="$importesAnuales/*[local-name()='ImporteAnualSUM'] != ''">
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="1">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Importe SUM acumulado:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:if test="$importesAnuales/*[local-name()='ImporteAnualSUM']">
																	<xsl:value-of select="$importesAnuales/*[local-name()='ImporteAnualSUM']"/>
																	<xsl:text>€</xsl:text>
																</xsl:if>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell> 
											</fo:table-row>  
										</xsl:if>    
									</fo:table-body>
								</fo:table>
							</fo:block> 
					</xsl:if>	 
					
					<xsl:variable name="datosExpediente" select="$datosEspecificos/*[local-name()='DatosImportes']/*[local-name()='DatosExpediente']"/>
					<xsl:if test="$datosExpediente != ''">
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos expediente</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-column column-width="proportional-column-width(1.2)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(0.8)"/>  
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Expediente:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$datosExpediente/*[local-name()='Expediente']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell> 
										 
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Situación expediente:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											
											<fo:table-cell padding="2pt" display-align="center"  >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$datosExpediente/*[local-name()='SituacionExpediente']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell> 
										</fo:table-row> 
										
										<xsl:if test="$datosExpediente/*[local-name()='CausaEstadoExpediente'] != ''">
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Causa estado expediente:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$datosExpediente/*[local-name()='CausaEstadoExpediente']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell> 
											</fo:table-row>  
										</xsl:if>  
										
										<xsl:if test="$datosExpediente/*[local-name()='MotivoEstadoExpediente'] != ''">
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Motivo estado expediente:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$datosExpediente/*[local-name()='MotivoEstadoExpediente']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell> 
											</fo:table-row>  
										</xsl:if>  
									</fo:table-body>
								</fo:table>
							</fo:block> 
					</xsl:if>	
						
					<xsl:variable name="listaDatosSolicitud" select="$datosEspecificos/*[local-name()='DatosImportes']/*[local-name()='ListaDatosSolicitud']"/> 
					<xsl:if test="$listaDatosSolicitud != ''"> 
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos solicitud</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						
						 
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-before="2mm">
	 							<fo:table-header>
	                            	<fo:table-row>
                                		<fo:table-cell padding="0pt" display-align="after"  background-color="#B0C4DE">
                                        	<fo:block text-align="right" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Tipo solicitud</xsl:text>															
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									
										<fo:table-cell padding="0pt" display-align="after"  background-color="#B0C4DE">
			                                         <fo:block text-align="right" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Fecha entrada</xsl:text>															
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
									
										<fo:table-cell padding="2pt" display-align="after"  background-color="#B0C4DE">
			                                         <fo:block text-align="right" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Expediente</xsl:text>															
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>
								
										<fo:table-cell padding="2pt" display-align="after"  background-color="#B0C4DE">
			                                <fo:block text-align="right" margin="0pt">
												<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Estado expediente</xsl:text>															
													</fo:inline>
												</fo:block>
											</fo:block>
										</fo:table-cell>  
		                           </fo:table-row>
	                           </fo:table-header>  
	                           
	                           
	                           <fo:table-body start-indent="0pt"> 
	                           		<xsl:for-each select="$listaDatosSolicitud/*[local-name()='DatosSolicitud']"> 
										<fo:table-row> 
											<fo:table-cell padding="2pt" display-align="after" >
												<fo:block text-align="right" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="*[local-name()='TipoSolicitud']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										
											<fo:table-cell padding="2pt" display-align="after" >
												<fo:block text-align="right" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="*[local-name()='FechaEntrada']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										
											<fo:table-cell padding="2pt" display-align="after" >
												<fo:block text-align="right" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="*[local-name()='Expediente']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										
											<fo:table-cell padding="2pt" display-align="after" >
												<fo:block text-align="right" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:choose>
																<xsl:when test="*[local-name()='EstadoExpediente'] = '1'"><xsl:text>Iniciada</xsl:text></xsl:when>
																<xsl:when test="*[local-name()='EstadoExpediente'] = '2'"><xsl:text>Pendiente de completar datos</xsl:text></xsl:when>
																<xsl:when test="*[local-name()='EstadoExpediente'] = '3'"><xsl:text>Pendiente de valoración</xsl:text></xsl:when>
																<xsl:when test="*[local-name()='EstadoExpediente'] = '4'"><xsl:text>Pendiente propuesta</xsl:text></xsl:when>
																<xsl:when test="*[local-name()='EstadoExpediente'] = '5'"><xsl:text>Pendiente visto bueno propuesta</xsl:text></xsl:when>
																<xsl:when test="*[local-name()='EstadoExpediente'] = '6'"><xsl:text>Propuesta confirmada</xsl:text></xsl:when>
																<xsl:when test="*[local-name()='EstadoExpediente'] = '7'"><xsl:text>Resuelta</xsl:text></xsl:when>
																<xsl:when test="*[local-name()='EstadoExpediente'] = '8'"><xsl:text>Pendiente recepción de alegaciones</xsl:text></xsl:when>
																<xsl:when test="*[local-name()='EstadoExpediente'] = '9'"><xsl:text>Anulada</xsl:text></xsl:when> 
																<xsl:when test="*[local-name()='EstadoExpediente'] = '12'"><xsl:text>Reintegro finalizado</xsl:text></xsl:when>
																<xsl:when test="*[local-name()='EstadoExpediente'] = '13'"><xsl:text>Reintegro bloqueado por recurso</xsl:text></xsl:when>
															</xsl:choose> 
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</xsl:for-each>
	                    		</fo:table-body>        
							</fo:table> 
					</xsl:if> 		 
				</fo:block>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>