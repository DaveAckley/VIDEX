package com.putable.videx.core.oio.save;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.putable.videx.core.oio.OIO;
import com.putable.videx.core.oio.OIOException;
import com.putable.videx.core.oio.OIOTop;
import com.putable.videx.core.oio.OIOValue;
import com.putable.videx.core.oio.OIOValueExternalizer;
import com.putable.videx.core.oio.OIOValues;
import com.putable.videx.core.oio.load.Token;
import com.putable.videx.interfaces.OIOAble;
import com.putable.videx.interfaces.OIOAbleGlobalMap;
import com.putable.videx.utils.ClassUtils;
import com.putable.videx.utils.FileUtils;

public class OIOSave {

    private final String mBaseDirectory;
    private final OIOValues mOIOValues = new OIOValues(this);
    private Path mLastSaveDirectory = null;

    /**
     * List of OIOAbles to be written to their own files.
     */
    private LinkedHashSet<OIOAble> mTopsPending = new LinkedHashSet<OIOAble>();
    private OIOAble mCurrentTop = null;
    private Set<OIOAble> mTopsWritten = new LinkedHashSet<OIOAble>();

    private LinkedHashSet<OIOAble> mPending = new LinkedHashSet<OIOAble>();
    private Set<OIOAble> mWritten = new LinkedHashSet<OIOAble>();

    public OIOSave(String baseDir) {
        this.mBaseDirectory = baseDir;
    }

    public Path saveTops(OIOAbleGlobalMap map)
            throws IOException, OIOException {
        this.mLastSaveDirectory = FileUtils
                .createUniqueTimestampSubdir(mBaseDirectory);
        while (mTopsPending.size() > 0) {
            Iterator<OIOAble> itr = mTopsPending.iterator();
            OIOAble top = itr.next();
            itr.remove();
            mCurrentTop = top;
            saveOwned(top, map);
            mTopsWritten.add(top);
            mCurrentTop = null;
        }
        return this.mLastSaveDirectory;
    }

    public void addTop(OIOAble top) {
        if (top == mCurrentTop)
            return;
        if (mTopsPending.contains(top))
            return;
        if (mTopsWritten.contains(top))
            return;
        mTopsPending.add(top);
    }

    public void addOwned(OIOAble ownedRef) {
        // We own it, so go inline unless it's a top but not the current top
        if (ownedRef != mCurrentTop) {
            Class<?> c = ownedRef.getClass();
            if (c.isAnnotationPresent(OIOTop.class)) {
                this.addTop(ownedRef);
                return;
            }
        }

        if (mPending.contains(ownedRef))
            return;
        if (mWritten.contains(ownedRef))
            return;
        mPending.add(ownedRef);
    }

    public boolean written(OIOAble maybeSerialize) {
        return mWritten.contains(maybeSerialize);
    }

    private String getEffectiveName(OIOAble oio) {
        String oioName = null; // XXX oio.getName();
        if (oioName == null) {
            int onum = oio.getOnum();
            if (onum <= 0)
                throw new IllegalStateException("Uninitted oio");
            oioName = FileUtils.toLex(onum);
        }
        return oioName;
    }

    private Path getPathWithExtension(OIOAble oio, String extension) {
        String oioName = getEffectiveName(oio);
        return Paths.get(mLastSaveDirectory.toString(),
                oioName + "." + extension);
    }

    private void writeOwned(OIOAble oio, OIOAbleGlobalMap map)
            throws IOException, OIOException {
        Path mainPath = getPathWithExtension(oio, "oio");
        Writer w = new BufferedWriter(new FileWriter(mainPath.toString()));
        addOwned(oio);
        serializeOwned(w, map);
        w.close();
    }

    private void writeOutOfLineString(OIOAble oio, String extension, String content)
            throws IOException {
        Path mainPath = getPathWithExtension(oio, extension);
        Writer w = new BufferedWriter(new FileWriter(mainPath.toString()));
        w.write(content);
        w.close();
    }

    private void writeOutOfLineBytes(OIOAble oio, String extension, byte[] content)
            throws IOException {
        Path mainPath = getPathWithExtension(oio, extension);
        OutputStream os = new FileOutputStream(mainPath.toString());
        try { 
            os.write(content);
        } 
        finally {
            os.close();
        }
    }

    public void saveOwned(OIOAble oio, OIOAbleGlobalMap map)
            throws IOException, OIOException {
        if (!mPending.isEmpty())
            throw new IllegalStateException();
        mWritten.clear();
        writeOwned(oio, map);
    }

    public void serializeOwned(Writer w, OIOAbleGlobalMap map)
            throws OIOException, IOException {
        while (mPending.size() > 0) {
            // Keep restarting the iterator since serialize may call addPending
            Iterator<OIOAble> it = mPending.iterator();
            OIOAble oio = it.next();
            it.remove();
            serialize(oio, w, map);
            mWritten.add(oio);

        }
    }

