package com.autoclub.joinaaa;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ClubModel {
    private static final String KEY_CLUB_TITLE   = "clubTitle";
    private static final String KEY_CLUB_POSTAL = "clubPostal";
    private static final String KEY_CLUB_HOST    = "clubHost";


    public final String Title;
    public final String PostalCode;
    public final String Host;

    public ClubModel(String name, String postalCode, String url)
    {
        this.Title = name;
        this.PostalCode = postalCode;
        this.Host = url;
    }

    public String getTitle() {
        return Title;
    }

    public ClubModel(Bundle bundle) {
        Title = bundle.getString(KEY_CLUB_TITLE, null);
        PostalCode = bundle.getString(KEY_CLUB_POSTAL, null);
        Host = bundle.getString(KEY_CLUB_HOST, null);
    }

    public Bundle getValuesBundle() {
        Bundle bundle = new Bundle();

        bundle.putString(KEY_CLUB_TITLE, Title);
        bundle.putString(KEY_CLUB_POSTAL, PostalCode);
        bundle.putString(KEY_CLUB_HOST, Host);
        return bundle;
    }
}
