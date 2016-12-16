package com.sleepingbear.enconversation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Administrator on 2015-11-27.
 */
public class DicUtils {
    public static String getString(String str) {
        if (str == null)
            return "";
        else
            return str.trim();
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return year + "" + (month + 1 > 9 ? "" : "0") + (month + 1) + "" + (day > 9 ? "" : "0") + day;
    }

    public static String getAddDay(String date, int addDay) {
        String mDate = date.replaceAll("[.-/]", "");

        int year = Integer.parseInt(mDate.substring(0, 4));
        int month = Integer.parseInt(mDate.substring(4, 6)) - 1;
        int day = Integer.parseInt(mDate.substring(6, 8));

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day + addDay);

        return c.get(Calendar.YEAR) + "" + (c.get(Calendar.MONTH) + 1 > 9 ? "" : "0") + (c.get(Calendar.MONTH) + 1) + "" + (c.get(Calendar.DAY_OF_MONTH) > 9 ? "" : "0") + c.get(Calendar.DAY_OF_MONTH);
    }

    public static String getDelimiterDate(String date, String delimiter) {
        if (getString(date).length() < 8) {
            return "";
        } else {
            return date.substring(0, 4) + delimiter + date.substring(4, 6) + delimiter + date.substring(6, 8);
        }
    }

    public static String getYear(String date) {
        if (date == null) {
            return "";
        } else {
            String mDate = date.replaceAll("[.-/]", "");
            return mDate.substring(0, 4);
        }
    }

    public static String getMonth(String date) {
        if (date == null) {
            return "";
        } else {
            String mDate = date.replaceAll("[.-/]", "");
            return mDate.substring(4, 6);
        }
    }

    public static String getDay(String date) {
        if (date == null) {
            return "";
        } else {
            String mDate = date.replaceAll("[.-/]", "");
            return mDate.substring(6, 8);
        }
    }

    public static void dicSqlLog(String str) {
        if (BuildConfig.DEBUG) {
            Log.d(CommConstants.tag + " ====>", str);
        }
    }

    public static void dicLog(String str) {
        if (BuildConfig.DEBUG) {
            Calendar cal = Calendar.getInstance();
            String time = cal.get(Calendar.HOUR_OF_DAY) + "시 " + cal.get(Calendar.MINUTE) + "분 " + cal.get(Calendar.SECOND) + "초";

            Log.d(CommConstants.tag + " ====>", time + " : " + str);
        }
    }

    public static String lpadding(String str, int length, String fillStr) {
        String rtn = "";

        for (int i = 0; i < length - str.length(); i++) {
            rtn += fillStr;
        }
        return rtn + (str == null ? "" : str);
    }

    public static String[] sentenceSplit(String sentence) {
        ArrayList<String> al = new ArrayList<String>();

        if ( sentence != null ) {
            String tmpSentence = sentence + " ";

            int startPos = 0;
            for (int i = 0; i < tmpSentence.length(); i++) {
                if (CommConstants.sentenceSplitStr.indexOf(tmpSentence.substring(i, i + 1)) > -1) {
                    if (i == 0) {
                        al.add(tmpSentence.substring(i, i + 1));
                        startPos = i + 1;
                    } else {
                        if (i != startPos) {
                            al.add(tmpSentence.substring(startPos, i));
                        }
                        al.add(tmpSentence.substring(i, i + 1));
                        startPos = i + 1;
                    }
                }
            }
        }

        String[] stringArr = new String[al.size()];
        stringArr = al.toArray(stringArr);

        return stringArr;
    }

    public static String getSentenceWord(String[] sentence, int kind, int position) {
        String rtn = "";
        if ( kind == 1 ) {
            rtn = sentence[position];
        } else if ( kind == 2 ) {
            if ( position + 2 <= sentence.length - 1 ) {
                if ( " ".equals(sentence[position + 1]) ) {
                    rtn = sentence[position] + sentence[position + 1] + sentence[position + 2];
                }
            }
        } else if ( kind == 3 ) {
            if ( position + 4 <= sentence.length - 1 ) {
                if ( " ".equals(sentence[position + 1]) && " ".equals(sentence[position + 3]) ) {
                    rtn = sentence[position] + sentence[position + 1] + sentence[position + 2] + sentence[position + 3] + sentence[position + 4];
                }
            }
        }

        //dicLog(rtn);
        return rtn;
    }

    public static String getOneSpelling(String spelling) {
        String rtn = "";
        String[] str = spelling.split(",");
        if ( str.length == 1 ) {
            rtn = spelling;
        } else {
            rtn = str[0] + "(" + str[1] + ")";
        }

        return rtn;
    }

    public static void writeInfoToFile(Context ctx, String saveData) {
        try {
            FileOutputStream fos = ctx.openFileOutput(CommConstants.infoFileName, ctx.MODE_APPEND);
            fos.write(saveData.getBytes());
            fos.write("\n".getBytes());
            fos.close();
        } catch (Exception e) {
            DicUtils.dicLog("File 에러=" + e.toString());
        }
    }

    public static void readInfoFromFile(Context ctx, SQLiteDatabase db) {
        readInfoFromFile(ctx, db, "");
    }
    public static void readInfoFromFile(Context ctx, SQLiteDatabase db, String fileName) {
        dicLog(DicUtils.class.toString() + " : " + "readInfoFromFile start");

        //데이타 복구
        FileInputStream fis = null;
        try {
            //데이타 초기화
            DicDb.initVocabulary(db);
            DicDb.initNote(db, "C01");
            DicDb.initNote(db, "C02");

            if ( fileName == null || fileName.length() == 0 ) {
                fis = ctx.openFileInput(CommConstants.infoFileName);
            } else {
                fis = new FileInputStream(new File(fileName));
            }

            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader buffreader = new BufferedReader(isr);

            //출력...
            String readString = buffreader.readLine();
            while (readString != null) {
                dicLog(readString);

                String[] row = readString.split(":");
                if ( row[0] ==  CommConstants.tag_code_ins) {
                    DicDb.insCode(db, row[1], row[2], row[3]);
                } else if ( row[0] ==  CommConstants.tag_code_upd) {
                } else if ( row[0] ==  CommConstants.tag_code_del) {
                } else if ( row[0] ==  CommConstants.tag_note_ins) {
                } else if ( row[0] ==  CommConstants.tag_note_del) {
                } else if ( row[0] ==  CommConstants.tag_note_del_all) {
                } else if ( row[0] ==  CommConstants.tag_voc_ins) {
                    DicDb.insDicVoc(db, row[3], row[1], row[2]);
                } else if ( row[0] ==  CommConstants.tag_voc_del) {
                    DicDb.delDicVoc(db, row[2], row[1]);
                } else if ( row[0] ==  CommConstants.tag_voc_del_all) {
                    DicDb.delDicVocAll(db, row[1]);
                } else if ( row[0] ==  CommConstants.tag_voc_memory) {
                }


                switch (row[0]) {
                    case "MYWORD_DELETE_ALL":
                        //단어장 전체 삭제
                        //DicUtils. writeInfoToFile(getApplicationContext(), "MYWORD_DELETE_ALL" + ":" + entryId);

                        break;
                    case "CATEGORY_INSERT":
                        //DicUtils. writeInfoToFile(getContext(), "CATEGORY_INSERT" + ":" + insCategoryCode + ":" + et_ins.getText().toString());
                        db.execSQL(DicQuery.getInsNewCategory("MY", row[1], row[2]));
                        break;
                    case "CATEGORY_UPDATE":
                        //DicUtils. writeInfoToFile(getContext(), "CATEGORY_UPDATE" + ":" + (String) v.getTag() + ":" + et_ins.getText().toString());
                        db.execSQL(DicQuery.getUpdCategory("MY", row[1], row[2]));
                        break;
                    case "CATEGORY_DELETE":
                        //DicUtils. writeInfoToFile(getContext(), "CATEGORY_DELETE" + ":" + code);
                        db.execSQL(DicQuery.getDelCategory("MY", row[1]));
                        db.execSQL(DicQuery.getDelDicVoc(row[1]));
                        break;
                    case "MEMORY":
                        //DicUtils.writeInfoToFile(context, "MEMORY" + ":" + entryId + ":" + (((CheckBox) v.findViewById(R.id.my_c_vi_cb_memorization)).isChecked() ? "Y" : "N"));
                        DicDb.updMemory(db, row[1], row[2]);
                        break;

                }
                readString = buffreader.readLine();
            }

            isr.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        dicLog(DicUtils.class.toString() + " : " + "readInfoFromFile end");

        //데이타 기록
        writeNewInfoToFile(ctx, db);
    }

    /**
     * 데이타 기록
     * @param ctx
     * @param db
     */
    public static void writeNewInfoToFile(Context ctx, SQLiteDatabase db) {
        System.out.println("writeNewInfoToFile start");

        writeNewInfoToFile(ctx, db, "");

        System.out.println("writeNewInfoToFile end");
    }
    public static void writeNewInfoToFile(Context ctx, SQLiteDatabase db, String fileName) {
        try {
            FileOutputStream fos = null;

            if ( fileName == null || fileName.length() == 0 ) {
                fos = ctx.openFileOutput(CommConstants.infoFileName, ctx.MODE_PRIVATE);
            } else {
                File saveFile = new File(fileName);
                try {
                    saveFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
                fos = new FileOutputStream(saveFile);
            }

            Cursor cursor = db.rawQuery(DicQuery.getWriteData(), null);
            while (cursor.moveToNext()) {
                DicUtils.dicLog(cursor.getString(cursor.getColumnIndexOrThrow("WRITE_DATA")));
                fos.write((cursor.getString(cursor.getColumnIndexOrThrow("WRITE_DATA")).getBytes()));
                fos.write("\n".getBytes());
            }
            cursor.close();

            fos.close();
        } catch (Exception e) {
            DicUtils.dicLog("File 에러=" + e.toString());
        }
    }

    public static boolean isHangule(String pStr) {
        boolean isHangule = false;
        String str = (pStr == null ? "" : pStr);
        try {
            if(str.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
                isHangule = true;
            } else {
                isHangule = false;
            }
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }

        return isHangule;
    }

    public static Document getDocument(String url) throws Exception {
        Document doc = null;
        //while (true) {
        //    try {
                doc = Jsoup.connect(url).timeout(60000).get();
        //        break;
        //    } catch (Exception e) {
        //        System.out.println(e.getMessage());
        //    }
        //}

        return doc;
    }

    public static Element findElementSelect(Document doc, String tag, String attr, String value) throws Exception {
        Elements es = doc.select(tag);
        for (Element es_r : es) {
            if (value.equals(es_r.attr(attr))) {
                return es_r;
            }
        }

        return null;
    }

    public static Element findElementForTag(Element e, String tag, int findIdx) throws Exception {
        if (e == null) {
            return null;
        }

        int idx = 0;
        for (int i = 0; i < e.children().size(); i++) {
            if (tag.equals(e.child(i).tagName())) {
                if (idx == findIdx) {
                    return e.child(i);
                } else {
                    idx++;
                }
            }
        }

        return null;
    }

    public static Element findElementForTagAttr(Element e, String tag, String attr, String value) throws Exception {
        if (e == null) {
            return null;
        }

        for (int i = 0; i < e.children().size(); i++) {
            if (tag.equals(e.child(i).tagName()) && value.equals(e.child(i).attr(attr))) {
                return e.child(i);
            }
        }

        return null;
    }

    public static String getAttrForTagIdx(Element e, String tag, int findIdx, String attr) throws Exception {
        if (e == null) {
            return null;
        }

        int idx = 0;
        for (int i = 0; i < e.children().size(); i++) {
            if (tag.equals(e.child(i).tagName())) {
                if (idx == findIdx) {
                    return e.child(i).attr(attr);
                } else {
                    idx++;
                }
            }
        }

        return "";
    }

    public static String getElementText(Element e) throws Exception {
        if (e == null) {
            return "";
        } else {
            return e.text();
        }
    }

    public static String getElementHtml(Element e) throws Exception {
        if (e == null) {
            return "";
        } else {
            return e.html();
        }
    }

    public static String getUrlParamValue(String url, String param) throws Exception {
        String rtn = "";

        if (url.indexOf("?") < 0) {
            return "";
        }
        String[] split_url = url.split("[?]");
        String[] split_param = split_url[1].split("[&]");
        for (int i = 0; i < split_param.length; i++) {
            String[] split_row = split_param[i].split("[=]");
            if (param.equals(split_row[0])) {
                rtn = split_row[1];
            }
        }

        return rtn;
    }

    public static Boolean isNetWork(AppCompatActivity context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
        boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
        boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if ((isWifiAvailable && isWifiConnect) || (isMobileAvailable && isMobileConnect)){
            return true;
        }else{
            return false;
        }
    }

}