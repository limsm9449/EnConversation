package com.sleepingbear.enconversation;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        Bundle b = getIntent().getExtras();
        StringBuffer allSb = new StringBuffer();
        StringBuffer CurrentSb = new StringBuffer();
        StringBuffer tempSb = new StringBuffer();


        tempSb.delete(0, tempSb.length());
        tempSb.append("* 뉴스" + CommConstants.sqlCR);
        tempSb.append("- 11개의 영문 뉴스가 있습니다. " + CommConstants.sqlCR);
        tempSb.append(" .국내 영어뉴스는 로딩이 빠르지만 외국 영어뉴스는 로딩이 많이 느립니다. 참고하세요." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "NEWS".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 뉴스 상세" + CommConstants.sqlCR);
        tempSb.append("- 영어뉴스를 보면서 필요한 단어 검색 기능이 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .뉴스를 보다가 모르는 단어를 클릭을 하면 하단에 클릭단어의 뜻이 보입니다. " + CommConstants.sqlCR);
        tempSb.append(" .클릭단어의 뜻이 없을경우 하단 오른쪽의 검색 버튼을 클릭하면 Naver,Daum에서 단어 검색을 할 수 있습니다. " + CommConstants.sqlCR);
        tempSb.append(" .하단 단어를 길게 클릭하시면 단어 상세를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .하단 단어 옆의 (+)를 클릭하시면 바로 단어장에 등록을 하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .뉴스의 단어를 길게 클릭하시면 단어보기, 단어검색(Naver,Daum), 번역, 문장보기, TTS, 전체TTS(4000자까지), 복사, 전체복사 기능을 사용하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .다음에 다시 읽고 싶은 기사가 있으면 상단의 북마크를 클릭하세요. 메인의 북마크 Tab에서 내용을 확인하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .뉴스를 보면서 클릭한 단어는 다 기록이 됩니다. 메인의 클릭단어 Tab에서 확인하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "WEB_VIEW".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 클릭단어" + CommConstants.sqlCR);
        tempSb.append("- 영어 뉴스를 보면서 클릭한 단어들에 대하여 관리하는 화면입니다." + CommConstants.sqlCR);
        tempSb.append(" .상단 수정 버튼(연필모양)를 클릭하시면 단어를 선택, 삭제, 단어장에 저장, 신규 단어장에 저장할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .단어를 클릭하시면 단어상세를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "CLICKWORD".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 북마크" + CommConstants.sqlCR);
        tempSb.append("- 북마크로 등록한 뉴스를 관리하는 화면입니다." + CommConstants.sqlCR);
        tempSb.append(" .상단 수정 버튼(연필모양)를 클릭하시면 북마크를 선택, 삭제할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "BOOKMARK".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 단어장" + CommConstants.sqlCR);
        tempSb.append("- 내가 등록한 단어를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .하단의 + 버튼을 클릭해서 신규 단어장을 추가할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .기존 단어장을 길게 클릭하시면 수정, 추가, 삭제,  내보내기, 가져오기를 하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .단어장을 클릭하시면 등록된 단어를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "VOCABULARY".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 단어장 - 나의 예문" + CommConstants.sqlCR);
        tempSb.append("- 내가 체크한 예문을 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "MY_SAMPLE".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 단어장 - 단어 학습" + CommConstants.sqlCR);
        tempSb.append("- 등록한 단어를 5가지 방법으로 공부할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .단어장 선택, 학습 종류 선택, 시간 선택을 하신후 학습시작을 클릭하세요." + CommConstants.sqlCR);
        tempSb.append(" .Default는 현재부터 60일전에 등록한 단어입니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "STUDY".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 단답 학습" + CommConstants.sqlCR);
        tempSb.append(" .단어를 클릭하시면 뜻을 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .별표를 클릭해서 암기여부를 표시합니다." + CommConstants.sqlCR);
        tempSb.append(" .단어를 길게 클릭하시면 단어 보기/전체 정답 보기를 선택하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "STUDY1".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 4지선다 학습" + CommConstants.sqlCR);
        tempSb.append(" .별표를 클릭해서 암기여부를 표시합니다." + CommConstants.sqlCR);
        tempSb.append(" .단어를 길게 클릭하시면 정답 보기/ 단어 보기/전체 정답 보기를 선택하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "STUDY2".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 카드형 학습" + CommConstants.sqlCR);
        tempSb.append("- 카드형 학습입니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "STUDY3".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 카드형 OX 학습" + CommConstants.sqlCR);
        tempSb.append("- 카드형 OX 학습입니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "STUDY4".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 카드형 4지선다 학습" + CommConstants.sqlCR);
        tempSb.append("- 카드형 4지선다 학습입니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "STUDY5".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 단어 상세" + CommConstants.sqlCR);
        tempSb.append("- 단어의 뜻, 발음, 상세 뜻, 예제, 기타 예제별로 단어 상세를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .별표를 클릭하시면 Default 단어장에 추가 됩니다." + CommConstants.sqlCR);
        tempSb.append(" .별표를 길게 클릭하시면 추가할 단어장을 선택하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "WORDVIEW".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 문장 상세" + CommConstants.sqlCR);
        tempSb.append("- 문장의 발음 및 관련 단어를 조회하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .연필 버튼을 클릭해서 문장을 입력하시면 관련 단어와 해석을 조회하실 수 있습니다.(해석은 정확도가 떨어지니 참고만 하세요)" + CommConstants.sqlCR);
        tempSb.append(" .단어를 클릭하시면 단어 보기 및 등록할 단어장을 선택 하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .별표를 클릭하시면 Default 단어장에 추가 됩니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( "SENTENCEVIEW".equals(b.getString("SCREEN")) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        if ( "ALL".equals(b.getString("SCREEN")) ) {
            ((TextView) this.findViewById(R.id.my_c_help_tv1)).setText(allSb.toString());
        } else {
            ((TextView) this.findViewById(R.id.my_c_help_tv1)).setText(CurrentSb.toString() + CommConstants.sqlCR + CommConstants.sqlCR + allSb.toString());
        }
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
