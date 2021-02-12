package wpd2.lab2.servlet;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wpd2.lab2.Milestone;
import wpd2.lab2.MilestonePlanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ResetPasswordServlet extends BaseServlet{

    static final String USER="";
    static final String PASS="";

    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL= "jdbc:h2:~/milestoneplannerdb";
    Connection conn = null;
    Statement stmt = null;

    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(MilestonePlannerServlet.class);
    private static final String MILESTONE_PLANNER_TEMPLATE = "mps.mustache.html";
    private static final long serialVersionUID = -7461821901454655091L;
    MilestonePlanner mp;
    Milestone milestone;
    String salt = "asd235aiuhasdn92impsi8cn9";


    static String getCurrentSession(HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);
        return session.getAttribute("USER_NAME").toString();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String referrer = request.getHeader("referer");

        if (referrer.equals("http://localhost:9000/forgotConfirmation.html"))
        {

            // instantiate new object here instead of globally so that the milestones don't overwrite one another
            milestone = new Milestone();



            // assigning parameters to local variables
            String password = request.getParameter("pass1");
            String email = request.getParameter("eml");

            password = password.toLowerCase();
            email = email.toLowerCase();

            //Sanatising the inputs
            System.out.println("Safe input:" + password);
            password = Jsoup.clean(password, Whitelist.basic());
            email = Jsoup.clean(email, Whitelist.basic());

            String act_pass = RegistrationServlet.Hash(password + salt);

            // setting the attributes of the milestone object
            milestone.setPassword(act_pass);
            milestone.setEmail(email);

            try
            {
                Class.forName(JDBC_DRIVER);
                System.out.println("Connecting to db1");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement stmt = conn.createStatement();

                System.out.println("Connection Successful");
//                String sql = "UPDATE MILESTONE SET (MILESTONE_TITLE, MILESTONE_DESC, DUE_DATE, PLANNER_ID, COMPLETE) VALUES(?,?,?,?,?) WHERE MILESTONE_TITLE = ?";
                String sql = "UPDATE USER SET PASSWORD =? WHERE EMAIL = ?";
                PreparedStatement state = conn.prepareStatement(sql);
                state.setObject(1, milestone.getPassword());
                state.setObject(2, milestone.getEmail());
                state.executeUpdate();
                stmt.close();
                System.out.println("Password Changed");
                response.sendRedirect("/index.html");
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }

            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        else{
            response.sendRedirect("/forgotConfirmation.html");
        }
    }
}
