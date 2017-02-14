package com.glimpse.lecretsi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;


public class IntroActivity extends AppIntro {
    boolean doubleBackToExitPressedOnce = false;

    //Class that implements he first time tutorial
    //Kudos to @paolorotolo https://github.com/apl-devs/AppIntro
    //BUG Images in the slide are known not to display properly on high resolution devices
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        ///Setting the tutorial slides
        ///Yea that simple - this guy is a hero
        ///Hooray for open source !

        ///Strings had to be hardcoded

        ///We use the deprecated getColor as it is the only one available on API 19
        addSlide(AppIntroFragment.newInstance(
                "Largonji ?",
                "Vos messages sont chiffrés à l\'aide du Largonji",
                R.drawable.question_mark,
                ContextCompat.getColor(this, R.color.wonderous_teal)
        ));

        addSlide(AppIntroFragment.newInstance(
                "Comment ?",
                "béton => létonbi   larme =>lalmeri \n auteur =>lauleurti   l’abstrait => l’alstraitbi",
                R.drawable.light_bulb,
                ContextCompat.getColor(this, R.color.lightbulb_yellow)
        ));

        addSlide(AppIntroFragment.newInstance(
                "Ton copains attendant !",
                "Ajouter vos amis en utilisant leur adresse e-mail",
                R.drawable.gentleman_figure,
                ContextCompat.getColor(this, R.color.gentleman_blue)
        ));

        addSlide(AppIntroFragment.newInstance(
                "Entraine toi !",
                "Utilisez l'assistant de Largonji pour pratiquer ... ou tout simplement déconner",
                R.drawable.book,
                ContextCompat.getColor(this, R.color.vast_blue)
        ));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!doubleBackToExitPressedOnce) {
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
