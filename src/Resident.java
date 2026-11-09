import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class Resident implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private String name;
    private String slotId;
    private boolean active = true;
    Resident(String id, String name) { this.id=id; this.name=name; }
    String getId(){return id;} String getName(){return name;}
    void setName(String name){this.name=name;}
    String getSlotId(){return slotId;} void setSlotId(String id){slotId=id;}
    boolean isActive(){return active;} void setActive(boolean value){active=value;}
}
