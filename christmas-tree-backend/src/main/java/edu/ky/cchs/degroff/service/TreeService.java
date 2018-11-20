package edu.ky.cchs.degroff.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import edu.ky.cchs.degroff.audio.Audio;
import edu.ky.cchs.degroff.json.AvailableSong;
import edu.ky.cchs.degroff.json.AvailableSongsResponse;
import edu.ky.cchs.degroff.json.TreeResponse;
import edu.ky.cchs.degroff.model.Instruction;
import edu.ky.cchs.degroff.model.MusicSet;
import edu.ky.cchs.degroff.pi.ITree;
import edu.ky.cchs.degroff.pi.TreePi;
import edu.ky.cchs.degroff.pi.TreeVirtual;
import edu.ky.cchs.degroff.util.TreeResourceUtil;
import javazoom.jl.player.Player;

@ConfigurationProperties( prefix = "tree-service" )
@Service
public class TreeService
    {
    Logger logger = LoggerFactory.getLogger( TreeService.class );

    private boolean virtual;
    private List<MusicSet> sets;

    private long milliSync = 0;

    private ITree tree;

    private Future runningInstruct = null;
    private Future runningSong = null;

    @Autowired
    private TreeResourceUtil resourceUtil;

    @Autowired
    private ThreadPoolExecutor songQueue;

    @Bean
    public ThreadPoolExecutor getThreadPool()
        {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool( 1 );
        }

    public TreeResponse playSong( int id )
        {
        MusicSet set = sets.get( id - 1 );
        logger.info( "About to run [{}][{}]", id, set.getMusicFile() );

        // ----------------------------------------------------------------------
        // Stop the last running song...
        stopLastRun();

        // ----------------------------------------------------------------------
        // Start playing the new song
        // songQueue = getThreadPool();
        runningInstruct = songQueue.submit( () -> runSetThread( set ) );

        return new TreeResponse( "200", "Playing: " + set.getMusicFile() );
        }

    private void stopLastRun()
        {
        // ----------------------------------------------------------------------
        // Get Audio Player and stop playing music
        Player play = Audio.getPlayer();
        if ( play != null && !play.isComplete() )
            {
            logger.info( "Stopping last song..." );
            play.close();
            }
        // ----------------------------------------------------------------------
        // Stop the Song future and Instruction future
        stopRunningProcess( runningSong, "Song" );
        stopRunningProcess( runningInstruct, "Instruct" );
        songQueue.purge();
        }

    private void stopRunningProcess( Future future, String msg )
        {
        // ----------------------------------------------------------------------
        // If song is running, stop it...
        if ( future != null )
            {
            logger.info( "HEY [{}][{}][{}]", msg, future.isCancelled(), future.isDone() );
            }
        if ( future != null && (!future.isDone() || !future.isCancelled()) )
            {
            logger.info( "Trying to cancel [{}][{}]", future.isCancelled(), future.isDone() );
            future.cancel( true );
            try
                {
                Thread.sleep( 1000 );
                }
            catch ( InterruptedException e )
                {
                // TODO Auto-generated catch block
                e.printStackTrace();
                }
            logger.info( "Is thread cancelled? [{}][{}]", future.isCancelled(), future.isDone() );
            }
        }

    private void runSetThread( MusicSet set )
        {
        // ----------------------------------------------------------------------
        // Pull off the first instruction
        System.out.println( "Running music/light set [" + set.getInstructionFile() + "]" );
        int iCnt = 0;
        List<Instruction> instructions = set.getInstructions();
        Instruction nextInstruct = instructions.get( iCnt++ );
        Long nextTime = nextInstruct.getTime();

        // ----------------------------------------------------------------------
        // Start the timer
        long startTime = System.currentTimeMillis() + milliSync;
        // ----------------------------------------------------------------------
        // Start playing the right file
        runningSong = Audio.playMP3New( set.getMusicResource(), runningSong );

        // ----------------------------------------------------------------------
        // Continue to loop while there are instructions
        while ( iCnt <= instructions.size() )
            {
            // ----------------------------------------------------------------------
            // if the next instruction is now or passed...
            if ( nextTime <= System.currentTimeMillis() - startTime )
                {
                // ----------------------------------------------------------------------
                // Only check if thread is interrupted if a new instruction is to be
                // carried out so it doesn't slow down
                if ( Thread.currentThread().isInterrupted() )
                    {
                    logger.info( "Breaking early from [{}]", set.getInstructionFile() );
                    break;
                    }
                Long holdTime = nextTime;
                // ----------------------------------------------------------------------
                // Execute instructions
                logger.info( "Run next instructions" );
                nextInstruct.setTree( tree );
                // ----------------------------------------------------------------------
                // Get Next Instruction
                nextInstruct = instructions.get( iCnt++ );
                nextTime = nextInstruct.getTime() + milliSync;
                }

            }
        logger.info( "DONE INSTRUCT [{}]", set.getMusicFile() );

        }

    public TreeResponse toggleLight( int channel )
        {
        StringBuilder msg = new StringBuilder( "Channel [" ).append( channel ).append( "] is turned " );
        if ( tree.isStrandOn( channel ) )
            {
            tree.turnStrandOff( channel );
            msg.append( "OFF" );
            }
        else
            {
            tree.turnStrandOn( channel );
            msg.append( "ON" );
            }
        logger.info( msg.toString() );
        return new TreeResponse( "200", msg.toString() );
        }

    public AvailableSongsResponse getSongs()
        {
        logger.info( "Getting songs..." );
        // ---------------------------------------------------------------
        // Loop through MusicSets and get Available songs
        AtomicInteger iCnt = new AtomicInteger( 1 );
        List<AvailableSong> songs = sets.stream().map(
                set -> new AvailableSong( set.getInstructionFile(), set.getInstructionFile(), iCnt.getAndIncrement() ) )
                .collect( Collectors.toList() );

        // ---------------------------------------------------------------
        // Return list
        return new AvailableSongsResponse( songs );

        }

    public void startup()
        {
        logger.info( "Initializing... " );
        // ---------------------------------------------------------------
        // Initialize Tree
        tree = (virtual) ? new TreeVirtual() : new TreePi();
        tree.init();

        // ---------------------------------------------------------------
        // Load up music
        loadMusicSets();

        logger.info( "Initialization Complete " );
        }

    private void loadMusicSets()
        {
        // ---------------------------------------------------------------
        // Load instructions
        sets = new ArrayList<>();
        Map<String, Resource> instructResources = resourceUtil.getInstructResources();
        Map<String, Resource> musicResources = resourceUtil.getMusicResources();
        instructResources.entrySet().stream().forEach( entry -> {
        try
            {
            sets.add( new MusicSet( entry.getValue(), musicResources ) );
            }
        catch ( IOException e )
            {
            logger.error( "Trouble loading resources", e );
            }
        } );
        }

    public void shutdown()
        {
        logger.info( "Shutting down... " );
        tree.shutdown();
        logger.info( "Shutting down Complete " );
        }

    public boolean isVirtual()
        {
        return virtual;
        }

    public void setVirtual( boolean virtual )
        {
        this.virtual = virtual;
        }

    public TreeResponse milliSync( long milliSync )
        {
        StringBuilder msg = new StringBuilder( "Added [" ).append( milliSync ).append( "] milliseconds to pad " );
        this.milliSync = milliSync;
        logger.info( msg.toString() );
        return new TreeResponse( "200", msg.toString() );
        }

    public TreeResponse refresh()
        {
        StringBuilder msg = new StringBuilder( "Refreshsed music sets " );
        loadMusicSets();
        logger.info( msg.toString() );
        return new TreeResponse( "200", msg.toString() );
        }
    }
