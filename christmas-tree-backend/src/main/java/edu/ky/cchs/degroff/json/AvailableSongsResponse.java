package edu.ky.cchs.degroff.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvailableSongsResponse
    {
    Map<String, List<AvailableSong>> categories;

    public Map<String, List<AvailableSong>> getCategories()
        {
        return categories;
        }

    public AvailableSongsResponse( List<AvailableSong> songs )
        {
        super();
        categories = new HashMap<>();
        loadSongs( songs );
        }

    public void setSongs( List<AvailableSong> songs )
        {
        loadSongs( songs );
        }

    private void loadSongs( List<AvailableSong> songs )
        {
        songs.stream()
                // Loop through songs.
                .forEach( song -> {
                // Get category if it exists
                List<AvailableSong> category = categories.get( song.getCategory() );
                // If category doesn't exist create and store it
                if ( category == null )
                    {
                    category = new ArrayList<>();
                    categories.put( song.getCategory(), category );
                    }
                // Add song to category
                category.add( song );
                } );
        }

    }
