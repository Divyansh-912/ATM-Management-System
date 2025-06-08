package AtmManagement;
public class ATMMain {
        public static void main(String[] args) {
            ATMService service = new ATMService();
            service.start();
        }
}
try{
    service.withdraw(amount);
} catch (InsufficientFundsException e) {
    System.out.println("Error: " + e.getMessage());
}
    public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String msg) {
        super(msg);
    }
}
public void start() {
    Scanner sc = new Scanner(System.in);
    while (true){
        System.out.println("1. Check Balance\n2. Deposit\n3. Withdraw\n4. Exit");
        int choice = sc.nextInt();
        switch (choice) {
            case 1: checkBalance(); break;
            case 2: deposit(); break;
            case 3: withdraw(); break;
            case 4: return;
            default: System.out.println("Invalid input. Try again.");
        }
    }
}
if (amount <= 0) {
    System.out.println("Amount must be positive.");
    return;
}

