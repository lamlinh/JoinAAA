package com.autoclub.joinaaa;

import android.content.res.Resources;

import java.util.ArrayList;

public class ClubsList {
    private final static ArrayList<ClubModel> _clubs = new ArrayList<ClubModel>(9) {
        {
            add(new ClubModel("AAA Alabama", "35004","www.alabama.aaa.com", "1-800-521-8124"));
            add(new ClubModel("Automobile Club of Southern California", "90001", "www.calif.aaa.com","1-866-903-4222"));
            add(new ClubModel("AAA East Central", "14008","www.eastcentral.aaa.com", "1-800-441-5008"));
            add(new ClubModel("AAA Hawaii", "96701","www.hawaii.aaa.com", "1-808-593-2221 or 1-800-736-2886"));
            add(new ClubModel("AAA Missouri", "63141","www.autoclubmo.aaa.com", "1-844-559-0831"));
            add(new ClubModel("AAA New Mexico", "87001","www.newmexico.aaa.com", "1-877-222-1020"));
            add(new ClubModel("AAA Northern New England", "03031","www.northernnewengland.aaa.com", "1-866-892-1162 or 1-207-780-6840"));
            add(new ClubModel("AAA Texas", "73301","www.texas.aaa.com", "1-800-765-0766"));
            add(new ClubModel("AAA Tidewater Virginia", "22432","www.tidewater.aaa.com", "1-800-501-4222"));
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
