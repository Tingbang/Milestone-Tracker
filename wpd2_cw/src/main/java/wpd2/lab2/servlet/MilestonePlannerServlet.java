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
import java.text.SimpleDateFormat;
import java.util.Date;

public class MilestonePlannerServlet extends BaseServlet {

    static final String USER = "";
    static final String PASS = "";

    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/milestoneplannerdb";
    Connection conn = null;
    Statement stmt = null;

    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(MilestonePlannerServlet.class);
    private static final String MILESTONE_PLANNER_TEMPLATE = "mps.mustache.html";
    private static final long serialVersionUID = -7461821901454655091L;
    MilestonePlanner mp;
    Milestone milestone;


    static String getCurrentSession(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        return session.getAttribute("USERNAME").toString();
    }

    public MilestonePlannerServlet() {
        mp = new MilestonePlanner("List of Milestones for WPD2 CW");
    }

    //right now, setting the data for the page by hand, later that comes from a data store
    private Object getObject() {
        return mp;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // instantiate new milestoneplanner object so milestones aren't replicated
            mp = new MilestonePlanner();

            System.out.println("Connecting to db");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // set milestone planner to the values held in the milestone_planner table according to the currently logged in user
            String sql = "SELECT * FROM MILESTONE_PLANNER WHERE USERNAME ='" + getCurrentSession(request) + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            try {
                // while there are rows in the table add the row values to attributes of the milestone object
                // then add that milestone to the milestone planner object
                if (rs.next()) {
                    mp.setID(rs.getInt("id"));
                    mp.setName(rs.getString("goal_name"));
                }
            } catch (Exception e) {
                System.out.println("Error accessing database." + e);
            }
            stmt.close();

            sql = "SELECT * FROM MILESTONE WHERE PLANNER_ID = '" + mp.getID() + "'";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            try {
                // while there are rows in the table add the row values to attributes of the milestone object
                // then add that milestone to the milestone planner object
                while (rs.next()) {
                    milestone = new Milestone();

                    milestone.setDescription(rs.getString("milestone_desc"));
                    milestone.setName(rs.getString("milestone_title"));
                    milestone.setIntendedDueDate(rs.getDate("due_date"));

                    mp.addMilestone(milestone);
                }
            } catch (Exception e) {
                System.out.println("Error accessing database.");
            }

            stmt.close();
            System.out.println("Connection to db closed");
        } catch (SQLException e) {
            System.out.println(e);
        }

        showView(response, MILESTONE_PLANNER_TEMPLATE, getObject());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // referrer is used to find out "where" a request has come from
        // solution found at: https://stackoverflow.com/questions/2648984/httpservletrequest-how-to-obtain-the-referring-url
        String referrer = request.getHeader("referer");

        // if the request has come from the addmilestone page, it is an addition
        if (referrer.equals("http://localhost:9000/addmilestone.html")) {
            // instantiate new object here instead of globally so that the milestones don't overwrite one another
            milestone = new Milestone();

            // assigning parameters to local variables
            String milestoneName = request.getParameter("milestoneName");
            String milestoneDescription = request.getParameter("milestoneDescription");
            Date milestoneDueDate = new Date();

            // following try/catch is needed to parse the string to a date
            try {
                milestoneDueDate = new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("milestoneDate"));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            // setting the attributes of the milestone object
            milestone.setName(milestoneName);
            milestone.setDescription(milestoneDescription);
            milestone.setIntendedDueDate(milestoneDueDate);

            try {
                Class.forName(JDBC_DRIVER);
                System.out.println("Connecting to db1");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement stmt = conn.createStatement();

                System.out.println("Connection Successful");
                String sql = "INSERT INTO MILESTONE (MILESTONE_TITLE, MILESTONE_DESC, DUE_DATE, PLANNER_ID, COMPLETE) VALUES(?,?,?,?,?)";
                PreparedStatement state = conn.prepareStatement(sql);
                state.setObject(1, milestone.getName());
                state.setObject(2, milestone.getDescription());
                state.setObject(3, milestone.getIntendedDueDate());
                state.setObject(4, mp.getID());
                state.setObject(5, "FALSE");
                state.executeUpdate();

                stmt.close();
                System.out.println("Milestone added");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (referrer.equals("http://localhost:9000/deletemilestone.html")) {
            // instantiate new object here instead of globally so that the milestones don't overwrite one another
            milestone = new Milestone();

            // assigning parameters to local variables
            String milestoneName = request.getParameter("milestoneName");
            milestone.setName(milestoneName);

            try {
                Class.forName(JDBC_DRIVER);
                System.out.println("Connecting to db1");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement stmt = conn.createStatement();

                try {
                    System.out.println("Connection Successful");
                    String sql = "DELETE FROM MILESTONE WHERE MILESTONE_TITLE = ?";
                    PreparedStatement state = conn.prepareStatement(sql);
                    state.setObject(1, milestone.getName());
                    state.executeUpdate();

                    stmt.close();
                    System.out.println("Milestone deleted");

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (referrer.equals("http://localhost:9000/editmilestone.html")) {
            // instantiate new object here instead of globally so that the milestones don't overwrite one another
            milestone = new Milestone();

            // assigning parameters to local variables
            String milestoneName = request.getParameter("milestoneName");
            String milestoneDescription = request.getParameter("milestoneDescription");
            Date milestoneDueDate = new Date();
            Date milestoneActualdate = new Date();
            String select = request.getParameter("chkBox");
            Boolean select1 = true;

            if (select.equals("complete")) {
                select1 = true;
            }
            if (select.equals("incomplete")) {
                select1 = false;
            }
            // following try/catch is needed to parse the string to a date
            try {
                milestoneDueDate = new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("milestoneDate"));
                milestoneActualdate = new SimpleDateFormat("dd/MM/yyyy").parse(request.getParameter("milestoneActualDate"));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            // setting the attributes of the milestone object
            milestone.setName(milestoneName);
            milestone.setDescription(milestoneDescription);
            milestone.setIntendedDueDate(milestoneDueDate);
            milestone.setActualCompletionDate(milestoneActualdate);
            milestone.setComplete(select1);

            try {
                Class.forName(JDBC_DRIVER);
                System.out.println("Connecting to db1");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement stmt = conn.createStatement();

                System.out.println("Connection Successful");
                String sql = "UPDATE MILESTONE SET MILESTONE_TITLE =?, MILESTONE_DESC =?, DUE_DATE =?, PLANNER_ID =?, COMPLETE =?, ACTUAL_DATE =? WHERE MILESTONE_TITLE = ?";
                PreparedStatement state = conn.prepareStatement(sql);
                state.setObject(1, milestone.getName());
                state.setObject(2, milestone.getDescription());
                state.setObject(3, milestone.getIntendedDueDate());
                state.setObject(4, mp.getID());
                state.setObject(5, select1);
                state.setObject(6, milestone.getActualCompletionDate());
                state.setObject(7, milestone.getName());
                state.executeUpdate();

                stmt.close();
                System.out.println("Milestone Updated");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
        response.sendRedirect("/milestoneplanners");
    }
}