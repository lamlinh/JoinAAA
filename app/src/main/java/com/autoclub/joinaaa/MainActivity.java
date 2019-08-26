package com.autoclub.joinaaa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.carrier.CarrierMessagingService;
import android.util.Base64;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.internal.FallbackServiceBroker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback,
               ClubDialogFragment.OnClubTitleSelectedListener {

    protected static final String _zipGateUrl = "https://zipgate.aaa.com";

    View _mainLayout;
    /**
     * Web view layout of this activity
     */
    WebView _webView;
    ProgressBar _progressBar;
    TextView _progressText;
    /**
     * Id to identity a location permission request.
     */
    private static final int REQUEST_LOCATION = 1;

    /**
     * Permission required to read device location.
     */
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final String TAG  = "WebView";
    protected static final int REQUEST_CHECK_SETTINGS = 9001;
    protected static  final String SETTING_CLUB_HOST = "ClubHost";
    protected static  final  String SETTING_CLUB_POSTAL_CODE = "ClubPostalCode";
    protected static final  String SETTING_CLUB_TITLE = "ClubTitle";

    FusedLocationProviderClient _fusedLocationClient = null;
    LocationCallback _locationCallback;
    LocationRequest _locationRequest;
    String _locationPostalCode = "";
    String _clubHost = "";
    String _newMemPageUrl = "";
    String _joinAAALink = "";

    boolean _isLoadingNewMemberPage;
    boolean _isRedirectToMemberPage;

    ArrayList<ClubModel> _clubs;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        //getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_main);

        _mainLayout = findViewById(R.id.mainLayout);
        _progressBar = findViewById(R.id.progressBar);

        //Set Reset button click event.
        FloatingActionButton reset = findViewById(R.id.fab);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,R.string.refresh_prompt, Snackbar.LENGTH_LONG)
                        .setAction(R.string.yes, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Refresh the membership page.
                                loadJoinAAA();
                            }
                        })
                        .setAnchorView(R.id.fab)
                        .setActionTextColor(Color.WHITE)
                        .show();
            }
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        configureWebView();
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String title = mPreferences.getString(SETTING_CLUB_TITLE,"");
        _clubHost = mPreferences.getString(SETTING_CLUB_HOST, "");
        _locationPostalCode = mPreferences.getString(SETTING_CLUB_POSTAL_CODE, "");
        if (savedInstanceState != null)
        {
            _webView.restoreState(savedInstanceState);
            return;
        }
        if (title == "" || _clubHost == "" || _locationPostalCode == "") {
            showLookupZipGate();
        }
        else
        {
            setTitle(title);
            loadJoinAAA();
        }

        /*
        if (_useClubSelection) {
            showLookupZipGate();
        }
        else {

            //Get fused location client.
            _fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            //Verify location access permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Location permissions are not granted. Send a permission request.
                requestLocationPermission();
            } else {
                checkLocationSettings();
            }
        }
        */
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_club: {
                showLookupZipGate();
                return true;
            }
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
    /*
    @SuppressLint({"WrongConstant"})
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
           return;
        }
        TextView textView  = (TextView) actionBar.getCustomView();
        if (textView != null) {
            textView.setText(title);
            textView.setTextSize(20);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextColor(Color.WHITE);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            //textView.setGravity(Gravity.CENTER_HORIZONTAL);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(textView);
        }
    }

    */
    private void loadJoinAAA() {
        //_webView.clearHistory();

        if (_clubHost == null || _clubHost == "" || _locationPostalCode == null || _locationPostalCode == "" ) {
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            _clubHost = mPreferences.getString(SETTING_CLUB_HOST, "");
            _locationPostalCode = mPreferences.getString(SETTING_CLUB_POSTAL_CODE, "");
            if (_clubHost == "" || _locationPostalCode == "") {
                showLookupZipGate();
                return;
            }
        }
        _isLoadingNewMemberPage = true;
        _isRedirectToMemberPage = false;
        String newMemberHost = _clubHost.replace("www","apps");
        _newMemPageUrl = "https://" + newMemberHost + "/ACEApps/membership/#/createMembership/?flowName=NewBiz";
        _joinAAALink = "https://" + newMemberHost + "/aceapps/membership/Join/SelectMembershipLevel?flowName=NewBiz";
        _webView.setVisibility(View.INVISIBLE);
        _webView.loadUrl("https://" + _clubHost + "/?zip=" + _locationPostalCode);
    }

    private void showErrorPage(String message) {
        String html = "<html><body>" + message + "</body></html>";
        String encodedHtml = Base64.encodeToString(html.getBytes(), Base64.NO_PADDING);
        _webView.loadData(encodedHtml, "text/html", "base64");
    }
    private void showLookupZipGate()
    {
        /*setIndicatorVisibility(false);
        String html = "<html><body>Fail to read the current location of device.</body></html>";
        String encodedHtml = Base64.encodeToString(html.getBytes(), Base64.NO_PADDING);
        myWebView.loadData(encodedHtml, "text/html", "base64");*/
        FragmentManager fragmentManager = getSupportFragmentManager();
        ClubDialogFragment clubDialog = new ClubDialogFragment();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        clubDialog.show(ft, "clubDialog");
    }

    private ClubModel getClub(String host) {
        if (_clubs == null) {
            _clubs = ClubsList.get();
        }
        for(ClubModel club: _clubs) {
            if (club.Host.equalsIgnoreCase(host))
                return club;
        }

        return null;
    }

    private void configureWebView() {
        //Web view settings
        _webView = (WebView) this.findViewById(R.id.webBrowser);

        //Set web view client handler
        _webView.setWebViewClient(new CustomWebViewClient());
        _webView.setWebChromeClient(new CustomWebChromeClient());
        WebSettings webSettings = _webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            String errorMessage = error.getDescription().toString();
            showErrorPage(errorMessage);
            _isRedirectToMemberPage = false;
            _isLoadingNewMemberPage = false;
            _webView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (!_isLoadingNewMemberPage ||
                    url.startsWith(_zipGateUrl) ||
                    url.toLowerCase().startsWith(_joinAAALink.toLowerCase())) {
                return;
            }
            //redirect to new membership page if club web site is loaded.
            if (!_isRedirectToMemberPage ) {
                _isRedirectToMemberPage = true;
                if (url.toLowerCase().startsWith("https://"+_clubHost.toLowerCase())) {
                    view.loadUrl(_joinAAALink);
                }
                else { //Something went wrong here.
                    _isLoadingNewMemberPage = false;
                    _webView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int progress) {
            _progressBar.setProgress(progress);
            _progressBar.setVisibility(View.VISIBLE);
            if (progress == 100) {
                _progressBar.setVisibility(View.GONE);
                if (!_isLoadingNewMemberPage) return;
                if (!_isRedirectToMemberPage) return;

                //Verify loading a new membership page.
                String url = view.getUrl();
                if (url.startsWith(_joinAAALink) || url.startsWith(_zipGateUrl)) {
                    //Skip and wait for the new membership page is fully loaded.
                    return;
                }
                //Try to hide the header and footer of membership page.
                _webView.evaluateJavascript("var header = document.getElementById('headerSection'); " +
                                "var footer = document.getElementById('footerSection'); " +
                                "if (header != null) {header.style.display = 'none';} " +
                                "if (footer != null) {footer.style.display = 'none';}",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                _webView.setVisibility(View.VISIBLE);
                            }
                        });

                _isRedirectToMemberPage = false;
                _isLoadingNewMemberPage = false;
            }
        }
    }

    private void requestLocationPermission() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Location has NOT been granted. Requesting permission");
        }

        //BEGIN_INCLUDE(location_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Snackbar.make(_mainLayout,R.string.permission_location_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
                        }
                    }).show();
        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
        }
        //END_INCLUDE(location_permission_request)
    }

    private void createLocationRequest() {
        _locationRequest = LocationRequest.create();
        _locationRequest.setInterval(10000);
        _locationRequest.setFastestInterval(5000);
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallBackListener()
    {
        _locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                _fusedLocationClient.removeLocationUpdates(_locationCallback);
               if (locationResult == null) {
                    if (BuildConfig.DEBUG) {Log.i(TAG, "Location result is null.");}
                    Snackbar.make(_mainLayout, R.string.read_location_failure,
                            Snackbar.LENGTH_LONG)
                            .show();
                    showLookupZipGate();
                    return;
                }

                //Get the address of first location from the result.
                List<Location> locations = locationResult.getLocations();
                if (locations.size() > 0) {
                    Location location = locations.get(0);
                    getAddress(location);
                }
            };
        };
    }

    private void getAddress(Location location) {
        List<Address> addresses = null;

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            //errorMessage = getString(R.string.service_not_available);
            //Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            //errorMessage = getString(R.string.invalid_lat_long_used);
            //Log.e(TAG, errorMessage + ". " +
            //        "Latitude = " + location.getLatitude() +
            //        ", Longitude = " +
             //       location.getLongitude(), illegalArgumentException);

        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            //if (errorMessage.isEmpty()) {
            //    errorMessage = getString(R.string.no_address_found);
            //    Log.e(TAG, errorMessage);
            //}
            //deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            showLookupZipGate();
        } else {
            Address address = addresses.get(0);
            _locationPostalCode = address.getPostalCode();

            loadJoinAAA();
        }
    }

    private void checkLocationSettings() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {

            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;

                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                        return;
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }

                showLookupZipGate();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (_locationRequest == null) {
            createLocationRequest();
        }
        if (_locationCallback == null)
            createLocationCallBackListener();

        _fusedLocationClient.requestLocationUpdates(_locationRequest, _locationCallback, null);
    }

    /**
     * Callback received when a permission request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (BuildConfig.DEBUG) { Log.i(TAG, "Received response for location permissions request."); }

            if (PermissionUtil.verifyPermissions(grantResults)) {
                checkLocationSettings();
            }
            else {
                if (BuildConfig.DEBUG) { Log.i(TAG, "Location permissions were NOT granted."); }
                Snackbar.make(_mainLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
                showLookupZipGate();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * The selected club model which is called back from the ClubDialogFrament.
     * @param selectedClub
     */
    @Override
    public void onSelectedClubTitleClick(ClubModel selectedClub) {
        _clubHost = selectedClub.Host;
        _locationPostalCode = selectedClub.PostalCode;
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SETTING_CLUB_HOST, _clubHost);
        editor.putString(SETTING_CLUB_POSTAL_CODE, _locationPostalCode);
        editor.putString(SETTING_CLUB_TITLE, selectedClub.Title);
        editor.apply();
        setTitle(selectedClub.Title);
        loadJoinAAA();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState ) {
        super.onSaveInstanceState(outState);
        _webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        _webView.restoreState(savedInstanceState);
    }
}
