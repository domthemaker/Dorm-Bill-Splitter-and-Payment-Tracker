import java.io.*;
import java.math.BigDecimal;

public abstract class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String period;
    private final BigDecimal amount;
    Transaction(String id,String period,BigDecimal amount){this.id=id;this.period=period;this.amount=amount;}
    String getId(){return id;} String getPeriod(){return period;}
    BigDecimal getAmount(){return amount;}
    abstract String description(); // Polymorphism: each transaction implements this.
}
    