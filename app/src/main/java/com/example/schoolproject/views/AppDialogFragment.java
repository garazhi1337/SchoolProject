package com.example.schoolproject.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.schoolproject.R;

import java.util.Locale;

public class AppDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final String[] namesArray = {
                getResources().getString(R.string.eng),
                getResources().getString(R.string.pol),
                getResources().getString(R.string.rus)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.chooselang))
                .setItems(namesArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String languageToLoad = null;

                        if (namesArray[which].equals(getResources().getString(R.string.eng))) {
                            languageToLoad = "en";
                        }

                        if (namesArray[which].equals(getResources().getString(R.string.pol))) {
                            languageToLoad = "pl";
                        }

                        if (namesArray[which].equals(getResources().getString(R.string.rus))) {
                            languageToLoad = "ru";
                        }

                        setLocale(getActivity(), languageToLoad);

                        Toast.makeText(getActivity(), namesArray[which], Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

    }
}
