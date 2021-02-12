package wpd2.lab2;

import com.sun.istack.internal.NotNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionFunct {


    static void CheckSession(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession(false);


    }



}
