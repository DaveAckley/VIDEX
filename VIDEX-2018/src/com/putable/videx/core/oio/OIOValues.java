package com.putable.videx.core.oio;

import java.util.HashMap;
import java.util.Map;

import com.putable.videx.core.oio.load.ParseException;
import com.putable.videx.core.oio.load.AST.ASTValue;
import com.putable.videx.core.oio.save.OIOLoad;
import com.putable.videx.core.oio.save.OIOSave;
import com.putable.videx.interfaces.OIOAble;
import com.putable.videx.interfaces.OIOAbleGlobalMap;
import com.putable.videx.utils.ClassUtils;

public class OIOValues {
    private Map<String, OIOValue> mOIOValues = new HashMap<String, OIOValue>();

    private OIOSave mSaver = null;
    private OIOLoad mLoader = null;
    
    public OIOValues() {  this(null,null); }
    public OIOValues(OIOLoad l) {  this(null,l); }
    public OIOValues(OIOSave s) {  this(s,null); }

    public OIOValues(OIOSave s, OIOLoad l) {  
        this.setSaver(s);
        this.setLoader(l);
    }
    
    public OIOLoad setLoader(OIOLoad l) {
        OIOLoad old = mLoader;
        mLoader = l;
        return old;
    }

    public OIOSave setSaver(OIOSave s) {
        OIOSave old = mSaver;
        mSaver = s;
        return old;
    }
    
    public OIOValue get(String typename) {
        return mOIOValues.get(typename);
    }

    private void put(String typename, OIOValue oiov) {
        if (mOIOValues.get(typename) != null)
            throw new IllegalStateException("Duplicate typename " + typename);
        mOIOValues.put(typename, oiov);
    }

    public String externalizeValue(Object val, Class<?> fc, OIOAbleGlobalMap map, boolean addrefs)
            throws OIOException {
        if (fc == null)
            fc = ClassUtils.getEffectiveClass(val.getClass());
        if (fc == OIOAble.class && addrefs) {
            mSaver.addOwned((OIOAble) val);
        }
        String typename = fc.getName();
        OIOValue oiv = this.get(typename);
        if (oiv != null) return oiv.externalize(val, map, addrefs);
        throw new OIOException("Unhandled member type: " + fc + " for " + val);
    }

    @SuppressWarnings("rawtypes") // Not sure how to conform stream::iterator stuff without this
    public  String externalizeIterable(Iterable itr, OIOAbleGlobalMap map, boolean owned)
            throws OIOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        String pref = "";
        for (Object o : itr) {
            sb.append(pref);
            pref = " ";
            sb.append(externalizeValue(o, null, map, owned));
        }
        sb.append("]");
        return sb.toString();
    }

    
    public Object getValue(ASTValue val, Class<?> fc, OIOAbleGlobalMap map) {
        return getValue(val, fc.getName(), map);

    }

    public Object getValue(ASTValue val, String typename, OIOAbleGlobalMap map) {
        OIOValue oiov = mOIOValues.get(typename);
        if (oiov == null)         
            throw new ParseException(val.getToken(),"Unhandled member type: '" + typename + "' for " + val);
        if (val.isNull()) {
            if (oiov.isNullOK()) return null;
            throw new ParseException(val.getToken(),"Null illegal in '" + typename + "' for " + val);
        }
        return oiov.internalize(val,map);
    }

    
    /*package access*/ OIOValue add(String typename, OIOValueExternalizer pack,
            OIOValueInternalizer unpack) {
        return add(typename, false, pack, unpack);
    }

    /*package access*/ OIOValue add(String typename, boolean nullOK, OIOValueExternalizer pack,
            OIOValueInternalizer unpack) {
        OIOValue ret = new OIOValue(nullOK, pack, unpack);
        put(typename, ret);
        return ret;
    }

    /*package access*/ OIOValue box(String typename, String unboxedtypename) {
        OIOValue un = get(unboxedtypename);
        if (un == null) throw new IllegalStateException();
        OIOValue ret = new OIOValue(true, un);
        put(typename, ret);
        return ret;
    }
    
    {
        OIOValueProcessors.define(this);
    }
    //
}
