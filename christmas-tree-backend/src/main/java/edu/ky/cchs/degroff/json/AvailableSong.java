package edu.ky.cchs.degroff.json;

public class AvailableSong
    {

    private String name;
    private String title;
    private String category;
    private int id;

    public AvailableSong( String name, String title, String category, int id )
        {
        super();
        this.name = name;
        this.title = title;
        this.category = category;
        this.id = id;
        }

    public String getCategory()
        {
        return category;
        }

    public void setCategory( String category )
        {
        this.category = category;
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
