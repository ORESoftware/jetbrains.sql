package general;

import huru.entity.*;
import huru.query.*;
import org.junit.Test;

import static huru.entity.Models.*;

import java.util.Arrays;
import java.util.List;

import static huru.query.Condition.*;

public class SQLTest {
  
  @Test
  public void test() {
    
    QueryBuilder<User> qb = new QueryBuilder<User>(User.Model);
    var u = User.Model;
    
    var sql = qb.select()
      .fields(
        u.EMAIL,
        u.HANDLE.as("foo")
      )
      .from(Tables.UserTable)
      .where(
        AND(u.EMAIL.eq("alex@gmail.com"), OR(u.HANDLE.gt(u.HANDLE), u.ID.eq("my id"), u.ID.neq("my id")))
      )
      .getSQL();
    
    System.out.println(sql);
    
  }
  
  @Test
  public void test1() {
    
    QueryBuilder<User> qb = new QueryBuilder<>(User.Model);
    var u = User.Model;
    
    var sql = qb.select()
      .fields(
        u.EMAIL,
        u.HANDLE.as("foo")
      )
      .from(
        Tables.UserTable.fullJoin(
          Tables.KlassTable.leftJoin(Tables.UserTable).on(u.EMAIL.eq(3))
        )
      )
      .where(
        AND(u.EMAIL.eq("alex@gmail.com"), OR(u.HANDLE.gt(u.HANDLE), u.ID.eq("my id"), u.ID.neq("my id"))))
      .getSQL();
    
    System.out.println(sql);
    
  }
  
  @Test
  public void test2() {
    
    QueryBuilder<User> qb = new QueryBuilder<>(User.Model);
    var u = User.Model;
    
    var sql = qb.select()
      .fields(
        u.EMAIL,
        u.HANDLE.as("foo")
      )
      .from(
        Join.Full(
          Tables.UserTable, Tables.KlassTable.leftJoin(Tables.UserTable)
        ).on(u.EMAIL.eq(5))
      )
      .where(
        AND(
          u.EMAIL.eq("alex@gmail.com"),
          OR(u.HANDLE.gt(u.HANDLE), u.ID.eq("my id"), u.ID.neq("my id")
          )
        )
      )
      .getSQL();
    
    System.out.println(sql);
    
  }
  
}
