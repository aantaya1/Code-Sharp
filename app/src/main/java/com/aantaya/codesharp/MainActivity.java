package com.aantaya.codesharp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.aantaya.codesharp.utils.ThemeHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set the application theme based on the user's prefs
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDarkMode = sharedPreferences.getBoolean("dark_mode", true);
        ThemeHelper.applyTheme(useDarkMode);

        //Setup the bottom navigation draws
        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemReselectedListener(item -> {
            // Do nothing. We don't want to re-load the fragment every time the
            // user clicks the menu item and they are already on that fragment.
        });

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        //This will handle switching between menu items for is using Material Design patterns
        NavigationUI.setupWithNavController(navView, navController);
    }
}
