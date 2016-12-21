package com.sleepingbear.enconversation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.util.HashMap;
import java.util.Random;

public class NoteStudyActivity extends AppCompatActivity implements View.OnClickListener {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private TextView my_tv_han;
    private TextView my_tv_foreign;
    private String currForeign;
    private String currSeq;

    private String kind;
    private String sampleSeq;
    private String sqlWhere;

    private long adviewTime = 0;

    NoteStudySearchTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_study);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        Bundle b = this.getIntent().getExtras();
        kind = b.getString("kind");
        sampleSeq = b.getString("sampleSeq");
        sqlWhere = b.getString("sqlWhere");

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setTitle("회화 학습");
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        my_tv_han = (TextView) this.findViewById(R.id.my_tv_han);
        my_tv_foreign = (TextView) this.findViewById(R.id.my_tv_foreign);

        ((ImageView) this.findViewById(R.id.my_iv_left)).setOnClickListener(this);
        ((ImageView) this.findViewById(R.id.my_iv_right)).setOnClickListener(this);

        ((ImageView) this.findViewById(R.id.my_iv_view)).setOnClickListener(this);
        ((ImageView) this.findViewById(R.id.my_iv_hide)).setOnClickListener(this);
        ((ImageView) this.findViewById(R.id.my_iv_hide)).setVisibility(View.GONE);

        //리스트 내용 변경
        changeListView();

        AdView av = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        av.loadAd(adRequest);
    }

    public void changeListView() {
        if (task != null) {
            return;
        }
        task = new NoteStudySearchTask();
        task.execute();
    }

    public void getData() {
        DicUtils.dicLog(this.getClass().toString() + " getData");
        if ( db != null ) {
            if ( "PATTERN".equals(kind) ) {
                cursor = db.rawQuery(DicQuery.getPatternSampleList(sqlWhere), null);
            } else if ( "SAMPLE".equals(kind) ) {
                cursor = db.rawQuery(DicQuery.getSample(sampleSeq), null);
            }else {
                cursor = db.rawQuery(DicQuery.getNoteList(kind), null);
            }

            if ( cursor.getCount() == 1 ) {
                ((ImageView) this.findViewById(R.id.my_iv_left)).setVisibility(View.GONE);
                ((ImageView) this.findViewById(R.id.my_iv_right)).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        DicUtils.dicLog("onClick");
        switch (v.getId()) {
            case R.id.my_iv_left:
                ((ImageView) this.findViewById(R.id.my_iv_hide)).setVisibility(View.GONE);
                ((ImageView) this.findViewById(R.id.my_iv_view)).setVisibility(View.VISIBLE);

                if ( !cursor.isFirst() ) {
                    cursor.moveToPrevious();
                    conversationShow();
                }

                break;
            case R.id.my_iv_right:
                ((ImageView) this.findViewById(R.id.my_iv_hide)).setVisibility(View.GONE);
                ((ImageView) this.findViewById(R.id.my_iv_view)).setVisibility(View.VISIBLE);

                if ( !cursor.isLast() ) {
                    cursor.moveToNext();
                    conversationShow();
                } else {
                    changeListView();
                }
                break;
            case R.id.my_iv_view:
                my_tv_foreign.setText(foreign);
                break;
            case R.id.my_iv_hide:
                conversationShow();
                break;
            default:
                String foreign = (String)my_tv_foreign.getText();

                //영문보기를 클릭하고 단어 클릭시 오류가 발생해서 체크를 해줌.
                if ( foreign.length() >= currForeign.length() ) {
                    Toast.makeText(this, "Refresh 버튼을 클릭한 후에 단어를 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ( "".equals(foreign) ) {
                    foreign = (String)v.getTag();
                } else {
                    foreign += " " + (String)v.getTag();
                }

                if ( foreign.equals( currForeign.substring( 0, foreign.length() ) ) ) {
                    my_tv_foreign.setText(foreign);
                    ((Button)v).setBackgroundColor(Color.rgb(189, 195, 195));
                }

                if ( foreign.equals( currForeign) ) {
                    LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View dialog_layout = inflater.inflate(R.layout.dialog_correct_answer, null);

                    //dialog 생성..
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setView(dialog_layout);
                    final android.app.AlertDialog alertDialog = builder.create();

                    ((TextView) dialog_layout.findViewById(R.id.my_tv_han)).setText(my_tv_han.getText());
                    ((TextView) dialog_layout.findViewById(R.id.my_tv_foreign)).setText(my_tv_foreign.getText());

                    // 광고 추가
                    PublisherAdView mPublisherAdView = new PublisherAdView(this);
                    mPublisherAdView.setAdSizes(new AdSize(300, 250));
                    mPublisherAdView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id2));

                    // Create an ad request.
                    PublisherAdRequest.Builder publisherAdRequestBuilder = new PublisherAdRequest.Builder();
                    ((RelativeLayout) dialog_layout.findViewById(R.id.my_rl_admob)).addView(mPublisherAdView);

                    // Start loading the ad.
                    mPublisherAdView.loadAd(publisherAdRequestBuilder.build());
                    adviewTime = System.currentTimeMillis();

                    ((Button) dialog_layout.findViewById(R.id.my_b_next)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (System.currentTimeMillis() > adviewTime + 2000) {
                                if (!cursor.isLast()) {
                                    cursor.moveToNext();
                                    conversationShow();
                                } else {
                                    changeListView();
                                }

                                alertDialog.dismiss();
                            }
                        }
                    });
                    ((Button) dialog_layout.findViewById(R.id.my_b_close)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    ((Button) dialog_layout.findViewById(R.id.my_b_detail)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("foreign", (String)my_tv_foreign.getText());
                            bundle.putString("han", (String)my_tv_han.getText());
                            bundle.putString("sampleSeq", currSeq);

                            Intent intent = new Intent(getApplication(), SentenceViewActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);

                            alertDialog.dismiss();
                        }
                    });

                    //개별 조회면은 다음 버튼을 안보이게 해준다.
                    if ( cursor.getCount() == 1 ) {
                        ((Button) dialog_layout.findViewById(R.id.my_b_next)).setVisibility(View.GONE);
                    }

                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                    FlowLayout wordArea = (FlowLayout) this.findViewById(R.id.my_ll_conversation_word);
                    wordArea.removeAllViews();
                }

                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_help) {
            Bundle bundle = new Bundle();
            bundle.putString("SCREEN", "NOTE_STUDY");

            Intent intent = new Intent(getApplication(), HelpActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private String foreign = "";
    private String[] foreignArr;
    public void conversationShow() {
        if ( cursor.getCount() > 0 ) {
            currSeq = cursor.getString(cursor.getColumnIndexOrThrow("SEQ"));
            currForeign = cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE1"));
            my_tv_han.setText(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE2")));
            my_tv_foreign.setText("");

            foreign = cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE1"));

            FlowLayout wordArea = (FlowLayout) this.findViewById(R.id.my_ll_conversation_word);
            wordArea.removeAllViews();

            foreignArr = getRandForeign(foreign.split(" "));
            for ( int i = 0; i < foreignArr.length; i++ ) {
                Button btn = new Button(this);
                btn.setBackgroundColor(Color.rgb(249, 151, 53));
                btn.setTextColor(Color.rgb(255, 255, 255));
                btn.setText(" " + foreignArr[i] + " ");
                btn.setAllCaps(false);
                btn.setTextSize(12);

                btn.setLayoutParams((new FlowLayout.LayoutParams(3, 3)));

                btn.setId(i);
                btn.setTag(foreignArr[i]);
                btn.setGravity(Gravity.LEFT);
                btn.setOnClickListener(this);
                System.out.println(foreignArr[i]);
                wordArea.addView(btn);
            }
        } else {
            my_tv_han.setText("");
            my_tv_foreign.setText("");
            currForeign = "";
        }
    }

    public String[] getRandForeign(String[] arr) {
        String[] rtnArr = new String[arr.length];

        Random random = new Random();
        HashMap hm = new HashMap();
        int cnt = 0;
        while ( true ) {
            int randomIdx = random.nextInt(arr.length);
            if ( !hm.containsKey(randomIdx + "") ) {
                hm.put(randomIdx + "", randomIdx + "");
                rtnArr[cnt++] = arr[randomIdx];
            }

            if ( cnt == arr.length ) {
                break;
            }
        }

        String str1 = "";
        String str2 = "";
        for ( int i = 0; i < arr.length; i++ ) {
            str1 += arr[i] + " ";
            str2 += rtnArr[i] + " ";
        }
        DicUtils.dicLog(str1 + " : " + str2);

        return rtnArr;
    }

    private class NoteStudySearchTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(NoteStudyActivity.this);
            pd.show();
            pd.setContentView(R.layout.custom_progress);

            pd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pd.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            getData();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            cursor.moveToFirst();
            conversationShow();

            pd.dismiss();
            task = null;

            super.onPostExecute(result);
        }
    }
}
