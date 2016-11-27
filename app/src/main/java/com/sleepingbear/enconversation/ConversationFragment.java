package com.sleepingbear.enconversation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class ConversationFragment extends Fragment implements View.OnClickListener {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private View mainView;
    private EditText et_search;
    private ConversationCursorAdapter adapter;

    private Activity mActivity;
    private Cursor dictionaryCursor;

    DicSearchTask task;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_conversation, container, false);

        mActivity = this.getActivity();

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

        changeListView(false);

        AdView av = (AdView)mainView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        av.loadAd(adRequest);

        et_search.requestFocus();

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

        if ( !"".equals(et_search.getText().toString()) ) {
            if ( DicUtils.isHangule(et_search.getText().toString()) ) {
                sql.append("SELECT SEQ _id, SENTENCE1, SENTENCE2, 'H' KIND" + CommConstants.sqlCR);
                sql.append("  FROM DIC_SAMPLE" + CommConstants.sqlCR);
                sql.append(" WHERE SENTENCE2 LIKE '%" + et_search.getText().toString() + "%'" + CommConstants.sqlCR);
                sql.append(" ORDER BY SENTENCE2" + CommConstants.sqlCR);
            } else {
                sql.append("SELECT SEQ _id, SENTENCE1, SENTENCE2, 'F' KIND" + CommConstants.sqlCR);
                sql.append("  FROM DIC_SAMPLE" + CommConstants.sqlCR);
                sql.append(" WHERE SENTENCE1 LIKE '%" + et_search.getText().toString() + "%'" + CommConstants.sqlCR);
                sql.append(" ORDER BY ORD" + CommConstants.sqlCR);
            }
        } else {
            sql.append("SELECT SEQ _id, SENTENCE1, SENTENCE2, 'F' KIND" + CommConstants.sqlCR);
            sql.append("  FROM DIC_SAMPLE" + CommConstants.sqlCR);
            sql.append(" ORDER BY ORD" + CommConstants.sqlCR);
        }
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
        dictionaryListView.setSelection(0);

        //소프트 키보드 없애기
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
    }

    /**
     * 단어가 선택되면은 단어 상세창을 열어준다.
     */
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur = (Cursor) adapter.getItem(position);
            cur.moveToPosition(position);

            Bundle bundle = new Bundle();
            bundle.putString("foreign", cur.getString(cur.getColumnIndexOrThrow("SENTENCE1")));
            bundle.putString("han", cur.getString(cur.getColumnIndexOrThrow("SENTENCE2")));

            Intent intent = new Intent(getActivity().getApplication(), SentenceViewActivity.class);
            intent.putExtras(bundle);

            startActivity(intent);
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

    public ConversationCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_conversation_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if ( "F".equals(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("KIND")))) ) {
            ((TextView) view.findViewById(R.id.my_f_dict_tv_foreign)).setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE1"))));
            ((TextView) view.findViewById(R.id.my_f_dict_tv_mean)).setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE2"))));
        } else {
            ((TextView) view.findViewById(R.id.my_f_dict_tv_foreign)).setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE2"))));
            ((TextView) view.findViewById(R.id.my_f_dict_tv_mean)).setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE1"))));
        }
        ((TextView) view.findViewById(R.id.my_f_dict_tv_spelling)).setText("");
    }
}
