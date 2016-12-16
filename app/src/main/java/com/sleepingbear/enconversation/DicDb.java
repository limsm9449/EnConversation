package com.sleepingbear.enconversation;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class DicDb {

    public static void insDicVoc(SQLiteDatabase db, String entryId, String kind) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM DIC_VOC " + CommConstants.sqlCR);
        sql.append(" WHERE KIND = '" + kind + "'" + CommConstants.sqlCR);
        sql.append("   AND ENTRY_ID = '" + entryId + "'" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());

        sql.setLength(0);
        sql.append("INSERT INTO DIC_VOC (KIND, ENTRY_ID, MEMORIZATION,RANDOM_SEQ, INS_DATE) " + CommConstants.sqlCR);
        sql.append("SELECT '" + kind + "', ENTRY_ID, 'N', RANDOM(), '" + DicUtils.getDelimiterDate(DicUtils.getCurrentDate(), ".")  + "' " + CommConstants.sqlCR);
        sql.append("  FROM DIC " + CommConstants.sqlCR);
        sql.append(" WHERE ENTRY_ID = '" + entryId + "'" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());
    }

    public static void insDicVocForWord(SQLiteDatabase db, String word, String kind) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM DIC_VOC " + CommConstants.sqlCR);
        sql.append(" WHERE KIND = '" + kind + "'" + CommConstants.sqlCR);
        sql.append("   AND ENTRY_ID = (SELECT ENTRY_ID FROM DIC WHERE WORD = '" + word + "')" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());

        sql.setLength(0);
        sql.append("INSERT INTO DIC_VOC (KIND, ENTRY_ID, MEMORIZATION,RANDOM_SEQ, INS_DATE) " + CommConstants.sqlCR);
        sql.append("SELECT '" + kind + "', ENTRY_ID, 'N', RANDOM(), '" + DicUtils.getDelimiterDate(DicUtils.getCurrentDate(), ".")  + "' " + CommConstants.sqlCR);
        sql.append("  FROM DIC " + CommConstants.sqlCR);
        sql.append(" WHERE ENTRY_ID = (SELECT ENTRY_ID FROM DIC WHERE WORD = '" + word + "')" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());
    }

    public static void insDicVoc(SQLiteDatabase db, String entryId, String kind, String insDate) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM DIC_VOC " + CommConstants.sqlCR);
        sql.append(" WHERE KIND = '" + kind + "'" + CommConstants.sqlCR);
        sql.append("   AND ENTRY_ID = '" + entryId + "'" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());

        sql.setLength(0);
        sql.append("INSERT INTO DIC_VOC (KIND, ENTRY_ID, MEMORIZATION,RANDOM_SEQ, INS_DATE) " + CommConstants.sqlCR);
        sql.append("SELECT '" + kind + "', ENTRY_ID, 'N', RANDOM(), '" + insDate + "' " + CommConstants.sqlCR);
        sql.append("  FROM DIC " + CommConstants.sqlCR);
        sql.append(" WHERE ENTRY_ID = '" + entryId + "'" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());
    }

    public static void delDicVoc(SQLiteDatabase db, String entryId, String kind) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM DIC_VOC " + CommConstants.sqlCR);
        sql.append(" WHERE KIND = '" + kind + "'" + CommConstants.sqlCR);
        sql.append("   AND ENTRY_ID = '" + entryId + "'" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());
    }

    public static void delDicVocAll(SQLiteDatabase db, String entryId) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM DIC_VOC " + CommConstants.sqlCR);
        sql.append(" WHERE ENTRY_ID = '" + entryId + "'" + CommConstants.sqlCR);
        DicUtils.dicLog(sql.toString());
        db.execSQL(sql.toString());
    }

    public static void updMemory(SQLiteDatabase db, String entryId, String memoryYn) {
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE DIC_VOC " + CommConstants.sqlCR);
        sql.append("   SET MEMORIZATION = '" + memoryYn + "'" + CommConstants.sqlCR);
        sql.append(" WHERE ENTRY_ID = '" + entryId + "' " + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());
    }

    /**
     * 단어장 초기화
     * @param db
     */
    public static void initVocabulary(SQLiteDatabase db) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM DIC_VOC" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());

        sql.delete(0, sql.length());
        sql.append("DELETE FROM DIC_CODE" + CommConstants.sqlCR);
        sql.append(" WHERE CODE_GROUP = 'VOC'" + CommConstants.sqlCR);
        sql.append(" WHERE CODE != 'VOC0001'" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());
    }

    public static String getEntryIdForWord(SQLiteDatabase db, String word) {
        String rtn = "";
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ENTRY_ID  " + CommConstants.sqlCR);
        sql.append("  FROM DIC " + CommConstants.sqlCR);
        sql.append(" WHERE WORD = '" + word + "'" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());

        Cursor cursor = db.rawQuery(sql.toString(), null);
        if ( cursor.moveToNext() ) {
            rtn = cursor.getString(cursor.getColumnIndexOrThrow("ENTRY_ID"));
        }
        cursor.close();

        return rtn;
    }


    public static boolean isExistInNote(SQLiteDatabase db, String sampleSeq) {
        boolean rtn = false;

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT COUNT(*) CNT  " + CommConstants.sqlCR);
        sql.append("  FROM DIC_NOTE " + CommConstants.sqlCR);
        sql.append(" WHERE CODE IN (SELECT CODE FROM DIC_CODE WHERE GROUP_CD = 'C01')" + CommConstants.sqlCR);
        sql.append("   AND SAMPLE_SEQ = " + sampleSeq +  CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());

        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndexOrThrow("CNT")) > 0) {
                rtn = true;
            }
        }
        cursor.close();

        return rtn;
    }

    public static void initNote(SQLiteDatabase db, String groupCode) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM DIC_NOTE WHERE CODE IN (SELECT CODE FROM DIC_CODE WHERE GROUP_CD IN ('" + groupCode + "')) " + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());
    }

    public static HashMap getMean(SQLiteDatabase db, String word) {
        HashMap rtn = new HashMap();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT SPELLING, MEAN, ENTRY_ID  " + CommConstants.sqlCR);
        sql.append("  FROM DIC " + CommConstants.sqlCR);
        sql.append(" WHERE WORD = '" + word.toLowerCase().replaceAll("'", " ") + "' OR TENSE LIKE '% " + word.toLowerCase().replaceAll("'", " ") + " %'" + CommConstants.sqlCR);
        sql.append("ORDER  BY SPELLING DESC " + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());

        Cursor cursor = db.rawQuery(sql.toString(), null);
        if ( cursor.moveToNext() ) {
            rtn.put("SPELLING", cursor.getString(cursor.getColumnIndexOrThrow("SPELLING")));
            rtn.put("MEAN", cursor.getString(cursor.getColumnIndexOrThrow("MEAN")));
            rtn.put("ENTRY_ID", cursor.getString(cursor.getColumnIndexOrThrow("ENTRY_ID")));
        } else {
            rtn = getMeanOther(db, word);
        }
        cursor.close();

        return rtn;
    }

    public static HashMap getMeanOther(SQLiteDatabase db, String word) {
        HashMap rtn = new HashMap();
        String findWord = "";

        if ( "s".indexOf(word.substring(word.length() - 1)) > -1 ) {
            findWord = word.substring(0, word.length() - 1);
        } else if ( word.length() > 2 && "es,ed,ly".indexOf(word.substring(word.length() - 2)) > -1 ) {
            findWord = word.substring(0, word.length() - 2);
        } else if ( word.length() > 3 && "ing".indexOf(word.substring(word.length() - 3))  > -1 ) {
            findWord = word.substring(0, word.length() - 3);
        } else {
            findWord = word;
        }
        DicUtils.dicLog("findWord : " + findWord);

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT SPELLING, MEAN, ENTRY_ID  " + CommConstants.sqlCR);
        sql.append("  FROM DIC " + CommConstants.sqlCR);
        sql.append(" WHERE WORD = '" + findWord.toLowerCase().replaceAll("'", " ") + "'" +  CommConstants.sqlCR);
        sql.append("ORDER  BY SPELLING DESC " + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());

        Cursor cursor = db.rawQuery(sql.toString(), null);
        if ( cursor.moveToNext() ) {
            rtn.put("SPELLING", cursor.getString(cursor.getColumnIndexOrThrow("SPELLING")));
            rtn.put("MEAN", cursor.getString(cursor.getColumnIndexOrThrow("MEAN")));
            rtn.put("ENTRY_ID", cursor.getString(cursor.getColumnIndexOrThrow("ENTRY_ID")));
        }
        cursor.close();

        return rtn;
    }

    public static void insConversationStudy(SQLiteDatabase db, String sampleSeq, String insDate) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  COUNT(*) CNT" + CommConstants.sqlCR);
        sql.append("FROM    DIC_CODE " + CommConstants.sqlCR);
        sql.append("WHERE   CODE_GROUP = 'C02'" +  CommConstants.sqlCR);
        sql.append("AND     CODE = '" + insDate + "'" + CommConstants.sqlCR);
        Cursor cursor = db.rawQuery(sql.toString(), null);
        if ( cursor.moveToNext() ) {
            if ( cursor.getInt(cursor.getColumnIndexOrThrow("CNT")) == 0 ) {
                sql.setLength(0);
                sql.append("INSERT INTO DIC_CODE(CODE_GROUP, CODE, CODE_NAME) " + CommConstants.sqlCR);
                sql.append("VALUES('C02', '" + insDate + "', '" + insDate + "')" + CommConstants.sqlCR);
                db.execSQL(sql.toString());
            }
        }
        cursor.close();

        sql.setLength(0);
        sql.append("DELETE  FROM DIC_NOTE " + CommConstants.sqlCR);
        sql.append("WHERE   CODE = '" + insDate + "'" + CommConstants.sqlCR);
        sql.append("AND     SAMPLE_SEQ = '" + sampleSeq + "'" + CommConstants.sqlCR);
        db.execSQL(sql.toString());

        sql.setLength(0);
        sql.append("INSERT INTO DIC_NOTE (CODE, SAMPLE_SEQ) " + CommConstants.sqlCR);
        sql.append("VALUES('" + insDate + "', " + sampleSeq + ") " + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());

        sql.setLength(0);
        sql.append("UPDATE  DIC_CODE " + CommConstants.sqlCR);
        sql.append("SET     CODE_NAME = CODE || ' - ' || ( SELECT COUNT(*) FROM DIC_NOTE WHERE CODE = DIC_CODE.CODE ) || '개를 학습 하셨습니다.'  " + CommConstants.sqlCR);
        sql.append("WHERE   CODE_GROUP = 'C02' AND CODE = '" + insDate + "'" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());
    }

    public static void insConversationToNote(SQLiteDatabase db, String code, String sampleSeq) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE  FROM DIC_NOTE " + CommConstants.sqlCR);
        sql.append("WHERE   CODE = '" + code + "'" + CommConstants.sqlCR);
        sql.append("AND     SAMPLE_SEQ = '" + sampleSeq + "'" + CommConstants.sqlCR);
        db.execSQL(sql.toString());

        sql.setLength(0);
        sql.append("INSERT INTO DIC_NOTE (CODE, SAMPLE_SEQ) " + CommConstants.sqlCR);
        sql.append("VALUES('" + code + "', " + sampleSeq + ") " + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());
    }

    public static void delAllConversationFromNote(SQLiteDatabase db, int seq) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM DIC_NOTE " + CommConstants.sqlCR);
        sql.append(" WHERE SEQ = " + seq + "" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());
    }

    public static void delConversationFromNote(SQLiteDatabase db, String code, int seq) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM DIC_NOTE " + CommConstants.sqlCR);
        sql.append(" WHERE CODE = '" + code + "'" + CommConstants.sqlCR);
        sql.append("   AND SEQ = " + seq + "" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());
    }

    public static void copyConversationToNote(SQLiteDatabase db, String copyKind, int sampleSeq) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE  FROM DIC_NOTE " + CommConstants.sqlCR);
        sql.append("WHERE   CODE = '" + copyKind + "'" + CommConstants.sqlCR);
        sql.append("AND     SAMPLE_SEQ = '" + sampleSeq + "'" + CommConstants.sqlCR);
        db.execSQL(sql.toString());

        sql.setLength(0);
        sql.append("INSERT INTO DIC_NOTE (CODE, SAMPLE_SEQ) " + CommConstants.sqlCR);
        sql.append("VALUES('" + copyKind + "', " + sampleSeq + "') " + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());
    }

    public static void moveConversationToNote(SQLiteDatabase db, String currKind, String copyKind, int sampleSeq) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE  FROM DIC_NOTE " + CommConstants.sqlCR);
        sql.append("WHERE   CODE = '" + copyKind + "'" + CommConstants.sqlCR);
        sql.append("AND     SAMPLE_SEQ = '" + sampleSeq + "'" + CommConstants.sqlCR);
        db.execSQL(sql.toString());

        sql.setLength(0);
        sql.append("INSERT INTO DIC_NOTE (CODE, SAMPLE_SEQ) " + CommConstants.sqlCR);
        sql.append("VALUES('" + copyKind + "', " + sampleSeq + "') " + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());

        sql.setLength(0);
        sql.append("DELETE  FROM DIC_NOTE " + CommConstants.sqlCR);
        sql.append("WHERE   CODE = '" + currKind + "'" + CommConstants.sqlCR);
        sql.append("AND     SAMPLE_SEQ = '" + sampleSeq + "'" + CommConstants.sqlCR);
        db.execSQL(sql.toString());
    }

    public static void insCode(SQLiteDatabase db, String groupCode, String code, String codeName) {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO DIC_NOTE (CODE_GROUP, CODE, CODE_NAME) " + CommConstants.sqlCR);
        sql.append("VALUES('" + groupCode + "', " + code + "', " + codeName + "') " + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());
        db.execSQL(sql.toString());
    }
}
