package com.sleepingbear.enconversation;

/**
 * Created by Administrator on 2015-11-30.
 */
public class CommConstants {
    public static String appName = "enConversation";
    public static String sqlCR = "\n";
    public static String sentenceSplitStr = "()[]<>\"',.?/= ";
    public static String regex = "/[()[]<>\"',.?/= /]";

    public static int changeKind_title = 0;

    public static int studyKind1 = 0;
    public static int studyKind2 = 1;
    public static int studyKind3 = 2;
    public static int studyKind4 = 3;
    public static int studyKind5 = 4;

    public static String tag = "enConversation";

    public static String infoFileName = "enConversation.txt";
    public static String folderName = "/enconversation";

    public final static int a_news = 1;
    public final static int a_vocabulary = 2;

    public static int f_ConversationStudy = 0;
    public static int f_Pattern = 1;
    public static int f_Conversation = 2;
    public static int f_Note = 3;
    public static int f_Vocabulary = 4;

    //코드 등록
    public static String tag_code_ins = "C_CODE_INS" ;
    public static String tag_code_upd = "C_CODE_UPD" ;
    //public static String tag_code_del = "C_CODE_DEL" ;
    //회화노트 등록
    public static String tag_note_ins = "C_NOTE_INS" ;
    public static String tag_note_del = "C_NOTE_DEL" ;
    public static String tag_note_del_all = "C_NOTE_DEL_ALL" ;
    //단어장 등록
    public static String tag_voc_ins = "C_VOC_INS" ;
    public static String tag_voc_del = "C_VOC_DEL" ;
    public static String tag_voc_del_all = "C_VOC_DEL_ALL" ;
    public static String tag_voc_memory = "C_VOC_MEMORY" ;

    public static String voc_group_code = "VOC" ;
    public static String voc_default_code = "VOC0001" ;
}
