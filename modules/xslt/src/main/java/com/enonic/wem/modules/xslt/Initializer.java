package com.enonic.wem.modules.xslt;

import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.ApplyContentPermissionsParams;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.page.CreatePageTemplateParams;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.site.CreateSiteParams;
import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.ModuleConfigs;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;
import com.enonic.wem.api.security.auth.AuthenticationInfo;

@Component(immediate = true)
public final class Initializer
{
    private final static Logger LOG = LoggerFactory.getLogger( Initializer.class );

    public static final ModuleKey THIS_MODULE = ModuleKey.from( Initializer.class );

    private static final AccessControlList PERMISSIONS =
        AccessControlList.of( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( Permission.READ ).build(),
                              AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( Permission.READ ).build(),
                              AccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).allowAll().build() );

    private ContentService contentService;

    private PageTemplateService pageTemplateService;

    @Activate
    public void initialize()
        throws Exception
    {
        runAs( RoleKeys.ADMIN, () -> {
            doInitialize();
            return null;
        } );
    }

    private void doInitialize()
    {
        final ContentPath path = ContentPath.from( ContentPath.ROOT, "xslt" );
        if ( hasContent( path ) )
        {
            LOG.info( "Already initialized with data. Skipping." );
            return;
        }

        LOG.info( "Initializing data...." );

        final ModuleConfig moduleConfig = ModuleConfig.newModuleConfig().
            module( THIS_MODULE ).
            config( new PropertyTree() ).
            build();
        final ModuleConfigs moduleConfigs = ModuleConfigs.from( moduleConfig );

        final Site site = contentService.create( createSiteContent( "Xslt", "Xslt demo site.", moduleConfigs ) );
        final UpdateContentParams setSitePermissions = new UpdateContentParams().
            contentId( site.getId() ).
            editor( ( content ) -> {
                content.permissions = PERMISSIONS;
                content.inheritPermissions = false;
            } );
        this.contentService.update( setSitePermissions );

        createRssTemplate( site.getPath() );

        this.contentService.applyPermissions(
            ApplyContentPermissionsParams.create().contentId( site.getId() ).modifier( PrincipalKey.ofAnonymous() ).build() );
    }

    private <T> T runAs( final PrincipalKey role, final Callable<T> runnable )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( role ).user( User.ANONYMOUS ).build();
        return ContextBuilder.from( ContextAccessor.current() ).authInfo( authInfo ).build().callWith( runnable );
    }

    private CreateSiteParams createSiteContent( final String displayName, final String description, final ModuleConfigs moduleConfigs )
    {
        return new CreateSiteParams().
            moduleConfigs( moduleConfigs ).
            description( description ).
            displayName( displayName ).
            parent( ContentPath.ROOT );
    }

    private Content createRssTemplate( final ContentPath sitePath )
    {
        final ContentTypeNames supports = ContentTypeNames.from( ContentTypeName.site() );

        return this.pageTemplateService.create( new CreatePageTemplateParams().
            site( sitePath ).
            name( "rss-page" ).
            displayName( "Rss page" ).
            controller( DescriptorKey.from( THIS_MODULE, "rss" ) ).
            supports( supports ).
            pageConfig( new PropertyTree() ).
            pageRegions( PageRegions.newPageRegions().
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
