<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" version="1.0" encoding="UTF-8"/>

<xsl:template match="animal">
  <xsl:param name="whatAnimal"><xsl:value-of select="."/></xsl:param>
  <xsl:element name="{$whatAnimal}"/>
</xsl:template>

</xsl:stylesheet>
