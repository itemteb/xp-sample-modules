<?xml version="1.0"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:portal="wem:portal"
                xmlns:wem="http://enonic.com/wem"
                exclude-result-prefixes="wem portal">

  <xsl:output method="html" omit-xml-declaration="yes"/>

  <xsl:include href="portal-lib.xsl"/>
  <xsl:param name="editable" select="false()"/>
  <xsl:param name="title" select="''"/>
  <xsl:param name="componentType" select="''"/>

  <xsl:template match="/">
    <div data-live-edit-type="{$componentType}">

    <h3>Beskrivelse</h3>
    <h4>Jumping Jack - Big Bounce</h4>

    <p>Trampoline av høyeste kvalitet laget for kommersielt bruk eller for den som vil ha det aller beste innen
        trampoliner. Trampolinen
        Berg Elite brukes blant annet av det tyske skilandslaget. Optimal brukervekt på trampolinen er 20-100
        kg.</p>


    <h4>Sprett</h4>

    <p>Trampolinen har galvaniserte fjær som er produsert i Europa og er spesiellt utviklet for å gi trampolinen
        optimal sprett.
        Hoppefølelsen på trampolinen er meget bra med en dyp og herlig sprett uten at det går ut over spensten.</p>

    <h4>Kvalitet og sikkerhet</h4>

    <p>Alle deler på trampolinen (bortsett fra hoppeduken) er håndlaget og testet i Nederland. Trampolinens ramme er
        dobbelt galvanisert og
        holder ekstremt høy kvalitet, noe som gjør trampolinen svært stille når den er i bruk. Trampolinens
        kantbeskyttelse er den beste på
        markedet. Sammen med en meget kontrollert sprett gir det den sikreste trampolinen i klassen. 10 års garanti
        på rammen og 5 års
        garanti på alle øvrige deler av trampolinen snakker for seg selv.</p>

</div>
  </xsl:template>

</xsl:stylesheet>
