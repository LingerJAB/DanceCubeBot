package com.mirai;

import com.mirai.event.AbstractHandler;
import net.mamoe.mirai.contact.Contact;

public class UserConfigUtils extends AbstractHandler {
    @Deprecated
    public static void cmdsFromFile(Contact contact) {
        System.out.println(contact);
    }
}
