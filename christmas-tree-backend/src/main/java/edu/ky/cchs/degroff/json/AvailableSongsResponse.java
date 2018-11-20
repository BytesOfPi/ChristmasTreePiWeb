package edu.ky.cchs.degroff.json;

import java.util.List;

public class AvailableSongsResponse
    {
    public AvailableSongsResponse( List<AvailableSong> songs )
        {
        super();
        this.songs = songs;
        }

    public List<AvailableSong> getSongs()
        {
        return songs;
        }

    public void setSongs( List<AvailableSong> songs )
        {
        this.songs = songs;
        }

    private List<AvailableSong> songs;

    public AvailableSongsResponse()
        {
        // TODO Auto-generated constructor stub
        }

    }
