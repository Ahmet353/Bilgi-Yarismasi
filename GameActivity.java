package com.example.ahmet.milyoner;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GameActivity extends Activity {
    PublisherInterstitialAd mPublisherInterstitialAd;
    List<Question> questions;
    Random rand = new Random();
    int trueOptionID;
    int changeJoker=0;
    int stepID;
    int time = 60;
    int counter;
    User user= new User();
    boolean jokerDouble = false;
    boolean jokerDoubleUseable=false;
    MediaPlayer mp = null;
    boolean soundEffectStatus;
    Typeface ty;
    ProgressDialog p;

    TextView questionTv, questionValueTv, dialogText;
    public static TextView timeText;
    Button option1, option2, option3, option4, change_question, spectator, fiftyfifty, doubleAnswer,dialog2Ok, dialog2Cancel,dialogOk, dialogCancel;
    ImageView moneyTreeImg;
    ArrayList<String> mMoneyTreeValues;
    ArrayList<Integer> mScore = new ArrayList<>();

    @Override
    public void onBackPressed() {
        backPressDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        ty=Typeface.createFromAsset(getAssets(),"Phenomena-Regular.otf");

        questionTv = (TextView) findViewById(R.id.question_tv);
        timeText = (TextView) findViewById(R.id.time_text);
        option1 = (Button) findViewById(R.id.option_1);
        option2 = (Button) findViewById(R.id.option_2);
        option3 = (Button) findViewById(R.id.option_3);
        option4 = (Button) findViewById(R.id.option_4);
        spectator = (Button) findViewById(R.id.spectator_btn);
        fiftyfifty = (Button) findViewById(R.id.fiftyfifty_btn);
        doubleAnswer = (Button) findViewById(R.id.double_answer_btn);


        change_question = (Button) findViewById(R.id.change_question_btn);
        questionValueTv = (TextView) findViewById(R.id.question_value_tv);
        moneyTreeImg = (ImageView) findViewById(R.id.money_tree_img);
        fiftyfifty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jokerFiftyfifty(view);
            }
        });
        spectator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jokerSpectatorDialog(view);
            }
        });
        change_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jokerChangeQuestion(view);
            }
        });
        doubleAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jokerDoubleAnswer(view);
            }
        });
        moneyTreeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GameActivity.this, MoneytreeActivity.class);
                i.putExtra("stepID", stepID);
                i.putExtra("moneyTreeValues", mMoneyTreeValues);
                startActivity(i);
            }
        });
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game(option1, 0);
            }
        });
        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game(option2, 1);
            }
        });
        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game(option3, 2);
            }
        });
        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game(option4, 3);
            }
        });
        soundEffectStatus = getIntent().getExtras().getBoolean("soundEffectStatus");
        new WebServiceCaller().execute("1");

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
    }

    class WebServiceCaller extends AsyncTask<String, Void, Void> {
        private static final String SOAP_ACTION = "http://tempuri.org/QuestionGetNumberLevel";
        private static final String METHOD_NAME = "QuestionGetNumberLevel";
        private static final String NAMESPACE = "http://tempuri.org/";
        private static final String URL = "http://www.recepkartal.com.tr/MilyonerService.asmx?WSDL";
        String RESULT;
        List<Question> liste = new ArrayList<Question>();


        @Override
        protected void onPreExecute() {
            p = new ProgressDialog(GameActivity.this);
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
            JsonParser jsonParser = new JsonParser();
            liste = jsonParser.getJsonParseList(jsonArray);
            p.dismiss();
            questions=liste;
            startGame();
        }


        @Override
        protected Void doInBackground(String... questionLevel) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("numberlevel1",user.numberLevel1);
            request.addProperty("numberlevel2",user.numberLevel2);
            request.addProperty("numberlevel3",user.numberLevel3);

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

    public void startGame() {
        moneyTree();
        changeJoker=0;
        jokerDouble=false;
        jokerDoubleUseable=false;
        resetNewGame();
        stepID = 0;
        timer.start();
        getQuestionBar();
    }

    public void game(Button v, int selectOptionInd) {
        if(!jokerDouble)
            optionEnable(false);
        timer.cancel();
        if (selectOptionInd == 5) {
            getQuestionBar();
        } else if (!truefalseController(selectOptionInd)) {
            if (soundEffectStatus)
                soundEffect("wrong");
            optionAffect(v, selectOptionInd);
            Toast.makeText(this, "Yanlış Cevap", Toast.LENGTH_SHORT).show();
        } else if (truefalseController(selectOptionInd)) {
            if (soundEffectStatus)
                soundEffect("correct");
            optionAffect(v, selectOptionInd);
            stepID++;
        }
        v.setEnabled(false);
    }

    public void finishDialog() {
        saveUser();
        reklam();
        Button dialogOk, dialogCancel;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.diaglog_view);
        dialogOk = (Button) dialog.findViewById(R.id.dialog_ok_btn);
        dialogCancel = (Button) dialog.findViewById(R.id.dialog_cancel_btn);
        dialogText = (TextView) dialog.findViewById(R.id.dialog_text);
        dialogText.setText("Kazanılan Miktar \n" + mMoneyTreeValues.get(stepID) + " TL");
        dialogOk.setText("YeniOyun");
        dialogCancel.setText("Anasayfa");
        dialogText.setTypeface(ty);
        dialogOk.setTypeface(ty);
        dialogCancel.setTypeface(ty);
        dialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WebServiceCaller().execute("1");
                dialog.cancel();
            }
        });
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                if (soundEffectStatus)
                    mp.stop();
                finish();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

    }

    public void bidDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.diaglog_view);
        dialog2Ok = (Button) dialog.findViewById(R.id.dialog_ok_btn);
        dialog2Cancel = (Button) dialog.findViewById(R.id.dialog_cancel_btn);
        dialogText = (TextView) dialog.findViewById(R.id.dialog_text);
        dialogText.setText("Kazanılan Miktar \n" + mMoneyTreeValues.get(stepID) + " TL");
        dialog2Ok.setText("Devam");
        dialog2Cancel.setText("Çekil");
        dialogText.setTypeface(ty);
        dialog2Ok.setTypeface(ty);
        dialog2Cancel.setTypeface(ty);
        dialog2Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQuestionBar();
                dialog.cancel();
            }
        });
        dialog2Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishGame();
                dialog.cancel();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void backPressDialog() {
        timer.cancel();
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
                finishGame();
                timer.cancel();
                dialog.cancel();
            }
        });
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.start();
                dialog.cancel();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void jokerSpectatorDialog(View view) {
        timer.cancel();
        view.setEnabled(false);
        view.setAlpha((float) 0.4);
        ProgressBar progressBarA, progressBarB, progressBarC, progressBarD;
        TextView optionApercentIndex, optionBpercentIndex, optionCpercentIndex, optionDpercentIndex;
        Button cancel;

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_spectator);
        progressBarA = (ProgressBar) dialog.findViewById(R.id.progressA);
        progressBarB = (ProgressBar) dialog.findViewById(R.id.progressB);
        progressBarC = (ProgressBar) dialog.findViewById(R.id.progressC);
        progressBarD = (ProgressBar) dialog.findViewById(R.id.progressD);
        optionApercentIndex = (TextView) dialog.findViewById(R.id.optionApercentText);
        optionBpercentIndex = (TextView) dialog.findViewById(R.id.optionBpercentText);
        optionCpercentIndex = (TextView) dialog.findViewById(R.id.optionCpercentText);
        optionDpercentIndex = (TextView) dialog.findViewById(R.id.optionDpercentText);
        cancel = (Button) dialog.findViewById(R.id.dialog_spectator_cancel);
        int percent[] = new int[4];
        for (int i = 0; i < 4; i++) {
            if (i != trueOptionID) {
                percent[i] = rand.nextInt(25) + 1;
            }
        }
        percent[trueOptionID] = 100 - (percent[(trueOptionID + 1) % 4] + percent[(trueOptionID + 2) % 4] + percent[(trueOptionID + 3) % 4]);
        progressBarA.setProgress(percent[0]);
        progressBarB.setProgress(percent[1]);
        progressBarC.setProgress(percent[2]);
        progressBarD.setProgress(percent[3]);
        optionApercentIndex.setText("%" + String.valueOf(percent[0]));
        optionBpercentIndex.setText("%" + String.valueOf(percent[1]));
        optionCpercentIndex.setText("%" + String.valueOf(percent[2]));
        optionDpercentIndex.setText("%" + String.valueOf(percent[3]));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.start();
                dialog.cancel();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void jokerFiftyfifty(View view) {
        view.setEnabled(false);
        view.setAlpha((float) 0.4);
        int a, i = 0;
        a = rand.nextInt(2) + 1;
        do {
            if ((trueOptionID + a) % 4 == 0 & i != 2) {
                option1.setEnabled(false);
                option1.setAlpha((float) 0.4);
                a++;
                i++;
            }
            if ((trueOptionID + a) % 4 == 1 & i != 2) {
                option2.setEnabled(false);
                option2.setAlpha((float) 0.4);
                a++;
                i++;
            }
            if ((trueOptionID + a) % 4 == 2 & i != 2) {
                option3.setEnabled(false);
                option3.setAlpha((float) 0.4);
                a++;
                i++;
            }
            if ((trueOptionID + a) % 4 == 3 & i != 2) {
                option4.setEnabled(false);
                option4.setAlpha((float) 0.4);
                a++;
                i++;
            }
        } while (i != 2);
    }

    public void jokerChangeQuestion(View view) {
        view.setEnabled(false);
        view.setAlpha((float) 0.4);
        changeJoker=1;
        game(change_question, 5);
    }

    public void jokerDoubleAnswer(View view) {
        view.setEnabled(false);
        view.setAlpha((float) 0.4);
        jokerDouble = true;
        jokerDoubleUseable=true;
    }

    //biten fonksiyonlar
    public void optionAffect(final Button button, final int selectOptionInd) {
        counter = 0;
        button.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (counter == 10) {
                    if (truefalseController(selectOptionInd)) {
                        if(stepID!=12)
                            bidDialog();
                        else
                            finishGame();
                    } else if (!truefalseController(selectOptionInd) && !jokerDouble) {
                        if (stepID >= 7)
                            stepID = 7;
                        else if (stepID >= 2)
                            stepID = 2;
                        else
                            stepID = 0;
                        finishGame();
                    }
                    button.removeCallbacks(this);
                    jokerDouble = false;
                    return;
                }
                //yanlıs secimde doğru secimle beraber gosterme
                if (truefalseController(selectOptionInd))
                    button.setBackgroundResource(counter % 2 == 1 ? R.drawable.trueoption : R.drawable.option);
                else if (!truefalseController(selectOptionInd) && jokerDouble == true) {
                    button.setBackgroundResource(counter % 2 == 1 ? R.drawable.falseoption : R.drawable.option);
                } else {
                    button.setBackgroundResource(counter % 2 == 1 ? R.drawable.falseoption : R.drawable.option);
                    if (trueOptionID == 0)
                        option1.setBackgroundResource(counter % 2 == 1 ? R.drawable.trueoption : R.drawable.option);
                    else if (trueOptionID == 1)
                        option2.setBackgroundResource(counter % 2 == 1 ? R.drawable.trueoption : R.drawable.option);
                    else if (trueOptionID == 2)
                        option3.setBackgroundResource(counter % 2 == 1 ? R.drawable.trueoption : R.drawable.option);
                    else if (trueOptionID == 3)
                        option4.setBackgroundResource(counter % 2 == 1 ? R.drawable.trueoption : R.drawable.option);
                }
                button.postDelayed(this, 100);
                counter++;
            }
        }, 0);
    }

    public void finishGame() {
        timer.cancel();
        setPoint(stepID);
        finishDialog();
    }

    public void setPoint(int ind){
        User.point+=mScore.get(ind);
    }

    public void getQuestionBar() {
        if(stepID<=1)
            user.numberLevel1+=1;
        else if (stepID<=7)
            user.numberLevel2+=1;
        else if (stepID<=12)
            user.numberLevel3+=1;
        optionEnable(true);
        if (soundEffectStatus)
            soundEffect("question");
        resetNextQuestion();
        if (stepID >= 7 && !jokerDoubleUseable) {
            doubleAnswer.setEnabled(true);
            doubleAnswer.setAlpha((float) 1);
        } else {
            doubleAnswer.setEnabled(false);
            doubleAnswer.setAlpha((float) 0.4);
        }
        if(stepID<2)
            time=15;
        else if(stepID<7)
            time=30;
        else if(stepID<12)
             time=60;
        if(questions.get(stepID+changeJoker).getAnswer().equals(questions.get(stepID+changeJoker).getOptionA()))
            trueOptionID=0;
        else if(questions.get(stepID+changeJoker).getAnswer().equals(questions.get(stepID+changeJoker).getOptionB()))
            trueOptionID=1;
        else if(questions.get(stepID+changeJoker).getAnswer().equals(questions.get(stepID+changeJoker).getOptionC()))
            trueOptionID=2;
        else if(questions.get(stepID+changeJoker).getAnswer().equals(questions.get(stepID+changeJoker).getOptionD()))
            trueOptionID=3;
        questionTv.setText(questions.get(stepID+changeJoker).getQuestion());
        option1.setText(questions.get(stepID+changeJoker).getOptionA());
        option2.setText(questions.get(stepID+changeJoker).getOptionB());
        option3.setText(questions.get(stepID+changeJoker).getOptionC());
        option4.setText(questions.get(stepID+changeJoker).getOptionD());
        questionValueTv.setText(mMoneyTreeValues.get(stepID + 1) + " TL Değerindeki Soru");

    }

    CountDownTimer timer = new CountDownTimer((time + 1) * 1000, 1000) {
        @Override
        public void onTick(long l) {
            time--;
            timeText.setText(String.valueOf(time));
            if (time == 0) {
                optionEnable(false);
                if (stepID >= 7)
                    stepID = 7;
                else if (stepID >= 2)
                    stepID = 2;
                else
                    stepID = 0;
                finishGame();
                timer.cancel();
            }
        }



        @Override
        public void onFinish() {

        }
    };

    public void resetNextQuestion() {
        timer.cancel();
        timer.start();
        int ind = R.drawable.option;
        option1.setBackgroundResource(ind);
        option2.setBackgroundResource(ind);
        option3.setBackgroundResource(ind);
        option4.setBackgroundResource(ind);
        option1.setEnabled(true);
        option2.setEnabled(true);
        option3.setEnabled(true);
        option4.setEnabled(true);
        option1.setAlpha((float) 1);
        option2.setAlpha((float) 1);
        option3.setAlpha((float) 1);
        option4.setAlpha((float) 1);
    }

    public void optionEnable(boolean status){
        option1.setEnabled(status);
        option2.setEnabled(status);
        option3.setEnabled(status);
        option4.setEnabled(status);
    }

    public boolean truefalseController(int ind) {
        if (trueOptionID == ind)
            return true;
        else
            return false;
    } public void moneyTree() {
        mMoneyTreeValues = new ArrayList<String>();
        mMoneyTreeValues.add("0");
        mMoneyTreeValues.add("500");
        mMoneyTreeValues.add("1.000");
        mMoneyTreeValues.add("2.000");
        mMoneyTreeValues.add("3.000");
        mMoneyTreeValues.add("5.000");
        mMoneyTreeValues.add("7.500");
        mMoneyTreeValues.add("15.000");
        mMoneyTreeValues.add("30.000");
        mMoneyTreeValues.add("60.000");
        mMoneyTreeValues.add("125.000");
        mMoneyTreeValues.add("250.000");
        mMoneyTreeValues.add("1.000.000");
        mScore.add(0);
        mScore.add(500);
        mScore.add(1000);
        mScore.add(2000);
        mScore.add(3000);
        mScore.add(5000);
        mScore.add(7500);
        mScore.add(15000);
        mScore.add(30000);
        mScore.add(60000);
        mScore.add(125000);
        mScore.add(250000);
        mScore.add(1000000);
    }

    public void resetNewGame() {
        change_question.setEnabled(true);
        change_question.setAlpha((float) 1);
        spectator.setEnabled(true);
        spectator.setAlpha((float) 1);
        fiftyfifty.setEnabled(true);
        fiftyfifty.setAlpha((float) 1);
        doubleAnswer.setEnabled(true);
        doubleAnswer.setAlpha((float) 1);
    }

    public void soundEffect(String effectChoose) {
        if (effectChoose.equals("question"))
            mp = MediaPlayer.create(getApplicationContext(), R.raw.question);
        if (effectChoose.equals("correct"))
            mp = MediaPlayer.create(getApplicationContext(), R.raw.correct);
        if (effectChoose.equals("wrong"))
            mp = MediaPlayer.create(getApplicationContext(), R.raw.wrong);
        mp.start();
    }

    public void reklam(){
        if (mPublisherInterstitialAd.isLoaded()) {
            mPublisherInterstitialAd.show();
        } else {
        }
    }

    private void requestNewInterstitial() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherInterstitialAd.loadAd(adRequest);
    }

    public void saveUser(){
        LocalDatabase localDatabase = new LocalDatabase(this);
        User user = new User();
        localDatabase.updateUser(user);
        new saveUserWebServiceCaller().execute();
    }

    public void setFont(){
        questionTv.setTypeface(ty);
        questionValueTv.setTypeface(ty);
        option1.setTypeface(ty);
        option2.setTypeface(ty);
        option3.setTypeface(ty);
        option4.setTypeface(ty);
        timeText.setTypeface(ty);



    }

    class saveUserWebServiceCaller extends AsyncTask<String, Void, Void> {
        private static final String SOAP_ACTION = "http://tempuri.org/UpdateUser";
        private static final String METHOD_NAME = "UpdateUser";
        private static final String NAMESPACE = "http://tempuri.org/";
        private static final String URL = "http://www.recepkartal.com.tr/MilyonerService.asmx?WSDL";
        String RESULT;
        List<Question> liste = new ArrayList<Question>();

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
}
