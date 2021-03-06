package com.ds.nitin;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

//import io.opencensus.internal.StringUtils;

import com.google.api.services.drive.Drive;

public class TestDrive {
	private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	List<String> list = new ArrayList();
	/**
	 * Global instance of the scopes required by this quickstart. If modifying these
	 * scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = Drive.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}
	
	public List imageData(String id) {

		try {
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
					.setApplicationName(APPLICATION_NAME).build();

			FileList r = service.files().list().setQ("'" + id + "'" + " in parents")
					.setFields("nextPageToken, files(id, name,mimeType, parents)").execute();

			for (File file : r.getFiles()) {
				// Content of specific folder based on Id
				if (file.getMimeType().equals("image/jpeg")) {
					list.add(file.getName());
				}
				if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
					imageData(file.getId());
					
				}
			}
		} catch (IOException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("No. of image in Folder " + list.size());

		return list;

	}

	public static void main(String... args) throws IOException, GeneralSecurityException, FileNotFoundException {
		
		String parent_id = null;
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();
		Map<String, String> obj = new HashMap<>();
		Map<String, String> root = new HashMap<>();
		// Print the names and IDs for up to 10 files.
		FileList result = service.files().list().setPageSize(1000)
				.setFields("nextPageToken, files(id, name, mimeType, parents)").execute();
		List<File> files = result.getFiles();

		if (files == null || files.isEmpty()) {
			System.out.println("No files found.");
		} else {
			int folder_count=0;
			int image_count=0;
			int count2 = 0;
			int count3 = 0;
			int count4 = 0;
			for (File file : files) {

				if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
					if(folder_count == 0)
						parent_id = file.getParents().toString();
					obj.put(file.getId(), file.getName());
					folder_count= folder_count + 1;
					
				}
				
				if (file.getMimeType().equals("image/jpeg")) {
//					System.out.println("" + f1.getMimeType() + "\t\t\t" + f1.getName());
					image_count = image_count +1;
				}
			}
			try {
				int count = 0;
				int count1 = 0;

				String html = "<html>" +
				"<head>"+
				"<script type='text/javascript'>" +
				"window.onload = function () {" +
					"var chart = new CanvasJS.Chart('chartContainer'," +
					"{" +
						"title:{" +
							"text: 'Google Drive Report'" +
						"}," +
						"legend: {" +
							"maxWidth: 350," +
							"itemWidth: 120" +
						"}," +
						"data: [";
				String html1 =	"{" +
							"type: 'pie'," +
							"showInLegend: true," +
							"legendText: '{indexLabel}'," +
							"dataPoints: [";	
				String html2 =		"]" +
						"}" +
						"]" +
					"});" +
					"chart.render();" +
 				"}" +
				"</script>" +
				"<script type='text/javascript' src='https://canvasjs.com/assets/script/canvasjs.min.js'></script>" +
				"</head>" +
				"<body>" +
				"<div id='chartContainer' style='height: 300px; width: 100%;'></div>" +
				"</body>" +
				"</html>";
				FileWriter fw = new FileWriter("D:\\ResultNew.html");
				fw.write(html);
				fw.write(html1);
				fw.write("{ y: " + folder_count +", indexLabel: 'No of Folders' },");
				fw.write("{ y: "+ image_count +", indexLabel: 'No of Images' },");
				fw.write("{ y: 5, indexLabel: 'No of Duplicate Images' }");
				fw.write(html2);
//				fw.close();
			/*	for (File file : files) {    // nitin comments

					if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
						obj.put(file.getId(), file.getName());
					}
				}*/
				/*
				 * for (File file : files) { if(file.getParents().toString().equals(parent_id) )
				 * { //System.out.println("" + f1.getParents() + "\t\t\t" + parent_id);
				 * root.put(file.getId(),file.getName()); // count2 = count2 + 1; } }
				 */
				for (Map.Entry<String, String> entry : root.entrySet()) {
					// Folder Name
//					System.out.println(" ");
//					System.out.println("Folder: " + entry.getValue());
//					System.out.println(" ");
					FileList r = service.files().list().setQ("'" + entry.getKey() + "'" + " in parents")
							.setFields("nextPageToken, files(id, name,mimeType, parents)").execute();
					for (File file : r.getFiles()) {
						// Content of specific folder based on Id
						if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
							System.out.println("" + file.getMimeType() + "\t\t\t" + file.getName());
							count = count + 1;
						}
						if(file.getMimeType().equals("image/jpeg")) {
							System.out.println("" + file.getMimeType() + "\t\t\t" + file.getName());
							count1 = count1 + 1;
						}
					}

