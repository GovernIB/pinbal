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
	
	<xsl:param name="ConsultaNPIE"/>
	<xsl:param name="ConsultaNumeroAfiliacion"/>
	<xsl:param name="ConsultaNumeroAfiliacionBeneficiario"/> 
	<xsl:param name="ConsultaEjercicio"/>
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
		<xsl:variable name="estado" select="$datosEspecificos/*[local-name()='Retorno']/*[local-name()='Estado']"/> 
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
		<fo:block text-align="left" margin-top="20pt">
			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
				<fo:inline font-weight="bold">
					<xsl:text>Datos de consulta</xsl:text>
				</fo:inline>
			</fo:block>
		</fo:block>
			<!-- DATOS DE LA CONSULTA DE IDENTIDAD -->
		<fo:block border-style="solid" border="1px 0 0 0" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-after="1mm">
			<fo:table table-layout="fixed" border-spacing="5pt" space-before="2mm">
				<fo:table-column column-width="proportional-column-width(1.4)"/>
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-column column-width="proportional-column-width(1)"/>	
				<fo:table-column column-width="proportional-column-width(1)"/>
				<fo:table-column column-width="proportional-column-width(0.6)"/>
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
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$consentimiento"/>
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
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">						
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
						<!-- NOMBRE Y APELLIDOS -->
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Nombre y apellidos:</xsl:text>
									</fo:inline>
								</fo:block>							
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										<xsl:value-of select="$nomApellidosPeticion"/>								
								</fo:block>							
						</fo:table-cell>
						
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:if test="$docPeticion != '' and $ConsultaNPIE = ''">
											<xsl:text>Documentación:</xsl:text>
										</xsl:if>
										<xsl:if test="$docPeticion = '' and $ConsultaNPIE != ''">
											<xsl:text>NPIE:</xsl:text>
										</xsl:if> 
									</fo:inline>
								</fo:block>							
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										 <xsl:if test="$docPeticion != '' and $ConsultaNPIE = ''">
											<xsl:text>(</xsl:text>
											<xsl:value-of select="$tipoDocPeticion"/>
											<xsl:text>) </xsl:text>
											<xsl:value-of select="$docPeticion"/>	
										</xsl:if>
										<xsl:if test="$docPeticion = '' and $ConsultaNPIE != ''">
											<xsl:value-of select="$ConsultaNPIE"/>
										</xsl:if> 								
								</fo:block>							
						</fo:table-cell>  
					</fo:table-row>
					
					<fo:table-row>
						<fo:table-cell padding="2pt" display-align="center">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Número afiliación:</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">								
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$ConsultaNumeroAfiliacion"/>
								</fo:block>	
						</fo:table-cell> 
						
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="1">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Ejercicio:</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$ConsultaEjercicio"/>
								</fo:block>	
						</fo:table-cell> 
					</fo:table-row> 
 					<fo:table-row>
					   
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="1">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Núm. afiliación beneficiario:</xsl:text>
									</fo:inline>
								</fo:block>	
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<xsl:value-of select="$ConsultaNumeroAfiliacionBeneficiario"/>
								</fo:block>	
						</fo:table-cell>  
					</fo:table-row>
				</fo:table-body>
			</fo:table> 
		</fo:block>

	    <!-- DATOS DE LA RESPUESTA -->
	    <fo:block text-align="left" margin-top="20pt">
			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
				<fo:inline font-weight="bold">
					<xsl:text>Datos de la respuesta</xsl:text>
				</fo:inline>
			</fo:block>
		</fo:block>
	   		<xsl:choose>
				<!-- Miramos el Codigo de Error de la etiqueta Atributos -->
				<xsl:when test="$estado/*[local-name()='CodigoEstado'] != '0' ">
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
				<xsl:when test="$estado/*[local-name()='CodigoEstado'] = '0' ">
					<fo:block border-style="solid" border="1px 0 0 0" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-after="1mm">
						 <xsl:variable name="persona" select="$datosEspecificos/*[local-name()='Retorno']/*[local-name()='Certificado']/*[local-name()='Persona']"/>
						 <xsl:if test="$persona != ''">
								<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								 <fo:table table-layout="fixed" width="100%" space-after="3mm">
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="1pt" display-align="center" background-color="#B0C4DE">
													<fo:block>
														<fo:inline font-weight="bold" display-align="after" font-size="9pt" font-family="Arial,Helvetica,sans-serif">
															<xsl:text>Datos titular</xsl:text>
														</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-column column-width="proportional-column-width(1.1)"/>
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-column column-width="proportional-column-width(1.1)"/>
										<fo:table-column column-width="proportional-column-width(0.95)"/>
										<fo:table-column column-width="proportional-column-width(0.95)"/>
										<fo:table-column column-width="proportional-column-width(0.9)"/>
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Documentación:</xsl:text>															
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:if test="$persona/*[local-name()='TipoDocumento']">
																	<xsl:text>(</xsl:text>
																	<xsl:value-of select="$persona/*[local-name()='TipoDocumento']"/>
																	<xsl:text>) </xsl:text>
																</xsl:if>
																<xsl:value-of select="$persona/*[local-name()='Documento']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell> 
												 
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Nombre y apellidos:</xsl:text>														
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$persona/*[local-name()='Nombre']"/>
																<xsl:if test="$persona/*[local-name()='Apellido1']"> 
																	<xsl:text> </xsl:text>
																	<xsl:value-of select="$persona/*[local-name()='Apellido1']"/>
																</xsl:if>
																<xsl:if test="$persona/*[local-name()='Apellido2']"> 
																	<xsl:text> </xsl:text>
																	<xsl:value-of select="$persona/*[local-name()='Apellido2']"/>
																</xsl:if>
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
																<xsl:text>Número afiliación:</xsl:text>
																
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$persona/*[local-name()='Afiliacion']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table> 
							 </fo:block> 
						 	</xsl:if>
						 
						 
						<xsl:variable name="datosCertificado" select="$datosEspecificos/*[local-name()='Retorno']/*[local-name()='Certificado']/*[local-name()='DatosCertificado']"/>
						 <xsl:if test="$datosCertificado != ''">
								<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								 <fo:table table-layout="fixed" width="100%" space-after="3mm">
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="1pt" display-align="center" background-color="#B0C4DE">
													<fo:block>
														<fo:inline font-weight="bold" display-align="after" font-size="9pt" font-family="Arial,Helvetica,sans-serif">
															<xsl:text>Datos del certificado</xsl:text>
														</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-column column-width="proportional-column-width(1.1)"/>
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-column column-width="proportional-column-width(1.1)"/>
										<fo:table-column column-width="proportional-column-width(0.95)"/>
										<fo:table-column column-width="proportional-column-width(0.95)"/>
										<fo:table-column column-width="proportional-column-width(0.9)"/>
										<fo:table-body start-indent="0pt">
											 
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Código:</xsl:text>															
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$datosCertificado/*[local-name()='Codigo']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Ejercicio:</xsl:text>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$datosCertificado/*[local-name()='Ejercicio']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table> 
							 </fo:block> 
						 	</xsl:if>
						  
						  <xsl:variable name="expedientes" select="$datosCertificado/*[local-name()='Expedientes']"/> 
						 <xsl:if test="$expedientes != '' ">
								<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								 <fo:table table-layout="fixed" width="100%" space-after="3mm">
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="1pt" display-align="center" background-color="#B0C4DE">
													<fo:block>
														<fo:inline font-weight="bold" display-align="after" font-size="9pt" font-family="Arial,Helvetica,sans-serif">
															<xsl:text>Expedientes</xsl:text>
														</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								  
									<xsl:for-each select="$expedientes/*[local-name()='Expediente']"> 
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
											<fo:table-column column-width="proportional-column-width(1.25)"/>
											<fo:table-column column-width="proportional-column-width(1.8)"/>
											<fo:table-column column-width="proportional-column-width(0.75)"/>
											<fo:table-column column-width="proportional-column-width(0.75)"/>
											<fo:table-column column-width="proportional-column-width(0.70)"/>
											<fo:table-column column-width="proportional-column-width(0.75)"/>
											
											<fo:table-header>
		                                      	<fo:table-row>
		                                       		<fo:table-cell padding="0pt" display-align="center"  background-color="#B0C4DE">
			                                            <fo:block text-align="right" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Nº expediente</xsl:text>															
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													
													<fo:table-cell padding="0pt" display-align="center"  background-color="#B0C4DE">
			                                            <fo:block text-align="right" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Prestación concedida</xsl:text>															
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													
													
													<fo:table-cell padding="0pt" display-align="center"  background-color="#B0C4DE">
			                                            <fo:block text-align="right" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Fecha resolución</xsl:text>															
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													
													<fo:table-cell padding="0pt" display-align="center"  background-color="#B0C4DE">
			                                            <fo:block text-align="right" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Importe resolución (€)</xsl:text>															
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													
													<fo:table-cell padding="0pt" display-align="center"  background-color="#B0C4DE">
			                                            <fo:block text-align="right" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Fecha factura</xsl:text>															
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													
													<fo:table-cell padding="0pt" display-align="center"  background-color="#B0C4DE">
			                                            <fo:block text-align="right" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Importe factura (€)</xsl:text>															
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-header>
											
											<fo:table-body start-indent="0pt">
												<fo:table-row> 
													<fo:table-cell padding="2pt" display-align="after" >
														<fo:block text-align="right" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='NumeroExpediente']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell> 
													<fo:table-cell padding="2pt" display-align="after" >
														<fo:block text-align="right" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='Prestacion']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell> 
													<fo:table-cell padding="2pt" display-align="after" >
														<fo:block text-align="right" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:if test="*[local-name()='FechaResolucion']">
																		<xsl:value-of select="concat(substring(string(*[local-name()='FechaResolucion']),9,2),'/',substring(string(*[local-name()='FechaResolucion']),6,2),'/',substring(string(*[local-name()='FechaResolucion']),1,4))"/>
																	</xsl:if>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell> 
													<fo:table-cell padding="2pt" display-align="after" >
														<fo:block text-align="right" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='ImporteResolucion']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell> 
													<fo:table-cell padding="2pt" display-align="after" >
														<fo:block text-align="right" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:if test="*[local-name()='FechaFactura']">
																		<xsl:value-of select="concat(substring(string(*[local-name()='FechaFactura']),9,2),'/',substring(string(*[local-name()='FechaFactura']),6,2),'/',substring(string(*[local-name()='FechaFactura']),1,4))"/>
																	</xsl:if>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell> 
													<fo:table-cell padding="2pt" display-align="after" >
														<fo:block text-align="right" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='ImporteFactura']"/>
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
					</fo:block>
			 	</xsl:when>	  
			</xsl:choose>
	</xsl:template>
</xsl:stylesheet>