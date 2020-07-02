package com.ds.gdrive;

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

import io.opencensus.internal.StringUtils;

import com.google.api.services.drive.Drive;

public class GDrive {
	private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

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
		InputStream in = GDrive.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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

	public static void main(String... args) throws IOException, GeneralSecurityException, FileNotFoundException {
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();
		Map<String, String> obj = new HashMap<>();

		// Print the names and IDs for up to 10 files.
		FileList result = service.files().list().setPageSize(1000)
				.setFields("nextPageToken, files(id, name, mimeType, parents)").execute();
		List<File> files = result.getFiles();

		if (files == null || files.isEmpty()) {
			System.out.println("No files found.");
		} else {
			int count2 = 0;
			int count3 = 0;
			int count4 = 0;
			try {
				int count = 0;
				int count1 = 0;
				
				//htmlcode
				FileWriter fw = new FileWriter("Result.html");
				String html = "<html lang='en-US>" ;

				String html1 = "<head>" +
				"<meta charset='utf-8'>" +
				  "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
				  "<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css'>" +
				  "<script src='https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js'></script>" +
				  "<script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js'></script>" +
				  "<body>" +
				"<div class='row'>" +
				        "<br><br><div id='piechart' class='col-sm-6'></div>" +
				        "<div id='table_div' class='col-sm-6'></div>" +
				"</div>" +
				"<script type='text/javascript' src='https://www.gstatic.com/charts/loader.js'></script>" +
				"<script type='text/javascript'>" +
				// Load google charts

				"google.charts.load('current', {'packages':['corechart']});" +
				"google.charts.setOnLoadCallback(drawChart);" +

				// Draw the chart and set the chart values
				"function drawChart() {" +
				  "var data = google.visualization.arrayToDataTable(["+
				
				  "['Summary', 'Count'],";
				
				String html2 = "]);" +

				  // Optional; add a title and set the width and height of the chart
				  "var options = {'title':'files & folders', 'width':450, 'height':300};"+

				  // Display the chart inside the <div> element with id="piechart"
				  "var chart = new google.visualization.PieChart(document.getElementById('piechart'));"+
				  "chart.draw(data, options);"+
				"}"+

				"</script>"+
				   "<script type='text/javascript' src='https://www.gstatic.com/charts/loader.js'></script>"+
				    "<script type='text/javascript'>"+
				      "google.charts.load('current', {'packages':['table']});" +
				      "google.charts.setOnLoadCallback(drawTable);" +

				      "function drawTable() {"+
				       " var data = new google.visualization.DataTable();" +
				        "data.addColumn('string', 'Summary of drive');" +
				        "data.addColumn('number', 'Count');"+
				        "data.addRows([";
				          
				      String html3= "]);" +

				        "var table = new google.visualization.Table(document.getElementById('table_div'));"+

				        "table.draw(data, {width: '50%', height: '20%'});"+
				      "}"+
				    "</script>"+
				"</body>"+
				    "<body><p><br></p></body>"+
				 
				"</body>" +
				    "<div id='columnchart_values' style='width: 900px; height: 300px;' align='center'></div>"+

				"<script type='text/javascript' src='https://www.gstatic.com/charts/loader.js'></script>"+
				  "<script type='text/javascript'>"+
				    "google.charts.load('current', {packages:['corechart']});"+
				    "google.charts.setOnLoadCallback(drawChart);"+
				    "function drawChart() {"+
				      "var data = google.visualization.arrayToDataTable(["+
				        "['Folders', 'Count', { role: 'style' } ],";
				        
				      String html4 = "]);"+

				      "var view = new google.visualization.DataView(data);"+
				      "view.setColumns([0, 1,"+
				                       "{ calc: 'stringify',"+
				                         "sourceColumn: 1,"+
				                         "type: 'string',"+
				                         "role: 'annotation' },"+
				                       "2]);"+

				      "var options = {"+
				        "title: 'Graphical Representation of drive',"+
				       " width: 600,"+
				       " height: 400,"+
				        "bar: {groupWidth: '95%'},"+
				        "legend: { position: 'none' },"+
				      "};"+
				      "var chart = new google.visualization.ColumnChart(document.getElementById('columnchart_values'));"+
				      "chart.draw(view, options);"+
				  "}"+

				  "</script>"+
				  "</body>"+
				"</html>";

				for (File file : files) {

					if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
						obj.put(file.getId(), file.getName());
					}
				}
				for (Map.Entry<String, String> entry : obj.entrySet()) {
					// Folder Name
//					System.out.println(" ");
//					System.out.println("Folder: " + entry.getValue());
//					System.out.println(" ");
					FileList r = service.files().list().setQ("'" + entry.getKey() + "'" + " in parents")
							.setFields("nextPageToken, files(id, name,mimeType, parents)").execute();
					for (File file : r.getFiles()) {
						// Content of specific folder based on Id
						if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
//							System.out.println("" + file.getMimeType() + "\t\t\t" + file.getName());
							count = count + 1;
						} else {
//							System.out.println("" + file.getMimeType() + "\t\t\t" + file.getName());
							count1 = count1 + 1;
						}
					}

//					System.out.println("No. of Folder " + count);
//					System.out.println("No. of Files " + count1);
//					System.out.println(" ");
					count = 0;
					count1 = 0;
				}
				System.out.println("Summary: ");
				System.out.println(" ");
//				fw.write("<html>");
//				fw.write("<head>");

				for (File f1 : files) {

					if (f1.getMimeType().equals("application/vnd.google-apps.folder")) {
//						System.out.println("" + f1.getMimeType() + "\t\t\t" + f1.getName());
						count2 = count2 + 1;
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
				System.out.println("No of Folders: " + count2);
				System.out.println("No of Files: " + count3);
				System.out.println("No of Duplicate Images: " + count4);
				fw.write(html);
				fw.write("<body><p><h1 style='align: center'><strong>Google Drive Report<strong></h1></p></body>");
				fw.write(html1);
				fw.write("['No of folders', 8],");
				fw.write("['No of files', 2],");
				fw.write("['No of duplicate images', 4]");
				fw.write(html2);
				 fw.write("['Number of folders',  8],");
		          fw.write("['Number of files',   2],");
		          fw.write("['No of Images', 4]");
				fw.write(html3);
				 fw.write("['test1', 8, '#b87333'],");
			        fw.write("['test2', 10, 'silver'],");
			        fw.write("['test3', 19, 'gold'],");
			        fw.write("['test4', 21, 'color: #e5e4e2']");
			        fw.write(html4);
			        fw.close();
				count2 = 0;
				count3 = 0;
				count4 = 0;
			} catch (Exception e) {
				e.getMessage();
			}
		}
	}

}