package com.laka.androidlib.util.filter;

import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.Spanned;

/**
 * @Author:summer
 * @Date:2019/10/17
 * @Description:中文6字符，英文12字符
 */
public class SketchLengthFilter implements InputFilter {

    private final int LENGTH_ENGLISH;

    public SketchLengthFilter() {
        this(16);
    }

    public SketchLengthFilter(int english) {
        LENGTH_ENGLISH = english;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int keep = LENGTH_ENGLISH - (calculateLength(dest.toString()) - (dend - dstart));
        end = calculateLength(source.toString());
        CharSequence result = "";
        if (keep >= 0) {
            if (keep >= end - start) {
                result = null; // keep original
            } else {
                result = subSequence(source.toString(), start, start + keep);
            }
        }
        return result;
    }

    private CharSequence subSequence(@NonNull String source, int start, int length) {
        int size = calculateLength(source);
        if (size < length) {
            return source;
        }
        char[] chars = source.toCharArray();
        if (chars.length < length) {
            return source;
        }
        char[] result = new char[length - start];
        System.arraycopy(chars, start, result, 0, length);
        return new String(result);
    }

    private int calculateLength(String string) {
        int length = 0;
        char[] chars = string.toCharArray();
        for (char c : chars) {
            if (isChinese(c)) {
                length += 2;
            } else {
                length++;
            }
        }
        return length;
    }

    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

}
