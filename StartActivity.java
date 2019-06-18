package com.example.ahmet.milyoner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fatihdemirel.milyoner.Json.JsonParserNotification;
import com.example.fatihdemirel.milyoner.others.Notification;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.squareup.picasso.Picasso;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class StartActivity extends Activity {
    PublisherInterstitialAd mPublisherInterstitialAd;
    Button newGameBtn, bestGamerBtn, soundBtn, addQuestionBtn, likeBtn;
    ImageView gamerPhoto, notificationImg;
    TextView scoreTv, userNameTv;
    boolean soundEffectStatus;
    public Dialog notificationdialog;
    Typeface ty;

    public void reklam() {
        if (mPublisherInterstitialAd.isLoaded()) {
            mPublisherInterstitialAd.show();
        } else {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        scoreTv.setText(String.valueOf(User.point + " ₺"));
        userNameTv.setText(User.username);
        saveUser();
    }

    @Override
    public void onBackPressed() {
        reklam();
        Button dialogOk, dialogCancel;
        TextView dialogText;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.diaglog_view);
        dialogOk = (Button) dialog.findViewById(R.id.dialog_ok_btn);
        dialogCancel = (Button) dialog.findViewById(R.id.dialog_cancel_btn);
        dialogText = (TextView) dialog.findViewById(R.id.dialog_text);
        dialogText.setText("Çıkmak istediğinize emin misiniz ?");
        dialogOk.setText("Evet");
        dialogCancel.setText("Hayır");
        dialogText.setTypeface(ty);
        dialogOk.setTypeface(ty);
        dialogCancel.setTypeface(ty);
        dialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                dialog.cancel();
            }
        });
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        ty = Typeface.createFromAsset(getAssets(), "Phenomena-Regular.otf");

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        saveUser();
        gamerPhoto = (ImageView) findViewById(R.id.gamer_photo);
        newGameBtn = (Button) findViewById(R.id.new_game_btn);
        bestGamerBtn = (Button) findViewById(R.id.best_gamer_btn);
        scoreTv = (TextView) findViewById(R.id.score_textview);
        soundBtn = (Button) findViewById(R.id.sound_btn);
        userNameTv = (TextView) findViewById(R.id.gamer_name);
        addQuestionBtn = (Button) findViewById(R.id.add_question);
        likeBtn = (Button) findViewById(R.id.like_btn);

        mPublisherInterstitialAd = new PublisherInterstitialAd(this);
        mPublisherInterstitialAd.setAdUnitId("ca-app-pub-1393573660153712/8864348383");
        mPublisherInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();

        setFont();
        new NotificationWebServiceCaller().execute();


        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri link = Uri.parse("https://play.google.com/store/apps/details?id=com.demirel.fatihdemirel.milyoner");
                Intent i = new Intent(Intent.ACTION_DEFAULT, link);
                startActivity(i);
            }
        });

        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StartActivity.this, AddQuestionActivity.class);
                startActivity(i);
            }
        });
        soundEffectStatus = true;
        soundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (soundEffectStatus == true) {
                    soundEffectStatus = false;
                    soundBtn.setBackgroundResource(R.drawable.soundoff_btn);
                } else if (soundEffectStatus == false) {
                    soundEffectStatus = true;
                    soundBtn.setBackgroundResource(R.drawable.soundon_btn);
                }
            }
        });

        newGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StartActivity.this, GameActivity.class);
                i.putExtra("soundEffectStatus", soundEffectStatus);
                startActivity(i);

            }
        });
        bestGamerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StartActivity.this, BestGamerActivity.class);
                startActivity(i);
            }
        });
        if (User.userID.length() > 10) {

        Picasso.with(this).load("https://graph.facebook.com/" + User.userID.toString() + "/picture?type=large").into(gamerPhoto);
        }
    }

    private void requestNewInterstitial() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherInterstitialAd.loadAd(adRequest);
    }

    public void getNotification(List<Notification> listNot) {

        if (!listNot.isEmpty()) {
            notificationdialog = null;
            notificationdialog = new Dialog(this);
            notificationdialog.setCancelable(true);
            notificationdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            notificationdialog.requestWindowFeature(1);
            notificationdialog.setContentView(R.layout.notificationdiolog_view);
            notificationImg = (ImageView) notificationdialog.findViewById(R.id.notificationImg);
            notificationImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notificationdialog.cancel();
                }
            });

            Picasso.with(notificationdialog.getContext()).load(listNot.get(0).link).into(notificationImg);
            notificationdialog.show();

        }
    }

    public void setFont() {
        newGameBtn.setTypeface(ty);
        bestGamerBtn.setTypeface(ty);
        addQuestionBtn.setTypeface(ty);
        userNameTv.setTypeface(ty);
        scoreTv.setTypeface(ty);
    }

    public void saveUser() {
        LocalDatabase localDatabase = new LocalDatabase(this);
        User user;
        user = localDatabase.getUser();
        localDatabase.updateUser(user);
        new WebServiceCaller().execute();
    }

    class WebServiceCaller extends AsyncTask<String, Void, Void> {
        private static final String SOAP_ACTION = "http://tempuri.org/UpdateUser";
        private static final String METHOD_NAME = "UpdateUser";
        private static final String NAMESPACE = "http://tempuri.org/";
        private static final String URL = "http://www.recepkartal.com.tr/MilyonerService.asmx?WSDL";
        String RESULT;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

        @Override
        protected Void doInBackground(String... questionLevel) {
            User user = new User();
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("id", user.userID);
            request.addProperty("username", user.username);
            request.addProperty("password", user.password);
            request.addProperty("point", user.point);
            request.addProperty("life", user.life);
            request.addProperty("numberlevel1", user.numberLevel1);
            request.addProperty("numberlevel2", user.numberLevel2);
            request.addProperty("numberlevel3", user.numberLevel3);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.debug = true;

            try {
                httpTransportSE.call(SOAP_ACTION, envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                RESULT = response.toString();


            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();


            }
            return null;
        }
    }

    class NotificationWebServiceCaller extends AsyncTask<String, Void, Void> {
        private static final String SOAP_ACTION = "http://tempuri.org/GetNotification";
        private static final String METHOD_NAME = "GetNotification";
        private static final String NAMESPACE = "http://tempuri.org/";
        private static final String URL = "http://www.recepkartal.com.tr/MilyonerService.asmx?WSDL";
        String RESULT;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            JsonParserNotification j = new JsonParserNotification();
            List<Notification> n = j.getNotification(RESULT);
            getNotification(n);
        }

        @Override
        protected Void doInBackground(String... questionLevel) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.debug = true;

            try {
                httpTransportSE.call(SOAP_ACTION, envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                RESULT = response.toString();


            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();


            }
            return null;
        }
    }

}
