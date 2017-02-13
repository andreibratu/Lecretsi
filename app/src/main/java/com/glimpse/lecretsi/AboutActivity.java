package com.glimpse.lecretsi;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutActivity extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_about, container, false);

        TextView developerTitle = (TextView) view.findViewById(R.id.developers_title);
        developerTitle.setText(R.string.about_dev_title);
        developerTitle.setTextSize(30);
        developerTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView developerText = (TextView) view.findViewById(R.id.developers_text);
        developerText.setText(R.string.about_dev_text);
        developerText.setTextSize(20);
        developerText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView testerTitle = (TextView) view.findViewById(R.id.testers_title);
        testerTitle.setText(R.string.about_testers_title);
        testerTitle.setTextSize(30);
        testerTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView testerText = (TextView) view.findViewById(R.id.testers_text);
        testerText.setText(R.string.about_testers_text);
        testerText.setTextSize(20);
        testerText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return view;
    }
}
