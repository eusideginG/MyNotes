package com.example.mynotes.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mynotes.MenuFragment;
import com.example.mynotes.R;

public class MenuHelper {
    private final String TAG = this.getClass().getSimpleName();
    Activity activity;
    ImageButton ibMenu;

    int layoutWrapper;

    public MenuHelper(Activity activity) {
        this.activity = activity;
        ibMenu = activity.findViewById(R.id.IBMenu);
    }

    public ImageButton getIbMenu() {
        return ibMenu;
    }

    public void setIbMenu(ImageButton ibMenu) {
        this.ibMenu = ibMenu;
    }

    public void setLayoutWrapper(int layoutWrapper) { this.layoutWrapper = layoutWrapper; }

    public void menuBtnPressed() {
        ibMenu.setOnClickListener(view -> {
            AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
            MenuFragment menuFragment = new MenuFragment();
            appCompatActivity.getSupportFragmentManager()
                    .beginTransaction().replace(layoutWrapper, menuFragment)
                    .addToBackStack(null).commit();
        });
    }
}
