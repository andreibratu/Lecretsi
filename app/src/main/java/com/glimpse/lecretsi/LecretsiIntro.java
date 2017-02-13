package com.glimpse.lecretsi;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;


public class LecretsiIntro extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance(
                "Largonji?",
                "Vos messages sont chiffrés à l\'aide du Largonji",
                R.drawable.question_mark,
                getResources().getColor(R.color.lightBlue)
        ));

        addSlide(AppIntroFragment.newInstance(
                "Comment?",
                "Si le mot commence par une consonne, vous la" +
                        " remplacez par L, ajoutez-la à la fin et ajoutez I\n"
                        + "Si le mot commence par une voyelle ou la lettre L, vous " +
                        "ajoutez L au début, placez la première consonne à la fin et ajoutez I",
                R.drawable.light_bulb,
                getResources().getColor(R.color.green)
        ));

        addSlide(AppIntroFragment.newInstance(
                "Ton copains attendant",
                "Ajouter vos amis en utilisant leur adresse e-mail",
                R.drawable.gentleman_figure,
                getResources().getColor(R.color.gentleman_blue)
        ));

        addSlide(AppIntroFragment.newInstance(
                "Entraine toi!",
                "Utilisez l'assistant de Largonji pour pratiquer ... ou tout simplement déconner",
                R.drawable.book,
                getResources().getColor(R.color.yellow_book)
        ));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        startActivity(new Intent(this,ConversationsActivity.class));
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        startActivity(new Intent(this,ConversationsActivity.class));
        finish();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if(!doubleBackToExitPressedOnce) {
            Toast.makeText(this, "Please follow the tutorial or skip it", Toast.LENGTH_SHORT).show();
        }
        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
