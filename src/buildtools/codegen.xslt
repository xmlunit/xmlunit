<?xml version="1.0"?>
<!--
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
                xmlns:html="http://www.w3.org/Profiles/XHTML-transitional">

   <xsl:output method="text" indent="no"/>

   <xsl:param name="compareMethod"/>
   <xsl:param name="nsQualifier"/>
   <xsl:param name="nsStart"/>
   <xsl:param name="nsEnd"/>
   <xsl:param name="import"/>
   <xsl:param name="extends"/>
   <xsl:param name="implements"/>
   <xsl:param name="summaryStart"/>
   <xsl:param name="summaryEnd"/>
   <xsl:param name="getXPath"/>

   <xsl:template match="class">
/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
     <xsl:value-of select="$nsQualifier"/>
     <xsl:text> </xsl:text>
     <xsl:value-of select="@ns"/>
     <xsl:value-of select="$nsStart"/>
     <xsl:text>
     </xsl:text>

     <xsl:apply-templates mode="imports" select="import"/>

     <xsl:value-of select="$summaryStart"/>
     <xsl:value-of select="@summary"/>
     <xsl:text>
     </xsl:text>
     <xsl:value-of select="$summaryEnd"/>
     <xsl:text>
</xsl:text>

     <xsl:value-of select="@qualifiers"/>
     <xsl:text> </xsl:text>
     class 
     <xsl:value-of select="@name"/>
     <xsl:if test="@extends">
       <xsl:text> </xsl:text>
       <xsl:value-of select="$extends"/>
       <xsl:text> </xsl:text>
       <xsl:value-of select="@extends"/>
     </xsl:if>
     <xsl:if test="@implements">
       <xsl:text> </xsl:text>
       <xsl:value-of select="$implements"/>
       <xsl:text> </xsl:text>
       <xsl:value-of select="@implements"/>
     </xsl:if>

     {
     <xsl:apply-templates/>
     }
     <xsl:value-of select="$nsEnd"/>
   </xsl:template>

   <xsl:template match="import" mode="imports">
     <xsl:value-of select="$import"/>
     <xsl:text> </xsl:text>
     <xsl:value-of select="@reference"/>;
   </xsl:template>

   <xsl:template match="lastResultDef">
        ComparisonResult lastResult = ComparisonResult.CRITICAL;
   </xsl:template>

   <xsl:template match="compare">
        lastResult =
            <xsl:value-of select="$compareMethod"/>(new Comparison(ComparisonType.<xsl:value-of select="@type"/>,
                                   control, <xsl:value-of select="$getXPath"/>(controlContext),
                                   control.<xsl:value-of select="@property"/>,
                                   test, <xsl:value-of select="$getXPath"/>(testContext),
                                   test.<xsl:value-of select="@property"/>));
        <xsl:call-template name="if-return-boilerplate"/>
   </xsl:template>

   <xsl:template match="compareExpr">
        lastResult =
            <xsl:value-of select="$compareMethod"/>(new Comparison(ComparisonType.<xsl:value-of select="@type"/>,
                                   control, <xsl:value-of select="$getXPath"/>(controlContext),
                                   <xsl:value-of select="@controlExpr"/>,
                                   test, <xsl:value-of select="$getXPath"/>(testContext),
                                   <xsl:value-of select="@testExpr"/>));
        <xsl:call-template name="if-return-boilerplate"/>
   </xsl:template>

   <xsl:template match="compareMethod">
        lastResult = <xsl:value-of select="@method"/>(control, controlContext,
                                                      test, testContext);
        <xsl:call-template name="if-return-boilerplate"/>
   </xsl:template>

   <xsl:template match="compareMethodExpr">
        lastResult = <xsl:value-of select="@method"/>(<xsl:value-of select="@controlExpr"/>, controlContext,
                                                      <xsl:value-of select="@testExpr"/>, testContext);
        <xsl:call-template name="if-return-boilerplate"/>
   </xsl:template>

   <xsl:template match="if-return-boilerplate">
        <xsl:call-template name="if-return-boilerplate"/>
   </xsl:template>

   <xsl:template name="if-return-boilerplate">
        if (lastResult == ComparisonResult.CRITICAL) {
            return lastResult;
        }
   </xsl:template>
</xsl:stylesheet>
