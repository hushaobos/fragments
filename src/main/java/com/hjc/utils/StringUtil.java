package com.hjc.utils;

import java.util.Objects;

public class StringUtil {
    public static boolean isEmpty(String value)
    {
        return value == null ? true : value.isEmpty();
    }

    //字符串合并
    public static String strAppend(Object... args)
    {
        if(args == null || args.length == 0)//如果参数为空则返回
            return null;

        StringBuilder builder = new StringBuilder();
        for (Object obj :args)
        {
            if(Objects.nonNull(obj))
            builder.append(obj);
        }
        return builder.toString();
    }
}
