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
	
	<xsl:param name="DatosEntradaNifEspanol"/>
	<xsl:param name="DatosEntradaNumDocId"/>
	<xsl:param name="DatosEntradaNombreCompletoRS"/>
	<xsl:param name="DatosEntradaFechaNacConst"/>
	<xsl:param name="DatosEntradaCodResolucionJudFis"/>
	<xsl:param name="DatosEntradaNombreResolucionJudFis"/>
	<xsl:param name="DatosEntradaIdResolucionJudFis"/>
	<xsl:param name="DatosEntradaIdUsuarioSolicitante"/> 
	
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
									
	<!-- fo:external-graphic content-width="scale-to-fit" content-height="100%" width="100%" scaling="uniform" src="url('data:image/jpeg;base64,{$logoDerecha}')"/ -->   
																	
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
		<xsl:variable name="estado" select="$datosEspecificos/*[local-name()='DatosSalida']/*[local-name()='EstadoRpta']"/> 
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
					<xsl:text>Consulta realizada</xsl:text>
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

					<xsl:if test="$DatosEntradaNifEspanol != ''  ">
						<fo:table table-layout="fixed" border-spacing="5pt" space-before="0mm">
				<fo:table-column column-width="proportional-column-width(0.2)"/>
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-column column-width="proportional-column-width(1)"/>	
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-body start-indent="0pt">
								<fo:table-row>
								  <fo:table-cell padding="2pt" display-align="center" number-columns-spanned="6">						
											<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
												<fo:inline font-weight="bold">
													<xsl:text>Consulta por NIF Español</xsl:text>
												</fo:inline>
											</fo:block>	
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell padding="2pt" display-align="center">						
											<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
												<fo:inline font-weight="bold">
													<xsl:text> </xsl:text>
												</fo:inline>
											</fo:block>	
									</fo:table-cell>
									<fo:table-cell padding="2pt" display-align="center">						
											<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
												<fo:inline font-weight="bold">
													<xsl:text>NIF Español</xsl:text>
												</fo:inline>
											</fo:block>	
									</fo:table-cell>
									<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="4">						
											<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
												<xsl:value-of select="$DatosEntradaNifEspanol"/>
											</fo:block>	
									</fo:table-cell>
						 </fo:table-row>
						 	</fo:table-body>
			</fo:table>	
					</xsl:if>
					<xsl:if test="$DatosEntradaNifEspanol = ''  ">
					
			 <fo:table table-layout="fixed"  width="100%" border-spacing="5pt" space-before="0mm">
				<fo:table-column column-width="proportional-column-width(0.2)"/>
				<fo:table-column column-width="proportional-column-width(1.2)"/>
				<fo:table-column column-width="proportional-column-width(1.4"/>
				<fo:table-column column-width="proportional-column-width(1.5)"/>	
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-body start-indent="0pt">
					<fo:table-row>
								  <fo:table-cell padding="2pt" display-align="center" number-columns-spanned="6">						
											<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
												<fo:inline font-weight="bold">
													<xsl:text>Consulta por otros datos</xsl:text>
												</fo:inline>
											</fo:block>	
									</fo:table-cell>
					</fo:table-row>
					 <fo:table-row>
						 <fo:table-cell padding="2pt" display-align="center">						
											<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
												<fo:inline font-weight="bold">
													<xsl:text> </xsl:text>
												</fo:inline>
											</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
									<fo:inline font-weight="bold">
										<xsl:text>Número doc. identificativo:</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$DatosEntradaNumDocId"/>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="0pt"  text-align="left" wrap-option="wrap" >						
								<fo:block font-size="8pt"    wrap-option="wrap" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold" >
										<xsl:text>Nombre completo/razón social</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$DatosEntradaNombreCompletoRS"/>
								</fo:block>	
						</fo:table-cell>
					</fo:table-row>
					 <fo:table-row>
						   <fo:table-cell padding="2pt" display-align="center">						
											<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
												<fo:inline font-weight="bold">
													<xsl:text> </xsl:text>
												</fo:inline>
											</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										 	<xsl:text>Fecha nac./const.</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="4" >						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$DatosEntradaFechaNacConst"/>
								</fo:block>	
						</fo:table-cell>
					</fo:table-row> 
						</fo:table-body>
			</fo:table>	
					</xsl:if>  
			
 
		<!--	<fo:table table-layout="fixed" border-spacing="5pt" space-before="0mm">
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
										<xsl:text>Nif Español</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$DatosEntradaNifEspanol"/>
								</fo:block>	
						</fo:table-cell>
						 

					 
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Nº Doc Id:</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$DatosEntradaNumDocId"/>
								</fo:block>	
						</fo:table-cell> 

						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Nombre/Razón Social</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$DatosEntradaNombreCompletoRS"/>
								</fo:block>	
						</fo:table-cell>
					</fo:table-row>  

					
					<fo:table-row>
					 
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Id Resolución Jud:</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$DatosEntradaIdResolucionJudFis"/>
								</fo:block>	
						</fo:table-cell>
					 
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Id  Usuario Solicitante:</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$DatosEntradaIdUsuarioSolicitante"/>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text> </xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>					
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text> asdfasdfadsfadsfasdf</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			  --> 

			  
			 
	    <!-- DATOS DE LA RESPUESTA -->
	   		</fo:block>
				<xsl:choose>
				<xsl:when test="$estado/*[local-name()='Codigo'] != '0000' "> 
					<fo:block margin-top="40pt" text-align="center" font-family="Arial,sans-serif" font-size="10pt" line-height="10pt" space-after.optimum="1pt" display-align="after">
						 <fo:inline font-weight="bold">
								<xsl:text>RESULTADO DE LA CONSULTA:      </xsl:text>
								<xsl:value-of select="$estado/*[local-name()='Codigo']"/>
								<xsl:text>  -  </xsl:text>
								<xsl:value-of select="$estado/*[local-name()='Descripcion']"/>
							</fo:inline> 
						</fo:block> 
			  </xsl:when>
				<!-- FIN Miramos el Codigo de Error de la etiqueta Atributos -->
				<xsl:when test="$estado/*[local-name()='Codigo'] = '0000' ">
					   
					 <xsl:variable name="intervinientes" select="$datosEspecificos/*[local-name()='DatosSalida']/*[local-name()='ListaIntervinientes']"/>
					 
					 <xsl:if test="$intervinientes != ''  ">
							<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										 
							 <fo:table table-layout="fixed" width="100%" space-after="3mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="1pt" display-align="center"  >
												<fo:block>
													<fo:inline font-weight="bold" display-align="after" font-size="11pt" font-family="Arial,Helvetica,sans-serif" color="#0F3577">
														<xsl:text>Información existente  en el Fichero de Titularidades Financieras</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
								 
									
						<xsl:for-each select="$intervinientes/*[local-name()='Interviniente']"> 					
							<xsl:variable name="intCount" select="position()"/>
 	
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm" margin-left="3%">
										<fo:table-column column-width="proportional-column-width(1.5)"/>
										<fo:table-column column-width="proportional-column-width(1.5)"/>
										<fo:table-column column-width="proportional-column-width(1.2)"/>
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-column column-width="proportional-column-width(1.1)"/>
										<fo:table-column column-width="proportional-column-width(0.8)"/>
										<fo:table-body start-indent="0pt">
											  <fo:table-row>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="6"   >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="10pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold"  color="#0E429C">
																<xsl:text>Resultado </xsl:text><xsl:value-of select="$intCount"/>	
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row border="1pt solid black"   background-color="#E6E6E6" >
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="6"   >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																<xsl:text>Datos Identificativos </xsl:text>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row border="1pt solid black"  >
												<fo:table-cell padding="2pt" display-align="center" border="inherit" border-right-width="0pt"  >
													<fo:block text-align="left" margin="0pt">
														 	<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
															<fo:inline font-weight="bold">
																<xsl:text>Tipo doc. id.: </xsl:text>															
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center"  border="inherit" border-left-width="0pt" >
													<fo:block text-align="left" margin="0pt">
														<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='DetalleInterviniente']/*[local-name()='TipoDocId']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center"  border="inherit"  border-right-width="0pt"  >
													<fo:block text-align="left" margin="0pt">
														<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
															<fo:inline font-weight="bold">
																<xsl:text>Número doc. id.:</xsl:text>														
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center"  border="inherit"  border-left-width="0pt"  > 
													<fo:block text-align="left" margin="0pt">
														<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='DetalleInterviniente']/*[local-name()='NumDocId']"/> 
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" border="inherit"  border-right-width="0pt"  >
													<fo:block text-align="left" margin="0pt">
														<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
															<fo:inline font-weight="bold">
																<xsl:text>País expedición:</xsl:text>
																
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center"  border="inherit" border-left-width="0pt"  >
													<fo:block text-align="left" margin="0pt">
														<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='DetalleInterviniente']/*[local-name()='PaisExpDocId']"/> 
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											
											<fo:table-row border="1pt solid black" >
												<fo:table-cell padding="2pt" border="inherit" border-right-width="0pt" >
													<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt"   >
															<fo:inline font-weight="bold">
																<xsl:text>Nombre completo/razón social. </xsl:text>															
															</fo:inline> 
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center"   border="inherit"  border-left-width="0pt">
													<fo:block text-align="left" margin="0pt">
													<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='DetalleInterviniente']/*[local-name()='NombreCompletoRS']"/>  
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center"  border="inherit"  border-right-width="0pt" >
													<fo:block text-align="left" margin="0pt">
														<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
															<fo:inline font-weight="bold">
																<xsl:text>Fecha nac./const.:</xsl:text>														
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center"  border="inherit"  border-left-width="0pt" >
													<fo:block text-align="left" margin="0pt">
														<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='DetalleInterviniente']/*[local-name()='FechaNacConst']"/> 
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center"  border="inherit"  border-right-width="0pt" >
													<fo:block text-align="left" margin="0pt">
														<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
															<fo:inline font-weight="bold">
																<xsl:text>País nacionalidad: </xsl:text>
																
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center"  border="inherit"   border-left-width="0pt" >
													<fo:block text-align="left" margin="0pt">
														<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
															<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='DetalleInterviniente']/*[local-name()='PaisNacionalidad']"/> 
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<fo:table-row border="1pt solid black" >
												<fo:table-cell padding="2pt" display-align="center"  border="inherit"  border-right-width="0pt" >
													<fo:block text-align="left" margin="0pt">
														<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
															<fo:inline font-weight="bold">
																<xsl:text>País residencia: </xsl:text>															
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center"  border="inherit"  border-left-width="0pt"  > 
													<fo:block text-align="left" margin="0pt">
														<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='DetalleInterviniente']/*[local-name()='PaisResidencia']"/>  
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="4" border="inherit" >
												<fo:block text-align="left" text-indent="2pt"     text-align-last="left" last-line-end-indent="2pt"  margin="0pt"  font-size="8pt"    font-family="Arial,Helvetica,sans-serif" line-height="8pt" space-after.optimum="1pt"   >
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
															 
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>	
										</fo:table-body>
									</fo:table> 
										 <xsl:variable name="entidades" select="*[local-name()='ListaEntidades']"/>
										 	<xsl:if test="$entidades != ''  ">
												<xsl:for-each select="$entidades/*[local-name()='Entidad']"> 
														<fo:block margin-top="10pt" margin-left="5pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
															<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm" margin-left="5%" border="1pt solid black">

																		<fo:table-column column-width="proportional-column-width(1)"/>
																		<fo:table-column column-width="proportional-column-width(1)"/>
																		<fo:table-column column-width="proportional-column-width(1)"/>
																		<fo:table-column column-width="proportional-column-width(1)"/> 
																		<fo:table-body start-indent="0pt">
																			  <fo:table-row>
																				<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="4" >
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold"  color="#0F4FBE" >
																								<xsl:text>Cuentas y depósitos</xsl:text>	
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																			</fo:table-row>
																			<fo:table-row>
																				<fo:table-cell padding="2pt" display-align="center" >
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold"  color="#0F5ADC" >
																									<xsl:text>Entidad</xsl:text>										
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				<fo:table-cell padding="2pt" display-align="center" >
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="normal"  color="#0F5ADC" >
																									<xsl:value-of select="*[local-name()='NombreEntidad']"/>									
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				<fo:table-cell padding="2pt" display-align="center"  >
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold"  color="#0F5ADC" >
																								<xsl:text>NIF entidad </xsl:text>	
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				<fo:table-cell padding="2pt" display-align="center" >
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="normal"  color="#0F5ADC" >
																									<xsl:value-of select="*[local-name()='NifEntidad']"/>									
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																			</fo:table-row>
																		</fo:table-body>
																	</fo:table>
														</fo:block>	 
												
													  <xsl:variable name="productos" select="*[local-name()='ListaProductos']"/>
														<xsl:if test="$productos != ''  "> 
															
																	<fo:block margin-top="10pt" margin-left="7pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm" >
																	<fo:table table-layout="fixed" width="100%" margin-left="6%" border-spacing="5pt" space-after="2mm" border="1pt solid black"      >

																		<fo:table-column column-width="proportional-column-width(1.2)"/>
																		<fo:table-column column-width="proportional-column-width(1.4)"/>
																		<fo:table-column column-width="proportional-column-width(1.4)"/>
																		<fo:table-column column-width="proportional-column-width(1)"/> 
																		 
																		
																		<fo:table-body start-indent="0pt">
																		<xsl:for-each select="$productos/*[local-name()='Producto']">
																				<xsl:variable name="intervenciones" select="*[local-name()='ListaIntervención']"/> 
																				 <xsl:variable name="bgclr">
																					<xsl:choose>
																						<xsl:when test="position() mod 2 =0">#EDF2F8</xsl:when>
																						<xsl:otherwise>white</xsl:otherwise>
																					</xsl:choose>
																				</xsl:variable>
																			  <fo:table-row    background-color="{$bgclr}" border="1pt solid black" >
																				<fo:table-cell padding="2pt" display-align="center"    border="inherit"  border-right-width="0pt">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold">
																								<xsl:text>Núm. cuenta o depósito:  	</xsl:text>	
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				<fo:table-cell padding="2pt" display-align="center"   border="inherit"   border-left-width="0pt">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="normal">
																									<xsl:value-of select="*[local-name()='NumProducto']"/>									
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				<fo:table-cell padding="2pt" display-align="center"    border="inherit"  border-right-width="0pt">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold">
																								<xsl:text>Tipo: </xsl:text>	
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				<fo:table-cell padding="2pt" display-align="center"     border="inherit"  border-left-width="0pt">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="normal">
																									<xsl:value-of select="*[local-name()='TipoProducto']"/>									
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																			</fo:table-row>
																			
																			<fo:table-row   background-color="{$bgclr}"  border="1pt solid black">
																				<fo:table-cell padding="2pt" display-align="center"    border="inherit"   border-right-width="0pt" >
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold">
																								<xsl:text>Fecha apertura: </xsl:text>	
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				<fo:table-cell padding="2pt" display-align="center"     border="inherit"  border-left-width="0pt">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="normal">
																									<xsl:value-of select="*[local-name()='FechaApertura']"/>									
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				<fo:table-cell padding="2pt" display-align="center"    border="inherit"  border-right-width="0pt" >
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold">
																								<xsl:text>Fecha cancelación: </xsl:text>	
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				<fo:table-cell padding="2pt" display-align="center"    border="inherit"  border-left-width="0pt">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="normal">
																									<xsl:value-of select="*[local-name()='FechaCancelacion']"/>									
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																			</fo:table-row>
																			<fo:table-row   background-color="{$bgclr}"  border="1pt solid black">
																				<fo:table-cell padding="2pt" display-align="center"   border="inherit" number-columns-spanned="4"  >
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold" color="#0E429C">
																								<xsl:text>Intervenciones: </xsl:text>	
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																			</fo:table-row>
																			<fo:table-row   background-color="{$bgclr}" border="1pt solid black">
																				<fo:table-cell padding="2pt" display-align="center"      border="inherit">
																					<fo:block text-align="right" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold">
																								<xsl:text>Tipo intervención</xsl:text>	
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				<fo:table-cell padding="2pt" display-align="center"   border="inherit"  >
																					<fo:block text-align="right" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold">
																								<xsl:text>Fecha desde</xsl:text>	
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				<fo:table-cell padding="2pt" display-align="center"   border="inherit">
																					<fo:block text-align="right" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold">
																								<xsl:text>Fecha hasta</xsl:text>	
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				<fo:table-cell padding="2pt" display-align="center"   border="inherit">
																					<fo:block text-align="right" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold">
																								 
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																			</fo:table-row>
																				
																				<xsl:for-each select="*[local-name()='ListaIntervencion']/*[local-name()='Intervencion']">
																					<fo:table-row   background-color="{$bgclr}" border="1pt solid black">
																					<fo:table-cell padding="2pt" display-align="center"    border="inherit">
																						<fo:block text-align="right" margin="0pt">
																							<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																								<fo:inline font-weight="normal">
																									<xsl:value-of select="*[local-name()='TipoInterv']"/>									
																								</fo:inline>
																							</fo:block>
																						</fo:block>
																					</fo:table-cell>
																					<fo:table-cell padding="2pt" display-align="center"   border="inherit">
																						<fo:block text-align="right" margin="0pt">
																							<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																								<fo:inline font-weight="normal">
																									<xsl:value-of select="*[local-name()='FechaDesde']"/>									
																								</fo:inline>
																							</fo:block>
																						</fo:block>
																					</fo:table-cell>
																					<fo:table-cell padding="2pt" display-align="center"      border="inherit">
																						<fo:block text-align="right" margin="0pt">
																							<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																								<fo:inline font-weight="normal">
																									<xsl:value-of select="*[local-name()='FechaHasta']"/>									
																								</fo:inline>
																							</fo:block>
																						</fo:block>
																					</fo:table-cell>
																					<fo:table-cell padding="2pt" display-align="center"   border="inherit">
																					<fo:block text-align="right" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold">
																								 
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																					</fo:table-cell> 
																				</fo:table-row>
																				</xsl:for-each>	
																					</xsl:for-each>	</fo:table-body>
																	</fo:table>
																	
																	</fo:block>
																		
																									
													 	</xsl:if> 
													 	 <xsl:variable name="nombres" select="*[local-name()='ListaNombres']"/>
														<xsl:if test="$nombres != ''  ">
															<fo:block margin-top="10pt" margin-left="7pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
															<fo:table table-layout="fixed" width="100%" space-after="3mm" margin-left="5%">
																<fo:table-body start-indent="0pt">
																	<fo:table-row border="1pt solid black">
																		<fo:table-cell padding="1pt" display-align="center"   >
																			<fo:block>
																				<fo:inline font-weight="bold" display-align="after" color="#0F3577" font-size="9pt" font-family="Arial,Helvetica,sans-serif">
																					<xsl:text>Otros Nombres</xsl:text>
																				</fo:inline>
																			</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																 
															<xsl:for-each select="$nombres/*[local-name()='Nombre']">
																	<fo:table-row border="1pt solid black">
																				<fo:table-cell padding="2pt" display-align="center" >
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="normal">
																								<xsl:value-of select="*[local-name()='NombreCompletoRS']"/>
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
										
														 <xsl:variable name="otrasdocs" select="*[local-name()='ListaDocumentosIdentificativos']"/>
												 
														 <xsl:if test="$otrasdocs != ''  ">
														 <fo:block margin-top="10pt" margin-left="7pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
																	<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm" margin-left="5%">

																		<fo:table-column column-width="proportional-column-width(1)"/> 
																		<fo:table-column column-width="proportional-column-width(0.5)"/> 
																		<fo:table-column column-width="proportional-column-width(1.9)"/> 
																		<fo:table-column column-width="proportional-column-width(1)"/> 
																		<fo:table-column column-width="proportional-column-width(1.2)"/> 
																		<fo:table-column column-width="proportional-column-width(1)"/>  
																<fo:table-body start-indent="0pt">
																	<fo:table-row border="1pt solid black">
																		<fo:table-cell padding="1pt" display-align="center"  number-columns-spanned="6" >
																			<fo:block>
																				<fo:inline font-weight="bold" display-align="after"  color="#0F3577" font-size="9pt" font-family="Arial,Helvetica,sans-serif">
																					<xsl:text>Otras Documentaciones</xsl:text>
																				</fo:inline>
																			</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
																	<xsl:for-each select="$otrasdocs/*[local-name()='DocumentoIdentificativo']">
																	
																		 
																			  <fo:table-row border="1pt solid black" >
																				  <fo:table-cell padding="2pt" display-align="center"   border="inherit"  border-right-width="0pt">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold">
																									<xsl:text>Tipo Doc. Id</xsl:text>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				 <fo:table-cell padding="2pt" display-align="center"   border="inherit"  border-left-width="0pt">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="normal">
																								<xsl:value-of select="*[local-name()='TipoDocId']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				<fo:table-cell padding="2pt" display-align="center"   border="inherit"  border-right-width="0pt">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold">
																									<xsl:text>Número doc. identificativo</xsl:text>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				 <fo:table-cell padding="2pt" display-align="center"   border="inherit"  border-left-width="0pt">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="normal">
																								<xsl:value-of select="*[local-name()='NumDocId']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>	
																				<fo:table-cell padding="2pt" display-align="center"  border="inherit"  border-right-width="0pt">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="bold">
																									<xsl:text>País Expedición</xsl:text>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																				</fo:table-cell>
																				 <fo:table-cell padding="2pt" display-align="center"  border="inherit"  border-left-width="0pt" >
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="9pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-weight="normal">
																								<xsl:value-of select="*[local-name()='PaisExpDocId']"/>
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
												</xsl:for-each>
										 	</xsl:if>
									</xsl:for-each>
								 
								 	 
								 
							 </fo:block>	 
					 	</xsl:if> 
					
				</xsl:when>	  
			</xsl:choose>
		 <fo:table>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="2" padding="0" display-align="center">
							<fo:block>
								<fo:block text-align="center">
									<fo:leader leader-pattern="rule" rule-thickness="1" leader-length="100%" color="white"/>
								</fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					 <fo:table-row>
						<fo:table-cell number-columns-spanned="2" padding="0" display-align="center">
							<fo:block>
								<fo:block text-align="left"  font-size="9pt" >
									<fo:inline font-weight="bold"> 	
									<xsl:text>Aviso legal:</xsl:text>
									</fo:inline>
								</fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
						<fo:table-row>
						<fo:table-cell number-columns-spanned="2" padding="0" display-align="center">
							<fo:block>
								<fo:block text-align="center">
									<fo:leader leader-pattern="rule" rule-thickness="1" leader-length="100%" color="white"/>
								</fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="2" padding="0" display-align="center">
							<fo:block>
								<fo:block font-family="Arial,Helvetica,sans-serif" font-style="italic" text-align="justify" color="#0F3577">
									<fo:inline font-weight="bold"> 
											<fo:block font-size="8pt" > <xsl:text>Los resultados mostrados se han obtenido con la información facilitada en los criterios de búsqueda.</xsl:text></fo:block>
									</fo:inline>
								</fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="2" padding="0" display-align="center">
							<fo:block>
								<fo:block text-align="center">
									<fo:leader leader-pattern="rule" rule-thickness="1" leader-length="100%" color="white"/>
								</fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="2" padding="0" display-align="center">
							<fo:block>
								<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" font-style="italic" text-align="justify" color="#0F3577">
									<fo:inline font-weight="bold">
											<xsl:text>
											 En el caso de resultados obtenidos mediante consulta por interviniente, no es posible certificar que todos los resultados mostrados correspondan a la misma persona física o jurídica. Los resultados se han agrupado por personas que tienen los mismos datos identificativos declarados por las entidades de crédito.</xsl:text>
									</fo:inline>
									 
								</fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="2" padding="0" display-align="center">
							<fo:block>
								<fo:block text-align="center">
									<fo:leader leader-pattern="rule" rule-thickness="1" leader-length="100%" color="white"/>
								</fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="2" padding="0" display-align="center">
							<fo:block>
								<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" font-style="italic" text-align="justify" color="#0F3577">
									<fo:inline font-weight="bold">
											<xsl:text> 
											 El uso de los datos proporcionados como resultado tendrá como finalidad exclusiva la de prevención del blanqueo de capitales y de la financiación del terrorismo.
											</xsl:text>
									</fo:inline>
									 
								</fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				 	<fo:table-row>
						<fo:table-cell number-columns-spanned="2" padding="0" display-align="center">
							<fo:block>
								<fo:block text-align="center">
									<fo:leader leader-pattern="rule" rule-thickness="1" leader-length="100%" color="white"/>
								</fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="2" padding="0" display-align="center">
							<fo:block>
								<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" font-style="italic" text-align="justify" color="#0F3577">
									<fo:inline font-weight="bold">
											<xsl:text> La consulta y acceso al Fichero de Titularidades Financieras que ha dado como respuesta los datos proporcionados a ese punto único queda incorporada al registro de consultas y accesos a que se refiere el artículo 52.3 del Reglamento de la Ley 10/2010, de 28 de abril, de prevención del blanqueo de capitales y de la financiación del terrorismo.

											</xsl:text>
									</fo:inline>
									 
								</fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
						<fo:table-row>
						<fo:table-cell number-columns-spanned="2" padding="0" display-align="center">
							<fo:block>
								<fo:block text-align="center">
									<fo:leader leader-pattern="rule" rule-thickness="1" leader-length="100%" color="white"/>
								</fo:block>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
						<fo:table-row>
						<fo:table-cell number-columns-spanned="2" padding="0" display-align="center">
							<fo:block>
								<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" font-style="italic" text-align="justify" color="#0F3577">
									<fo:inline font-weight="bold">
											<xsl:text>  
La calidad, integridad y veracidad de los datos proporcionados es responsabilidad de las entidades de crédito, según el artículo 51.3 del Reglamento de la Ley 10/2010, de 28 de abril.
											</xsl:text>
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
	</xsl:template>
</xsl:stylesheet>
