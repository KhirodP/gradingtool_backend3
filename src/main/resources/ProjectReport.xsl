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
			<fo:page-sequence master-reference="A4-portrait" initial-page-number="1">
				<fo:static-content flow-name="xsl-region-after" text-align="center" font-size="small">
					 <fo:block text-align="center" font-size="small">
       					Page <fo:page-number/> of <fo:page-number-citation ref-id="my-sequence-id"/>
       				 </fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body">
					<fo:block text-align="center" color="#2B77B4" font-size="26px"> 
						Project Report
					</fo:block>
					<fo:block><fo:leader/></fo:block> <!-- Inserts an empty line in PDF -->
					<fo:table border-collapse="collapse" table-layout="fixed" width="17cm" left="0cm">
						<fo:table-column column-width="5cm"/>
						<fo:table-column column-width="4cm"/>
						<fo:table-column column-width="0.5cm"/>
						<fo:table-column column-width="5cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-body>
							<fo:table-row line-height="30pt">
								<fo:table-cell border-bottom="1px solid #B1B1B1">
									<fo:block text-align="start" font-weight="bold" font-size="12pt">
									Project Name: 
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1">
									<fo:block text-align="start" font-size="12pt" color="dimgray" >
											<xsl:value-of select="reportHeader/projectName"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>   </fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1">
									<fo:block text-align="start" font-weight="bold" font-size="12pt">
									No of partners included:										 
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1">
									<fo:block text-align="start" font-size="12pt" color="dimgray">
											<xsl:value-of select="reportHeader/partnersIncluded"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row line-height="20pt">
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-weight="bold" font-size="12pt">
									Date on which report was extracted:										
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-size="12pt" color="dimgray">
											<xsl:value-of select="reportHeader/date"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>   </fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-weight="bold" font-size="12pt">
									No of training centers included :										
									</fo:block>
								</fo:table-cell>
								<fo:table-cell border-bottom="1px solid #B1B1B1" display-align="center">
									<fo:block text-align="start" font-size="12pt" color="dimgray">
											<xsl:value-of select="reportHeader/tcIncluded"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					
					<fo:block><fo:leader/></fo:block>	<!-- Inserts an empty line in PDF -->
					
					<xsl:if test="warning != ''">
						<fo:block text-align="center" font-size="large" color="#ffa500" font-family="Georgia">Warning!</fo:block>
						<fo:block text-align="center" font-size="medium" color="#ffa500" font-family="Georgia">
							<xsl:value-of select="warning"/>
						</fo:block>
					</xsl:if>
					
					<fo:block><fo:leader/></fo:block>	<!-- Inserts an empty line in PDF -->
					<fo:table border-collapse="collapse" border="1px solid #ddd" table-layout="fixed" width="17cm">
						<fo:table-column column-width="1.5cm"/>
						<fo:table-column column-width="7.5cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-column column-width="2cm"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell background-color="#4CAF50" display-align="center" padding-start="5pt" border="1px solid #ddd" line-height="12pt" padding-top="12px" padding-bottom="12px">
									<fo:block color="white" font-size="small" font-weight="bold">Sl.No</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4CAF50" display-align="center" padding-start="5pt" border="1px solid #ddd" line-height="12pt" padding-top="12px" padding-bottom="12px">
									<fo:block color="white" font-size="small" font-weight="bold">PIA Name</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4CAF50" display-align="center" padding-start="5pt" border="1px solid #ddd" line-height="12pt" padding-top="12px" padding-bottom="12px">
									<fo:block color="white" font-size="small" font-weight="bold">Centre Rating</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4CAF50" display-align="center" padding-start="5pt" border="1px solid #ddd" line-height="12pt" padding-top="12px" padding-bottom="12px">
									<fo:block color="white" font-size="small" font-weight="bold">Project Grading</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4CAF50" display-align="center" padding-start="5pt" border="1px solid #ddd" line-height="12pt" padding-top="12px" padding-bottom="12px">
									<fo:block color="white" font-size="small" font-weight="bold">Final %age</fo:block>
								</fo:table-cell>
								<fo:table-cell background-color="#4CAF50" text-align="center" display-align="center" border="1px solid #ddd" line-height="12pt" padding-top="12px" padding-bottom="12px">
									<fo:block color="white" font-size="small" font-weight="bold">Grade</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<xsl:for-each select="reportBody/partnersSummaries/partnersSummary">
								<fo:table-row>
									<xsl:choose>
										<xsl:when test="position() mod 2 = 1">
											<fo:table-cell background-color="lightgray" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"> <xsl:value-of select="position()"/></fo:block>
											</fo:table-cell>
											<fo:table-cell background-color="lightgray" text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="pia"/></fo:block>
											</fo:table-cell>
											<fo:table-cell background-color="lightgray" text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="number(centerRating)"/>%</fo:block>
											</fo:table-cell>
											<fo:table-cell background-color="lightgray" text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="number(projectGrading)"/>%</fo:block>
											</fo:table-cell>
											<fo:table-cell background-color="lightgray" text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="number(finalAvg)"/>%</fo:block>
											</fo:table-cell>
											<fo:table-cell background-color="lightgray" text-align="center" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="grade"/></fo:block>
											</fo:table-cell>
										</xsl:when>
										<xsl:otherwise>
											<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"> <xsl:value-of select="position()"/></fo:block>
											</fo:table-cell>
											<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="pia"/></fo:block>
											</fo:table-cell>
											<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="number(centerRating)"/>%</fo:block>
											</fo:table-cell>
											<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="number(projectGrading)"/>%</fo:block>
											</fo:table-cell>
											<fo:table-cell text-align="start" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="number(finalAvg)"/>%</fo:block>
											</fo:table-cell>
											<fo:table-cell text-align="center" border="1px solid #ddd" display-align="center" padding="5px">
												<fo:block font-size="small"><xsl:value-of select="grade"/></fo:block>
											</fo:table-cell>
										</xsl:otherwise>
									</xsl:choose>
									
								</fo:table-row>
							</xsl:for-each>
						</fo:table-body>
					</fo:table>
					 <fo:block id="my-sequence-id"/>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
</xsl:stylesheet>