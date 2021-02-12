package wpd2.lab2.servlet;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class RegistrationServlet extends BaseServlet
{
    static final String USER="";
    static final String PASS="";
    private String access;

    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL= "jdbc:h2:~/milestoneplannerdb";
    Connection conn = null;
    Statement stmt = null;

    static final Logger LOG = LoggerFactory.getLogger(RegistrationServlet.class);
    private static final long serialVersionUID = -7461821901454655091L;
    private static final String CONFIRM_REGISTRATION_TEMPLATE = "registration_success.html";

    public RegistrationServlet()
    {

    }

    //This method hashes the password using MD5 hashing method
    public static String Hash(String pass){
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(pass.getBytes());
            byte[] b = digest.digest();
            StringBuffer sb = new StringBuffer();

            for(byte b1 : b){
                sb.append(Integer.toHexString(b1& 0xff));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,  IOException
    {

        //Grabs the user input from the form
        String username = request.getParameter("uname");
        String password1 = request.getParameter("psw");
        String conf_pass = request.getParameter("psw1");
        String email1 = request.getParameter("eml");
        String access = "true";
        String salt = "asd235aiuhasdn92impsi8cn9";

        //Converts inputs into lowercase to prevent any error's
        username = username.toLowerCase();
        password1 = password1.toLowerCase();
        email1 = email1.toLowerCase();
        username = username.toLowerCase();

        //Sanitising the inputs before submission
        username = Jsoup.clean(username, Whitelist.basic());
        password1 = Jsoup.clean(password1, Whitelist.basic());
        conf_pass = Jsoup.clean(conf_pass, Whitelist.basic());
        email1 = Jsoup.clean(email1, Whitelist.basic());

        //Hashes and salts the password using the Hash Method written above.
        String act_pass = Hash(password1 + salt);
        //if both the passwords match
        if(password1.equals(conf_pass))
        {
            try
            { //Check if the username already exists
                Class.forName(JDBC_DRIVER);
                //Creating the connection to the database(3 parameters)
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                //Prepare the query
                String sql1 = "SELECT * FROM user WHERE USERNAME ='" + username +"'";
                String email_query = "SELECT * FROM user WHERE EMAIL ='" + email1 +"'";
                Statement stmt_2 = conn.createStatement();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql1);
                ResultSet rs2 = stmt_2.executeQuery(email_query);

                //If the username already exists throw an error.
                if(rs.next())
                {
                    stmt.close();
                    response.sendRedirect("/error.html");
                    //Checking if the email already exists
                }

                if(rs2.next())
                {
                    stmt_2.close();
                    response.sendRedirect("/email_error.html");
                }

                else
                { //else submit user data into the database

                    stmt.close();

                    try
                    {
                        Class.forName(JDBC_DRIVER);
                        //Create the connection to the database.
                        conn = DriverManager.getConnection(DB_URL, USER, PASS);
                        System.out.println("Connection Successful");
                        String sql = "INSERT INTO user(USERNAME,PASSWORD,EMAIL,EDIT_ACCESS) VALUES(?,?,?,?)";
                        PreparedStatement state = conn.prepareStatement(sql);
                        //Creating bound parameters
                        state.setObject(1, username);
                        state.setObject(2, act_pass);
                        state.setObject(3, email1);
                        state.setObject(4, access);
                        state.executeUpdate();
                        //Close the statement
                        stmt.close();
                        //System.out.println("Account Created");

                        Class.forName(JDBC_DRIVER);
                        //Create the connection to the database.
                        conn = DriverManager.getConnection(DB_URL, USER, PASS);
                        System.out.println("Connection Successful");
                        sql = "INSERT INTO MILESTONE_PLANNER(USERNAME,goal_name) VALUES(?,?)";
                        state = conn.prepareStatement(sql);
                        //Creating bound parameters
                        state.setObject(1, username);
                        state.setObject(2, "Your milestone planner");
                        state.executeUpdate();
                        //Close the statement
                        stmt.close();
                    }

                    catch  (ClassNotFoundException e)
                    {
                        e.printStackTrace();
                    }

                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }
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

        //System.out.println("Finished");
        //Once Registration is finished, redirect the user to the login page.
        response.sendRedirect("/registration_success.html");
    }
}