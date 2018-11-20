package edu.ky.cchs.degroff.pi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

@Component
public class TreePi implements ITree
    {

    private List<GpioPinDigitalOutput> channelPinMap = null;
    private GpioController gpio;
    // private List<Pin> pinMap = Arrays.asList( new Pin[] { RaspiPin.GPIO_02,
    // RaspiPin.GPIO_03, RaspiPin.GPIO_04,
    // RaspiPin.GPIO_14, RaspiPin.GPIO_15, RaspiPin.GPIO_17, RaspiPin.GPIO_18,
    // RaspiPin.GPIO_27 } );

    private List<Pin> pinMap = Arrays.asList( new Pin[] { RaspiPin.GPIO_08, RaspiPin.GPIO_09, RaspiPin.GPIO_07,
            RaspiPin.GPIO_15, RaspiPin.GPIO_16, RaspiPin.GPIO_00, RaspiPin.GPIO_01, RaspiPin.GPIO_02 } );

    public TreePi()
        {
        // TODO Auto-generated constructor stub
        }

    @Override
    public void init()
        {
        // Initialize controller and list of pins
        gpio = GpioFactory.getInstance();
        channelPinMap = new ArrayList<>();

        // -----------------------------------------------------------------------
        // Initialize the pins
        for ( Pin pin : pinMap )
            {
            channelPinMap.add( createPin( pin, pin.getName() ) );
            }
        }

    private GpioPinDigitalOutput createPin( Pin pin, String name )
        {
        // provision gpio pin and turn off
        final GpioPinDigitalOutput gpioPDO = gpio.provisionDigitalOutputPin( pin, name, PinState.LOW );

        // set shutdown state for this pin
        gpioPDO.setShutdownOptions( true, PinState.LOW );

        return gpioPDO;
        }

    @Override
    public void turnStrandOn( int channel )
        {
        if ( channel < 0 || channel > channelPinMap.size() ) { return; }

        GpioPinDigitalOutput gpiodo = channelPinMap.get( channel - 1 );
        gpiodo.setState( PinState.HIGH );
        System.out.println( gpiodo.getName() + ": ON" );
        // try
        // {
        // Thread.sleep( 80 );
        // }
        // catch ( InterruptedException e )
        // {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        }

    @Override
    public void turnStrandOff( int channel )
        {
        if ( channel < 0 || channel > channelPinMap.size() ) { return; }

        GpioPinDigitalOutput gpiodo = channelPinMap.get( channel - 1 );

        gpiodo.setState( PinState.LOW );
        System.out.println( gpiodo.getName() + ": OFF" );
        // try
        // {
        // Thread.sleep( 80 );
        // }
        // catch ( InterruptedException e )
        // {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        }

    @Override
    public void refresh()
        {
        // for Swing only... not needed for Pi. Strand changes happen instantly.
        ;
        }

    @Override
    public void shutdown()
        {
        // TODO Auto-generated method stub
        gpio.shutdown();
        }

    @Override
    public boolean isStrandOn( int channel )
        {
        GpioPinDigitalOutput gpiodo = channelPinMap.get( channel - 1 );
        return gpiodo.getState().equals( PinState.HIGH );
        }

    }
