package com.aantaya.codesharp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.aantaya.codesharp.ui.settings.SettingsActivity;
import com.aantaya.codesharp.utils.ColorUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
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
