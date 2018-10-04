package com.putable.videx.core.oio.load;

import java.io.IOException;
import java.io.StreamTokenizer;

import com.putable.videx.utils.FileUtils;

public class Token {
    public final String file;
    public final int lineno;
    public final TokType toktype;
    public final String sval;
    public final double nval;
    public final int oval; // Object ref

    public Token(String file, StreamTokenizer st) {
        String fn = file;
        TokType tt = TokType.ERROR;
        int line = -1;
        String sval = null;
        double nval = Double.NaN;
        int oval = -1;
        
        try {
            st.nextToken();
            boolean isTrue;
            if (st.ttype == StreamTokenizer.TT_WORD &&
                    ((isTrue = st.sval.equalsIgnoreCase("true")) ||
                            st.sval.equalsIgnoreCase("false"))) {
                tt = TokType.BOOL;
                nval = isTrue?1:0;
            } else {
                tt = TokType.getTokType(st.ttype);
                if (tt == null) throw new IOException("Unrecognized input '"+(char) st.ttype + "'");
                sval = st.sval;
                nval = st.nval;
            }
        } catch (IOException e) {
            tt = TokType.ERROR;
            sval = ""+e;
        }

        line = st.lineno();

        if (tt == TokType.ONUM) {
            try {
                st.nextToken();
                if (st.ttype != StreamTokenizer.TT_NUMBER)
                    throw new IOException("Not number after '#', found " + st.toString());
                String lex = Integer.toString((int) st.nval);
                Long fromLex = FileUtils.fromLex(lex);
                if (fromLex == null)
                    throw new IOException("Not leximited format #" + lex);
                oval = fromLex.intValue();
            } catch (IOException e) {
                tt = TokType.ERROR;
                sval = ""+e;
            }
        }
        this.file = fn;
        this.lineno = line;
        this.toktype = tt;
        this.sval = sval;
        this.nval = nval;
        this.oval = oval;
    }

    public static String escapeQuotedString(String sval) {
        sval = sval.replaceAll("([\"\\\\])", "\\\\$1");
        sval = sval.replaceAll("\n","\\\\n");          
        sval = sval.replaceAll("\b","\\\\b");          
        sval = sval.replaceAll("\r","\\\\r");          
        sval = sval.replaceAll("\f","\\\\f");          
        sval = sval.replaceAll("\t","\\\\t");          
        sval = sval.replaceAll("\0","\\\\000");     
        sval = '"' + sval + '"';
        return sval;
    }

    @Override
    public String toString() {
        String val = "";
        if (toktype == null) return "??: null";
        switch (toktype) {
        case ID: val = "ID:"+sval; break;
        case NUM: val = "NUM:"+nval; break;
        case EOF: val = "EOF"; break;
        case ONUM: val = "#"+FileUtils.toLex(oval); break;
        case STRING: val = "\"" + sval + "\""; break; // not escaped properly
        case ERROR: val = "ERROR: "+ sval; break;
        default: val = "'" + (char) toktype.tokenType + "'"; break; 
        }
        return file + ":" + lineno + ":" + val;
    }

    public String asCode() {
        String val = "";
        switch (toktype) {
        case ID: val = sval; break;
        case NUM: val = ""+nval; break;
        case EOF: val = ""; break;
        case ONUM: val = "#"+oval; break;
        case STRING: val = escapeQuotedString(sval); break; // XXX not escaped properly
        case ERROR: val = "<<ERROR: "+ sval + ">>"; break;
        default: val = "" + (char) toktype.tokenType; break; 
        }
        return val;
    }
}
