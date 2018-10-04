package com.putable.videx.core.oio.load;

import java.util.TreeMap;

import com.putable.videx.interfaces.OIOAble;
import com.putable.videx.interfaces.OIOAbleGlobalMap;
import com.putable.videx.utils.ClassUtils;

public class GlobalOnumMap implements OIOAbleGlobalMap {
    private enum Mode {
        LOADING, MAKING
    };

    private Mode mMode = null;
    private TreeMap<Integer, OIOAble> mOIOAMap = new TreeMap<Integer, OIOAble>();

    public GlobalOnumMap() {
        startLoading();
    }

    @Override
    public void startLoading() {
        mOIOAMap.clear();
        mMode = Mode.LOADING;
    }

    @Override
    public void endLoading() {
        mMode = Mode.MAKING;
    }

    private int allocateNewOnum() {
        if (this.mOIOAMap.size() == 0) return 1;
        return mOIOAMap.lastKey()+1;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends OIOAble> T newOIOAble(Class<?> classDescribed, Integer onum) {
        if (!ClassUtils.findInterface(OIOAble.class, classDescribed)) 
            return null;
        if (mMode == Mode.LOADING && onum == null)
            throw new IllegalArgumentException("Null onum specified during loading");
        if (mMode == Mode.MAKING && onum != null)
            throw new IllegalArgumentException("Onum value '" + onum + "' specified during making");
        if (onum == null)
            onum = allocateNewOnum();
        try {
            OIOAble oio = (OIOAble) classDescribed.newInstance();
            oio.setOnum(onum);
            this.mOIOAMap.put(onum, oio);
            return (T) oio;
        } catch (Exception e) {
            // Object instantiation failed and we have no way to tell anyone grr
            System.err.println("Object instantiation failed for class "
                    + classDescribed + ": " + e);
        }
        return null;
    }

    @Override
    public OIOAble get(int onum) {
        return this.mOIOAMap.get(onum);
    }

    @Override
    public int getOnum(OIOAble oio) {
        if (oio == null) throw new IllegalArgumentException();
        int onum = oio.getOnum();
        if (onum <= 0) {
            if (this.mMode != Mode.MAKING) throw new IllegalStateException("OY");
            oio.setOnum(onum = allocateNewOnum());
            this.mOIOAMap.put(onum, oio);
        }
        return onum;
    }
}