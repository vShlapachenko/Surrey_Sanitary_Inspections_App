package com.example.cmpt276_project_iron.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Restaurant;

import java.util.List;

public class UpdatedFavouritesFragment extends AppCompatDialogFragment {

    private Context context;
    private List<Restaurant> updatedRestaurants;

    public UpdatedFavouritesFragment(Context context, List<Restaurant> updatedRestaurants) {
        this.context = context;
        this.updatedRestaurants = updatedRestaurants;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.updated_favourites, null);
        ListView listView = view.findViewById(R.id.favouritesList);
        ArrayAdapter<Restaurant> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, updatedRestaurants);
        listView.setAdapter(adapter);
        return new AlertDialog.Builder(getActivity())
                .setTitle("This list of your favourite restaurants has new inspections")
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();

    }
}
