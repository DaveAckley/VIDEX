package com.putable.videx.core.oio;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.putable.videx.core.Pose;
import com.putable.videx.core.oio.load.ParseException;
import com.putable.videx.core.oio.load.Token;
import com.putable.videx.core.oio.load.AST.ASTValue;
import com.putable.videx.interfaces.OIOAble;
import com.putable.videx.interfaces.Rider;
import com.putable.videx.interfaces.VO;
import com.putable.videx.utils.FileUtils;

public class OIOValueProcessors {
    public static void define(OIOValues o) {
        ///
        // VALUE EXTERNALIZERS AND INTERNALIZERS FOR SELECT TYPES HERE!
        //

        o.add(int.class.getName(), 
                (value, map, add) -> "" + value,
                (av,map) -> av.getAsInt());
        o.box(Integer.class.getName(), int.class.getName());

        o.add(boolean.class.getName(), 
                (value, map, add) -> "" + value,
                (av,map) -> av.getAsBoolean());
        o.box(Boolean.class.getName(), boolean.class.getName());

        o.add(char.class.getName(), 
                (value, map, add) -> "" + (((int) value) & 0xffff),
                (av,map) -> (char) av.getAsInt());
        o.box(Character.class.getName(), char.class.getName());

        o.add(short.class.getName(), 
                (value, map, add) -> "" + value,
                (av,map) -> (short) av.getAsInt());
        o.box(Short.class.getName(), short.class.getName());

        o.add(float.class.getName(), 
                (value, map, add) -> "" + value,
                (av,map) -> (float) av.getAsDouble());
        o.box(Float.class.getName(), float.class.getName());
            
        o.add(double.class.getName(), 
                (value, map, add) -> "" + value,
                (av,map) -> av.getAsDouble());
        o.box(Double.class.getName(), double.class.getName());

        o.add(String.class.getName(), true, 
                (value, map, add) -> Token.escapeQuotedString((String) value),
                (av,map) -> av.getAsString());
            
        o.add(java.nio.file.Path.class.getName(), true, 
                (value, map, add) -> Token.escapeQuotedString(((Path) value).toString()),
                (av,map) -> Paths.get(av.getAsString()));
            
        o.add(OIOAble.class.getName(), true,
                (value, map, add) -> { 
                    OIOAble oio = (OIOAble) value;
                    return "#" + FileUtils.toLex(map.getOnum(oio));
                },
                (av,map) -> { 
                    OIOAble oio = map.get(av.getAsOnum());
                    if (oio == null) 
                        throw new ParseException(av.getToken(), "Undefined ref");
                    return oio;
                });
        
        o.add(Point2D.class.getName(), true,
                (value, map, add) -> 
                    "[" + ((Point2D) value).getX() + 
                    " " + ((Point2D) value).getY() +
                    "]",
                (av, map) -> {
                    List<ASTValue> avs = av.getArrayValue();
                    if (avs==null) throw new IllegalStateException();
                    if (avs.size() != 2) throw new IllegalArgumentException();
                    Point2D ret = 
                            new Point2D.Double(
                                    avs.get(0).getAsDouble(),
                                    avs.get(1).getAsDouble());
                    return ret;
                });

        o.add(Rectangle.class.getName(), true,
                (value, map, add) -> 
                    "[" + ((Rectangle) value).x +
                    " " + ((Rectangle) value).y +
                    " " + ((Rectangle) value).width +
                    " " + ((Rectangle) value).height +
                    "]",
                (av, map) -> {
                    List<ASTValue> avs = av.getArrayValue();
                    if (avs==null) throw new IllegalStateException();
                    if (avs.size() != 4) throw new IllegalArgumentException();
                    Rectangle ret = 
                            new Rectangle(
                                    avs.get(0).getAsInt(),
                                    avs.get(1).getAsInt(),
                                    avs.get(2).getAsInt(),
                                    avs.get(3).getAsInt());
                    return ret;
                });
        o.add(Shape.class.getName(), true,
                (value, map, add) -> {
                    if (!(value instanceof Rectangle)) 
                        throw new IllegalArgumentException("Rectangle only Shape supported");
                    return 
                            "[" + ((Rectangle) value).x +
                            " " + ((Rectangle) value).y +
                            " " + ((Rectangle) value).width +
                            " " + ((Rectangle) value).height +
                            "]";
                },
                (av, map) -> {
                    List<ASTValue> avs = av.getArrayValue();
                    if (avs==null) throw new IllegalStateException();
                    if (avs.size() != 4) throw new IllegalArgumentException();
                    Rectangle ret = 
                            new Rectangle(
                                    avs.get(0).getAsInt(),
                                    avs.get(1).getAsInt(),
                                    avs.get(2).getAsInt(),
                                    avs.get(3).getAsInt());
                    return ret;
                });

        o.add(LinkedList.class.getName(), true,
                (value, map, add) -> o.externalizeIterable((LinkedList<Object>) value, map, add),
                (av, map) -> {
                    List<ASTValue> avs = av.getArrayValue();
                    if (avs==null) throw new IllegalStateException();
                    LinkedList<OIOAble> ret = new LinkedList<OIOAble>();
                    for (ASTValue avi : avs) {
                        int onum = avi.getAsOnum();
                        ret.add(map.get(onum));
                    }
                    return ret;
                });
        o.add(Color.class.getName(),  true,
                (value, map, add) -> {
                    Color c = (Color) value;
                    return 
                            "[" + c.getRed() + 
                            " " + c.getGreen() + 
                            " " + c.getBlue() +
                            ((c.getAlpha()==255)?"":" "+c.getAlpha()) +
                            "]";
                },
                (av, map) -> {
                    List<ASTValue> avs = av.getArrayValue();
                    if (avs==null) throw new IllegalStateException();
                    int len = avs.size(); 
                    if (len < 3 || len > 4) throw new IllegalArgumentException();
                    int r = avs.get(0).getAsInt();
                    int g = avs.get(1).getAsInt();
                    int b = avs.get(2).getAsInt();
                    int a = (len==4) ? avs.get(3).getAsInt() : 255; 
                    return new Color(r,g,b,a);
                });

        o.add(Pose.class.getName(),  true,
                (value, map, add) -> {
                    Pose p = (Pose) value;
                    return String.format("[%f %f %f %f %f %f %f]",
                            p.getPAX(), p.getPAY(), p.getR(),
                            p.getSX(), p.getSY(), p.getOAX(), p.getOAY());
                },
                (av, map) -> {
                    List<ASTValue> avs = av.getArrayValue();
                    if (avs==null) throw new IllegalStateException();
                    int len = avs.size(); 
                    if (len != 7) throw new IllegalArgumentException();
                    Pose p = new Pose();
                    p.setPAX(avs.get(0).getAsDouble());
                    p.setPAY(avs.get(1).getAsDouble());
                    p.setR(  avs.get(2).getAsDouble());
                    p.setSX( avs.get(3).getAsDouble());
                    p.setSY( avs.get(4).getAsDouble());
                    p.setOAX(avs.get(5).getAsDouble());
                    p.setOAY(avs.get(6).getAsDouble());
                    return p;
                });

        o.add("java.util.LinkedList<java.lang.Integer>", true,
                (value, map, add) -> o.externalizeIterable((LinkedList<Object>) value, map, add),
                (av, map) -> {
                    List<ASTValue> avs = av.getArrayValue();
                    if (avs==null) throw new IllegalStateException();
                    LinkedList<Integer> ret = new LinkedList<Integer>();
                    for (ASTValue avi : avs) {
                        int num = avi.getAsInt();
                        ret.add(num);
                    }
                    return ret;
                });
// XXX WHEN AND HOW ARE WE EVER GOING TO DEAL WITH INBOUND
// TYPE NAMES NOT HAVING GENERIC TYPES BUT OUTBOUND NAMES 
// HAVING THEM SO WE ARE CREATING MULTIPLE ENTRIES HERE?        
        o.add(TreeMap.class.getName(), true,
                (value, map, add) -> o.externalizeIterable(((TreeMap<Object,Object>) value).entrySet(), map, add),
                (av, map) -> {
                    List<ASTValue> avs = av.getArrayValue();
                    if (avs==null) throw new IllegalStateException();
                    TreeMap<String,String> ret = new TreeMap<String,String>();
                    for (ASTValue avi : avs) {
                        List<ASTValue> avs2 = avi.getArrayValue();
                        String k = avs2.get(0).getAsString();
                        String v = avs2.get(1).getAsString();
                        ret.put(k, v);
                    }
                    return ret;
                });

        o.add("java.util.TreeMap<java.lang.String, java.lang.String>", true,
                (value, map, add) -> o.externalizeIterable(((TreeMap<Object,Object>) value).entrySet(), map, add),
                (av, map) -> {
                    List<ASTValue> avs = av.getArrayValue();
                    if (avs==null) throw new IllegalStateException();
                    TreeMap<String,String> ret = new TreeMap<String,String>();
                    for (ASTValue avi : avs) {
                        List<ASTValue> avs2 = avi.getArrayValue();
                        String k = avs2.get(0).getAsString();
                        String v = avs2.get(1).getAsString();
                        ret.put(k, v);
                    }
                    return ret;
                });

        o.add("java.util.TreeMap$Entry", true,
                (value, map, add) -> {
                    Map.Entry<Object,Object> kv = (Map.Entry<Object,Object>) value;
                    String k = (String) kv.getKey();
                    String v = (String) kv.getValue();
                    return String.format("[%s %s]", 
                            Token.escapeQuotedString(k),
                            Token.escapeQuotedString(v));
                }, 
                (av, map) -> {
                    List<ASTValue> avs = av.getArrayValue();
                    if (avs==null) throw new IllegalStateException();
                    TreeMap<String,String> ret = new TreeMap<String,String>();
                    for (ASTValue avi : avs) {
                        List<ASTValue> avs2 = avi.getArrayValue();
                        String k = avs2.get(0).getAsString();
                        String v = avs2.get(1).getAsString();
                        ret.put(k, v);
                    }
                    return ret;
                });

        o.add("java.util.LinkedList<com.putable.videx.interfaces.VO>", true,
                (value, map, add) -> o.externalizeIterable((LinkedList<Object>) value, map, add),
                (av, map) -> {
                    List<ASTValue> avs = av.getArrayValue();
                    if (avs==null) throw new IllegalStateException();
                    LinkedList<VO> ret = new LinkedList<VO>();
                    for (ASTValue avi : avs) {
                        int onum = avi.getAsOnum();
                        OIOAble oio = map.get(onum);
                        if (oio == null)
                            throw new ParseException(avi.getToken(), "Undefined onum");
                        if (!(oio instanceof VO)) 
                            throw new ParseException(avi.getToken(), "Not a VO");
                        ret.add((VO) oio);
                    }
                    return ret;
                });

        o.add("java.util.LinkedList<com.putable.videx.interfaces.Rider>", true,
                (value, map, add) -> o.externalizeIterable((LinkedList<Object>) value, map, add),
                (av, map) -> {
                    List<ASTValue> avs = av.getArrayValue();
                    if (avs==null) throw new IllegalStateException();
                    LinkedList<Rider> ret = new LinkedList<Rider>();
                    for (ASTValue avi : avs) {
                        int onum = avi.getAsOnum();
                        OIOAble oio = map.get(onum);
                        if (!(oio instanceof Rider)) 
                            throw new ParseException(avi.getToken(), "Not a Rider");
                        ret.add((Rider) oio);
                    }
                    return ret;
                });

        o.add("java.lang.Class", true,
                (value, map, add) -> '"' + ((Class<?>) value).getName() + '"',
                (av, map) -> {
                    throw new ParseException(av.getToken(), "UNIMPLEMENTED");
                });

        o.add("java.lang.Class<? extends com.putable.videx.interfaces.Rider>", true,
                (value, map, add) -> '"' + ((Class<? extends Rider>) value).getName() + '"',
                (av, map) -> {
                    try {
                        return Class.forName(av.getAsString());
                    } catch (ClassNotFoundException e) {
                        throw new ParseException(av.getToken(), "Not a class name");
                    }
                });
    }
}  


