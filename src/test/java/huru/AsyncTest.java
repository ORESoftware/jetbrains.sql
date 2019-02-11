package huru;

//import huru.util.Asyncc;
import org.ores.async.Asyncc;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.unit.Async;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ores.async.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static java.util.Arrays.asList;


import java.util.Arrays;
import java.util.Map;

class ZoomCounter {
  
  public Integer val = 0;
  
  public ZoomCounter() {
  
  }
  
  public Integer getVal() {
    return this.val;
  }
  
  public Integer increment() {
    return ++this.val;
  }
}

@RunWith(VertxUnitRunner.class)
public class AsyncTest {
  
  final static Logger log = LoggerFactory.getLogger(AsyncTest.class);
  
  
  @Test
  public void testQueue(TestContext tc) {
    
    Async z = tc.async();

//    Queue q = new Queue<Integer>((task, v) -> {
//      v.run(null, null);
//    });

//    Queue q = new Queue<Integer,Integer>(1, new ITaskHandler<Integer,Integer>() {
//      @Override
//      public void run(Task<Integer,Integer> t, IAsyncErrFirstCb<Integer> v) {
//            v.run(null,5);
//      }
//    });
    
    ZoomCounter c = new ZoomCounter();
    
    var q = new Queue<Integer, Integer>(1, (task, v) -> {
      v.done(null, task.getValue() * 2 + 2);
    });
    
    System.out.println("The concurrency is: " + q.getConcurrency());
    
    q.onSaturated(queue -> {
      System.out.println("saturated");
    });
    
    q.onUnsaturated(queue -> {
      System.out.println("unsaturated");
    });
    
    q.onDrain(queue -> {
      System.out.println("Calling zz complete" + "/" + c.increment() + "/" + queue.getOnDrainCbs().size());
//      z.complete();
    });
    
    q.push(new Queue.Task<>(3, (err, v) -> {
      System.out.println(err);
      System.out.println(v);
    }));
    
    q.push(new Queue.Task<>(5, (err, v) -> {
      log.debug((String)err,v);
    }));
    
    q.push(new Queue.Task<>(5, (err, v) -> {
      System.out.println(err);
      System.out.println(v);
    }));
    
    new Thread(() -> {
      
      try{
        Thread.sleep(100);
      }
      catch(Exception err){
        log.debug(err.toString());
      }
      
      
      q.push(new Queue.Task<>(3, (err, v) -> {
        System.out.println(err);
        System.out.println(v);
      }));
      
      q.onDrain(queue -> {
        System.out.println("Calling zz 2 complete" + "/" + c.increment() + "/" + queue.getOnDrainCbs().size());
        z.complete();
      });
      
      
    }).start();
  }
  
  
  @Test
  public void testMap(TestContext tc) {
    
    Async z = tc.async();
    
    Asyncc.<Integer, Integer, Object>Map(
      
      asList(1, 2, 3),
      
      (kv, cb) -> {
      
        cb.done(null, kv.value + 2);
//        cb.done("foo", kv.value + 2);
      },
      
      (e, results) -> {
        
        System.out.println(results.toString());
        
        if (e != null) {
          throw new Error(e.toString());
        } else {
          z.complete();
        }
        
      });
  }
  
  @Test
  public void testParallel(TestContext tc) {
    
    Async z = tc.async();
    
    Asyncc.Parallel(asList(
      
      v -> {
        
        v.done(null, null);
      }
    
    ), (e, results) -> {
      
      if (e != null) {
        z.complete();
      } else {
        z.complete();
      }
      
    });
  }
  
  @Test
  public void testParallelLimitMap(TestContext tc) {
    
    Async z = tc.async();
    
    Asyncc.<Integer,Object>ParallelLimit(3, Map.of(
      
      "foo",  v -> {
        v.done(null, 2);
      },
      
      "bar",  v -> {
        v.done(null, 3);
      }
    
    ), (e, results) -> {
      
      System.out.println(results.toString());
      
      if (e != null) {
        z.complete();
      } else {
        z.complete();
      }
      
    });
  }
  
  @Test
  public void testParallelMap(TestContext tc) {
    
    Async z = tc.async();
    
    Asyncc.<Integer,Object>Parallel(Map.of(
      
      "foo",  v -> {
        v.done(null, 2);
      },
      
      "bar",  v -> {
        v.done(null, 3);
      }

    ), (e, results) -> {
      
      System.out.println(results.toString());
      
      if (e != null) {
        z.complete();
      } else {
        z.complete();
      }
      
    });
  }
  
  @Test
  public void testParallelLimit(TestContext tc) {
    
    Async z = tc.async();
    
    Asyncc.ParallelLimit(4, asList(
      
      v -> {
        v.done(null, null);
      },
      
      v -> {
        v.done(null, null);
      },
      
      v -> {
        
        new Thread(() -> {
          try {
            Thread.sleep(3);
          } catch (Exception e) {
            System.out.println(e);
          }
          v.done(null, null);
        })
          .start();
      },
      
      v -> {
        new Thread(() -> {
          try {
            Thread.sleep(3);
          } catch (Exception e) {
            System.out.println(e);
          }
          v.done(null, null);
        })
          .start();
      },
      
      v -> {
        v.done(null, null);
      },
      
      v -> {
        v.done(null, null);
      },
      
      v -> {
        new Thread(() -> {
          try {
            Thread.sleep(3);
          } catch (Exception e) {
            System.out.println(e);
          }
          
          v.done(null, null);
        })
          .start();
      },
      
      v -> {
        v.done(null, null);
      }
    
    ), (e, results) -> {
      
      System.out.println("DDDDDDDAMN");
      
      if (e != null) {
        z.complete();
      } else {
        z.complete();
      }
      
    });
  }
  
  @Test
  public void testSeries(TestContext tc) {
    
    Async z = tc.async();
    
    Asyncc.Series(asList(
      
      v -> {
        new Thread(() -> {
          try {
            Thread.sleep(500);
          } catch (Exception e) {
            System.out.println(e);
          }
          
          v.done(null, null);
        })
          .start();
      },
      
      v -> {
        new Thread(() -> {
          try {
            Thread.sleep(500);
          } catch (Exception e) {
            System.out.println(e);
          }
          
          v.done(null, null);
        })
          .start();
      },
      
      v -> {
        new Thread(() -> {
          try {
            Thread.sleep(500);
          } catch (Exception e) {
            System.out.println(e);
          }
          
          v.done(null, null);
        })
          .start();
      }
    
    ), (e, results) -> {
      
      if (e != null) {
        z.complete();
      } else {
        z.complete();
      }
      
    });
  }
  
}
