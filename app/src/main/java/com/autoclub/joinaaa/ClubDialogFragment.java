package com.autoclub.joinaaa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ClubDialogFragment extends DialogFragment   {
    private static final String ARG_ITEMS = "ARG_ITEMS";

    public interface OnClubTitleSelectedListener {
        public void onSelectedClubTitleClick(ClubModel club);
    }

    OnClubTitleSelectedListener listener;
/*
    public static Bundle convertClubsArrayListToBundle(List<ClubModel> clubsList) {
        int itemCount = clubsList.size();
        ArrayList<Bundle> clubBundles = new ArrayList<>();
        for (int i = 0; i < itemCount; ++i) {
            clubBundles.add(clubsList.get(i).getValuesBundle());
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_ITEMS, clubBundles);
        return bundle;
    }

    public ArrayList<ClubModel> getClubsArrayFromBundle(Bundle bundle) {
        ArrayList<Bundle> itemBundles = bundle.getParcelableArrayList(ARG_ITEMS);
        ArrayList<ClubModel> items = new ArrayList<>();
        for (Bundle itemBundle: itemBundles) {
            items.add(new ClubModel(itemBundle));
        }
        return items;
    }

    public static ClubDialogFragment newInstance(ArrayList<ClubModel> clubsList) {
        Bundle args = new Bundle();
        args.putBundle(ARG_ITEMS, convertClubsArrayListToBundle(clubsList));
        //args.putInt(ARG_SELECTED_INDEX, selectedIndex);

        ClubDialogFragment fragment = new ClubDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_SELECTED_INDEX, selectedIndex);
    }
*/

    /*@Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /*Bundle args = getArguments();
        if (args != null)
        {
            _clubs = getClubsArrayFromBundle(args.getBundle(ARG_ITEMS));
            //selectedIndex = args.getInt(ARG_SELECTED_INDEX, -1);
        }
        else
        {
           createClubsList();
        }
        /*if (savedInstanceState != null) {
            selectedIndex = savedInstanceState.getInt(ARG_SELECTED_INDEX, selectedIndex);
        }*/

        String[] clubTitles = ClubsList.getClubTitles();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.ThemeOverlay_Material_Dialog_Alert);
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.club_choice_dialog, null))
                .setTitle(R.string.pick_club_title)
                .setCancelable(true)
                .setItems(clubTitles, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                        if (listener != null) {
                            listener.onSelectedClubTitleClick(ClubsList.getAtIndex(i));
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnClubTitleSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnClubTitleSelectedListener.");
        }

    }
}
