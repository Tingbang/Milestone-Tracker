package wpd2.lab2.servlet;


import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

// Extend HttpServlet class
@WebServlet(urlPatterns = {"/forgotpassword"})
public class ForgotPasswordServlet extends BaseServlet{

    private static final String senderEmail = "wpd2project@gmail.com";
    private static final String senderPassword = "CoTiTaMo";

    static private void sendAsHtml(String to, String title, String html) throws MessagingException {
        System.out.println("Sending email to " + to);

        Session session = createSession();

        //create message using session
        MimeMessage message = new MimeMessage(session);
        prepareEmailMessage(message, to, title, html);

        //sending message
        Transport.send(message);
        System.out.println("Done");
    }

    private static void prepareEmailMessage(MimeMessage message, String to, String title, String html)
            throws MessagingException {
        message.setContent(html, "text/html; charset=utf-8");
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(title);
    }

    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");//Outgoing server requires authentication
        props.put("mail.smtp.starttls.enable", "true");//TLS must be activated
        props.put("mail.smtp.host", "Smtp.gmail.com"); //Outgoing server (SMTP)
        props.put("mail.smtp.port", "587");//Outgoing port

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        return session;
    }

    public static void main(String[] args) throws MessagingException {
        ForgotPasswordServlet.sendAsHtml(email,
                "Milestone Planner",
                "<h2>MileStonePlanner Application</h2><p>Please select the following link to reset your password</p><a href=http://localhost:9000/forgotConfirmation.html>Password Reset Link</a>");
    }

    static final Logger LOG = LoggerFactory.getLogger(LoginServlet.class);
    private final static String USER_NAME_KEY = "userName";
    private static final String MILESTONE_PLANNER_TEMPLATE = "mps..html";
    private static final long serialVersionUID = -7461821901454655091L;
    static private final String USER="";
    static private final String PASS="";

    static final private String JDBC_DRIVER = "org.h2.Driver";
    static final private String DB_URL= "jdbc:h2:~/milestoneplannerdb";
    ResultSet rs;
    private Connection conn = null;
    Statement stmt = null;
    private String eml;
    private static String email = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        email = request.getParameter("email");

        email = Jsoup.clean(email, Whitelist.basic());

        email = email.toLowerCase();

        try {
            System.out.println("Connecting to db");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            String sql = "SELECT * FROM USER WHERE EMAIL ='" + email +"'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                eml =rs.getString("email");

                if(eml.equals(email)){
//                        response.sendRedirect("/reset_password.html");
                    try {
                        ForgotPasswordServlet.main(null);
                        System.out.println("Instruction sent to Email");
                        response.sendRedirect("/index.html");
                    }
                    catch (MessagingException me) {
                        System.out.println(me);

                    }
                }
                else{
                    System.out.println("Invalid Email, please try again");
                    response.sendRedirect("/forgotConfirmation.html");
                }
            }
            else{
                System.out.println("Invalid Email, please try again");
                response.sendRedirect("/forgotConfirmation.html");
            }
            stmt.close();

        } catch (SQLException e) {
            System.out.println( e);

        }
    }

}