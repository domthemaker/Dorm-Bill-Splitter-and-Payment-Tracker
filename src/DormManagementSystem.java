import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/** Console-based Dorm Bill Splitter and Payment Tracker. */
public class DormManagementSystem {
    private static final Scanner INPUT = new Scanner(System.in);
    
    private static DormData data = FileManager.load();

    // Encapsulation: fields are private and accessed through methods.


    // Abstraction + inheritance: transactions share a common base class.




    public static void run(){
        boolean running=true;
        while(running){
            menu("Dorm Bill Splitter and Payment Tracker",
                "[1] Resident Management", "[2] Slot Management", "[3] Bill Management",
                "[4] Payment Management", "[5] View Slot Balance", "[6] View Resident Status",
                "[7] Generate Reports", "[8] Save Records", "[0] Save and Exit");
            int choice=readInt("Select an option: ");
            switch(choice){
                case 1: residentMenu();break;
                case 2: slotMenu();break;
                case 3: billMenu();break;
                case 4: paymentMenu();break;
                case 5: slotBalance();pause();break;
                case 6: residentStatus();pause();break;
                case 7: reports();pause();break;
                case 8: save();pause();break;
                case 0: save();running=false;break;
                default:message("Invalid selection. Please try again.");pause();
            }
        }
        header("Thank you for using Dorm Bill Splitter!");
    }
    // Consistent, readable console interface (ASCII for terminal compatibility).
    private static final int WIDTH = 78;
    private static void line(){System.out.println("=".repeat(WIDTH));}
    private static void rule(){System.out.println("-".repeat(WIDTH));}
    private static void header(String title){System.out.println();line();System.out.println("  "+title.toUpperCase());line();}
    private static void message(String text){System.out.println("  >> "+text);}
    private static void empty(String item){message("No "+item+" found.");}
    private static void pause(){System.out.print("\nPress ENTER to continue...");INPUT.nextLine();}
    private static void menu(String title,String... options){header(title);for(String option:options)System.out.println("  "+option);rule();}
    private static String shortText(String value,int max){if(value==null)return "-";return value.length()<=max?value:value.substring(0,max-3)+"...";}
    private static void residentTable(){
        header("Resident Directory");
        if(data.residents.isEmpty()){empty("residents");return;}
        System.out.printf("%-12s %-30s %-12s %-10s%n","ID","NAME","SLOT","STATUS");rule();
        for(Resident r:data.residents.values())printResident(r);
        rule();message("Total residents: "+data.residents.size());
    }
    private static void slotTable(){
        header("Dorm Slot Directory");
        if(data.slots.isEmpty()){empty("slots");return;}
        for(Slot slot:data.slots.values()){
            List<Resident> people=residentsInSlot(slot.getId());
            System.out.printf("SLOT %-12s | %d active resident(s)%n",slot.getId(),people.size());
            for(Resident r:people)System.out.printf("    %-12s %s%n",r.getId(),r.getName());
            rule();
        }
    }

