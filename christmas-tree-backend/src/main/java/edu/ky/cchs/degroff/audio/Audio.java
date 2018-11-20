package edu.ky.cchs.degroff.audio;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javazoom.jl.decoder.JavaLayerException;

public class Audio
    {
    private static Logger logger = LoggerFactory.getLogger( Audio.class );

    private static javazoom.jl.player.Player player;

    public static javazoom.jl.player.Player getPlayer()
        {
        return player;
        }

    public static Future playMP3New( Resource mp3Resource, Future runningSong )
        {
        logger.info( "Running new song [{}]", mp3Resource.getFilename() );
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool( 1 );
        // if ( runningSong != null && !runningSong.isDone() &&
        // !runningSong.isCancelled() && player != null )
        // {
        // System.out.println( "Cancelling song" );
        // player.close();
        // runningSong.cancel( true );
        // System.out.println( "DONE Cancelling song" );
        // }

        Future f = executor.submit( () -> {
        try ( InputStream mp3In = mp3Resource.getInputStream() )
            {
            player = new javazoom.jl.player.Player( mp3In );
            player.play();
            logger.info( "DONE MUSIC [{}]", mp3Resource.getFilename() );
            }
        catch ( JavaLayerException | IOException ex )
            {
            ex.printStackTrace();
            }
        } );
        executor.shutdown();
        return f;
        }

    public static Future playMP3New( Resource mp3Resource )
        {
        return playMP3New( mp3Resource, null );
        }

    }
