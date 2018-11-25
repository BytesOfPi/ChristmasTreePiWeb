package edu.ky.cchs.degroff.model;

import java.util.ArrayList;
import java.util.List;

import edu.ky.cchs.degroff.pi.ITree;

public class Instruction
    {

    long time;
    List<Integer> strandOn = new ArrayList<>();
    List<Integer> strandOff = new ArrayList<>();

    public Instruction( String input )
        {
        // ----------------------------------------------------------------------
        // Split the instruction string into 3 pieces:
        // 1) The time of the instruction
        // 2) The strands to turn on
        // 3) The strands to turn off
        input = input.replaceAll( "\t", ":" );
        String[] instruction = input.split( ":" );
        time = Long.parseLong( instruction[0] );

        // ----------------------------------------------------------------------
        // You may not turn on any lights... if so, return early, otherwise...
        // add to the list of lights to turn ON
        if ( instruction.length < 2 ) { return; }
        parseStrands( strandOn, instruction[1] );

        // ----------------------------------------------------------------------
        // You may not turn off any lights... if so, return early, otherwise...
        // add to the list of lights to turn OFF
        if ( instruction.length < 3 ) { return; }
        parseStrands( strandOff, instruction[2] );

        }

    public void parseStrands( List<Integer> strands, String val )
        {
        // ----------------------------------------------------------------------
        // If the values are empty, don't add anything to this list
        if ( val == null || val.trim().isEmpty() ) { return; }
        // ----------------------------------------------------------------------
        // Otherwise parse the value and add to the instruction strands
        String[] strandVals = val.split( "," );
        for ( String strand : strandVals )
            {
            strands.add( Integer.parseInt( strand ) );
            }
        }

    public long getTime()
        {
        return time;
        }

    public void setTree( ITree tree )
        {
        // ----------------------------------------------------------------------
        // Loop through the strands turning the appropriate on and off
        for ( Integer strand : strandOn )
            {
            tree.turnStrandOn( strand );
            }
        for ( Integer strand : strandOff )
            {
            tree.turnStrandOff( strand );
            }
        // ----------------------------------------------------------------------
        // Refresh the tree (if applicable)
        tree.refresh();
        }

    }
