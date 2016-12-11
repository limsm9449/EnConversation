package com.sleepingbear.enconversation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;
import java.util.Random;

import static com.sleepingbear.enconversation.R.style.myButton;


public class ConversationStudyFragment extends Fragment implements View.OnClickListener {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private View mainView;
    private Cursor cursor;
    private TextView my_tv_han;
    private TextView my_tv_foreign;
    private String currForeign;
    private String currSeq;
    private int difficult = 1;
    private boolean isStart = false;

    ConversationStudySearchTask task;

    public ConversationStudyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_conversation_study, container, false);

        dbHelper = new DbHelper(getContext());
        db = dbHelper.getWritableDatabase();

        my_tv_han = (TextView) mainView.findViewById(R.id.my_tv_han);
        my_tv_foreign = (TextView) mainView.findViewById(R.id.my_tv_foreign);

        ((ImageView) mainView.findViewById(R.id.my_iv_left)).setOnClickListener(this);
        ((ImageView) mainView.findViewById(R.id.my_iv_right)).setOnClickListener(this);

        ((ImageView) mainView.findViewById(R.id.my_iv_foreign_view)).setOnClickListener(this);
        ((ImageView) mainView.findViewById(R.id.my_iv_refresh)).setOnClickListener(this);

        ((RadioButton) mainView.findViewById(R.id.my_rb_easy)).setOnClickListener(this);
        ((RadioButton) mainView.findViewById(R.id.my_rb_normal)).setOnClickListener(this);
        ((RadioButton) mainView.findViewById(R.id.my_rb_hard)).setOnClickListener(this);

        //리스트 내용 변경
        changeListView(true);

        AdView av = (AdView)mainView.findViewById(R.id.adView);
        AdRequest adRequest = new  AdRequest.Builder().build();
        av.loadAd(adRequest);


        //소프트 키보드 없애기
        //InputMethodManager imm= (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        return mainView;
    }

    public void changeListView(boolean isKeyin) {
        if ( isKeyin ) {
            if (task != null) {
                return;
            }
            task = new ConversationStudyFragment.ConversationStudySearchTask();
            task.execute();
        }
    }

    public void getData() {
        DicUtils.dicLog(this.getClass().toString() + " getData");
        if ( db != null ) {
            cursor = db.rawQuery(DicQuery.getConversationStudy(difficult), null);

            if ( cursor.getCount() == 0 ) {
            }
        }
    }

    @Override
    public void onClick(View v) {
        DicUtils.dicLog("onClick");
        switch (v.getId()) {
            case R.id.my_iv_left:
                if ( !cursor.isFirst() ) {
                    cursor.moveToPrevious();
                    conversationShow();
                }

                isStart = false;

                break;
            case R.id.my_iv_right:
                if ( isStart ) {
                    DicDb.insConversationStudy(db, currSeq, DicUtils.getDelimiterDate(DicUtils.getCurrentDate(),"."));
                    DicUtils. writeInfoToFile(getContext(), CommConstants.f_tag_c_study_ins + ":" + DicUtils.getDelimiterDate(DicUtils.getCurrentDate(),".") + ":" + currSeq);
                }
                if ( !cursor.isLast() ) {
                    cursor.moveToNext();
                    conversationShow();
                } else {
                    changeListView(true);
                }
                break;
            case R.id.my_iv_foreign_view:
                my_tv_foreign.setText(foreign);
                break;
            case R.id.my_iv_refresh:
                conversationShow();
                break;
            case R.id.my_rb_easy:
                difficult = 1;
                changeListView(true);
                break;
            case R.id.my_rb_normal:
                difficult = 2;
                changeListView(true);
                break;
            case R.id.my_rb_hard:
                difficult = 3;
                changeListView(true);
                break;
            default:
                DicUtils.dicLog("click word : " + (String)v.getTag());
                String foreign = (String)my_tv_foreign.getText();

                DicUtils.dicLog(foreign.length() + " : " + currForeign.length() );
                //영문보기를 클릭하고 단어 클릭시 오류가 발생해서 체크를 해줌.
                if ( foreign.length() >= currForeign.length() ) {
                    Toast.makeText(getContext(), "Refresh 버튼을 클릭한 후에 단어를 선택해 주세요.", Toast.LENGTH_SHORT).show();
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
                } else {
                    //Toast.makeText(getContext(), "틀린 단어를 선택하셨습니다.\n다른 단어를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }

                if ( foreign.equals( currForeign) ) {
                    Toast.makeText(getContext(), "맞는 문장입니다.\n다음 회화 문제를 풀어보세요.", Toast.LENGTH_SHORT).show();

                    FlowLayout wordArea = (FlowLayout) mainView.findViewById(R.id.my_ll_conversation_word);
                    wordArea.removeAllViews();

                    isStart = true;
                }

                break;
        }
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

            FlowLayout wordArea = (FlowLayout) mainView.findViewById(R.id.my_ll_conversation_word);
            wordArea.removeAllViews();

            foreignArr = getRandForeign(foreign.split(" "));
            for ( int i = 0; i < foreignArr.length; i++ ) {
                Button btn = new Button(getContext());
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

            isStart = false;
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

    private class ConversationStudySearchTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getContext());
            pd.setIndeterminate(true);
            pd.setCancelable(false);
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
