package org.example.filexpressbackend.enums;

public enum TransferMethod {
    CLIENT_TO_CLIENT, // המשתמש שולח ישירות לנמען
    CLIENT_TO_BOTH,   // המשתמש שולח גם לשרת וגם לנמען
    SERVER_RELAY     // המשתמש שולח לשרת, והשרת שולח לנמען
}
