package com.sleepingbear.enconversation;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class PatchActivity extends AppCompatActivity {
    private int fontSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patch);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setTitle("패치 내용");
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        StringBuffer patch = new StringBuffer();

        patch.append("* 신규 패치" + CommConstants.sqlCR);
        patch.append("- 단어장 상세 부분을 네이버 검색, Daum 검색, 예제로 변경하였습니다." + CommConstants.sqlCR);
        patch.append("- 영어사전, 영어회화, 영어신문을 하나로 통합하여 사용하면 좋을듯해서 '최고의 영어학습' 어플을 새로 만들었습니다. 한개의 어플로 계속 기능개선을 할 예정입니다." + CommConstants.sqlCR);
        patch.append("" + CommConstants.sqlCR);
        patch.append("- 어플이 가로로 변경이 되는 문제점 수정 - 가로/세로 변경시 화면 구성 및 오류가 발생" + CommConstants.sqlCR);
        patch.append("- 단어장에서 TTS로 단어, 뜻을 듣는 기능 추가 - 상단 Context Menu에서 TTS 선택" + CommConstants.sqlCR);
        patch.append("- 단어학습에서 '카드형 4지선다 TTS 학습' 기능 추가" + CommConstants.sqlCR);
        patch.append("- 네이버 회화를 보고 돌아올 경우 리스트가 처음으로 가는 문제점 수정" + CommConstants.sqlCR);
        patch.append("- 단어장 편집시 전체를 선택하고 이동,삭제,복사를 한 후에 체크를 두번씩 해야 하는 문제점 수정" + CommConstants.sqlCR);
        patch.append("- 2016.12.21 : 영어회화 어플 개발" + CommConstants.sqlCR);

        ((TextView) this.findViewById(R.id.my_c_patch_tv1)).setText(patch.toString());

        AdView av = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        av.loadAd(adRequest);

        fontSize = Integer.parseInt( DicUtils.getPreferencesValue( this, CommConstants.preferences_font ) );
        ((TextView) this.findViewById(R.id.my_c_patch_tv1)).setTextSize(fontSize);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
