import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class DormData implements Serializable {
    private static final long serialVersionUID = 1L;
    Map<String,Resident> residents=new LinkedHashMap<>();
    Map<String,Slot> slots=new LinkedHashMap<>();
    List<Bill> bills=new ArrayList<>();
    List<Payment> payments=new ArrayList<>();
    int nextBill=1,nextPayment=1;
}
