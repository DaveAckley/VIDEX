package com.putable.videx.core.oio.save;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.putable.videx.core.oio.OIO;
import com.putable.videx.core.oio.OIOException;
import com.putable.videx.core.oio.OIOValue;
import com.putable.videx.core.oio.OIOValueInternalizer;
import com.putable.videx.core.oio.OIOValues;
import com.putable.videx.core.oio.load.Lexer;
import com.putable.videx.core.oio.load.OIOCompiler;
import com.putable.videx.core.oio.load.ParseException;
import com.putable.videx.core.oio.load.TokType;
import com.putable.videx.core.oio.load.Token;
import com.putable.videx.core.oio.load.AST.ASTObj;
import com.putable.videx.core.oio.load.AST.ASTValue;
import com.putable.videx.interfaces.OIOAble;
import com.putable.videx.interfaces.OIOAbleGlobalMap;
import com.putable.videx.utils.ClassUtils;
import com.putable.videx.utils.FileUtils;

public class OIOLoad {

    private final String mBaseDirectory;
    public String getBaseDirectory() {
        return mBaseDirectory;
    }
    private final OIOAbleGlobalMap mOnumMap;
    private final OIOValues mOIOValues = new OIOValues(this);
    private String mLastLoadDirectory = null;
    private FileTime mLastLoadDirectoryModificationTime = null;
    private OIOCompiler mCompiler;

    private int mTopOnum = -1;
    private List<File> mPendingOIOs = new LinkedList<File>();
    private Map<Long, HashMap<String, byte[]>> mOutOfLineContent = new HashMap<Long, HashMap<String, byte[]>>();

    private byte[] getOutOfLineContentIfAnyAsByteArray(int onum,
            String extension) {
        HashMap<String, byte[]> hm = mOutOfLineContent.get(Long.valueOf(onum));
        if (hm == null)
            return null;
        byte[] data = hm.get(extension);
        return data;
    }

    private String getOutOfLineContentIfAny(int onum, String extension) {
        byte[] data = getOutOfLineContentIfAnyAsByteArray(onum, extension);
        if (data == null)
            return null;
        return new String(data, StandardCharsets.UTF_8);
    }

    public OIOAbleGlobalMap getMap() {
        return this.mOnumMap;
    }
    
    public OIOLoad(String baseDir, OIOAbleGlobalMap map) {
        this.mBaseDirectory = baseDir;
        this.mOnumMap = map;
    }

    private void reset() {
        mTopOnum = -1;
        mPendingOIOs.clear();
        mOutOfLineContent.clear();
        mOnumMap.startLoading();
    }

    public OIOAble loadIfNeeded() throws IOException, OIOException {
        Path maxsub = FileUtils.findMaxSubdirNameUnder(Paths.get(mBaseDirectory));
        if (maxsub == null)
            throw new OIOException("No subdirs found under '"+mBaseDirectory+"'");
        String dir = maxsub.toString();
        FileTime mod = FileUtils.getModificationTime(Paths.get(dir));
        if (mLastLoadDirectory == null || !dir.equals(mLastLoadDirectory)
                || mLastLoadDirectoryModificationTime == null
                || mLastLoadDirectoryModificationTime.compareTo(mod) < 0) {
            this.mLastLoadDirectory = dir;
            mLastLoadDirectoryModificationTime = mod;
            return load();
        }
        return null;
    }

    public OIOAble load() throws OIOException {
        reset();

        System.out.println("LOADING DIRECTORY: "+this.mLastLoadDirectory);
        File dir = new File(this.mLastLoadDirectory);
        File[] files = dir.listFiles();
        Arrays.sort(files, null);

        for (final File fileEntry : files) {
            considerFile(fileEntry);
        }

        mCompiler = new OIOCompiler(this.mLastLoadDirectory, this.mOnumMap,
                this);

        for (final File fileEntry : mPendingOIOs) {
            loadOIO(fileEntry);
        }
        mCompiler.configureOIOs();

        if (mTopOnum <= 0)
            throw new OIOException("No top onum; 0 file not found");
        OIOAble ret = this.mOnumMap.get(mTopOnum);
        if (ret == null)
            throw new OIOException("Undefined top onum " + mTopOnum);
        //reset();
        return ret;
    }

