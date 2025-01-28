package jp.fhub.fhub_feeling.constant;

public class DiaryConstants {
    public static final String DIARY_NOT_FOUND = "対象の日記が存在しません";
    public static final String DIARY_FORBIDDEN = "この日記を表示する権限がありません。";

    private DiaryConstants() {
        throw new UnsupportedOperationException("このクラスは定数クラスであり、インスタンス化できません。");
    }
}
