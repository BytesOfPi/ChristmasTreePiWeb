package edu.ky.cchs.degroff.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Service;

@ConfigurationProperties( prefix = "tree-service" )
@Service
public class TreeResourceUtil
    {
    Logger logger = LoggerFactory.getLogger( TreeResourceUtil.class );

    private List<String> loadMusic;
    private List<String> loadInstruct;
    private ResourceLoader resourceLoader;

    @Autowired
    public TreeResourceUtil( ResourceLoader resourceLoader )
        {
        this.resourceLoader = resourceLoader;
        }

    public Map<String, Resource> getInstructResources()
        {
        return getResourceMap( loadInstruct );
        }

    public Map<String, Resource> getMusicResources()
        {
        return getResourceMap( loadMusic );
        }

    private Map<String, Resource> getResourceMap( List<String> loadClasspath )
        {
        // Loop through classpaths and get Resource[]
        return loadClasspath.stream().map( classpath -> getResources( classpath ) )
                // Change Resource[] to List then stream
                .map( Arrays::asList ).flatMap( Collection::stream )
                // Collect all found resources in a map
                .collect( Collectors.toMap( r -> r.getFilename(), r -> r, ( res1, res2 ) -> {
                logger.info( "dup key found" );
                return res1;
                } ) );

        }

    // public static InputStream getResource( String val ) throws IOException
    // {
    // System.out.println( "Attempting to get resource [" + val + "]" );
    // Resource resource = new ClassPathResource( val );
    // if ( resource.exists() ) { return resource.getInputStream(); }
    // File initialFile = new File( val );
    // return new FileInputStream( initialFile );
    //
    // }

    private Resource[] getResources( String pattern )
        {
        try
            {
            return ResourcePatternUtils.getResourcePatternResolver( resourceLoader ).getResources( pattern );
            }
        catch ( IOException ioe )
            {
            logger.info( "Trouble getting resources for [{}]", pattern );
            }
        return new Resource[] {};
        }

    public List<String> getLoadMusic()
        {
        return loadMusic;
        }

    public void setLoadMusic( List<String> loadMusic )
        {
        this.loadMusic = loadMusic;
        }

    public List<String> getLoadInstruct()
        {
        return loadInstruct;
        }

    public void setLoadInstruct( List<String> loadInstruct )
        {
        this.loadInstruct = loadInstruct;
        }
    }
