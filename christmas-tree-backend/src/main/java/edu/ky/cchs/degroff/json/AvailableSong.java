package edu.ky.cchs.degroff.json;

public class AvailableSong
    {

    private String name;
    private String title;
    private int id;

    public AvailableSong( String name, String title, int id )
        {
        super();
        this.name = name;
        this.title = title;
        this.id = id;
        }

    public String getName()
        {
        return name;
        }

    public void setName( String name )
        {
        this.name = name;
        }

    public String getTitle()
        {
        return title;
        }

    public void setTitle( String title )
        {
        this.title = title;
        }

    public int getId()
        {
        return id;
        }

    public void setId( int id )
        {
        this.id = id;
        }

    public AvailableSong()
        {
        // TODO Auto-generated constructor stub
        }

    }
