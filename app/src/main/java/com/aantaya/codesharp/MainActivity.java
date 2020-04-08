package com.aantaya.codesharp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.aantaya.codesharp.ui.settings.SettingsActivity;
import com.aantaya.codesharp.utils.ColorUtils;
import com.aantaya.codesharp.utils.ThemeHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.github.kbiakov.codeview.classifier.CodeProcessor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set the application theme based on the user's prefs
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDarkMode = sharedPreferences.getBoolean("dark_mode", true);
        ThemeHelper.applyTheme(useDarkMode);

        //Setup the toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Setup the bottom navigation draws
        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                // Do nothing. We don't want to re-load the fragment every time the
                // user clicks the menu item and they are already on that fragment.
            }
        });

        // Passing each menu ID as a set of Ids because each menu should be considered as
        // top level destinations and not display the up button
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //This will handle switching between menu items for is using Material Design patterns
        NavigationUI.setupWithNavController(navView, navController);

        // Initialize the code processor that auto-detects programming languages for CodeViews
        CodeProcessor.init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        setIconColorsWithAppTheme(menu);

        //todo: need to add stuff to support search
        //https://developer.android.com/training/search/setup

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_more:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.main_menu_filter:
                //todo: need to implement
                Toast.makeText(this, "Need to implement 7832", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * private helper method for setting the main page's icons to display as the correct tint
     * depending on the user's theme prefs
     *
     * @param menu reference to menu containing icons we want to update
     */
    private void setIconColorsWithAppTheme(Menu menu){
        MenuItem item = menu.findItem(R.id.action_more);
        Drawable drawableWrap = DrawableCompat.wrap(item.getIcon()).mutate();
        DrawableCompat.setTint(drawableWrap, ColorUtils.getThemeColor(this, R.attr.colorOnPrimary));
        item.setIcon(drawableWrap);

        item = menu.findItem(R.id.main_menu_search);
        drawableWrap = DrawableCompat.wrap(item.getIcon()).mutate();
        DrawableCompat.setTint(drawableWrap, ColorUtils.getThemeColor(this, R.attr.colorOnPrimary));
        item.setIcon(drawableWrap);

        item = menu.findItem(R.id.main_menu_filter);
        drawableWrap = DrawableCompat.wrap(item.getIcon()).mutate();
        DrawableCompat.setTint(drawableWrap, ColorUtils.getThemeColor(this, R.attr.colorOnPrimary));
        item.setIcon(drawableWrap);
    }
}
