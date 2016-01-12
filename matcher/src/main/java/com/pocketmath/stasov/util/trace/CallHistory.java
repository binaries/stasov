package com.pocketmath.stasov.util.trace;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by etucker on 8/26/15.
 */
public class CallHistory {

   // static class CallData {

   // }

  //  private static CallData traceMethod(Method method) {

    //}
/*
    private static CallData trace() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement ste : stackTrace) {
            final String className = ste.getClassName();
            boolean trace = false;
            try {
                Class clazz = Class.forName(className);
                Annotation[] annos = clazz.getAnnotations();
                for (final Annotation anno : annos) {
                    if (anno instanceof Traceable) {
                        trace = true;
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }*/

    protected final void logNode() {

        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        StackTraceElement ste = null;
        for (StackTraceElement stackTraceElement: stackTrace) {
            final String className = stackTraceElement.getClassName();
            final String methodName = stackTraceElement.getMethodName();

          //  stackTraceElement.

            if (stackTraceElement.getMethodName().endsWith("getStackTrace")
                    ||
                    className.equals(this.getClass().getCanonicalName())
                            &&
                            (methodName.endsWith("logOutputNode")
                                    || methodName.endsWith("logInputNode")
                                    || methodName.endsWith("logNode"))) {

                // do nothing
            } else {
                ste = stackTraceElement;
                break;
            }
        }

        if (ste == null) throw new IllegalStateException();

        final String className = ste.getClassName();
        final String methodName = ste.getMethodName();

       // logRecord.setSourceClassName(className);
       // logRecord.setSourceMethodName(methodName);

       // logger.log(logRecord);
    }
}
