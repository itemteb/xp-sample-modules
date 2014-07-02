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
      <h3>Kommentarer</h3>

      <div class="media">
        <a href="#" class="pull-left">
          <img src="{portal:createResourceUrl('img/anon.jpg')}" height="64px" width="64px"/>
        </a>

        <div class="media-body">
          <p>
            Vi er svært godt fornøyd med vår trampoline fra Springfield Trampolines. Ungene har brukt den hele
            sommeren og den er like
            fin
            også
            sikkerhetsnettet. Kvaliteten er definitivt mye bedre enn billigvariantene man får kjøpt på
            lekebutikker og hagesentre etc.
            Vår
            trampoline er dessuten helt klart den mest populære i nabolaget pga spensten Tilogmed mor og far har
            forsøkt seg litt...
            Veldig
            bra
            trim Det var enkelt å bestille på nettet og helt supert å få den levert på døra. Monteringen gikk
            også veldig greit. Jeg
            anbefaler
            absolutt alle andre å legge litt ekstra penger i en kvalitetstrampoline fra Springfield Trampolines
          </p>

          <h5>Thomas Enonicsen</h5>

        </div>
        <hr/>
      </div>
      <div class="media">
        <a href="#" class="pull-left">
          <img src="{portal:createResourceUrl('img/anon.jpg')}" height="64px" width="64px"/>
        </a>

        <div class="media-body">

          <p>
            En veldig bra trampoline Den er solid. Videoen der dere sammenligner denne og en billig modell er
            riktig. Mange har spurt
            hvorfor
            jeg ville betale så mye for en trampline når det er mulig å få den billig. De fleste skjønner
            hvorfor når de ser den og
            prøver
            den.
          </p>

          <h5>Thomas Sigdestad</h5>

        </div>
        <hr/>
      </div>
      <div class="media">
        <a href="#" class="pull-left">
          <img src="{portal:createResourceUrl('img/anon.jpg')}" height="64px" width="64px"/>
        </a>

        <div class="media-body">

          <p>
            Vi er svært fornøyd med vår trampoline. Den er sikker man faller aldri mellom hoppematte og
            sikkrehtsmatte selv når barn i
            alle
            andre løper rundt på kanten eller hopper aldri så skjevt eller mange. Sikkerhetsnettet har en
            ypperlig løsning og er svært
            komfortabelt å kræsje i. I det hele tatt veldig fornøyd og anbefaler tramploinene til alle som spør.
          </p>

          <h5>Thomas Lund</h5>

        </div>
        <hr/>
      </div>
      <div class="media">
        <a href="#" class="pull-left">
          <img src="{portal:createResourceUrl('img/anon.jpg')}" height="64px" width="64px"/>
        </a>

        <div class="media-body">

          <p>
            Vi er veldig fornøyd med trampolina den er solid og god kvalitet. vi føler også at vi kan stole på
            sikkerhetsnettet noe som
            er
            viktig for oss
          </p>

          <h5>Enonic Thomassen</h5>

        </div>
        <hr/>
      </div>
    </div>
  </xsl:template>

</xsl:stylesheet>