    public void configureOIOAble(ASTObj o, OIOAbleGlobalMap map) {
        OIOAble object = o.getOIOAbleInstance();
        try {
            int onum = map.getOnum(object);
            Class<?> objectClass = object.getClass();
            while (objectClass != null) {
                for (Field field : objectClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    OIO ann = field.getAnnotation(OIO.class);
                    if (ann == null)
                        continue;
                    if (ann.value() >= 0) {
                        if (field.getGenericType() == String.class) {
                            String val = null;
                            switch (ann.value()) {
                            case OIO.FILENAME:
                                val = o.getToken().file;
                                break;
                            case OIO.BASE_DIRECTORY:
                                val = mCompiler.getBaseDirectory();
                                break;
                            default:
                                throw new IllegalArgumentException();
                            }
                            field.set(object, val);
                        } else {
                            mCompiler.warn("@OIO(value) field must be String");
                        }
                        continue;
                    }
                    ASTValue av = o.getASTValue(field.getName());
                    if (ann.inline()) {
                        if (av == null) continue;
                        //System.out.println("WANT LOAD TYPE: "
                                //+ field.getType().getCanonicalName());
                        Type gtype = field.getGenericType();
                        //System.out.println("GENERO TYPE: " + gtype);
                        if (gtype instanceof ParameterizedType) {
                            ParameterizedType aType = (ParameterizedType) gtype;
                            //System.out.println("PARMA TYPE: " + aType);
                            Type[] fieldArgTypes = aType
                                    .getActualTypeArguments();
                            for (Type fieldArgType : fieldArgTypes) {
                                if (fieldArgType instanceof WildcardType) {
                                    System.out.println(
                                            "WildCardType fieldArgType = " + fieldArgType);
                                } else {
                                    /*Class<?> fieldArgClass = (Class<?>) fieldArgType;
                                    System.out.println(
                                            "fieldArgClass = " + fieldArgClass);*/
                                }
                            }
                        }
                        Class<?> fc = ClassUtils
                                .getEffectiveClass(field.getType());
                        String effectiveTypeName = gtype.getTypeName();
                        if (!(gtype instanceof ParameterizedType)) {
                            effectiveTypeName = fc.getTypeName();
                        } else {
                            if (gtype instanceof WildcardType) {
                                System.out.println(
                                        "GTYPE CHANEGO? WildCardType fieldArgType = " + gtype);
                            }
                        }
                        Object fval = getValue(av, effectiveTypeName, map);
                        field.set(object, fval);
                    } else {
                        String extension = ann.extension();
                        if (extension.length() == 0)
                            extension = field.getName();
                        else if (extension.startsWith("."))
                            extension = extension.substring(1,
                                    extension.length());
                        //System.out.println("FIELDKK " + field );
                        //System.out.println("FIELDTTKK " + field.getGenericType());
                        //System.out.println("FIEDODOLDTTKK " + byte[].class);
                        Class<?> ftype = field.getType();
                        if (ftype == byte[].class) {
                            byte[] content = this.getOutOfLineContentIfAnyAsByteArray(onum, extension);
                            if (content != null) {
                                field.set(object, content);
                            }
                        } else if (ftype == String.class) {
                            String content = this.getOutOfLineContentIfAny(onum,
                                    extension);
                            if (content != null) {
                                field.set(object, content);
                            }
                        } else 
                            throw new ParseException(o.getToken(), "OIO(inline=false) only legal on String or byte[] members, not "+field);
                    }
                }
                objectClass = objectClass.getSuperclass();
            }
            return;
        } catch (IllegalAccessException e) {
            throw new ParseException(o.getToken(), e.getMessage());
        }
    }

    private void loadOIO(File fileEntry) {
        mCompiler.loadFile(fileEntry);
    }

    private String[] decomposeFileName(String fname) {
        String[] pieces = fname.split("[.]");
        if (pieces.length == 0 || pieces.length > 2) {
            System.err.println("SKIPPING WEIRD FILE '" + fname + "'");
            return null;
        } else if (pieces.length == 1)
            return new String[] { pieces[0], "" };
        return pieces;
    }

    private void processTop(Path file) {
        String data = FileUtils.readWholeFile(file);
        StringReader sr = new StringReader(data);
        Lexer lex = new Lexer("0", sr);
        Token tok = lex.nextToken();
        if (tok.toktype != TokType.ONUM)
            throw new ParseException(tok, "Expected top onum");
        int onum = tok.oval;
        if (this.mTopOnum > 0)
            throw new ParseException(tok, "Duplicate top onum, vs " + mTopOnum);
        this.mTopOnum = onum;
    }

    private void considerFile(File file) throws OIOException {
        if (file.isDirectory())
            return;
        String fname = file.getName();
        if (fname.equals("0")) {
            processTop(file.toPath());
            return;
        }
        String[] pieces = decomposeFileName(fname);
        if (pieces == null)
            return;
        String base = pieces[0], extension = pieces[1];
        Long lonum = FileUtils.fromLex(base);
        if (lonum == null)
            System.err.println("SKIPPING NON-LEX FILE '" + fname + "'");
        if (extension.equals("oio"))
            this.mPendingOIOs.add(file);
        else {
            byte[] data = FileUtils.readWholeFileAsByteArray(file.toPath());
            HashMap<String, byte[]> extdat = this.mOutOfLineContent.get(lonum);
            if (extdat == null) {
                extdat = new HashMap<String, byte[]>();
                this.mOutOfLineContent.put(lonum, extdat);
            }
            extdat.put(extension, data);
        }
    }

