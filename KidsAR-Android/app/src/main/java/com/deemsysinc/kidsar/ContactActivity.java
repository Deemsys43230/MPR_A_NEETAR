package com.deemsysinc.kidsar;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deemsysinc.kidsar.utils.Constants;
import com.deemsysinc.kidsar.utils.MyApplication;
import com.deemsysinc.kidsar.utils.NetworkDetector;
import com.deemsysinc.kidsar.utils.PlayAudioService;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ContactActivity extends AppCompatActivity {
    ImageView close;
    EditText contact_name, contact_email;
    AppCompatEditText contact_querybox;
    Button contact_submit;
    String cname, cemail, cquery, emailbodytext;
    private boolean response = false;
    private ProgressBar progressnavigation;
    NetworkDetector networkDetector;

    TextView alertTitle, alert_message;
    Button okalert, cancelalert;
    View dialogView;
    AlertDialog alertDialog, alertDialogemail, alertDialogquery, alertDialoginternet, alertDialogvalidemail, alertDialogemailsend;
    private boolean isback = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_contact);
        networkDetector = new NetworkDetector(this);
        contact_name = findViewById(R.id.contact_name);
        contact_email = findViewById(R.id.contact_email);
        contact_querybox = findViewById(R.id.contact_querybox);
        contact_submit = findViewById(R.id.contact_submit);
        progressnavigation = findViewById(R.id.progressnavigation);
        close = findViewById(R.id.buttonclose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                finish();
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            }
        });

        contact_submit.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("IMAGE", "motion event: " + event.toString());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        contact_submit.setScaleX(0.9f);
                        contact_submit.setScaleY(0.9f);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        contact_submit.setScaleX(1f);
                        contact_submit.setScaleY(1f);
                        break;
                    }
                }
                return false;
            }
        });

        contact_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cname = contact_name.getText().toString();
                cemail = contact_email.getText().toString();
                cquery = contact_querybox.getText().toString();
                if (cname.isEmpty()) {
                    showAlert(R.string.alertStringInfo, "Enter name");
                } else if (cemail.isEmpty()) {
                    showAlert(R.string.alertStringInfo, "Enter email ID");
                } else if (cquery.isEmpty()) {
                    showAlert(R.string.alertStringInfo, "Enter query");
                } else {
                    String email = contact_email.getText().toString().trim();
                    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                    if (email.matches(emailPattern)) {
                        if (networkDetector.isConnectingToInternet()) {
                            SendMail sendMail = new SendMail();
                            sendMail.execute();
                        } else {
                            showAlert(R.string.noInternet, "Make sure your device is connected to the internet.");
                        }
                    } else {
                        showAlert(R.string.alertStringInfo, "Enter a valid email ID");
                    }
                }
            }
        });
    }

    private void showAlert(int title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertdialog, null);
        builder.setView(dialogView);
        TextView alertTitle = dialogView.findViewById(R.id.alertTitle);
        TextView alert_message = dialogView.findViewById(R.id.alert_message);
        alertTitle.setText(title);
        alert_message.setText(message);
        Button okalert = dialogView.findViewById(R.id.okalert);
        okalert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        Button cancelalert = (Button) dialogView.findViewById(R.id.cancelalert);
        cancelalert.setVisibility(View.GONE);
        alertDialog = builder.create();
        if (alertDialog != null) {
            alertDialog.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication myApp = (MyApplication) this.getApplication();
        if (myApp.wasInBackground) {
            PlayAudioService.onResumePlayer();
        }
        myApp.stopActivityTransitionTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isback = true;
        ((MyApplication) this.getApplication()).startActivityTransitionTimer(this);
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        finish();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    class SendMail extends AsyncTask<Void, Void, Void> {
        Session session;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            double currentVersion = 0;
            String Version = "";
            PackageInfo packageInfo = null;
            try {
                packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                Version = packageInfo.versionName;
                currentVersion = Double.parseDouble(Version);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            emailbodytext = Constants.contactemailbody + " Name : " + cname + "\n Email ID : " + cemail + "\n Query : " + cquery + Constants.contactemailbody2 + Version;
            contact_submit.setEnabled(false);
            progressnavigation.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", Constants.contacthost);
            properties.put("mail.smtp.socketFactory.port", "465");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.port", "465");
            session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Constants.contactfromemail, Constants.contactfrompassword);
                }
            });
            MimeMessage mimeMessage = new MimeMessage(session);
            try {
                mimeMessage.setFrom(new InternetAddress(Constants.contactfromemail));
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(Constants.contacttoemail));
                mimeMessage.setSubject(Constants.contactemailsubject);
                mimeMessage.setText(emailbodytext);
                Transport.send(mimeMessage);
                response = true;
            } catch (MessagingException e) {
                e.printStackTrace();
                Log.d("MailSendException", e.toString());
                response = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressnavigation.setVisibility(View.GONE);
            contact_submit.setEnabled(true);
            int resStringTitle = R.string.alertMailfailure;
            String resString = "Sorry! We encountered a problem while submitting your query. Please try again later.";
            if (response) {
                resStringTitle = R.string.alertMailSuccess;
                resString = "Query submitted";
            }
            //For App is in Foreground or background.
            if (!isback)
                showAlert(resStringTitle, resString);
        }
    }
}
