package com.dab.medireminder.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dab.medireminder.R;
import com.dab.medireminder.constant.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TextSpeechUtils {

    public static boolean checkLanguageAvailable(TextToSpeech textToSpeech, Locale localeUse) {
        try {
            List<Locale> localeList = new ArrayList<>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Set<Locale> locales = textToSpeech.getAvailableLanguages();
                if (locales != null) {
                    for (Locale locale : locales) {
                        int res = textToSpeech.isLanguageAvailable(locale);
                        if (res >= TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                            localeList.add(locale);
                        }
                    }
                }

            } else {
                Locale[] allLocales = Locale.getAvailableLocales();
                for (Locale locale : allLocales) {
                    try {
                        int res = textToSpeech.isLanguageAvailable(locale);
                        locale.getVariant();
                        boolean hasVariant = locale.getVariant().length() > 0;
                        locale.getCountry();
                        boolean hasCountry = locale.getCountry().length() > 0;

                        boolean isLocaleSupported =
                                !hasVariant && !hasCountry && res == TextToSpeech.LANG_AVAILABLE ||
                                        !hasVariant && hasCountry && res == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
                                        res == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE;


                        if (isLocaleSupported) {
                            localeList.add(locale);
                        }
                    } catch (Exception ex) {
                    }
                }
            }

            if (localeList.contains(localeUse)) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public static void notifySetupTextToSpeech(final Context context, TextToSpeech textToSpeech) {
        PackageManager pm = context.getPackageManager();
        boolean isInstalled = isPackageInstalled("com.google.android.tts", pm);

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.NAME_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean isSelectedEngine = sharedPreferences.getBoolean("isSelectedEngine", false);

        if (isInstalled) {
            String engine = textToSpeech.getDefaultEngine();

            if (engine == null || engine.isEmpty() || !engine.equals("com.google.android.tts")) {
                if (!isSelectedEngine) {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.text_to_speech_dailog_layout);
                    dialog.show();

                    TextView btnIgnore = (TextView) dialog.findViewById(R.id.btnIgnore);
                    TextView btnYes = (TextView) dialog.findViewById(R.id.btnYes);
                    final CheckBox cbSuggest = (CheckBox) dialog.findViewById(R.id.cbSuggest);

                    btnYes.setOnClickListener(view -> {
                        editor.putBoolean("isSelectedEngine", cbSuggest.isChecked());
                        editor.apply();

                        dialog.dismiss();

                        Intent intent = new Intent();
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.TextToSpeechSettings"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        ((Activity) (context)).finish();
                    });

                    btnIgnore.setOnClickListener(view -> {
                        editor.putBoolean("isSelectedEngine", cbSuggest.isChecked());
                        editor.apply();

                        dialog.cancel();
                    });
                }
            }
        } else {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(context.getResources().getString(R.string.warning))
                    .setMessage(context.getResources().getString(R.string.warning_title));

            alertDialogBuilder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());

            alertDialogBuilder.setPositiveButton("Yes", (dialog, id) -> {
                dialog.dismiss();

                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.tts")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.tts")));
                }
                ((Activity) (context)).finish();
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
