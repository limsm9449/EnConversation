package com.sleepingbear.enconversation;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;
import java.util.Random;


public class ConversationStudyFragment extends Fragment implements View.OnClickListener {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private View mainView;
    private Cursor cursor;
    private TextView my_tv_han;
    private TextView my_tv_foreign;

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

        //리스트 내용 변경
        changeListView(true);

        AdView av = (AdView)mainView.findViewById(R.id.adView);
        AdRequest adRequest = new  AdRequest.Builder().build();
        av.loadAd(adRequest);

        return mainView;
    }

    public void changeListView(boolean isKeyin) {
        DicUtils.dicLog(this.getClass().toString() + " changeListView");
        if ( db != null && isKeyin) {
            StringBuffer sql = new StringBuffer();

            sql.append("SELECT SEQ _id, SEQ, SENTENCE1, SENTENCE2" + CommConstants.sqlCR);
            sql.append("  FROM DIC_SAMPLE" + CommConstants.sqlCR);
            sql.append(" ORDER BY ORD" + CommConstants.sqlCR);
            sql.append(" LIMIT 1000" + CommConstants.sqlCR);
            DicUtils.dicSqlLog(sql.toString());

            cursor = db.rawQuery(sql.toString(), null);

            if ( cursor.getCount() == 0 ) {
            } else {
                cursor.moveToFirst();
                conversationShow();
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
                break;
            case R.id.my_iv_right:
                if ( !cursor.isLast() ) {
                    cursor.moveToNext();
                    conversationShow();
                } else {
                    changeListView(true);
                }
                break;
            default:
                if ( "".equals(my_tv_foreign.getText()) ) {
                    my_tv_foreign.setText((String)v.getTag());
                } else {
                    my_tv_foreign.setText(my_tv_foreign.getText() + (String)v.getTag());
                }

                break;
        }
    }

    private String foreign = "";
    private String[] foreignArr;
    public void conversationShow() {
        if ( cursor.getCount() > 0 ) {
            my_tv_han.setText(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE2")) + " : " + cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE1")));
            my_tv_foreign.setText("");

            foreign = cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE1"));

            FlowLayout wordArea = (FlowLayout) mainView.findViewById(R.id.my_ll_conversation_word);
            wordArea.removeAllViews();

            foreignArr = getRandForeign(foreign.split(" "));
            for ( int i = 0; i < foreignArr.length; i++ ) {
                Button btn = new Button(getContext());
                btn.setText(foreignArr[i]);
                //btn.setWidth(66);
                btn.setTextSize(11);
                btn.setId(i);
                btn.setTag(foreignArr[i]);
                btn.setOnClickListener(this);

                wordArea.addView(btn);
            }
        } else {
            my_tv_han.setText("");
            my_tv_foreign.setText("");
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
}
