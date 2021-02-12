package wpd2.lab2.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wpd2.lab2.MilestonePlanner;
import wpd2.lab2.Milestone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;

public class IncompletePlannerServlet extends BaseServlet
{
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(IncompletePlannerServlet.class);
    private static final String INCOMPLETE_PLANNER_TEMPLATE = "IP.mustache.html";
    private static final long serialVersionUID = -7461821901454655091L;

    static final String USER="";
    static final String PASS="";
    static final String DB_URL= "jdbc:h2:~/milestoneplannerdb";
    Connection conn = null;
    MilestonePlanner ip;
    Milestone milestone;


    static String getCurrentSession(HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);
        return session.getAttribute("userName").toString();
    }

    public IncompletePlannerServlet()
    {
        ip = new MilestonePlanner("List of Incomplete Milestones for WPD2 CW");
    }

    //right now, setting the data for the page by hand, later that comes from a data store
    private Object getObject()
    {
        return ip;
    }
    private Object getObject2()
    {
        return milestone;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        //connect to the database

        try {
            ip = new MilestonePlanner();
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            String sql = "SELECT * FROM MILESTONE_PLANNER";//" WHERE USER_NAME ='" + getCurrentSession(request) + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if(rs.next()){

                System.out.println("Works1");
                ip.setID(rs.getInt("id"));
                ip.setName(rs.getString("goal_name"));


            }
            System.out.println(ip.getName());
            stmt.close();
            System.out.println("end statement 1");

            //Start statement 2

            String sql1 = "SELECT * FROM MILESTONE WHERE PLANNER_ID = '" + ip.getID() + "' AND complete = false";
            Statement stmt2 = conn.createStatement();
            ResultSet rs2 = stmt2.executeQuery(sql1);

            while(rs2.next()){
                System.out.println("Works 2");
                milestone = new Milestone();

                milestone.setDescription(rs2.getString("milestone_desc"));
                milestone.setName(rs2.getString("milestone_title"));
                milestone.setIntendedDueDate(rs2.getDate("due_date"));
                ip.addMilestone(milestone);

                System.out.println(milestone.getDescription());


            }
            stmt2.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        showView(response, INCOMPLETE_PLANNER_TEMPLATE, getObject());


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String topic = request.getParameter("milestoneName");

        //ip.ListMilestones(topic);
        response.sendRedirect("/IncompletePlanner");
    }
}