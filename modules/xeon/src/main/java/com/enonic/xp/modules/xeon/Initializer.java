package com.enonic.xp.modules.xeon;

import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.page.CreatePageTemplateParams;
import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.content.page.PageRegions;
import com.enonic.xp.content.page.PageTemplateService;
import com.enonic.xp.content.page.region.ImageComponent;
import com.enonic.xp.content.page.region.LayoutComponent;
import com.enonic.xp.content.page.region.LayoutRegions;
import com.enonic.xp.content.page.region.PartComponent;
import com.enonic.xp.content.page.region.Region;
import com.enonic.xp.content.site.CreateSiteParams;
import com.enonic.xp.content.site.ModuleConfig;
import com.enonic.xp.content.site.ModuleConfigs;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(immediate = true)
public final class Initializer
{
    private final static Logger LOG = LoggerFactory.getLogger( Initializer.class );

    public static final ModuleKey THIS_MODULE = ModuleKey.from( Initializer.class );

    private static final AccessControlList PERMISSIONS =
        AccessControlList.of( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( Permission.READ ).build(),
                              AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( Permission.READ ).build(),
                              AccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).allowAll().build() );

    private ContentPath xeonFolder = ContentPath.from( "/xeon" );

    private ContentService contentService;

    private PageTemplateService pageTemplateService;

    @Activate
    public void initialize()
        throws Exception
    {
        LOG.info( "initialize...." );

        runAs( RoleKeys.CONTENT_MANAGER_ADMIN, () -> {
            doInitialize();
            return null;
        } );
    }

    public void doInitialize()
        throws Exception
    {
        if ( !this.hasContent( xeonFolder ) )
        {
            final ModuleConfig moduleConfig = ModuleConfig.newModuleConfig().
                module( THIS_MODULE ).
                config( new PropertyTree() ).
                build();
            final ModuleConfigs moduleConfigs = ModuleConfigs.from( moduleConfig );

            final Site site = contentService.create( createSiteContent( "Xeon", "Xeon demo site.", moduleConfigs ) );
            final UpdateContentParams setSitePermissions = new UpdateContentParams().
                contentId( site.getId() ).
                editor( ( content ) -> {
                    content.permissions = PERMISSIONS;
                    content.inheritPermissions = false;
                } );
            contentService.update( setSitePermissions );

            createPageTemplateHomePage( site.getPath() );
            createPageTemplateBannerPage( site.getPath() );
            createPageTemplatePersonPage( site.getPath() );

            contentService.applyPermissions(
                ApplyContentPermissionsParams.create().contentId( site.getId() ).modifier( PrincipalKey.ofAnonymous() ).build() );
        }
    }

    private CreateSiteParams createSiteContent( final String displayName, final String description, final ModuleConfigs moduleConfigs )
    {
        return new CreateSiteParams().
            moduleConfigs( moduleConfigs ).
            description( description ).
            displayName( displayName ).
            parent( ContentPath.ROOT );
    }

    private Content createPageTemplateHomePage( final ContentPath sitePath )
    {
        final ContentTypeNames supports = ContentTypeNames.from( ContentTypeName.site() );

        return pageTemplateService.create( new CreatePageTemplateParams().
            site( sitePath ).
            name( "home-page" ).
            displayName( "Home page" ).
            controller( DescriptorKey.from( THIS_MODULE, "apage" ) ).
            supports( supports ).
            pageConfig( new PropertyTree() ).
            pageRegions( PageRegions.newPageRegions().
                add( Region.newRegion().
                    name( "main" ).
                    add( PartComponent.newPartComponent().name( "Empty-part" ).build() ).
                    build() ).
                build() ) );
    }

    private Content createPageTemplateBannerPage( final ContentPath sitePath )
    {
        final ContentTypeNames supports = ContentTypeNames.from( ContentTypeName.site() );

        return pageTemplateService.create( new CreatePageTemplateParams().
            site( sitePath ).
            name( "banner-page" ).
            displayName( "Banner" ).
            controller( DescriptorKey.from( THIS_MODULE, "banner-page" ) ).
            supports( supports ).
            pageConfig( new PropertyTree() ).
            pageRegions( PageRegions.newPageRegions().
                add( Region.newRegion().
                    name( "main" ).
                    add( LayoutComponent.newLayoutComponent().name( "Layout-3-col" ).
                        descriptor( DescriptorKey.from( THIS_MODULE, "layout-3-col" ) ).
                        regions( LayoutRegions.newLayoutRegions().
                            add( Region.newRegion().name( "left" ).
                                add( ImageComponent.newImageComponent().name( "Image" ).build() ).
                                build() ).
                            add( Region.newRegion().name( "center" ).
                                add( ImageComponent.newImageComponent().name( "Image" ).build() ).
                                build() ).
                            add( Region.newRegion().name( "right" ).
                                add( ImageComponent.newImageComponent().name( "Image" ).build() ).
                                build() ).
                            build() ).
                        build() ).
                    add( PartComponent.newPartComponent().name( "mypart" ).build() ).
                    build() ).
                build() ) );
    }

    private Content createPageTemplatePersonPage( final ContentPath sitePath )
    {
        final ContentTypeNames supports = ContentTypeNames.from( ContentTypeName.from( THIS_MODULE, "person" ) );

        return pageTemplateService.create( new CreatePageTemplateParams().
            site( sitePath ).
            name( "person-page" ).
            displayName( "Person" ).
            controller( DescriptorKey.from( THIS_MODULE, "person" ) ).
            supports( supports ).
            pageConfig( new PropertyTree() ).
            pageRegions( PageRegions.newPageRegions().
                add( Region.newRegion().
                    name( "main" ).
                    add( PartComponent.newPartComponent().name( "Person" ).descriptor(
                        DescriptorKey.from( THIS_MODULE, "person" ) ).build() ).
                    build() ).
                build() ) );
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

    private <T> T runAs( final PrincipalKey role, final Callable<T> runnable )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( role ).user( User.ANONYMOUS ).build();
        return ContextBuilder.from( ContextAccessor.current() ).authInfo( authInfo ).build().callWith( runnable );
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setPageTemplateService( final PageTemplateService pageTemplateService )
    {
        this.pageTemplateService = pageTemplateService;
    }
}
