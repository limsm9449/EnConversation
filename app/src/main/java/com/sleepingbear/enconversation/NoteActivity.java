package com.sleepingbear.enconversation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import static java.security.AccessController.getContext;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private NoteCursorAdapter adapter;
    private String kind;

    private boolean isAllCheck = false;
    private int mSelect;
    private boolean isEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        Bundle b = this.getIntent().getExtras();
        kind = b.getString("kind");

        ((ImageView)this.findViewById(R.id.my_iv_all)).setOnClickListener(this);
        ((ImageView)this.findViewById(R.id.my_iv_delete)).setOnClickListener(this);
        ((ImageView)this.findViewById(R.id.my_iv_copy)).setOnClickListener(this);
        ((ImageView)this.findViewById(R.id.my_iv_move)).setOnClickListener(this);
        ((ImageView)this.findViewById(R.id.my_iv_edit)).setOnClickListener(this);
        ((ImageView)this.findViewById(R.id.my_iv_exit)).setOnClickListener(this);

        if ( "C01".equals(kind) ) {
            ((RelativeLayout)this.findViewById(R.id.my_c_rl_top)).setVisibility(View.VISIBLE);

            ((ImageView)this.findViewById(R.id.my_iv_all)).setVisibility(View.GONE);
            ((ImageView)this.findViewById(R.id.my_iv_delete)).setVisibility(View.GONE);
            ((ImageView)this.findViewById(R.id.my_iv_copy)).setVisibility(View.GONE);
            ((ImageView)this.findViewById(R.id.my_iv_move)).setVisibility(View.GONE);
            ((ImageView)this.findViewById(R.id.my_iv_exit)).setVisibility(View.GONE);
        } else {
            ((RelativeLayout)this.findViewById(R.id.my_c_rl_top)).setVisibility(View.GONE);
        }
        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setTitle(b.getString("kindName"));
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        changeListView();

        AdView av = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        av.loadAd(adRequest);
    }
    public void changeListView() {
        Cursor cursor = db.rawQuery(DicQuery.getConversation(kind), null);

        ListView listView = (ListView) this.findViewById(R.id.my_c_note_lv);
        adapter = new NoteCursorAdapter(getApplicationContext(), cursor, db, 0);
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

                final android.support.v7.app.AlertDialog.Builder dlg = new android.support.v7.app.AlertDialog.Builder(NoteActivity.this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_iv_edit:
                ((ImageView) this.findViewById(R.id.my_iv_all)).setVisibility(View.VISIBLE);
                ((ImageView) this.findViewById(R.id.my_iv_delete)).setVisibility(View.VISIBLE);
                ((ImageView) this.findViewById(R.id.my_iv_copy)).setVisibility(View.VISIBLE);
                ((ImageView) this.findViewById(R.id.my_iv_move)).setVisibility(View.VISIBLE);
                ((ImageView) this.findViewById(R.id.my_iv_exit)).setVisibility(View.VISIBLE);

                ((ImageView) this.findViewById(R.id.my_iv_edit)).setVisibility(View.GONE);

                break;
            case R.id.my_iv_exit:
                ((ImageView) this.findViewById(R.id.my_iv_all)).setVisibility(View.GONE);
                ((ImageView) this.findViewById(R.id.my_iv_delete)).setVisibility(View.GONE);
                ((ImageView) this.findViewById(R.id.my_iv_copy)).setVisibility(View.GONE);
                ((ImageView) this.findViewById(R.id.my_iv_move)).setVisibility(View.GONE);
                ((ImageView) this.findViewById(R.id.my_iv_exit)).setVisibility(View.GONE);

                ((ImageView) this.findViewById(R.id.my_iv_edit)).setVisibility(View.VISIBLE);

                break;
            case R.id.my_iv_all :
                if ( isAllCheck ) {
                    isAllCheck = false;
                } else {
                    isAllCheck = true;
                }
                adapter.allCheck(isAllCheck);
                break;
            case R.id.my_iv_delete :
                if ( !adapter.isCheck() ) {
                    Toast.makeText(this, "선택된 데이타가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    new android.app.AlertDialog.Builder(this)
                            .setTitle("알림")
                            .setMessage("삭제하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.delete();
                                    changeListView();

                                    DicUtils.writeNewInfoToFile(getApplicationContext(), db);
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }

                break;
            case R.id.my_iv_copy :
                if ( !adapter.isCheck() ) {
                    Toast.makeText(this, "선택된 데이타가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {

                    //메뉴 선택 다이얼로그 생성
                    Cursor cursor = db.rawQuery(DicQuery.getMyConversationKindContextMenu(kind), null);
                    final String[] kindCodes = new String[cursor.getCount()];
                    final String[] kindCodeNames = new String[cursor.getCount()];

                    int idx = 0;
                    while (cursor.moveToNext()) {
                        kindCodes[idx] = cursor.getString(cursor.getColumnIndexOrThrow("KIND"));
                        kindCodeNames[idx] = cursor.getString(cursor.getColumnIndexOrThrow("KIND_NAME"));
                        idx++;
                    }
                    cursor.close();

                    final android.support.v7.app.AlertDialog.Builder dlg = new android.support.v7.app.AlertDialog.Builder(NoteActivity.this);
                    dlg.setTitle("회화 노트 선택");
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

                        }
                    });
                    dlg.show();
                }

                break;
            case R.id.my_iv_move :
                if ( !adapter.isCheck() ) {
                    Toast.makeText(this, "선택된 데이타가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    //메뉴 선택 다이얼로그 생성
                    Cursor cursor = db.rawQuery(DicQuery.getSentenceViewContextMenu(), null);
                    final String[] kindCodes = new String[cursor.getCount()];
                    final String[] kindCodeNames = new String[cursor.getCount()];

                    int idx = 0;
                    while (cursor.moveToNext()) {
                        kindCodes[idx] = cursor.getString(cursor.getColumnIndexOrThrow("KIND"));
                        kindCodeNames[idx] = cursor.getString(cursor.getColumnIndexOrThrow("KIND_NAME"));
                        idx++;
                    }
                    cursor.close();

                    /*
                    final AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
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
                            adapter.save(kindCodes[mSelect]);
                            changeListView();

                            DicUtils.writeNewInfoToFile(this, db);

                            Toast.makeText(this, "단어장에 추가하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dlg.show();
                    */
                }
                break;
        }
    }

}

