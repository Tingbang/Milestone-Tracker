package wpd2.lab2;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Milestone
{
    //attributes
    private String name;
    private String description;
    private Date intendedDueDate;
    private Date actualCompletionDate;
    private String password;
    private String email;
    private boolean complete;


    // constructors
    public Milestone()
    {
        this.name = "";
        this.description = "";
        this.intendedDueDate = new Date();
        this.actualCompletionDate = new Date();
        this.password = "";
        this.email = "";
        this.complete = true;
    }

    public Milestone(String nameIn, String descriptionIn, String dueDateIn)
    {
        this.name = nameIn;
        this.description = descriptionIn;

        try {
            this.intendedDueDate = new SimpleDateFormat("dd/MM/yyyy").parse(dueDateIn);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        this.actualCompletionDate = new Date();
    }

    // methods
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public boolean getComplete() {
        return complete;
    }


    public String getDescription() {
        return description;
    }

    public Date getIntendedDueDate() {
        return intendedDueDate;
    }

    public Date getActualCompletionDate() {
        return actualCompletionDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIntendedDueDate(Date intendedDueDate) {
        this.intendedDueDate = intendedDueDate;
    }

    public void setActualCompletionDate(Date actualCompletionDate) {
        this.actualCompletionDate = actualCompletionDate;
    }
}