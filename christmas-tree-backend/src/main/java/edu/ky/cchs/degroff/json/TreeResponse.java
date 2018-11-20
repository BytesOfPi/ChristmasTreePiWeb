package edu.ky.cchs.degroff.json;

public class TreeResponse
    {

    private String code;
    private String message;

    public TreeResponse( String code, String message )
        {
        super();
        this.code = code;
        this.message = message;
        }

    public String getCode()
        {
        return code;
        }

    public void setCode( String code )
        {
        this.code = code;
        }

    public String getMessage()
        {
        return message;
        }

    public void addTreeResponse( TreeResponse response )
        {
        this.code = response.getCode();
        this.message += response.getMessage();
        }

    public void setMessage( String message )
        {
        this.message = message;
        }

    public TreeResponse()
        {
        // TODO Auto-generated constructor stub
        }

    }
