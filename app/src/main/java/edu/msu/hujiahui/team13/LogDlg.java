package edu.msu.hujiahui.team13;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.msu.hujiahui.team13.Cloud.Cloud;

public class LogDlg extends DialogFragment {

    private AlertDialog dlg;

    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";

    private boolean checked = true;
    /**
     * Create the dialog box
     * @param savedInstanceState The saved instance bundle
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.login);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.login_dlg, null);
        builder.setView(view);

        // Add a cancel button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Cancel just closes the dialog box
            }
        });


        // checkbox onclick listener
        CheckBox remember = (CheckBox) view.findViewById(R.id.rememberCheckBox);
        remember.setChecked(true);
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    checked = true;
                    Toast.makeText(getActivity(),"checked", Toast.LENGTH_SHORT).show();

                }else if (!compoundButton.isChecked()){
                    checked = false;
                    Toast.makeText(getActivity(),"unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add a OK button
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText editName = (EditText)dlg.findViewById(R.id.editName);
                EditText editPassword = (EditText)dlg.findViewById(R.id.editPassword);

                // update new preferences username and password
                MainActivity activity = (MainActivity) getActivity();
                if (checked){
                    activity.getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                            .edit()
                            .putString(PREF_USERNAME, editName.getText().toString())
                            .putString(PREF_PASSWORD, editPassword.getText().toString())
                            .commit();
                }

                log(editName.getText().toString(),editPassword.getText().toString());
            }
        });

        dlg = builder.create();
        // dlg.setContentView(R.layout.login_dlg);

        //get preference username and password
        SharedPreferences pref = getActivity().getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String username = pref.getString(PREF_USERNAME, null);
        String password = pref.getString(PREF_PASSWORD, null);

        // set preference username and password to the editText
        EditText editName = (EditText) view.findViewById(R.id.editName);
        editName.setText(username, TextView.BufferType.EDITABLE);
        EditText editPassword = (EditText) view.findViewById(R.id.editPassword);
        editPassword.setText(password, TextView.BufferType.EDITABLE);

        return dlg;
    }

    /**
     * Set true if we want to cancel
     */
    private volatile boolean ret = false;
    /**
     * Actually log in the game
     * @param name name to log-in
     * @param pw password to log-in
     */
    private void log(final String name, final String pw) {
        if (!(getActivity() instanceof MainActivity)) {
            return;
        }

        MainActivity activity = (MainActivity) getActivity();
        final MainView view = (MainView)getActivity().findViewById(R.id.mainview);

        // handle login process
        new Thread(new Runnable() {
            /*
             * Create a thread to save
             */
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                String ok = cloud.getUserNameFromCloud(name, pw);

                if (ok != null) {
                    activity.setUsername(name);
                    ret = true;
                }

                boolean fail1 = (ok == null);

                //post the state of login
                view.post(new Runnable() {

                    @Override
                    public void run() {
                        dlg.dismiss();
                        if(fail1) {
                            Toast.makeText(view.getContext(),
                                    R.string.login_fail,
                                    Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(view.getContext(),
                                    R.string.login_success,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                // start game when login successfully
                if (!fail1){
                    //activity.music.stop();
                    activity.startGhostActivity();
                }
            }
        }).start();
    }
}
