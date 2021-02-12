package wpd2.lab2.servlet;

import com.sun.istack.internal.NotNull;
import org.eclipse.jetty.server.session.Session;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wpd2.lab2.MilestonePlanner;
import wpd2.lab2.SessionFunct;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.sql.Statement;

import static wpd2.lab2.servlet.RegistrationServlet.Hash;

public class LoginServlet extends BaseServlet
{
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(LoginServlet.class);
    private final static String USER_NAME_KEY = "userName";
    private static final String MILESTONE_PLANNER_TEMPLATE = "mps..html";
    private static final long serialVersionUID = -7461821901454655091L;
    static final String USER="";
    static final String PASS="";


    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL= "jdbc:h2:~/milestoneplannerdb";
    private static HttpSession session;

    ResultSet rs;
    Connection conn = null;
    Statement stmt = null;
    String pwd;
    String uname;

    public LoginServlet()
    {

    }

    static void clearCurrentUser(HttpServletRequest request, HttpServletRequest response) {
        session = request.getSession(true);
        session.removeAttribute(USER_NAME_KEY);
        session.invalidate();
        response.getSession(Boolean.parseBoolean("false"));
    }
    static void setCurrentSession(HttpServletRequest request, String userName){
        session = request.getSession(true);
        session.setAttribute("USERNAME", userName);
    }
    static void CheckSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        session = request.getSession(false);

        if (session == null){
            System.out.println("No session");
            //response.sendRedirect("index.html");
        }else{
            System.out.println("session set");
           // response.sendRedirect("/milestoneplanners");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String username1 = request.getParameter("uname");
        String password1= request.getParameter("psw");
        String salt ="asd235aiuhasdn92impsi8cn9";
        String DB_PASS = Hash(password1 +salt);

        username1 = username1.toLowerCase();

        //Sanatising the inputs
        username1 = Jsoup.clean(username1, Whitelist.basic());
        password1 = Jsoup.clean(password1, Whitelist.basic());


        username1 = username1.toLowerCase();

        try {
            System.out.println("Setting session");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            String sql = "SELECT * FROM user WHERE USERNAME ='" + username1 +"'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                uname =rs.getString("USERNAME");
                pwd = rs.getString("password");

                if(uname.equals(username1) && pwd.equals(DB_PASS)){
                    setCurrentSession(request,username1);
                    CheckSession(request,response);


                }else{
                    response.sendRedirect("/login_error.html");
                }
            }else{
                response.sendRedirect("/login_error.html");
            }
            stmt.close();
            response.sendRedirect("/milestoneplanners");

        } catch (SQLException e) {
            System.out.println(e);

        }

    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        clearCurrentUser(request, (HttpServletRequest) response);
        CheckSession(request,response);
        response.sendRedirect("/index.html");

    }
}