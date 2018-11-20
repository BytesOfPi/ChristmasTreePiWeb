package edu.ky.cchs.degroff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ky.cchs.degroff.json.AvailableSongsResponse;
import edu.ky.cchs.degroff.json.TreeResponse;
import edu.ky.cchs.degroff.service.TreeService;

@RestController
@RequestMapping( "tree" )
public class TreeController
    {

    @Autowired
    private TreeService svc;

    @GetMapping( value = "/maint/refresh" )
    public TreeResponse refresh()
        {
        return svc.refresh();
        }

    @GetMapping( value = "/maint/{milli}" )
    public TreeResponse milliSync( @PathVariable long milliSync )
        {
        return svc.milliSync( milliSync );
        }

    @GetMapping( value = "/toggle/{light}" )
    public TreeResponse toggleLight( @PathVariable int light )
        {
        if ( light > 0 && light < 9 ) { return svc.toggleLight( light ); }

        TreeResponse resp = new TreeResponse();
        for ( int i = 1; i < 9; i++ )
            {
            resp.addTreeResponse( svc.toggleLight( i ) );
            }
        return resp;
        }

    @GetMapping( value = "/song/get" )
    public AvailableSongsResponse getSongs()
        {
        return svc.getSongs();
        }

    @GetMapping( value = "/song/play/{id}" )
    public TreeResponse playSong( @PathVariable int id )
        {
        return svc.playSong( id );
        }

    }
