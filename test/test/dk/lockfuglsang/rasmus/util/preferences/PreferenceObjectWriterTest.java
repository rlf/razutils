package test.dk.lockfuglsang.rasmus.util.preferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.lockfuglsang.rasmus.util.preferences.GetterIterator;
import dk.lockfuglsang.rasmus.util.preferences.PreferenceObject;
import dk.lockfuglsang.rasmus.util.preferences.PreferenceObjectWriter;
import dk.lockfuglsang.rasmus.util.preferences.impl.PreferenceObjectImpl;
import dk.lockfuglsang.rasmus.util.preferences.impl.PreferenceObjectWriterImpl;
import dk.lockfuglsang.rasmus.util.resources.Resource;

public class PreferenceObjectWriterTest {
  static private final Resource RES = new Resource("PreferenceObjectWriterTest");
  static private final Random RANDOM = new Random(System.currentTimeMillis());
  
  
  static private PreferenceObjectWriter writer;
  
  @BeforeClass
  public static void setUpClass() {
    writer = new PreferenceObjectWriterImpl();
  }
  @Before
  public void setUp() throws BackingStoreException {
    cleanUp();
  }
  @After
  public void cleanUp() throws BackingStoreException {
    Preferences prefs = Preferences.userNodeForPackage(PreferenceObjectWriterImpl.class);
    prefs.removeNode();
    prefs.flush();
  }
  
