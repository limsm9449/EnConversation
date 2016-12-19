package com.sleepingbear.enconversation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;


public class ConversationFragment extends Fragment implements View.OnClickListener {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private View mainView;
    private EditText et_search;
    private CheckBox cb_foreignView;
    private ConversationCursorAdapter adapter;

    private Cursor dictionaryCursor;
    private int mSelect = 0;

    DicSearchTask task;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_conversation, container, false);


        dbHelper = new DbHelper(getContext());
        db = dbHelper.getWritableDatabase();

        et_search = (EditText) mainView.findViewById(R.id.my_f_conv_et_search);
        et_search.addTextChangedListener(textWatcherInput);
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ( keyCode == KeyEvent.KEYCODE_ENTER ) {
                    changeListView(true);
                }

                return false;
            }
        });

        ImageView iv_clear = (ImageView)mainView.findViewById(R.id.my_f_conv_iv_clear);
        iv_clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                et_search.setText("");

                changeListView(true);
            }
        });

        ((ImageButton) mainView.findViewById(R.id.my_f_conv_ib_search)).setOnClickListener(this);
        ((ImageView) mainView.findViewById(R.id.my_iv_view)).setOnClickListener(this);
        ((ImageView) mainView.findViewById(R.id.my_iv_hide)).setOnClickListener(this);


        ((ImageView) mainView.findViewById(R.id.my_iv_hide)).setVisibility(View.GONE);

        changeListView(false);

        AdView av = (AdView)mainView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        av.loadAd(adRequest);

        return mainView;
    }

    public void changeListView(boolean isKeyin) {
        if ( isKeyin ) {
            ((RelativeLayout)mainView.findViewById(R.id.my_f_conv_rl_msg)).setVisibility(View.GONE);

            if (task != null) {
                return;
            }
            task = new DicSearchTask();
            task.execute();
        }
    }

    public void getData() {
        DicUtils.dicLog(this.getClass().toString() + " changeListView");

        StringBuffer sql = new StringBuffer();

        sql.append("SELECT SEQ _id, SEQ, SENTENCE1, SENTENCE2" + CommConstants.sqlCR);
        sql.append("  FROM DIC_SAMPLE" + CommConstants.sqlCR);
        if ( !"".equals(et_search.getText().toString()) ) {
            String[] search = et_search.getText().toString().split(" ");
            String condi1 = "";
            String condi2 = "";
            for ( int i = 0; i < search.length; i++ ) {
                condi1 += ("".equals(condi1) ? "" : " AND ") + " SENTENCE1 LIKE '%" + search[i] + "%'";
                condi2 += ("".equals(condi2) ? "" : " AND ") + " SENTENCE2 LIKE '%" + search[i] + "%'";
            }
            sql.append(" WHERE ( " + condi1 + " ) OR" + CommConstants.sqlCR);
            sql.append("         ( " + condi2 + " )" + CommConstants.sqlCR);
        }
        sql.append(" ORDER BY ORD" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());

        dictionaryCursor = db.rawQuery(sql.toString(), null);

        //결과가 나올때까지 기달리게 할려고 다음 로직을 추가한다. 안하면 progressbar가 사라짐.. cursor도  Thread 방식으로 돌아가나봄
        if ( dictionaryCursor.getCount() == 0 ) {
        }
     }

    public void setListView() {
        if ( dictionaryCursor.getCount() == 0 ) {
            Toast.makeText(getContext(), "검색된 데이타가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        ListView dictionaryListView = (ListView) mainView.findViewById(R.id.my_f_conv_lv);
        adapter = new ConversationCursorAdapter(getContext(), dictionaryCursor, 0);
        dictionaryListView.setAdapter(adapter);

        dictionaryListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        dictionaryListView.setOnItemClickListener(itemClickListener);
        dictionaryListView.setOnItemLongClickListener(itemLongClickListener);
        dictionaryListView.setSelection(0);

        //소프트 키보드 없애기
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur = (Cursor) adapter.getItem(position);
            adapter.setStatus( cur.getString(cur.getColumnIndexOrThrow("SEQ")) );
            adapter.notifyDataSetChanged();
        }
    };

    AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur = (Cursor) adapter.getItem(position);
            final String sampleSeq = cur.getString(cur.getColumnIndexOrThrow("SEQ"));
            final String foreign = cur.getString(cur.getColumnIndexOrThrow("SENTENCE1"));
            final String han = cur.getString(cur.getColumnIndexOrThrow("SENTENCE2"));

            //메뉴 선택 다이얼로그 생성
            Cursor cursor = db.rawQuery(DicQuery.getNoteKindContextMenu(true), null);
            final String[] kindCodes = new String[cursor.getCount()];
            final String[] kindCodeNames = new String[cursor.getCount()];

            int idx = 0;
            while (cursor.moveToNext()) {
                kindCodes[idx] = cursor.getString(cursor.getColumnIndexOrThrow("KIND"));
                kindCodeNames[idx] = cursor.getString(cursor.getColumnIndexOrThrow("KIND_NAME"));
                idx++;
            }
            cursor.close();

            final android.support.v7.app.AlertDialog.Builder dlg = new android.support.v7.app.AlertDialog.Builder(getActivity());
            dlg.setTitle("메뉴 선택");
            dlg.setSingleChoiceItems(kindCodeNames, mSelect, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    mSelect = arg1;
                }
            });
            dlg.setNegativeButton("취소", null);
            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if ( mSelect == 0 ) {
                        Bundle bundle = new Bundle();
                        bundle.putString("code", "");
                        bundle.putString("sampleSeq", sampleSeq);

                        Intent intent = new Intent(getActivity().getApplication(), NoteStudyActivity.class);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    } else if ( mSelect == 1 ) {
                        Bundle bundle = new Bundle();
                        bundle.putString("foreign", foreign);
                        bundle.putString("han", han);
                        bundle.putString("sampleSeq", sampleSeq);

                        Intent intent = new Intent(getActivity().getApplication(), SentenceViewActivity.class);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    } else {
                        DicDb.insConversationToNote(db, kindCodes[mSelect], sampleSeq);
                        DicUtils.writeInfoToFile(getActivity().getApplicationContext(), db, "C01");
                    }
                }
            });
            dlg.show();

            return false;
        }
    };

    /**
     * 검색 단어가 변경되었으면 다시 검색을 한다.
     */
    TextWatcher textWatcherInput = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
               //changeListView();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void onClick(View v) {
        if ( v.getId() == R.id.my_f_conv_ib_search) {
            changeListView(true);
        } else if ( v.getId() == R.id.my_iv_view ) {
            adapter.setForeignView(true);
            adapter.notifyDataSetChanged();

            ((ImageView) mainView.findViewById(R.id.my_iv_view)).setVisibility(View.GONE);
            ((ImageView) mainView.findViewById(R.id.my_iv_hide)).setVisibility(View.VISIBLE);
        } else if ( v.getId() == R.id.my_iv_hide ) {
            adapter.setForeignView(false);
            adapter.notifyDataSetChanged();

            ((ImageView) mainView.findViewById(R.id.my_iv_view)).setVisibility(View.VISIBLE);
            ((ImageView) mainView.findViewById(R.id.my_iv_hide)).setVisibility(View.GONE);
        }
    }

    private class DicSearchTask extends AsyncTask<Void, Void, Void> {

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
            setListView();

            pd.dismiss();
            task = null;

            super.onPostExecute(result);
        }
    }
}

class ConversationCursorAdapter extends CursorAdapter {
    public boolean isForeignView = false;
    public HashMap statusData = new HashMap();

    public ConversationCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_conversation_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.my_tv_han)).setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE2"))));
        if ( isForeignView || statusData.containsKey(cursor.getString(cursor.getColumnIndexOrThrow("SEQ")))  ) {
            ((TextView) view.findViewById(R.id.my_tv_foreign)).setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE1"))));
        } else {
            ((TextView) view.findViewById(R.id.my_tv_foreign)).setText("Click..");
        }
    }

    public void setForeignView(boolean foreignView) {
        isForeignView = foreignView;
        statusData.clear();
    }

    public void setStatus(String sampleSeq) {
        statusData.put(sampleSeq, "Y");
    }
}
