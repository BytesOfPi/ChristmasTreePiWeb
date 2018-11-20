package edu.ky.cchs.degroff.pi;

public interface ITree
    {
    public void init();

    public void turnStrandOn( int channel );

    public void turnStrandOff( int channel );

    public boolean isStrandOn( int channel );

    public void refresh();

    public void shutdown();
    }
