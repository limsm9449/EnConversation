package com.sleepingbear.enconversation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ConversationNoteFragment extends Fragment implements View.OnClickListener {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private View mainView;
    private ConversationNoteCursorAdapter adapter;
    private boolean isAllCheck = false;

    public Spinner s_group;
    public String groupCode;
    public int mSelect = 0;

    private boolean isEditing;

    public ConversationNoteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_conversation_note, container, false);

        dbHelper = new DbHelper(getContext());
        db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(DicQuery.getConversationGroup(), null);
        String[] from = new String[]{"KIND_NAME"};
        int[] to = new int[]{android.R.id.text1};
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(getContext(), android.R.layout.simple_spinner_item, cursor, from, to);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_group = (Spinner) mainView.findViewById(R.id.my_s_conversation_note);
        s_group.setAdapter(mAdapter);
        s_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                groupCode = ((Cursor) s_group.getSelectedItem()).getString(1);

                changeListView(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //리스트 내용 변경
        changeListView(true);

        AdView av = (AdView)mainView.findViewById(R.id.adView);
        AdRequest adRequest = new  AdRequest.Builder().build();
        av.loadAd(adRequest);

        return mainView;
    }

    public void changeListView(boolean isKeyin) {
        if ( db != null ) {
            Cursor listCursor = db.rawQuery(DicQuery.getConversationKind(groupCode), null);
            ListView listView = (ListView) mainView.findViewById(R.id.my_f_conversation_note_lv);
            adapter = new ConversationNoteCursorAdapter(getContext(), listCursor, 0);
            listView.setAdapter(adapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(itemClickListener);
            listView.setSelection(0);
        }
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if ( !isEditing ) {
                Cursor cur = (Cursor) adapter.getItem(position);

                Bundle bundle = new Bundle();
                bundle.putString("kind", cur.getString(cur.getColumnIndexOrThrow("KIND")));
                bundle.putString("kindName", cur.getString(cur.getColumnIndexOrThrow("KIND_NAME")));

                Intent intent = new Intent(getContext(), NoteActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onClick(View v) {
    }
}

class ConversationNoteCursorAdapter extends CursorAdapter {
    public ConversationNoteCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_conversation_note_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.my_tv_kind)).setText(cursor.getString(cursor.getColumnIndexOrThrow("KIND_NAME")));
    }

}



