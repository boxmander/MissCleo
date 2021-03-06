import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class DriveQuickstart {
	private Drive googleDriveService;
	private List<com.google.api.services.drive.model.File> googleDriveFileList;
	//private static final DateTimeFormatter googleDriveDtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public DriveQuickstart() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        googleDriveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Drive getGoogleDriveService()											{ return googleDriveService; }
    public List<com.google.api.services.drive.model.File> getGoogleDriveFileList()	{ return googleDriveFileList; }

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receier = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receier).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        DriveQuickstart newDriveQuickStart = new DriveQuickstart();
        newDriveQuickStart.populateFiles();
        System.out.println("Here is the file we want to get[" + newDriveQuickStart.getGoogleDriveFileList().get(0).getName() + "]");
        newDriveQuickStart.writeFile(newDriveQuickStart.getGoogleDriveFileList().get(0));
        System.out.println("Done?!");
    }

    private void populateFiles() throws IOException {
        // Print the names and IDs for up to 10 files.
        FileList result = getGoogleDriveService().files().list()
        		.setQ("name contains 'mp3'")
                .setPageSize(200)
                .setFields("nextPageToken, files(id, name, modifiedTime, createdTime)")
                .execute();
        googleDriveFileList = result.getFiles();
    }

    private void writeFile(com.google.api.services.drive.model.File googleDriveFile) throws IOException {
    	java.io.File outputFile = new java.io.File(googleDriveFile.getName());
    	FileOutputStream fop = new FileOutputStream(outputFile);
    	
    	if(!outputFile.exists()) {
    		outputFile.createNewFile();
    	}
    	getGoogleDriveService().files().get(googleDriveFile.getId()).executeMediaAndDownloadTo(fop);
    	fop.flush();
    	fop.close();
    }
    
    private static String getStackTraceAsString(Exception e) {
		StringBuilder steSb = new StringBuilder();
		StackTraceElement[] steArray = e.getStackTrace();
		steSb.append(e.getMessage() + "\n");
		for(StackTraceElement ste : steArray) {
			steSb.append(ste.toString() + "\n");
		}
		return steSb.toString();
    }
}