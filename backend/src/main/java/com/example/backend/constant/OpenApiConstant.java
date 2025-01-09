package com.example.backend.constant;

public class OpenApiConstant {
    public static final String DEV_SERVER_URL = "http://localhost:8888";
    public static final String DEV_SERVER_DESCRIPTION = "Dev Environment";
    public static final String VERSION = "1.0.0";
    public static final String TITLE = "API document - Internet Banking - SunriseBank";
    public static final String DESCRIPTION =
            "  The API system for the Internet Banking project provides functionalities for customers, employees, and administrators, including login, account management, performing transfers, debt reminders, transaction history, and password changes. The system also offers APIs to integrate with other banks, utilizing asymmetric encryption RSA for secure communication between banks.\n" +
            "\n" +
            "  Customer Module:\n" +
            "  - Login with Google reCAPTCHA.\n" +
            "  - List accounts and account details.\n" +
            "  - Manage recipient list for transfers.\n" +
            "  - Perform internal and interbank transfers.\n" +
            "  - Manage debt reminders, create and pay reminders.\n" +
            "  - View transaction history.\n" +
            "  - Change and recover password via OTP.\n" +
            "\n" +
            "  Employee Module:\n" +
            "  - Create new customer accounts.\n" +
            "  - Deposit money into accounts.\n" +
            "  - View transaction history of customer accounts.\n" +
            "\n" +
            "  Administrator Module:\n" +
            "  - Manage employee list.\n" +
            "  - View transaction statistics with linked banks.\n" +
            "\n" +
            "  Provides secure APIs for linking with other banks, supporting account information queries and deposit operations.\n" +
            "\n" +
            "  The API uses RSA encryption for secure communication between banks, ensuring that only pre-registered banks can perform API transactions with each other.\n" +
            "\n" +
            "  The APIs require high security with OTPs and asymmetric signatures to safeguard critical financial transactions.\n" +
            "\n" +
            "  All account information and transactions are encrypted and secured to ensure integrity and privacy for the users.\n";
}
