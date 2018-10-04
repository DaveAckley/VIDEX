package com.putable.videx.utils;

import java.lang.reflect.ParameterizedType;

import com.putable.videx.interfaces.OIOAble;

public class ClassUtils {

    public static Class<?> getEffectiveClass(ParameterizedType ptype) {
        System.out.println("GECPT "+ ptype.getRawType());
        throw new UnsupportedOperationException("IMPLEMENTOid");
/*
        if (findInterface(OIOAble.class, type)) {
            type = OIOAble.class;
        }
        return type;
*/
    }

    public static Class<?> getEffectiveClass(Class<?> type) {
        Class<?> in = type;
        if (findInterface(OIOAble.class, type)) {
            type = OIOAble.class;
        }
        //System.out.println("GEC: "+in+" -> "+type);
        return type;
    }

    public static boolean findInterface(Class<?> iface, Class<?> ofclass) {
        Class<?>[] ifaces = ofclass.getInterfaces();
        for (Class<?> i : ifaces) {
            if (i == iface)
                return true;
            if (i.isInterface() && findInterface(iface, i))
                return true;
        }
        Class<?> sup = ofclass.getSuperclass();
        if (sup != null)
            return findInterface(iface, sup);
        return false;
    }

}
