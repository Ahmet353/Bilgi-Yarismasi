package com.example.fatihdemirel.milyoner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.fatihdemirel.milyoner.Json.JsonParserSquence;

import org.json.JSONArray;
import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class BestGamerActivity extends Activity {
    RecyclerView recyclerView;
    BestGamerRecyclerAdapter bg;
    ProgressDialog p;
    TextView squenceTxt;
    List<Gamer> list;
    Typeface ty;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bestgamer_activity);

        squenceTxt = (TextView) findViewById(R.id.squenceNumberText);
        recyclerView = (RecyclerView) findViewById(R.id.bestGamerRecycler);
        recyclerView.setHasFixedSize(true);
        ty = Typeface.createFromAsset(getAssets(), "Phenomena-Regular.otf");
        new WebServiceCaller().execute();
        new SquenceWebServiceCaller().execute();

    }

    public void getData() {
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        bg = new BestGamerRecyclerAdapter(list, this, ty);
        recyclerView.setAdapter(bg);

    }

    class WebServiceCaller extends AsyncTask<String, Void, Void> {
        private static final String SOAP_ACTION = "http://tempuri.org/GetUsers";
        private static final String METHOD_NAME = "GetUsers";
        private static final String NAMESPACE = "http://tempuri.org/";
        private static final String URL = "http://www.recepkartal.com.tr/MilyonerService.asmx?WSDL";
        String RESULT;


        @Override
        protected void onPreExecute() {
            p = new ProgressDialog(BestGamerActivity.this);
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
            JsonParserUser jsonParser = new JsonParserUser();
            list = jsonParser.getJsonParseList(jsonArray);
            getData();
            p.dismiss();
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

    class SquenceWebServiceCaller extends AsyncTask<String, Void, Void> {
        private static final String SOAP_ACTION = "http://tempuri.org/GetSequence";
        private static final String METHOD_NAME = "GetSequence";
        private static final String NAMESPACE = "http://tempuri.org/";
        private static final String URL = "http://www.recepkartal.com.tr/MilyonerService.asmx?WSDL";
        String RESULT;


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            JsonParserSquence jsonParserSquence = new JsonParserSquence();
            squenceTxt.setText(jsonParserSquence.getSquenceofUser(RESULT));
        }

        @Override
        protected Void doInBackground(String... questionLevel) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("id", User.userID);

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
