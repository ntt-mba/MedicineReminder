package com.dab.medireminder.data.model;

import android.content.Context;

import com.dab.medireminder.R;

import java.util.Iterator;
import java.util.Set;

public enum IgnoreReason {
    SUSPENDED(R.string.reason_suspended),
    SILENT(R.string.reason_silent),
    CALL(R.string.reason_call),
    SCREEN_OFF(R.string.reason_screen_off),
    SCREEN_ON(R.string.reason_screen_on),
    HEADSET_OFF(R.string.reason_headset_off),
    HEADSET_ON(R.string.reason_headset_on);

    private final int stringId;

    IgnoreReason(int resId) {
        this.stringId = resId;
    }

    /**
     * @param c Context required to get the string resource.
     * @return The user-visible string for this ignore reason.
     */
    String getString(Context c) {
        return c.getString(stringId);
    }

    /**
     * Converts a set of ignore reasons to a comma-separated string.
     *
     * @param reasons The set to be converted.
     * @param c       Context required to get string resources.
     * @return The resulting string.
     */
    public static String convertSetToString(Set<IgnoreReason> reasons, Context c) {
        StringBuilder builder = new StringBuilder();
        Iterator<IgnoreReason> iterator = reasons.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next().getString(c));
            if (iterator.hasNext()) builder.append(", ");
        }
        return builder.toString();
    }
}
