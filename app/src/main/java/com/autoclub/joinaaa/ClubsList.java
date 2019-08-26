package com.autoclub.joinaaa;

import android.content.res.Resources;

import java.util.ArrayList;

public class ClubsList {
    private final static ArrayList<ClubModel> _clubs = new ArrayList<ClubModel>(9) {
        {
            add(new ClubModel("AAA Alabama", "35068","www.alabama.aaa.com"));
            add(new ClubModel("Automobile Club of Southern California", "92626", "www.calif.aaa.com"));
            add(new ClubModel("AAA East Central", "40201","www.eastcentral.aaa.com"));
            add(new ClubModel("AAA Hawaii", "96817","www.hawaii.aaa.com"));
            add(new ClubModel("AAA Missouri", "63141","www.autoclubmo.aaa.com"));
            add(new ClubModel("AAA New Mexico", "87505","www.newmexico.aaa.com"));
            add(new ClubModel("AAA Northern New England", "04210","www.northernnewengland.aaa.com"));
            add(new ClubModel("AAA Texas", "79109","www.texas.aaa.com"));
            add(new ClubModel("AAA Tidewater Virginia", "23462","www.tidewater.aaa.com"));
        }
    };

    public static ArrayList<ClubModel> get(){return _clubs;}


    public static ClubModel getAtIndex(int index) {
        return _clubs.get(index);
    }

    public static String[] getClubTitles() {
        final int count = _clubs.size();
        String[] clubTitles = new String[count];
        for (int i = 0; i < count; ++i) {
            clubTitles[i] = _clubs.get(i).Title;
        }
        return clubTitles;
    }

}
