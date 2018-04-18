<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0" xmlns:str="http://www.str.com" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:functx="http://www.functx.com" exclude-result-prefixes="str functx">
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
	<xsl:param name="Ejercicio"/>
	
	
	<!-- Para provocar salto de linea cuando un texto es demasiado largo -->
	<xsl:template name="intersperse-with-zero-spaces">
		<xsl:param name="str"/>
		<xsl:variable name="spacechars">
			&#x9;&#xA;
			&#x2000;&#x2001;&#x2002;&#x2003;&#x2004;&#x2005;
			&#x2006;&#x2007;&#x2008;&#x2009;&#x200A;&#x200B;
		</xsl:variable>
		<xsl:if test="string-length($str) &gt; 0">
			<xsl:variable name="c1" select="substring($str, 1, 1)"/>
			<xsl:variable name="c2" select="substring($str, 2, 1)"/>
			<xsl:value-of select="$c1"/>
			<xsl:if test="$c2 != '' and
not(contains($spacechars, $c1) or
contains($spacechars, $c2))">
				<xsl:text>&#x200B;</xsl:text>
			</xsl:if>
			<xsl:call-template name="intersperse-with-zero-spaces">
				<xsl:with-param name="str" select="substring($str, 2)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="/">
		<xsl:variable name="datosEspecificos" select="/*[local-name()='TransmisionDatos']/*[local-name()='DatosEspecificos']"/>
		<xsl:variable name="datosTitular" select="$datosEspecificos/*[local-name()='DatosTitular']"/>
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
		<xsl:variable name="datosTitular" select="//*[local-name()='TransmisionDatos']/*[local-name()='DatosGenericos']/*[local-name()='Titular']"/>
		<!-- <xsl:variable name="estado" select="$datosEspecificos/*[local-name()='EstadoResultado']"/> -->
		
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
				<xsl:text> </xsl:text>
				<xsl:value-of select="$idPeticion"/>
			</fo:inline>
		</fo:block>
		<fo:block text-align="center" font-family="Arial,sans-serif" font-size="8pt" line-height="10pt" space-after.optimum="1pt" display-align="after" color="#2F4F4F">
			<fo:inline font-weight="bold">
				<xsl:text>Identificador de transmisión:</xsl:text>
			</fo:inline>
			<fo:inline font-weight="bold">
				<xsl:text> </xsl:text>
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
					<fo:table-row>
					   <!-- Periodo consulta -->
						<fo:table-cell padding="2pt" display-align="center" >							
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
									<fo:inline font-weight="bold">
										<xsl:text>Periodo Consulta:     </xsl:text>
									</fo:inline>  
								</fo:block>													
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5">						
								<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
										<xsl:value-of select="$Ejercicio"/>							
								</fo:block>							
						</fo:table-cell> 
					</fo:table-row>	
				</fo:table-body>
			</fo:table> 
		</fo:block>

	    <!-- DATOS DE LA RESPUESTA -->	
		<xsl:choose>
			<!-- Miramos el Codigo de Error de la etiqueta Atributos -->
			<xsl:when test="$datosEspecificos/*[local-name()='Cabecera']/*[local-name()='CodRet'] != '1000' ">
				<xsl:variable name="cabecera" select="$datosEspecificos/*[local-name()='Cabecera']"/>
				<fo:block margin-top="40pt" text-align="center" font-family="Arial,sans-serif" font-size="10pt" line-height="10pt" space-after.optimum="1pt" display-align="after">
					<fo:inline font-weight="bold">
						<xsl:text>RESULTADO DE LA CONSULTA:      </xsl:text>
						<xsl:value-of select="$datosEspecificos/*[local-name()='Cabecera']/*[local-name()='CodRet']"/>
						<xsl:text>  -  </xsl:text>
						<xsl:value-of select="$datosEspecificos/*[local-name()='Cabecera']/*[local-name()='DescripcionError']"/>
					</fo:inline>
				</fo:block>
			</xsl:when>
			<xsl:when test="$datosEspecificos/*[local-name()='Cabecera']/*[local-name()='CodRet'] = '1000'">
				<fo:block text-align="left" margin-top="25pt">
					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
						<fo:inline font-weight="bold">
							<xsl:text>Datos de respuesta</xsl:text>
						</fo:inline>
					</fo:block>
				</fo:block>
				<fo:block border-style="solid" border-width="1px 1px 1px 1px">
					<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-column column-width="proportional-column-width(0.6)"/>
								<fo:table-column column-width="proportional-column-width(0.8)"/>
								<fo:table-column column-width="proportional-column-width(0.8)"/>
								<fo:table-column column-width="proportional-column-width(1.8)"/>
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt" font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Referencia:</xsl:text>
													</fo:inline>												
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$datosEspecificos/*[local-name()='Cabecera']/*[local-name()='Referencia']"/>
													</fo:inline> 
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Fecha emisión:</xsl:text>														
													</fo:inline>												
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt" font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:if test="$datosEspecificos/*[local-name()='Cabecera']/*[local-name()='FechaEmision'] != ''">
															<xsl:value-of select="concat(substring(string($datosEspecificos/*[local-name()='Cabecera']/*[local-name()='FechaEmision']),9,2),'/',substring(string($datosEspecificos/*[local-name()='Cabecera']/*[local-name()='FechaEmision']),6,2),'/',substring(string($datosEspecificos/*[local-name()='Cabecera']/*[local-name()='FechaEmision']),1,4))"/>
														</xsl:if>
													</fo:inline>
												</fo:block>
										</fo:table-cell>
									</fo:table-row>	
									
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt" font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Ejercicio:</xsl:text>
													</fo:inline>												
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$datosEspecificos/*[local-name()='Cabecera']/*[local-name()='Ejercicio']"/>
													</fo:inline> 
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">	</fo:inline>												
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt" font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
													</fo:inline>
												</fo:block>
										</fo:table-cell>
									</fo:table-row>											
								</fo:table-body>
							</fo:table>
													
						</fo:block>
					<xsl:if test="$datosTitular">
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
								<fo:table-column column-width="proportional-column-width(0.6)"/>
								<fo:table-column column-width="proportional-column-width(0.8)"/>
								<fo:table-column column-width="proportional-column-width(0.8)"/>
								<fo:table-column column-width="proportional-column-width(1.8)"/>
								<fo:table-body start-indent="0pt">
									<!-- Fila 1 Hijo -->
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Documentación:</xsl:text>
													</fo:inline>
												 
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:text>(</xsl:text>
														<xsl:value-of select="$datosTitular/*[local-name()='TipoDocumentacion']"/>
														<xsl:text>)  </xsl:text>
														<xsl:value-of select="$datosTitular/*[local-name()='Documentacion']"/>
													</fo:inline>
											</fo:block>
										</fo:table-cell>
																			
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Nombre y apellidos:</xsl:text>
													</fo:inline> 
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
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
										</fo:table-cell>																		
									</fo:table-row>												
								</fo:table-body>
							</fo:table>												
						</fo:block>
					</xsl:if>
					
					<!-- **************************************************
                            IMPUTACIONES
                         *************************************************** -->					
					<xsl:if test="$datosEspecificos/*[local-name()='Imputaciones'] != ''">
						<!-- Panel Imputaciones -->
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													IMPUTACIONES
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						<xsl:for-each select="$datosEspecificos/*[local-name()='Imputaciones']/*[local-name()='DatosEconomicos']">
								<xsl:variable name="datoEconomico" select="current()"/>													
								<!-- Tabla con los datos de operación -->
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
								<xsl:if test="position() = 1">
										<fo:table-header  start-indent="20pt">
											<fo:table-cell display-align="after" padding="6px" height="17pt" background-color="#C0C0C0">
												<fo:block font-weight="bold" font-size="8pt" font-family="Arial,Helvetica,sans-serif">Texto</fo:block>
											</fo:table-cell>
											<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
												<fo:block font-weight="bold" font-size="8pt" font-family="Arial,Helvetica,sans-serif">Valor</fo:block>
											</fo:table-cell>											
										</fo:table-header>
									</xsl:if>
									<fo:table-body start-indent="20pt">															
										<fo:table-row>
											<fo:table-cell padding="6pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															 <xsl:value-of select="$datoEconomico/*[local-name()='Texto']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											
											<fo:table-cell padding="6pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$datoEconomico/*[local-name()='Signo']"/>
															<xsl:value-of select="$datoEconomico/*[local-name()='Enteros']"/>
															<xsl:text>,</xsl:text>
															<xsl:value-of select="$datoEconomico/*[local-name()='Decimales']"/>															
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>											
									</fo:table-body>
								</fo:table>
						</xsl:for-each><!-- fin FOR EACH DE DATOS ECONOMICOS -->
						
						<xsl:if test="$datosEspecificos/*[local-name()='Imputaciones']/*[local-name()='Cola']">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Otros datos imputaciones </xsl:text>
														<xsl:value-of select="$datosEspecificos/*[local-name()='Imputaciones']/*[local-name()='Cola']"/>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</xsl:if>
					</xsl:if>
					
					<!-- **************************************************
                            IRPF
                         *************************************************** -->
                         
                    <xsl:if test="$datosEspecificos/*[local-name()='irpf']">
						<!-- Panel IRPF -->
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>IRPF</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						
						<!-- Panel Cabecera renta -->
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Cabecera renta</xsl:text>
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
								<fo:table-column column-width="proportional-column-width(4.5)"/>
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>NIF solicitante:</xsl:text>
													</fo:inline>											 
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="2pt" display-align="center">
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='CabeceraRenta']/*[local-name()='NifSolicitante']"/>
													</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>									
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Nombre solicitante:</xsl:text>
													</fo:inline> 
											</fo:block>
										</fo:table-cell>										
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														 <xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='CabeceraRenta']/*[local-name()='NombreSolicitante']"/>
													</fo:inline>
											</fo:block>
										</fo:table-cell>																		
									</fo:table-row>												
									<fo:table-row>									
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Primer declarante:</xsl:text>
													</fo:inline> 
											</fo:block>
										</fo:table-cell>										
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														 <xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='CabeceraRenta']/*[local-name()='PrimerDeclarante']"/>
													</fo:inline>
											</fo:block>
										</fo:table-cell>																		
									</fo:table-row>
									<xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='CabeceraRenta']/*[local-name()='SegundoTitular'] != '' ">
										<fo:table-row>									
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Segundo titular:</xsl:text>
														</fo:inline> 
												</fo:block>
											</fo:table-cell>										
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															 <xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='CabeceraRenta']/*[local-name()='SegundoTitular']"/>
														</fo:inline>
												</fo:block>
											</fo:table-cell>																		
										</fo:table-row>
									</xsl:if>
									<fo:table-row>									
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Modelo:</xsl:text>
														</fo:inline> 
												</fo:block>
											</fo:table-cell>										
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															 <xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='CabeceraRenta']/*[local-name()='Modelo']"/>
														</fo:inline>
												</fo:block>
											</fo:table-cell>																		
									</fo:table-row>
									<fo:table-row>									
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Tributación:</xsl:text>
														</fo:inline> 
												</fo:block>
											</fo:table-cell>										
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															 <xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='CabeceraRenta']/*[local-name()='Tributacion']"/>
														</fo:inline>
												</fo:block>
											</fo:table-cell>																		
									</fo:table-row>
									<fo:table-row>									
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Origen datos:</xsl:text>
														</fo:inline> 
												</fo:block>
											</fo:table-cell>										
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															 <xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='CabeceraRenta']/*[local-name()='OrigenDatos']"/>
														</fo:inline>
												</fo:block>
											</fo:table-cell>																		
										</fo:table-row>
								</fo:table-body>
							</fo:table>												
						</fo:block>
						
						<!-- Datos económicos IRPF -->
						<xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosEconomicos']">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos económicos</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</xsl:if>
					
						<xsl:for-each select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosEconomicos']">
								<xsl:variable name="datoEconomico" select="current()"/>													
								<!-- Tabla con los datos de operación -->
								<fo:table table-layout="fixed" width="50%" border-spacing="5pt" space-after="2mm">
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<xsl:if test="position() = 1">
										<fo:table-header  start-indent="20pt">
											
											<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
												<fo:block font-weight="bold" font-size="8pt"  text-align="right" font-family="Arial,Helvetica,sans-serif">Casilla</fo:block>
											</fo:table-cell>	
											<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
												<fo:block font-weight="bold" font-size="8pt" font-family="Arial,Helvetica,sans-serif" text-align="right" >
													Valor
												</fo:block>
											</fo:table-cell>										
										</fo:table-header>
									</xsl:if>
									<fo:table-body start-indent="20pt">															
										<fo:table-row>										
											<fo:table-cell padding="6pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
														<fo:inline font-weight="normal">
															 <xsl:value-of select="$datoEconomico/*[local-name()='DECasilla']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											
											<fo:table-cell padding="6pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
														<fo:inline font-weight="normal">
															 <xsl:value-of select="$datoEconomico/*[local-name()='DESigno']"/>
															  <xsl:value-of select="$datoEconomico/*[local-name()='DEEnteros']"/>
															  <xsl:text>,</xsl:text>
															  <xsl:value-of select="$datoEconomico/*[local-name()='DEDecimales']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>											
									</fo:table-body>
								</fo:table>
						</xsl:for-each><!-- fin FOR EACH DE DATOS ECONOMICOS -->
						
						<!-- Otros datos IRPF -->
						<xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='OtrosDatos']">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos económicos</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</xsl:if>
						<xsl:for-each select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='OtrosDatos']">
								<xsl:variable name="otroDato" select="current()"/>													
								<!-- Tabla con los datos de operación -->
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-column column-width="proportional-column-width(3)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<xsl:if test="position() = 1">
										<fo:table-header  start-indent="20pt">
											<fo:table-cell display-align="after" padding="6px"
												height="17pt" background-color="#C0C0C0">
												<fo:block font-weight="bold" font-size="8pt"	font-family="Arial,Helvetica,sans-serif">Descripción</fo:block>
											</fo:table-cell>
											<fo:table-cell display-align="after" padding="6px"
												height="17pt" background-color="#C0C0C0">
												<fo:block font-weight="bold" font-size="8pt" font-family="Arial,Helvetica,sans-serif" text-align="right">Valor</fo:block>
											</fo:table-cell>																	
										</fo:table-header>
									</xsl:if>
									<fo:table-body start-indent="20pt">															
										<fo:table-row>
											<fo:table-cell padding="6pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															 <xsl:value-of select="$otroDato/*[local-name()='ODDescripcion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
																						
											<fo:table-cell padding="6pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
														<fo:inline font-weight="normal">
															 <xsl:if test="$otroDato/*[local-name()='ODSigno']!=''">
																 <xsl:value-of select="$otroDato/*[local-name()='ODSigno']"/><xsl:text> </xsl:text>
															 </xsl:if>
															 <xsl:if test="$otroDato/*[local-name()='ODEnteros']!=''">
																 <xsl:value-of select="$otroDato/*[local-name()='ODEnteros']"/>
															</xsl:if>
															<xsl:text>,</xsl:text>
															<xsl:if test="$otroDato/*[local-name()='ODDecimales']!=''">
																 <xsl:value-of select="$otroDato/*[local-name()='ODDecimales']"/>
															</xsl:if>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>											
									</fo:table-body>
								</fo:table>
						</xsl:for-each><!-- fin FOR EACH DE OTROS DATOS -->
					
						<!-- Datos adiccionales IRPF -->
						<xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos adiccionales</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</xsl:if>
						<!-- DCDatosPersonales -->
						<xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosPersonales']">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="2mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos personales</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
							<fo:block border-style="solid" border="0 0 0 0" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-after="1mm">
								<fo:table table-layout="fixed" border-spacing="5pt" space-before="2mm">
									<fo:table-column column-width="proportional-column-width(6)"/>
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center" >							
													<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
														<xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosPersonales']/*[local-name()='DCLiteral']"/>
													</fo:block>							
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
								<fo:table table-layout="fixed" border-spacing="5pt" space-before="2mm">
									<fo:table-column column-width="proportional-column-width(2)"/>
									<fo:table-column column-width="proportional-column-width(4)"/>
									<fo:table-body start-indent="0pt">									
										 <!-- Estado Civil -->
										 <xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosPersonales']/*[local-name()='DCEstadoCivil']">
											<fo:table-row>											
												<fo:table-cell padding="2pt" display-align="center" >							
														<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
															<fo:inline font-weight="bold">
																<xsl:text>Estado civil:</xsl:text>
															</fo:inline>
														</fo:block>							
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" >							
														<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
															<xsl:text>(</xsl:text>	
															<xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosPersonales']/*[local-name()='DCEstadoCivil']/*[local-name()='DCFecha']"/>
															<xsl:text>)  </xsl:text>
															<xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosPersonales']/*[local-name()='DCEstadoCivil']/*[local-name()='DCContenido']"/>
														</fo:block>							
												</fo:table-cell>
											</fo:table-row>
										</xsl:if>
										<!-- Fecha nacimiento -->
										 <xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosPersonales']/*[local-name()='DCFechaNac']">																		
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center" >							
														<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
															<fo:inline font-weight="bold">
																<xsl:text>Fecha nacimiento:</xsl:text>
															</fo:inline>
														</fo:block>							
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" >							
														<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
																<xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosPersonales']/*[local-name()='DCFechaNac']"/>
														</fo:block>							
												</fo:table-cell>
											</fo:table-row>	
										</xsl:if>								
										  <!-- Minusvalia -->
										  <xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosPersonales']/*[local-name()='DCMinusvalia']">																		
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center"  >							
														<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
															<fo:inline font-weight="bold">
																<xsl:text>Minusvalía:</xsl:text>
															</fo:inline>
														</fo:block>							
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" >							
														<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
															<xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosPersonales']/*[local-name()='DCMinusvalia']"/>
														</fo:block>							
												</fo:table-cell>
											</fo:table-row>	
										</xsl:if>							
									</fo:table-body>
								</fo:table>
							</fo:block>
						</xsl:if>
						<!-- DCDatosConyuge -->
						<xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosConyuge']">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="2mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos conyuge</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
							<fo:block border-style="solid" border="0 0 0 0" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-after="1mm">
								<fo:table table-layout="fixed" border-spacing="5pt" space-before="2mm">
									<fo:table-column column-width="proportional-column-width(6)"/>
									<fo:table-body start-indent="0pt">			
										<fo:table-row>	
											<fo:table-cell padding="2pt" display-align="center" >							
													<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
														<xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCConyuge']/*[local-name()='DCLiteral']"/>
													</fo:block>							
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
								<fo:table table-layout="fixed" border-spacing="5pt" space-before="2mm">
									<fo:table-column column-width="proportional-column-width(2)"/>
									<fo:table-column column-width="proportional-column-width(4)"/>
									<fo:table-body start-indent="0pt">							
										<!-- Fecha nacimiento -->
										<xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosConyuge']/*[local-name()='DCFechaNac']">												
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center" >							
														<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
															<fo:inline font-weight="bold">
																<xsl:text>Fecha nacimiento:</xsl:text>
															</fo:inline>
														</fo:block>							
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" >							
														<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
																<xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosConyuge']/*[local-name()='DCFechaNac']"/>
														</fo:block>							
												</fo:table-cell>
											</fo:table-row>
										</xsl:if>
										  <!-- Minusvalia -->
										 <xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosConyuge']/*[local-name()='DCMinusvalia']">
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center"  >							
														<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
															<fo:inline font-weight="bold">
																<xsl:text>Minusvalía:</xsl:text>
															</fo:inline>
														</fo:block>							
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" >							
														<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
															<xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosConyuge']/*[local-name()='DCMinusvalia']"/>
														</fo:block>							
												</fo:table-cell>
											</fo:table-row>	
										</xsl:if>							
									</fo:table-body>
								</fo:table>
							</fo:block>
						</xsl:if>
						<!-- DCDatosHijos -->
						<xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosHijos']">	
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="2mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos hijos</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
							<fo:block  text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-after="1mm">
								<fo:table table-layout="fixed" border-spacing="5pt" space-before="2mm">
									<fo:table-column column-width="proportional-column-width(6)"/>
									<fo:table-body start-indent="0pt">
										<!-- DCLiteral -->									
										<fo:table-row>	
											<fo:table-cell padding="2pt" display-align="center" >							
													<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
														<xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosHijos']/*[local-name()='DCLiteral']"/>
													</fo:block>							
											</fo:table-cell>
										</fo:table-row>						
									</fo:table-body>
								</fo:table>
							</fo:block>
							<fo:block text-align="left" margin-top="10pt">
								<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
									<xsl:for-each select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosHijos']/*[local-name()='DCNumHijos']">
											<xsl:variable name="hijo" select="current()"/>													
											<!-- Tabla con los datos de operación -->
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(2)"/>
												<fo:table-column column-width="proportional-column-width(4)"/>
												<fo:table-body start-indent="30pt">	
													<fo:table-row>												
															<fo:table-cell padding="6pt" display-align="center" background-color="#C0C0C0">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold" >		
																			Hijo
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center" background-color="#C0C0C0">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">																		 
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													<!-- Nombre Hijo -->
													<xsl:if test="$hijo/*[local-name()='DCNombreHijo']">
														<fo:table-row>												
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Nombre:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$hijo/*[local-name()='DCNombreHijo']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- Fecha nacimiento -->
													<xsl:if test="$hijo/*[local-name()='DCFechaNacim']">
														<fo:table-row>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Fecha nacimiento:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$hijo/*[local-name()='DCFechaNacim']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- Fecha Adopción -->
													<xsl:if test="$hijo/*[local-name()='DCFechaAdopc']">
														<fo:table-row>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Fecha adopción:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$hijo/*[local-name()='DCFechaAdopc']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- DCMinusvalia -->
													<xsl:if test="$hijo/*[local-name()='DCMinusvalia']">
														<fo:table-row>	
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Minusvalía:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$hijo/*[local-name()='DCMinusvalia']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- DCVinculacion -->
													<xsl:if test="$hijo/*[local-name()='DCVinculacion']">
														<fo:table-row>	
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Vinculación:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$hijo/*[local-name()='DCVinculacion']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>				
														</fo:table-row>
													</xsl:if>												
												</fo:table-body>
											</fo:table>
										</xsl:for-each><!-- fin FOR EACH   -->      
								</fo:block>
							</fo:block>		
						</xsl:if>
						<!-- DCDatosAscend -->
						<xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosAscend']">	
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="2mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos ascendientes</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
							<fo:block  text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-after="1mm">
								<fo:table table-layout="fixed" border-spacing="5pt" space-before="2mm">
									<fo:table-column column-width="proportional-column-width(6)"/>
									<fo:table-body start-indent="0pt">
										<!-- DCLiteral -->									
										<fo:table-row>	
											<fo:table-cell padding="2pt" display-align="center" >							
													<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
														<xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosAscend']/*[local-name()='DCLiteral']"/>
													</fo:block>							
											</fo:table-cell>
										</fo:table-row>						
									</fo:table-body>
								</fo:table>
							</fo:block>
							<fo:block text-align="left" margin-top="10pt">
								<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
									<xsl:for-each select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosHijos']/*[local-name()='DCNumHijos']">
											<xsl:variable name="ascend" select="current()"/>													
											<!-- Tabla con los datos de operación -->
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(2)"/>
												<fo:table-column column-width="proportional-column-width(4)"/>
												<fo:table-body start-indent="30pt">	
													<fo:table-row>												
															<fo:table-cell padding="6pt" display-align="center" background-color="#C0C0C0">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold" >		
																			Ascendiente
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center" background-color="#C0C0C0">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">																		 
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													<!-- Nombre Ascendiente -->
													<xsl:if test="$ascend/*[local-name()='DCNombreAscend']">
														<fo:table-row>												
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Nombre:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$ascend/*[local-name()='DCNombreAscend']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- Fecha nacimiento -->
													<xsl:if test="$ascend/*[local-name()='DCFechaNacim']">
														<fo:table-row>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Fecha nacimiento:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$ascend/*[local-name()='DCFechaNacim']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- Minusvalía -->
													<xsl:if test="$ascend/*[local-name()='DCMinusvalia']">
														<fo:table-row>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Minusvalía:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$ascend/*[local-name()='DCMinusvalia']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- Vinculación -->
													<xsl:if test="$ascend/*[local-name()='DCVinculacion']">
														<fo:table-row>	
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Situación:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$ascend/*[local-name()='DCVinculacion']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- Convivencia -->
													<xsl:if test="$ascend/*[local-name()='DCConvivencia']">
														<fo:table-row>	
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Convivencia:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$ascend/*[local-name()='DCConvivencia']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>				
														</fo:table-row>
													</xsl:if>												
												</fo:table-body>
											</fo:table>
										</xsl:for-each><!-- fin FOR EACH   -->      
								</fo:block>
							</fo:block>		
						</xsl:if>
						<!-- DCDatosVivienda -->	
						<xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosVivienda']">					
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="2mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos viviendas</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
							<fo:block  text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-after="1mm">
								<fo:table table-layout="fixed" border-spacing="5pt" space-before="2mm">
									<fo:table-column column-width="proportional-column-width(6)"/>
									<fo:table-body start-indent="0pt">
										<!-- DCLiteral -->									
										<fo:table-row>	
											<fo:table-cell padding="2pt" display-align="center" >							
													<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
														<xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosVivienda']/*[local-name()='DCLiteral']"/>
													</fo:block>							
											</fo:table-cell>
										</fo:table-row>						
									</fo:table-body>
								</fo:table>
							</fo:block>
							<fo:block text-align="left" margin-top="10pt">
								<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
									<xsl:for-each select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosVivienda']/*[local-name()='DCNumViviendas']">
											<xsl:variable name="vivienda" select="current()"/>													
											<!-- Tabla con los datos de operación -->
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(2)"/>
												<fo:table-column column-width="proportional-column-width(4)"/>
												<fo:table-body start-indent="30pt">	
													<fo:table-row>												
															<fo:table-cell padding="6pt" display-align="center" background-color="#C0C0C0">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold" >		
																			Vivienda
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center" background-color="#C0C0C0">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">																		 
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													<!-- Contribución -->
													<xsl:if test="$vivienda/*[local-name()='DCContrib']">
														<fo:table-row>												
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Contribución:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$vivienda/*[local-name()='DCContrib']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- Titularidad -->
													<xsl:if test="$vivienda/*[local-name()='DCParticipac']">
														<fo:table-row>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Participación:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$vivienda/*[local-name()='DCParticipac']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- Situación -->
													<xsl:if test="$vivienda/*[local-name()='DCRefCatastr']">
														<fo:table-row>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Ref. Catastral:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$vivienda/*[local-name()='DCRefCatastr']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- Situacion -->
													<xsl:if test="$vivienda/*[local-name()='DCSituacion']">
														<fo:table-row>	
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Situación:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$vivienda/*[local-name()='DCSituacion']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- Titularidad -->
													<xsl:if test="$vivienda/*[local-name()='DCTitularidad']">
														<fo:table-row>	
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Titularidad:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$vivienda/*[local-name()='DCTitularidad']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>				
														</fo:table-row>
													</xsl:if>												
												</fo:table-body>
											</fo:table>
										</xsl:for-each><!-- fin FOR EACH   -->      
								</fo:block>
							</fo:block>
						</xsl:if>
						<!-- DCDatosInmuebles -->
						<xsl:if test="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosInmuebles']">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="2mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos inmuebles</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
							<fo:block  text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-after="1mm">
								<fo:table table-layout="fixed" border-spacing="5pt" space-before="2mm">
									<fo:table-column column-width="proportional-column-width(6)"/>
									<fo:table-body start-indent="0pt">
										<!-- DCLiteral -->									
										<fo:table-row>	
											<fo:table-cell padding="2pt" display-align="center" >							
													<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" >
														<xsl:value-of select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosInmuebles']/*[local-name()='DCLiteral']"/>
													</fo:block>							
											</fo:table-cell>
										</fo:table-row>						
									</fo:table-body>
								</fo:table>
							</fo:block>
							<fo:block text-align="left" margin-top="10pt">
								<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
									<xsl:for-each select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='DatosCola']/*[local-name()='DCDatosInmuebles']/*[local-name()='DCNumInmuebles']">
											<xsl:variable name="inmueble" select="current()"/>													
											<!-- Tabla con los datos de operación -->
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(2)"/>
												<fo:table-column column-width="proportional-column-width(4)"/>
												<fo:table-body start-indent="30pt">	
													<fo:table-row>												
															<fo:table-cell padding="6pt" display-align="center" background-color="#C0C0C0">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold" >		
																			Inmueble
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center" background-color="#C0C0C0">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">																		 
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													<!-- Contribución -->
													<xsl:if test="$inmueble/*[local-name()='DCContrib']">
														<fo:table-row>												
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Contribución:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$inmueble/*[local-name()='DCContrib']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- Titularidad -->
													<xsl:if test="$inmueble/*[local-name()='DCTitularidad']">
														<fo:table-row>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Titularidad:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$inmueble/*[local-name()='DCTitularidad']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- Situación -->
													<xsl:if test="$inmueble/*[local-name()='DCSituacion']">
														<fo:table-row>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Situación:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$inmueble/*[local-name()='DCSituacion']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- Ref Catastral -->
													<xsl:if test="$inmueble/*[local-name()='DCRefCatastr']">
														<fo:table-row>	
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Ref. Catastral:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$inmueble/*[local-name()='DCRefCatastr']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>	
													</xsl:if>
													<!-- Uso -->
													<xsl:if test="$inmueble/*[local-name()='DCUso']">
														<fo:table-row>	
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Uso:
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$inmueble/*[local-name()='DCUso']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>				
														</fo:table-row>
													</xsl:if>	
													<!-- Renta Imputada -->
													<xsl:if test="$inmueble/*[local-name()='DCRentaImputada']">
														<fo:table-row>	
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			 Renta imputada:
																		</fo:inline>
																	</fo:block> 
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="6pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			 <xsl:value-of select="$inmueble/*[local-name()='DCRentaImputada']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>	
														</fo:table-row>	
													</xsl:if>
												</fo:table-body>
											</fo:table>
										</xsl:for-each><!-- fin FOR EACH   -->      
								</fo:block>
							</fo:block>
						</xsl:if>
						
						<xsl:variable name="rentaAgraria" select="$datosEspecificos/*[local-name()='irpf']/*[local-name()='AERentaAgraria']"/>						
						<xsl:if test="$rentaAgraria">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos renta agraria</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
							<xsl:if test="$rentaAgraria/*[local-name()='AEGenerales1']">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="3pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos generales 1</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							<xsl:for-each select="$rentaAgraria/*[local-name()='AEGenerales1']/*[local-name()='AEDatosCasillas']">
									<xsl:variable name="datoGeneral1" select="current()"/>													
									<!-- Tabla con los datos de operación -->
									<fo:table table-layout="fixed" width="50%" border-spacing="5pt" space-after="2mm">
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-column column-width="proportional-column-width(1)"/>
										<xsl:if test="position() = 1">
											<fo:table-header  start-indent="20pt">
												
												<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
													<fo:block font-weight="bold" font-size="8pt"  text-align="right" font-family="Arial,Helvetica,sans-serif">Casilla</fo:block>
												</fo:table-cell>	
												<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
													<fo:block font-weight="bold" font-size="8pt" font-family="Arial,Helvetica,sans-serif" text-align="right" >
														Valor
													</fo:block>
												</fo:table-cell>										
											</fo:table-header>
										</xsl:if>
										<fo:table-body start-indent="20pt">															
											<fo:table-row>										
												<fo:table-cell padding="6pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
															<fo:inline font-weight="normal">
																 <xsl:value-of select="$datoGeneral1/*[local-name()='AECasilla']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												
												<fo:table-cell padding="6pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
															<fo:inline font-weight="normal">
																 <xsl:value-of select="$datoGeneral1/*[local-name()='AESigno']"/>
																  <xsl:value-of select="$datoGeneral1/*[local-name()='AEEnteros']"/>
																  <xsl:text>,</xsl:text>
																  <xsl:value-of select="$datoGeneral1/*[local-name()='AEDecimales']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>											
										</fo:table-body>
									</fo:table>
							</xsl:for-each><!-- fin FOR EACH DE generales 1 -->
							 </fo:block>
							</xsl:if>					
					     
					     
					     <xsl:if test="$rentaAgraria/*[local-name()='AEEstimacionDirecta']">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="3pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Estimación directa</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
									 <xsl:if test="$rentaAgraria/*[local-name()='AEEstimacionDirecta']/*[local-name()='AEAgricolas']">
											 <fo:block text-indent="3mm" margin-top="2pt" margin-left="4pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
													<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
														<fo:table-body start-indent="0pt">
															<fo:table-row>
																<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
																	<fo:block>
																		<fo:inline font-weight="bold" display-align="after">
																			<xsl:text>Agrícola</xsl:text>
																		</fo:inline>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</fo:table-body>
													</fo:table>
									<xsl:for-each select="$rentaAgraria/*[local-name()='AEEstimacionDirecta']/*[local-name()='AEAgricolas']/*[local-name()='AEDatosCasillas']">
											<xsl:variable name="datoAgricola" select="current()"/>													
											<!-- Tabla con los datos de operación -->
											<fo:table table-layout="fixed" width="50%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(1)"/>
												<fo:table-column column-width="proportional-column-width(1)"/>
												<xsl:if test="position() = 1">
													<fo:table-header  start-indent="20pt">
														
														<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
															<fo:block font-weight="bold" font-size="8pt"  text-align="right" font-family="Arial,Helvetica,sans-serif">Casilla</fo:block>
														</fo:table-cell>	
														<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
															<fo:block font-weight="bold" font-size="8pt" font-family="Arial,Helvetica,sans-serif" text-align="right" >
																Valor
															</fo:block>
														</fo:table-cell>										
													</fo:table-header>
												</xsl:if>
												<fo:table-body start-indent="20pt">															
													<fo:table-row>										
														<fo:table-cell padding="6pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
																	<fo:inline font-weight="normal">
																		 <xsl:value-of select="$datoAgricola/*[local-name()='AECasilla']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="6pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
																	<fo:inline font-weight="normal">
																		 <xsl:value-of select="$datoAgricola/*[local-name()='AESigno']"/>
																		  <xsl:value-of select="$datoAgricola/*[local-name()='AEEnteros']"/>
																		  <xsl:text>,</xsl:text>
																		  <xsl:value-of select="$datoAgricola/*[local-name()='AEDecimales']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>											
												</fo:table-body>
											</fo:table>
											</xsl:for-each><!-- fin FOR EACH DE agricolas -->
											</fo:block>
									</xsl:if>
									 <xsl:if test="$rentaAgraria/*[local-name()='AEEstimacionDirecta']/*[local-name()='AENoAgricolas']">
											 <fo:block text-indent="3mm" margin-top="2pt" margin-left="4pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
													<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
														<fo:table-body start-indent="0pt">
															<fo:table-row>
																<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
																	<fo:block>
																		<fo:inline font-weight="bold" display-align="after">
																			<xsl:text>No agrícolas</xsl:text>
																		</fo:inline>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</fo:table-body>
													</fo:table>
									<xsl:for-each select="$rentaAgraria/*[local-name()='AEEstimacionDirecta']/*[local-name()='AENoAgricolas']/*[local-name()='AEDatosCasillas']">
											<xsl:variable name="datoNoAgricola" select="current()"/>													
											<!-- Tabla con los datos de operación -->
											<fo:table table-layout="fixed" width="50%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(1)"/>
												<fo:table-column column-width="proportional-column-width(1)"/>
												<xsl:if test="position() = 1">
													<fo:table-header  start-indent="20pt">
														
														<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
															<fo:block font-weight="bold" font-size="8pt"  text-align="right" font-family="Arial,Helvetica,sans-serif">Casilla</fo:block>
														</fo:table-cell>	
														<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
															<fo:block font-weight="bold" font-size="8pt" font-family="Arial,Helvetica,sans-serif" text-align="right" >
																Valor
															</fo:block>
														</fo:table-cell>										
													</fo:table-header>
												</xsl:if>
												<fo:table-body start-indent="20pt">															
													<fo:table-row>										
														<fo:table-cell padding="6pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
																	<fo:inline font-weight="normal">
																		 <xsl:value-of select="$datoNoAgricola/*[local-name()='AECasilla']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="6pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
																	<fo:inline font-weight="normal">
																		 <xsl:value-of select="$datoNoAgricola/*[local-name()='AESigno']"/>
																		  <xsl:value-of select="$datoNoAgricola/*[local-name()='AEEnteros']"/>
																		  <xsl:text>,</xsl:text>
																		  <xsl:value-of select="$datoNoAgricola/*[local-name()='AEDecimales']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>											
												</fo:table-body>
											</fo:table>
											</xsl:for-each><!-- fin FOR EACH DE noagricolas -->
											</fo:block>
									</xsl:if>
							  </fo:block>
							</xsl:if><!--fin estimacion directa-->
							
						<xsl:if test="$rentaAgraria/*[local-name()='AEEstimacionObjetiva']">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="3pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Estimación objetiva</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
								<xsl:for-each select="$rentaAgraria/*[local-name()='AEEstimacionObjetiva']/*[local-name()='AEDatosCasillas']">
											<xsl:variable name="datoObjetiva" select="current()"/>													
											<!-- Tabla con los datos de operación -->
											<fo:table table-layout="fixed" width="50%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(1)"/>
												<fo:table-column column-width="proportional-column-width(1)"/>
												<xsl:if test="position() = 1">
													<fo:table-header  start-indent="20pt">
														
														<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
															<fo:block font-weight="bold" font-size="8pt"  text-align="right" font-family="Arial,Helvetica,sans-serif">Casilla</fo:block>
														</fo:table-cell>	
														<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
															<fo:block font-weight="bold" font-size="8pt" font-family="Arial,Helvetica,sans-serif" text-align="right" >
																Valor
															</fo:block>
														</fo:table-cell>										
													</fo:table-header>
												</xsl:if>
												<fo:table-body start-indent="20pt">															
													<fo:table-row>										
														<fo:table-cell padding="6pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
																	<fo:inline font-weight="normal">
																		 <xsl:value-of select="$datoObjetiva/*[local-name()='AECasilla']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="6pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
																	<fo:inline font-weight="normal">
																		 <xsl:value-of select="$datoObjetiva/*[local-name()='AESigno']"/>
																		  <xsl:value-of select="$datoObjetiva/*[local-name()='AEEnteros']"/>
																		  <xsl:text>,</xsl:text>
																		  <xsl:value-of select="$datoObjetiva/*[local-name()='AEDecimales']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>											
												</fo:table-body>
											</fo:table>
											</xsl:for-each><!-- fin FOR EACH DE objetva -->
								
							  </fo:block>
						</xsl:if><!--fin estimacion objetiva-->
					    
					
					    <xsl:if test="$rentaAgraria/*[local-name()='AEAtribucionRentas']">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="3pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Atribución rentas</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
								 <xsl:if test="$rentaAgraria/*[local-name()='AEAtribucionRentas']/*[local-name()='AEEntidad1']">
											 <fo:block text-indent="3mm" margin-top="2pt" margin-left="4pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
													<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
														<fo:table-body start-indent="0pt">
															<fo:table-row>
																<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
																	<fo:block>
																		<fo:inline font-weight="bold" display-align="after">
																			<xsl:text>Primera entidad </xsl:text>
																				 <xsl:if test="$rentaAgraria/*[local-name()='AEAtribucionRentas']/*[local-name()='AEEntidad1']/*[local-name()='AENifEntidad1'] != ''">
																				  ( CIF: <xsl:value-of select="$rentaAgraria/*[local-name()='AEAtribucionRentas']/*[local-name()='AEEntidad1']/*[local-name()='AENifEntidad1']"/> )
																				 </xsl:if>
																		</fo:inline>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</fo:table-body>
													</fo:table>
									<xsl:for-each select="$rentaAgraria/*[local-name()='AEAtribucionRentas']/*[local-name()='AEEntidad1']/*[local-name()='AEDatosCasillas']">
											<xsl:variable name="datoEntidad1" select="current()"/>													
											<!-- Tabla con los datos de operación -->
											<fo:table table-layout="fixed" width="50%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(1)"/>
												<fo:table-column column-width="proportional-column-width(1)"/>
												<xsl:if test="position() = 1">
													<fo:table-header  start-indent="20pt">
														
														<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
															<fo:block font-weight="bold" font-size="8pt"  text-align="right" font-family="Arial,Helvetica,sans-serif">Casilla</fo:block>
														</fo:table-cell>	
														<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
															<fo:block font-weight="bold" font-size="8pt" font-family="Arial,Helvetica,sans-serif" text-align="right" >
																Valor
															</fo:block>
														</fo:table-cell>										
													</fo:table-header>
												</xsl:if>
												<fo:table-body start-indent="20pt">															
													<fo:table-row>										
														<fo:table-cell padding="6pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
																	<fo:inline font-weight="normal">
																		 <xsl:value-of select="$datoEntidad1/*[local-name()='AECasilla']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="6pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
																	<fo:inline font-weight="normal">
																		 <xsl:value-of select="$datoEntidad1/*[local-name()='AESigno']"/>
																		  <xsl:value-of select="$datoEntidad1/*[local-name()='AEEnteros']"/>
																		  <xsl:text>,</xsl:text>
																		  <xsl:value-of select="$datoEntidad1/*[local-name()='AEDecimales']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>											
												</fo:table-body>
											</fo:table>
											</xsl:for-each><!-- fin FOR EACH DE entidad1 -->
											</fo:block>
									</xsl:if>
						
						  
						  		 <xsl:if test="$rentaAgraria/*[local-name()='AEAtribucionRentas']/*[local-name()='AEEntidad2']">
											 <fo:block text-indent="3mm" margin-top="2pt" margin-left="4pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
													<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
														<fo:table-body start-indent="0pt">
															<fo:table-row>
																<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
																	<fo:block>
																		<fo:inline font-weight="bold" display-align="after">
																			<xsl:text>Segunda entidad </xsl:text>
																				 <xsl:if test="$rentaAgraria/*[local-name()='AEAtribucionRentas']/*[local-name()='AEEntidad2']/*[local-name()='AENifEntidad2'] != ''">
																				  ( CIF: <xsl:value-of select="$rentaAgraria/*[local-name()='AEAtribucionRentas']/*[local-name()='AEEntidad2']/*[local-name()='AENifEntidad2']"/> )
																				 </xsl:if>
																		</fo:inline>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</fo:table-body>
													</fo:table>
									<xsl:for-each select="$rentaAgraria/*[local-name()='AEAtribucionRentas']/*[local-name()='AEEntidad2']/*[local-name()='AEDatosCasillas']">
											<xsl:variable name="datoEntidad2" select="current()"/>													
											<!-- Tabla con los datos de operación -->
											<fo:table table-layout="fixed" width="50%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(1)"/>
												<fo:table-column column-width="proportional-column-width(1)"/>
												<xsl:if test="position() = 1">
													<fo:table-header  start-indent="20pt">
														
														<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
															<fo:block font-weight="bold" font-size="8pt"  text-align="right" font-family="Arial,Helvetica,sans-serif">Casilla</fo:block>
														</fo:table-cell>	
														<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
															<fo:block font-weight="bold" font-size="8pt" font-family="Arial,Helvetica,sans-serif" text-align="right" >
																Valor
															</fo:block>
														</fo:table-cell>										
													</fo:table-header>
												</xsl:if>
												<fo:table-body start-indent="20pt">															
													<fo:table-row>										
														<fo:table-cell padding="6pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
																	<fo:inline font-weight="normal">
																		 <xsl:value-of select="$datoEntidad2/*[local-name()='AECasilla']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="6pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
																	<fo:inline font-weight="normal">
																		 <xsl:value-of select="$datoEntidad2/*[local-name()='AESigno']"/>
																		  <xsl:value-of select="$datoEntidad2/*[local-name()='AEEnteros']"/>
																		  <xsl:text>,</xsl:text>
																		  <xsl:value-of select="$datoEntidad2/*[local-name()='AEDecimales']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>											
												</fo:table-body>
											</fo:table>
											</xsl:for-each><!-- fin FOR EACH DE entidad1 -->
											</fo:block>
									</xsl:if>
						  </fo:block>
						</xsl:if><!--fin estimacion atribucion rentas-->
					
					
<xsl:if test="$rentaAgraria/*[local-name()='AEGenerales2']">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="3pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos generales 2</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							<xsl:for-each select="$rentaAgraria/*[local-name()='AEGenerales2']/*[local-name()='AEDatosCasillas']">
									<xsl:variable name="datoGeneral2" select="current()"/>													
									<!-- Tabla con los datos de operación -->
									<fo:table table-layout="fixed" width="50%" border-spacing="5pt" space-after="2mm">
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-column column-width="proportional-column-width(1)"/>
										<xsl:if test="position() = 1">
											<fo:table-header  start-indent="20pt">
												
												<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
													<fo:block font-weight="bold" font-size="8pt"  text-align="right" font-family="Arial,Helvetica,sans-serif">Casilla</fo:block>
												</fo:table-cell>	
												<fo:table-cell display-align="after" padding="6px" 	height="17pt" background-color="#C0C0C0">
													<fo:block font-weight="bold" font-size="8pt" font-family="Arial,Helvetica,sans-serif" text-align="right" >
														Valor
													</fo:block>
												</fo:table-cell>										
											</fo:table-header>
										</xsl:if>
										<fo:table-body start-indent="20pt">															
											<fo:table-row>										
												<fo:table-cell padding="6pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
															<fo:inline font-weight="normal">
																 <xsl:value-of select="$datoGeneral2/*[local-name()='AECasilla']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												
												<fo:table-cell padding="6pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after" text-align="right">
															<fo:inline font-weight="normal">
																 <xsl:value-of select="$datoGeneral2/*[local-name()='AESigno']"/>
																  <xsl:value-of select="$datoGeneral2/*[local-name()='AEEnteros']"/>
																  <xsl:text>,</xsl:text>
																  <xsl:value-of select="$datoGeneral2/*[local-name()='AEDecimales']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>											
										</fo:table-body>
									</fo:table>
							</xsl:for-each><!-- fin FOR EACH DE generales2 -->
							 </fo:block>
							</xsl:if>

					</xsl:if><!--renta agraria-->
						
						
					</xsl:if>	<!-- de irpf -->
				</fo:block>
			</xsl:when>
		</xsl:choose> 
	</xsl:template>
</xsl:stylesheet> 
