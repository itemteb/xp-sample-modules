package com.enonic.wem.sample.features;


import org.osgi.framework.Bundle;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.CreateMediaParams;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.security.PrincipalKey;

public class BundleMediaImporter
{
    private final Bundle bundle;

    private final ContentService contentService;

    private final String startDirectory;

    private final ContentPath destination;

    private final PrincipalKey creator;

    public BundleMediaImporter( final Builder builder )
    {
        this.bundle = builder.bundle;
        this.contentService = builder.contentService;
        this.startDirectory = builder.startDirectory;
        this.destination = builder.destination;
        this.creator = builder.creator;
    }

    private CreateContentParams.Builder makeFolder()
    {
        return CreateContentParams.create().
            owner( creator ).
            contentData( new PropertyTree() ).
            type( ContentTypeName.folder() );
    }

    public void start()
        throws Exception
    {
        new BundleDirectoryTraverser( new BundleDirectoryTraverser.Builder().
            bundle( bundle ).
            startDirectory( startDirectory ).
            filePattern( "*" ) )
        {

            @Override
            protected void foundDirectory( final String name, final String parentPath )
                throws Exception
            {
                final ContentPath contentParentPath = ContentPath.from( destination, ContentPath.from( parentPath ).asRelative() );

                contentService.create( makeFolder().displayName( name ).parent( contentParentPath ).build() );
            }

            @Override
            protected void foundFile( final String name, final String parentPath, final String filePath )
                throws Exception
            {
                final ContentPath parentContent = ContentPath.from( destination, ContentPath.from( parentPath ).asRelative() );

                final CreateMediaParams createMediaParams = new CreateMediaParams().
                    byteSource( getByteSource( filePath ) ).
                    name( name ).
                    parent( parentContent );

                contentService.create( createMediaParams );
            }
        }.importMedia();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Bundle bundle;

        private ContentService contentService;

        private String startDirectory;

        private ContentPath destination;

        private PrincipalKey creator;

        public Builder bundle( final Bundle value )
        {
            this.bundle = value;
            return this;
        }

        public Builder contentService( final ContentService value )
        {
            this.contentService = value;
            return this;
        }

        public Builder startDirectory( final String value )
        {
            this.startDirectory = value;
            return this;
        }

        public Builder destination( final ContentPath value )
        {
            this.destination = value;
            return this;
        }

        public Builder creator( final PrincipalKey value )
        {
            this.creator = value;
            return this;
        }

        public BundleMediaImporter build()
        {
            return new BundleMediaImporter( this );
        }
    }
}
