package com.xhb.scancode.plugin.util;


import java.io.PrintWriter;
import java.io.StringWriter;

public class Log {
    private Log() {
    }

    private static LogImp debugLog = new LogImp() {

        @Override
        public void v(final String tag, final String msg, final Object... obj) {
            String log = obj == null ? msg : String.format(msg, obj);
            System.out.println(String.format("[VERBOSE][%s]%s", tag, log));
        }

        @Override
        public void i(final String tag, final String msg, final Object... obj) {
            String log = obj == null ? msg : String.format(msg, obj);
            System.out.println(String.format("[INFO][%s]%s", tag, log));
        }

        @Override
        public void d(final String tag, final String msg, final Object... obj) {
            String log = obj == null ? msg : String.format(msg, obj);
            System.out.println(String.format("[DEBUG][%s]%s", tag, log));
        }

        @Override
        public void w(final String tag, final String msg, final Object... obj) {
            String log = obj == null ? msg : String.format(msg, obj);
            System.out.println(String.format("[WARN][%s]%s", tag, log));
        }

        @Override
        public void e(final String tag, final String msg, final Object... obj) {
            String log = obj == null ? msg : String.format(msg, obj);
            System.out.println(String.format("[ERROR][%s]%s", tag, log));
        }

        @Override
        public void printErrStackTrace(String tag, Throwable tr, String format, Object... obj) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            tr.printStackTrace(pw);
            log += "  " + sw.toString();
            System.out.println(String.format("[ERROR][%s]%s", tag, log));
        }
    };
    private static LogImp logImp = debugLog;

    public interface LogImp {

        void v(final String tag, final String msg, final Object... obj);

        void i(final String tag, final String msg, final Object... obj);

        void w(final String tag, final String msg, final Object... obj);

        void d(final String tag, final String msg, final Object... obj);

        void e(final String tag, final String msg, final Object... obj);

        void printErrStackTrace(String tag, Throwable tr, final String format, final Object... obj);

    }

    public static void v(final String tag, final String msg, final Object... obj) {
        logImp.v(tag, msg, obj);
    }

    public static void e(final String tag, final String msg, final Object... obj) {
        logImp.e(tag, msg, obj);
    }

    public static void w(final String tag, final String msg, final Object... obj) {
        logImp.w(tag, msg, obj);
    }

    public static void i(final String tag, final String msg, final Object... obj) {
        logImp.i(tag, msg, obj);
    }

    public static void d(final String tag, final String msg, final Object... obj) {
        logImp.d(tag, msg, obj);
    }

    public static void printErrStackTrace(String tag, Throwable tr, final String format, final Object... obj) {
        logImp.printErrStackTrace(tag, tr, format, obj);
    }
}