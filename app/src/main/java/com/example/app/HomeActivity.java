package com.example.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends android.app.Activity {

    private final Handler handler = new Handler();
    private TextView clockView;
    private TextView dateView;
    private final int white = Color.WHITE;
    private final int softWhite = Color.argb(210, 255, 255, 255);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            window.setDecorFitsSystemWindows(false);
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(18), dp(8), dp(18), dp(10));
        root.setBackground(createWallpaper());

        installInsets(root);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams clockParams =
                new LinearLayout.LayoutParams(-1, dp(92));
        clockParams.topMargin = dp(4);

        clockView = new TextView(this);
        clockView.setTextColor(white);
        clockView.setTextSize(62);
        clockView.setGravity(Gravity.CENTER);
        clockView.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        content.addView(clockView, clockParams);

        dateView = new TextView(this);
        dateView.setTextColor(softWhite);
        dateView.setTextSize(19);
        dateView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams dateParams =
                new LinearLayout.LayoutParams(-1, dp(38));
        dateParams.bottomMargin = dp(12);
        content.addView(dateView, dateParams);

        TextView search = new TextView(this);
        search.setText("⌕   חיפוש");
        search.setTextColor(Color.WHITE);
        search.setTextSize(17);
        search.setGravity(Gravity.CENTER_VERTICAL);
        search.setPadding(dp(16), 0, dp(16), 0);
        search.setBackground(round(Color.argb(80,255,255,255), 24));
        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(-1, dp(48));
        searchParams.bottomMargin = dp(18);
        content.addView(search, searchParams);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setRowCount(4);
        grid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        grid.setUseDefaultMargins(false);

        AppItem[] apps = new AppItem[] {
                new AppItem("טלפון", "☎", "#34C759", "phone"),
                new AppItem("הודעות", "✉", "#0A84FF", "messages"),
                new AppItem("דפדפן", "◉", "#5E5CE6", "browser"),
                new AppItem("מצלמה", "◉", "#8E8E93", "camera"),
                new AppItem("מפות", "⌖", "#FF9F0A", "maps"),
                new AppItem("מוזיקה", "♫", "#FF375F", "music"),
                new AppItem("קבצים", "▣", "#0A84FF", "files"),
                new AppItem("הגדרות", "⚙", "#8E8E93", "settings"),
                new AppItem("לוח שנה", "31", "#FF453A", "calendar"),
                new AppItem("שעון", "◷", "#1C1C1E", "clock"),
                new AppItem("תמונות", "✿", "#BF5AF2", "photos"),
                new AppItem("מחשבון", "＋", "#5856D6", "calculator")
        };

        for (AppItem app : apps) {
            grid.addView(createAppIcon(app));
        }

        LinearLayout.LayoutParams gridParams =
                new LinearLayout.LayoutParams(-1, dp(360));
        content.addView(grid, gridParams);

        TextView dots = new TextView(this);
        dots.setText("●  ○  ○");
        dots.setTextColor(Color.WHITE);
        dots.setTextSize(12);
        dots.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams dotsParams =
                new LinearLayout.LayoutParams(-1, dp(34));
        dotsParams.bottomMargin = dp(4);
        content.addView(dots, dotsParams);

        LinearLayout dock = new LinearLayout(this);
        dock.setGravity(Gravity.CENTER);
        dock.setPadding(dp(8), dp(8), dp(8), dp(8));
        dock.setBackground(round(Color.argb(85, 255,255,255), 28));

        String[][] dockApps = {
                {"☎", "טלפון", "#34C759"},
                {"✉", "הודעות", "#0A84FF"},
                {"◉", "דפדפן", "#5E5CE6"},
                {"◉", "מצלמה", "#8E8E93"}
        };

        for (String[] item : dockApps) {
            TextView icon = makeIcon(item[0], item[1], item[2]);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(66), 1f);
            dock.addView(icon, p);
        }

        LinearLayout.LayoutParams dockParams =
                new LinearLayout.LayoutParams(-1, dp(82));
        dockParams.topMargin = dp(4);
        content.addView(dock, dockParams);

        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));

        setContentView(root);
        updateClock();
    }

    private void installInsets(View view) {
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            view.setOnApplyWindowInsetsListener((v, insets) -> {
                WindowInsets.Type.InsetsType systemBars = null;
                android.graphics.Insets bars =
                        insets.getInsets(WindowInsets.Type.systemBars());
                v.setPadding(dp(18), bars.top + dp(8), dp(18), bars.bottom + dp(10));
                return insets;
            });
            view.requestApplyInsets();
        }
    }

    private void updateClock() {
        Date now = new Date();
        clockView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(now));
        dateView.setText(new SimpleDateFormat("EEEE  d בMMMM", new Locale("he", "IL")).format(now));
        handler.postDelayed(this::updateClock, 1000);
    }

    private View createAppIcon(AppItem app) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(dp(4), dp(4), dp(4), dp(4));

        TextView icon = makeIcon(app.symbol, app.label, app.color);
        item.addView(icon, new LinearLayout.LayoutParams(dp(66), dp(66)));

        TextView label = new TextView(this);
        label.setText(app.label);
        label.setTextColor(white);
        label.setTextSize(12);
        label.setGravity(Gravity.CENTER);
        label.setMaxLines(1);
        item.addView(label, new LinearLayout.LayoutParams(dp(82), dp(24)));

        GridLayout.LayoutParams p = new GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f));
        p.width = 0;
        p.height = 0;
        p.setMargins(dp(2), dp(4), dp(2), dp(4));
        item.setLayoutParams(p);

        item.setOnClickListener(v -> launch(app.action, app.label));
        return item;
    }

    private TextView makeIcon(String symbol, String description, String hex) {
        TextView icon = new TextView(this);
        icon.setText(symbol);
        icon.setTextColor(Color.WHITE);
        icon.setTextSize(symbol.length() > 1 ? 20 : 31);
        icon.setGravity(Gravity.CENTER);
        icon.setTypeface(Typeface.DEFAULT_BOLD);
        icon.setContentDescription(description);
        int color = Color.parseColor(hex);
        icon.setBackground(round(color, 16));
        return icon;
    }

    private android.graphics.drawable.Drawable createWallpaper() {
        android.graphics.drawable.GradientDrawable drawable =
                new android.graphics.drawable.GradientDrawable(
                        android.graphics.drawable.GradientDrawable.Orientation.TL_BR,
                        new int[] {
                                Color.rgb(55, 79, 138),
                                Color.rgb(104, 88, 156),
                                Color.rgb(25, 33, 58)
                        });
        return drawable;
    }

    private android.graphics.drawable.Drawable round(int color, float radiusDp) {
        android.graphics.drawable.GradientDrawable d =
                new android.graphics.drawable.GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radiusDp));
        return d;
    }

    private void launch(String action, String label) {
        try {
            Intent intent = null;
            switch (action) {
                case "phone":
                    intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"));
                    break;
                case "browser":
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                    break;
                case "camera":
                    intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    break;
                case "settings":
                    intent = new Intent(Settings.ACTION_SETTINGS);
                    break;
                default:
                    Toast.makeText(this, label + " נבחר", Toast.LENGTH_SHORT).show();
                    return;
            }
            startActivity(intent);
        } catch (Exception ex) {
            Toast.makeText(this, label + " נבחר", Toast.LENGTH_SHORT).show();
        }
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private static class AppItem {
        final String label;
        final String symbol;
        final String color;
        final String action;

        AppItem(String label, String symbol, String color, String action) {
            this.label = label;
            this.symbol = symbol;
            this.color = color;
            this.action = action;
        }
    }
}
