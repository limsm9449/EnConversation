package com.sleepingbear.enconversation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
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

public class BookmarkFragment extends Fragment implements View.OnClickListener {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private View mainView;
    private BookmarkCursorAdapter adapter;
    private boolean isAllCheck = false;

    private AppCompatActivity mMainActivity;

    private RelativeLayout editRl;

    private boolean isEditing;

    public BookmarkFragment() {
    }

    public void setMainActivity(AppCompatActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_bookmark, container, false);

        dbHelper = new DbHelper(getContext());
        db = dbHelper.getWritableDatabase();

        ((ImageView) mainView.findViewById(R.id.my_f_bm_all)).setOnClickListener(this);
        ((ImageView) mainView.findViewById(R.id.my_f_bm_delete)).setOnClickListener(this);

        editRl = (RelativeLayout) mainView.findViewById(R.id.my_f_bookmark_rl);
        editRl.setVisibility(View.GONE);

        //리스트 내용 변경
        changeListView();

        AdView av = (AdView)mainView.findViewById(R.id.adView);
        AdRequest adRequest = new  AdRequest.Builder().build();
        av.loadAd(adRequest);

        return mainView;
    }

    public void changeListView() {
        if ( db != null ) {
            Cursor listCursor = db.rawQuery(DicQuery.getBookmark(), null);
            ListView listView = (ListView) mainView.findViewById(R.id.my_f_bookmark_lv);
            adapter = new BookmarkCursorAdapter(getContext(), listCursor, db, 0);
            listView.setAdapter(adapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(itemClickListener);
            listView.setSelection(0);
        }
    }

    /**
     * 북마크가 선택되면은 뉴스 상세창을 열어준다.
     */
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if ( !isEditing ) {
                Cursor cur = (Cursor) adapter.getItem(position);

                String kind = cur.getString(cur.getColumnIndexOrThrow("KIND"));
                String title = cur.getString(cur.getColumnIndexOrThrow("TITLE"));
                String url = cur.getString(cur.getColumnIndexOrThrow("URL"));

                Intent intent = new Intent(getActivity().getApplication(), WebViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("kind", kind);
                bundle.putString("title", title);
                bundle.putString("url", url);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        }
    };


    @Override
    public void onClick(View v) {
        DicUtils.dicLog("onClick");
        switch (v.getId()) {
            case R.id.my_f_bm_all :
                if ( isAllCheck ) {
                    isAllCheck = false;
                } else {
                    isAllCheck = true;
                }
                adapter.allCheck(isAllCheck);
                break;
            case R.id.my_f_bm_delete :
                if ( !adapter.isCheck() ) {
                    Toast.makeText(getContext(), "선택된 데이타가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    new android.app.AlertDialog.Builder(getActivity())
                            .setTitle("알림")
                            .setMessage("삭제하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.delete();
                                    changeListView();

                                    DicUtils.writeNewInfoToFile(getContext(), db);
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
        }
    }

    public void changeEdit( boolean isEditing ) {
        //처음에 오류가 발생하는 경우가 있음
        if ( editRl == null ) {
            return;
        }

        this.isEditing = isEditing;

        if ( isEditing ) {
            editRl.setVisibility(View.VISIBLE);
        } else {
            editRl.setVisibility(View.GONE);
        }

        adapter.editChange(isEditing);
    }
}

class BookmarkCursorAdapter extends CursorAdapter {
    private SQLiteDatabase mDb;
    public boolean[] isCheck;
    public int[] seq;
    private boolean isEditing = false;

    public BookmarkCursorAdapter(Context context, Cursor cursor, SQLiteDatabase db, int flags) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_bookmark_item, parent, false);

        ClickwordCursorAdapter.ViewHolder viewHolder = new ClickwordCursorAdapter.ViewHolder();
        viewHolder.cb = (CheckBox) view.findViewById(R.id.my_f_bi_cb_check);
        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                ClickwordCursorAdapter.ViewHolder viewHolder = (ClickwordCursorAdapter.ViewHolder)buttonView.getTag();
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
        ClickwordCursorAdapter.ViewHolder viewHolder = (ClickwordCursorAdapter.ViewHolder) view.getTag();
        viewHolder.position = cursor.getPosition();
        viewHolder.cb.setTag(viewHolder);

        ((TextView) view.findViewById(R.id.my_f_bi_tv_bookmark)).setText(cursor.getString(cursor.getColumnIndexOrThrow("TITLE")));
        ((TextView) view.findViewById(R.id.my_f_bi_tv_date)).setText(cursor.getString(cursor.getColumnIndexOrThrow("INS_DATE")));

        if ( isCheck[cursor.getPosition()] ) {
            ((CheckBox)view.findViewById(R.id.my_f_bi_cb_check)).setButtonDrawable(android.R.drawable.checkbox_on_background);
        } else {
            ((CheckBox)view.findViewById(R.id.my_f_bi_cb_check)).setButtonDrawable(android.R.drawable.checkbox_off_background);
        }

        if ( isEditing ) {
            ((CheckBox) view.findViewById(R.id.my_f_bi_cb_check)).setVisibility(View.VISIBLE);
        } else {
            ((CheckBox) view.findViewById(R.id.my_f_bi_cb_check)).setVisibility(View.GONE);
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
                DicDb.delDicBookmark(mDb, seq[i]);
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