    private void serialize(OIOAble object, Writer w, OIOAbleGlobalMap map)
            throws OIOException, IOException {
        if (written(object))
            return;
        try {
            int onum = map.getOnum(object);
            if (onum <= 0)
                throw new IllegalStateException();
            Class<?> startClass = object.getClass();
            Class<?> objectClass = startClass;
            w.write("#" + FileUtils.toLex(onum) + ":" + objectClass.getName()
                    + " {\n");
            while (objectClass != null) {
                boolean first = true;
                for (Field field : objectClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    OIO ann = field.getAnnotation(OIO.class);
                    if (ann != null) {
                        if (ann.obsolete()) continue; // Don't save obsolete fields
                        if (first) {
                            if (objectClass != startClass) {
                                w.write("\n    // "
                                        + objectClass.getSimpleName());
                                w.write("\n");
                            }
                            first = false;
                        }
                        Object val = field.get(object);
                        if (val != null) {
                            if (ann.inline()) {
                                Class<?> fc = ClassUtils
                                        .getEffectiveClass(field.getType());
                                w.write("    " + field.getName() + ": "
                                        + formatVal(val, fc, map, ann.owned())
                                        + "\n");
                            } else {
                                String extension = ann.extension();
                                if (extension.length() == 0)
                                    extension = field.getName();
                                else if (extension.startsWith("."))
                                    extension = extension.substring(1,
                                            extension.length());
                                Class<?> ftype = field.getType();
                                if (ftype == byte[].class) {
                                    writeOutOfLineBytes(object, extension,
                                            (byte[]) val);
                                } else if (ftype == String.class) {
                                    writeOutOfLineString(object, extension,
                                            (String) val);
                                } else
                                    throw new IllegalStateException(
                                            "Unrecognized @OIO(inline=false) member type: "
                                                    + ftype + " for " + field);

                            }
                        }
                    }
                }
                objectClass = objectClass.getSuperclass();
            }
            w.write("}\n\n");
            return;
        } catch (IllegalAccessException e) {
            throw new OIOException(e.getMessage());
        }
    }

    @SuppressWarnings("rawtypes") // Not sure how to conform stream::iterator
                                  // stuff without this
    private String formatIterable(Iterable itr, OIOAbleGlobalMap map,
            boolean owned) throws OIOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        String pref = "";
        for (Object o : itr) {
            sb.append(pref);
            pref = " ";
            sb.append(formatVal(o, null, map, owned));
        }
        sb.append("]");
        return sb.toString();
    }

    private String formatIterable(float[] ary, boolean owned)
            throws OIOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        String pref = "";
        for (float o : ary) {
            sb.append(pref);
            pref = " ";
            sb.append(o);
        }
        sb.append("]");
        return sb.toString();
    }

    private Map<Class<?>, OIOValueExternalizer> mFormatters = new HashMap<Class<?>, OIOValueExternalizer>();
    {
        mFormatters.put(int.class, (value, map, add) -> "" + value);
        mFormatters.put(boolean.class, (value, map, add) -> "" + value);
        mFormatters.put(double.class, (value, map, add) -> "" + value);
        mFormatters.put(String.class,
                (value, map, add) -> value == null ? "null"
                        : Token.escapeQuotedString((String) value));
        mFormatters.put(Double.class,
                (value, map, add) -> value == null ? "null" : "" + value);
        mFormatters.put(Integer.class,
                (value, map, add) -> value == null ? "null" : "" + value);
        mFormatters.put(Point2D.class,
                (value, map, add) -> value == null ? "null"
                        : "[" + ((Point2D) value).getX() + " "
                                + ((Point2D) value).getY() + "]");
        mFormatters.put(Color.class, (value, map, add) -> {
            Color c = (Color) value;
            return "[" + c.getRed() + " " + c.getGreen() + " " + c.getBlue()
                    + ((c.getAlpha() == 255) ? "" : " " + c.getAlpha()) + "]";
        });
        mFormatters.put(java.nio.file.Path.class, 
                (value, map, add) -> value == null ? "null" : ((Path) value).toString());
        mFormatters.put(OIOAble.class, (value, map, add) -> {
            OIOAble oio = (OIOAble) value;
            if (oio == null)
                return "null";
            if (add)
                addOwned(oio);
            return "#" + FileUtils.toLex(map.getOnum(oio));
        });
        mFormatters.put(LinkedList.class, (value, map,
                add) -> formatIterable((LinkedList<Object>) value, map, add));
        mFormatters.put(int[].class, (value, map, add) -> formatIterable(
                Arrays.stream((int[]) value)::iterator, map, add)); // what dah
                                                                    // frock?
        mFormatters.put(double[].class, (value, map, add) -> formatIterable(
                Arrays.stream((double[]) value)::iterator, map, add));
        mFormatters.put(float[].class,
                (value, map, add) -> formatIterable((float[]) value, add));
                  
    }

    private String formatVal(Object val, Class<?> fc, OIOAbleGlobalMap map,
            boolean addrefs) throws OIOException {
        if (fc == null)
            fc = ClassUtils.getEffectiveClass(val.getClass());
        if (fc == OIOAble.class && addrefs) {
            this.addOwned((OIOAble) val);
        }
        String typename = fc.getName();
        OIOValue oiv = mOIOValues.get(typename);
        if (oiv != null)
            return oiv.externalize(val, map, addrefs);
        throw new OIOException("Unhandled member type: " + fc + " (" + typename +") for " + val);
    }

    public void save(OIOAble theOneAndOnlyTop, OIOAbleGlobalMap omap)
            throws IOException, OIOException {
        if (!this.mTopsPending.isEmpty())
            throw new IllegalStateException();
        System.out.println("SAVE "+theOneAndOnlyTop);
        this.addTop(theOneAndOnlyTop);
        this.saveTops(omap);
        System.out.println("SAVED TOPS "+omap);
        Path topFile = Paths.get(this.mLastSaveDirectory.toString(), "0");
        System.out.println("topFile="+topFile);
        IOException e = FileUtils.writeWholeFile(topFile,
                "#" + FileUtils.toLex(theOneAndOnlyTop.getOnum()) + "\n");
        System.out.println("WROTE topFile="+topFile+ " got "+e);

        if (e != null)
            throw e;
    }
}
