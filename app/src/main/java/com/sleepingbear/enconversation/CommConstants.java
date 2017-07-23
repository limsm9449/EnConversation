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
    public static int studyKind6 = 5;

    public static String tag = "enConversation";

    public static String infoFileName = "enconversation.txt";
    public static String infoFileNameC01 = "C01.txt";
    public static String infoFileNameC02 = "C02.txt";
    public static String infoFileNameVoc = "VOC.txt";
    public static String folderName = "/enconversation";

    public final static int s_note = 1;
    public final static int s_vocabulary = 2;
    public final static int s_setting = 3;

    public static int f_ConversationStudy = 0;
    public static int f_Pattern = 1;
    public static int f_Conversation = 2;
    public static int f_Note = 3;
    public static int f_Vocabulary = 4;

    //코드 등록
    public static String tag_code_ins = "C_CODE_INS" ;
    //회화노트 등록
    public static String tag_note_ins = "C_NOTE_INS" ;
    //단어장 등록
    public static String tag_voc_ins = "C_VOC_INS" ;

    public static String voc_default_code = "VOC0001" ;

    public static String preferences_font = "key_fontSize";
    public static String preferences_wordView = "key_wordView";


}
