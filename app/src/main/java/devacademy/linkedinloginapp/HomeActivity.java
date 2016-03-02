package devacademy.linkedinloginapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String host = "api.linkedin.com";
    private static final String topCardUrl = "https://" + host + "/v1/people/~:" +
            "(email-address,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";
    ProgressDialog progress;
    ImageView profile_pic;
    TextView user_name,user_email;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logout = (Button) findViewById(R.id.logout_button);
        progress= new ProgressDialog(this);
        progress.setMessage("Retrieve data...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        getUserData();


        user_email = (TextView) findViewById(R.id.email);
        user_name = (TextView) findViewById(R.id.username);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LISessionManager.getInstance(getApplicationContext()).clearSession();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    /*Once User's can authenticated,
      It make an HTTP GET request to LinkedIn's REST API using the currently authenticated user's credentials.
      If successful, A LinkedIn ApiResponse object containing all of the relevant aspects of the server's response will be returned.
     */

    public void getUserData(){
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(HomeActivity.this, topCardUrl, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {
                    Log.w(TAG, "BE VALi" + result.toString());
                    setUserProfile(result.getResponseDataAsJson());
                    progress.dismiss();

                } catch (Exception e) {
                    Log.w(TAG, "EROARE!");
                    e.printStackTrace();
                }

            }

            @Override
            public void onApiError(LIApiError error) {
                // ((TextView) findViewById(R.id.error)).setText(error.toString());

            }
        });
    }

    /*
       Set User Profile Information in Navigation Bar.
     */

    public  void  setUserProfile(JSONObject response){

        try {

            user_email.setText(response.get("emailAddress").toString());
            user_name.setText(response.get("formattedName").toString());

            Picasso.with(this).load(response.getString("pictureUrl"))
                    .into(profile_pic);

        } catch (Exception e){
            e.printStackTrace();
        }
    }


}