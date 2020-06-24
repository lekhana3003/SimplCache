package com.SimplCache.Utilities;

/**
 * @author Lekhana Ganji
 * @since 24-06-2020
 *
 */
import java.lang.instrument.Instrumentation;

public class SizeOfObject {

        private static Instrumentation instrumentation;

        public static void premain(String args, Instrumentation inst) {
            instrumentation = inst;
        }

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        instrumentation = inst;

    }

        public static Long sizeof(Object o) {
            return instrumentation.getObjectSize(o);
        }

}
