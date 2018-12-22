package edu.ky.cchs.degroff.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public class MusicSet
    {
    private Logger logger = LoggerFactory.getLogger( MusicSet.class );
    private String instructionFile;
    private String musicFile;
    private String title;
    private String category;
    private boolean isVirtual;
    private List<Instruction> instructions = new ArrayList<>();
    private Resource musicResource;

    public MusicSet( Resource resource, Map<String, Resource> musicResourceMap ) throws IOException
        {
        // ----------------------------------------------------------------------
        // Use Scanner to read in the text file
        instructionFile = resource.getFilename();
        logger.info( "Reading instructions [" + resource.getURI() + "]" );
        try ( InputStream resourceInputStream = resource.getInputStream() )
            {
            loadInputStream( resourceInputStream );
            }

        musicResource = musicResourceMap.get( musicFile );
        logger.info( "Loaded [{}] [{}] [{}]", resource.getFilename(), musicFile,
                musicResourceMap.keySet().contains( musicFile ) );

        }

    public String getCategory()
        {
        return category;
        }

    public String getInstructionFile()
        {
        return instructionFile;
        }

    public List<Instruction> getInstructions()
        {
        return instructions;
        }

    public String getMusicFile()
        {
        return musicFile;
        }

    public Resource getMusicResource()
        {
        return musicResource;
        }

    public String getTitle()
        {
        return title;
        }

    public boolean isVirtual()
        {
        return isVirtual;
        }

    public void setCategory( String category )
        {
        this.category = category;
        }

    public void setInstructionFile( String instructionFile )
        {
        this.instructionFile = instructionFile;
        }

    public void setMusicResource( Resource musicResource )
        {
        this.musicResource = musicResource;
        }

    public void setTitle( String title )
        {
        this.title = title;
        }

    public void setVirtual( boolean isVirtual )
        {
        this.isVirtual = isVirtual;
        }

    private void loadInputStream( InputStream resourceInputStream )
        {
        Scanner sc = new Scanner( resourceInputStream );

        // ----------------------------------------------------------------------
        // The first line should be where the MP3 file is found
        musicFile = sc.nextLine().trim();

        // ----------------------------------------------------------------------
        // The next line is outdated - it used to cue when the tree was actual
        // or virtual
        setVirtual( Boolean.parseBoolean( sc.nextLine() ) );

        // ----------------------------------------------------------------------
        // Grab the next line. If it has a colon in it, it's an old instruction
        // otherwise, it is newer and has the Title and Category in it
        String check = sc.nextLine().trim();
        if ( check.contains( ":" ) )
            {
            title = instructionFile;
            category = "Miscellaneous";
            instructions.add( new Instruction( check ) );
            }
        else
            {
            title = check;
            category = sc.nextLine().trim();
            }

        // ----------------------------------------------------------------------
        // The rest of the lines are instructions to add
        while ( sc.hasNextLine() )
            {
            instructions.add( new Instruction( sc.nextLine().trim() ) );
            }
        }

    }
