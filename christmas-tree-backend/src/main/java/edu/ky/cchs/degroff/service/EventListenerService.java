package edu.ky.cchs.degroff.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class EventListenerService
    {
    Logger logger = LoggerFactory.getLogger( EventListenerService.class );

    @Autowired
    private TreeService svc;

    @EventListener
    public void onStartup( ApplicationReadyEvent event )
        {
        logger.info( "Application starting up..." );
        svc.startup();
        }

    @EventListener
    public void onShutdown( ContextClosedEvent event )
        {
        logger.info( "Application shutting down..." );
        svc.shutdown();
        }

    }
