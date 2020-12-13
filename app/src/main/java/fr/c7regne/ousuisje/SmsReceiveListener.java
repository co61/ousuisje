package fr.c7regne.ousuisje;

public interface SmsReceiveListener {
    void returnSMS(String phoneNumber, String phoneMessage);

}
