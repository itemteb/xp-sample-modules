package com.enonic.wem.sample.demo;

import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.CreateMediaParams;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.security.CreateGroupParams;
import com.enonic.wem.api.security.CreateUserParams;
import com.enonic.wem.api.security.CreateUserStoreParams;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalRelationship;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;
import com.enonic.wem.api.security.acl.UserStoreAccess;
import com.enonic.wem.api.security.acl.UserStoreAccessControlEntry;
import com.enonic.wem.api.security.acl.UserStoreAccessControlList;
import com.enonic.wem.api.security.auth.AuthenticationInfo;

@Component(immediate = true)
public final class DemoInitializer
{
    private final static Logger LOG = LoggerFactory.getLogger( DemoInitializer.class );

    private static final AccessControlList PERMISSIONS =
        AccessControlList.of( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( Permission.READ ).build(),
                              AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( Permission.READ ).build(),
                              AccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).allowAll().build() );

    private static final String[] FOLDER_IMAGES_POP =
        {"Pop_01.jpg", "Pop_02.jpg", "Pop_03.jpg", "Pop_04.jpg", "Pop_05.jpg", "Pop_06.jpg", "Pop_07.jpg", "Pop_08.jpg", "Pop-Black.jpg",
            "Pop-Green.jpg", "Pop-Silverpink.jpg"};

    private static final String[] FOLDER_IMAGES_BIG =
        {"Big Bounce - R\u00f8d Tattoo.jpg", "Big Bounce - R\u00f8d.jpg", "Big Bounce_01.jpg", "Big Bounce_02.jpg", "Big Bounce_03.jpg",
            "Big Bounce_04.jpg", "Big Bounce_05.jpg", "Big Bounce_06.jpg", "Big Bounce_07.jpg", "Big Bounce_08.jpg", "Big Bounce_10.jpg",
            "Big Bounce_11.jpg", "Big Bounce_12.jpg"};

    private static final String IMAGE_ARCHIVE_PATH_ELEMENT = "image-archive";

    private static final UserStoreKey USER_STORE_KEY = new UserStoreKey( "enonic" );

    public static final PrincipalKey EMPLOYEES = PrincipalKey.ofGroup( USER_STORE_KEY, "employees" );

    public static final PrincipalKey CONSULTANTS = PrincipalKey.ofGroup( USER_STORE_KEY, "consultants" );

    public static final PrincipalKey DEVELOPERS = PrincipalKey.ofGroup( USER_STORE_KEY, "developers" );

    public static final PrincipalKey OPERATIONS = PrincipalKey.ofGroup( USER_STORE_KEY, "operations" );

    public static final PrincipalKey OSLO = PrincipalKey.ofGroup( USER_STORE_KEY, "norway" );

    public static final PrincipalKey MINSK = PrincipalKey.ofGroup( USER_STORE_KEY, "belarus" );

    public static final PrincipalKey SAN_FRANCISCO = PrincipalKey.ofGroup( USER_STORE_KEY, "usa" );

    private ContentService contentService;

    private SecurityService securityService;

    @Activate
    public void initialize()
        throws Exception
    {
        runAs( RoleKeys.ADMIN, () -> {
            createImages();
            createUserStore();
            return null;
        } );
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

    private void createImages()
        throws Exception
    {
        final ContentPath imageArchivePath = ContentPath.from( ContentPath.ROOT, IMAGE_ARCHIVE_PATH_ELEMENT );
        if ( hasContent( imageArchivePath ) )
        {
            LOG.info( "Already initialized with data. Skipping." );
            return;
        }

        LOG.info( "Initializing demo content..." );
        final long tm = System.currentTimeMillis();

        try
        {
            doCreateImages();
        }
        finally
        {
            LOG.info( "Initialized demo content in " + ( System.currentTimeMillis() - tm ) + " ms" );
        }

    }

    private void doCreateImages()
        throws Exception
    {

        final ContentPath imageArchivePath = contentService.create( createFolder().
            parent( ContentPath.ROOT ).
            displayName( "Image Archive" ).
            permissions( PERMISSIONS ).
            inheritPermissions( false ).
            build() ).getPath();

        contentService.create( createFolder().
            parent( imageArchivePath ).
            displayName( "Misc" ).build() );

        contentService.create( createFolder().
            parent( imageArchivePath ).
            displayName( "People" ).build() );

        ContentPath trampolinerPath = contentService.create( createFolder().
            parent( imageArchivePath ).
            displayName( "Trampoliner" ).build() ).getPath();

        final ContentPath folderImagesBig = contentService.create( createFolder().
            parent( trampolinerPath ).
            displayName( "Jumping Jack - Big Bounce" ).build() ).getPath();

        final ContentPath folderImagesPop = contentService.create( createFolder().
            parent( trampolinerPath ).
            displayName( "Jumping Jack - Pop" ).
            type( ContentTypeName.folder() ).build() ).getPath();

        for ( final String fileName : FOLDER_IMAGES_BIG )
        {
            createImageContent( folderImagesBig, fileName );
        }

        for ( final String fileName : FOLDER_IMAGES_POP )
        {
            createImageContent( folderImagesPop, fileName );
        }
    }

    private void createImageContent( final ContentPath parent, final String fileName )
        throws Exception
    {
        // TODO: fix due to Intellij failing when building jar with ø in resource file
        final String fixedFileName = fileName.replace( "\u00f8", "_o_" );
        final byte[] bytes = loadImageFileAsBytes( fixedFileName );
        if ( bytes == null )
        {
            return;
        }

        // FIXME: hack to avoid exception from NodeName preconditions
        final String filteredFileName =
            fileName.replace( " ", "_" ).replace( "\u00F8", "o" ).replace( "\u00E6", "ae" ).replace( "\u00E5", "aa" ).toLowerCase();

        final CreateMediaParams params = new CreateMediaParams().
            mimeType( "image/jpeg" ).
            name( filteredFileName ).
            parent( parent ).byteSource( ByteSource.wrap( bytes ) );
        contentService.create( params ).getId();
    }

    private byte[] loadImageFileAsBytes( final String fileName )
    {
        final String filePath = "/images/" + fileName;

        try
        {
            return Resources.toByteArray( getClass().getResource( filePath ) );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private CreateContentParams.Builder createFolder()
    {
        return CreateContentParams.create().
            owner( PrincipalKey.ofAnonymous() ).
            contentData( new PropertyTree() ).
            type( ContentTypeName.folder() ).
            inheritPermissions( true );
    }

    private void createUserStore()
    {
        final UserStoreKey userStoreKey = new UserStoreKey( "enonic" );
        final UserStore userStore = securityService.getUserStore( userStoreKey );
        if ( userStore == null )
        {
            final UserStoreAccessControlList permissions = UserStoreAccessControlList.of(
                UserStoreAccessControlEntry.create().principal( RoleKeys.ADMIN ).access( UserStoreAccess.ADMINISTRATOR ).build(),
                UserStoreAccessControlEntry.create().principal( DEVELOPERS ).access( UserStoreAccess.USER_STORE_MANAGER ).build(),
                UserStoreAccessControlEntry.create().principal( CONSULTANTS ).access( UserStoreAccess.CREATE_USERS ).build() );
            final CreateUserStoreParams createUserStoreParams = CreateUserStoreParams.create().
                key( USER_STORE_KEY ).
                displayName( "Enonic User Store" ).
                permissions( permissions ).
                build();
            securityService.createUserStore( createUserStoreParams );

            createPrincipals();
        }
    }

    private void createPrincipals()
    {
        final CreateGroupParams createDev = CreateGroupParams.create().
            groupKey( DEVELOPERS ).
            displayName( "Developers" ).
            build();
        final Group dev = addGroup( createDev );

        final CreateGroupParams createCon = CreateGroupParams.create().
            groupKey( CONSULTANTS ).
            displayName( "Consultants" ).
            build();
        final Group con = addGroup( createCon );

        final CreateGroupParams createOp = CreateGroupParams.create().
            groupKey( OPERATIONS ).
            displayName( "Operations" ).
            build();
        final Group op = addGroup( createOp );

        final CreateGroupParams createOslo = CreateGroupParams.create().
            groupKey( OSLO ).
            displayName( "Enonic Oslo" ).
            build();
        final Group oslo = addGroup( createOslo );

        final CreateGroupParams createMinsk = CreateGroupParams.create().
            groupKey( MINSK ).
            displayName( "Enonic Minsk" ).
            build();
        final Group minsk = addGroup( createMinsk );

        final CreateGroupParams createSF = CreateGroupParams.create().
            groupKey( SAN_FRANCISCO ).
            displayName( "Enonic San Francisco" ).
            build();
        final Group sf = addGroup( createSF );

        final CreateGroupParams createEmployees = CreateGroupParams.create().
            groupKey( EMPLOYEES ).
            displayName( "Enonic Employees" ).
            build();
        final Group employees = addGroup( createEmployees );

        addMember( employees.getKey(), dev.getKey() );
        addMember( employees.getKey(), con.getKey() );
        addMember( employees.getKey(), op.getKey() );

        addMember( RoleKeys.CONTENT_MANAGER_APP, employees.getKey() );
        addMember( RoleKeys.USER_MANAGER_APP, dev.getKey() );

        createUser( "mer", "Morten Eriksen", EMPLOYEES, OSLO );
        createUser( "tsi", "Thomas Sigdestad", EMPLOYEES, OSLO );

        createUser( "aro", "Alex Rodr\u00EDguez", DEVELOPERS, OSLO );
        createUser( "jvs", "J\u00F8rund Skriubakken", DEVELOPERS, OSLO );
        createUser( "jsi", "J\u00F8rgen Sivesind", DEVELOPERS, OSLO );
        createUser( "rmy", "Runar Myklebust", DEVELOPERS, OSLO );
        createUser( "srs", "Sten Roger Sandvik", DEVELOPERS, OSLO );
        createUser( "tlo", "Tor L\u00F8kken", DEVELOPERS, OSLO );

        createUser( "pmi", "Pavel Milkevich", DEVELOPERS, MINSK );
        createUser( "vbr", "Vlachaslau Bradnitski", DEVELOPERS, MINSK );
        createUser( "mta", "Mikita Taukachou", DEVELOPERS, MINSK );
        createUser( "sig", "Siarhei Gauruseu", DEVELOPERS, MINSK );

        createUser( "bhj", "Bj\u00F8rnar Hjelmevold", CONSULTANTS, OSLO );
        createUser( "bwe", "Bobby Westberg", CONSULTANTS, OSLO );
        createUser( "mla", "Michael Lazell", CONSULTANTS, SAN_FRANCISCO );
        createUser( "oda", "\u00D8yvind Dahl", CONSULTANTS, OSLO );
        createUser( "rfo", "Rune Forberg", CONSULTANTS, OSLO );

        createUser( "esu", "Erik Sunde", OPERATIONS, OSLO );
        createUser( "mbe", "Marek Bettman", OPERATIONS, OSLO );
    }

    private User createUser( final String userName, final String displayName, final PrincipalKey... memberships )
    {
        final CreateUserParams createUser = CreateUserParams.create().
            userKey( PrincipalKey.ofUser( USER_STORE_KEY, userName ) ).
            displayName( displayName ).
            login( userName ).
            email( userName + "@enonic.com" ).
            password( "password" ).
            build();
        final User user = addUser( createUser );
        if ( user != null )
        {
            for ( PrincipalKey key : memberships )
            {
                addMember( key, user.getKey() );
            }
            addMember( RoleKeys.ADMIN_LOGIN, user.getKey() );
        }
        return user;
    }

    private void addMember( final PrincipalKey parent, final PrincipalKey member )
    {
        try
        {
            final PrincipalRelationship relationship = PrincipalRelationship.from( parent ).to( member );
            securityService.addRelationship( relationship );
            LOG.info( "Principal " + member + " added as member of " + parent );
        }
        catch ( Throwable t )
        {
            LOG.error( "Unable to add principal " + member + " as member of " + parent );
        }
    }

    private User addUser( final CreateUserParams createUser )
    {
        try
        {
            if ( !securityService.getUser( createUser.getKey() ).isPresent() )
            {
                final User user = securityService.createUser( createUser );
                LOG.info( "User created: " + createUser.getKey().toString() );
                return user;
            }
        }
        catch ( Throwable t )
        {
            LOG.error( "Unable to initialize user: " + createUser.getKey().toString(), t );
        }
        return null;
    }

    private Group addGroup( final CreateGroupParams createGroup )
    {
        try
        {
            if ( !securityService.getGroup( createGroup.getKey() ).isPresent() )
            {
                final Group group = securityService.createGroup( createGroup );
                LOG.info( "Group created: " + createGroup.getKey().toString() );
                return group;
            }
        }
        catch ( Throwable t )
        {
            LOG.error( "Unable to initialize group: " + createGroup.getKey().toString(), t );
        }
        return null;
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
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }
}

