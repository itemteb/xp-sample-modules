package com.enonic.wem.sample.features;

import java.util.concurrent.Callable;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.export.ExportService;
import com.enonic.wem.api.export.ImportNodesParams;
import com.enonic.wem.api.export.NodeImportResult;
import com.enonic.wem.api.initializer.DataInitializer;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.vfs.VirtualFile;
import com.enonic.wem.api.vfs.VirtualFiles;

public final class FeaturesInitializer
    implements DataInitializer
{
    private ContentService contentService;

    private ExportService exportService;

    private final Logger LOG = LoggerFactory.getLogger( FeaturesInitializer.class );

    @Override
    public void initialize()
        throws Exception
    {
        runAs( RoleKeys.CONTENT_MANAGER_ADMIN, () -> {
            doInitialize();
            return null;
        } );
    }

    private void doInitialize()
        throws Exception
    {
        if ( hasContent( ContentPath.from( "/features" ) ) )
        {
            return;
        }

        final Bundle bundle = FrameworkUtil.getBundle( this.getClass() );

        final VirtualFile source = VirtualFiles.from( bundle, "/import" );

        final NodeImportResult nodeImportResult = this.exportService.importNodes( ImportNodesParams.create().
            source( source ).
            targetNodePath( NodePath.newPath( "/content" ).build() ).
            includeNodeIds( true ).
            dryRun( false ).
            build() );

        logImport( nodeImportResult );

        createLargeTree();
    }

    private void logImport( final NodeImportResult nodeImportResult )
    {
        LOG.info( "-------------------" );
        LOG.info( "Imported nodes:" );
        for ( final NodePath nodePath : nodeImportResult.getAddedNodes() )
        {
            LOG.info( nodePath.toString() );
        }

        LOG.info( "-------------------" );
        LOG.info( "Binaries:" );
        for ( final String binaryRef : nodeImportResult.getExportedBinaries() )
        {
            LOG.info( binaryRef );
        }

        LOG.info( "-------------------" );
        LOG.info( "Errors:" );
        for ( final NodeImportResult.ImportError importError : nodeImportResult.getImportErrors() )
        {
            LOG.info( importError.getMessage(), importError.getException() );
        }
    }

    private CreateContentParams.Builder makeFolder()
    {
        return CreateContentParams.create().
            owner( PrincipalKey.ofAnonymous() ).
            contentData( new PropertyTree() ).
            type( ContentTypeName.folder() );
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

    @SuppressWarnings("UnusedDeclaration")
    public void setExportService( final ExportService exportService )
    {
        this.exportService = exportService;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    private void createLargeTree()
    {
        final ContentPath largeTreePath = ContentPath.from( "/large-tree" );
        if ( !hasContent( largeTreePath ) )
        {
            contentService.create( makeFolder().
                name( "large-tree" ).
                displayName( "Large tree" ).
                parent( ContentPath.ROOT ).build() );

            for ( int i = 1; i <= 2; i++ )
            {
                Content parent = contentService.create( makeFolder().
                    displayName( "large-tree-node-" + i ).
                    displayName( "Large tree node " + i ).
                    parent( largeTreePath ).build() );

                for ( int j = 1; j <= 100; j++ )
                {
                    contentService.create( makeFolder().
                        displayName( "large-tree-node-" + i + "-" + j ).
                        displayName( "Large tree node " + i + "-" + j ).
                        parent( parent.getPath() ).build() );
                }
            }
        }
    }

    private <T> T runAs( final PrincipalKey role, final Callable<T> runnable )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( role ).user( User.ANONYMOUS ).build();
        return ContextBuilder.from( ContextAccessor.current() ).authInfo( authInfo ).build().callWith( runnable );
    }
}
