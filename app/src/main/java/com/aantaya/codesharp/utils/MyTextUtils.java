package com.aantaya.codesharp.utils;

import android.content.Context;
import android.text.Html;
import android.text.SpannedString;
import android.text.TextUtils;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class MyTextUtils {

    private MyTextUtils(){
        //private constructor, don't instantiate
    }

    /**
     * Method for displaying string from resources, with placeholders and preserving the html
     * formatting (bold, italics, etc)
     *
     * Found this method in SO:
     * https://stackoverflow.com/a/23562910/10043776
     *
     * @param context Application context to get resources
     * @param id resId of the string
     * @param args placeholder args
     * @return formatted CharSequence
     */
    public static CharSequence getText(@NonNull Context context, @StringRes int id, Object... args) {

        for(int i = 0; i < args.length; ++i)
            args[i] = args[i] instanceof String? TextUtils.htmlEncode((String)args[i]) : args[i];

        return Html.fromHtml(String.format(Html.toHtml(new SpannedString(context.getText(id))), args));
    }
}
