package jp.fhub.fhub_feeling.constant;

public class UserConstants {
    public static final String USER_NOT_FOUND = "対象のユーザーが存在しません";
    public static final String USER_FORBIDDEN = "このページを表示する権限がありません";

    private UserConstants() {
        throw new UnsupportedOperationException("このクラスは定数クラスであり、インスタンス化できません。");
    }
}
