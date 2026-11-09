import java.math.BigDecimal;
import java.util.*;

public class Bill extends Transaction {
    private static final long serialVersionUID = 1L;
    private String type;
    // Sallocations keep old bills correct when occupants later change
    private Map<String,BigDecimal> shares;
    Bill(String id,String period,String type,BigDecimal amount,Map<String,BigDecimal> shares){
        super(id,period,amount);this.type=type;this.shares=new LinkedHashMap<>(shares);
    }
    String getType(){return type;} Map<String,BigDecimal> getShares(){return shares;}
    @Override String description(){return "Bill: "+type;}
}
