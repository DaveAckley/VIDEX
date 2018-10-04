package com.putable.videx.spike;

import java.util.HashMap;
import java.util.Map;

public class GrrLambdaSpikeIGuess {
    static class Fields {
        int foo;
        double bar;
        String clams;
        private static Map<String,Setter> mConfigs = new HashMap<String,Setter>();
        {
            mConfigs.put("foo", (fl,n) -> fl.foo = (int) n);
            mConfigs.put("bar", (fl,n) -> fl.bar = (double) n);
            mConfigs.put("clams", (fl,n) -> fl.clams = (String) n);
        }
        void configure(String name, Object value) {
            Setter s = mConfigs.get(name);
            if (s != null) s.set(this, value);
        }
    }
    interface Setter {
        void set(Fields o, Object val);
    }
    public static void main(String[] args) {
        Fields f = new Fields();
        f.configure("foo", 6);
        f.configure("bar", 6.6);
        f.configure("clams", "zofofo");
        f.configure("snorg", null);
        System.out.println(String.format("foo=%d, bar=%f, clams=%s\n", f.foo,f.bar,f.clams));
    }

}
