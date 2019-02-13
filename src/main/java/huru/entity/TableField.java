package huru.entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;






public class TableField {
  
  private Set<Class<? extends Annotation>> annots = new HashSet<>();
  private String dbName;
  private String runtimeName;
  
  TableField(String k, String v) {
    
    this.dbName = k;
    this.runtimeName = v;
    
    Field field = null;
    try {
      field = TableField.class.getField(k.toUpperCase());
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    
    Annotation[] annotations = field.getDeclaredAnnotations();
    for(int i = 0; i < annotations.length; i++){
      Annotation a = annotations[i];
      this.annots.add(a.getClass());
    }
  }
  

  public String getDbName() {
    return this.dbName;
  }

  public String getRuntimeName() {
    return this.runtimeName;
  }
  

  public boolean hasAnnotation(Annotation a) {
    return this.annots.contains(a);
  }
  
  public boolean hasAnnotation(Class<? extends Annotation> c){
    return this.annots.contains(c);
  }
}
