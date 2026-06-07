package com.theo.community_api.common;

public final class ValidationConst {
    public static final String PASSWORD_REGEX =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$";

    public static final String NICKNAME_REGEX =
            "^\\S+$";

    public static final int POST_TITLE_MAX_LENGTH = 26;

    public static final int COMMENT_MAX_LENGTH = 50;

    public static final int REPLY_MAX_LENGTH = 50;
}