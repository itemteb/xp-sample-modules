package org.seedsavers;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.page.CreatePageTemplateParams;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.region.ImageComponent;
import com.enonic.wem.api.content.page.region.PartComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.site.CreateSiteParams;
import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.ModuleConfigs;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.initializer.DataInitializer;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.util.Reference;

@SuppressWarnings("UnusedDeclaration")
public final class Initializer
    implements DataInitializer
{
    private final static Logger LOG = LoggerFactory.getLogger( Initializer.class );

    public static final ModuleKey THIS_MODULE = ModuleKey.from( Initializer.class );

    private static final ContentTypeName MEMBER_CONTENT_TYPE_NAME = ContentTypeName.from( THIS_MODULE, "member" );

    private ContentPath seedSaversFolder = ContentPath.from( "/seed-savers" );

    private ContentPath membersFolder = ContentPath.from( seedSaversFolder, "members" );

    private ContentService contentService;

    private PageTemplateService pageTemplateService;

    private ContentTypeService contentTypeService;

    private HashMap<String, Content> familyByDisplayName = new HashMap<String, Content>();

    @Override
    public void initialize()
        throws Exception
    {
        LOG.info( "initialize...." );

        if ( !this.hasContent( ContentPath.from( "/seed-savers" ) ) )
        {
            final PropertyTree moduleConfigData = new PropertyTree();
            moduleConfigData.setString( "language", "no" );
            final ModuleConfig moduleConfig = ModuleConfig.newModuleConfig().
                module( THIS_MODULE ).
                config( moduleConfigData ).
                build();
            final ModuleConfigs moduleConfigs = ModuleConfigs.from( moduleConfig );

            final Site site = contentService.create( createSiteContent( "Seed Savers", "A Site for seed savers.", moduleConfigs ) );

            createPageTemplateTopMain( site.getPath() );
            createPageTemplatePlant( site.getPath() );
            createPageTemplateMember( site.getPath() );
            createPageTemplateFamily( site.getPath() );
            createPageTemplateGenus( site.getPath() );

            contentService.create( createFolder().
                parent( seedSaversFolder ).
                displayName( "Members" ).build() );

            final Content families = contentService.create( createFolder().
                parent( seedSaversFolder ).
                displayName( "Families" ).build() );

            createFamilies( families.getPath() );

            final Content genuses = contentService.create( createFolder().
                parent( seedSaversFolder ).
                displayName( "Genus" ).build() );

            createGenuses( genuses.getPath() );

            contentService.create( createFolder().
                parent( seedSaversFolder ).
                displayName( "Varieties" ).build() );

            contentService.create( createFolder().
                parent( seedSaversFolder ).
                displayName( "Caretakings" ).build() );

            createMembers();
        }
    }

    private void createMembers()
    {
        contentService.create( createMember( "Åse Kålrot" ).build() );
        contentService.create( createMember( "Guri Gulrot" ).build() );
        contentService.create( createMember( "Johan Støver" ).build() );
        contentService.create( createMember( "Jørund Vier Skriubakken" ).build() );
        contentService.create( createMember( "Kari Korn" ).build() );
        contentService.create( createMember( "Per Kål" ).build() );
        contentService.create( createMember( "Petter Skvaller" ).build() );
    }

    private CreateSiteParams createSiteContent( final String displayName, final String description, final ModuleConfigs moduleConfigs )
    {
        return new CreateSiteParams().
            moduleConfigs( moduleConfigs ).
            description( description ).
            displayName( displayName ).
            parent( ContentPath.ROOT );
    }

    private CreateContentParams.Builder createFolder()
    {
        return CreateContentParams.create().
            owner( PrincipalKey.ofAnonymous() ).
            contentData( new PropertyTree() ).
            type( ContentTypeName.folder() );
    }

    private CreateContentParams.Builder createMember( final String displayName )
    {
        final ContentTypeName contentType = MEMBER_CONTENT_TYPE_NAME;
        final PropertyTree data = new PropertyTree();
        data.setString( "name", displayName );
        return CreateContentParams.create().
            displayName( displayName ).
            owner( PrincipalKey.ofAnonymous() ).
            contentData( data ).
            type( contentType ).
            parent( this.membersFolder );
    }

    private void createFamilies( final ContentPath parentPath )
    {
        Content family = contentService.create( createFamily( "Plantaginaceae", "Kjempefamilien" ).
            parent( parentPath ).build() );
        familyByDisplayName.put( family.getDisplayName(), family );

        family = contentService.create( createFamily( "Verbenaceae", "Jernurtfamilien" ).
            parent( parentPath ).build() );
        familyByDisplayName.put( family.getDisplayName(), family );

        family = contentService.create( createFamily( "Lamiaceae", "Leppeblomstfamilien" ).
            parent( parentPath ).build() );
        familyByDisplayName.put( family.getDisplayName(), family );
    }

    private CreateContentParams.Builder createFamily( final String scientificName, final String norwegianName )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "norwegianNames", norwegianName );

        return CreateContentParams.create().
            displayName( scientificName ).
            owner( PrincipalKey.ofAnonymous() ).
            contentData( data ).
            type( ContentTypeName.from( THIS_MODULE, "family" ) );
    }

    private void createGenuses( final ContentPath parentPath )
    {
        contentService.create( createGenus( "Plantago", "Grodblad", "Plantaginaceae" ).
            parent( parentPath ).build() );
        contentService.create( createGenus( "Antirrhinum", "Løvemunnslekta", "Plantaginaceae" ).
            parent( parentPath ).build() );
        contentService.create( createGenus( "Digitalis", "Revebjelleslekta", "Plantaginaceae" ).
            parent( parentPath ).build() );
    }

    private CreateContentParams.Builder createGenus( final String scientificName, final String norwegianName,
                                                     final String familyDisplayName )
    {
        final Content family = this.familyByDisplayName.get( familyDisplayName );
        final PropertyTree data = new PropertyTree();
        data.addString( "norwegianNames", norwegianName );
        if ( family != null )
        {
            data.addReference( "family", Reference.from( family.getId().toString() ) );
        }

        return CreateContentParams.create().
            displayName( scientificName ).
            owner( PrincipalKey.ofAnonymous() ).
            contentData( data ).
            type( ContentTypeName.from( THIS_MODULE, "genus" ) );
    }

    private Content createPageTemplateTopMain( final ContentPath sitePath )
    {
        final ContentTypeNames supports = ContentTypeNames.from( ContentTypeName.site() );
        return pageTemplateService.create( new CreatePageTemplateParams().
            site( sitePath ).
            name( "top-main" ).
            displayName( "Top and main" ).
            controller( DescriptorKey.from( THIS_MODULE, "top-main" ) ).
            supports( supports ).
            pageConfig( new PropertyTree() ).
            pageRegions( PageRegions.newPageRegions().
                add( Region.newRegion().
                    name( "top" ).
                    add( ImageComponent.newImageComponent().name( "Image" ).build() ).
                    build() ).
                add( Region.newRegion().name( "main" ).build() ).
                build() ) );
    }

    private Content createPageTemplateFamily( final ContentPath sitePath )
    {
        final ContentTypeNames supports = ContentTypeNames.from( ContentTypeName.from( THIS_MODULE, "family" ) );
        return pageTemplateService.create( new CreatePageTemplateParams().
            site( sitePath ).
            displayName( "Family" ).
            controller( DescriptorKey.from( THIS_MODULE, "family" ) ).
            supports( supports ).
            pageRegions( PageRegions.newPageRegions().
                add( Region.newRegion().
                    name( "main" ).
                    add( ImageComponent.newImageComponent().name( "Image" ).build() ).
                    add( PartComponent.newPartComponent().
                        name( "Family" ).descriptor( DescriptorKey.from( THIS_MODULE, "family" ) ).
                        build() ).
                    build() ).
                build() ).
            pageConfig( new PropertyTree() ) );
    }

    private Content createPageTemplateGenus( final ContentPath sitePath )
    {
        final ContentTypeNames supports = ContentTypeNames.from( ContentTypeName.from( THIS_MODULE, "genus" ) );
        return pageTemplateService.create( new CreatePageTemplateParams().
            site( sitePath ).
            displayName( "Genus" ).
            controller( DescriptorKey.from( THIS_MODULE, "genus" ) ).
            supports( supports ).
            pageRegions( PageRegions.newPageRegions().
                add( Region.newRegion().
                    name( "main" ).
                    add( ImageComponent.newImageComponent().name( "Image" ).build() ).
                    add( PartComponent.newPartComponent().
                        name( "Genus" ).descriptor( DescriptorKey.from( THIS_MODULE, "genus" ) ).
                        build() ).
                    build() ).
                build() ).
            pageConfig( new PropertyTree() ) );
    }

    private Content createPageTemplatePlant( final ContentPath sitePath )
    {
        final ContentTypeNames supports = ContentTypeNames.from( ContentTypeName.from( THIS_MODULE, "plant" ) );
        return pageTemplateService.create( new CreatePageTemplateParams().
            site( sitePath ).
            displayName( "Plant" ).
            controller( DescriptorKey.from( THIS_MODULE, "plant" ) ).
            supports( supports ).
            pageRegions( PageRegions.newPageRegions().
                add( Region.newRegion().
                    name( "main" ).
                    add( ImageComponent.newImageComponent().name( "Image" ).build() ).
                    add( PartComponent.newPartComponent().
                        name( "Plant" ).descriptor( DescriptorKey.from( THIS_MODULE, "plant" ) ).
                        build() ).
                    build() ).
                build() ).
            pageConfig( new PropertyTree() ) );
    }

    private Content createPageTemplateMember( final ContentPath sitePath )
    {
        final ContentTypeNames supports = ContentTypeNames.from( ContentTypeName.from( THIS_MODULE, "member" ) );
        return pageTemplateService.create( new CreatePageTemplateParams().
            site( sitePath ).
            displayName( "Member" ).
            controller( DescriptorKey.from( THIS_MODULE, "member" ) ).
            supports( supports ).
            pageRegions( PageRegions.newPageRegions().
                add( Region.newRegion().
                    name( "main" ).
                    add( PartComponent.newPartComponent().
                        name( "Member" ).descriptor( DescriptorKey.from( THIS_MODULE, "member" ) ).
                        build() ).
                    build() ).
                build() ).
            pageConfig( new PropertyTree() ) );
    }

    private boolean hasContent( final ContentPath path )
    {
        try
        {
            return this.contentService.getByPath( path ) != null;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public void setPageTemplateService( final PageTemplateService pageTemplateService )
    {
        this.pageTemplateService = pageTemplateService;
    }

    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }
}
