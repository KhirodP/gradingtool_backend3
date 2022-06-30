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
						Training Center Report
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
									Center ID :										 
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1">
									<fo:block text-align="start" font-size="small" color="dimgray">
											<xsl:value-of select="reportHeader/centerId"/>
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
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center" line-height="12pt">
									<fo:block text-align="start" font-weight="bold" font-size="small">
									Partner SPOC Phone :										
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-size="small" color="dimgray">
											<xsl:value-of select="reportHeader/partnerSPOCPhone"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row line-height="20pt">
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-weight="bold" font-size="small">
									Training Center Address :										
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center"  number-columns-spanned="4">
									<fo:block text-align="start" font-size="small" color="dimgray">
											<xsl:value-of select="reportHeader/tcaddress"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row line-height="20pt">
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center" line-height="15pt">
									<fo:block text-align="start" font-weight="bold" font-size="small">
									Training Center SPOC Name :										
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-size="small" color="dimgray">
											<xsl:value-of select="reportHeader/centerInchargeName"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>   </fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-weight="bold" font-size="small">
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
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center" line-height="15pt">
									<fo:block text-align="start" font-weight="bold" font-size="small">
									Training Center Phone :										
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-size="small" color="dimgray">
											<xsl:value-of select="reportHeader/centerInchargeContact"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>   </fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center" line-height="12pt">
									<fo:block text-align="start" font-weight="bold" font-size="small">
									Partner SPOC name :										
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-size="small" color="dimgray">
											<xsl:value-of select="reportHeader/partnerSPOCName"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row line-height="20pt">
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center" line-height="15pt">
									<fo:block text-align="start" font-weight="bold" font-size="small">
									Training Center Email ID : 										
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-size="small" color="dimgray">
											<xsl:value-of select="reportHeader/centerInchargeEmail"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>   </fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center" line-height="15pt">
									<fo:block text-align="start" font-weight="bold" font-size="small">
									Partner Project ID : 										
									</fo:block>
								</fo:table-cell>								
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-size="small" color="dimgray">
											<xsl:value-of select="reportHeader/partnerProjectId"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					
					<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
					<xsl:if test="warning != ''">
						<fo:block text-align="center" font-size="large" color="#ffa500"  font-family="Georgia">Warning!</fo:block>
						<fo:block text-align="center" font-size="medium" color="#ffa500"  font-family="Georgia">
							<xsl:value-of select="warning"/>
						</fo:block>
					</xsl:if>
					<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
					
					
					<fo:block left="0cm" color="darkgoldenrod" font-size="medium" font-weight="bold">Audit Summary</fo:block>
					<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
					<!-- Audits Table -->
					<fo:table border-collapse="collapse" border="1px solid #ddd" table-layout="fixed" width="17cm">				
							<fo:table-column column-width="0.5cm"/>
							<fo:table-column column-width="4cm"/>
							<fo:table-column column-width="4cm"/>
							<fo:table-column column-width="4cm"/>
							<fo:table-column column-width="4.5cm"/>
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
											</xsl:otherwise>
										</xsl:choose>										
									</fo:table-row>									
								</xsl:for-each>  							
							</fo:table-body>
					</fo:table>
					
					
					<!-- FORM Iteration - forEach 
						 i.e., For each form, we have to create each SUMMARY SCORE CARD table.
					-->					
		 			<xsl:for-each select="reportBody/forms/form">
						<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
						<fo:block left="0cm" color="darkgoldenrod" font-size="medium" font-weight="bold">Summary Score Card</fo:block>
						<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
						<fo:table border-collapse="collapse" border="1px solid #ddd" table-layout="fixed" width="17cm">				
							<fo:table-column column-width="8cm"/>
							<fo:table-column column-width="3cm"/>
							<fo:table-column column-width="2cm"/>
							<fo:table-column column-width="2cm"/>
							<fo:table-body>
								<!-- SUMMARY SCORE CARD table ROW HEADERS -->
								<fo:table-row>
									<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
										<fo:block></fo:block>
									</fo:table-cell>
									<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
										<fo:block color="white" font-size="small" font-weight="bold">Max Marks</fo:block>
									</fo:table-cell>
									<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
										<fo:block color="white" font-size="small" font-weight="bold">SA Score</fo:block>
									</fo:table-cell>
									<fo:table-cell background-color="#4caf50" border="1px solid #ddd" display-align="center" padding-start="5pt" padding-top="10px" padding-bottom="10px">
										<fo:block color="white" font-size="small" font-weight="bold">FA Score</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<!-- SUMMARY SCORE CARD table ROW VALUES -->
								<fo:table-row>			<!--  FORM NAME ROW -->
									<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
										<fo:block font-size="small">
											<xsl:value-of select="formName"/>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
										<fo:block font-size="small">
											<xsl:value-of select="maxMarks"/>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
										<fo:block font-size="small">
											<xsl:value-of select="number(sascore)"/>%
										</fo:block>
									</fo:table-cell>
									<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
										<fo:block font-size="small">
											<xsl:value-of select="number(fascore)"/>%
										</fo:block>
									</fo:table-cell>									
								</fo:table-row> 
								
								<!--  For the above form, these are the SECTIONS that the 
									  form containing.
								 -->
						 		<xsl:for-each select="sectionsDetails/sectionsDetail">
									<fo:table-row>
										<xsl:choose>
											<xsl:when test="position()-1 mod 2 = 1">
												<fo:table-cell  background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="sectionName"/>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="maxScore"/>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="number(sAScore)"/>%
													</fo:block>
												</fo:table-cell>
												<fo:table-cell background-color="#f2f2f2" border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="number(fAScore)"/>%
													</fo:block>
												</fo:table-cell>
											</xsl:when>
											<xsl:otherwise>
												<fo:table-cell border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="sectionName"/>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="maxScore"/>
													</fo:block>
												</fo:table-cell>
												<fo:table-cell border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="number(sAScore)"/>%
													</fo:block>
												</fo:table-cell>
												<fo:table-cell border="1px solid #ddd" display-align="center" padding="5px">
													<fo:block font-size="small">
														<xsl:value-of select="number(fAScore)"/>%
													</fo:block>
												</fo:table-cell>
											</xsl:otherwise>
										</xsl:choose>										
									</fo:table-row>									
								</xsl:for-each>  
							</fo:table-body>
						</fo:table>				<!-- End of SUMMARY SCORE CARD TABLE -->
						
						<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
						<fo:block color="darkgoldenrod" font-size="medium" font-weight="bold">Section wise Score Card</fo:block>						
						
						<!-- 
							For each SECTION, There is to be separate table for the section							
						 -->
		 				<xsl:for-each select="sectionsDetails/sectionsDetail">
							<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
							<fo:block color="#34495E" font-size="small" font-weight="bold" margin-left="0.5cm">
								<xsl:value-of select="sectionName"></xsl:value-of>
							</fo:block>	
							<fo:table border-collapse="collapse" border="1px solid #ddd" table-layout="fixed" width="16.5cm"  margin-left="0.5cm">
								<fo:table-column column-width="0.5cm"/>
								<fo:table-column column-width="2.5cm"/>
								<fo:table-column column-width="2.1cm"/>
								<fo:table-column column-width="5.4cm"/>
								<fo:table-column column-width="2cm"/>
								<fo:table-column column-width="2cm"/>
								<fo:table-column column-width="2cm"/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell background-color="#5D6D7E"  border="1px solid #ddd" display-align="center" padding-start="5pt" line-height="12pt" padding-top="10px" padding-bottom="10px">
											<fo:block color="white" font-size="9px" font-weight="bold"  margin-left="-0.5cm">#</fo:block>
										</fo:table-cell>
										<fo:table-cell background-color="#5D6D7E"  border="1px solid #ddd" display-align="center" padding-start="5pt" line-height="12pt" padding-top="10px" padding-bottom="10px">
											<fo:block color="white" font-size="9px" font-weight="bold"  margin-left="-0.5cm">Parameter</fo:block>
										</fo:table-cell>
										<fo:table-cell background-color="#5D6D7E" border="1px solid #ddd"  display-align="center" padding-start="5pt" line-height="12pt" padding-top="10px" padding-bottom="10px">
											<fo:block color="white" font-size="9px" font-weight="bold" margin-left="-0.5cm">Max.Marks</fo:block>
										</fo:table-cell>
										<fo:table-cell background-color="#5D6D7E" border="1px solid #ddd"  display-align="center" padding-start="5pt" line-height="12pt" padding-top="10px" padding-bottom="10px">
											<fo:block color="white" font-size="9px" font-weight="bold" margin-left="-0.5cm">Criteria for Grading</fo:block>
										</fo:table-cell>
										<fo:table-cell background-color="#5D6D7E" border="1px solid #ddd"  display-align="center" padding-start="5pt" line-height="12pt" padding-top="10px" padding-bottom="10px">
											<fo:block color="white" font-size="9px" font-weight="bold" margin-left="-0.5cm">Mark Obtainable</fo:block>
										</fo:table-cell>
										<fo:table-cell background-color="#5D6D7E" border="1px solid #ddd"  display-align="center" padding-start="5pt" line-height="12pt" padding-top="10px" padding-bottom="10px">
											<fo:block color="white" font-size="9px" font-weight="bold" margin-left="-0.5cm">SA Score</fo:block>
										</fo:table-cell>
										<fo:table-cell background-color="#5D6D7E" border="1px solid #ddd" display-align="center" padding-start="5pt" line-height="12pt" padding-top="10px" padding-bottom="10px">
											<fo:block color="white" font-size="9px" font-weight="bold" margin-left="-0.5cm">FA Score</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<xsl:for-each select="scorecards/scorecard">
										<fo:table-row  border="1px solid #ddd">
											<fo:table-cell  border="1px solid #ddd" display-align="center" padding="5px" background-color="#f2f2f2">
												<fo:block font-size="9px" margin-left="-0.5cm">
													<xsl:value-of select="position()"></xsl:value-of>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell  border="1px solid #ddd" display-align="center" padding="5px" background-color="#f2f2f2">
												<fo:block page-break-inside="avoid" font-size="9px" margin-left="-0.5cm">
													<xsl:value-of select="parameter"></xsl:value-of>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell  border="1px solid #ddd" display-align="center" padding="5px" background-color="#f2f2f2">
												<fo:block font-size="9px" margin-left="-0.5cm">
													<xsl:value-of select="maxMarks"></xsl:value-of>
												</fo:block>
											</fo:table-cell>
											
											<!-- Creating a nested table inside a column to handle multiple row entries -->
											
											<fo:table-cell border="1px solid #ddd">
												<fo:table border-collapse="collapse" width="100%" margin-left="-0.5cm" border="none" table-layout="fixed">
													<fo:table-column column-width="5.4cm"/>
													<fo:table-column column-width="1.9cm"/>
													<fo:table-body>
														<xsl:for-each select="optionsDetails/optionsDetail">
															<fo:table-row>
																<xsl:choose>
																	<xsl:when test="position() mod 2 = 1">															
																		<fo:table-cell border-bottom="1px solid #ddd" background-color="#f2f2f2" padding-left="3px" padding-right="3px">
																			<fo:block font-size="9px" text-align="start">
																				<xsl:value-of select="optionValue"></xsl:value-of>
																			</fo:block>
																		</fo:table-cell>
																		<fo:table-cell display-align="center" border-bottom="1px solid #ddd" background-color="#f2f2f2" padding-left="3px" padding-right="3px">
																			<fo:block font-size="9px">
																				<xsl:value-of select="optionWeightage"></xsl:value-of>
																			</fo:block>
																		</fo:table-cell>
																	</xsl:when>
																	<xsl:otherwise>
																		<fo:table-cell border-bottom="1px solid #ddd" padding-left="3px" padding-right="3px">
																			<fo:block font-size="9px" text-align="start">
																				<xsl:value-of select="optionValue"></xsl:value-of>
																			</fo:block>
																		</fo:table-cell>
																		<fo:table-cell display-align="center" border-bottom="1px solid #ddd" padding-left="3px" padding-right="3px">
																			<fo:block font-size="9px">
																				<xsl:value-of select="optionWeightage"></xsl:value-of>
																			</fo:block>
																		</fo:table-cell>
																	</xsl:otherwise>
																</xsl:choose>
															</fo:table-row>
														</xsl:for-each>
													</fo:table-body>													
												</fo:table>
											</fo:table-cell>  
										  	<fo:table-cell><fo:block/></fo:table-cell>
										  	
											<fo:table-cell border="1px solid #ddd" display-align="center" background-color="#f2f2f2">
												<fo:block font-size="9px" margin-left="-0.5cm" text-align="center">
													<xsl:value-of select="sAScore"></xsl:value-of>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell border="1px solid #ddd" display-align="center" background-color="#f2f2f2">
												<fo:block font-size="9px" margin-left="-0.5cm" text-align="center">
													<xsl:value-of select="fAScore"></xsl:value-of>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</xsl:for-each>
								</fo:table-body>
							</fo:table>
							</xsl:for-each>				
						</xsl:for-each>			<!-- End of FORM -->  
					<fo:block id="my-sequence-id"/>
				</fo:flow>				<!-- Closing fo:flow -->
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
</xsl:stylesheet>