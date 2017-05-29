<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
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
									<!--<fo:external-graphic content-width="scale-to-fit" content-height="100%" width="100%" scaling="uniform" src="url('data:image/jpeg;base64,{$logoDerecha}')"/> -->
									<fo:external-graphic content-width="scale-to-fit" content-height="scale-to-fit" width="4cm" height="2cm" scaling="uniform" src="url('data:image/jpeg;base64,{$logoIzquierda}')"/>
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
					<xsl:variable name="matricula" select="//*[local-name()='Consulta']/*[local-name()='Matricula']"/>
					<xsl:variable name="bastidor" select="//*[local-name()='Consulta']/*[local-name()='Bastidor']"/>
					<xsl:variable name="nive" select="//*[local-name()='Consulta']/*[local-name()='NIVE']"/>
					<fo:table-row>
						<fo:table-cell padding="2pt" display-align="center">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<fo:inline font-weight="bold">
									<xsl:if test="$matricula">
										<xsl:text>Matricula</xsl:text>
									</xsl:if>
									<xsl:if test="$bastidor">
										<xsl:text>Bastidor</xsl:text>
									</xsl:if>
									<xsl:if test="$nive">
										<xsl:text>NIVE</xsl:text>
									</xsl:if>
								</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5">
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
								<xsl:if test="$matricula">
									<xsl:value-of select="$matricula"/>
								</xsl:if>
								<xsl:if test="$bastidor">
									<xsl:value-of select="$bastidor"/>
								</xsl:if>
								<xsl:if test="$nive">
									<xsl:value-of select="$nive"/>
								</xsl:if>
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
							<fo:block font-size="8pt" text-align="left" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
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
							<xsl:text>Datos Generales</xsl:text>
						</fo:inline>
					</fo:block>
				</fo:block>
				<fo:block border-style="solid" border-width="1px 1px 1px 1px">
					<xsl:variable name="datos" select="$datosEspecificos/*[local-name()='Retorno']/*[local-name()='DatosVehiculo']/*[local-name()='datosGenerales']"/>
					<xsl:if test="$datos">
						<xsl:variable name="datosVehiculo" select="$datos/*[local-name()='descripcionVehiculo']"/>
						<xsl:if test="$datosVehiculo">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos del Vehículo</xsl:text>
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
															<xsl:text>Bastidor:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$datosVehiculo/*[local-name()='bastidor']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Marca:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$datosVehiculo/*[local-name()='marca']/*[local-name()='descripcion']"/>
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
															<xsl:text>Tipo Vehículo:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$datosVehiculo/*[local-name()='tipoVehiculo']/*[local-name()='descripcion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Modelo:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$datosVehiculo/*[local-name()='modelo']"/>
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
															<xsl:text>País Procedencia:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$datosVehiculo/*[local-name()='paisProcedencia']/*[local-name()='descripcion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Servicio:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$datosVehiculo/*[local-name()='servicio']/*[local-name()='descripcion']"/>
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
															<xsl:text>Servicio Complementario:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$datosVehiculo/*[local-name()='servicioComplementario']/*[local-name()='descripcion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Tipo Industria:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$datosVehiculo/*[local-name()='tipoIndustria']/*[local-name()='descripcion']"/>
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
															<xsl:text>NIVE:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$datosVehiculo/*[local-name()='nive']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</xsl:if>
						<xsl:variable name="domicilioVehiculo" select="$datos/*[local-name()='domicilioVehiculo']"/>
						<xsl:if test="$domicilioVehiculo/*[local-name()='calle']">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos Domicilio Vehículo</xsl:text>
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
															<xsl:text>Calle</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$domicilioVehiculo/*[local-name()='calle']"/>
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
															<xsl:text>Cod Postal:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$domicilioVehiculo/*[local-name()='codPostal']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
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
															<xsl:value-of select="$domicilioVehiculo/*[local-name()='municipio']"/>
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
															<xsl:text>Provincia:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$domicilioVehiculo/*[local-name()='provincia']/*[local-name()='descripcion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</xsl:if>
						<xsl:variable name="domicilioIne" select="$datos/*[local-name()='domicilioVehiculoIne']"/>
						<xsl:if test="$domicilioIne/*[local-name()='municipio']!= ''">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Domicilio Ine</xsl:text>
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
															<xsl:text>Dirección:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$domicilioIne/*[local-name()='tipoVia']/text()"/>
															<xsl:text> </xsl:text>
															<xsl:value-of select="$domicilioIne/*[local-name()='via']/text()"/>
															<xsl:if test="$domicilioIne/*[local-name()='numeroVia']/text() != ''">
																<xsl:text>, Nº </xsl:text>
																<xsl:value-of select="$domicilioIne/*[local-name()='numeroVia']/text()"/>
															</xsl:if>
															<xsl:if test="$domicilioIne/*[local-name()='portal']/text() != ''">
																<xsl:text>, Portal </xsl:text>
																<xsl:value-of select="$domicilioIne/*[local-name()='portal']/text()"/>
															</xsl:if>
															<xsl:if test="$domicilioIne/*[local-name()='bloque']/text() != ''">
																<xsl:text>, Bloque </xsl:text>
																<xsl:value-of select="$domicilioIne/*[local-name()='bloque']/text()"/>
															</xsl:if>
															<xsl:if test="$domicilioIne/*[local-name()='escalera']/text() != ''">
																<xsl:text>, Escalera </xsl:text>
																<xsl:value-of select="$domicilioIne/*[local-name()='escalera']/text()"/>
															</xsl:if>
															<xsl:if test="$domicilioIne/*[local-name()='planta']/text() != ''">
																<xsl:text>, Planta </xsl:text>
																<xsl:value-of select="$domicilioIne/*[local-name()='planta']/text()"/>
															</xsl:if>
															<xsl:if test="$domicilioIne/*[local-name()='puerta']/text() != ''">
																<xsl:text>, Puerta </xsl:text>
																<xsl:value-of select="$domicilioIne/*[local-name()='puerta']/text()"/>
															</xsl:if>
															<xsl:if test="$domicilioIne/*[local-name()='puerta']/text() != '0'">
																<xsl:text>, Km </xsl:text>
																<xsl:value-of select="$domicilioIne/*[local-name()='km']/text()"/>
															</xsl:if>
															<xsl:if test="$domicilioIne/*[local-name()='puerta']/text() != '0'">
																<xsl:text>, Hm </xsl:text>
																<xsl:value-of select="$domicilioIne/*[local-name()='hm']/text()"/>
															</xsl:if>
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
															<xsl:text>Cod Postal:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<xsl:value-of select="$domicilioIne/*[local-name()='codPostal']"/>
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
															<xsl:value-of select="$domicilioIne/*[local-name()='pueblo']"/>
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
															<xsl:value-of select="$domicilioIne/*[local-name()='municipio']"/>
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
														<fo:inline font-style="normal">
															<xsl:value-of select="$domicilioIne/*[local-name()='provincia']/*[local-name()='descripcion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
						</xsl:if>
						<xsl:variable name="fechas" select="$datos/*[local-name()='fechasControl']"/>
						<xsl:if test="$fechas">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Fechas Control</xsl:text>
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
															<xsl:text>Fecha Caducidad Turística: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center"> 
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
															<!-- xsl:value-of select="$fechas/*[local-name()='fechaCaducidadTuristica']"/ -->
															<xsl:choose>
																	<xsl:when test="string($fechas/*[local-name()='fechaCaducidadTuristica']) = ''"></xsl:when>
																	<xsl:otherwise>
																		<xsl:value-of select="concat(substring(string($fechas/*[local-name()='fechaCaducidadTuristica']),9,2),'/',substring(string($fechas/*[local-name()='fechaCaducidadTuristica']),6,2),'/',substring(string($fechas/*[local-name()='fechaCaducidadTuristica']),1,4))"/>
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
															<xsl:text>Fecha Importación: </xsl:text>
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
																	<xsl:when test="string($fechas/*[local-name()='fechaImportacion']) = ''"></xsl:when>
																	<xsl:otherwise>
																		<xsl:value-of select="concat(substring(string($fechas/*[local-name()='fechaImportacion']),9,2),'/',substring(string($fechas/*[local-name()='fechaImportacion']),6,2),'/',substring(string($fechas/*[local-name()='fechaImportacion']),1,4))"/>
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
															<xsl:text>Fecha ITV: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<!-- xsl:value-of select="$fechas/*[local-name()='fechaItv']"/ -->
															<xsl:choose>
																	<xsl:when test="string($fechas/*[local-name()='fechaItv']) = ''"></xsl:when>
																	<xsl:otherwise>
																		<xsl:value-of select="concat(substring(string($fechas/*[local-name()='fechaItv']),9,2),'/',substring(string($fechas/*[local-name()='fechaItv']),6,2),'/',substring(string($fechas/*[local-name()='fechaItv']),1,4))"/>
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
															<xsl:text>Fecha Matriculación: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															<!-- xsl:value-of select="$fechas/*[local-name()='fechaMatriculacion']"/ -->
															<xsl:choose>
																	<xsl:when test="string($fechas/*[local-name()='fechaMatriculacion']) = ''"></xsl:when>
																	<xsl:otherwise>
																		<xsl:value-of select="concat(substring(string($fechas/*[local-name()='fechaMatriculacion']),9,2),'/',substring(string($fechas/*[local-name()='fechaMatriculacion']),6,2),'/',substring(string($fechas/*[local-name()='fechaMatriculacion']),1,4))"/>
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
															<xsl:text>Fecha Primera Matriculación: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-style="normal">
															 <xsl:choose>
																	<xsl:when test="string($fechas/*[local-name()='fechaPrimeraMatriculacion']) = ''"></xsl:when>
																	<xsl:otherwise>
																		<xsl:value-of select="concat(substring(string($fechas/*[local-name()='fechaPrimeraMatriculacion']),9,2),'/',substring(string($fechas/*[local-name()='fechaPrimeraMatriculacion']),6,2),'/',substring(string($fechas/*[local-name()='fechaPrimeraMatriculacion']),1,4))"/>
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
						</xsl:if>
						
						
						<xsl:variable name="indicadores" select="$datos/*[local-name()='indicadores']"/>
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
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(0.3)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(0.3)"/>
									<fo:table-body start-indent="0pt">
										<!-- Fila 1 Hijo -->
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Baja Definitiva: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='bajaDefinitiva']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Baja Temporal: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='bajaTemporal']"/>
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
															<xsl:text>Carga EE FF: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='cargaEEFF']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Embargo: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='embargo']"/>
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
															<xsl:text>Exceso Peso: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='excesoPesoDimension']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Importación: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='importacion']"/>
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
															<xsl:text>Incidencias: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='incidencias']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Limitación Disposición: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='limitacionDisposicion']"/>
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
															<xsl:text>Posesión: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='posesion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Precinto: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='precinto']"/>
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
															<xsl:text>Puerto Franco: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='puertoFranco']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Reformas: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='reformas']"/>
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
															<xsl:text>Renting: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='renting']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Subasta: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='subasta']"/>
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
															<xsl:text>Sustracción: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='sustraccion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Tutela: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$indicadores/*[local-name()='tutela']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
						
					</xsl:if>	
						
						
						<xsl:variable name="cotitulares" select="$datos/*[local-name()='listaCotitulares']"/>
						<xsl:if test="$cotitulares/*[local-name()='cotitular']">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Cotitulares</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
							<fo:block margin-top="10pt" margin-left="2pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-column column-width="proportional-column-width(0.3)"/>
									<fo:table-column column-width="proportional-column-width(1)"/> 
									<fo:table-body start-indent="0pt">
										<!-- Fila 1 Hijo -->
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Documentación: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Filiación: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										 
										</fo:table-row>
										<xsl:for-each select="$cotitulares/*[local-name()='cotitular']">
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='idDocumento']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>			
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='filiacion']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>		
											</fo:table-row>				
									</xsl:for-each>
								</fo:table-body>
						</fo:table>
					</fo:block>	
				</xsl:if >	
					
					
					<xsl:variable name="matriculacion" select="$datos/*[local-name()='matriculacion']"/>
						<xsl:if test="$matriculacion">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Matriculación</xsl:text>
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
															<xsl:text>Clase Matriculación: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$matriculacion/*[local-name()='claseMatriculacion']/*[local-name()='descripcion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Fecha Matriculación: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal"> 
															<xsl:choose>
																	<xsl:when test="string($matriculacion/*[local-name()='fechaMatriculacion']) = ''"></xsl:when>
																	<xsl:otherwise>
																		<xsl:value-of select="concat(substring(string($matriculacion/*[local-name()='fechaMatriculacion']),9,2),'/',substring(string($matriculacion/*[local-name()='fechaMatriculacion']),6,2),'/',substring(string($matriculacion/*[local-name()='fechaMatriculacion']),1,4))"/>
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
														<fo:inline font-weight="normal">
															<xsl:value-of select="$matriculacion/*[local-name()='jefatura']/*[local-name()='descripcion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Matricula: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$matriculacion/*[local-name()='matricula']"/>
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
															<xsl:text>Sucursal: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="3">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$matriculacion/*[local-name()='sucursal']/*[local-name()='descripcion']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
					</xsl:if>
					
					
											
						<xsl:variable name="requisitoria" select="$datos/*[local-name()='requisitoria']"/>
						<xsl:if test="$requisitoria">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Requisitoria</xsl:text>
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
															<xsl:text>Tipo: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$requisitoria/*[local-name()='tipo']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Descripción: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$requisitoria/*[local-name()='descripcion']"/>
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
															<xsl:text>Fecha Vigor: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal"> 
															<xsl:choose>
																	<xsl:when test="string($requisitoria/*[local-name()='fechaVigor']) = ''"></xsl:when>
																	<xsl:otherwise>
																		<xsl:value-of select="concat(substring(string($requisitoria/*[local-name()='fechaVigor']),9,2),'/',substring(string($requisitoria/*[local-name()='fechaVigor']),6,2),'/',substring(string($requisitoria/*[local-name()='fechaVigor']),1,4))"/>
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
															<xsl:text>Acciones: </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$requisitoria/*[local-name()='acciones']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
								</fo:table-body>
								</fo:table>
							</fo:block>		
					</xsl:if>
					
						<xsl:variable name="servicioAutonomo" select="$datos/*[local-name()='servicioAutonomo']"/>
						<xsl:if test="$servicioAutonomo">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Servicio Autónomo</xsl:text>
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
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-body start-indent="0pt">
										<!-- Fila 1 Hijo -->
										<fo:table-row>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Código IAE:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$servicioAutonomo/*[local-name()='codigoIAE']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Titular Autónomo:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$servicioAutonomo/*[local-name()='doiTitularAutonomo']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Indicador:</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="normal">
															<xsl:value-of select="$servicioAutonomo/*[local-name()='indicador']"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
								</fo:table-body>
								</fo:table>
							</fo:block>							
					</xsl:if>
					 
					<xsl:variable name="titular" select="$datos/*[local-name()='titular']"/>
					<xsl:if test="$titular">
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
					
								<xsl:variable name="datosPersona" select="$titular/*[local-name()='datosPersona']/*[local-name()='identificacionPFisica']"/>
								<xsl:if test="$datosPersona/*[local-name()='idDocumento']"> 
									<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
											<fo:table-body start-indent="0pt">
												<fo:table-row>
													<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
														<fo:block>
															<fo:inline font-weight="bold" display-align="after">
																<xsl:text>Datos Identificativos Persona Física</xsl:text>
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
																	<xsl:text>Primer Apellido:</xsl:text>
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
																	<xsl:text>Segundo Apellido:</xsl:text>
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
																	<xsl:text>Fecha Nacimiento:</xsl:text>
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
																	<xsl:text>Dirección Electrónica:</xsl:text>
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
				
								<xsl:variable name="datosEmpresa" select="$titular/*[local-name()='datosPersona']/*[local-name()='identificacionPJuridica']"/>
								<xsl:if test="$datosEmpresa/*[local-name()='cif']" >	
						
									<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
											<fo:table-body start-indent="0pt">
												<fo:table-row>
													<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
														<fo:block>
															<fo:inline font-weight="bold" display-align="after">
																<xsl:text>Datos Identificativos Persona Jurídica</xsl:text>
															</fo:inline>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>
									</fo:block>
									<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
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
																	<xsl:text>Razón Social</xsl:text>
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
				
								
							<xsl:variable name="domicilio" select="$titular/*[local-name()='domicilio']"/>
							<xsl:if test="$domicilio">
								<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
													<fo:block>
														<fo:inline font-weight="bold" display-align="after">
															<xsl:text>Datos Domicilio</xsl:text>
														</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:block>
								<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-body start-indent="0pt">
											<!-- Fila 1 Hijo -->
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-weight="bold">
																<xsl:text>Calle:</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
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
																<xsl:text>Código Postal:</xsl:text>
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
																<xsl:text>Pueblo</xsl:text>
																<fo:leader leader-pattern="space"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$domicilio/*[local-name()='pueblo']"/>
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
												<fo:table-cell padding="2pt" display-align="center">
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
								
							<xsl:variable name="domicilioIne" select="$titular/*[local-name()='domicilioIne']"/>
							<xsl:if test="$domicilioIne/*[local-name()='municipio']!= ''"> 
									<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
													<fo:block>
														<fo:inline font-weight="bold" display-align="after">
															<xsl:text>Domicilio Ine</xsl:text>
														</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:block>
								<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
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
																	<xsl:text>Dirección:</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
													<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="3">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																		<xsl:value-of select="$domicilioIne/*[local-name()='tipoVia']/text()"/>
																		<xsl:text> </xsl:text>
																		<xsl:value-of select="$domicilioIne/*[local-name()='via']/text()"/>
																		<xsl:if test="$domicilioIne/*[local-name()='numeroVia']/text() != ''" >
																			<xsl:text>, Nº </xsl:text>
																			<xsl:value-of select="$domicilioIne/*[local-name()='numeroVia']/text()"/>
																		</xsl:if>
																		<xsl:if test="$domicilioIne/*[local-name()='portal']/text() != ''" >
																			<xsl:text>, Portal </xsl:text>
																			<xsl:value-of select="$domicilioIne/*[local-name()='portal']/text()"/>
																		</xsl:if>
																		<xsl:if test="$domicilioIne/*[local-name()='bloque']/text() != ''" >
																			<xsl:text>, Bloque </xsl:text>
																			<xsl:value-of select="$domicilioIne/*[local-name()='bloque']/text()"/>
																		</xsl:if>
																		<xsl:if test="$domicilioIne/*[local-name()='escalera']/text() != ''" >
																			<xsl:text>, Escalera </xsl:text>
																			<xsl:value-of select="$domicilioIne/*[local-name()='escalera']/text()"/>
																		</xsl:if>
																		<xsl:if test="$domicilioIne/*[local-name()='planta']/text() != ''" >
																			<xsl:text>, Planta </xsl:text>
																			<xsl:value-of select="$domicilioIne/*[local-name()='planta']/text()"/>
																		</xsl:if>
																		<xsl:if test="$domicilioIne/*[local-name()='puerta']/text() != ''" >
																			<xsl:text>, Puerta </xsl:text>
																			<xsl:value-of select="$domicilioIne/*[local-name()='puerta']/text()"/>
																		</xsl:if>
																		<xsl:if test="$domicilioIne/*[local-name()='puerta']/text() != '0'" >
																			<xsl:text>, Km </xsl:text>
																			<xsl:value-of select="$domicilioIne/*[local-name()='km']/text()"/>
																		</xsl:if>
																		<xsl:if test="$domicilioIne/*[local-name()='puerta']/text() != '0'" >
																			<xsl:text>, Hm </xsl:text>
																			<xsl:value-of select="$domicilioIne/*[local-name()='hm']/text()"/>
																		</xsl:if>
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
																	<xsl:text>Cod Postal:</xsl:text>
																	<fo:leader leader-pattern="space"/>
																</fo:inline>
															</fo:block>
														</fo:block>
													</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
														<fo:block text-align="left" margin="0pt">
															<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																<fo:inline font-style="normal">
																	 <xsl:value-of select="$domicilioIne/*[local-name()='codPostal']"/>
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
																	 <xsl:value-of select="$domicilioIne/*[local-name()='pueblo']"/>
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
																	 <xsl:value-of select="$domicilioIne/*[local-name()='municipio']"/>
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
																<fo:inline font-style="normal">
																	 <xsl:value-of select="$domicilioIne/*[local-name()='provincia']/*[local-name()='descripcion']"/>
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
					
					<xsl:variable name="datosTecnicos" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosTecnicos']"/>
					<xsl:if test="$datosTecnicos">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos Técnicos</xsl:text>
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
															<xsl:text>Carrocería</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Catálogo Homologación</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Color</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Fabricante</xsl:text> 
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
																<xsl:value-of select="$datosTecnicos/*[local-name()='carroceria']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$datosTecnicos/*[local-name()='catHomologacion']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$datosTecnicos/*[local-name()='color']/*[local-name()='descripcion']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$datosTecnicos/*[local-name()='fabricante']"/>
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
															<xsl:text>Número Homologación</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Tipo ITV</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Variante</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										 	<fo:table-cell padding="2pt" display-align="center" >
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Versión</xsl:text>
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
																<xsl:value-of select="$datosTecnicos/*[local-name()='numHomologacion']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$datosTecnicos/*[local-name()='tipoItv']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$datosTecnicos/*[local-name()='variante']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$datosTecnicos/*[local-name()='version']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
											</fo:table-cell>
										</fo:table-row> 
										<fo:table-row> 
											<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="3">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Procedencia </xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row> 
											<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="3">
												<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="$datosTecnicos/*[local-name()='procedencia']/*[local-name()='descripcion']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
											</fo:table-cell>
										</fo:table-row> 
									</fo:table-body>
								</fo:table>
							</fo:block>
							<xsl:variable name="combustibles" select="$datosTecnicos/*[local-name()='combustibleEmisiones']"/>
									<xsl:if test="$combustibles">
										<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
															<fo:block>
																<fo:inline font-weight="bold" display-align="after">
																	<xsl:text>Emisiones Combustibles</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block>
										<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
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
																		<xsl:text>Autonomía Eléctrica: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Categoría Eléctrica: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Código Eco: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>CO 2: </xsl:text>
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
																		<xsl:value-of select="$combustibles/*[local-name()='autonomiaElectrica']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$combustibles/*[local-name()='categoriaElectrica']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$combustibles/*[local-name()='codigoEco']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$combustibles/*[local-name()='codos']"/>
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
																		<xsl:text>Consumo: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Eco Innovación: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Nivel Emisiones: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Propulsión: </xsl:text>
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
																		<xsl:value-of select="$combustibles/*[local-name()='consumo']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$combustibles/*[local-name()='ecoInnovacion']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$combustibles/*[local-name()='nivelEmisiones']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$combustibles/*[local-name()='propulsion']/*[local-name()='descripcion']"/>
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
																		<xsl:text>Reducción Eco: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Tipo Alimentación: </xsl:text>
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
																		<xsl:value-of select="$combustibles/*[local-name()='reduccionEco']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$combustibles/*[local-name()='tipoAlimentacion']/*[local-name()='descripcion']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													 
													</fo:table-row>
											
												</fo:table-body>
											</fo:table>
										</fo:block>
										</xsl:if>
										
								<xsl:variable name="distancias" select="$datosTecnicos/*[local-name()='distancias']"/>
									<xsl:if test="$distancias">
										<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
															<fo:block>
																<fo:inline font-weight="bold" display-align="after">
																	<xsl:text>Distancias</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block>
										<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
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
																		<xsl:text>Distancia Ejes: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Vía Anterior: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Vía Posterior: </xsl:text>
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
																		<xsl:value-of select="$distancias/*[local-name()='distanciaEjes']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center"  >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$distancias/*[local-name()='viaAnterior']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														 <fo:table-cell padding="2pt" display-align="center"  >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$distancias/*[local-name()='viaPosterior']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block>	
									</xsl:if>	
								<xsl:variable name="documentos" select="$datosTecnicos/*[local-name()='documentos']"/>
									<xsl:if test="$documentos">
										<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
															<fo:block>
																<fo:inline font-weight="bold" display-align="after">
																	<xsl:text>Documentos</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block>
										<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
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
																		<xsl:text>Certificado Conformidad: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Hoja Rosa: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Hoja Rescate: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Validez Tarjeta: </xsl:text>
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
																		<xsl:value-of select="$documentos/*[local-name()='certificadoConformidad']/*[local-name()='datos']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center"  >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$documentos/*[local-name()='hojaRosa']/*[local-name()='datos']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														 <fo:table-cell padding="2pt" display-align="center"  >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$documentos/*[local-name()='hojaRescate']/*[local-name()='datos']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														 <fo:table-cell padding="2pt" display-align="center"  >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$documentos/*[local-name()='periodoValidezTarjeta']"/>
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
																		<xsl:text>Validez Tarjeta 15: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Validez Tarjeta No Válida: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Validez Tarjeta Válida: </xsl:text> 
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Tarjeta ITV Definitiva: </xsl:text>
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
																		<xsl:value-of select="$documentos/*[local-name()='periodoValidezTarjeta15']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center"  >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$documentos/*[local-name()='periodoValidezTarjetaNoValida']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														 <fo:table-cell padding="2pt" display-align="center"  >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$documentos/*[local-name()='periodoValidezTarjetaValida']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														 <fo:table-cell padding="2pt" display-align="center"  >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$documentos/*[local-name()='tarjetaItvDefinitiva']/*[local-name()='datos']"/>
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
																		<xsl:text>Tarjeta ITV No Válida: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Tarjeta ITV Temporal: </xsl:text>
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
																		<xsl:value-of select="$documentos/*[local-name()='tarjetaItvNoValida']/*[local-name()='datos']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="3">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$documentos/*[local-name()='tarjetaItvTemporal']/*[local-name()='datos']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell> 
													</fo:table-row>											
											
												</fo:table-body>
											</fo:table>
										</fo:block>	
									</xsl:if>	
						
					
									<xsl:variable name="masas" select="$datosTecnicos/*[local-name()='masas']"/>
									<xsl:if test="$masas">
										<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
															<fo:block>
																<fo:inline font-weight="bold" display-align="after">
																	<xsl:text>Masas</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block>
										<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(1.5)"/>
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
																		<xsl:text>Masa Máxima Técnica: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Masas Máxima: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Masa Servicio: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Peso Máximo: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Tara: </xsl:text>
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
																		<xsl:value-of select="$masas/*[local-name()='masaMaxTecnica']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center"  >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$masas/*[local-name()='masaMaxima']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														 <fo:table-cell padding="2pt" display-align="center"  >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$masas/*[local-name()='masaServicio']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														 <fo:table-cell padding="2pt" display-align="center"  >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$masas/*[local-name()='pesoMaximo']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														 <fo:table-cell padding="2pt" display-align="center"  >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$masas/*[local-name()='tara']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
											</fo:table-body>
											</fo:table>
									</fo:block>
							</xsl:if>
					
					
								<xsl:variable name="plazas" select="$datosTecnicos/*[local-name()='plazas']"/>
									<xsl:if test="$plazas">
										<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
															<fo:block>
																<fo:inline font-weight="bold" display-align="after">
																	<xsl:text>Plazas</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block>
										<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
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
																		<xsl:text>Mixtas: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Normales: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell> 
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>PIE: </xsl:text>
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
																		<xsl:value-of select="$plazas/*[local-name()='mixtas']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$plazas/*[local-name()='normales']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$plazas/*[local-name()='numPlazasPie']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell> 

													</fo:table-row>		
											</fo:table-body>
											</fo:table>
										</fo:block>		
								</xsl:if>	
				
									<xsl:variable name="potencias" select="$datosTecnicos/*[local-name()='potencias']"/>
									<xsl:if test="$potencias">
										<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
															<fo:block>
																<fo:inline font-weight="bold" display-align="after">
																	<xsl:text>Potencias</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block>
										<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
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
																		<xsl:text>Cilindrada : </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Potencia fiscal:</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell> 
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Neta máxima: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Relación Potecia/Peso: </xsl:text>
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
																		<xsl:value-of select="$potencias/*[local-name()='cilindrada']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$potencias/*[local-name()='potenciaFiscal']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$potencias/*[local-name()='potenciaNetaMax']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell> 
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$potencias/*[local-name()='relPotenciaPeso']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell> 

													</fo:table-row>		
											</fo:table-body>
											</fo:table>
										</fo:block>		
								</xsl:if>	
				
								 <xsl:variable name="tarjetaitv" select="$datosTecnicos/*[local-name()='datosTarjetaItv']"/>
									<xsl:if test="$tarjetaitv">
										<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
															<fo:block>
																<fo:inline font-weight="bold" display-align="after">
																	<xsl:text>Datos Tarjeta ITV</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block>
										<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-column column-width="proportional-column-width(1)"/>
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
																		<xsl:text>Fabricante Base:</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell> 
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Masa Servicio Base: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Marca Base: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="-1pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Homologación Base: </xsl:text>
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
																		<xsl:value-of select="$tarjetaitv/*[local-name()='tipoTarjetaItv']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$tarjetaitv/*[local-name()='fabricanteBase']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$tarjetaitv/*[local-name()='masaServicioBase']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell> 
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$tarjetaitv/*[local-name()='marcaBase']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell> 
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$tarjetaitv/*[local-name()='chomologacionBase']"/>
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
																		<xsl:text>Tipo Base: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Variante Base:</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell> 
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Versión Base: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="2">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Mom. Base: </xsl:text>
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
																		<xsl:value-of select="$tarjetaitv/*[local-name()='tipoBase']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$tarjetaitv/*[local-name()='varianteBase']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$tarjetaitv/*[local-name()='versionBase']"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell> 
														<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="2">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-style="normal">
																		<xsl:value-of select="$tarjetaitv/*[local-name()='momBase']"/>
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
					

					<xsl:variable name="datosResponsables" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosResponsables']"/>
					<xsl:if test="$datosResponsables">
							<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Datos Responsables</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block> 
							
							<xsl:variable name="listaArrendatarios" select="$datosResponsables/*[local-name()='listaArrendatarios']"/>
									<xsl:if test="$listaArrendatarios">
										<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
															<fo:block>
																<fo:inline font-weight="bold" display-align="after">
																	<xsl:text>Arrendatarios</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block>
										<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
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
																		<xsl:text>Documentación: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Filiación: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Inicio: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Fin: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<xsl:for-each select="$listaArrendatarios/*[local-name()='arrendatario']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-style="normal">
																				<xsl:value-of select="*[local-name()='idDocumento']"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-style="normal">
																				<xsl:value-of select="*[local-name()='filiacion']"/>
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
																				<xsl:value-of select="*[local-name()='fechaFin']"/>
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
									
							<xsl:variable name="listaConductoresHabituales" select="$datosResponsables/*[local-name()='listaConductoresHabituales']"/>
									<xsl:if test="$listaConductoresHabituales">
										<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
															<fo:block>
																<fo:inline font-weight="bold" display-align="after">
																	<xsl:text>Conductores Habituales</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block>
										<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
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
																		<xsl:text>Documentación: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Filiación: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Inicio: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Fin: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<xsl:for-each select="$listaConductoresHabituales/*[local-name()='conductorHabitual']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-style="normal">
																				<xsl:value-of select="*[local-name()='idDocumento']"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-style="normal">
																				<xsl:value-of select="*[local-name()='filiacion']"/>
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
						
							<xsl:variable name="listaPoseedores" select="$datosResponsables/*[local-name()='listaPoseedores']"/>
									<xsl:if test="$listaPoseedores">
										<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
															<fo:block>
																<fo:inline font-weight="bold" display-align="after">
																	<xsl:text>Poseedores</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block>
										<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<xsl:for-each select="$listaPoseedores/*[local-name()='poseedor']">
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
																		<xsl:text>Documentación: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Filiación: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
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
																		<xsl:text>Fecha Posesión: </xsl:text>
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
																				<xsl:value-of select="*[local-name()='idDocumento']"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-style="normal">
																				<xsl:value-of select="*[local-name()='filiacion']"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-style="normal">
																				<xsl:value-of select="*[local-name()='fechaInicio']"/>
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
														<fo:table-cell padding="2pt" display-align="center"  >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Tipo: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center"  >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Situación Posesión: </xsl:text>
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
																				<xsl:value-of select="*[local-name()='tipo']"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-style="normal">
																				<xsl:value-of select="*[local-name()='situacionPosesion']/*[local-name()='descripcion']"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
														</fo:table-row>
														<xsl:variable name="domicilio" select="*[local-name()='domicilio']"/>
														<xsl:if test="$domicilio">
																<fo:table-row>
																	<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="4">
																		<fo:block text-align="left" margin="0pt">
																			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																				<fo:inline font-weight="bold">
																					<xsl:text>Domicilio: </xsl:text>
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
																					<fo:inline font-weight="bold">
																						<xsl:text>Calle:</xsl:text>
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
																						<xsl:value-of select="$domicilio/*[local-name()='calle']"/>
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
																						<xsl:text>Cod Postal:</xsl:text>
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
																	</fo:table-row>
																	<fo:table-row>
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
																					<fo:inline font-style="normal">
																						<xsl:value-of select="$domicilio/*[local-name()='provincia']/*[local-name()='descripcion']"/>
																					</fo:inline>
																				</fo:block>
																			</fo:block>
																		</fo:table-cell>
																	</fo:table-row>
															</xsl:if>
															
														<xsl:variable name="domicilioIne" select="*[local-name()='domicilioIne']"/>
														<xsl:if test="$domicilioIne">
																<fo:table-row>
																	<fo:table-cell padding="2pt" display-align="center"  number-columns-spanned="4">
																		<fo:block text-align="left" margin="0pt">
																			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																				<fo:inline font-weight="bold">
																					<xsl:text>Domicilio INE: </xsl:text>
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
																					<xsl:text>Dirección:</xsl:text>
																					<fo:leader leader-pattern="space"/>
																				</fo:inline>
																			</fo:block>
																		</fo:block>
																	</fo:table-cell>
																	<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
																		<fo:block text-align="left" margin="0pt">
																			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																				<fo:inline font-style="normal">
																					<xsl:value-of select="$domicilioIne/*[local-name()='tipoVia']/text()"/>
																					<xsl:text> </xsl:text>
																					<xsl:value-of select="$domicilioIne/*[local-name()='via']/text()"/>
																					<xsl:if test="$domicilioIne/*[local-name()='numeroVia']/text() != ''">
																						<xsl:text>, Nº </xsl:text>
																						<xsl:value-of select="$domicilioIne/*[local-name()='numeroVia']/text()"/>
																					</xsl:if>
																					<xsl:if test="$domicilioIne/*[local-name()='portal']/text() != ''">
																						<xsl:text>, Portal </xsl:text>
																						<xsl:value-of select="$domicilioIne/*[local-name()='portal']/text()"/>
																					</xsl:if>
																					<xsl:if test="$domicilioIne/*[local-name()='bloque']/text() != ''">
																						<xsl:text>, Bloque </xsl:text>
																						<xsl:value-of select="$domicilioIne/*[local-name()='bloque']/text()"/>
																					</xsl:if>
																					<xsl:if test="$domicilioIne/*[local-name()='escalera']/text() != ''">
																						<xsl:text>, Escalera </xsl:text>
																						<xsl:value-of select="$domicilioIne/*[local-name()='escalera']/text()"/>
																					</xsl:if>
																					<xsl:if test="$domicilioIne/*[local-name()='planta']/text() != ''">
																						<xsl:text>, Planta </xsl:text>
																						<xsl:value-of select="$domicilioIne/*[local-name()='planta']/text()"/>
																					</xsl:if>
																					<xsl:if test="$domicilioIne/*[local-name()='puerta']/text() != ''">
																						<xsl:text>, Puerta </xsl:text>
																						<xsl:value-of select="$domicilioIne/*[local-name()='puerta']/text()"/>
																					</xsl:if>
																					<xsl:if test="$domicilioIne/*[local-name()='puerta']/text() != '0'">
																						<xsl:text>, Km </xsl:text>
																						<xsl:value-of select="$domicilioIne/*[local-name()='km']/text()"/>
																					</xsl:if>
																					<xsl:if test="$domicilioIne/*[local-name()='puerta']/text() != '0'">
																						<xsl:text>, Hm </xsl:text>
																						<xsl:value-of select="$domicilioIne/*[local-name()='hm']/text()"/>
																					</xsl:if>
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
																					<xsl:text>Cod Postal:</xsl:text>
																					<fo:leader leader-pattern="space"/>
																				</fo:inline>
																			</fo:block>
																		</fo:block>
																	</fo:table-cell>
																	<fo:table-cell padding="2pt" display-align="center">
																		<fo:block text-align="left" margin="0pt">
																			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																				<fo:inline font-style="normal">
																					<xsl:value-of select="$domicilioIne/*[local-name()='codPostal']"/>
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
																					<xsl:value-of select="$domicilioIne/*[local-name()='pueblo']"/>
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
																					<xsl:value-of select="$domicilioIne/*[local-name()='municipio']"/>
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
																				<fo:inline font-style="normal">
																					<xsl:value-of select="$domicilioIne/*[local-name()='provincia']/*[local-name()='descripcion']"/>
																				</fo:inline>
																			</fo:block>
																		</fo:block>
																	</fo:table-cell>
																</fo:table-row>
														</xsl:if>
													</fo:table-body>
												</fo:table>
											</xsl:for-each>
										</fo:block>
									</xsl:if>							
				
				
				
						<xsl:variable name="listaTutores" select="$datosResponsables/*[local-name()='listaTutores']"/>
									<xsl:if test="$listaTutores">
										<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
											<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
												<fo:table-body start-indent="0pt">
													<fo:table-row>
														<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
															<fo:block>
																<fo:inline font-weight="bold" display-align="after">
																	<xsl:text>Tutores</xsl:text>
																</fo:inline>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>
										</fo:block>
										<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
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
																		<xsl:text>Documentación: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Filiación: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Trámite: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Trámite: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<xsl:for-each select="$listaTutores/*[local-name()='tutor']">
															<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-style="normal">
																				<xsl:value-of select="*[local-name()='idDocumento']"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-style="normal">
																				<xsl:value-of select="*[local-name()='filiacion']"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-style="normal">
																				<xsl:value-of select="*[local-name()='tramite']"/>
																			</fo:inline>
																		</fo:block>
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																	<fo:block text-align="left" margin="0pt">
																		<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																			<fo:inline font-style="normal"> 
																				<xsl:choose>
																						<xsl:when test="string(*[local-name()='fechaTramite']) = ''"></xsl:when>
																						<xsl:otherwise>
																							<xsl:value-of select="concat(substring(string(*[local-name()='fechaTramite']),9,2),'/',substring(string(*[local-name()='fechaTramite']),6,2),'/',substring(string(*[local-name()='fechaTramite']),1,4))"/>
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
				
				
						
						</xsl:if>
				
				
				<xsl:variable name="tramitesBajas" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosTramites']/*[local-name()='listaBajas']"/>
				<xsl:variable name="tramitesDuplicados" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosTramites']/*[local-name()='listaDuplicados']"/>
				<xsl:variable name="tramitesRematriculaciones" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosTramites']/*[local-name()='listaRematriculaciones']"/>
				<xsl:variable name="tramitesProrrogas" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosTramites']/*[local-name()='listaProrrogas']"/> 
				<xsl:variable name="tramitesTransferencias" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosTramites']/*[local-name()='listaTransferencias']"/> 
				<xsl:variable name="tramitesMatriculacionTemp" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosTramites']/*[local-name()='matriculacionTemporal']"/> 
				<xsl:if test="$tramitesDuplicados or $tramitesRematriculaciones or $tramitesProrrogas or $tramitesBajas or $tramitesMatriculacionTemp or $tramitesTransferencias">
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
					<xsl:if test="$tramitesBajas">	
								<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-body start-indent="0pt">
										<fo:table-row>
											<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
												<fo:block>
													<fo:inline font-weight="bold" display-align="after">
														<xsl:text>Bajas</xsl:text>
													</fo:inline>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
									<fo:table-column column-width="proportional-column-width(1)"/>
									<fo:table-column column-width="proportional-column-width(1)"/>
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
															<xsl:text>Tipo Baja</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Causa baja</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Fecha Inicio</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Fecha Fin</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Jefatura</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell padding="2pt" display-align="center">
												<fo:block text-align="left" margin="0pt">
													<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
														<fo:inline font-weight="bold">
															<xsl:text>Sucursal</xsl:text>
															<fo:leader leader-pattern="space"/>
														</fo:inline>
													</fo:block>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<xsl:for-each select="$tramitesBajas/*[local-name()='baja']">
											<fo:table-row>
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																<xsl:value-of select="*[local-name()='tipoBaja']/*[local-name()='descripcion']"/>
															</fo:inline>
														</fo:block>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell padding="2pt" display-align="center">
													<fo:block text-align="left" margin="0pt">
														<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
															<fo:inline font-style="normal">
																	<xsl:value-of select="*[local-name()='causaBaja']/*[local-name()='descripcion']"/>
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
										</xsl:for-each>
									</fo:table-body>
								</fo:table>
							</fo:block>
	
					</xsl:if>		
						
					<xsl:if test="$tramitesDuplicados/*[local-name()='duplicado']">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Duplicados</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										
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
																			<xsl:text>Razón Duplicado</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Duplicado</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Jefatura</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Sucursal</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
												</fo:table-row>
												<xsl:for-each select="$tramitesDuplicados/*[local-name()='duplicado']">
												<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='razonDuplicado']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							 <xsl:choose>
																								<xsl:when test="string(*[local-name()='fechaDuplicado']) = ''"></xsl:when>
																								<xsl:otherwise>
																									<xsl:value-of select="concat(substring(string(*[local-name()='fechaDuplicado']),9,2),'/',substring(string(*[local-name()='fechaDuplicado']),6,2),'/',substring(string(*[local-name()='fechaDuplicado']),1,4))"/>
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
												</xsl:for-each> 
								</fo:table-body>
							</fo:table>
							
						</fo:block>
					</xsl:if>	
			
					<xsl:if test="$tramitesProrrogas/*[local-name()='prorroga']">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Prórrogas</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"  border="1pt solid black">
											<fo:table-column column-width="proportional-column-width(1)"/>
											<fo:table-column column-width="proportional-column-width(1)"/>	 
											<fo:table-body start-indent="0pt">
												<!-- Fila 1 Hijo -->
										
												<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Fecha Inicio</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Inicio</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														 
												</fo:table-row>
												<xsl:for-each select="$tramitesProrrogas/*[local-name()='prorroga']">
												<fo:table-row>
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
			
			
					<xsl:if test="$tramitesRematriculaciones/*[local-name()='rematriculacion']">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Rematriculaciones</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"  border="1pt solid black">
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
																		<xsl:text>Razón: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell> 
												</fo:table-row>
												<xsl:for-each select="$tramitesRematriculaciones/*[local-name()='rematriculacion']">
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
																							<xsl:choose>
																									<xsl:when test="string(*[local-name()='fechaRematriculacion']) = ''"></xsl:when>
																									<xsl:otherwise>
																										<xsl:value-of select="concat(substring(string(*[local-name()='fechaRematriculacion']),9,2),'/',substring(string(*[local-name()='fechaRematriculacion']),6,2),'/',substring(string(*[local-name()='fechaRematriculacion']),1,4))"/>
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
																							<xsl:value-of select="*[local-name()='razonRematriculacion']/*[local-name()='descripcion']"/>
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
			
		
					<xsl:if test="$tramitesTransferencias/*[local-name()='transferencia']">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Transferencias</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										
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
																			<xsl:text>Documentación Anterior: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Transferencia: </xsl:text>
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
												<xsl:for-each select="$tramitesTransferencias/*[local-name()='transferencia']">
												<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='idDocumentoAnterior']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal"> 
																							<xsl:choose>
																									<xsl:when test="string(*[local-name()='fechaTransferencia']) = ''"></xsl:when>
																									<xsl:otherwise>
																										<xsl:value-of select="concat(substring(string(*[local-name()='fechaTransferencia']),9,2),'/',substring(string(*[local-name()='fechaTransferencia']),6,2),'/',substring(string(*[local-name()='fechaTransferencia']),1,4))"/>
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
												</xsl:for-each> 
								</fo:table-body>
							</fo:table>
							
						</fo:block>
					</xsl:if>	

			
					<xsl:if test="$tramitesMatriculacionTemp/*[local-name()='matriculaAnterior'] or $tramitesMatriculacionTemp/*[local-name()='fecha']">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Matriculación Temporal</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"  border="1pt solid black">
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
																		<xsl:text>Matrícula Anterior: </xsl:text>
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
																							<xsl:value-of select="$tramitesMatriculacionTemp/*[local-name()='anotacion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							 <xsl:choose>
																									<xsl:when test="string($tramitesMatriculacionTemp/*[local-name()='fecha']) = ''"></xsl:when>
																									<xsl:otherwise>
																										<xsl:value-of select="concat(substring(string($tramitesMatriculacionTemp/*[local-name()='fecha']),9,2),'/',substring(string($tramitesMatriculacionTemp/*[local-name()='fecha']),6,2),'/',substring(string($tramitesMatriculacionTemp/*[local-name()='fecha']),1,4))"/>
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
																							<xsl:value-of select="$tramitesMatriculacionTemp/*[local-name()='matriculaAnterior']"/>
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
				
				<xsl:variable name="datosAvisos" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosAdministrativos']/*[local-name()='listaAvisos']"/>
				<xsl:variable name="datosDenegatorias" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosAdministrativos']/*[local-name()='listaDenegatorias']"/>
				<xsl:variable name="datosEmbargos" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosAdministrativos']/*[local-name()='listaEmbargos']"/>
				<xsl:variable name="datosImpagos" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosAdministrativos']/*[local-name()='listaImpagos']"/> 
				<xsl:variable name="datosLimitaciones" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosAdministrativos']/*[local-name()='listaLimitaciones']"/> 
				<xsl:variable name="datosPrecintos" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosAdministrativos']/*[local-name()='listaPrecintos']"/> 
				<xsl:if test="$datosAvisos or $datosDenegatorias or $datosEmbargos or $datosImpagos or $datosLimitaciones or $datosPrecintos">
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos Administrativos</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
					</fo:block>	
					<xsl:if test="$datosAvisos">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos Avisos</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										
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
																			<xsl:text>Anotación</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Jefatura</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Sucursal</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
												</fo:table-row>
												<xsl:for-each select="$datosAvisos/*[local-name()='aviso']">
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
												</xsl:for-each> 
								</fo:table-body>
							</fo:table>
							
						</fo:block>
					</xsl:if>
					
					<xsl:if test="$datosDenegatorias">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos Denegatorias</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										
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
																			<xsl:text>Anotación</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Jefatura</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Sucursal</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
												</fo:table-row>
												<xsl:for-each select="$datosDenegatorias/*[local-name()='denegatoria']">
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
												</xsl:for-each> 
								</fo:table-body>
							</fo:table>
							
						</fo:block>
					</xsl:if>
		
					<xsl:if test="$datosEmbargos">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos Embargos</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										
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
																			<xsl:text>Autoridad</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Expediente</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Materialización</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Trámite</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
												</fo:table-row>
												<xsl:for-each select="$datosEmbargos/*[local-name()='embargo']">
												<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='accionJudicial']/*[local-name()='autoridad']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='accionJudicial']/*[local-name()='expediente']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																					<xsl:value-of select="*[local-name()='accionJudicial']/*[local-name()='fechaMaterializacion']"/>
																					<xsl:choose>
																							<xsl:when test="string(*[local-name()='accionJudicial']/*[local-name()='fechaMaterializacion']) = ''"></xsl:when>
																							<xsl:otherwise>
																								<xsl:value-of select="concat(substring(string(*[local-name()='accionJudicial']/*[local-name()='fechaMaterializacion']),9,2),'/',substring(string(*[local-name()='accionJudicial']/*[local-name()='fechaMaterializacion']),6,2),'/',substring(string(*[local-name()='accionJudicial']/*[local-name()='fechaMaterializacion']),1,4))"/>
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
																								<xsl:when test="string(*[local-name()='accionJudicial']/*[local-name()='fechaTramite']) = ''"></xsl:when>
																								<xsl:otherwise>
																									<xsl:value-of select="concat(substring(string(*[local-name()='accionJudicial']/*[local-name()='fechaTramite']),9,2),'/',substring(string(*[local-name()='accionJudicial']/*[local-name()='fechaTramite']),6,2),'/',substring(string(*[local-name()='accionJudicial']/*[local-name()='fechaTramite']),1,4))"/>
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

				<xsl:if test="$datosImpagos">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos Impagos</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										
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
																			<xsl:text>Año impago</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Doi</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Municipio</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
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
												</fo:table-row>
												<xsl:for-each select="$datosImpagos/*[local-name()='impago']">
												<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='anyoImpago']"/>
																						
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='doi']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='municipio']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='provincia']/*[local-name()='descripcion']"/>
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
			
			
					<xsl:if test="$datosLimitaciones">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos Limitaciones</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"  border="1pt solid black">
											<fo:table-column column-width="proportional-column-width(0.5)"/>
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
																			<xsl:text>Anotación</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Domicilio Financiera</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Registro</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Tipo Limitación</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
												</fo:table-row>
												<xsl:for-each select="$datosLimitaciones/*[local-name()='limitacionDisposicion']">
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
																								<xsl:value-of select="*[local-name()='financiera_domicilio']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='registro']"/>
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
																						<xsl:value-of select="*[local-name()='tipoLimitacion']/*[local-name()='descripcion']"/>
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
	
	
	<xsl:if test="$datosPrecintos">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos Precintos</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										
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
																			<xsl:text>Autoridad: </xsl:text>
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
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Materialización:</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Trámite: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
												</fo:table-row>
												<xsl:for-each select="$datosPrecintos/*[local-name()='precinto']">
												<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='accionJudicial']/*[local-name()='autoridad']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='accionJudicial']/*[local-name()='expediente']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							 <xsl:choose>
																								<xsl:when test="string(*[local-name()='accionJudicial']/*[local-name()='fechaMaterializacion']) = ''"></xsl:when>
																								<xsl:otherwise>
																									<xsl:value-of select="concat(substring(string(*[local-name()='accionJudicial']/*[local-name()='fechaMaterializacion']),9,2),'/',substring(string(*[local-name()='accionJudicial']/*[local-name()='fechaMaterializacion']),6,2),'/',substring(string(*[local-name()='accionJudicial']/*[local-name()='fechaMaterializacion']),1,4))"/>
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
																								<xsl:when test="string(*[local-name()='accionJudicial']/*[local-name()='fechaTramite']) = ''"></xsl:when>
																								<xsl:otherwise>
																									<xsl:value-of select="concat(substring(string(*[local-name()='accionJudicial']/*[local-name()='fechaTramite']),9,2),'/',substring(string(*[local-name()='accionJudicial']/*[local-name()='fechaTramite']),6,2),'/',substring(string(*[local-name()='accionJudicial']/*[local-name()='fechaTramite']),1,4))"/>
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
		
			</xsl:if>	
				<xsl:variable name="datosItv" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosITVReformas']"/> 
				<xsl:variable name="datosItvLista" select="$datosItv/*[local-name()='listaItvs']"/> 
				<xsl:variable name="datosItvReformas" select="$datosVehiculo/*[local-name()='listaReformas']"/> 
				<xsl:if test="$datosItvLista or $datosItvReformas">
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos ITV</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
					</fo:block>		
					<xsl:if test="$datosItvLista">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Listado de ITVs</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
									<xsl:for-each select="$datosItvLista/*[local-name()='itv']">	
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"  border="1pt solid black">
											<fo:table-column column-width="proportional-column-width(0.5)"/>
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
																			<xsl:text>Anotación</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Cuenta Horas</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Estación</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Caducidad</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Fin Anterior</xsl:text>
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
																								<xsl:value-of select="*[local-name()='cuentaHoras']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='estacion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																								 <xsl:choose>
																										<xsl:when test="string(*[local-name()='fechaCaducidad']) = ''"></xsl:when>
																										<xsl:otherwise>
																											<xsl:value-of select="concat(substring(string(*[local-name()='fechaCaducidad']),9,2),'/',substring(string(*[local-name()='fechaCaducidad']),6,2),'/',substring(string(*[local-name()='fechaCaducidad']),1,4))"/>
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
																										<xsl:when test="string(*[local-name()='fechaFinAnterior']) = ''"></xsl:when>
																										<xsl:otherwise>
																											<xsl:value-of select="concat(substring(string(*[local-name()='fechaFinAnterior']),9,2),'/',substring(string(*[local-name()='fechaFinAnterior']),6,2),'/',substring(string(*[local-name()='fechaFinAnterior']),1,4))"/>
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
																			<xsl:text>Kilometraje</xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Motivo ITV</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
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
														<fo:table-cell padding="2pt" display-align="center" >
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Resultado ITV</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														 <fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha ITV</xsl:text>
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
																							<xsl:value-of select="*[local-name()='kilometraje']"/>
																						
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='motivoItv']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='provincia']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center" >
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																						<xsl:value-of select="*[local-name()='resultadoItv']/*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell>
															 	<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																								<xsl:choose>
																										<xsl:when test="string(*[local-name()='fechaItv']) = ''"></xsl:when>
																										<xsl:otherwise>
																											<xsl:value-of select="concat(substring(string(*[local-name()='fechaItv']),9,2),'/',substring(string(*[local-name()='fechaItv']),6,2),'/',substring(string(*[local-name()='fechaItv']),1,4))"/>
																										</xsl:otherwise>
																								</xsl:choose> 
																								
																						</fo:inline>
																					</fo:block>
																				</fo:block>
														</fo:table-cell>	
												</fo:table-row> 		 
											<xsl:if test="$datosItvLista/*[local-name()='resultadoItv']">	
												<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
																						<fo:block text-align="left" margin="0pt">
																							<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																								<fo:inline font-style="normal">
																										 <fo:leader leader-pattern="space"/>
																								</fo:inline>
																							</fo:block>
																						</fo:block>
														</fo:table-cell>	
														<fo:table-cell padding="2pt" display-align="center">
																						<fo:block text-align="left" margin="0pt">
																							<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																								<fo:inline font-style="normal">
																									<fo:leader leader-pattern="space"/>
																								</fo:inline>
																							</fo:block>
																						</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Defectos ITV: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>	
												</fo:table-row> 
												<xsl:for-each select="$datosItvLista/*[local-name()='resultadoItv']">	
														<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																						<fo:block text-align="left" margin="0pt">
																							<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																								<fo:inline font-style="normal">
																										 <fo:leader leader-pattern="space"/>
																								</fo:inline>
																							</fo:block>
																						</fo:block>
																</fo:table-cell>	
																<fo:table-cell padding="2pt" display-align="center">
																						<fo:block text-align="left" margin="0pt">
																							<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																								<fo:inline font-style="normal">
																									<fo:leader leader-pattern="space"/>
																								</fo:inline>
																							</fo:block>
																						</fo:block>
																</fo:table-cell>
																<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='descripcion']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																</fo:table-cell> 
															 
												</fo:table-row> 		
											</xsl:for-each>
											</xsl:if>
											
											   <!-- ***********************************************  -->
											   <xsl:if test="*[local-name()='listaDefectosItv']">	
												<fo:table-row> 
														<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Defectos ITV: </xsl:text>
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
																				<xsl:text>Gravedad:</xsl:text>
																		</fo:inline>
																	</fo:block>
																</fo:block>
														</fo:table-cell>	
														<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-style="normal">
																			<xsl:text>Tipo:</xsl:text>
																		</fo:inline>
																	</fo:block>
																</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
														</fo:table-cell>	
												</fo:table-row> 
												
												<xsl:for-each select="*[local-name()='listaDefectosItv']/*[local-name()='defectoItv']">	
														<fo:table-row>
																<fo:table-cell padding="2pt" display-align="center">
																						<fo:block text-align="left" margin="0pt">
																							<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																								<fo:inline font-style="normal">
																										<xsl:value-of select="*[local-name()='gravedadDefectoItv']/*[local-name()='codigo']"/> 
																										 <xsl:if test="*[local-name()='gravedadDefectoItv']/*[local-name()='descripcion']">
																												<xsl:text>  -  </xsl:text>
																												<xsl:value-of select="*[local-name()='gravedadDefectoItv']/*[local-name()='descripcion']"/>
																										</xsl:if>
																								</fo:inline>
																							</fo:block>
																						</fo:block>
																</fo:table-cell>	
																<fo:table-cell padding="2pt" display-align="center">
																						<fo:block text-align="left" margin="0pt">
																							<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																								<fo:inline font-style="normal">
																										<xsl:value-of select="*[local-name()='tipoDefectoItv']/*[local-name()='codigo']"/>
																										 <xsl:if test="*[local-name()='tipoDefectoItv']/*[local-name()='descripcion']">
																												<xsl:text>  -  </xsl:text>
																												<xsl:value-of select="*[local-name()='tipoDefectoItv']/*[local-name()='descripcion']"/>
																										</xsl:if>

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
											</xsl:for-each>
											</xsl:if>
											
								</fo:table-body>
							</fo:table>
							</xsl:for-each> 
						</fo:block>
					</xsl:if>
			
					<xsl:if test="$datosItvReformas">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Reformas</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								 
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"  border="1pt solid black">
											<fo:table-column column-width="proportional-column-width(1)"/>
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
																			<xsl:text>Estación: </xsl:text>
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
																			<xsl:text>Fecha Validez: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Nuevo Permiso: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Provincia: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
													</fo:table-row>
												 <xsl:for-each select="$datosItvLista/*[local-name()='itv']">
														 <fo:table-row>
																	<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='estacionReforma']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																	</fo:table-cell>
																	<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal"> 
																							<xsl:choose>
		<xsl:when test="string(*[local-name()='fechaReforma']) = ''"></xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="concat(substring(string(*[local-name()='fechaReforma']),9,2),'/',substring(string(*[local-name()='fechaReforma']),6,2),'/',substring(string(*[local-name()='fechaReforma']),1,4))"/>
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
		<xsl:when test="string(*[local-name()='fechaValidez']) = ''"></xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="concat(substring(string(*[local-name()='fechaValidez']),9,2),'/',substring(string(*[local-name()='fechaValidez']),6,2),'/',substring(string(*[local-name()='fechaValidez']),1,4))"/>
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
																							<xsl:value-of select="*[local-name()='nuevoPermiso']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																	</fo:table-cell>
																	<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='provinciaReforma']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																	</fo:table-cell>
														</fo:table-row>
														 <xsl:if test="$datosItvLista/*[local-name()='listaMotivosReforma']/*[local-name()='motivoReforma']">
															 <fo:table-row>
																		<fo:table-cell padding="2pt" display-align="center">
																			<fo:block text-align="left" margin="0pt">
																				<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																					<fo:inline font-weight="bold">
																						<fo:leader leader-pattern="space"/>
																					</fo:inline>
																				</fo:block>
																			</fo:block>
																	</fo:table-cell>
																	<fo:table-cell padding="2pt" display-align="center">
																		<fo:block text-align="left" margin="0pt">
																			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																				<fo:inline font-weight="bold">
																					<fo:leader leader-pattern="space"/>
																				</fo:inline>
																			</fo:block>
																		</fo:block>
																	</fo:table-cell>
																	<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="3">
																		<fo:block text-align="left" margin="0pt">
																			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																				<fo:inline font-weight="bold">
																					<xsl:text>Motivos Reforma: </xsl:text>
																					<fo:leader leader-pattern="space"/>
																				</fo:inline>
																			</fo:block>
																		</fo:block>
																	</fo:table-cell>
															</fo:table-row>
															 <xsl:for-each select="$datosItvLista/*[local-name()='motivoReforma']">
																 <fo:table-row>
																		 <fo:table-cell padding="2pt" display-align="center">
																			<fo:block text-align="left" margin="0pt">
																				<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																					<fo:inline font-weight="bold">
																						<fo:leader leader-pattern="space"/>
																					</fo:inline>
																				</fo:block>
																			</fo:block>
																	</fo:table-cell>
																	<fo:table-cell padding="2pt" display-align="center">
																		<fo:block text-align="left" margin="0pt">
																			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																				<fo:inline font-weight="bold">
																					<fo:leader leader-pattern="space"/>
																				</fo:inline>
																			</fo:block>
																		</fo:block>
																	</fo:table-cell>
																	<fo:table-cell padding="2pt" display-align="center">
																		<fo:block text-align="left" margin="0pt">
																			<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																				<fo:inline font-weight="bold">
																					<fo:leader leader-pattern="space"/>
																				</fo:inline>
																			</fo:block>
																		</fo:block>
																	</fo:table-cell>
																	<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='datosReforma']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																	</fo:table-cell>
																	<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																					<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																						<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='observacionesReforma']"/>
																						</fo:inline>
																					</fo:block>
																				</fo:block>
																	</fo:table-cell>
																</fo:table-row>
															  </xsl:for-each>
														 </xsl:if>
												 </xsl:for-each>
								</fo:table-body>
							</fo:table>
						</fo:block>
					</xsl:if>
				</xsl:if>			
				
				<xsl:variable name="datosSeguros" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosSeguros']/*[local-name()='listaSeguros']"/> 
				<xsl:if test="$datosSeguros">
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos Seguro</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
					</fo:block>				 
								<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"  border="1pt solid black">
											<fo:table-column column-width="proportional-column-width(0.5)"/>
											<fo:table-column column-width="proportional-column-width(1.5)"/>	
											<fo:table-column column-width="proportional-column-width(1)"/>	
											<fo:table-column column-width="proportional-column-width(1)"/>	
											<fo:table-body start-indent="0pt">
												<!-- Fila 1 Hijo -->
										
												<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Contrato: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
												 		<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Entidad: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Inicio:</xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell padding="2pt" display-align="center">
															<fo:block text-align="left" margin="0pt">
																<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																	<fo:inline font-weight="bold">
																		<xsl:text>Fecha Fin: </xsl:text>
																		<fo:leader leader-pattern="space"/>
																	</fo:inline>
																</fo:block>
															</fo:block>
														</fo:table-cell>
												</fo:table-row>
												<xsl:for-each select="$datosSeguros/*[local-name()='seguro']">
													<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='contratoSeguro']/*[local-name()='descripcion']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
																	</fo:table-cell>	 
																	<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='entidad']/*[local-name()='descripcion']"/>
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
						 
				<xsl:variable name="vehiculos" select="//*[local-name()='DatosVehiculo']/*[local-name()='listaVehiculos']"/>
				<xsl:if test="$vehiculos/*[local-name()='matricula']">				
									<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
										<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
											<fo:table-body start-indent="0pt">
												<fo:table-row>
													<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
														<fo:block>
															<fo:inline font-weight="bold" display-align="after">
																<xsl:text>Matrícula</xsl:text>
															</fo:inline>
														</fo:block>
													</fo:table-cell>
												</fo:table-row>
											</fo:table-body>
										</fo:table>
									</fo:block>
									<fo:block text-indent="3mm" margin-top="2pt" margin-left="15pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
															 <fo:inline  display-align="after">
																			<xsl:value-of select="$vehiculos/*[local-name()='matricula']"/> 
															</fo:inline>
													 
									</fo:block>									
									
					</xsl:if>				 
					
				<xsl:variable name="seguridadElementos" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosSeguridad']/*[local-name()='listaElementosSeguridad']"/> 
				<xsl:variable name="seguridadNcap" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosSeguridad']/*[local-name()='ncap']"/> 
				<xsl:variable name="seguridadRescate" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosSeguridad']/*[local-name()='hojaRescate']"/> 
				<xsl:if test="$seguridadElementos or $seguridadNcap or $seguridadRescate">
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos Seguridad</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
					</fo:block>	
					<xsl:if test="$seguridadElementos">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Elementos de Seguridad</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								 		<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"  border="1pt solid black">
											<fo:table-column column-width="proportional-column-width(1)"/>
											<fo:table-column column-width="proportional-column-width(1)"/>	
											<fo:table-column column-width="proportional-column-width(1)"/>	 
											<fo:table-body start-indent="0pt">
											<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Nombre: </xsl:text>
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
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Valor: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>			
											</fo:table-row>
											<xsl:for-each select="$seguridadElementos/*[local-name()='elementoSeguridad']">
													<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='nombre']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>	
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='tipo']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='valor']"/>
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
					
					
					<xsl:if test="$seguridadNcap">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Seguridad NCAP</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								 		<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"  border="1pt solid black">
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
																			<xsl:text>Año Ensayo: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>	
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Adultos: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>		
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Menores: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>			
																<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Peatones: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Seguridad: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>			
																<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Global: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>			
											</fo:table-row>
											<xsl:for-each select="$seguridadNcap">
													<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='anioEnsayo']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>	
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='adultos']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='menores']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell> 
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='peatones']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell> 
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='seguridad']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell> 
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																							<xsl:value-of select="*[local-name()='global']"/>
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
				
					<xsl:if test="$seguridadRescate">	
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Hojas Rescate</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
							<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
								 		<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"  border="1pt solid black">
											<fo:table-column column-width="proportional-column-width(1)"/> 
											<fo:table-body start-indent="0pt">
												<xsl:for-each select="$seguridadRescate">
													<fo:table-row>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='strHojaRescate']"/>
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
				
				
				<xsl:variable name="tallerIncidencias" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosLibroTaller']/*[local-name()='listaDetalleIncidencias']/*[local-name()='detalleIncidencia']"/> 
				<xsl:variable name="tallerIncidenciaSeleccionada" select="//*[local-name()='DatosVehiculo']/*[local-name()='datosLibroTaller']/*[local-name()='detalleIncidenciaSeleccionada']"/> 
				<xsl:if test="$tallerIncidencias or $tallerIncidenciaSeleccionada">
						<fo:block text-indent="3mm" margin-top="2pt" margin-left="2pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
							<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
								<fo:table-body start-indent="0pt">
									<fo:table-row>
										<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
											<fo:block>
												<fo:inline font-weight="bold" display-align="after">
													<xsl:text>Datos Libro Taller</xsl:text>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
					</fo:block>	
						<xsl:if test="$tallerIncidencias">	
								<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
													<fo:block>
														<fo:inline font-weight="bold" display-align="after">
															<xsl:text>Incidencias Taller</xsl:text>
														</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:block>
								<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
									<xsl:for-each select="$tallerIncidencias">								 	
								 		<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"  border="1pt solid black">
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
																			<xsl:text>Kilómetros: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>		
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Importancias: </xsl:text>
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
																								<xsl:value-of select="*[local-name()='kms']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='importancias']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='tipo']"/>
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
																			<xsl:text>Concepto: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>	
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Descripción: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>		
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Anotador: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Fecha Legalización: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>		
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Pieza: </xsl:text>
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
																								<xsl:value-of select="*[local-name()='concepto']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='descripcion']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='anotador']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								 
																								<xsl:choose>
		<xsl:when test="string(*[local-name()='fechaLegalizacion']) = ''"></xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="concat(substring(string(*[local-name()='fechaLegalizacion']),9,2),'/',substring(string(*[local-name()='fechaLegalizacion']),6,2),'/',substring(string(*[local-name()='fechaLegalizacion']),1,4))"/>
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
																								<xsl:value-of select="*[local-name()='pieza']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>	
												</fo:table-row>
												<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Lst Incidencia Taller: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>		
												</fo:table-row>
												 <fo:table-row>
															<fo:table-cell padding="2pt" display-align="center"  >
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>  </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center"  >
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Tipo : </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>		
															<fo:table-cell padding="2pt" display-align="center">
																							<fo:block text-align="left" margin="0pt">
																								<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																									<fo:inline font-style="normal">
																										<xsl:value-of select="*[local-name()='lstIncidenciaTaller']/*[local-name()='incidenciaTaller']/*[local-name()='tipo']"/>
																									</fo:inline>
																								</fo:block>
																							</fo:block>
															</fo:table-cell>	
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Elemento : </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>		
															<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="*[local-name()='lstIncidenciaTaller']/*[local-name()='incidenciaTaller']/*[local-name()='elemento']"/>
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
				
				
					 <xsl:if test="$tallerIncidenciaSeleccionada">	
								<fo:block text-indent="3mm" margin-top="2pt" margin-left="10pt" margin-right="2pt" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
									<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm">
										<fo:table-body start-indent="0pt">
											<fo:table-row>
												<fo:table-cell padding="3pt" display-align="center" background-color="#B0C4DE">
													<fo:block>
														<fo:inline font-weight="bold" display-align="after">
															<xsl:text>Incidencia Seleccionada</xsl:text>
														</fo:inline>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:block>
								<fo:block margin-top="10pt" margin-left="10pt" margin-right="2pt" text-indent="3mm" font-family="Arial,sans-serif" font-size="10pt" space-before="5mm" space-after="4mm">
									  	
								 		<fo:table table-layout="fixed" width="100%" border-spacing="5pt" space-after="2mm"  border="1pt solid black">
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
																			<xsl:text>Kilómetros: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>		
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Importancias: </xsl:text>
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
		<xsl:when test="string($tallerIncidenciaSeleccionada/*[local-name()='fecha']) = ''"></xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="concat(substring(string($tallerIncidenciaSeleccionada/*[local-name()='fecha']),9,2),'/',substring(string($tallerIncidenciaSeleccionada/*[local-name()='fecha']),6,2),'/',substring(string($tallerIncidenciaSeleccionada/*[local-name()='fecha']),1,4))"/>
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
																								<xsl:value-of select="$tallerIncidenciaSeleccionada/*[local-name()='kms']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="$tallerIncidenciaSeleccionada/*[local-name()='importancias']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="$tallerIncidenciaSeleccionada/*[local-name()='tipo']"/>
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
																			<xsl:text>Concepto: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>	
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Descripción: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>		
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Anotador: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Fecha Legalización: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>		
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Pieza: </xsl:text>
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
																								<xsl:value-of select="$tallerIncidenciaSeleccionada/*[local-name()='concepto']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="$tallerIncidenciaSeleccionada/*[local-name()='descripcion']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="$tallerIncidenciaSeleccionada/*[local-name()='anotador']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center">
																					<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								 <xsl:choose>
		<xsl:when test="string($tallerIncidenciaSeleccionada/*[local-name()='fechaLegalizacion']) = ''"></xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="concat(substring(string($tallerIncidenciaSeleccionada/*[local-name()='fechaLegalizacion']),9,2),'/',substring(string($tallerIncidenciaSeleccionada/*[local-name()='fechaLegalizacion']),6,2),'/',substring(string($tallerIncidenciaSeleccionada/*[local-name()='fechaLegalizacion']),1,4))"/>
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
																								<xsl:value-of select="$tallerIncidenciaSeleccionada/*[local-name()='pieza']"/>
																							</fo:inline>
																						</fo:block>
																					</fo:block>
															</fo:table-cell>	
												</fo:table-row>
												<fo:table-row>
														<fo:table-cell padding="2pt" display-align="center" number-columns-spanned="5">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Lst Incidencia Taller: </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>		
												</fo:table-row>
												 <fo:table-row>
															<fo:table-cell padding="2pt" display-align="center"  >
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>  </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>
															<fo:table-cell padding="2pt" display-align="center"  >
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Tipo : </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>		
															<fo:table-cell padding="2pt" display-align="center">
																							<fo:block text-align="left" margin="0pt">
																								<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																									<fo:inline font-style="normal">
																										<xsl:value-of select="$tallerIncidenciaSeleccionada/*[local-name()='lstIncidenciaTaller']/*[local-name()='incidenciaTaller']/*[local-name()='tipo']"/>
																									</fo:inline>
																								</fo:block>
																							</fo:block>
															</fo:table-cell>	
															<fo:table-cell padding="2pt" display-align="center">
																<fo:block text-align="left" margin="0pt">
																	<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																		<fo:inline font-weight="bold">
																			<xsl:text>Elemento : </xsl:text>
																			<fo:leader leader-pattern="space"/>
																		</fo:inline>
																	</fo:block>
																</fo:block>
															</fo:table-cell>		
															<fo:table-cell padding="2pt" display-align="center">
																				<fo:block text-align="left" margin="0pt">
																						<fo:block font-size="8pt" font-family="Arial,Helvetica,sans-serif" line-height="10pt" space-after.optimum="1pt" display-align="after">
																							<fo:inline font-style="normal">
																								<xsl:value-of select="$tallerIncidenciaSeleccionada/*[local-name()='lstIncidenciaTaller']/*[local-name()='incidenciaTaller']/*[local-name()='elemento']"/>
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
				
						  
					</xsl:if>
					
					
				 	 
				</fo:block>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
