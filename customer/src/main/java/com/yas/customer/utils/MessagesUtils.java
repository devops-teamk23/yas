package com.yas.customer.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public class MessagesUtils {

    private static final ResourceBundle MESSAGE_BUNDLE = ResourceBundle.getBundle("messages.messages", Locale.getDefault());

    private MessagesUtils() {
    }

    public static String getMessage(String errorCode, Object... var2) {
        String message;
        try {
            message = MESSAGE_BUNDLE.getString(errorCode);
        } catch (MissingResourceException ex) {
            // case message_code is not defined.
            message = errorCode;
        }
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(message, var2);
        return formattingTuple.getMessage();
    }
}
