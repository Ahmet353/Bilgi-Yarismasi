package com.example.ahmet.milyoner;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fatihdemirel.milyoner.Question;
import com.example.fatihdemirel.milyoner.R;
import com.example.fatihdemirel.milyoner.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class AddQuestionActivity extends Activity {
    EditText questionEdt, optionAEdt, optionBEdt, optionCEdt, optionDEdt, answerEdt;
    Question question = new Question();
    TextView dialogtxt;
    Dialog dialog;
    Button btn,dialogbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addquestion_activity);

        questionEdt = (EditText) findViewById(R.id.addQuestionEdt);
        optionAEdt = (EditText) findViewById(R.id.addOptionAEdt);
        optionBEdt = (EditText) findViewById(R.id.addOptionBEdt);
        optionCEdt = (EditText) findViewById(R.id.addOptionCEdt);
        optionDEdt = (EditText) findViewById(R.id.addOptionDEdt);
        answerEdt = (EditText) findViewById(R.id.addAnswerEdt);

        dialog=new Dialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_int_control);

        dialogbtn=(Button)dialog.findViewById(R.id.dialog_int_ok_btn);
        dialogtxt=(TextView)dialog.findViewById(R.id.dialog_int_text);

        dialogbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn = (Button) findViewById(R.id.add_question_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question.setQuestion(questionEdt.getText().toString());
                question.setAnswer(answerEdt.getText().toString());
                question.setOptionA(optionAEdt.getText().toString());
                question.setOptionB(optionBEdt.getText().toString());
                question.setOptionC(optionCEdt.getText().toString());
                question.setOptionD(optionDEdt.getText().toString());

                if (!questionEdt.getText().toString().equals("") && !optionAEdt.getText().toString().equals("") && !optionBEdt.getText().toString().equals("") && !optionCEdt.getText().toString().equals("") && !optionDEdt.getText().toString().equals("") && !answerEdt.getText().toString().equals("")) {
                    if (answerEdt.getText().toString().equals(optionAEdt.getText().toString()) || answerEdt.getText().toString().equals(optionBEdt.getText().toString()) || answerEdt.getText().toString().equals(optionCEdt.getText().toString()) || answerEdt.getText().toString().equals(optionDEdt.getText().toString())) {
                        new WebServiceCaller().execute();
                        resetEdit();
                        dialogtxt.setText("Soru öneriniz için teşekkür ederiz.!!! Katkı için daha çok soru önerilerinizi bekliyoruz.");
                        dialog.show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Cevap Şıklarda Yok. Kontrol Ediniz.",Toast.LENGTH_LONG).show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), "Eksik veya hatalı giriş", Toast.LENGTH_LONG).show();
            }
        });


    }


    public void resetEdit() {
        questionEdt.setText("");
        optionAEdt.setText("");
        optionBEdt.setText("");
        optionCEdt.setText("");
        optionDEdt.setText("");
        answerEdt.setText("");

    }

    class WebServiceCaller extends AsyncTask<String, Void, Void> {
        ProgressDialog p;
        private static final String SOAP_ACTION = "http://tempuri.org/UserAddQuestion";
        private static final String METHOD_NAME = "UserAddQuestion";
        private static final String NAMESPACE = "http://tempuri.org/";
        private static final String URL = "http://www.recepkartal.com.tr/MilyonerService.asmx?WSDL";
        String RESULT;

        @Override
        protected void onPreExecute() {
            p = new ProgressDialog(AddQuestionActivity.this);
            p.setIndeterminate(true);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            p.dismiss();
        }

        @Override
        protected Void doInBackground(String... questionLevel) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("question", question.getQuestion());
            request.addProperty("answer", question.getAnswer());
            request.addProperty("optionA", question.getOptionA());
            request.addProperty("optionB", question.getOptionB());
            request.addProperty("optionC", question.getOptionC());
            request.addProperty("optionD", question.getOptionD());
            request.addProperty("answer", question.getAnswer());
            request.addProperty("userId", User.userID);

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
