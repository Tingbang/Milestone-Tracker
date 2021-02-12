package wpd2.lab2;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wpd2.lab2.servlet.*;

public class Runner {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(Runner.class);

    private static final int PORT = 9000;
    private final String milestonePlanner;

    private Runner(String milestonePlanner)
    {
        this.milestonePlanner = milestonePlanner;
    }

    //the start method, starts the server and sets up the servlets that the server knows
    private void start() throws Exception
    {
        //creating an instance of the server listening on the specified port
        Server server = new Server(PORT);

        //creating a servlet context handler defining the environment for the servlets to run, e.g. which folder
        //the root is mapped to
        ServletContextHandler handler = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        handler.setContextPath("/");
        handler.setInitParameter("org.eclipse.jetty.servlet.Default." + "resourceBase", "src/main/resources/webapp");

        MilestonePlannerServlet milestonePlannerServlet = new MilestonePlannerServlet();
        handler.addServlet(new ServletHolder(milestonePlannerServlet), "/milestoneplanners");

        IncompletePlannerServlet incompletePlannerServlet = new IncompletePlannerServlet();
        handler.addServlet(new ServletHolder(incompletePlannerServlet), "/IncompletePlanner");

        RegistrationServlet RegistrationServlet = new RegistrationServlet();
        handler.addServlet(new ServletHolder(RegistrationServlet), "/registration");

        LoginServlet LoginServlet = new LoginServlet();
        handler.addServlet(new ServletHolder(LoginServlet), "/loggedin");

        LogOutServlet LogOutServlet = new LogOutServlet();
        handler.addServlet(new ServletHolder(LogOutServlet), "/loggedout");

        ForgotPasswordServlet ForgotPasswordServlet = new ForgotPasswordServlet();
        handler.addServlet(new ServletHolder(ForgotPasswordServlet), "/forgotpassword");

        ResetPasswordServlet ResetPasswordServlet = new ResetPasswordServlet();
        handler.addServlet(new ServletHolder(ResetPasswordServlet), "/resetpassword");


        //instantiating DefaultServlet and setting the requests that it responds to
        //and adding it to the server
        DefaultServlet ds = new DefaultServlet();
        handler.addServlet(new ServletHolder(ds), "/");

        //start the server
        server.start();
        LOG.info("Server started, will run until terminated");
        server.join();

    }

    public static void main(String[] args) {
        try {
            LOG.info("starting");

            new Runner("Group AL Milestone Planner").start();
        } catch (Exception e) {
            LOG.error("Unexpected error running milestone planner: " + e.getMessage());
        }
    }
}
