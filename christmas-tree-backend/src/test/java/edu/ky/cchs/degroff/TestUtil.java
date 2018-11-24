package edu.ky.cchs.degroff;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

public class TestUtil
    {

    @Test
    public void doNothing()
        {

        }

    // @Test
    public void changeNames()
        {
        String[] ext = { "mp3" };
        File dir = new File( "f:/music" );
        Collection<File> files = FileUtils.listFiles( dir, ext, true );

        files.stream()
                // Filter only mp3s
                .filter( File::exists ).filter( File::isFile )
                // Loop through files and rename
                .forEach( file -> {
                String path = file.getParentFile().getPath();
                String newName = FilenameUtils.removeExtension( file.getName() );
                newName = newName.replaceAll( "[^\\w]", "" );
                newName = newName.substring( 0, Math.min( newName.length(), 20 ) );

                StringBuilder sbName = new StringBuilder( path ).append( '\\' ).append( newName ).append( ".mp3" );

                File newFile = new File( sbName.toString() );
                System.out.println( sbName.toString() );
                try
                    {
                    FileUtils.moveFile( file, newFile );
                    }
                catch ( IOException e )
                    {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    }
                // System.out.println( file.getParentFile().getPath() );
                // System.out.println( file.getAbsolutePath() );
                // System.out.println( file.getName() );
                // Path source = Paths.get(file.getAbsolutePath());
                // Path newdir = Paths.get("to/path");
                // Files.move(source, newdir.resolve(source.getFileName()), REPLACE_EXISTING);
                } );
        }

    }
