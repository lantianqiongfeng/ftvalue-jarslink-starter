package com.ftvalue.jarslink.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);


    public static final String DATE_FORMATE_LONG = "yyyyMMddHHmmssSSS";

    public static Date parseDate(String dateStr, String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            return simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            LOGGER.info("formate date error [{}]",e);
        }
        return null;
    }
}
