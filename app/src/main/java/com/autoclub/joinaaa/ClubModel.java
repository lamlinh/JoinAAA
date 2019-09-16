package com.autoclub.joinaaa;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ClubModel {
    public static final String KEY_CLUB_TITLE   = "clubTitle";
    public static final String KEY_CLUB_POSTAL = "clubPostal";
    public static final String KEY_CLUB_HOST    = "clubHost";
    public static final String KEY_NEW_MEM_CONTACT = "newMembershipContact";


    public final String Title;
    public final String PostalCode;
    public final String Host;
    public final String NewMembershipContact;

    public ClubModel(String name, String postalCode, String url, String newMembershipContact)
    {
        this.Title = name;
        this.PostalCode = postalCode;
        this.Host = url;
        this.NewMembershipContact = newMembershipContact;
    }

    public String getTitle() {
        return Title;
    }

    public ClubModel(Bundle bundle) {
        Title = bundle.getString(KEY_CLUB_TITLE, null);
        PostalCode = bundle.getString(KEY_CLUB_POSTAL, null);
        Host = bundle.getString(KEY_CLUB_HOST, null);
        NewMembershipContact = bundle.getString(KEY_NEW_MEM_CONTACT, null);
    }

    public Bundle getValuesBundle() {
        Bundle bundle = new Bundle();

        bundle.putString(KEY_CLUB_TITLE, Title);
        bundle.putString(KEY_CLUB_POSTAL, PostalCode);
        bundle.putString(KEY_CLUB_HOST, Host);
        bundle.putString(KEY_NEW_MEM_CONTACT, NewMembershipContact);
        return bundle;
    }
}
