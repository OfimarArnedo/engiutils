package engiutils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class EngiCachingDelegatedMap implements Map {
  private WeakHashMap cache = new WeakHashMap();
  
  public void clear() {
    throw new IllegalAccessError("Read-only store!");
  }
  
  public boolean containsValue(Object value) {
    throw new UnsupportedOperationException();
  }
  
  public Set entrySet() {
    throw new UnsupportedOperationException();
  }
  
  public Object get(Object key) {
    Object value = this.cache.get(key);
    if (value == null) {
      value = read(key);
      this.cache.put(key, value);
    } 
    return value;
  }
  
  public Object put(Object key, Object value) {
    throw new IllegalAccessError("Read-only store!");
  }
  
  public void putAll(Map t) {
    throw new IllegalAccessError("Read-only store!");
  }
  
  public abstract Object read(Object paramObject);
  
  public Object remove(Object key) {
    throw new IllegalAccessError("Read-only store!");
  }
  
  public abstract int size();
  
  public Collection values() {
    throw new UnsupportedOperationException();
  }
}