					System.out.println("No. of Folder " + count);
					System.out.println("No. of Files " + count1);
//					System.out.println(" ");
					count = 0;
					count1 = 0;
					
				}
				System.out.println("Summary: ");
				new TestDrive().imageData("1WjL0AVOJ8aBkdw9VvV09sirsCRN94bYu");
//				fw.write("<html>");
//				fw.write("<head>");
                fw.write("<br><br><br><TABLE BORDER='5' bordercolor='black' align = 'center'><TR><TH >Summary of Drive<TH></TR>");

				for (File f1 : files) {

//					fw.write("<script type=\"text/javascript\">");
//					fw.write("window.onload = function () {");
//					fw.write("var chart = new CanvasJS.Chart(\"chartContainer\",");
//					fw.write("{");
//					fw.write("title:{");
//					fw.write("text: \"Google Drive Report\"");
//					fw.write("},");
//					fw.write("legend: {");
//					fw.write("maxWidth: 350,");
//					fw.write("itemWidth: 120");
//					fw.write("},");
//					fw.write("data: [");
//					fw.write("type: \"pie\",");
//					fw.write("showInLegend: true,");
//					fw.write("legendText: \"{indexLabel}\",");

					if (f1.getMimeType().equals("application/vnd.google-apps.folder")) {
//						System.out.println("" + f1.getMimeType() + "\t\t\t" + f1.getName());
						if(f1.getParents().toString().equals(parent_id) )
							{
							//System.out.println("" + f1.getParents() + "\t\t\t" + parent_id);
							root.put(f1.getId(),f1.getName());
						    count2 = count2 + 1;
							}
					}
					if (!f1.getMimeType().equals("application/vnd.google-apps.folder")) {
//						System.out.println("" + f1.getMimeType() + "\t\t\t" + f1.getName());
						count3 = count3 + 1;
					}
					if (f1.getMimeType().equals("image/png")) {
//						System.out.println("" + f1.getMimeType() + "\t\t\t" + f1.getName());
						count4 = count4 + 1;
					}
				}
				System.out.println("No of Folders: " + folder_count);
				System.out.println("No of Images: " + image_count);
				System.out.println("No of Duplicate Images: " + 5);
				System.out.println("Parent ID : " + parent_id);
				System.out.println("No of Folder in root Drive : " + count2);
				System.out.println(root);
				
				fw.write("<TR><TD>" + "No of folders: " + "<TD>" + folder_count);
				fw.write("<TR><TD>" + "No of images: " + "<TD>" + image_count);
				fw.write("<TR><TD>" + "No of duplicate Images: " + "<TD>" + 5);
				fw.write("</TABLE>");

//				fw.write("dataPoints: [");
//				fw.write("{ y:" + count2 + ",indexLabel: \"No of Folders\" }");
//				fw.write("{ y:" + count3 + ",indexLabel: \"No of Files\" }");
//				fw.write("{ y:" + count4 + ",indexLabel: \"No of Duplicate Images\" }");
//				fw.write("]");
//				fw.write("}");
//				fw.write("]");
//				fw.write("});");
//				fw.write("chart.render();");
//				fw.write("}");
//				fw.write("</script>");
//				fw.write(
//						"<script type=\"text/javascript\" src=\"https://canvasjs.com/assets/script/canvasjs.min.js\"></script>");
//				fw.write("</head>");
//				fw.write("<body>");
//				fw.write("<div id=\"chartContainer\" style=\"height: 300px; width: 100%;\"></div>");
//				fw.write("</body>");
//				fw.write("</html>");
//				fw.close();
				count2 = 0;
				count3 = 0;
				count4 = 0;
				fw.close();
			} catch (Exception e) {
				e.getMessage();
			}
//			System.out.println("out: " +count2);
		}
	}

}

