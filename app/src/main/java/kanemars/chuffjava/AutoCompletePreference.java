package kanemars.chuffjava;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.EditTextPreference;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.*;
import kanemars.KaneHuxleyJavaConsumer.StationCodes;

import static android.widget.Toast.*;
import static kanemars.KaneHuxleyJavaConsumer.StationCodes.STATION_CODES_SET;

public class AutoCompletePreference extends EditTextPreference {

    private AutoCompleteTextView autoCompleteTextView = null;
    String my_var; //keep track!

    public AutoCompletePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        autoCompleteTextView = new AutoCompleteTextView(context, attrs);
        autoCompleteTextView.setThreshold(0);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, StationCodes.STATION_CODES);
        autoCompleteTextView.setAdapter(adapter);

        Dialog dlg = getDialog();
        if(dlg instanceof AlertDialog)
        {
            AlertDialog alertDlg = (AlertDialog)dlg;
            Button btn = alertDlg.getButton(AlertDialog.BUTTON_POSITIVE);
            btn.setEnabled(false);
        }

        // Following piece of code restricts user to select from autocompletion textview only
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus) {
                    String str = autoCompleteTextView.getText().toString();

                    ListAdapter listAdapter = autoCompleteTextView.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(str.compareTo(temp) == 0) {
                            return;
                        }
                    }

                    autoCompleteTextView.setText("");

                }
            }
        });

    }

    @Override
    protected void onBindDialogView(View view) {
        AutoCompleteTextView editText = autoCompleteTextView;
        editText.setText(getText());

        ViewParent oldParent = editText.getParent();
        if (oldParent != view) {
            if (oldParent != null) {
                ((ViewGroup) oldParent).removeView(editText);
            }
            onAddEditTextToDialogView(view, editText);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            String value = autoCompleteTextView.getText().toString();
            if (callChangeListener(value)) {
                setText(value);
            }
        }
    }
}