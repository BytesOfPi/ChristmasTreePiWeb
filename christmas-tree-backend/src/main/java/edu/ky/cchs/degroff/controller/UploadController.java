package edu.ky.cchs.degroff.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping( "upload" )
public class UploadController
    {
    Logger logger = LoggerFactory.getLogger( UploadController.class );

    @PostMapping( value = "/file" )
    public Map<String, String> submit( @RequestParam( "fileLoad" ) List<MultipartFile> files ) throws IOException
        {
        Map<String, String> response = new HashMap<>();
        if ( files == null || files.isEmpty() )
            {
            logger.info( "No Files sent..." );
            return response;
            }

        for ( MultipartFile file : files )
            {
            logger.info( "File [{}][{}][{}]", file.getContentType(), file.getName(), file.getOriginalFilename() );
            response.put( file.getOriginalFilename(), saveFile( file ) );// saveFile( file );
            }

        logger.info( "Done" );
        return response;
        }

    private String saveFile( MultipartFile file ) throws IOException
        {
        String name = file.getOriginalFilename();
        String path = (file.getContentType().equals( "audio/mp3" )) ? "music/" + name : null;
        path = (file.getContentType().equals( "text/plain" )) ? "instructions/" + name : path;

        if ( path == null ) return "no file loaded";

        File newFile = new File( path );
        if ( newFile.exists() ) return newFile + " already exists";

        FileUtils.writeByteArrayToFile( newFile, file.getBytes() );
        return newFile + " SUCCESS";

        }
    }
