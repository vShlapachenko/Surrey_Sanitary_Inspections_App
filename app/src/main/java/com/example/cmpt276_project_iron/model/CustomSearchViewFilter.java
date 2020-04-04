package com.example.cmpt276_project_iron.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

/**
 * Custom search view extended class to allow for empty inputs which will then allow for the keyboard
 * to be hidden
 */
public class CustomSearchViewFilter extends SearchView {

    private OnQueryTextListener queryListener;

    public CustomSearchViewFilter(Context context) {
        super(context);
    }

    public CustomSearchViewFilter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSearchViewFilter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnQueryTextListener(OnQueryTextListener listener) {
        super.setOnQueryTextListener(listener);
        queryListener = listener; //(android.support.v7.appcompat.R.id.search_src_text)
        SearchAutoComplete inputSearch = this.findViewById(androidx.appcompat.R.id.edit_query);
        inputSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (listener != null) {
                listener.onQueryTextSubmit(getQuery().toString());
            }
            return true;
        });
    }

}
