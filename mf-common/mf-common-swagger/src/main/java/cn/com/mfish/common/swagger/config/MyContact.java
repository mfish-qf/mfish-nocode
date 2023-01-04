package cn.com.mfish.common.swagger.config;

import lombok.Data;
import springfox.documentation.service.Contact;

/**
 * @author: mfish
 * @description：联系方式
 * @date: 2022/8/18 23:22
 */
@Data
public class MyContact {
    private String name;
    private String url;
    private String email;

    public Contact getContact() {
        return new Contact(name, url, email);
    }
}
