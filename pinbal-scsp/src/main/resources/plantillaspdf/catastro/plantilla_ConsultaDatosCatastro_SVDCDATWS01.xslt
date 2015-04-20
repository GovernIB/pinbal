<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0" xmlns:str="http://www.str.com" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:functx="http://www.functx.com" exclude-result-prefixes="str functx">
	<xsl:output version="1.0" method="xml" encoding="UTF-8" indent="no"/>
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
	<xsl:param name="referenciaCatastralpc1"/>
	<xsl:param name="referenciaCatastralpc2"/>
	<xsl:param name="referenciaCatastralcar"/>
	<xsl:param name="referenciaCatastralcc1"/>
	<xsl:param name="referenciaCatastralcc2"/>
	<xsl:param name="localizacionINEcm"/>
	<xsl:param name="localizacionINEcp"/>
	<xsl:param name="cppcpo"/>
	<xsl:param name="cppcpa"/>
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
				<xsl:text>Identificador de Petición:</xsl:text>
			</fo:inline>
			<fo:inline font-weight="bold">
				<xsl:text> </xsl:text>
				<xsl:value-of select="$idPeticion"/>
			</fo:inline>
		</fo:block>
		<fo:block text-align="center" font-family="Arial,sans-serif" font-size="8pt" line-height="10pt" space-after.optimum="1pt" display-align="after" color="#2F4F4F">
			<fo:inline font-weight="bold">
				<xsl:text>Identificador de Transmisión:</xsl:text>
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
						<fo:table-cell padding="2pt" display-align="center">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<fo:inline font-weight="bold">
									<xsl:text>Tipo Doc:</xsl:text>
								</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<xsl:value-of select="$tipoDocPeticion"/>
							</fo:block>
						</fo:table-cell>
						<!-- DOCUMENTACION -->
						<fo:table-cell padding="2pt" display-align="center">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<fo:inline font-weight="bold">
									<xsl:text>Documentación:</xsl:text>
								</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<xsl:value-of select="$docPeticion"/>
							</fo:block>
						</fo:table-cell>
						<!-- NOMBRE Y APELLIDOS -->
						<fo:table-cell padding="2pt" display-align="center">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<fo:inline font-weight="bold">
									<xsl:text>Nombre y Apellidos:</xsl:text>
								</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<xsl:value-of select="$nomApellidosPeticion"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<!-- CONSENTIMIENTO -->
						<fo:table-cell padding="2pt" display-align="center">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<fo:inline font-weight="bold">
									<xsl:text>Consentimiento:</xsl:text>
								</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<xsl:value-of select="$consentimiento"/>
							</fo:block>
						</fo:table-cell>
						<!-- NUMERO EXPEDIENTE -->
						<fo:table-cell padding="2pt" display-align="center">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<fo:inline font-weight="bold">
									<xsl:text>Nº Expediente:</xsl:text>
								</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<xsl:value-of select="$idExpediente"/>
							</fo:block>
						</fo:table-cell>
						<!-- FINALIDAD -->
						<fo:table-cell padding="2pt" display-align="center">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<fo:inline font-weight="bold">
									<xsl:text>Finalidad:</xsl:text>
								</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<xsl:value-of select="$finalidad"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			<fo:table table-layout="fixed" border-spacing="5pt" space-before="0mm">
				<fo:table-body start-indent="0pt">
					<fo:table-row>
						<!-- Nombre Procedimiento -->
						<fo:table-cell padding="2pt" display-align="center">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<fo:inline font-weight="bold">
									<xsl:text>Nombre Procedimiento:     </xsl:text>
								</fo:inline>
								<xsl:value-of select="$codProcedimiento"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			<!-- Si la consulta es por referencia catastral -->
		 <xsl:choose>
				<xsl:when test="$referenciaCatastralpc1 != '' and $referenciaCatastralpc2 != '' ">
					<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
						<fo:table-body start-indent="0pt">
							<fo:table-row>
								<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
									<fo:block>
										<fo:inline font-weight="bold" display-align="after">
											<xsl:text>Datos Referencia Catastral</xsl:text>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					<fo:table table-layout="fixed" border-spacing="5pt" space-before="0mm">
						<fo:table-column column-width="proportional-column-width(1.25)"/>
						<fo:table-column column-width="proportional-column-width(0.75)"/>
						<fo:table-column column-width="proportional-column-width(1.25)"/>
						<fo:table-column column-width="proportional-column-width(0.75)"/>
						<fo:table-column column-width="proportional-column-width(1.25)"/>
						<fo:table-column column-width="proportional-column-width(0.75)"/>
						<fo:table-body start-indent="0pt">
							<fo:table-row>
								<fo:table-cell padding="2pt" display-align="center">
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
										<fo:inline font-weight="bold">
											<xsl:text>Ref. CatastRal 1:</xsl:text>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
										<xsl:value-of select="$referenciaCatastralpc1"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
										<fo:inline font-weight="bold">
											<xsl:text>Ref. Catastral 2:</xsl:text>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
										<xsl:value-of select="$referenciaCatastralpc2"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
										<fo:inline font-weight="bold">
											<xsl:text>Cargo de la Ref. Catastral:</xsl:text>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
										<xsl:value-of select="$referenciaCatastralcar"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell padding="2pt" display-align="center">
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
										<fo:inline font-weight="bold">
											<xsl:text>Dígito Control 1:</xsl:text>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
										<xsl:value-of select="$referenciaCatastralcc1"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
										<fo:inline font-weight="bold">
											<xsl:text>Dígito Control 2:</xsl:text>
										</fo:inline>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
										<xsl:value-of select="$referenciaCatastralcc2"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</xsl:when>
			<!-- Si la consulta es por referencia catastral -->
				<xsl:otherwise>
					<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
						<fo:table-body start-indent="0pt">
							<fo:table-row>
								<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
									<fo:block>
										<fo:inline font-weight="bold" display-align="after">
											<xsl:text>Datos Referencia Rústica</xsl:text>
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
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
										<fo:inline font-weight="bold">
											<xsl:text>Comunidad Autónoma:     </xsl:text>
										</fo:inline>
										<xsl:value-of select="$localizacionINEcm"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
										<fo:inline font-weight="bold">
											<xsl:text>Municipio:     </xsl:text>
										</fo:inline>
										<xsl:value-of select="$localizacionINEcp"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
										<fo:inline font-weight="bold">
											<xsl:text>Cod. Polígono:     </xsl:text>
										</fo:inline>
										<xsl:value-of select="$cppcpo"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt" display-align="center">
									<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
										<fo:inline font-weight="bold">
											<xsl:text>Cod. Parcela:     </xsl:text>
										</fo:inline>
										<xsl:value-of select="$cppcpa"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</xsl:otherwise>
			</xsl:choose>
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
			<xsl:when test="$datosEspecificos/*[local-name()='Estado']/*[local-name()='CodigoEstado'] = '0003'">
				<xsl:variable name="datosConsulta" select="$datosEspecificos/*[local-name()='den']"/>
				<xsl:variable name="listaBienesInmuebles" select="$datosEspecificos/*[local-name()='listaBienesInmuebles']"/>
				<fo:block text-align="left" margin-top="25pt">
					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
						<fo:inline font-weight="bold">
							<xsl:text>Datos de Respuesta</xsl:text>
						</fo:inline>
					</fo:block>
				</fo:block>
				<fo:block border-style="solid" border-width="1px 1px 1px 1px">
					<xsl:if test="$listaBienesInmuebles/*[local-name()='datosInmueble']">
						<!-- Panel Datos Bienes Inmuebles -->
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="1mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>BIENES INMUEBLES DEL TITULAR</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
						<!-- Se itera sobre la lista de bienes -->
						<xsl:for-each select="$listaBienesInmuebles/*[local-name()='datosInmueble']">

							<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
											<fo:table-body start-indent="0pt">
												<fo:table-row>
													<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
														<fo:block>
															<fo:inline font-weight="bold" display-align="after">
																<xsl:text>Datos Referencia Rústica</xsl:text>
															</fo:inline>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>
							</fo:block>
							<xsl:variable name="listaBienInmuebleCount" select="position()"/>
							<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" space-after="3mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="1pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after" font-size="9pt" font-family="Arial,Helvetica,sans-serif">
														<xsl:text>Bien Rústico</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
								<!-- DATOS RUSTICOS -->
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<!-- Fila 1 Hijo -->
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block>
													<fo:inline-container>
														<fo:block>
															<xsl:text>&#x2029;</xsl:text>
														</fo:block>
													</fo:inline-container>
												</fo:block>
												<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='idine']/*[local-name()='cn']">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Tipo:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
															<fo:inline font-style="normal">
																<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='idine']/*[local-name()='cn'] = 'UR' ">
																	<xsl:text>URBANO</xsl:text>
																</xsl:if>
																<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='idine']/*[local-name()='cn'] = 'RU' ">
																	<xsl:text>RUSTICO</xsl:text>
																</xsl:if>
															</fo:inline>
														</fo:block>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block>
													<fo:inline-container>
														<fo:block>
															<xsl:text>&#x2029;</xsl:text>
														</fo:block>
													</fo:inline-container>
												</fo:block>
												<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='idine']/*[local-name()='rc']">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Referencia Catastral:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
															<fo:inline font-style="normal">
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='idine']/*[local-name()='rc']/*[local-name()='pc1']"/>
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='idine']/*[local-name()='rc']/*[local-name()='pc2']"/>
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='idine']/*[local-name()='rc']/*[local-name()='car']"/>
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='idine']/*[local-name()='rc']/*[local-name()='cc1']"/>
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='idine']/*[local-name()='rc']/*[local-name()='cc2']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
										</fo:table-row>
										<!-- Fila 2 Hijo -->
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block>
													<fo:inline-container>
														<fo:block>
															<xsl:text>&#x2029;</xsl:text>
														</fo:block>
													</fo:inline-container>
												</fo:block>
												<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='idine']/*[local-name()='loine']/*[local-name()='cp']">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Código de Provincia INE:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
															<fo:inline font-style="normal">
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='idine']/*[local-name()='loine']/*[local-name()='cp']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block>
													<fo:inline-container>
														<fo:block>
															<xsl:text>&#x2029;</xsl:text>
														</fo:block>
													</fo:inline-container>
												</fo:block>
												<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='idine']/*[local-name()='loine']/*[local-name()='cm']">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Código de Municipio INE:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
															<fo:inline font-style="normal">
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='idine']/*[local-name()='loine']/*[local-name()='cm']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
										</fo:table-row>
										<!-- Fila 3 Hijo -->
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block>
													<fo:inline-container>
														<fo:block>
															<xsl:text>&#x2029;</xsl:text>
														</fo:block>
													</fo:inline-container>
												</fo:block>
												<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='ldt']">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Domicilio Tributario:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
															<fo:inline font-style="normal">
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='ldt']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block>
													<fo:inline-container>
														<fo:block>
															<xsl:text>&#x2029;</xsl:text>
														</fo:block>
													</fo:inline-container>
												</fo:block>
												<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='avc']">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Año del Valor Catastral:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
															<fo:inline font-style="normal">
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='avc']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
										</fo:table-row>
										<!-- Fila 4 Hijo -->
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block>
													<fo:inline-container>
														<fo:block>
															<xsl:text>&#x2029;</xsl:text>
														</fo:block>
													</fo:inline-container>
												</fo:block>
												<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='vcat']">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Valor Catastral:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
															<fo:inline font-style="normal">
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='vcat']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block>
													<fo:inline-container>
														<fo:block>
															<xsl:text>&#x2029;</xsl:text>
														</fo:block>
													</fo:inline-container>
												</fo:block>
												<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='vcs']">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Valor Catastral del Suelo:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
															<fo:inline font-style="normal">
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='vcs']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
										</fo:table-row>
										<!-- Fila 5 Hijo -->
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block>
													<fo:inline-container>
														<fo:block>
															<xsl:text>&#x2029;</xsl:text>
														</fo:block>
													</fo:inline-container>
												</fo:block>
												<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='vcc']">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Valor Catastral de la construcción:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
															<fo:inline font-style="normal">
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='vcc']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block>
													<fo:inline-container>
														<fo:block>
															<xsl:text>&#x2029;</xsl:text>
														</fo:block>
													</fo:inline-container>
												</fo:block>
												<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='luso']">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Uso:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
															<fo:inline font-style="normal">
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='luso']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
										</fo:table-row>
										<!-- Fila 6 Hijo -->
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block>
													<fo:inline-container>
														<fo:block>
															<xsl:text>&#x2029;</xsl:text>
														</fo:block>
													</fo:inline-container>
												</fo:block>
												<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='sfc']">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Superficie Construida:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
															<fo:inline font-style="normal">
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='sfc']"/>
																<xsl:text>m</xsl:text>
															</fo:inline>
															<fo:inline baseline-shift="super" font-size="5px">
																<xsl:text>2</xsl:text>
															</fo:inline>
														</fo:block>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block>
													<fo:inline-container>
														<fo:block>
															<xsl:text>&#x2029;</xsl:text>
														</fo:block>
													</fo:inline-container>
												</fo:block>
												<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='cpt']">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Coeficiente de Participacion del Titular:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
															<fo:inline font-style="normal">
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='cpt']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
										</fo:table-row>
										<!-- Fila 7 Hijo -->
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block>
													<fo:inline-container>
														<fo:block>
															<xsl:text>&#x2029;</xsl:text>
														</fo:block>
													</fo:inline-container>
												</fo:block>
												<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='ant']">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Antigüedad:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
															<fo:inline font-style="normal">
																<xsl:value-of select="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='ant']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block>
													<fo:inline-container>
														<fo:block>
															<xsl:text>&#x2029;</xsl:text>
														</fo:block>
													</fo:inline-container>
												</fo:block>
												<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='debi']/*[local-name()='cpt']">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text></xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
															<fo:inline font-style="normal">
																
															</fo:inline>
														</fo:block>
													</fo:block>
												</xsl:if>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
								<!-- FINCAS COLINDANTES -->
								<!-- FINCAS COLINDANTES -->
								<xsl:if test="./*[local-name()='BienInmuebleRustico']/*[local-name()='lcol']/*[local-name()='col']">

									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="1pt" display-align="center"
													background-color="#B0C4DE">
													<fo:block margin-left="2pt" margin-right="2pt">
														<fo:inline font-weight="bold" display-align="after"
															font-size="9pt" font-family="Arial,Helvetica,sans-serif">
															<xsl:text>Fincas Colindantes</xsl:text>
														</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>


									<xsl:for-each select="./*[local-name()='BienInmuebleRustico']/*[local-name()='lcol']/*">
										<xsl:variable name="listaFincasColindatesCount" select="position()" />

											<fo:table table-layout="fixed" width="100%" space-after="4mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="1pt" display-align="center">
															<fo:block border-color="#B0C4DE" border-style="solid"
																border="1px 1px 1px 1px">
																<fo:inline font-weight="bold" display-align="after"
																	font-size="9pt" font-family="Arial,Helvetica,sans-serif">
																	<xsl:text>Finca Colindante </xsl:text>
																	<xsl:value-of select="$listaFincasColindatesCount" />
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										<fo:table  table-layout="fixed" width="100%" border-spacing="3pt" space-after="1mm">
											<fo:table-column column-width="proportional-column-width(0.6)" />
											<fo:table-column column-width="proportional-column-width(1.4)" />
											<fo:table-body start-indent="0pt">
												<xsl:if test="./*[local-name()='rcof']/*[local-name()='rc']">
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="bold">
																	<xsl:text>Ref.Catastral:</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="normal">
																	<xsl:value-of select="./*[local-name()='rcof']/*[local-name()='rc']/*[local-name()='pc1']"/><xsl:text> </xsl:text>
																	<xsl:value-of select="./*[local-name()='rcof']/*[local-name()='rc']/*[local-name()='pc2']"/><xsl:text> </xsl:text>
																	<xsl:value-of select="./*[local-name()='rcof']/*[local-name()='rc']/*[local-name()='car']"/><xsl:text> </xsl:text>
																	<xsl:value-of select="./*[local-name()='rcof']/*[local-name()='rc']/*[local-name()='cc1']"/><xsl:text> </xsl:text>
																	<xsl:value-of select="./*[local-name()='rcof']/*[local-name()='rc']/*[local-name()='cc2']"/>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
												<xsl:if test="./*[local-name()='rcof']/*[local-name()='rfin']">
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="bold">
																	<xsl:text>Ref.Finca</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="normal">
																	<xsl:value-of select="./*[local-name()='rcof']/*[local-name()='rfin']/*[local-name()='pc1']"/><xsl:text> </xsl:text>
																	<xsl:value-of select="./*[local-name()='rcof']/*[local-name()='rfin']/*[local-name()='pc2']"/>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
												<xsl:if test="./*[local-name()='idp_out']/*[local-name()='nom']">
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="bold">
																	<xsl:text>Titular:</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="normal">
																	<xsl:value-of select="./*[local-name()='idp_out']/*[local-name()='nom']"/>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
												<xsl:if test="./*[local-name()='idp_out']/*[local-name()='nif']">
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="bold">
																	<xsl:text>NIF:</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="normal">
																	<xsl:value-of select="./*[local-name()='idp_out']/*[local-name()='nif']"/>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
												<xsl:if test="./*[local-name()='sup']">
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="bold">
																	<xsl:text>Superficie:</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="normal">
																	<xsl:value-of select="./*[local-name()='sup']"/>
																	<xsl:text>m</xsl:text>
																	<fo:inline baseline-shift="super" font-size="3px">
																		<xsl:text>2</xsl:text>
																	</fo:inline>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
												<xsl:if test="./*[local-name()='lloc']">
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="bold">
																	<xsl:text>Domicilio de la Finca:</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="normal">
																	<xsl:value-of select="./*[local-name()='lloc']"/>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
												<xsl:if test="./*[local-name()='dtrus']/*[local-name()='lorus']/*[local-name()='cma']">
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="bold">
																	<xsl:text>Cod. Municipal Agregado:</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="normal">
																	<xsl:value-of select="./*[local-name()='dtrus']/*[local-name()='lorus']/*[local-name()='cma']"/>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
												<xsl:if test="./*[local-name()='dtrus']/*[local-name()='lorus']/*[local-name()='czc']">
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="bold">
																	<xsl:text>Cod. Zona Concentración:</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="normal">
																	<xsl:value-of select="./*[local-name()='dtrus']/*[local-name()='lorus']/*[local-name()='czc']"/>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
												<xsl:if test="./*[local-name()='dtrus']/*[local-name()='lorus']/*[local-name()='cpp']/*[local-name()='cpp']">
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="bold">
																	<xsl:text>Cod.Ide.Rústica:</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="normal">
																	<xsl:value-of select="./*[local-name()='dtrus']/*[local-name()='lorus']/*[local-name()='cpp']/*[local-name()='cpp']"/>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
												<xsl:if test="./*[local-name()='dtrus']/*[local-name()='lorus']/*[local-name()='cpp']/*[local-name()='cpo']">
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="bold">
																	<xsl:text>Cod.Poligono:</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="normal">
																	<xsl:value-of select="./*[local-name()='dtrus']/*[local-name()='lorus']/*[local-name()='cpp']/*[local-name()='cpo']"/>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
												<xsl:if test="./*[local-name()='dtrus']/*[local-name()='lorus']/*[local-name()='cpp']/*[local-name()='cpa']">
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="bold">
																	<xsl:text>Cod.Parcela</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="normal">
																	<xsl:value-of select="./*[local-name()='dtrus']/*[local-name()='lorus']/*[local-name()='cpp']/*[local-name()='cpa']"/>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
												<xsl:if test="./*[local-name()='dtrus']/*[local-name()='lorus']/*[local-name()='npa']">
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="bold">
																	<xsl:text>Nombre Paraje</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
														
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt"
																	space-after.optimum="1pt" display-align="after" text-align="left" margin="0pt">
																<fo:inline font-weight="normal">
																	<xsl:value-of select="./*[local-name()='dtrus']/*[local-name()='lorus']/*[local-name()='npa']"/>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
											</fo:table-body>
										</fo:table>
									</xsl:for-each>
								</xsl:if>
								<!-- LISTA DE TITULARES -->
								<xsl:if test="./*[local-name()='listaTitulares']">
									<xsl:for-each select="./*[local-name()='listaTitulares']/*">
										<xsl:variable name="listaTitularesCount" select="position()"/>
										<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<xsl:if test="position() = 1">
												<fo:table table-layout="fixed" width="100%" space-after="3mm">
													<fo:table-body start-indent="0pt">
														<fo:table-row>
															<fo:table-cell padding="1pt" display-align="center" background-color="#B0C4DE">
																<fo:block>
																	<fo:inline font-weight="bold" display-align="after" font-size="9pt" font-family="Arial,Helvetica,sans-serif">
																		<xsl:text>Lista de Titulares</xsl:text>
																	</fo:inline>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</fo:table-body>
												</fo:table>
											</xsl:if>
											<fo:table table-layout="fixed" width="100%" space-after="4mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="1pt" display-align="center">
															<fo:block border-color="#B0C4DE" border-style="solid" border="1px 1px 1px 1px">
																<fo:inline font-weight="bold" display-align="after" font-size="9pt" font-family="Arial,Helvetica,sans-serif">
																	<xsl:text>Datos del Titular </xsl:text>
																	<xsl:value-of select="$listaTitularesCount"/>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(0.6)"/>
												<fo:table-column column-width="proportional-column-width(1.4)"/>
												<fo:table-body start-indent="0pt">
													<!-- bloque información del derecho -->
													<xsl:if test="./*[local-name()='der']">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Información del Derecho:</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:if test="./*[local-name()='der']/*[local-name()='cdr']">
																				<xsl:text>Código de Derecho:</xsl:text>
																			</xsl:if>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" space-before="3pt" display-align="after">
																		<fo:inline font-weight="normal">
																			<xsl:choose>
																				<xsl:when test="./*[local-name()='der']/*[local-name()='cdr'] = 'PR' ">
																					<xsl:text>Propiedad</xsl:text>
																				</xsl:when>
																				<xsl:when test="./*[local-name()='der']/*[local-name()='cdr'] = 'NP' ">
																					<xsl:text>Nuda propiedad</xsl:text>
																				</xsl:when>
																				<xsl:when test="./*[local-name()='der']/*[local-name()='cdr'] = 'US' ">
																					<xsl:text>Usufructo</xsl:text>
																				</xsl:when>
																				<xsl:when test="./*[local-name()='der']/*[local-name()='cdr'] = 'CA' ">
																					<xsl:text>Concesión administrativa</xsl:text>
																				</xsl:when>
																				<xsl:when test="./*[local-name()='der']/*[local-name()='cdr'] = 'DS' ">
																					<xsl:text>Derecho de superficie</xsl:text>
																				</xsl:when>
																				<xsl:when test="./*[local-name()='der']/*[local-name()='cdr'] = 'DF' ">
																					<xsl:text>Disfrutador</xsl:text>
																				</xsl:when>
																			</xsl:choose>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:if test="./*[local-name()='der']/*[local-name()='pct']">
																				<xsl:text>Porcentaje:</xsl:text>
																			</xsl:if>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" space-before="3pt" display-align="after">
																		<fo:inline font-weight="normal">
																			<xsl:value-of select="./*[local-name()='der']/*[local-name()='pct']"/>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:if test="./*[local-name()='ord']">
																				<xsl:text>Ordinal del Titular:</xsl:text>
																			</xsl:if>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" space-before="3pt" display-align="after">
																		<fo:inline font-weight="normal">
																			<xsl:value-of select="./*[local-name()='ord']"/>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
													<!-- bloque 2 -->
													<xsl:if test="./*[local-name()='idp'] ">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:if test="./*[local-name()='idp']/*[local-name()='nif']">
																				<xsl:text>NIF:</xsl:text>
																			</xsl:if>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			<xsl:value-of select="./*[local-name()='idp']/*[local-name()='nif']"/>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:if test="./*[local-name()='idp']/*[local-name()='nom']">
																				<xsl:text>Nombre completo del titular:</xsl:text>
																			</xsl:if>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			<xsl:value-of select="./*[local-name()='idp']/*[local-name()='nom']"/>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
													<!-- bloque 3 -->
													<xsl:if test="./*[local-name()='idpa']">
														<xsl:if test="./*[local-name()='idpa']/*[local-name()='nif']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>NIF:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='idpa']/*[local-name()='nif']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='idpa']/*[local-name()='anif']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Motivo de la ausencia de NIF:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:choose>
																					<xsl:when test="./*[local-name()='idpa']/*[local-name()='anif'] = '1' ">
																						<xsl:text>Extranjero sin NIE</xsl:text>
																					</xsl:when>
																					<xsl:when test="./*[local-name()='idpa']/*[local-name()='anif'] = '2' ">
																						<xsl:text>Menor de edad sin NIF</xsl:text>
																					</xsl:when>
																					<xsl:when test="./*[local-name()='idpa']/*[local-name()='anif'] = '9' ">
																						<xsl:text>Otras situaciones</xsl:text>
																					</xsl:when>
																				</xsl:choose>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='idpa']/*[local-name()='nom']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Nombre completo del titular:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='idpa']/*[local-name()='nom']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
													</xsl:if>
													<!-- bloque 4 -->
													<xsl:if test="./*[local-name()='idps']">
														<xsl:if test="./*[local-name()='idps']/*[local-name()='nif']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>NIF:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='idps']/*[local-name()='nif']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='idps']/*[local-name()='cii']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Clave de identificación interna:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='idps']/*[local-name()='cii']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='idps']/*[local-name()='nom']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Nombre completo del titular:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='idps']/*[local-name()='nom']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
													</xsl:if>
													<!-- bloque 5 -->
													<xsl:if test="./*[local-name()='idp_out']">
														<xsl:if test="./*[local-name()='idp_out']/*[local-name()='nif']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>NIF:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='idp_out']/*[local-name()='nif']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='idp_out']/*[local-name()='nom']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Nombre completo del titular:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='idp_out']/*[local-name()='nom']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
													</xsl:if>
													<!-- bloque 6 -->
													<xsl:if test="./*[local-name()='lder']">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Derecho de la Propiedad:</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			<xsl:value-of select="./*[local-name()='lder']"/>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
													<!-- bloque 7 DOMICILIO -->
													<xsl:if test="./*[local-name()='df']">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Domicilio Fiscal:</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
														<xsl:if test="./*[local-name()='df']/*[local-name()='loine']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Código de Derecho:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='loine']/*[local-name()='cp']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='loine']/*[local-name()='cm']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Código de Municpio INE:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='loine']/*[local-name()='cm']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='cmc']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Código de Municpio segun DGC:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='cmc']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='np']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Nombre de provincia:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='np']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='nm']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Nombre de municipio:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='nm']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='nem']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Nombre de entidad menor:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='nem']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
													</xsl:if>
													<xsl:if test="./*[local-name()='df']/*[local-name()='dir']">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Dirección:</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
														<xsl:if test="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='cv']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Código de Vía:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='cv']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='tv']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Tipo de Vía:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='tv']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='nv']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Nombre de Vía:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='nv']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='pnp']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Primer nombre de policía:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='pnp']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='plp']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Letra del Primer número de policía:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='plp']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='snp']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Segundo número de policía:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='snp']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='slp']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Letra del segundo número de policía:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='slp']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='km']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Kilómetro:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='km']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='td']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Dirección no estructurada:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='dir']/*[local-name()='td']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
													</xsl:if>
													<xsl:if test="./*[local-name()='df']/*[local-name()='loint']">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Localización interna:</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
														<xsl:if test="./*[local-name()='df']/*[local-name()='loint']/*[local-name()='bq']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Bloque:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='loint']/*[local-name()='bq']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='loint']/*[local-name()='es']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Escalera:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='loint']/*[local-name()='es']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='loint']/*[local-name()='pt']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Planta:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='loint']/*[local-name()='pt']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='loint']/*[local-name()='pu']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Puerta:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='df']/*[local-name()='loint']/*[local-name()='pu']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='df']/*[local-name()='pos']">
															<xsl:if test="./*[local-name()='df']/*[local-name()='pos']/*[local-name()='dp']">
																<fo:table-row>
																	<fo:table-cell padding="2pt" display-align="center">
																		<fo:block>
																			<fo:inline-container>
																				<fo:block>
																					<xsl:text>&#x2029;</xsl:text>
																				</fo:block>
																			</fo:inline-container>
																		</fo:block>
																		<fo:block text-align="left" margin="0pt">
																			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																				<fo:inline font-weight="bold">
																					<xsl:text>Datos Postales:</xsl:text>
																					<fo:leader leader-pattern="space"/>
																				</fo:inline>
																			</fo:block>
																		</fo:block>
																	</fo:table-cell>
																	<fo:table-cell padding="2pt" display-align="center">
																		<fo:block>
																			<fo:inline-container>
																				<fo:block>
																					<xsl:text>&#x2029;</xsl:text>
																				</fo:block>
																			</fo:inline-container>
																		</fo:block>
																		<fo:block text-align="left" margin="0pt">
																			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																				<fo:inline font-weight="normal">
																					<xsl:value-of select="./*[local-name()='df']/*[local-name()='pos']/*[local-name()='dp']"/>
																					<fo:leader leader-pattern="space"/>
																				</fo:inline>
																			</fo:block>
																		</fo:block>
																	</fo:table-cell>
																</fo:table-row>
															</xsl:if>
															<xsl:if test="./*[local-name()='df']/*[local-name()='pos']/*[local-name()='ac']">
																<fo:table-row>
																	<fo:table-cell padding="2pt" display-align="center">
																		<fo:block>
																			<fo:inline-container>
																				<fo:block>
																					<xsl:text>&#x2029;</xsl:text>
																				</fo:block>
																			</fo:inline-container>
																		</fo:block>
																		<fo:block text-align="left" margin="0pt">
																			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																				<fo:inline font-weight="bold">
																					<xsl:text>Apartado de Correos:</xsl:text>
																					<fo:leader leader-pattern="space"/>
																				</fo:inline>
																			</fo:block>
																		</fo:block>
																	</fo:table-cell>
																	<fo:table-cell padding="2pt" display-align="center">
																		<fo:block>
																			<fo:inline-container>
																				<fo:block>
																					<xsl:text>&#x2029;</xsl:text>
																				</fo:block>
																			</fo:inline-container>
																		</fo:block>
																		<fo:block text-align="left" margin="0pt">
																			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																				<fo:inline font-weight="normal">
																					<xsl:value-of select="./*[local-name()='df']/*[local-name()='pos']/*[local-name()='ac']"/>
																					<fo:leader leader-pattern="space"/>
																				</fo:inline>
																			</fo:block>
																		</fo:block>
																	</fo:table-cell>
																</fo:table-row>
															</xsl:if>
														</xsl:if>
													</xsl:if>
													<!-- FIN DOMICILIO -->
													<xsl:if test="./*[local-name()='ldf']">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Literal del Domicilio Fiscal:</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			<xsl:value-of select="./*[local-name()='ldf']"/>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
													<xsl:if test="./*[local-name()='cony']">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Información Sobre el conyuge:</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
														<xsl:if test="./*[local-name()='cony']/*[local-name()='nif']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>NIF:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='cony']/*[local-name()='nif']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='cony']/*[local-name()='nom']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Apellidos y Nombre o Razón Social:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='cony']/*[local-name()='nom']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
													</xsl:if>
													<xsl:if test="./*[local-name()='idcbf']">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Identificación de la comunidad de bienes formal:</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
														<xsl:if test="./*[local-name()='idcbf']/*[local-name()='nifcb']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Nif de la comunidad de bienes:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='idcbf']/*[local-name()='nifcb']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='idcbf']/*[local-name()='nomcb']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Denominación o razón social de la Comunidad de Bienes:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='idcbf']/*[local-name()='nomcb']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
													</xsl:if>
													<xsl:if test="./*[local-name()='iatit']">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Información adicional de titularidad:</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
														<xsl:if test="./*[local-name()='iatit']/*[local-name()='nifcy']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>NIF del Cónyuge:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='iatit']/*[local-name()='nifcy']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='iatit']/*[local-name()='nifcb']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>NIF de la Comunidad de Bienes:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='iatit']/*[local-name()='nifcb']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='iatit']/*[local-name()='ct']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Complemento de titularidad:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='iatit']/*[local-name()='ct']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
													</xsl:if>
													<xsl:if test="./*[local-name()='rtit']">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Rango de titularidad:</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
														<xsl:if test="./*[local-name()='rtit']/*[local-name()='fit']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Fecha de Inicio de la titularidad:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='rtit']/*[local-name()='fit']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<xsl:if test="./*[local-name()='rtit']/*[local-name()='fft']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="bold">
																				<xsl:text>Fecha de fin de la titularidad:</xsl:text>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block>
																		<fo:inline-container>
																			<fo:block>
																				<xsl:text>&#x2029;</xsl:text>
																			</fo:block>
																		</fo:inline-container>
																	</fo:block>
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-weight="normal">
																				<xsl:value-of select="./*[local-name()='rtit']/*[local-name()='fft']"/>
																				<fo:leader leader-pattern="space"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
													</xsl:if>
													<xsl:if test="./*[local-name()='faj']">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Fecha de alteración juridica:</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block>
																	<fo:inline-container>
																		<fo:block>
																			<xsl:text>&#x2029;</xsl:text>
																		</fo:block>
																	</fo:inline-container>
																</fo:block>
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="normal">
																			<xsl:value-of select="./*[local-name()='faj']/*[local-name()='faj']"/>
																			<fo:leader leader-pattern="space"/>
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
						<!-- DATOS FINCA -->
								<xsl:if test="./*[local-name()='finca']">
									<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										<fo:table table-layout="fixed" width="100%" space-after="3mm">
											<fo:table-body start-indent="0pt">
												<fo:table-row>
													<fo:table-cell padding="1pt" display-align="center" background-color="#B0C4DE">
														<fo:block>
															<fo:inline font-weight="bold" display-align="after" font-size="9pt" font-family="Arial,Helvetica,sans-serif">
																<xsl:text>Datos de la finca donde está situado el bien inmueble</xsl:text>
															</fo:inline>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>
										<!-- DATOS DE LA FINCA -->
										<!-- localización y tipo de inmueble --> 
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
											<fo:table-column column-width="proportional-column-width(0.6)"/>
											<fo:table-column column-width="proportional-column-width(1.4)"/>
											<fo:table-body start-indent="0pt">
												
												<xsl:if test="./*[local-name()='finca']/*[local-name()='ldt']">
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">		
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-weight="bold">
																	<xsl:text>Localización de la finca:</xsl:text>
																</fo:inline>
															</fo:block>								
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">		
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	<xsl:value-of select="./*[local-name()='finca']/*[local-name()='ldt']"/>
																</fo:inline>
															</fo:block>								
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
												<xsl:if test="./*[local-name()='finca']/*[local-name()='ltp']">
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Tipo de Inmueble:</xsl:text>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-style="normal">
																			<xsl:value-of select="./*[local-name()='finca']/*[local-name()='ltp']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</xsl:if>
												<xsl:if test="./*[local-name()='finca']/*[local-name()='dff']/*[local-name()='ssf']">
													<xsl:if test="./*[local-name()='finca']/*[local-name()='dff']/*[local-name()='ssf']/*[local-name()='ss']">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">																														
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Superficie del suelo de la finca:</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>																	
																</fo:block>															
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">																														
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="./*[local-name()='finca']/*[local-name()='dff']/*[local-name()='ssf']/*[local-name()='ss']"/>
																		<xsl:text>m</xsl:text>
																	</fo:inline>
																	<fo:inline baseline-shift="super" font-size="5px">
																		<xsl:text>2</xsl:text>
																	</fo:inline>
																</fo:block>															
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
													<xsl:if test="./*[local-name()='finca']/*[local-name()='dff']/*[local-name()='ssf']/*[local-name()='sct']">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">	
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Superficie Construida de la finca:</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>															
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">	
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-style="normal">
																			<xsl:value-of select="./*[local-name()='finca']/*[local-name()='dff']/*[local-name()='ssf']/*[local-name()='sct']"/>
																			<xsl:text>m</xsl:text>
																		</fo:inline>
																		<fo:inline baseline-shift="super" font-size="5px">
																			<xsl:text>2</xsl:text>
																		</fo:inline>
																	</fo:block>
																</fo:block>															
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
												</xsl:if>
												<xsl:if test="./*[local-name()='finca']/*[local-name()='infgraf']">
													<xsl:if test="./*[local-name()='finca']/*[local-name()='infgraf']/*[local-name()='esc']">
														<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">																
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Escala:</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">																
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-style="italic" text-decoration="underline">
																			<xsl:value-of select="./*[local-name()='finca']/*[local-name()='infgraf']/*[local-name()='esc']"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</xsl:if>
												</xsl:if>
												<!-- URL -->
												<xsl:if test="./*[local-name()='finca']/*[local-name()='infgraf']/*[local-name()='igraf']">	
													<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>URL con información cartográfica de la finca:</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="6pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="italic" text-decoration="underline">
																		<xsl:value-of select="./*[local-name()='finca']/*[local-name()='infgraf']/*[local-name()='igraf']"/>
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
							</fo:block>
						</xsl:for-each>
					</xsl:if>
				</fo:block>
				<!-- FIN LISTA -->
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
