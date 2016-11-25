package com.sleepingbear.enconversation;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DicQuery {
    public static String getDicForWord(String word) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT A.*, " + CommConstants.sqlCR);
        sql.append("       SEQ _id, " + CommConstants.sqlCR);
        sql.append("       (SELECT COUNT(*) FROM DIC_VOC WHERE ENTRY_ID = A.ENTRY_ID) MY_VOC " + CommConstants.sqlCR);
        sql.append("  FROM DIC A " + CommConstants.sqlCR);
        sql.append(" WHERE WORD = '" + word.toLowerCase().replaceAll("'", " ") + "' OR TENSE LIKE '% " + word.toLowerCase().replaceAll("'", " ") + " %'" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getVocCategory() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT 1 _id, CODE KIND, CODE_NAME KIND_NAME," + CommConstants.sqlCR);
        sql.append("            COALESCE((SELECT COUNT(*)" + CommConstants.sqlCR);
        sql.append("                        FROM DIC_VOC" + CommConstants.sqlCR);
        sql.append("                       WHERE KIND = A.CODE" + CommConstants.sqlCR);
        sql.append("                       GROUP BY  KIND),0) CNT" + CommConstants.sqlCR);
        sql.append("  FROM DIC_CODE A" + CommConstants.sqlCR);
        sql.append(" WHERE CODE_GROUP = 'MY'" + CommConstants.sqlCR);
        sql.append(" ORDER BY 1,3" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String updVocRandom() {
        StringBuffer sql = new StringBuffer();

        sql.append("UPDATE DIC_VOC" + CommConstants.sqlCR);
        sql.append("   SET RANDOM_SEQ = RANDOM()" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getSampleAnswerForStudy(String vocKind, int answerCnt) {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT SEQ _id," + CommConstants.sqlCR);
        sql.append("       WORD," + CommConstants.sqlCR);
        sql.append("       MEAN" + CommConstants.sqlCR);
        sql.append("FROM   DIC" + CommConstants.sqlCR);
        sql.append("WHERE  ENTRY_ID NOT IN (SELECT ENTRY_ID FROM DIC_VOC WHERE KIND = '" + vocKind + "')" + CommConstants.sqlCR);
        sql.append("AND    SPELLING != ''" + CommConstants.sqlCR);
        sql.append("ORDER  BY RANDOM()" + CommConstants.sqlCR);
        sql.append("LIMIT  " + answerCnt + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getVocabularyCategoryCount() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT 1 _id, CODE KIND, CODE_NAME KIND_NAME," + CommConstants.sqlCR);
        sql.append("            COALESCE((SELECT COUNT(*)" + CommConstants.sqlCR);
        sql.append("                        FROM DIC_VOC" + CommConstants.sqlCR);
        sql.append("                       WHERE KIND = A.CODE),0) CNT" + CommConstants.sqlCR);
        sql.append("  FROM DIC_CODE A" + CommConstants.sqlCR);
        sql.append(" WHERE CODE_GROUP = 'MY'" + CommConstants.sqlCR);
        sql.append(" ORDER BY 1,3" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getSentenceViewContextMenu() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT 2 _id, 2 ORD, CODE KIND, CODE_NAME||' 등록' KIND_NAME" + CommConstants.sqlCR);
        sql.append("  FROM DIC_CODE A" + CommConstants.sqlCR);
        sql.append(" WHERE CODE_GROUP = 'MY'" + CommConstants.sqlCR);
        sql.append(" ORDER BY 1,4" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getVocabularyCategory() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT 1 _id, CODE KIND, CODE_NAME KIND_NAME" + CommConstants.sqlCR);
        sql.append("  FROM DIC_CODE A" + CommConstants.sqlCR);
        sql.append(" WHERE CODE_GROUP = 'MY'" + CommConstants.sqlCR);
        sql.append(" ORDER BY 1,3" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getInsCategoryCode(SQLiteDatabase mDb) {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT MAX(CODE) CODE" + CommConstants.sqlCR);
        sql.append("  FROM DIC_CODE" + CommConstants.sqlCR);
        sql.append(" WHERE CODE_GROUP = 'MY'" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());

        String insCategoryCode = "";
        Cursor maxCategoryCursor = mDb.rawQuery(sql.toString(), null);
        if ( maxCategoryCursor.moveToNext() ) {
            String max = maxCategoryCursor.getString(maxCategoryCursor.getColumnIndexOrThrow("CODE"));
            int maxCategory = Integer.parseInt(max.substring(2,max.length()));
            insCategoryCode = "MY" + DicUtils.lpadding(Integer.toString(maxCategory + 1), 4, "0");
            DicUtils.dicSqlLog("insCategoryCode : " + insCategoryCode);
        }

        return insCategoryCode;
    }

    public static String getInsNewCategory(String codeGroup, String code, String codeName) {
        StringBuffer sql = new StringBuffer();

        sql.append("INSERT INTO DIC_CODE(CODE_GROUP, CODE, CODE_NAME)" + CommConstants.sqlCR);
        sql.append("VALUES('" + codeGroup + "', '" + code + "', '" + codeName + "')" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getUpdCategory(String codeGroup, String code, String codeName) {
        StringBuffer sql = new StringBuffer();

        sql.append("UPDATE DIC_CODE" + CommConstants.sqlCR);
        sql.append("   SET CODE_NAME = '" + codeName + "'" + CommConstants.sqlCR);
        sql.append(" WHERE CODE_GROUP = '" + codeGroup + "'" + CommConstants.sqlCR);
        sql.append("   AND CODE = '" + code + "'" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getDelCategory(String codeGroup, String code) {
        StringBuffer sql = new StringBuffer();

        sql.append("DELETE FROM DIC_CODE" + CommConstants.sqlCR);
        sql.append(" WHERE CODE_GROUP = '" + codeGroup + "'" + CommConstants.sqlCR);
        sql.append("   AND CODE = '" + code + "'" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getDelDicVoc(String code) {
        StringBuffer sql = new StringBuffer();

        sql.append("DELETE FROM DIC_VOC" + CommConstants.sqlCR);
        sql.append(" WHERE KIND = '" + code + "'" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getMainCategoryCount() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT 1 _id, CODE KIND, CODE_NAME KIND_NAME" + CommConstants.sqlCR);
        sql.append("  FROM DIC_CODE" + CommConstants.sqlCR);
        sql.append(" WHERE CODE_GROUP = 'GRP'" + CommConstants.sqlCR);
        sql.append("   AND CODE LIKE 'W%'" + CommConstants.sqlCR);
        sql.append(" UNION" + CommConstants.sqlCR);
        sql.append("SELECT 2 _id, CODE KIND, CODE_NAME KIND_NAME" + CommConstants.sqlCR);
        sql.append("  FROM DIC_CODE" + CommConstants.sqlCR);
        sql.append(" WHERE CODE_GROUP = 'GRP'" + CommConstants.sqlCR);
        sql.append("   AND CODE LIKE 'S%'" + CommConstants.sqlCR);
        sql.append(" ORDER BY _ID, CODE" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getSubCategoryCount(String codeGroup, int mOrder) {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT 1 _id, 2 ORD, CODE_GROUP, CODE KIND, CODE_NAME KIND_NAME, UPD_DATE, W_CNT, S_CNT, BOOKMARK_CNT" + CommConstants.sqlCR);
        sql.append("  FROM DIC_CODE A" + CommConstants.sqlCR);
        sql.append(" WHERE CODE_GROUP = '" + codeGroup + "'" + CommConstants.sqlCR);
        if ( mOrder == 0 ) {
            sql.append(" ORDER BY A.BOOKMARK_CNT" + CommConstants.sqlCR);
        } else if ( mOrder == 1 ) {
            sql.append(" ORDER BY A.BOOKMARK_CNT DESC" + CommConstants.sqlCR);
        } else if ( mOrder == 2 ) {
            sql.append(" ORDER BY A.UPD_DATE" + CommConstants.sqlCR);
        } else if ( mOrder == 3 ) {
            sql.append(" ORDER BY A.UPD_DATE DESC" + CommConstants.sqlCR);
        } else if ( mOrder == 4 ) {
            sql.append(" ORDER BY A.CODE_NAME" + CommConstants.sqlCR);
        } else if ( mOrder == 5 ) {
            sql.append(" ORDER BY A.CODE_NAME DESC" + CommConstants.sqlCR);
        }
        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getMyDic() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT A.*, B.WORD " + CommConstants.sqlCR);
        sql.append(" FROM DIC_VOC A, DIC B" + CommConstants.sqlCR);
        sql.append(" WHERE A.ENTRY_ID = B.ENTRY_ID" + CommConstants.sqlCR);


        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getMyMemoryDic() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT A.*, B.WORD " + CommConstants.sqlCR);
        sql.append("  FROM DIC_VOC A, DIC B" + CommConstants.sqlCR);
        sql.append(" WHERE A.ENTRY_ID = B.ENTRY_ID" + CommConstants.sqlCR);
        sql.append("   AND A.MEMORIZATION = 'Y'" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getToday() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT A.TODAY, B.WORD, B.ENTRY_ID " + CommConstants.sqlCR);
        sql.append("  FROM DIC_TODAY A, DIC B" + CommConstants.sqlCR);
        sql.append(" WHERE A.ENTRY_ID = B.ENTRY_ID" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getVocabularyCount() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT 1 _id, COUNT(*) CNT" + CommConstants.sqlCR);
        sql.append("  FROM DIC_VOC" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getGrammar() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT SEQ _id, GRAMMAR, MEAN, DESCRIPTION, SAMPLES, ORD " + CommConstants.sqlCR);
        sql.append(" FROM DIC_GRAMMAR" + CommConstants.sqlCR);
        sql.append("ORDER BY GRAMMAR" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getSaveVocabulary(String kind) {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT B.WORD, B.SPELLING, B.MEAN" + CommConstants.sqlCR);
        sql.append("  FROM DIC_VOC A, DIC B" + CommConstants.sqlCR);
        sql.append(" WHERE A.ENTRY_ID = B.ENTRY_ID" + CommConstants.sqlCR);
        sql.append("   AND A.KIND = '" + kind + "'" + CommConstants.sqlCR);
        sql.append(" ORDER BY B.WORD" + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getMySample() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT SEQ _id, TODAY, SENTENCE1, SENTENCE2" + CommConstants.sqlCR);
        sql.append("  FROM DIC_MY_SAMPLE" + CommConstants.sqlCR);
        sql.append(" ORDER BY TODAY DESC, SENTENCE1" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getCategoryWord(String categoryId) {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT SEQ _id, ENTRY_ID" + CommConstants.sqlCR);
        sql.append("  FROM DIC_CATEGORY_WORD" + CommConstants.sqlCR);
        sql.append(" WHERE CODE = '" + categoryId + "'" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getCategory(String codeGroup, String code) {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT *" + CommConstants.sqlCR);
        sql.append("  FROM DIC_CODE" + CommConstants.sqlCR);
        sql.append(" WHERE CODE_GROUP = '" + codeGroup + "'" + CommConstants.sqlCR);
        sql.append("   AND CODE = '" + code + "'" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getClickword() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT B.SEQ _id, A.SEQ, B.ENTRY_ID, B.WORD, B.MEAN, B.SPELLING, A.INS_DATE" + CommConstants.sqlCR);
        sql.append("FROM   DIC_CLICK_WORD A, DIC B" + CommConstants.sqlCR);
        sql.append("WHERE  A.ENTRY_ID = B.ENTRY_ID " + CommConstants.sqlCR);
        sql.append("ORDER  BY A.INS_DATE DESC, B.WORD" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getBookmark() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT SEQ _id, SEQ, KIND, TITLE, URL, INS_DATE" + CommConstants.sqlCR);
        sql.append("FROM   DIC_BOOKMARK" + CommConstants.sqlCR);
        sql.append("ORDER  BY INS_DATE DESC, TITLE" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getWriteData() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT 'CATEGORY_INSERT'||':'||A.CODE||':'||A.CODE_NAME WRITE_DATA" + CommConstants.sqlCR);
        sql.append("  FROM DIC_CODE A" + CommConstants.sqlCR);
        sql.append(" WHERE CODE_GROUP = 'MY'" + CommConstants.sqlCR);
        sql.append("   AND CODE != 'MY0000'" + CommConstants.sqlCR);
        sql.append("UNION" + CommConstants.sqlCR);

        sql.append("SELECT 'MYWORD_INSERT'||':'||A.KIND||':'||A.INS_DATE||':'||A.ENTRY_ID WRITE_DATA " + CommConstants.sqlCR);
        sql.append(" FROM DIC_VOC A, DIC B" + CommConstants.sqlCR);
        sql.append(" WHERE A.ENTRY_ID = B.ENTRY_ID" + CommConstants.sqlCR);
        sql.append("UNION" + CommConstants.sqlCR);

        sql.append("SELECT 'MEMORY'||':'||A.ENTRY_ID||'Y' WRITE_DATA " + CommConstants.sqlCR);
        sql.append("  FROM DIC_VOC A, DIC B" + CommConstants.sqlCR);
        sql.append(" WHERE A.ENTRY_ID = B.ENTRY_ID" + CommConstants.sqlCR);
        sql.append("   AND A.MEMORIZATION = 'Y'" + CommConstants.sqlCR);
        sql.append("UNION" + CommConstants.sqlCR);

        sql.append("SELECT 'MYSAMPLE_INSERT'||':'||SENTENCE1||':'||SENTENCE2||':'||TODAY WRITE_DATA " + CommConstants.sqlCR);
        sql.append("  FROM DIC_MY_SAMPLE" + CommConstants.sqlCR);
        sql.append("UNION" + CommConstants.sqlCR);

        sql.append("SELECT 'CLICK_WORD'||':'||ENTRY_ID||':'||INS_DATE WRITE_DATA " + CommConstants.sqlCR);
        sql.append("  FROM DIC_CLICK_WORD" + CommConstants.sqlCR);
        sql.append("UNION" + CommConstants.sqlCR);

        sql.append("SELECT 'BOOKMARK'||':'||KIND||':'||TITLE||':'||URL||':'||INS_DATE WRITE_DATA " + CommConstants.sqlCR);
        sql.append("  FROM DIC_BOOKMARK" + CommConstants.sqlCR);

        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }
}