    private Map<String, OIOValueInternalizer> mUnpackers = new HashMap<String, OIOValueInternalizer>();
    {
        mUnpackers.put(int.class.getName(), (av, map) -> av.getAsInt());
        mUnpackers.put(boolean.class.getName(), (av, map) -> av.getAsBoolean());
        mUnpackers.put(double.class.getName(), (av, map) -> av.getAsDouble());
        mUnpackers.put(String.class.getName(), (av, map) -> av.getAsString());
        mUnpackers.put(Double.class.getName(),
                (av, map) -> av == null ? null : av.getAsDouble());
        mUnpackers.put(Integer.class.getName(),
                (av, map) -> av == null ? null : av.getAsInt());
        mUnpackers.put(OIOAble.class.getName(), (av, map) -> {
            int onum = av.getAsOnum();
            OIOAble oio = map.get(onum);
            if (oio == null)
                throw new ParseException(av.getToken(), "Undefined ref");
            return oio;
        });

        mUnpackers.put("java.awt.geom.Point2D", (av, map) -> {
            List<ASTValue> avs = av.getArrayValue();
            if (avs == null)
                throw new IllegalStateException();
            if (avs.size() != 2)
                throw new IllegalArgumentException();
            Point2D ret = new Point2D.Double(avs.get(0).getAsDouble(),
                    avs.get(1).getAsDouble());
            return ret;
        });

        mUnpackers.put("java.util.LinkedList<java.lang.Integer>", (av, map) -> {
            List<ASTValue> avs = av.getArrayValue();
            if (avs == null)
                throw new IllegalStateException();
            LinkedList<Integer> ret = new LinkedList<Integer>();
            for (ASTValue avi : avs) {
                ret.add(avi.getAsInt());
            }
            return ret;
        });
        mUnpackers.put("float[]", (av, map) -> {
            List<ASTValue> avs = av.getArrayValue();
            if (avs == null)
                throw new IllegalStateException();
            int len = avs.size();
            float[] fs = new float[len];
            int i = 0;
            for (ASTValue avi : avs) {
                fs[i++] = (float) avi.getAsDouble();
            }
            return fs;
        });
        mUnpackers.put("int[]", (av, map) -> {
            List<ASTValue> avs = av.getArrayValue();
            if (avs == null)
                throw new IllegalStateException();
            int len = avs.size();
            int[] fs = new int[len];
            int i = 0;
            for (ASTValue avi : avs) {
                fs[i++] = avi.getAsInt();
            }
            return fs;
        });

        /*
         * mFormatters.put(List.class, (value, add) ->
         * formatIterable((List<Object>) value, add));
         */
        /*
         * No such thing as FloatStream apparently thank you very much for the
         * completeness mFormatters.put(float[].class, (value, add) ->
         * formatIterable(Arrays.stream((float[]) value)::iterator, add));
         */
        /*
         * mFormatters.put(double[].class, (value, add) ->
         * formatIterable(Arrays.stream((double[]) value)::iterator, add));
         */
        /*
         * No such thing as BooleantStream apparently thank you very much for
         * the completeness mFormatters.put(boolean[].class, (value, add) ->
         * formatIterable(Arrays.stream((boolean[]) value)::iterator, add));
         */
    }

    private Object getValue(ASTValue val, Class<?> fc, OIOAbleGlobalMap map) {
        return getValue(val, fc.getName(), map);

    }

    private Object getValue(ASTValue val, String typename,
            OIOAbleGlobalMap map) {
        OIOValue oiov = mOIOValues.get(typename);
        if (oiov == null)
            throw new ParseException(val.getToken(),
                    "Unhandled member type: '" + typename + "' for " + val);
        if (val.isNull()) {
            if (oiov.isNullOK())
                return null;
            throw new ParseException(val.getToken(),
                    "Null illegal in '" + typename + "' for " + val);
        }
        return oiov.internalize(val, map);
    }

    /*
     * private Class<?> getEffectiveClass(Class<?> type) { if
     * (findInterface(OIOAble.class, type)) { type = OIOAble.class; } return
     * type; }
     * 
     * 
     * private String formatVal(Object val, Class<?> fc, boolean addrefs) throws
     * OIOException { if (fc == null) fc = getEffectiveClass(val.getClass()); if
     * (mFormatters.containsKey(fc)) return mFormatters.get(fc).format(val,
     * addrefs); throw new OIOException("Unhandled member type: " + fc + " for "
     * + val); }
     */
}
