package Lab1;
import java.io.*;

public interface ClockService<X,Y> extends Serializable{
	 
	 public X increment();
	 public X getTimeStamp();
	 public int compare(X message1, X message2);
     public void clockSynchronize(Y msg);
     public boolean equal(X message1, X message2);
     public void print();
     public String getTimeStampString(Object timestamp);
}