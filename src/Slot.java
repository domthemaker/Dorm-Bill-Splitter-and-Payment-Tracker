import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class Slot implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    Slot(String id){this.id=id;}
    String getId(){return id;}
}
