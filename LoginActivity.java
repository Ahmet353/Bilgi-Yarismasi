package com.example.fatihdemirel.milyoner;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class LoginActivity extends Activity {
    LoginButton loginButton;
    ProgressDialog p;
    TextView warningDialogTxt;
    LocalDatabase localDatabase = new LocalDatabase(this);
    Dialog warningDialog;
    Button guestLogin, facebookLogin;
    Button warningDialogBtn;
    CallbackManager callbackManager;
    String idFace, usernameFace;
    static User user = null;




    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.login_activity);

        loginButton = (LoginButton) findViewById(R.id.login_button);

        guestLogin = (Button) findViewById(R.id.guestLogin);

        guestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localDatabase.tableName = "User";
                user = localDatabase.getUser();
                if (user == null)
                    new CreateUserWebServiceCaller().execute();
                else {
                    new SearchUserWebServiceCaller().execute();
                }
            }
        });
        if (internetKontrol())
            start();
        else
            warningDialog.show();


        loginButton.setReadPermissions(Arrays.asList("public_profile ", "user_friends", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject userFace,
                                    GraphResponse response) {
                                //user_name.setText(response.toString());
                                try {
                                    idFace = userFace.getString("id");
                                    usernameFace = userFace.getString("name");
                                    if (user == null)
                                        new CreateFacebookUserWebServiceCaller().execute();
                                    else {
                                        localDatabase.tableName = "UserFacebook";
                                        new SearchUserWebServiceCaller().execute();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        });

    }

    public void start() {
        localDatabase.tableName = "UserFacebook";
        user = localDatabase.getUser();
        if (user != null) {
            guestLogin.setAlpha(0);
            facebookLogin.setAlpha(0);
            new SearchUserWebServiceCaller().execute();
        } else {
            guestLogin.setAlpha(1);
            facebookLogin.setAlpha(1);
        }


    }

    protected boolean internetKontrol() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            warningDialog = new Dialog(this);
            warningDialog.setCancelable(false);
            warningDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            warningDialog.requestWindowFeature(1);
            warningDialog.setContentView(R.layout.dialog_int_control);
            warningDialogTxt = (TextView) warningDialog.findViewById(R.id.dialog_int_text);
            warningDialogTxt.setText("İnternet bağlantınızı kontrol ediniz.");
            warningDialogBtn = (Button) warningDialog.findViewById(R.id.dialog_int_ok_btn);
            warningDialogBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    warningDialog.cancel();
                    finish();
                }
            });
        }
        return false;
    }


    class CreateUserWebServiceCaller extends AsyncTask<Void, Void, Void> {
        private static final String SOAP_ACTION = "http://tempuri.org/CreateUser";
        private static final String METHOD_NAME = "CreateUser";
        private static final String NAMESPACE = "http://tempuri.org/";
        private static final String URL = "http://www.recepkartal.com.tr/MilyonerService.asmx?WSDL";
        String RESULT;

        @Override
        protected void onPreExecute() {
            p = new ProgressDialog(LoginActivity.this);
            p.setMessage("Yükleniyor...");
            p.setIndeterminate(true);
            p.setCancelable(false);
            p.show();
        }






        @Override
        protected void onPostExecute(Void aVoid) {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(RESULT);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            User user = new User();
            Gamer gamer;
            JsonParserUser jsonParserUser = new JsonParserUser();
            gamer = jsonParserUser.getJsonParse(jsonArray);
            user.userID = gamer.userID;
            user.username = gamer.username;
            user.password = gamer.password;
            user.point = gamer.point;
            user.life = gamer.life;
            user.numberLevel1 = gamer.numberLevel1;
            user.numberLevel2 = gamer.numberLevel2;
            user.numberLevel3 = gamer.numberLevel3;
            p.dismiss();
            localDatabase.tableName = "User";
            localDatabase.addUser(user);
            Intent i = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(i);
            finish();
        }

        @Override
        protected Void doInBackground(Void... params) {
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

            } catch (HttpResponseException e) {
                e.printStackTrace();
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class SearchUserWebServiceCaller extends AsyncTask<Void, Void, Void> {
        private static final String SOAP_ACTION = "http://tempuri.org/UserLogin";
        private static final String METHOD_NAME = "UserLogin";
        private static final String NAMESPACE = "http://tempuri.org/";
        private static final String URL = "http://www.recepkartal.com.tr/MilyonerService.asmx?WSDL";
        String RESULT;

        @Override
        protected void onPreExecute() {
            p = new ProgressDialog(LoginActivity.this);
            p.setMessage("Yükleniyor...");
            p.setIndeterminate(true);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(RESULT);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Gamer gamer = null;
            JsonParserUser jsonParserUser = new JsonParserUser();
            gamer = jsonParserUser.getJsonParse(jsonArray);
            LocalDatabase localDatabase = new LocalDatabase(getApplicationContext());
            user.userID = gamer.userID;
            user.username = gamer.username;
            user.password = gamer.password;
            user.point = gamer.point;
            user.life = gamer.life;
            user.numberLevel1 = gamer.numberLevel1;
            user.numberLevel2 = gamer.numberLevel2;
            user.numberLevel3 = gamer.numberLevel3;
            localDatabase.updateUser(user);
            p.dismiss();
            Intent i = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(i);
            finish();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("id", user.userID);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.debug = true;

            try {
                httpTransportSE.call(SOAP_ACTION, envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                RESULT = response.toString();

            } catch (HttpResponseException e) {
                e.printStackTrace();
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class CreateFacebookUserWebServiceCaller extends AsyncTask<Void, Void, Void> {
        private static final String SOAP_ACTION = "http://tempuri.org/CreateUserFacebook";
        private static final String METHOD_NAME = "CreateUserFacebook";
        private static final String NAMESPACE = "http://tempuri.org/";
        private static final String URL = "http://www.recepkartal.com.tr/MilyonerService.asmx?WSDL";
        String RESULT;

        @Override
        protected void onPreExecute() {
            p = new ProgressDialog(LoginActivity.this);
            p.setMessage("Yükleniyor...");
            p.setIndeterminate(true);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(RESULT);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            User user = new User();
            Gamer gamer;
            JsonParserUser jsonParserUser = new JsonParserUser();
            gamer = jsonParserUser.getJsonParse(jsonArray);
            user.userID = gamer.userID;
            user.username = gamer.username;
            user.password = gamer.password;
            user.point = gamer.point;
            user.life = gamer.life;
            user.numberLevel1 = gamer.numberLevel1;
            user.numberLevel2 = gamer.numberLevel2;
            user.numberLevel3 = gamer.numberLevel3;
            p.dismiss();
            localDatabase.tableName = "UserFacebook";
            localDatabase.addUser(user);
            Intent i = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(i);
            finish();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("id", idFace);
            request.addProperty("username", usernameFace);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.debug = true;

            try {
                httpTransportSE.call(SOAP_ACTION, envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                RESULT = response.toString();

            } catch (HttpResponseException e) {
                e.printStackTrace();
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void logGet() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.demirel.fatihdemirel.milyoner", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }


}


