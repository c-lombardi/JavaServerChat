import java.io.Serializable;

/**
 * Created by Christopher on 4/13/2014.
 */
public class Record implements Serializable {
    private int id;
    private String rep;
    private String location;
    private int amount;
    public Record ()
    {
    }
    public Record (String r, String l, int a)
    {
        this.rep = r;
        this.location = l;
        this.amount = a;
    }
    public Record (int i, String r, String l, int a)
    {
        this.amount = a;
        this.id = i;
        this.rep = r;
        this.location = l;
    }
    public String getRep()
    {
        return this.rep;
    }
    public String getLocation()
    {
        return this.location;
    }
    public Integer getId()
    {
        return this.id;
    }
    public Integer getAmount()
    {
        return this.amount;
    }
    public void setId(int i)
    {
        this.id = i;
    }
    public void setLocation(String l)
    {
        this.location = l;
    }
    public void setRep(String r)
    {
        this.rep = r;
    }
    public void setAmount(Integer a)
    {
        this.amount = a;
    }
}
