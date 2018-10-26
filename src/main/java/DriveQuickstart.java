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
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list()
                .setPageSize(20)
                .setFields("nextPageToken, files(id, name, modifedTime, createdTime)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
            	try {
            		DateTime googleDirveFileModifedDateTime = file.getModifiedTime();
            		DateTime googleDriveFileCreatedDateTime = file.getCreatedTime();
            		
            		if(googleDirveFileModifedDateTime == null) {
            			System.out.println("googleDirveFileModifedDateTime is null");
            		}
            		
            		if(googleDriveFileCreatedDateTime == null) {
            			System.out.println("googleDriveFileCreatedDateTime is null");
            		}
            		//LocalDateTime modifedLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(googlDateTime.getValue()), ZoneId.systemDefault());
                	//System.out.printf("%s (%s)\n", file.getName(), file.getId(), modifedLocalDateTime.format(googleDriveDtf));
                	//System.out.printf("%s (%s)\n", file.getName(), file.getId());
            		String modifedDateTimeString = (googleDirveFileModifedDateTime != null) ? googleDirveFileModifedDateTime.toString() : "null";
            		String createdDateTimeString = (googleDriveFileCreatedDateTime != null) ? googleDriveFileCreatedDateTime.toString() : "null";
            		System.out.println("FileName[" + file.getName() + "] fileModified[" + modifedDateTimeString + "] createdDateTimeString[" + createdDateTimeString + "]");
            	} catch(Exception e) {
            		StringBuilder steSb = new StringBuilder();
            		StackTraceElement[] steArray = e.getStackTrace();
            		steSb.append(e.getMessage() + "\n");
            		for(StackTraceElement ste : steArray) {
            			steSb.append(ste.toString() + "\n");
            		}
            		System.out.println("StackTraceElement[\n" + steSb.toString() + "]");
            	}
            }
        }
    }
}