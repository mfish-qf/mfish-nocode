package cn.com.mfish.common.swagger.config;

import io.swagger.v3.oas.models.info.Contact;
import lombok.Data;

/**
 * @author: mfish
 * @description: 联系方式
 * @date: 2022/8/18 23:22
 */
@Data
public class MyContact {
    private String name;
    private String url;
    private String email;

    public Contact getContact() {
        Contact contact = new Contact();
        contact.setName(name);
        contact.setUrl(url);
        contact.setEmail(email);
        return contact;
    }
}
