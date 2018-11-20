package edu.ky.cchs.degroff.pi;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeVirtual implements ITree
    {
    private Logger logger = LoggerFactory.getLogger( TreeVirtual.class );
    private List<Boolean> lights;

    public TreeVirtual()
        {
        lights = Arrays.asList( new Boolean[] { false, false, false, false, false, false, false, false } );
        }

    @Override
    public void init()
        {
        // TODO Auto-generated method stub

        }

    @Override
    public void turnStrandOn( int channel )
        {
        // logger.info( "Turning [{}] ON", channel );
        lights.set( channel - 1, true );

        }

    @Override
    public void turnStrandOff( int channel )
        {
        // logger.info( "Turning [{}] OFF", channel );
        lights.set( channel - 1, false );
        }

    @Override
    public boolean isStrandOn( int channel )
        {
        return lights.get( channel - 1 );
        }

    @Override
    public void refresh()
        {
        // TODO Auto-generated method stub

        }

    @Override
    public void shutdown()
        {
        // TODO Auto-generated method stub

        }

    }
