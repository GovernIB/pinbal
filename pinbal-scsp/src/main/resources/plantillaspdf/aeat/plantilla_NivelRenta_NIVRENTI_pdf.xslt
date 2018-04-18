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
		<xsl:variable name="estado" select="$datosEspecificos/*[local-name()='EstadoResultado']"/>
		<xsl:variable name="ctasBancarias" select="$datosEspecificos/*[local-name()='CtasBancarias']"/>
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
	    <xsl:variable name="datosCabecera" select="$datosEspecificos/*[local-name()='Cabecera']"/>
	    <!-- DATOS DE LA RESPUESTA -->	
		<xsl:choose>
			<!-- Miramos el Codigo de Error de la etiqueta Atributos -->
			<xsl:when test="$datosEspecificos/*[local-name()='Cabecera']/*[local-name()='CodRet'] != '1000' ">
				<xsl:variable name="cabecera" select="$datosEspecificos/*[local-name()='Cabecera']"/>
				<fo:block margin-top="40pt" text-align="center" font-family="Arial,sans-serif" font-size="10pt" line-height="10pt" space-after.optimum="1pt" display-align="after">
					<fo:inline font-weight="bold">
						<xsl:text>RESULTADO DE LA CONSULTA:      </xsl:text>
						<xsl:value-of select="$datosCabecera/*[local-name()='CodRet']"/>
						<xsl:text>  -  </xsl:text>
						<xsl:value-of select="$datosCabecera/*[local-name()='DescripcionError']"/>
					</fo:inline>
				</fo:block>
			</xsl:when>
			<xsl:when test="$datosEspecificos/*[local-name()='Cabecera']/*[local-name()='CodRet'] = '1000'">
				<fo:block text-align="left" margin-top="25pt">
					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
						<fo:inline font-weight="bold">
							<!-- <xsl:text>Se han obtenido los siguientes datos de Títulos No Universitarios:</xsl:text> -->
							<xsl:text>Datos de respuesta</xsl:text>
						</fo:inline>
					</fo:block>
				</fo:block>
				<fo:block border-style="solid" border-width="1px 1px 1px 1px">
					<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							 <fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-column column-width="proportional-column-width(0.8)"/>
								<fo:table-column column-width="proportional-column-width(1)"/>
								<fo:table-column column-width="proportional-column-width(1.2)"/>
								<fo:table-column column-width="proportional-column-width(1)"/>	
								<fo:table-column column-width="proportional-column-width(0.8)"/>
								<fo:table-column column-width="proportional-column-width(1)"/> 
								<fo:table-column column-width="proportional-column-width(1.2)"/>
								<fo:table-column column-width="proportional-column-width(1)"/>
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
														<xsl:value-of select="$datosCabecera/*[local-name()='Referencia']"/>
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
														 <xsl:value-of select="concat(substring(string($datosCabecera/*[local-name()='FechaEmision']),9,2),'/',substring(string($datosCabecera/*[local-name()='FechaEmision']),6,2),'/',substring(string($datosCabecera/*[local-name()='FechaEmision']),1,4))"/>
													</fo:inline>
												</fo:block>
										</fo:table-cell>
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
														<xsl:value-of select="$datosCabecera/*[local-name()='Ejercicio']"/>
													</fo:inline> 
											</fo:block>
										</fo:table-cell>		
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt" font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-weight="bold">
														<xsl:text>Tipo respuesta:</xsl:text>
													</fo:inline>												
											</fo:block>
										</fo:table-cell>		
										<fo:table-cell padding="2pt" display-align="center" >
											<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
													<fo:inline font-style="normal">
														<xsl:choose>
															<xsl:when test="$datosCabecera/*[local-name()='TipoRespuesta']  = 'IR'">
																<xsl:text>IRPF</xsl:text>
															</xsl:when>
															<xsl:when test="$datosCabecera/*[local-name()='TipoRespuesta']  = 'IM'">
																<xsl:text>Imputaciones</xsl:text>
															</xsl:when>
															<xsl:when test="$datosCabecera/*[local-name()='TipoRespuesta']  = 'NR'">
																<xsl:text>Nivel de renta</xsl:text>
															</xsl:when>
															
															<xsl:otherwise><xsl:text></xsl:text></xsl:otherwise>
														</xsl:choose> 
													</fo:inline> 
											</fo:block>
										</fo:table-cell>
									</fo:table-row>									
								</fo:table-body>
							</fo:table>
													
						</fo:block>
				 
					<xsl:variable name="imputaciones" select="$datosEspecificos/*[local-name()='Imputaciones']"/>
					<xsl:variable name="irpf" select="$datosEspecificos/*[local-name()='irpf']"/> 
					<xsl:if test="$imputaciones">
						<!-- Panel Datos Cuentas Bancarias -->
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							 <fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													 <xsl:text>Imputaciones </xsl:text> 
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						 
						 <xsl:if test="$imputaciones/*[local-name()='DatosEconomicos']">
							<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								 
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"> 
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>	
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-body start-indent="0pt">
										<!-- Fila  Hijo -->
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Descripción</xsl:text>															
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="right" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Valor</xsl:text>														
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											
										</fo:table-row>
										<xsl:for-each select="$imputaciones/*[local-name()='DatosEconomicos']"> 
												<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		 <xsl:value-of select="*[local-name()='Literal']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
												
												<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="right" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
														 <xsl:value-of select="*[local-name()='Signo']"/>  <xsl:value-of select="*[local-name()='Enteros']"/> , <xsl:value-of select="*[local-name()='Decimales']"/>
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
					</xsl:if>
					 
					
					<xsl:if test="$irpf">
						<!-- Panel Datos Cuentas Bancarias -->
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							 <fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													 <xsl:text>Datos renta  </xsl:text> 
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
							
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>	
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-body start-indent="0pt">
										<!-- Fila  Hijo -->
										<fo:table-row>
											<fo:table-cell padding="2pt" text-align="left" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>NIF solicitante:</xsl:text>															
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" text-align="left" number-columns-spanned="5">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$irpf/*[local-name()='CabeceraRenta']/*[local-name()='NifSolicitante']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
											<fo:table-cell padding="2pt" text-align="left" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Nombre solicitante:</xsl:text>														
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" text-align="left" number-columns-spanned="5">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
														 <xsl:value-of select="$irpf/*[local-name()='CabeceraRenta']/*[local-name()='NombreSolicitante']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										
										<fo:table-row>
											<fo:table-cell padding="2pt" text-align="left" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Primer declarante:</xsl:text>															
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" text-align="left" number-columns-spanned="5">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$irpf/*[local-name()='CabeceraRenta']/*[local-name()='PrimerDeclarante']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										
										<xsl:if test="$irpf/*[local-name()='CabeceraRenta']/*[local-name()='SegundoTitular'] != ''"> 
											 <fo:table-row>
												<fo:table-cell padding="2pt" text-align="left" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Segundo titular:</xsl:text>															
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" text-align="left" number-columns-spanned="5">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$irpf/*[local-name()='CabeceraRenta']/*[local-name()='SegundoTitular']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</xsl:if>
										
										<fo:table-row>
											<fo:table-cell padding="2pt" text-align="left" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Modelo:</xsl:text>														
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" text-align="left" number-columns-spanned="5">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
														 <xsl:value-of select="$irpf/*[local-name()='CabeceraRenta']/*[local-name()='Modelo']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										
										<fo:table-row>
											<fo:table-cell padding="2pt" text-align="left" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Tributación: </xsl:text>															
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" text-align="left" number-columns-spanned="5">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
																<xsl:value-of select="$irpf/*[local-name()='CabeceraRenta']/*[local-name()='Tributacion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row>
											<fo:table-cell padding="2pt" text-align="left" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Origen datos:</xsl:text>														
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" text-align="left" number-columns-spanned="5">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
														 <xsl:value-of select="$irpf/*[local-name()='CabeceraRenta']/*[local-name()='OrigenDatos']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>			
						</fo:block>
						
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													 <xsl:text>Datos nivel renta  </xsl:text> 
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
							
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
															<xsl:text>Descripción:</xsl:text>															
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$irpf/*[local-name()='NivelRenta']/*[local-name()='NRLiteral']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell> 
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Valor:</xsl:text>														
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="right" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
														<xsl:value-of select="$irpf/*[local-name()='NivelRenta']/*[local-name()='NRSigno']"/>
														<xsl:value-of select="$irpf/*[local-name()='NivelRenta']/*[local-name()='NREnteros']"/>,
														<xsl:value-of select="$irpf/*[local-name()='NivelRenta']/*[local-name()='NRDecimales']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
												<fo:block text-align="right" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														 
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>							
							</fo:table>
						</fo:block>
						
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<xsl:if test="$irpf/*[local-name()='DatosEconomicos']">	
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														 <xsl:text>Datos económicos  </xsl:text> 
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							
								<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
									<fo:table table-layout="fixed" width="70%" border-spacing="5pt" space-after="2mm"> 
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-column column-width="proportional-column-width(1)"/>	
										<fo:table-column column-width="proportional-column-width(1)"/>
										<fo:table-column column-width="proportional-column-width(1)"/> 
										
										<fo:table-body start-indent="0pt">
											<!-- Fila  Hijo -->
											<fo:table-row>  
												 <fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Casilla</xsl:text>															
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												
												
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="4">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Descripción</xsl:text>															
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="1">
													<fo:block text-align="right" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Valor</xsl:text>															
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											<xsl:for-each select="$irpf/*[local-name()='DatosEconomicos']"> 
											
											<xsl:variable name="bgclr">
											    <xsl:choose>
											        <xsl:when test="position() mod 2">#CCC</xsl:when>
											        <xsl:otherwise>#FFF</xsl:otherwise>
											    </xsl:choose>
											</xsl:variable>
										  
										   <fo:table-row background-color ="{$bgclr}">
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='DECasilla']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell> 
												
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="4">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='DECasillaDescripcion']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											  
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="right" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='DESigno']"/><xsl:value-of select="*[local-name()='DEEnteros']"/> , 
																	<xsl:value-of select="*[local-name()='DEDecimales']"/>
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
						
						</fo:block>
						
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													 <xsl:text>Datos adicionales  </xsl:text> 
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<xsl:variable name="datosPersonales" select="$irpf/*[local-name()='DatosCola']/*[local-name()='DCDatosPersonales']"/>
							<xsl:if test="$datosPersonales">
									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
													<fo:block font-size="10pt" >
														<fo:inline font-weight="bold" display-align="after">
															 <xsl:text>Datos personales  </xsl:text> 
														</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
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
													<fo:block text-align="left" margin="0pt" font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Primer declarante:</xsl:text>
															</fo:inline>												
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
													<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
															  <xsl:value-of select="$datosPersonales/*[local-name()='DCLiteral']"/>
															</fo:inline> 
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt" font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Estado civil:</xsl:text>
															</fo:inline>												
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2" >
													<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
															   <xsl:value-of select="$datosPersonales/*[local-name()='DCEstadoCivil']/*[local-name()='DCFecha']"/> - 
																<xsl:value-of select="$datosPersonales/*[local-name()='DCEstadoCivil']/*[local-name()='DCContenido']"/>
															</fo:inline> 
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
											
											<fo:table-row>	
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt" font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Fecha nacimiento:</xsl:text>
															</fo:inline>												
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
													<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
															 <xsl:value-of select="$datosPersonales/*[local-name()='DCFechaNac']"/>
															</fo:inline> 
													</fo:block>
												</fo:table-cell>	 
												 
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Minusvalía:</xsl:text>														
															</fo:inline>												
													</fo:block>
												</fo:table-cell>
												
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
													<fo:block text-align="left" margin="0pt" font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal"> 
																<xsl:value-of select="$datosPersonales/*[local-name()='DCMinusvalia']"/>
															</fo:inline>
														</fo:block>
												</fo:table-cell>
											</fo:table-row>								
										</fo:table-body>
									</fo:table>							
									
									
									
									
									
							</xsl:if>
						</fo:block>
						
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<xsl:variable name="datosconyuge" select="$irpf/*[local-name()='DatosCola']/*[local-name()='DCDatosConyuge']"/>
							<xsl:if test="$datosconyuge">
							
									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
													<fo:block  font-size="10pt">
														<fo:inline font-weight="bold" display-align="after">
															 <xsl:text>Datos conyuge  </xsl:text> 
														</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
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
													<fo:block text-align="left" margin="0pt" font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Conyuge:</xsl:text>
															</fo:inline>												
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
													<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
															  <xsl:value-of select="$datosconyuge/*[local-name()='DCLiteral']"/>
															</fo:inline> 
													</fo:block>
												</fo:table-cell>
												 
												 
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt" font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Fecha nacimiento:</xsl:text>
															</fo:inline>												
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
													<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
															   <xsl:value-of select="$datosconyuge/*[local-name()='DCFechaNac']"/>
															</fo:inline> 
													</fo:block>
												</fo:table-cell>
											</fo:table-row> 
											<fo:table-row>  
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt"  font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Minusvalía:</xsl:text>														
															</fo:inline>												
													</fo:block>
												</fo:table-cell>
												
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5">
													<fo:block text-align="left" margin="0pt" font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal"> 
																<xsl:value-of select="$datosconyuge/*[local-name()='DCMinusvalia']"/>
															</fo:inline>
														</fo:block>
												</fo:table-cell> 
											</fo:table-row>								
										</fo:table-body>
									</fo:table>	
							</xsl:if>
						</fo:block>
						
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
						   	<xsl:variable name="datoshijo" select="$irpf/*[local-name()='DatosCola']/*[local-name()='DCDatosHijos']"/>
							<xsl:if test="$datoshijo">		
							
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block font-size="10pt">
													<fo:inline font-weight="bold" display-align="after">
														 <xsl:text>Datos hijos </xsl:text> 
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>	
									<xsl:for-each select="$datoshijo/*[local-name()='DCNumHijos']"> 
										<xsl:variable name="hijo" select="position()"/>
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
											<fo:table-body start-indent="0pt">
												<fo:table-row>
													<fo:table-cell padding="3pt" margin-left="3pt" display-align="center" background-color="#B0C4DE">
														<fo:block font-size="9pt">
															<fo:inline font-weight="bold" display-align="after">
																 <xsl:text>Hijo  </xsl:text> <xsl:value-of select="$hijo"/>
															</fo:inline>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>								 
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
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Nombre:</xsl:text>															
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='DCNombreHijo']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													
													<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Minusvalía:</xsl:text>															
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="2">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='DCMinusvalia']"/>
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
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center"  >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='DCFechaNacim']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell> 
													<fo:table-cell padding="2pt" display-align="center" >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Fecha adopción:</xsl:text>															
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center" >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='DCFechaAdopc']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell> 
													
													<fo:table-cell padding="2pt" display-align="center" >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Vinculación:</xsl:text>															
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center" >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='DCVinculacion']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell> 
												</fo:table-row>
											</fo:table-body>
										</fo:table>							
									</fo:block>
								</xsl:for-each>	   
		 				    </xsl:if>
						</fo:block>	
								
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
  							<xsl:variable name="datosascendiente" select="$irpf/*[local-name()='DatosCola']/*[local-name()='DCDatosAscend']"/>
							<xsl:if test="$datosascendiente">
						
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block font-size="10pt">
													<fo:inline font-weight="bold" display-align="after">
														 <xsl:text>Datos ascendientes </xsl:text> 
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>	
								<xsl:for-each select="$datosascendiente/*[local-name()='DCNumAscend']"> 
									<xsl:variable name="asc" select="position()"/>
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="3pt" margin-left="3pt" display-align="center" background-color="#B0C4DE">
													<fo:block font-size="9pt">
														<fo:inline font-weight="bold" display-align="after">
															 <xsl:text>Hijo  </xsl:text> <xsl:value-of select="$asc"/>
														</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>								 
									 <fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
									 
									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										 
										<fo:table-body start-indent="0pt">
											<!-- Fila  Hijo -->
											
											
											<xsl:if test="*[local-name()='DCNombreAscend']">
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
																<xsl:value-of select="*[local-name()='DCNombreAscend']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												</fo:table-row>
											 </xsl:if>
											 <fo:table-row>
											<xsl:if test="*[local-name()='DCFechaNacim']">
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Fecha nacimiento:</xsl:text>															
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='DCFechaNacim']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											 </xsl:if>										  
											<xsl:if test="*[local-name()='DCConvivencia']">
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Convivencia:</xsl:text>															
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='DCConvivencia']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											 </xsl:if>										  
											  
											  <xsl:if test="*[local-name()='DCMinusvalia']">
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Minusvalía:</xsl:text>															
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='DCMinusvalia']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											 </xsl:if>		
											 
											   <xsl:if test="*[local-name()='DCVinculacion']">
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Vinculación:</xsl:text>															
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" >
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='DCVinculacion']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
											 </xsl:if>		
											</fo:table-row>
										</fo:table-body>
									</fo:table>							
								</fo:block>
							</xsl:for-each>	   
 				   			 </xsl:if>					
						</fo:block>
					
					
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
					  		<xsl:variable name="datosvivienda" select="$irpf/*[local-name()='DatosCola']/*[local-name()='DCDatosVivienda']"/>
					 		<xsl:if test="$datosvivienda">
					
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
													<fo:block font-size="10pt">
														<fo:inline font-weight="bold" display-align="after">
															 <xsl:text>Datos viviendas </xsl:text> 
														</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>	
									<xsl:for-each select="$datosvivienda/*[local-name()='DCNumViviendas']"> 
									<xsl:variable name="viv" select="position()"/>
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
											<fo:table-body start-indent="0pt">
												<fo:table-row>
													<fo:table-cell padding="3pt" margin-left="3pt" display-align="center" background-color="#B0C4DE">
														<fo:block font-size="9pt">
															<fo:inline font-weight="bold" display-align="after">
																 <xsl:text>Vivienda  </xsl:text> <xsl:value-of select="$viv"/>
															</fo:inline>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>								 
										 <fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										 
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"> 
											<fo:table-body start-indent="0pt">
												<!-- Fila  Hijo -->
												<fo:table-row>
											 
												<xsl:if test="*[local-name()='DCContrib']">
													<fo:table-cell padding="2pt" display-align="center" >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Contribuyente:</xsl:text>															
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center" >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='DCContrib']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
												 </xsl:if>		
		
		
		
												<xsl:if test="*[local-name()='DCParticipac']">
													<fo:table-cell padding="2pt" display-align="center" >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Participación:</xsl:text>															
																</fo:inline>
															</fo:block> 
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center" >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='DCParticipac']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell> 
												 </xsl:if>						 				  
												
													  <xsl:if test="*[local-name()='DCTitularidad']">
													<fo:table-cell padding="2pt" display-align="center" >
														
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Titularidad</xsl:text>															
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center" >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='DCTitularidad']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
												 </xsl:if>		
												 									  
												  </fo:table-row>
												  <fo:table-row>
												  <xsl:if test="*[local-name()='DCSituacion']">
													<fo:table-cell padding="2pt" display-align="center" >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Situación:</xsl:text>														 	
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5"  >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='DCSituacion']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
												 </xsl:if>		
												 </fo:table-row>
												 <xsl:if test="*[local-name()='DCRefCatastr']">
												 <fo:table-row>
													<fo:table-cell padding="2pt" display-align="center" >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Ref. Catastral:</xsl:text>															
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center" >
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='DCRefCatastr']"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													</fo:table-row>
													</xsl:if>	
												
											</fo:table-body>
										</fo:table>							
									</fo:block>
								</xsl:for-each>	   
		 				    </xsl:if>	
		 				</fo:block>	
					</xsl:if>
				</fo:block>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
