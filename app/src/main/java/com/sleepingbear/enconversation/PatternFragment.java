package com.sleepingbear.enconversation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class PatternFragment extends Fragment {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private View mainView;
    private PatternFragCursorAdapter adapter;

    private Cursor cursor;

    private Activity mActivity;

    public PatternFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_pattern, container, false);


        dbHelper = new DbHelper(getContext());
        db = dbHelper.getWritableDatabase();

        changeListView(true);

        AdView av = (AdView)mainView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        av.loadAd(adRequest);

        return mainView;
    }

    public void changeListView(boolean isKeyin) {
        if ( isKeyin ) {
            if ( db != null ) {
                cursor = db.rawQuery(DicQuery.getPatternList(), null);

                if (cursor.getCount() == 0) {
                    Toast.makeText(getContext(), "검색된 데이타가 없습니다.", Toast.LENGTH_SHORT).show();
                }

                ListView listView = (ListView) mainView.findViewById(R.id.my_f_convation_lv);
                adapter = new PatternFragCursorAdapter(getContext(), cursor, 0);
                listView.setAdapter(adapter);

                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                listView.setOnItemClickListener(itemClickListener);
                listView.setOnItemLongClickListener(itemLongClickListener);
                listView.setSelection(0);
            }
        }
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur = (Cursor) adapter.getItem(position);

            Bundle bundle = new Bundle();
            bundle.putString("PATTERN", cur.getString(cur.getColumnIndexOrThrow("PATTERN")));
            bundle.putString("DESC", cur.getString(cur.getColumnIndexOrThrow("DESC")));
            bundle.putString("SQL_WHERE", cur.getString(cur.getColumnIndexOrThrow("SQL_WHERE")));

            Intent intent = new Intent(getActivity().getApplication(), PatternActivity.class);
            intent.putExtras(bundle);

            startActivity(intent);
        }
    };

    AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur = (Cursor) adapter.getItem(position);

            Bundle bundle = new Bundle();
            bundle.putString("kind", "PATTERN");
            bundle.putString("sqlWhere", cur.getString(cur.getColumnIndexOrThrow("SQL_WHERE")));

            Intent intent = new Intent(getActivity().getApplicationContext(), NoteStudyActivity.class);
            intent.putExtras(bundle);

            startActivity(intent);

            return true;
        }
    };
}

class PatternFragCursorAdapter extends CursorAdapter {
    int fontSize = 0;

    public PatternFragCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);

        fontSize = Integer.parseInt( DicUtils.getPreferencesValue( context, CommConstants.preferences_font ) );
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_pattern_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.my_tv_pattern)).setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("PATTERN"))));
        ((TextView) view.findViewById(R.id.my_tv_pattern_desc)).setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("DESC"))));

        //사이즈 설정
        ((TextView) view.findViewById(R.id.my_tv_pattern)).setTextSize(fontSize);
        ((TextView) view.findViewById(R.id.my_tv_pattern_desc)).setTextSize(fontSize);
    }

}
