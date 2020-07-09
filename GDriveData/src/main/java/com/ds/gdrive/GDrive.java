package com.ds.gdrive;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
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
import java.util.Arrays;
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

public class GDrive {
	private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	List<String> list = new ArrayList();
	public static int duplicate_count = 0;
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

	public static void downloadFile(String id, String name) throws IOException {

		try {
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
					.setApplicationName(APPLICATION_NAME).build();
//			String fileId = "1eK3V-xZSaBK2PRdVBaLVVmz1cNPNkd6v";
			OutputStream outputStream = new ByteArrayOutputStream();
			service.files().get(id).executeMediaAndDownloadTo(outputStream);

//			String file = "AAAAAAAAAAAAAA.png";
			try (OutputStream outputStream2 = new FileOutputStream(name)) {
				((ByteArrayOutputStream) outputStream).writeTo(outputStream2);
			}
			System.out.println("File downloaded successfully");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public List<String> imageData(String id) {

		try {
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
					.setApplicationName(APPLICATION_NAME).build();

			FileList r = service.files().list().setQ("'" + id + "'" + " in parents")
					.setFields("nextPageToken, files(id, name,mimeType, parents)").execute();

			for (File file : r.getFiles()) {
				// Content of specific folder based on Id
				if (file.getMimeType().equals("image/png")) {
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

		return list;

	}

	public static boolean processImage(String name1, String name2) throws IOException, GeneralSecurityException {

		try {
			// Load the images
			Image image1 = Toolkit.getDefaultToolkit().getImage(name1);
			Image image2 = Toolkit.getDefaultToolkit().getImage(name2);

			PixelGrabber grabImage1Pixels = new PixelGrabber(image1, 0, 0, -1, -1, false);
			PixelGrabber grabImage2Pixels = new PixelGrabber(image2, 0, 0, -1, -1, false);

			int[] image1Data = null;

			if (grabImage1Pixels.grabPixels()) {
				int width = grabImage1Pixels.getWidth();
				int height = grabImage1Pixels.getHeight();
				image1Data = new int[width * height];
				image1Data = (int[]) grabImage1Pixels.getPixels();
			}

			int[] image2Data = null;

			if (grabImage2Pixels.grabPixels()) {
				int width = grabImage2Pixels.getWidth();
				int height = grabImage2Pixels.getHeight();
				image2Data = new int[width * height];
				image2Data = (int[]) grabImage2Pixels.getPixels();
			}

			if (java.util.Arrays.equals(image1Data, image2Data))

				return java.util.Arrays.equals(image1Data, image2Data);

		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		return false;
	}

	///////////////////////////

	public static int imageDataCount(List<String> IList) {
		List<String> DList = new ArrayList();
		int duplicate_count = 0;
		int total_dupimage = 0;
		// DriveQuick obj=new DriveQuick();
		try {

			for (int i = 0; i < IList.size() - 1; i++) {
				duplicate_count = 0;
				for (int j = i + 1; j < IList.size(); j++) {
					if (i == 0) {
						if (processImage(IList.get(i), IList.get(j))) {
							duplicate_count = duplicate_count + 1;
							if (!DList.contains(IList.get(j))) {
								DList.add(IList.get(j));
							}
						}
					} else {
						if (!DList.contains(IList.get(i))) {
							if (processImage(IList.get(i), IList.get(j))) {
								duplicate_count = duplicate_count + 1;
								if ((!DList.contains(IList.get(j))) && (!IList.get(i).equals(IList.get(j)))) {
									DList.add(IList.get(j));
								}
							}
						}
					}
				} // inner loop

				total_dupimage = total_dupimage + duplicate_count;

				if (i == 0) {
					DList.add(IList.get(i));

					System.out.println("No of Duplicate images of " + "\t\t" + IList.get(i) + "\t\t" + duplicate_count);
				}

				else {

					if (!DList.contains(IList.get(i))) {
						DList.add(IList.get(i));
						System.out.println(
								"Images: " + "\t\t" + IList.get(i) + "\t\t" + duplicate_count);
					}
				}

//				 System.out.println(DList);
			}

		} catch (Exception e) {
			e.getMessage();
		}

		return total_dupimage;
	}

	public static void main(String... args) throws IOException, GeneralSecurityException, FileNotFoundException {
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();
		Map<String, String> obj = new HashMap<>();
		Map<String, String> root = new HashMap<>();
		String parent_id = null;
		// Print the names and IDs for up to 10 files.
		FileList result = service.files().list().setPageSize(1000)
				.setFields("nextPageToken, files(id, name, mimeType, parents)").execute();
		List<File> files = result.getFiles();
		int total_dupimage = 0;
		Map<String, String> map = new HashMap();
		Map<String, String> mapimage = new HashMap();
		if (files == null || files.isEmpty()) {
			System.out.println("No files found.");
		} else {
			int count2 = 0;
			int count3 = 0;
			int count4 = 0;
			System.out.println("Summary: ");
			System.out.println(" ");
			// htmlcode
			FileWriter fw = new FileWriter("Result.html");
			String html = "<html>" + "<head>" + "<style>" + "p.solid{border-style: solid;}" + "</style>" + "</head>"
					+ "<body>";

			String html1 = "<meta charset='utf-8'>"
					+ "<meta name='viewport' content='width=device-width, initial-scale=1'>"
					+ "<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css'>"
					+ "<script src='https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js'></script>"
					+ "<script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js'></script>"
					+ "<body>" + "<div class='row'>" + "<div id='piechart' class=\"col-sm-6\"></div>"
					+ "<div id='columnchart_values' class=\"col-sm-6\"></div>" + "</div>"
//					+ "<div id='table_div' class='col-sm-6'></div>" + "</div>"
					+ "<script type='text/javascript' src='https://www.gstatic.com/charts/loader.js'></script>"
					+ "<script type='text/javascript'>" +
					// Load google charts

					"google.charts.load('current', {'packages':['corechart']});"
					+ "google.charts.setOnLoadCallback(drawChart);" +

					// Draw the chart and set the chart values
					"function drawChart() {" + "var data = google.visualization.arrayToDataTable([" +

					"['Summary', 'Count'],";

			String html2 = "]);" +

			// Optional; add a title and set the width and height of the chart
					"var options = {'title':'Images & Folders', 'width':800, 'height':500};" +

					// Display the chart inside the <div> element with id="piechart"
					"var chart = new google.visualization.PieChart(document.getElementById('piechart'));"
					+ "chart.draw(data, options);" + "}" +

					"</script>";
//					+ "<script type='text/javascript' src='https://www.gstatic.com/charts/loader.js'></script>"
//					+ "<script type='text/javascript'>" + "google.charts.load('current', {'packages':['table']});"
//					+ "google.charts.setOnLoadCallback(drawTable);" +
//
//					"function drawTable() {" + " var data = new google.visualization.DataTable();"
//					+ "data.addColumn('string', 'Summary of drive');" + "data.addColumn('number', 'Count');"
//					+ "data.addRows([";

			String html3 =
//					"]);" +
//
//					"var table = new google.visualization.Table(document.getElementById('table_div'));" +
//
//					"table.draw(data, {width: '50%', height: '20%'});" + "}" + "</script>" + "</body>"
					"<body>" +

							"<script type='text/javascript' src='https://www.gstatic.com/charts/loader.js'></script>"
							+ "<script type='text/javascript'>"
							+ "google.charts.load('current', {packages:['corechart']});"
							+ "google.charts.setOnLoadCallback(drawChart);" + "function drawChart() {"
							+ "var data = google.visualization.arrayToDataTable(["
							+ "['Folders', 'Count', { role: 'style' } ],";

			String html4 = "]);" +

					"var view = new google.visualization.DataView(data);" + "view.setColumns([0, 1,"
					+ "{ calc: 'stringify'," + "sourceColumn: 1," + "type: 'string'," + "role: 'annotation' }," + "2]);"
					+

					"var options = {" + "title: 'Graphical Representation of Images & Folders'," + " width: 800,"
					+ " height: 500," + "bar: {groupWidth: '95%'}," + "legend: { position: 'none' }," + "};"
					+ "var chart = new google.visualization.ColumnChart(document.getElementById('columnchart_values'));"
					+ "chart.draw(view, options);" + "}" +

					"</script>" + "</body>" + "</body>";
			try {

				for (File f1 : files) {

					if (f1.getMimeType().equals("application/vnd.google-apps.folder")) {
//					System.out.println("" + f1.getMimeType() + "\t\t\t" + f1.getName());
						if (count2 == 0)
							parent_id = f1.getParents().toString();
						count2 = count2 + 1;
					}
					if (!f1.getMimeType().equals("application/vnd.google-apps.folder")) {
//					System.out.println("" + f1.getMimeType() + "\t\t\t" + f1.getName());
						count3 = count3 + 1;
					}
					if (f1.getMimeType().equals("image/png")) {
//					System.out.println("" + f1.getMimeType() + "\t\t\t" + f1.getName());
						mapimage.put(f1.getId(), f1.getName());
						count4 = count4 + 1;
					}
					if (f1.getMimeType().equals("application/vnd.google-apps.folder")) {
//						System.out.println("" + f1.getMimeType() + "\t\t\t" + f1.getName());
						if (f1.getParents().toString().equals(parent_id)) {
							// System.out.println("" + f1.getParents() + "\t\t\t" + parent_id);
							root.put(f1.getId(), f1.getName());
							// count2 = count2 + 1;
						}
					}
				} // end of for loop
				System.out.println("Parent ID of Root Folder: " + parent_id);
				List<String> IList = new ArrayList();
				for (Map.Entry<String, String> imgList : mapimage.entrySet()) {
					downloadFile(imgList.getKey(), imgList.getValue());
					System.out.println("File name: " + imgList.getValue());
					IList.add(imgList.getValue());
				}

				total_dupimage = imageDataCount(IList);

				System.out.println("Parent ID of root Folder: " + parent_id);
				System.out.println("No of Folders: " + count2);
				System.out.println("No of Duplicate Images: " + total_dupimage);
				System.out.println("No of  Images: " + count4);
				fw.write(html);
				fw.write("<b><h1 style='text-align:center;'>Google Drive Report</h1></b><br></body>");
				fw.write(html1);
				fw.write("['No of folders'," + count2 + "],");
				fw.write("['No of Duplicate images'," + total_dupimage + "],");
				fw.write("['No of  images'," + count4 + "]");
				System.out.println(" ");
				fw.write(html2);
				fw.write(html3);
				count2 = 0;
				count3 = 0;
				count4 = 0;
			} catch (Exception e) {
				e.getMessage();
			}
			try {
				int count = 0;
				int count1 = 0;

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
						if (file.getMimeType().equals("image/png")) {
//							System.out.println("" + file.getMimeType() + "\t\t\t" + file.getName());
							count = count + 1;
//							}
						} else {
//							System.out.println("" + file.getMimeType() + "\t\t\t" + file.getName());
							count1 = count1 + 1;
						}
					}
					count = 0;
					count1 = 0;
				}

				int imageCount = 0;
				for (Map.Entry<String, String> entry : root.entrySet()) {

					List l = new GDrive().imageData(entry.getKey());
					// List l= new TestDrive().imageData(entry.getKey());
					imageCount = imageDataCount(l);
					System.out.println("total images in " + entry.getValue() + ": " + l.size());
					System.out.println("No of dup in  " + entry.getValue() + ": " + imageCount);
					fw.write("['" + entry.getValue() + "', " + l.size() + ", 'gold'],");
					fw.write("[' ', " + imageCount + ", 'blue'],");
				}
				fw.write(html4);
				fw.write(
						"<b><p style='text-align:left; width:50%; display: inline-block;'>The piechart and table represents the count of folders, images and duplicate images</p></b></body>");
				fw.write(
						"<body><b><span style='text-align: right; width:93%; display: inline-block;'>The bar graph represents the number of images in each root folder in the drive</span></p></b></body>");
				fw.write("</html>");
				for (File file : files) {
					// Content of specific folder based on Id
					if (file.getMimeType().equals("image/png")) {
						map.put(file.getId(), file.getName());
					}

				}
			} catch (Exception e) {
				e.getMessage();
			} finally {
				fw.close();
			}
		}
	}

}
