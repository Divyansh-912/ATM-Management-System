// ATM Logic in JavaScript (Converted from Java)

class Account {
    constructor(accountNumber, holderName, balance, pin) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
        this.pin = pin;
    }

    deposit(amount) {
        this.balance += amount;
    }

    withdraw(amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }
}

class ATMService {
    constructor() {
        this.accounts = [];
    }

    createAccount(number, name, initialBalance, pin) {
        if (!number || !name || initialBalance < 0 || !/^\d{4}$/.test(pin)) {
            throw new Error("Invalid input or PIN format");
        }

        if (this.findAccount(number)) {
            throw new Error("Account already exists");
        }

        this.accounts.push(new Account(number, name, initialBalance, pin));
    }

    findAccount(number) {
        return this.accounts.find(acc => acc.accountNumber === number);
    }

    deposit(number, amount, pin) {
        const acc = this.findAccount(number);
        if (!acc) {
            console.log("Account not found");
            return;
        }
        if (acc.pin !== pin) {
            console.log("Authentication failed.");
            return;
        }
        acc.deposit(amount);
    }

    withdraw(number, amount, pin) {
        const acc = this.findAccount(number);
        if (!acc) {
            console.log("Account not found");
            return false;
        }
        if (acc.pin !== pin) {
            console.log("Authentication failed.");
            return false;
        }
        const success = acc.withdraw(amount);
        return success;
    }

    displayAccounts() {
        this.accounts.forEach(acc => {
            console.log(`${acc.accountNumber} | ${acc.holderName} | ${acc.balance.toFixed(2)}`);
        });
    }
}

// CLI Simulation in Browser Console
function startATM() {
    const service = new ATMService();
    const readline = require('readline').createInterface({
        input: process.stdin,
        output: process.stdout
    });

    function ask() {
        console.log("\n--- ATM System ---");
        console.log("1. Create Account");
        console.log("2. Deposit");
        console.log("3. Withdraw");
        console.log("4. Show Accounts");
        console.log("5. Exit");

        readline.question("Enter choice: ", choice => {
            switch (choice) {
                case '1':
                    readline.question("Enter account number: ", number => {
                        readline.question("Enter holder name: ", name => {
                            readline.question("Enter initial balance: ", balance => {
                                readline.question("Set 4-digit PIN: ", pin => {
                                    try {
                                        service.createAccount(number, name, parseFloat(balance), pin);
                                        console.log("Account created successfully.");
                                    } catch (e) {
                                        console.log(e.message);
                                    }
                                    ask();
                                });
                            });
                        });
                    });
                    break;
                case '2':
                    readline.question("Enter account number: ", number => {
                        readline.question("Enter deposit amount: ", dep => {
                            readline.question("Enter PIN: ", pin => {
                                try {
                                    service.deposit(number, parseFloat(dep), pin);
                                    console.log("Deposit successful.");
                                } catch (e) {
                                    console.log(e.message);
                                }
                                ask();
                            });
                        });
                    });
                    break;
                case '3':
                    readline.question("Enter account number: ", number => {
                        readline.question("Enter withdrawal amount: ", withAmt => {
                            readline.question("Enter PIN: ", pin => {
                                const success = service.withdraw(number, parseFloat(withAmt), pin);
                                if (!success) {
                                    console.log("Insufficient balance or authentication failed.");
                                } else {
                                    console.log("Withdrawal successful.");
                                }
                                ask();
                            });
                        });
                    });
                    break;
                case '4':
                    service.displayAccounts();
                    ask();
                    break;
                case '5':
                    console.log("Exiting...");
                    readline.close();
                    break;
                default:
                    console.log("Invalid choice.");
                    ask();
            }
        });
    }

    ask();
}

// server.js
const express = require('express');
const app = express();
app.use(express.json());

const ATMService = require('./atmService'); // your ATM logic in a module
const service = new ATMService();

app.post('/createAccount', (req, res) => {
  try {
    const { number, name, balance, pin } = req.body;
    service.createAccount(number, name, balance, pin);
    res.json({ success: true });
  } catch (e) {
    res.status(400).json({ error: e.message });
  }
});

// similarly add /deposit, /withdraw endpoints

app.listen(3000, () => console.log('Server running on http://localhost:3000'));

// Uncomment below line to run in Node.js
// 
startATM();