import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class Payment extends Transaction {
    private static final long serialVersionUID = 1L;
    private final String residentId;
    private final LocalDate date;
    Payment(String id,String period,String residentId,BigDecimal amount,LocalDate date){
        super(id,period,amount);this.residentId=residentId;this.date=date;
    }
    String getResidentId(){return residentId;} LocalDate getDate(){return date;}
    @Override String description(){return "Payment by "+residentId;}
}
