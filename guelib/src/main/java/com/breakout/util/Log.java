package com.breakout.util;

import android.os.Environment;

import com.breakout.util.constant.CValue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


/**
 * log switch according to {@link Log#DEBUG }
 *
 * @author sung-gue
 * @version 1.0 (2011-10-04)
 */
public final class Log {
    private final static String TAG = "com.breakout.util";

    private final static boolean DEBUG = CValue.DEBUG;
    private final static boolean TRACE_ENABLE = true;

    public static void v(String msg) {
        if (DEBUG) {
            if (TRACE_ENABLE) {
                android.util.Log.v(TAG, msg + info());
            } else {
                android.util.Log.v(TAG, msg);
            }
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            if (TRACE_ENABLE) {
                android.util.Log.v(tag, msg + info());
            } else {
                android.util.Log.v(tag, msg);
            }
        }
    }

    public static void d(String msg) {
        if (DEBUG) {
            if (TRACE_ENABLE) {
                android.util.Log.d(TAG, msg + info());
            } else {
                android.util.Log.d(TAG, msg);
            }
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            if (TRACE_ENABLE) {
                android.util.Log.d(tag, msg + info());
            } else {
                android.util.Log.d(tag, msg);
            }
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            if (TRACE_ENABLE) {
                android.util.Log.i(TAG, msg + info());
            } else {
                android.util.Log.i(TAG, msg);
            }
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            if (TRACE_ENABLE) {
                android.util.Log.i(tag, msg + info());
            } else {
                android.util.Log.i(tag, msg);
            }
        }
    }

    public static void i(boolean alwaysShow, String tag, String msg) {
        if (DEBUG || alwaysShow) {
            if (TRACE_ENABLE) {
                android.util.Log.i(tag, msg + info());
            } else {
                android.util.Log.i(tag, msg);
            }
        }
    }

    public static void w(String msg) {
        if (DEBUG) {
            if (TRACE_ENABLE) {
                android.util.Log.w(TAG, msg + info());
            } else {
                android.util.Log.w(TAG, msg);
            }
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            if (TRACE_ENABLE) {
                android.util.Log.w(tag, msg + info());
            } else {
                android.util.Log.w(tag, msg);
            }
        }
    }

    public static void w(boolean alwaysShow, String tag, String msg) {
        if (DEBUG || alwaysShow) {
            if (TRACE_ENABLE) {
                android.util.Log.w(tag, msg + info());
            } else {
                android.util.Log.w(tag, msg);
            }
        }
    }

    public static void w(String msg, Throwable tr) {
        if (DEBUG) {
            if (TRACE_ENABLE) {
                android.util.Log.w(TAG, msg + info(), tr);
            } else {
                android.util.Log.w(TAG, msg, tr);
            }
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            if (TRACE_ENABLE) {
                android.util.Log.w(tag, msg + info(), tr);
            } else {
                android.util.Log.w(tag, msg, tr);
            }
        }
    }

    public static void w(boolean alwaysShow, String tag, String msg, Throwable tr) {
        if (DEBUG || alwaysShow) {
            if (TRACE_ENABLE) {
                android.util.Log.w(tag, msg + info(), tr);
            } else {
                android.util.Log.w(tag, msg, tr);
            }
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            if (TRACE_ENABLE) {
                android.util.Log.e(TAG, msg + info());
            } else {
                android.util.Log.e(TAG, msg);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            if (TRACE_ENABLE) {
                android.util.Log.e(tag, msg + info());
            } else {
                android.util.Log.e(tag, msg);
            }
        }
    }

    public static void e(String msg, Throwable tr) {
        if (DEBUG) {
            if (TRACE_ENABLE) {
                android.util.Log.e(TAG, msg + info(), tr);
            } else {
                android.util.Log.e(TAG, msg, tr);
            }
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            if (TRACE_ENABLE) {
                android.util.Log.e(tag, msg + info(), tr);
            } else {
                android.util.Log.e(tag, msg, tr);
            }
        }
    }

    public static void e(boolean alwaysShow, String tag, String msg, Throwable tr) {
        if (DEBUG || alwaysShow) {
            if (TRACE_ENABLE) {
                android.util.Log.e(tag, msg + info(), tr);
            } else {
                android.util.Log.e(tag, msg, tr);
            }
        }
    }

    private static String info() {
        int level = 4;
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        return "       " + extract(traces[level].toString()) + " ";
    }

    private static String extract(String str) {
        try {
            int pos1 = str.lastIndexOf("(");
            int pos2 = str.lastIndexOf(")");
            return pos1 > 0 && pos2 > 0 ? str.substring(pos1, pos2 + 1) : str;
        } catch (Exception e) {
            return str;
        }
    }

    public static void write(String filePath, String str) {
        String fullPath = Environment.getExternalStorageDirectory().getPath() + filePath;
        File f = new File(fullPath);

        try {
            if (!f.exists()) {
                f.mkdirs();
                f.createNewFile();
            }

            BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true), "UTF-8"));
            buf.append(str);
            buf.newLine();
            buf.close();
        } catch (Exception e) {
            e(true, TAG, "Log write Exception : " + e.getMessage(), e);
        }

    }

}