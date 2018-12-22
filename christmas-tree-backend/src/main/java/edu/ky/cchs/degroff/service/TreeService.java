package edu.ky.cchs.degroff.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * The Class TreeService.
 */
@ConfigurationProperties( prefix = "tree-service" )
@Service
public class TreeService
    {
    private static final long REPEAT_WAIT_MILLIS = 2000;
    private static final int SYNC_MILLIS_WAIT_TIME = 2000;
    private static final int SYNC_MAX_TRIES = 10;

    /** The logger. */
    private Logger logger = LoggerFactory.getLogger( TreeService.class );

    /** The virtual. */
    private List<Integer> repeatIds = new ArrayList<>();
    private int repeatCountdown = 0;

    /** The virtual. */
    private boolean virtual;

    /** The sets. */
    private List<MusicSet> sets;

    /** The milli sync. */
    private long milliSync;

    /** The tree. */
    private ITree tree;

    /** The running song. */
    private Future runningSong = null;

    /** The running instructions. */
    private List<Future> runningInstructions = new ArrayList<>();

    /** The resource util. */
    @Autowired
    private TreeResourceUtil resourceUtil;

    /** The song queue. */
    @Autowired
    private ThreadPoolExecutor songQueue;

    /**
     * Generate a new thread pool
     *
     * @return the thread pool executor
     */
    @Bean
    public ThreadPoolExecutor genThreadPool()
        {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool( 1 );
        }

    public long getMilliSync()
        {
        return milliSync;
        }

    /**
     * Gets the songs.
     *
     * @return the songs
     */
    public AvailableSongsResponse getSongs()
        {
        logger.info( "Getting songs..." );
        // ---------------------------------------------------------------
        // Loop through MusicSets and get Available songs
        AtomicInteger iCnt = new AtomicInteger( 1 );
        List<AvailableSong> songs = sets.stream()
                // ---------------------------------------------------------------
                // Map Set to JSON Response
                .map( set -> new AvailableSong( set.getInstructionFile(), set.getTitle(), set.getCategory(),
                        iCnt.getAndIncrement() ) )
                .collect( Collectors.toList() );

        // ---------------------------------------------------------------
        // Return list
        return new AvailableSongsResponse( songs );

        }

    /**
     * Checks if is virtual.
     *
     * @return true, if is virtual
     */
    public boolean isVirtual()
        {
        return virtual;
        }

    /**
     * Milli sync.
     *
     * @param milliSync
     *            the milli sync
     * @return the tree response
     */
    public TreeResponse milliSync( long milliSync )
        {
        StringBuilder msg = new StringBuilder( "Added [" ).append( milliSync ).append( "] milliseconds to pad " );
        this.milliSync = milliSync;
        logger.info( msg.toString() );
        return new TreeResponse( "200", msg.toString() );
        }

    /**
     * Play list.
     *
     * @param ids
     *            the ids
     * @return the tree response
     */
    public TreeResponse playList( List<Integer> ids, boolean repeat )
        {
        // ----------------------------------------------------------------------
        // Stop the last running song...
        stopLastRun();

        // ----------------------------------------------------------------------
        // If queue is taking too long to terminate, get a new thread pool
        if ( songQueue.getActiveCount() > 0 )
            {
            songQueue = genThreadPool();
            }

        // ----------------------------------------------------------------------
        // If we're repeating, set values
        if ( repeat && ids.size() > 1 )
            {
            repeatCountdown = ids.size();
            repeatIds = ids;
            }
        // ----------------------------------------------------------------------
        // otherwise, make sure to NOT repeat
        else
            {
            repeatCountdown = 0;
            repeatIds.clear();
            }

        // ----------------------------------------------------------------------
        // Start playing the new song
        ids.stream().forEach( id -> {
        // ----------------------------------------------------------------------
        // Pull back song (MusicSet)
        // Spin off a new thread in Song Queue
        // Add the Future to the list
        MusicSet set = sets.get( id - 1 );
        logger.info( "Loading playlist song [{}][{}]", id, set.getMusicFile() );
        runningInstructions.add( songQueue.submit( () -> runSetThread( set ) ) );
        } );

        return new TreeResponse( "200", "Loaded songs into queue." );
        }

    /**
     * Play a single song. This method creates a playlist of 1 given the ID Make
     * sure NOT to repeat
     *
     * @param id
     *            the id
     * @return the tree response
     */
    public TreeResponse playSong( int id )
        {
        return playList( Arrays.asList( new Integer[] { Integer.valueOf( id ) } ), false );
        }

    /**
     * Refresh.
     *
     * @return the tree response
     */
    public TreeResponse refresh()
        {
        logger.info( "Millisync {}", milliSync );
        StringBuilder msg = new StringBuilder( "Refreshsed music sets " );
        loadMusicSets();
        logger.info( msg.toString() );
        return new TreeResponse( "200", msg.toString() );
        }

    public void setMilliSync( long milliSync )
        {
        this.milliSync = milliSync;
        }

    /**
     * Sets the virtual.
     *
     * @param virtual
     *            the new virtual
     */
    public void setVirtual( boolean virtual )
        {
        this.virtual = virtual;
        }

    /**
     * Shutdown.
     */
    public void shutdown()
        {
        logger.info( "Shutting down... " );
        if ( tree != null ) tree.shutdown();
        logger.info( "Shutting down Complete " );
        }

    /**
     * Startup.
     */
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

    /**
     * Stop last run.
     *
     * This method will attempt to: 1) Stop the currently running JPlayer 2)
     * Interrupt the thread it's running on if it's still running 3) Loop through
     * all the queued instruction threads and interrupt 4) Clear out the instruction
     * queue.
     *
     * @return the tree response
     */
    public TreeResponse stopLastRun()
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
        // Stop the Song future
        stopRunningProcess( runningSong, "Song" );

        // ----------------------------------------------------------------------
        // Loop backward through the queued instructions and stop them
        // Once they are done, clear out the queue
        for ( int i = runningInstructions.size() - 1; i >= 0; i-- )
            {
            stopRunningProcess( runningInstructions.get( i ), "Instruct" );
            }
        runningInstructions.clear();
        songQueue.purge();
        songQueue.getQueue().clear();

        return new TreeResponse( "200", "Stopped song " );
        }

    public void stopPlayList()
        {
        // ----------------------------------------------------------------------
        // Wipe out last playlist repeat songs so they don't repeat
        repeatIds.clear();
        }

    /**
     * Toggle light.
     *
     * @param channel
     *            the channel
     * @return the tree response
     */
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

    /**
     * Check to see if we need to repeat playlist
     */
    private void checkRepeat()
        {
        // ----------------------------------------------------------------------
        // If we're not repeating, ignore
        if ( repeatIds.isEmpty() ) { return; } // return early

        // ----------------------------------------------------------------------
        // If we're at the last song, spin off a thread that waits
        if ( --repeatCountdown <= 0 )
            {
            logger.info( "Trigger REPEAT PLAYLIST" );

            // ----------------------------------------------------------------------
            // Spin a thread seconds in the future to kick off playlist again while
            // this thread completes
            ThreadPoolExecutor temp = genThreadPool();
            temp.submit( () -> {
            threadSleep( REPEAT_WAIT_MILLIS );
            this.playList( repeatIds, true );
            } );
            temp.shutdown();
            }
        }

    /**
     * Load music sets.
     */
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

    /**
     * Run set thread.
     *
     * @param set
     *            the set
     */
    private void runSetThread( MusicSet set )
        {
        // ----------------------------------------------------------------------
        // Pull off the first instruction
        logger.info( "Running music/light set [" + set.getInstructionFile() + "]" );
        if ( Thread.currentThread().isInterrupted() )
            {
            logger.info( "Instruction terminated early" );
            return;
            }

        List<Instruction> instructions = set.getInstructions();
        int iCnt = 0, iSize = instructions.size();
        Instruction nextInstruct = instructions.get( iCnt++ );
        Long nextTime = nextInstruct.getTime();

        // ----------------------------------------------------------------------
        // Start playing the right file
        runningSong = Audio.playMP3New( set.getMusicResource() );

        // ----------------------------------------------------------------------
        // Start the timer
        long startTime = System.currentTimeMillis();
        int playerSync = 0;
        boolean interrupted = false;

        // ----------------------------------------------------------------------
        // Continue to loop while there are instructions
        while ( iCnt < iSize )
            {
            // ----------------------------------------------------------------------
            // If this is the first time and we haven't synched music to start of run
            // let's do that...
            if ( playerSync == 0 )
                {
                startTime = System.currentTimeMillis();
                playerSync = Audio.getTime();
                if ( playerSync != 0 )
                    {
                    startTime -= playerSync;
                    }
                }
            // ----------------------------------------------------------------------
            // if the next instruction is now or passed...
            if ( nextTime <= System.currentTimeMillis() - startTime )
                {
                // ----------------------------------------------------------------------
                // Execute instructions
                nextInstruct.setTree( tree );
                // ----------------------------------------------------------------------
                // Get Next Instruction
                nextInstruct = instructions.get( iCnt++ );
                nextTime = nextInstruct.getTime() + milliSync;
                // ----------------------------------------------------------------------
                // Only check if thread is interrupted if a new instruction is to be
                // carried out so it doesn't slow down
                if ( Thread.currentThread().isInterrupted() )
                    {
                    interrupted = true;
                    logger.info( "Breaking early from [{}]", set.getInstructionFile() );
                    break;
                    }
                }
            }
        logger.info( "DONE INSTRUCT [{}]", set.getMusicFile() );

        // ----------------------------------------------------------------------
        // Sync back with music thread
        syncMusic( interrupted );

        // ----------------------------------------------------------------------
        // If at last song and we have a repeat list, call playlist again
        checkRepeat();
        }

    /**
     * Stop running process.
     *
     * @param future
     *            the future
     * @param msg
     *            the msg
     */
    private void stopRunningProcess( Future future, String msg )
        {
        // ----------------------------------------------------------------------
        // If song is running, stop it...
        if ( future != null && (!future.isDone() || !future.isCancelled()) )
            {
            logger.info( "Trying to cancel [{}][{}]", future.isCancelled(), future.isDone() );
            future.cancel( true );
            threadSleep( 1000 );
            }
        }

    private void syncMusic( boolean interrupted )
        {
        // ----------------------------------------------------------------------
        // If we were interrupted or music is done, return early
        if ( interrupted ) { return; }

        int cnt = 0;
        // ----------------------------------------------------------------------
        // While Music hasn't finished and we've not tried enough times
        // sleep
        while ( !Audio.isComplete() && cnt++ < SYNC_MAX_TRIES )
            {
            logger.info( "Sync try [{}]", cnt );
            threadSleep( SYNC_MILLIS_WAIT_TIME );
            }

        }

    private void threadSleep( long millis )
        {
        try
            {
            Thread.sleep( millis );
            }
        catch ( InterruptedException e )
            {
            logger.error( "Thread sleep interrupted", e );
            Thread.currentThread().interrupt();
            }
        }
    }