  @Test
  public void testWrite_SimpleString() throws BackingStoreException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    PreferenceObject<String> po = new PreferenceObjectImpl<String>(RES, "PO", "Hello World");
    String expected = "PO => Hello World\r\n";
    writer.write(po);
    Preferences prefs = Preferences.userNodeForPackage(PreferenceObjectWriterImpl.class);
    assertEquals(expected, getTree(prefs));
  }
  @Test
  public void testWrite_SimpleCollection() throws Exception {
    List<String> list = new ArrayList<String>();
    PreferenceObject<List<String>> po = new PreferenceObjectImpl<List<String>>(RES, "PO", list);
    writer.write(po);
    Preferences prefs = Preferences.userNodeForPackage(PreferenceObjectWriterImpl.class);
    String actual = getTree(prefs);
    String expected = getTree("PO", list);
    assertEquals(expected, actual);
    
    list.add("hi mom");
    list.add("hi dad");
    writer.write(po);
    actual = getTree(prefs);
    expected = getTree("PO", list);
    assertEquals(expected, actual);
    
    list.remove(0);
    list.remove(0);
    list.add("no one knows!");
    writer.write(po);
    actual = getTree(prefs);
    expected = getTree("PO", list);
    assertEquals(expected, actual);
  }
  @SuppressWarnings("unchecked")
  @Test
  public void testWrite_MixedCollection() throws Exception {
    List list = new ArrayList();
    list.add("hello world");
    list.add(new Double(RANDOM.nextDouble()));
    PreferenceObject<Collection> po = new PreferenceObjectImpl<Collection>(RES, "PO", list);
    try {
      writer.write(po);
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      // As expected
    }
  }
  @Test
  public void testWrite_ValueObject() throws Exception {
    PreferenceObject<ValueObjectFlat> po = new PreferenceObjectImpl<ValueObjectFlat>(RES, "PO", new ValueObjectFlat());
    ValueObjectFlat vo = po.getValue();
    writer.write(po);
    Preferences prefs = Preferences.userNodeForPackage(PreferenceObjectWriterImpl.class);
    String actual = getTree(prefs);
    String expected = getTree("PO", vo);
    assertEquals(expected, actual);
  }
  @Test
  public void testWrite_ValueObjectComplex() throws Exception {
    PreferenceObject<ValueObjectComplex> po = new PreferenceObjectImpl<ValueObjectComplex>(RES, "PO", new ValueObjectComplex());
    ValueObjectComplex vo = po.getValue();
    writer.write(po);
    Preferences prefs = Preferences.userNodeForPackage(PreferenceObjectWriterImpl.class);
    String actual = getTree(prefs);
    String expected = getTree("PO", vo);
    assertEquals(expected, actual);
  }
  @Test
  public void testRead_SimpleString() throws Exception {
    PreferenceObject<String> po = new PreferenceObjectImpl<String>(RES, "PO", "Hello World");
    String expected = "Hello World";
    writer.write(po);
    
    String actual = writer.read(String.class, "PO");
    assertEquals(expected, actual);
  }
  @Test
  public void testRead_SimpleInteger() throws Exception {
    Integer io = RANDOM.nextInt();
    PreferenceObject<Integer> po = new PreferenceObjectImpl<Integer>(RES, "PO", io);
    writer.write(po);
    
    Integer expected = io;
    
    Integer actual = writer.read(Integer.class, "PO");
    assertEquals(expected, actual);
  }
  @Test
  public void testRead_SimpleLong() throws Exception {
    Long io = RANDOM.nextLong();
    PreferenceObject<Long> po = new PreferenceObjectImpl<Long>(RES, "PO", io);
    writer.write(po);
    
    Long expected = io;
    
    Long actual = writer.read(Long.class, "PO");
    assertEquals(expected, actual);
  }
  @Test
  public void testRead_SimpleFloat() throws Exception {
    Float io = RANDOM.nextFloat();
    PreferenceObject<Float> po = new PreferenceObjectImpl<Float>(RES, "PO", io);
    writer.write(po);
    
    Float expected = io;
    
    Float actual = writer.read(Float.class, "PO");
    assertEquals(expected, actual);
  }
  @Test
  public void testRead_SimpleDouble() throws Exception {
    Double io = RANDOM.nextDouble();
    PreferenceObject<Double> po = new PreferenceObjectImpl<Double>(RES, "PO", io);
    writer.write(po);
    
    Double expected = io;
    
    Double actual = writer.read(Double.class, "PO");
    assertEquals(expected, actual);
  }
  @Test
  public void testRead_SimpleBoolean() throws Exception {
    Boolean io = RANDOM.nextInt() % 2 == 0;
    PreferenceObject<Boolean> po = new PreferenceObjectImpl<Boolean>(RES, "PO", io);
    writer.write(po);
    
    Boolean expected = io;
    
    Boolean actual = writer.read(Boolean.class, "PO");
    assertEquals(expected, actual);
  }
  @SuppressWarnings("unchecked")
  @Test
  public void testRead_SimpleCollection() throws Exception {
    List<String> list = new ArrayList<String>();
    PreferenceObject<List<String>> po = new PreferenceObjectImpl<List<String>>(RES, "PO", list);
    writer.write(po);
    
    List<String> expected = list;
    List<String> actual = writer.read(ArrayList.class, "PO");
    assertEquals(expected, actual);
    
    list.add("hi mom");
    list.add("hi dad");
    writer.write(po);
    actual = writer.read(ArrayList.class, "PO");
    assertEquals(expected, actual);
    
    list.remove(0);
    list.remove(0);
    list.add("no one knows!");
    writer.write(po);
    actual = writer.read(ArrayList.class, "PO");
    assertEquals(expected, actual);
  }
  @Test
  public void testRead_ValueObject() throws Exception {
    PreferenceObject<ValueObjectFlat> po = new PreferenceObjectImpl<ValueObjectFlat>(RES, "PO", new ValueObjectFlat());
    writer.write(po);
    
    ValueObjectFlat actual = writer.read(ValueObjectFlat.class, "PO");
    ValueObjectFlat expected = po.getValue();
    assertNotSame(expected, actual);
    assertEquals(expected.toString(), actual.toString());
    assertEquals(expected, actual);
  }
  @Test
  public void testRead_ValueObjectComplex() throws Exception {
    PreferenceObject<ValueObjectComplex> po = new PreferenceObjectImpl<ValueObjectComplex>(RES, "PO", new ValueObjectComplex());
    writer.write(po);
    
    ValueObjectComplex expected = po.getValue();
    ValueObjectComplex actual = writer.read(ValueObjectComplex.class, "PO");
    assertNotSame(expected, actual);
    assertEquals(expected.toString(), actual.toString());
    assertEquals(expected, actual);
  }
  
  
  // --------------------------------------------------------------------------
  // Test Helpers
  // --------------------------------------------------------------------------
  private boolean isSimple(Object obj) {
    return (obj instanceof Number || obj instanceof String || obj instanceof Boolean);
  }
  private String getTree(Preferences prefs) throws BackingStoreException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    printTree(ps, prefs);
    ps.flush();
    return baos.toString();
  }
  private String getTree(String key, Object obj) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    printTree(ps, key, obj, "");
    ps.flush();
    return baos.toString();
  }
  private void printTree(PrintStream ps, String key, Object obj, String indent) throws Exception {
    if (obj instanceof Collection<?>) {
      printTree(ps, key, (Collection<?>)obj, indent);
    } else if (isSimple(obj)) {
      ps.println(indent + key + " => " + obj);
    } else {
      ps.println(indent + key + ":");
      indent = indent + "  ";
      for (GetterIterator it = new GetterIterator(obj); it.hasNext();) {
        Method m = it.next();
        Object value = m.invoke(obj);
        String k = GetterIterator.getFieldName(m.getName());
        if (isSimple(value)) {
          ps.println(indent + k + " => " + value);
        } else if (value instanceof Collection<?>) {
          printTree(ps, k, (Collection<?>) value, indent);
        } else {
          printTree(ps, k, value, indent);
        }
      }
    }
  }
  private <T extends Collection<?>> void printTree(PrintStream ps, String k, T obj, String indent) throws Exception {
    Iterator<?> it = obj.iterator();
    ps.println(indent + k + ":");
    Object clObj = null;
    for (int i = 0; i < obj.size(); i++) {
      Object value = it.next();
      if (clObj == null) {
        clObj = value;
        if (!isSimple(clObj)) {
          ps.println(indent + "  CLASS => " + clObj.getClass().getName());
        }
      }
      String key = "" + i;
      printTree(ps, key, value, indent + "  ");
    }
    if (isSimple(clObj)) {
      ps.println(indent + "  CLASS => " + clObj.getClass().getName());
    }
  }
  private void printTree(PrintStream ps, Preferences prefs) throws BackingStoreException {
    printTree(ps, prefs, "");
  }
  private void printTree(PrintStream ps, Preferences prefs, String indent) throws BackingStoreException {
    String[] keys = prefs.keys();
    Arrays.sort(keys);
    for (int i = 0; i < keys.length; i++) {
      ps.println(indent + keys[i] + " => " + prefs.get(keys[i], "<unset>"));
    }
    String[] children = prefs.childrenNames();
    Arrays.sort(children);
    for (int i = 0; i < children.length; i++) {
      ps.println(indent + children[i] + ":");
      printTree(ps, prefs.node(children[i]), indent + "  ");
    }
  }
  static public class ValueObjectFlat {
    private String s;
    private int i;
    private long l;
    private float f;
    private double d;
    private boolean b;
    private boolean flat;
    public ValueObjectFlat() {
      s = "my string";
      i = RANDOM.nextInt();
      l = RANDOM.nextLong();
      f = RANDOM.nextFloat();
      d = RANDOM.nextDouble();
      b = true;
      flat = true;
    }
    public String getS() {
      return s;
    }
    public void setS(String s) {
      this.s = s;
    }
    public int getI() {
      return i;
    }
    public void setI(int i) {
      this.i = i;
    }
    public long getL() {
      return l;
    }
    public void setL(long l) {
      this.l = l;
    }
    public float getF() {
      return f;
    }
    public void setF(float f) {
      this.f = f;
    }
    public double getD() {
      return d;
    }
    public void setD(double d) {
      this.d = d;
    }
    @Override
    public String toString() {
      return this.getClass().getName() + "[" + flat + "," + b + "," + i + "," + l + "," + f + "," + d + "," + s + "]";
    }
    public boolean getB() {
      return b;
    }
    public void setB(boolean b) {
      this.b = b;
    }
    public boolean isFlat() {
      return flat;
    }
    public void setFlat(boolean flat) {
      this.flat = flat;
    }
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (b ? 1231 : 1237);
      long temp;
      temp = Double.doubleToLongBits(d);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + Float.floatToIntBits(f);
      result = prime * result + (flat ? 1231 : 1237);
      result = prime * result + i;
      result = prime * result + (int) (l ^ (l >>> 32));
      result = prime * result + ((s == null) ? 0 : s.hashCode());
      return result;
    }
    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      final ValueObjectFlat other = (ValueObjectFlat) obj;
      if (b != other.b)
        return false;
      if (Double.doubleToLongBits(d) != Double.doubleToLongBits(other.d))
        return false;
      if (Float.floatToIntBits(f) != Float.floatToIntBits(other.f))
        return false;
      if (flat != other.flat)
        return false;
      if (i != other.i)
        return false;
      if (l != other.l)
        return false;
      if (s == null) {
        if (other.s != null)
          return false;
      } else if (!s.equals(other.s))
        return false;
      return true;
    }
  }
  static public class ValueObjectComplex {
    private ValueObjectFlat obj;
    private Set<ValueObjectFlat> objects;
    public ValueObjectComplex() {
      obj = new ValueObjectFlat();
      objects = new HashSet<ValueObjectFlat>();
      objects.add(new ValueObjectFlat());
      objects.add(new ValueObjectFlat());
      objects.add(new ValueObjectFlat());
    }
    public ValueObjectFlat getObj() {
      return obj;
    }
    public void setObj(ValueObjectFlat obj) {
      this.obj = obj;
    }
    public Set<ValueObjectFlat> getObjects() {
      return objects;
    }
    public void setObjects(Set<ValueObjectFlat> objects) {
      this.objects = objects;
    }
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((obj == null) ? 0 : obj.hashCode());
      result = prime * result + ((objects == null) ? 0 : objects.hashCode());
      return result;
    }
    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      final ValueObjectComplex other = (ValueObjectComplex) obj;
      if (this.obj == null) {
        if (other.obj != null)
          return false;
      } else if (!this.obj.equals(other.obj))
        return false;
      if (objects == null) {
        if (other.objects != null)
          return false;
      } else if (!objects.equals(other.objects))
        return false;
      return true;
    }
    @Override
    public String toString() {
      return this.getClass().getName() + "[" + obj + "," + objects + "]";
    }
  }
}
