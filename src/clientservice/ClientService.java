package clientservice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import network.NetworkUser;
import mediator.Mediator;
import common.IFile;
import common.IUser;
import common.LocalUser;
import common.SimpleFile;


public class ClientService extends SwingWorker<IUser, IUser> implements IClientService{
	private Mediator med;
	private LocalUser me;
	String selfUserName;
	int port;
	private String homePrefix = "root/";
	
	private String serverHost = "localhost";
	private int serverPort = 8080;
	
	String serviceURL;
	
	private final String USER_AGENT = "Mozilla/5.0";
	
	public ClientService(Mediator med, String selfUserName, String port) {
		this.med = med;
		med.registerClientService(this);
		this.selfUserName = selfUserName;
		this.port = Integer.parseInt(port);
		
		serviceURL = "http://" + serverHost + ":" + serverPort + "/FileTransferUserServer/UserServiceServlet";
	}
	
	@Override
	public LocalUser getSelfUser() {
		return me;
	}
	
	private HttpURLConnection getConnection(String method, String params) {
		URL obj = null;
		HttpURLConnection con = null;
		try {
			obj = new URL(serviceURL + params);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		
		try {
			con = (HttpURLConnection) obj.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		con.setRequestProperty("User-Agent", USER_AGENT);
		try {
			con.setRequestMethod(method);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return con;
	}
	
	public void updateSelfFiles() {
		String home = homePrefix + selfUserName + "/";
		
		File dir = new File(home);
		String[] fileList = dir.list();
		List<IFile> files = new ArrayList<IFile>();
		
		for (String file : fileList) {
			files.add(new SimpleFile(file));
		}
		
		
		HttpURLConnection con = getConnection("POST", "");
		
		String fileStr = "";
		for (IFile f : files)
			fileStr += f.getName() + ",";
		
		fileStr = fileStr.substring(0, fileStr.length() - 1);
		
		String urlParameters = "name=" + selfUserName +
				               "&port=" + port +
				               "&file=" + fileStr;
		
		con.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
		con.setDoOutput(true);
		con.setDoInput(true);
				
		DataOutputStream wr;
		try {
			wr = new DataOutputStream(con.getOutputStream ());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			con.getResponseMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		con.disconnect();
	}

	@Override
	protected IUser doInBackground() throws Exception {
		String home = homePrefix + selfUserName + "/";
		me = new LocalUser(selfUserName, home, port);
		
		updateSelfFiles();
		
		while (true) {
			/* Get all users in memory then render them */
			ArrayList<String> userPort = new ArrayList<String>();
			ArrayList<IUser> users = new ArrayList<IUser>();
			users.add(me);
			HttpURLConnection con = getConnection("GET", "");
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
	        String line;
			while ((line = rd.readLine()) != null) {
	            userPort.add(line);
	        }
	        rd.close();
	        con.disconnect();
	        
	        for (String up : userPort) {
	        	String name = up.split(",")[0];
	        	if (name.equals(selfUserName))
	        		continue;
	        	
	        	String port = up.split(",")[1];
	        	
	        	con = getConnection("GET", "?name=" + name);
				
				rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
		        line = rd.readLine();
		        
		        ArrayList<IFile> userFiles = new ArrayList<IFile>();
				for (String f : line.split(",")) {
					userFiles.add(new SimpleFile(f));
				}
		      
		        rd.close();
		        con.disconnect();
	        	
		        users.add(new NetworkUser(name, userFiles, Integer.parseInt(port)));
	        }
			
			
	        med.refreshUsers(users);
			Thread.sleep(1300);
		}
		
	
	}

	@Override
	public void exit() {
		HttpURLConnection con = getConnection("POST", "");
		String urlParameters = "name=" + selfUserName +
				               "&exit=yes";
				             
		
		con.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
		con.setDoOutput(true);
		con.setDoInput(true);
				
		DataOutputStream wr;
		try {
			wr = new DataOutputStream(con.getOutputStream ());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			con.getResponseMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		con.disconnect();
	}
	
}

