package com.sleepingbear.enconversation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class PatternActivity extends AppCompatActivity {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private PatternCursorAdapter adapter;
    public String sqlWhere;
    public int mSelect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        Bundle b = this.getIntent().getExtras();
        sqlWhere = b.getString("SQL_WHERE");

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setTitle(b.getString("PATTERN"));
        ((TextView)this.findViewById(R.id.my_tv_pattern_desc)).setText("뜻 : " + b.getString("DESC"));
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        getListView();

        AdView av = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        av.loadAd(adRequest);
    }

    public void getListView() {
        Cursor cursor = db.rawQuery(DicQuery.getPatternSample(sqlWhere), null);

        ListView listView = (ListView) this.findViewById(R.id.my_c_pattern_lv);
        adapter = new PatternCursorAdapter(getApplicationContext(), cursor, this);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cur = (Cursor) adapter.getItem(i);

                Bundle bundle = new Bundle();
                bundle.putString("foreign", cur.getString(cur.getColumnIndexOrThrow("SENTENCE1")));
                bundle.putString("han", cur.getString(cur.getColumnIndexOrThrow("SENTENCE2")));
                bundle.putString("seq", cur.getString(cur.getColumnIndexOrThrow("SEQ")));

                Intent intent = new Intent(getApplication(), SentenceViewActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cur = (Cursor) adapter.getItem(position);

                final String sampleSeq = cur.getString(cur.getColumnIndexOrThrow("SEQ"));

                //메뉴 선택 다이얼로그 생성
                Cursor cursor = db.rawQuery(DicQuery.getMyConversationKindContextMenu(), null);
                final String[] kindCodes = new String[cursor.getCount()];
                final String[] kindCodeNames = new String[cursor.getCount()];

                int idx = 0;
                while (cursor.moveToNext()) {
                    kindCodes[idx] = cursor.getString(cursor.getColumnIndexOrThrow("KIND"));
                    kindCodeNames[idx] = cursor.getString(cursor.getColumnIndexOrThrow("KIND_NAME"));
                    idx++;
                }
                cursor.close();

                final android.support.v7.app.AlertDialog.Builder dlg = new android.support.v7.app.AlertDialog.Builder(PatternActivity.this);
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
                        DicDb.insConversationNote(db, kindCodes[mSelect], sampleSeq, DicUtils.getDelimiterDate(DicUtils.getCurrentDate(), "."));
                        DicUtils.writeInfoToFile(getApplicationContext(), CommConstants.f_tag_c_note_ins + ":" + kindCodes[mSelect] + ":" + DicUtils.getDelimiterDate(DicUtils.getCurrentDate(), ".") + ":" + sampleSeq);
                    }
                });
                dlg.show();

                return true;
            };
        });
        listView.setSelection(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_help) {
            Bundle bundle = new Bundle();
            bundle.putString("SCREEN", "DIC_CATEGORY");

            Intent intent = new Intent(getApplication(), HelpActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}

class PatternCursorAdapter extends CursorAdapter {
    public PatternCursorAdapter(Context context, Cursor cursor, Activity activity) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_pattern_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.my_tv_foreign)).setText(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE1")));
        ((TextView) view.findViewById(R.id.my_tv_han)).setText(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE2")));
    }
}