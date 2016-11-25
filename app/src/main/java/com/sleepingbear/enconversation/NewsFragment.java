package com.sleepingbear.enconversation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;


public class NewsFragment extends Fragment {
    private View  mainView;
    private ListView listView;
    private NewsAdapter adapter;

    public NewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_news, container, false);

        listView = (ListView)mainView.findViewById(R.id.my_f_news_lv);

        ArrayList<NewsVo> items = new ArrayList<>();
        items.add(new NewsVo("E001", "Chosun",R.drawable.img_chosunilbo));
        items.add(new NewsVo("E002", "Joongang Daily",R.drawable.img_joongangdaily));
        items.add(new NewsVo("E003", "Korea Herald",R.drawable.img_koreaherald));
        items.add(new NewsVo("E004", "Korea Times",R.drawable.img_koreatimes));
        items.add(new NewsVo("E005", "ABC",R.drawable.img_abcnews));
        items.add(new NewsVo("E006", "BBC",R.drawable.img_bbc));
        items.add(new NewsVo("E007", "CNN",R.drawable.img_cnn));
        items.add(new NewsVo("E008", "Los Angeles Times",R.drawable.img_losangelestimes));
        items.add(new NewsVo("E009", "The New Work Times",R.drawable.img_newworktimes));
        items.add(new NewsVo("E010", "Reuters",R.drawable.img_reuters));
        items.add(new NewsVo("E011", "Washingtone Post",R.drawable.img_washingtonepost));

        adapter = new NewsAdapter(getContext(), 0, items);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(itemClickListener);

        AdView av = (AdView)mainView.findViewById(R.id.adView);
        AdRequest adRequest = new  AdRequest.Builder().build();
        av.loadAd(adRequest);

        return mainView;
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NewsVo cur = (NewsVo) adapter.getItem(position);

            DicUtils.dicLog(cur.getName());

            Intent intent = new Intent(getActivity().getApplication(), WebViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("kind", cur.getKind());
            intent.putExtras(bundle);

            getActivity().startActivityForResult(intent, CommConstants.a_news);
        }
    };

    private class NewsVo {
        private String kind;
        private String name;
        private int imageRes;

        public NewsVo(String kind, String name, int imageRes) {
            this.kind = kind;
            this.name = name;
            this.imageRes = imageRes;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getImageRes() {
            return imageRes;
        }

        public void setImageRes(int imageRes) {
            this.imageRes = imageRes;
        }
    }

    private class NewsAdapter extends ArrayAdapter<NewsVo> {
        private ArrayList<NewsVo> items;

        public NewsAdapter(Context context, int textViewResourceId, ArrayList<NewsVo> objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.fragment_news_item, null);
            }

            // ImageView 인스턴스
            ImageView imageView = (ImageView)v.findViewById(R.id.my_f_news_item_iv);
            imageView.setImageResource(items.get(position).imageRes);

            return v;
        }
    }

}
