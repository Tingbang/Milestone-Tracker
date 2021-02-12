package wpd2.lab2.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogOutServlet extends BaseServlet{


    private final static String USER_NAME_KEY = "userName";

    static void clearCurrentUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(true);
        session.removeAttribute(USER_NAME_KEY);
        session.invalidate();
        response.sendRedirect("/index.html");
        System.out.println("Cleared");
    }


    static void CheckSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        if (session == null){
            System.out.println("No session");
            response.sendRedirect("index.html");
        }else{
            System.out.println("session set");
            clearCurrentUser(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        System.out.println("Start to clear session");
        CheckSession(request,response);

    }






}
