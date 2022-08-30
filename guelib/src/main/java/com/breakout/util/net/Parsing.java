package com.breakout.util.net;

import android.content.Context;

import java.io.InputStream;


/**
 * @author sung-gue
 * @version 1.0 (2012. 6. 17.)
 */
@Deprecated
public abstract class Parsing {
    protected final String TAG = getClass().getSimpleName();

    protected Parsing _parsing;

    protected Context _context;

    protected InputStream _responseXml;


    protected Parsing() {
    }


    /**
     * parsing을 위하여 actiontype에따라 분류하여 작업
     *
     * @param responseXml HttpEntity.getContent()
     */
    public abstract void responseParsing(InputStream responseXml) throws Exception;

}