class NoteCursorAdapter extends CursorAdapter {
    private SQLiteDatabase mDb;
    public boolean[] isCheck;
    public int[] seq;
    private boolean isEditing = false;

    public NoteCursorAdapter(Context context, Cursor cursor, SQLiteDatabase db, int flags) {
        super(context, cursor, 0);
        mDb = db;

        isCheck = new boolean[cursor.getCount()];
        seq = new int[cursor.getCount()];
        while ( cursor.moveToNext() ) {
            isCheck[cursor.getPosition()] = false;
            seq[cursor.getPosition()] = cursor.getInt(cursor.getColumnIndexOrThrow("SEQ"));
        }
        cursor.moveToFirst();
    }

    static class ViewHolder {
        protected int position;
        protected CheckBox cb;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_note_item, parent, false);


        ViewHolder viewHolder = new ViewHolder();
        viewHolder.cb = (CheckBox) view.findViewById(R.id.my_cb_check);
        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                ViewHolder viewHolder = (ViewHolder)buttonView.getTag();
                isCheck[viewHolder.position] = isChecked;
                notifyDataSetChanged();

                DicUtils.dicLog("onCheckedChanged : " + viewHolder.position);
            }
        });

        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.position = cursor.getPosition();
        viewHolder.cb.setTag(viewHolder);


        ((TextView) view.findViewById(R.id.my_tv_foreign)).setText(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE1")));
        ((TextView) view.findViewById(R.id.my_tv_han)).setText(cursor.getString(cursor.getColumnIndexOrThrow("SENTENCE2")));

        if ( isCheck[cursor.getPosition()] ) {
            ((CheckBox)view.findViewById(R.id.my_cb_check)).setButtonDrawable(android.R.drawable.checkbox_on_background);
        } else {
            ((CheckBox)view.findViewById(R.id.my_cb_check)).setButtonDrawable(android.R.drawable.checkbox_off_background);
        }

        if ( isEditing ) {
            ((RelativeLayout) view.findViewById(R.id.my_rl_left)).setVisibility(View.VISIBLE);
        } else {
            ((RelativeLayout) view.findViewById(R.id.my_rl_left)).setVisibility(View.GONE);
        }
    }

    public void allCheck(boolean chk) {
        for ( int i = 0; i < isCheck.length; i++ ) {
            isCheck[i] = chk;
        }

        notifyDataSetChanged();
    }

    public void delete() {
        for ( int i = 0; i < isCheck.length; i++ ) {
            if ( isCheck[i] ) {
                DicDb.delConversationNote(mDb, seq[i]);
            }
        }
    }

    public void save(String kind) {
        for ( int i = 0; i < isCheck.length; i++ ) {
            if ( isCheck[i] ) {
                //DicDb.insDicVoc(mDb, entryId[i], kind);
                DicDb.delDicClickWord(mDb, seq[i]);
            }
        }
    }

    public boolean isCheck() {
        boolean rtn = false;
        for ( int i = 0; i < isCheck.length; i++ ) {
            if ( isCheck[i] ) {
                rtn = true;
                break;
            }
        }

        return rtn;
    }

    public void editChange(boolean isEditing) {
        this.isEditing = isEditing;
        notifyDataSetChanged();
    }

}