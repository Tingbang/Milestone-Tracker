package wpd2.lab2;

import java.util.List;
import java.util.ArrayList;

public class MilestonePlanner
{
    // attributes
    private int ID;
    private String name;
    private List<Milestone> milestones;


    // constructors
    public MilestonePlanner()
    {
        this.milestones = new ArrayList<>();
    }

    public MilestonePlanner(String nameIn)
    {
        this.ID = 0;
        this.name = nameIn;
        this.milestones = new ArrayList<>();
    }


    // methods
    public int getID(){return this.ID;}

    public String getName()
    {
        return this.name;
    }

    public List<Milestone> getMilestones()
    {
        return this.milestones;
    }

    public void setID(int ID){this.ID = ID;}

    public void setName(String n)
    {
        this.name = n;
    }

    public void setMilestones(List<Milestone> t)
    {
        this.milestones = t;
    }

    public String toString()
    {
        return ("name: " + name + ", milestones: " + milestones.toString());
    }

    public void addMilestone(Milestone milestoneIn)
    {
        milestones.add(milestoneIn);
    }

    public void removeMilestone(String milestoneIn)
    {
        milestones.remove(milestoneIn);
    }

}