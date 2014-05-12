package com.idp.servlets;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UserServiceServlet
 */
@WebServlet("/UserServiceServlet")
public class UserServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, String> userFiles; 
	private HashMap<String, String> userPort;
     /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServiceServlet() {
        super();
        userFiles = new HashMap<String, String>();
        userPort = new HashMap<String, String>();
    }
    
    void addUser(String user) {
    	userFiles.put(user, "");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
	
		response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        
        if (name == null) {
        	for (String user : userFiles.keySet()) {
        		if (userPort.get(user) == null)
        			continue;
        		writer.println(user + "," + userPort.get(user));
        	}
        	return;
        }
        
        if (!userFiles.containsKey(name)) {
        	response.setStatus(404);
        	writer.println("404 Not Found");
        	return;
        }
        
        writer.println(userFiles.get(name));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
		
		if ("yes".equals(request.getParameter("exit"))) {
			userPort.remove(name);
			userFiles.remove(name);
			
		}
		
		
		String port = request.getParameter("port");
		String files = request.getParameter("file");
		addUser(name);
		
		userPort.put(name, port);
		userFiles.put(name, files);
		
		response.setStatus(200);
	}

}
