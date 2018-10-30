package allkaps.gmail.com.misscleo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

public class PlayerView extends AppCompatActivity {

    private static final int REQUEST_CODE_SIGN_IN = 0;
    private GoogleSignInClient mGoogleSignInClient;
    private DriveResourceClient mDriveResourceClient;
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                                .requestScopes(Drive.SCOPE_FILE)
                                                                .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Here we are suckers!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_view);

        mGoogleSignInClient = buildGoogleSignInClient();

        //Intent intent =mGoogleSignInClient.getSignInIntent();
        //intent.
        //To get the account for the current user, you need to call GoogleSignInClient.getGoogleSignInAccountFromIntent()
        // The getGoogleSignInAccountFromIntent() method returns a Task object. In the success handler for the Task object,
        // you can access the GoogleSignInAccount associated with the current user.

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mDriveResourceClient = Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Here we are suckers!111");
                EditText searchText = findViewById(R.id.searchEditText);
                TextView searchResultTextView = findViewById(R.id.searchResultsTextView);

                String searchString = searchText.getText().toString();
                //searchResultTextView.setText(searchString);
                System.out.println("Here we are suckers!222");


                DriveFolder rootFolder = mDriveResourceClient.getRootFolder().getResult();
                System.out.println("Here we are suckers rootFolder[" + rootFolder.toString() + "]");

                Query query2 = new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, "*wet*"))
                        .build();
                MetadataBuffer mdb = mDriveResourceClient.query(query2).getResult();

                String fileName = mdb.get(0).getTitle();

                System.out.println("Here is the fileName[" + fileName + "]");
                searchResultTextView.setText(fileName + " [" + searchString + "]");

                try {
                    //mGoogleSignInClient.
                    //DriveQuickstart newDriveQuickStart = new DriveQuickstart();
                    // newDriveQuickStart.populateFiles();
                    //String fileSearchResults = newDriveQuickStart.searchFiles(searchString);
                    //searchResultTextView.setText(fileSearchResults);
                    //System.out.println("Here we are suckers!333 fileSearchResults[" + fileSearchResults + "]");
                } catch(Exception e) {
                    //System.out.println(DriveQuickstart.getStackTraceAsString(e));
                }

            }
        });

    }

}
