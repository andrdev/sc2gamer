package com.github.andrdev.sc2gamer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;


public class AlarmTimePreference extends DialogPreference {
    private int number = 0;
    private EditText mEditText = null;

    public AlarmTimePreference(final Context context,
                                  final AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.alarm_time);
        this.setPositiveButtonText("Set");
        this.setNegativeButtonText("Cancel");
        mEditText = (EditText)getDialog().findViewById(R.id.numberPicker);
        mEditText.setText("44");
    }


    @Override
    protected void onDialogClosed(final boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            String s = mEditText.getText().toString();
            number = Integer.valueOf(s);
            Log.d("DreeTimep", "d+"+number);
            final int time = Integer.valueOf(this.number);

            if (this.callChangeListener(time)) {
                this.persistInt(time);
            }
        }
    }
}

//    private static final int DEFAULT_VALUE = 0;
//    private int mCurrentValue;
//    NumberPicker picker;
//
//    public AlarmTimePreference(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        setDialogLayoutResource(R.layout.alarm_time);
////        picker = new NumberPicker(getContext());
////        setPositiveButtonText(android.R.string.ok);
////        setNegativeButtonText(android.R.string.cancel);
////        setDialogIcon(null);
//    }
//
//    @Override
//    protected void onBindDialogView(View view) {
//        super.onBindDialogView(view);
//
//
//        picker = (NumberPicker) view.findViewById(R.id.numberPicker);
//        mCurrentValue = getPersistedInt(DEFAULT_VALUE);
//        picker.setMaxValue(100);
//        picker.setMinValue(0);
//// TODO this should be an XML parameter:
//        picker.setValue(mCurrentValue);
//    }
//
//
//    @Override
//    public void onClick(DialogInterface dialog, int which) {
//        super.onClick(dialog, which);
//        this.setPersistent(true);
//        if (which == DialogInterface.BUTTON_POSITIVE) {
////            persistInt();
////            callChangeListener(initialValue);
//        }
//    }
//
////    @Override
////    protected void onSetInitialValue(boolean restorePersistedValue,
////                                     Object defaultValue) {
////        int def = (defaultValue instanceof Number) ? (Integer) defaultValue
////                : (defaultValue != null) ? Integer.parseInt(defaultValue.toString()) : 1;
////        if (restorePersistedValue) {
////            this.initialValue = getPersistedInt(def);
////        } else this.initialValue = (Integer) defaultValue;
////    }
//
//    @Override
//    protected Object onGetDefaultValue(TypedArray a, int index) {
//        return a.getInt(index, 1);
//    }
//
//    @Override
//    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
//
//        if (restorePersistedValue) {
//            // Restore existing state
//            mCurrentValue = PreferenceManager.getDefaultSharedPreferences(getContext())
//                    .getInt("pref_alarm_time", DEFAULT_VALUE);
//            Log.d("dree", "" + mCurrentValue);
//        } else {
//            // Set default state from the XML attribute
//            mCurrentValue = (Integer) defaultValue;
//            persistInt(mCurrentValue);
//        }
//    }
//
//    @Override
//    protected void onDialogClosed(boolean positiveResult) {
//        if (positiveResult) {
//            persistInt(mCurrentValue);
//        }
//    }
//}

//
//
//
//    private int mMin, mMax, mDefault;
//    private String mMaxExternalKey, mMinExternalKey;
//    private NumberPicker mNumberPicker;
//    public AlarmTimePreference(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        TypedArray dialogType = context.obtainStyledAttributes(attrs,
//                com.android.internal.R.styleable.DialogPreference, 0, 0);
//        TypedArray numberPickerType = context.obtainStyledAttributes(attrs,
//                R.styleable.NumberPickerPreference, 0, 0);
//        mMaxExternalKey = numberPickerType.getString(R.styleable.NumberPickerPreference_maxExternal);
//        mMinExternalKey = numberPickerType.getString(R.styleable.NumberPickerPreference_minExternal);
//        mMax = numberPickerType.getInt(R.styleable.NumberPickerPreference_max, 5);
//        mMin = numberPickerType.getInt(R.styleable.NumberPickerPreference_min, 0);
//        mDefault = dialogType.getInt(com.android.internal.R.styleable.Preference_defaultValue, mMin);
//        dialogType.recycle();
//        numberPickerType.recycle();
//    }
//    @Override
//    protected View onCreateDialogView() {
//        int max = mMax;
//        int min = mMin;
//External values
//        if (mMaxExternalKey != null) {
//            max = getSharedPreferences().getInt(mMaxExternalKey, mMax);
//        }
//        if (mMinExternalKey != null) {
//            min = getSharedPreferences().getInt(mMinExternalKey, mMin);
//        }
//        LayoutInflater inflater =
//                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.alarm_time, null);
//        mNumberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
//// Initialize state
//        mNumberPicker.setMaxValue(max);
//        mNumberPicker.setMinValue(min);
//        mNumberPicker.setValue(getPersistedInt(mDefault));
//        mNumberPicker.setWrapSelectorWheel(false);
//// No keyboard popup
////        EditText textInput = (EditText) mNumberPicker.findViewById();
////        textInput.setCursorVisible(false);
////        textInput.setFocusable(false);
////        textInput.setFocusableInTouchMode(false);
//        return view;
//    }
//    @Override
//    protected void onDialogClosed(boolean positiveResult) {
//        if (positiveResult) {
//            persistInt(mNumberPicker.getValue());
//        }
//    }
//}
//
