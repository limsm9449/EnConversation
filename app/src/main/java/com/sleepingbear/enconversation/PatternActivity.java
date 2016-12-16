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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;

public class PatternActivity extends AppCompatActivity {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private PatternCursorAdapter adapter;
    public String sqlWhere;
    public int mSelect = 0;
    private boolean isForeignView = false;

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
        Cursor cursor = db.rawQuery(DicQuery.getPatternSampleList(sqlWhere), null);

        ListView listView = (ListView) this.findViewById(R.id.my_c_pattern_lv);
        adapter = new PatternCursorAdapter(getApplicationContext(), cursor, this);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cur = (Cursor) adapter.getItem(i);
                adapter.setStatus( cur.getString(cur.getColumnIndexOrThrow("SEQ")) );
                adapter.notifyDataSetChanged();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                        if ( mSelect == 0 ) {
                            Bundle bundle = new Bundle();
                            bundle.putString("code", "");
                            bundle.putString("sampleSeq", sampleSeq);

                            Intent intent = new Intent(getApplication(), ConversationStudyActivity.class);
                            intent.putExtras(bundle);

                            startActivity(intent);
                        } else if ( mSelect == 1 ) {
                            Bundle bundle = new Bundle();
                            bundle.putString("foreign", foreign);
                            bundle.putString("han", han);
                            bundle.putString("sampleSeq", sampleSeq);

                            Intent intent = new Intent(getApplication(), SentenceViewActivity.class);
                            intent.putExtras(bundle);

                            startActivity(intent);
                        } else {
                            DicDb.insConversationToNote(db, kindCodes[mSelect], sampleSeq);
                            DicUtils.writeInfoToFile(getApplicationContext(), CommConstants.tag_note_ins + ":" + kindCodes[mSelect] + ":" + sampleSeq);
                        }
                    }
                });
                dlg.show();

                return true;
            };
        });
        listView.setSelection(0);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if ( isForeignView ) {
            ((MenuItem) menu.findItem(R.id.action_view)).setVisible(false);
            ((MenuItem) menu.findItem(R.id.action_hide)).setVisible(true);
        } else {
            ((MenuItem) menu.findItem(R.id.action_view)).setVisible(true);
            ((MenuItem) menu.findItem(R.id.action_hide)).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_view) {
            isForeignView = true;
            invalidateOptionsMenu();

            adapter.setForeignView(isForeignView);
            adapter.notifyDataSetChanged();
        } else if (id == R.id.action_hide) {
            isForeignView = false;
            invalidateOptionsMenu();

            adapter.setForeignView(isForeignView);
            adapter.notifyDataSetChanged();
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
    public HashMap statusData = new HashMap();
    public boolean isForeignView = false;

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
        ((TextView) view.findViewById(R.id.my_tv_han)).setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE2"))));
        if ( isForeignView || statusData.containsKey(cursor.getString(cursor.getColumnIndexOrThrow("SEQ")))  ) {
            ((TextView) view.findViewById(R.id.my_tv_foreign)).setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE1"))));
        } else {
            ((TextView) view.findViewById(R.id.my_tv_foreign)).setText("Click..");
        }
    }

    public void setForeignView(boolean foreignView) {
        isForeignView = foreignView;
    }

    public void setStatus(String sampleSeq) {
        statusData.put(sampleSeq, "Y");
    }
}