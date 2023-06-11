package edu.msu.hujiahui.team13;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import edu.msu.hujiahui.team13.Cloud.Cloud;

public class SignDlg extends DialogFragment {

    private AlertDialog dlg;

    /**
     * Create the dialog box
     * @param savedInstanceState The saved instance bundle
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.signup);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        //@SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.signin_dlg, null);
        builder.setView(view);

        // Add a cancel button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Cancel just closes the dialog box
            }
        });


        // Add a OK button
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText editName = (EditText)dlg.findViewById(R.id.editName);
                EditText editPassword = (EditText)dlg.findViewById(R.id.editPassword);
                EditText editconfirmPassword = (EditText)dlg.findViewById(R.id.editconfirmPassword);

                MainActivity activity = (MainActivity) getActivity();
                final MainView view = (MainView)getActivity().findViewById(R.id.mainview);

                if (editPassword.getText().toString().equals(editconfirmPassword.getText().toString())){
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(),
                                    R.string.sign_success,
                                    Toast.LENGTH_SHORT).show();

                        }
                    });
                    save(editName.getText().toString(),editPassword.getText().toString());
                }

                else{
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(),
                                    R.string.sign_fail,
                                    Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

        EditText editPassword = (EditText)view.findViewById(R.id.editPassword);

        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView text = (TextView) view.findViewById(R.id.complexity);
                EditText editPassword = (EditText)dlg.findViewById(R.id.editPassword);
                String complexity = check_complexity(editPassword);
                if (complexity.equals("low"))
                    text.setText(R.string.complexity_low);
                else if (complexity.equals("mid"))
                    text.setText(R.string.complexity_mid);
                else if (complexity.equals("high"))
                    text.setText(R.string.complexity_high);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        dlg = builder.create();
        //dlg.setContentView(R.layout.signin_dlg);

        return dlg;
    }


    // password complexity identify
    private String check_complexity(TextView text) {
        String pw = text.getText().toString();
        int upperCase = 0;
        int lowerCase = 0;
        int digit = 0;
        int special = 0;

        for (int i=0; i < pw.length(); i++)
        {
            if(Character.isUpperCase(pw.charAt(i)))
                upperCase = 1;
            else if (Character.isLowerCase(pw.charAt(i)))
                lowerCase = 1;
            else if (Character.isDigit(pw.charAt(i)))
                digit = 1;
            else
                special = 1;
        }

        int sum = upperCase + lowerCase + digit + special;
        if (pw.length() > 5) sum += 1;
        String ret;
        if (sum <=1)
            ret = "low";
        else if (sum>=4)
            ret = "high";
        else
            ret = "mid";
        return ret;
    }

    /**
     * Actually save the User
     * @param name name to save it under
     * @param pw password to save it under
     */
    private void save(final String name, final String pw) {

        if (!(getActivity() instanceof MainActivity)) {
            return;
        }

        final MainActivity activity = (MainActivity) getActivity();
        //final MainView view = (MainView) activity.findViewById(R.id.hatterView);

        new Thread(new Runnable() {

            /*
             * Create a thread to save
             */
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean ok = cloud.saveUserToCloud(name, pw);

            }
        }).start();
    }
}