    private static String ask(String prompt){System.out.print(prompt);return INPUT.nextLine().trim();}
    private static int readInt(String prompt){
        while(true){try{return Integer.parseInt(ask(prompt));}catch(NumberFormatException e){System.out.println("Enter a whole number.");}}
    }
    private static BigDecimal money(String prompt){
        while(true){try{BigDecimal n=new BigDecimal(ask(prompt)).setScale(2,RoundingMode.HALF_UP);
            if(n.signum()<=0)throw new NumberFormatException();return n;
        }catch(NumberFormatException e){System.out.println("Enter a positive amount (e.g. 250.00).");}}
    }
    private static String required(String prompt){String s;do{s=ask(prompt);if(s.isEmpty())System.out.println("Required field.");}while(s.isEmpty());return s;}
    private static String period(){return required("Billing period (e.g. 2026-11): ");}
    private static String peso(BigDecimal x){return "PHP "+x.setScale(2,RoundingMode.HALF_UP).toPlainString();}
    private static BigDecimal zero(){return BigDecimal.ZERO.setScale(2);}
    private static Resident findResident(){Resident r=data.residents.get(required("Resident ID: "));
        if(r==null)System.out.println("Resident not found.");return r;}
    private static void residentMenu(){
        while(true){
            menu("Resident Management", "[1] Add Resident", "[2] Update Resident",
                "[3] Deactivate Resident", "[4] Search Resident", "[5] View All Residents", "[0] Back to Main Menu");
            int choice=readInt("Select an option: ");if(choice==0)return;
            switch(choice){
                case 1:{header("Add Resident");String id=required("New resident ID: ");
                    if(data.residents.containsKey(id)){message("ID already exists.");break;}
                    data.residents.put(id,new Resident(id,required("Name: ")));message("Resident added successfully.");break;}
                case 2:{header("Update Resident");Resident r=findResident();if(r!=null){r.setName(required("New name: "));message("Resident updated.");}break;}
                case 3:{header("Deactivate Resident");Resident r=findResident();if(r!=null){r.setActive(false);r.setSlotId(null);
                    message("Resident deactivated and unassigned; history retained.");}break;}
                case 4:{header("Search Resident");Resident r=findResident();if(r!=null){
                    System.out.printf("%-12s %-30s %-12s %-10s%n","ID","NAME","SLOT","STATUS");rule();printResident(r);}break;}
                case 5:residentTable();break;
                default:message("Invalid selection.");
            }
            pause();
        }
    }
    private static void printResident(Resident r){
        System.out.printf("%-12s %-30s %-12s %-10s%n",shortText(r.getId(),12),shortText(r.getName(),30),
            shortText(r.getSlotId()==null?"Unassigned":r.getSlotId(),12),r.isActive()?"Active":"Inactive");
    }
    private static void slotMenu(){
        while(true){
            menu("Slot Management", "[1] Add Slot", "[2] Assign Resident", "[3] Unassign Resident",
                "[4] View All Slots", "[0] Back to Main Menu");
            int choice=readInt("Select an option: ");if(choice==0)return;
            switch(choice){
                case 1:{header("Add Slot");String id=required("New slot number: ");
                    if(data.slots.containsKey(id))message("Slot already exists.");
                    else{data.slots.put(id,new Slot(id));message("Slot added successfully.");}break;}
                case 2:{header("Assign Resident");Resident r=findResident();if(r==null)break;
                    if(!r.isActive()){message("Resident is inactive.");break;}
                    String id=required("Assign to slot: ");if(!data.slots.containsKey(id)){message("Slot does not exist.");break;}
                    r.setSlotId(id);message("Resident assigned to slot "+id+".");break;}
                case 3:{header("Unassign Resident");Resident r=findResident();if(r!=null){r.setSlotId(null);message("Resident unassigned.");}break;}
                case 4:slotTable();break;
                default:message("Invalid selection.");
            }
            pause();
        }
    }
    private static List<Resident> residentsInSlot(String slot){List<Resident> list=new ArrayList<>();
        for(Resident r:data.residents.values())if(r.isActive()&&slot.equals(r.getSlotId()))list.add(r);return list;}
    private static Map<String,List<Resident>> occupiedSlots(){Map<String,List<Resident>> map=new LinkedHashMap<>();
        for(Slot s:data.slots.values()){List<Resident> occupants=residentsInSlot(s.getId());if(!occupants.isEmpty())map.put(s.getId(),occupants);}return map;}
    private static void billMenu(){
        while(true){
            menu("Bill Management", "[1] Add Bill", "[2] Update Bill", "[3] Delete Bill",
                "[4] View All Bills", "[0] Back to Main Menu");
            int choice=readInt("Select an option: ");if(choice==0)return;
            switch(choice){
                case 1:header("Add Bill");addBill();break;
                case 2:header("Update Bill");replaceBill();break;
                case 3:header("Delete Bill");deleteBill();break;
                case 4:showBills();break;
                default:message("Invalid selection.");
            }
            pause();
        }
    }
    private static Map<String,BigDecimal> allocate(BigDecimal amount){
        Map<String,List<Resident>> occupied=occupiedSlots();
        if(occupied.isEmpty())return null;
        // Split into integer cents to guarantee the sum of shares equals the bill.
        long cents=amount.movePointRight(2).longValueExact();
        int slots=occupied.size();long base=cents/slots, remainder=cents%slots;
        Map<String,BigDecimal> shares=new LinkedHashMap<>();int slotIndex=0;
        for(List<Resident> residents:occupied.values()){
            long slotCents=base+(slotIndex++<remainder?1:0);
            long each=slotCents/residents.size(),extra=slotCents%residents.size();
            for(int i=0;i<residents.size();i++){
                long individual=each+(i<extra?1:0);
                shares.put(residents.get(i).getId(),BigDecimal.valueOf(individual,2));
            }
        }
        return shares;
    }
    private static void addBill(){String p=period(),type=required("Bill type (Rent/Water/Electricity/Internet/etc.): ");
        BigDecimal amount=money("Total amount: ");Map<String,BigDecimal> shares;
        try{shares=allocate(amount);}catch(ArithmeticException e){System.out.println("Amount is too large.");return;}
        if(shares==null){System.out.println("Assign residents to at least one slot first.");return;}
        Bill b=new Bill("B"+data.nextBill++,p,type,amount,shares);data.bills.add(b);
        header("Bill Allocation - "+b.getId());
        System.out.printf("%-14s %15s%n","RESIDENT ID","SHARE");rule();
        for(Map.Entry<String,BigDecimal> e:shares.entrySet())
            System.out.printf("%-14s %15s%n",e.getKey(),peso(e.getValue()));
        rule();message("Total bill: "+peso(amount));
    }
    private static Bill findBill(String id){for(Bill b:data.bills)if(b.getId().equals(id))return b;return null;}
    private static void replaceBill(){String id=required("Bill ID to update: ");Bill old=findBill(id);
        if(old==null){System.out.println("Bill not found.");return;}
        System.out.println("Updating recalculates shares using CURRENT slot assignments.");
        String type=required("New bill type: ");BigDecimal amount=money("New amount: ");
        Map<String,BigDecimal> shares;
        try{shares=allocate(amount);}catch(ArithmeticException e){System.out.println("Amount is too large.");return;}
        if(shares==null){System.out.println("No occupied slots.");return;}
        data.bills.set(data.bills.indexOf(old),new Bill(id,old.getPeriod(),type,amount,shares));System.out.println("Bill updated.");
    }
    private static void deleteBill(){Bill b=findBill(required("Bill ID to delete: "));
        if(b==null)System.out.println("Bill not found.");else{data.bills.remove(b);System.out.println("Bill deleted.");}}
    private static void showBills(){
        header("Bill Records");if(data.bills.isEmpty()){empty("bills");return;}
        System.out.printf("%-8s %-12s %-30s %16s%n","BILL ID","PERIOD","TYPE","AMOUNT");rule();
        for(Bill b:data.bills)System.out.printf("%-8s %-12s %-30s %16s%n",b.getId(),
            shortText(b.getPeriod(),12),shortText(b.getType(),30),peso(b.getAmount()));
        rule();message("Total bills: "+data.bills.size());
    }
    private static void paymentMenu(){
        while(true){
            menu("Payment Management", "[1] Record Payment", "[2] Delete/Correct Payment",
                "[3] View Payment History", "[0] Back to Main Menu");
            int choice=readInt("Select an option: ");if(choice==0)return;
            switch(choice){
                case 1:{header("Record Payment");Resident r=findResident();if(r==null)break;
                    String p=period();BigDecimal amount=money("Payment amount: ");
                    if(due(r.getId(),p).signum()==0){message("No bill allocation for this resident and period.");break;}
                    LocalDate date;try{date=LocalDate.parse(required("Payment date (YYYY-MM-DD): "));}
                    catch(Exception e){message("Invalid date.");break;}
                    Payment payment=new Payment("P"+data.nextPayment++,p,r.getId(),amount,date);
                    data.payments.add(payment);message("Payment "+payment.getId()+" recorded. Status: "+status(r.getId(),p));break;}
                case 2:{header("Delete / Correct Payment");String id=required("Payment ID to remove (then re-enter corrected payment): ");
                    boolean removed=data.payments.removeIf(p->p.getId().equals(id));message(removed?"Payment removed.":"Payment not found.");break;}
                case 3:{header("Payment History");if(data.payments.isEmpty()){empty("payments");break;}
                    System.out.printf("%-8s %-14s %-12s %16s %-12s%n","PAY ID","RESIDENT ID","PERIOD","AMOUNT","DATE");rule();
                    for(Payment p:data.payments)System.out.printf("%-8s %-14s %-12s %16s %-12s%n",
                        p.getId(),shortText(p.getResidentId(),14),shortText(p.getPeriod(),12),peso(p.getAmount()),p.getDate());
                    rule();message("Total payments: "+data.payments.size());break;}
                default:message("Invalid selection.");
            }
            pause();
        }
    }
    private static BigDecimal due(String id,String period){BigDecimal total=zero();
        for(Bill b:data.bills)if(b.getPeriod().equals(period))total=total.add(b.getShares().getOrDefault(id,zero()));return total;}
    private static BigDecimal paid(String id,String period){BigDecimal total=zero();
        for(Payment p:data.payments)if(p.getResidentId().equals(id)&&p.getPeriod().equals(period))total=total.add(p.getAmount());return total;}
    private static String status(String id,String period){BigDecimal d=due(id,period),p=paid(id,period);
        if(d.signum()==0)return p.signum()>0?"Excess / no bill":"No bill";
        int comparison=p.compareTo(d);return comparison==0?"Paid":comparison>0?"Excess":p.signum()==0?"Unpaid":"Partial";}
    private static void residentStatus(){
        header("Resident Payment Status");Resident r=findResident();if(r==null)return;String p=period();
        BigDecimal d=due(r.getId(),p),pay=paid(r.getId(),p);
        rule();System.out.println("Resident Name : "+r.getName());
        System.out.println("Resident ID   : "+r.getId());
        System.out.println("Billing Period: "+p);rule();
        System.out.println("Total Share   : "+peso(d));
        System.out.println("Amount Paid   : "+peso(pay));
        System.out.println("Balance       : "+peso(d.subtract(pay)));
        System.out.println("Payment Status: "+status(r.getId(),p));rule();
    }
    private static void slotBalance(){
        header("Slot Balance");String slot=required("Slot number: ");String p=period();
        if(!data.slots.containsKey(slot)){message("Slot not found.");return;}
        BigDecimal dueTotal=zero(),paidTotal=zero();
        List<Resident> people=residentsInSlot(slot);
        System.out.println("Slot: "+slot+" | Period: "+p+" | Current occupants: "+people.size());rule();
        if(people.isEmpty()){empty("current occupants");return;}
        System.out.printf("%-24s %15s %15s %-10s%n","RESIDENT","SHARE","PAID","STATUS");rule();
        for(Resident r:people){BigDecimal d=due(r.getId(),p),pay=paid(r.getId(),p);
            dueTotal=dueTotal.add(d);paidTotal=paidTotal.add(pay);
            System.out.printf("%-24s %15s %15s %-10s%n",shortText(r.getName(),24),peso(d),peso(pay),status(r.getId(),p));}
        rule();System.out.println("Total Due     : "+peso(dueTotal));
        System.out.println("Total Paid    : "+peso(paidTotal));
        System.out.println("Total Balance : "+peso(dueTotal.subtract(paidTotal)));rule();
        message("Balances use CURRENT slot occupants; past bill shares remain with original residents.");
    }
    private static void reports(){
        header("Billing Period Report");String p=period();BigDecimal billed=zero(),received=zero();
        for(Bill b:data.bills)if(b.getPeriod().equals(p))billed=billed.add(b.getAmount());
        for(Payment pay:data.payments)if(pay.getPeriod().equals(p))received=received.add(pay.getAmount());
        System.out.println("Billing Period: "+p);rule();
        System.out.println("Total Billed  : "+peso(billed));
        System.out.println("Total Received: "+peso(received));
        System.out.println("Net Balance   : "+peso(billed.subtract(received)));rule();
        System.out.printf("%-12s %-23s %14s %14s %-10s%n","ID","RESIDENT","DUE","PAID","STATUS");rule();
        boolean any=false;
        for(Resident r:data.residents.values())if(due(r.getId(),p).signum()!=0||paid(r.getId(),p).signum()!=0){
            any=true;System.out.printf("%-12s %-23s %14s %14s %-10s%n",shortText(r.getId(),12),
                shortText(r.getName(),23),peso(due(r.getId(),p)),peso(paid(r.getId(),p)),status(r.getId(),p));}
        if(!any)empty("records for this period");
        rule();message("For current-occupant slot balances, select option 5 on the main menu.");
    }
    private static void save(){FileManager.save(data);}
}
