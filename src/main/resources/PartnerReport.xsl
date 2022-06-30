<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
							  xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:template match="/root">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="A4-portrait"
             			 page-height="29.7cm" page-width="21.0cm" margin="2cm">             			 
               <fo:region-body />
               <fo:region-after/>
               </fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="A4-portrait">
			
				<!-- Static content for Page number in format (Page X of N) -->
				<fo:static-content flow-name="xsl-region-after" text-align="center" font-size="small">
					 <fo:block text-align="center" font-size="small">
       					Page <fo:page-number/> of <fo:page-number-citation ref-id="my-sequence-id"/> 
       				 </fo:block>
				</fo:static-content>
								
				<fo:flow flow-name="xsl-region-body">
					<fo:block text-align="center" color="#2B77B4" font-size="26px"> 
						Partner Report
					</fo:block>
					<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
					
					<!-- Table 1: For REPORT HEADERS -->
					<fo:table border-collapse="collapse" table-layout="fixed" width="17cm" left="0cm">
						<fo:table-column column-width="4.5cm"/>
						<fo:table-column column-width="4.5cm"/>
						<fo:table-column column-width="0.5cm"/>
						<fo:table-column column-width="4.5cm"/>
						<fo:table-column column-width="3cm"/>
						<fo:table-body>
							<!-- REPORT HEADERS table VALUES -->
							<fo:table-row line-height="30pt">
								<fo:table-cell border-bottom="1px solid #B1B1B1">
									<fo:block text-align="start" font-weight="bold" font-size="small">
									Project Name: 
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1">
									<fo:block text-align="start" font-size="small" color="dimgray" >
											<xsl:value-of select="reportHeader/projectName"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>   </fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1">
									<fo:block text-align="start" font-weight="bold" font-size="small">
									Partner SPOC phone :										 
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1">
									<fo:block text-align="start" font-size="small" color="dimgray">
											<xsl:value-of select="reportHeader/partnerSPOCPhone"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							
							<fo:table-row line-height="20pt">
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center" line-height="10pt">
									<fo:block text-align="start" font-weight="bold" font-size="small">
									Name of PIA :										
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center" line-height="15pt">
									<fo:block text-align="start" font-size="small" color="dimgray">
											<xsl:value-of select="reportHeader/pia"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>   </fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-weight="bold" font-size="small" line-height="0pt">
									Partner SPOC Email ID :										
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-size="small" color="dimgray">
											<xsl:value-of select="reportHeader/partnerSPOCEmailId"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row line-height="20pt">
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-weight="bold" font-size="small">
									Partner SPOC Name :										
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-size="small" color="dimgray">
											<xsl:value-of select="reportHeader/partnerSPOCName"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					
					<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
					
					<xsl:if test="warning != ''">
						<fo:block text-align="center" font-size="large" color="#ffa500" font-family="Georgia">Warning!</fo:block>
						<fo:block text-align="center" font-size="medium" color="#ffa500" font-family="Georgia">
							<xsl:value-of select="warning"/>
						</fo:block>
					</xsl:if>
					<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
					
					<fo:block left="0cm" color="darkgoldenrod" font-size="medium" font-weight="bold">Audit Summary</fo:block>
					<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
					<!-- Audits Table -->
					<fo:table border-collapse="collapse" border="1px solid #ddd" table-layout="fixed" width="17cm">				
							<fo:table-column column-width="0.5cm"/>
							<fo:table-column column-width="3.5cm"/>
							<fo:table-column column-width="3.5cm"/>
							<fo:table-column column-width="2.5cm"/>
							<fo:table-column column-width="3.5cm"/>
							<fo:table-column column-width="3.5cm"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
										<fo:block color="white" font-size="small" font-weight="bold">#</fo:block>
									</fo:table-cell>
									<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
										<fo:block color="white" font-size="small" font-weight="bold">Field Auditor Name</fo:block>
									</fo:table-cell>
									<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
										<fo:block color="white" font-size="small" font-weight="bold">Secondary Auditor Name</fo:block>
									</fo:table-cell>
									<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
										<fo:block color="white" font-size="small" font-weight="bold">Date of Audit</fo:block>
									</fo:table-cell>
									<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
										<fo:block color="white" font-size="small" font-weight="bold">Form Name</fo:block>
									</fo:table-cell>
									<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
										<fo:block color="white" font-size="small" font-weight="bold">Training Center/Grading Form</fo:block>
									</fo:table-cell>
								</fo:table-row>	
								<xsl:for-each select="reportHeader/audits/audit">
									<fo:table-row>
										<xsl:choose>
											<xsl:when test="position() mod 2 = 1">
												<fo:table-cell  background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="position()"/>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell  background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="fieldAuditorName"/>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="secondaryAuditorName"/>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="auditDate"/>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="formName"/>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="tcName"/>
													</fo:block>
												</fo:table-cell>
											</xsl:when>
											<xsl:otherwise>
												<fo:table-cell border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="position()"/>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="fieldAuditorName"/>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="secondaryAuditorName"/>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="auditDate"/>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="formName"/>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="tcName"/>
													</fo:block>
												</fo:table-cell>
											</xsl:otherwise>
										</xsl:choose>										
									</fo:table-row>									
								</xsl:for-each>  							
							</fo:table-body>
					</fo:table>
					<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
					<fo:block left="0cm" color="darkgoldenrod" font-size="medium" font-weight="bold">Summary Report</fo:block>
					<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
					
					<!-- TABLE 2: For SUMMARY REPORT -->
					<fo:table border-collapse="collapse" border="1px solid #ddd" table-layout="fixed" width="17cm">
						<fo:table-column column-width="1cm"/>
						<fo:table-column column-width="8cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-body>
							<!-- SUMMARY REPORT table ROW HEADERS -->
							<fo:table-row>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center"  padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">#</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">PIA Name</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">Center Rating</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">Project Grading</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">Final %age</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" text-align="center" display-align="center">
									<fo:block color="white" font-size="small" font-weight="bold">Grade</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<!-- SUMMARY REPORT table ROW VALUES -->
							<fo:table-row>
								<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
									<fo:block font-size="small">
										<xsl:value-of select="position()"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
									<fo:block font-size="small">
										<xsl:value-of select="reportHeader/pia"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
									<fo:block font-size="small">
										<xsl:value-of select="number(reportBody/summaryReport/centerRating)"/>%
									</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
									<fo:block font-size="small">
										<xsl:value-of select="number(reportBody/summaryReport/projectGrading)"/>%
									</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
									<fo:block font-size="small">
										<xsl:value-of select="number(reportBody/summaryReport/finalAvg)"/>%
									</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
									<fo:block font-size="small">
										<xsl:value-of select="reportBody/summaryReport/grade"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
					<fo:block left="0cm" color="darkgoldenrod" font-size="medium" font-weight="bold">Center Rating Summary</fo:block>
					<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
					
					<!-- TABLE 3: For CENTER RATING SUMMARY -->
					<fo:table border-collapse="collapse" border="1px solid #ddd" table-layout="fixed" width="17cm">
						<fo:table-column column-width="5cm"/>
						<fo:table-column column-width="4cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-body>							
							<!-- CENTER RATING SUMMARY table ROW HEADERS -->
							<fo:table-row>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">Training Center</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">TC Address</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">TC SPOC</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">SA Score</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">FA Score</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" text-align="center" display-align="center" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">Max marks</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<!-- CENTER RATING SUMMARY table ROW VALUES -->
							
							<xsl:for-each select="reportBody/centerRatingSummaries/centerRatingSummary">
								<fo:table-row>
									<xsl:choose>
										<xsl:when test="position() mod 2 = 1">
											<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"> <xsl:value-of select="trainingCenter"/></fo:block>
											</fo:table-cell>
											<fo:table-cell background-color="#f2f2f2" text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="tcaddress"/></fo:block>
											</fo:table-cell>
											<fo:table-cell background-color="#f2f2f2" text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="tcspocname"/></fo:block>
											</fo:table-cell>
											<fo:table-cell background-color="#f2f2f2" text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="sascore"/></fo:block>
											</fo:table-cell>
											<fo:table-cell background-color="#f2f2f2" text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="fascore"/></fo:block>
											</fo:table-cell>
											<fo:table-cell background-color="#f2f2f2" text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="maxMarks"/></fo:block>
											</fo:table-cell>
										</xsl:when>
										<xsl:otherwise>
											<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"> <xsl:value-of select="trainingCenter"/></fo:block>
											</fo:table-cell>
											<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="tcaddress"/></fo:block>
											</fo:table-cell>
											<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="tcspocname"/></fo:block>
											</fo:table-cell>
											<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="sascore"/></fo:block>
											</fo:table-cell>
											<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="fascore"/></fo:block>
											</fo:table-cell>
											<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="maxMarks"/></fo:block>
											</fo:table-cell>
										</xsl:otherwise>
									</xsl:choose>									
								</fo:table-row>								
							</xsl:for-each>
							
							<!-- Row which calculates the total of SAScore, FAScore, MaxMarks
								 Condition check is also there.
								 If total row is ODD row, Background color= grey
								 If total row is EVEN row, Background color= normal
							 -->
							<fo:table-row line-height="20pt">
								<xsl:choose>
									<xsl:when test="count(reportBody/centerRatingSummaries/centerRatingSummary)+1 mod 2 = 1">
										<fo:table-cell background-color="#f2f2f2" text-align="start" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
												<fo:block font-size="small">Total</fo:block>
										</fo:table-cell>
										<fo:table-cell background-color="#f2f2f2" text-align="start" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
												<fo:block></fo:block>
										</fo:table-cell>
										<fo:table-cell background-color="#f2f2f2" text-align="start" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
												<fo:block></fo:block>
										</fo:table-cell>
										<fo:table-cell background-color="#f2f2f2" text-align="start" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
												<fo:block font-size="small"><xsl:value-of select="sum(reportBody/centerRatingSummaries/centerRatingSummary/sascore)"/></fo:block>
										</fo:table-cell>
										<fo:table-cell background-color="#f2f2f2" text-align="start" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
												<fo:block font-size="small"><xsl:value-of select="sum(reportBody/centerRatingSummaries/centerRatingSummary/fascore)"/></fo:block>
										</fo:table-cell>
										<fo:table-cell background-color="#f2f2f2" text-align="start" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
												<fo:block font-size="small"><xsl:value-of select="sum(reportBody/centerRatingSummaries/centerRatingSummary/maxMarks)"/></fo:block>
										</fo:table-cell>
									</xsl:when>
									<xsl:otherwise>
										<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small">Total</fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block></fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block></fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="sum(reportBody/centerRatingSummaries/centerRatingSummary/sascore)"/></fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="sum(reportBody/centerRatingSummaries/centerRatingSummary/fascore)"/></fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="sum(reportBody/centerRatingSummaries/centerRatingSummary/maxMarks)"/></fo:block>
										</fo:table-cell>
									</xsl:otherwise>
								</xsl:choose>																		
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
					<fo:block left="0cm" color="darkgoldenrod" font-size="medium" font-weight="bold">Partner Grading Summary</fo:block>
					<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
					
					<!-- TABLE 4: For PARTNER GRADING SUMMARY -->
					<fo:table page-break-inside="avoid" border-collapse="collapse" border="1px solid #ddd" table-layout="fixed" width="17cm">
						<fo:table-column column-width="7cm"/>
						<fo:table-column column-width="3.7cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-column column-width="2.3cm"/>
						<fo:table-body>
							<!-- PARTNER GRADING SUMMARY table ROW HEADERS -->
							<fo:table-row>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">PIA Name</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">Partner SPOC (performed by)</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">SA Score</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">FA Score</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
									<fo:block color="white" font-size="small" font-weight="bold">Max marks</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<!-- PARTNER GRADING SUMMARY table ROW VALUES -->
							<fo:table-row>
								<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px" background-color="#f2f2f2">
										<fo:block font-size="small"> <xsl:value-of select="reportHeader/pia"/></fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px" background-color="#f2f2f2">
										<fo:block font-size="small"><xsl:value-of select="reportHeader/partnerSPOCName"/></fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px" background-color="#f2f2f2">
										<fo:block font-size="small"><xsl:value-of select="reportBody/partnerGradingSummary/sascore"/></fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px" background-color="#f2f2f2">
										<fo:block font-size="small"><xsl:value-of select="reportBody/partnerGradingSummary/fascore"/></fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px" background-color="#f2f2f2">
										<fo:block font-size="small"><xsl:value-of select="reportBody/partnerGradingSummary/maxMarks"/></fo:block>
								</fo:table-cell>							
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					<fo:block id="my-sequence-id"/>
				</fo:flow>				<!-- Closing fo:flow -->
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
</xsl:stylesheet>