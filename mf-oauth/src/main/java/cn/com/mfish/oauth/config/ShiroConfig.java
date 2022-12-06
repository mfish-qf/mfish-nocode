package cn.com.mfish.oauth.config;

import cn.com.mfish.oauth.cache.redis.RedisCacheManager;
import cn.com.mfish.oauth.cache.redis.RedisSessionDAO;
import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.oauth.credentials.MyHashedCredentialsMatcher;
import cn.com.mfish.oauth.credentials.QRCodeCredentialsMatcher;
import cn.com.mfish.oauth.credentials.SmsCredentialsMatcher;
import cn.com.mfish.oauth.realm.MultipleRealm;
import cn.com.mfish.oauth.realm.PhoneSmsRealm;
import cn.com.mfish.oauth.realm.QRCodeRealm;
import cn.com.mfish.oauth.realm.UserPasswordRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qiufeng
 * @date 2020/2/10 18:05
 */
@Slf4j
@Configuration
public class ShiroConfig {
    @Value("${shiro.security.algorithmName}")
    private String algorithmName = "md5";
    @Value("${shiro.security.hashIterations}")
    private int hashIterations = 2;
    @Value("${shiro.security.hexEncoded}")
    private boolean hexEncoded = true;

    /**
     * 设置shiro拦截器
     *
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //拦截器.
//        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        // 配置不会被拦截的链接 顺序判断
        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
//        filterChainDefinitionMap.put("/logout", "logout");
//        filterChainDefinitionMap.put("/oauth2/**", "anon");
//        filterChainDefinitionMap.put("/sendMsg", "anon");
//        filterChainDefinitionMap.put("/qrCodeLogin/**","anon");
//        filterChainDefinitionMap.put("/css/**", "anon");
//        filterChainDefinitionMap.put("/img/**", "anon");
//        filterChainDefinitionMap.put("/js/**", "anon");
//        filterChainDefinitionMap.put("/fonts/**", "anon");
//        filterChainDefinitionMap.put("/wx/**", "anon");
//        filterChainDefinitionMap.put("/swagger-ui.html/**", "anon");
//        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
//        filterChainDefinitionMap.put("/v2/api-docs/**", "anon");
//        filterChainDefinitionMap.put("/webjars/springfox-swagger-ui/**", "anon");
        //<!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边
        //<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
//        filterChainDefinitionMap.put("/**", "user");
//        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        //未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/404");
        return shiroFilterFactoryBean;
    }

    /**
     * 启动shiro在ioc容器中的注解，只有在使用
     * 开启 Shiro 的注解功能 (如 @RequiresRoles,@RequiresPermissions),
     * 需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证,
     * 需要配置两个bean(DefaultAdvisorAutoProxyCreator(可选) 和 AuthorizationAttributeSourceAdvisor)实现此功能。
     * Spring Boot系列安全框架Apache Shiro基本功能
     *
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    /**
     * AuthorizationAttributeSourceAdvisor，shiro里实现的Advisor类，
     * 内部使用AopAllianceAnnotationsAuthorizingMethodInterceptor来拦截用以下注解的方法。
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * LifecycleBeanPostProcessor，这是个DestructionAwareBeanPostProcessor的子类，
     * 负责org.apache.shiro.util.Initializable类型bean的生命周期的，初始化和销毁。
     * 主要是AuthorizingRealm类的子类，以及EhCacheManager类。
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 凭证匹配器加密规则 采用2次MD5加密
     * 密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     *
     * @return
     */
    @Bean
    public MyHashedCredentialsMatcher myHashedCredentialsMatcher() {
        MyHashedCredentialsMatcher myHashedCredentialsMatcher = new MyHashedCredentialsMatcher();
        myHashedCredentialsMatcher.setHashAlgorithmName(algorithmName);
        myHashedCredentialsMatcher.setHashIterations(hashIterations);
        myHashedCredentialsMatcher.setStoredCredentialsHexEncoded(hexEncoded);
        return myHashedCredentialsMatcher;
    }

    @Bean
    public SmsCredentialsMatcher smsCredentialsMatcher() {
        return new SmsCredentialsMatcher();
    }

    @Bean
    public QRCodeCredentialsMatcher qrCodeCredentialsMatcher() {
        return new QRCodeCredentialsMatcher();
    }

    /**
     * 用户密码登录方式初始化
     *
     * @return
     */
    @Bean
    public UserPasswordRealm userPasswordRealm() {
        UserPasswordRealm userPasswordRealm = new UserPasswordRealm();
        userPasswordRealm.setCredentialsMatcher(myHashedCredentialsMatcher());
        userPasswordRealm.setCachingEnabled(false);
        return userPasswordRealm;
    }

    @Bean
    public PhoneSmsRealm phoneSmsRealm() {
        PhoneSmsRealm phoneSmsRealm = new PhoneSmsRealm();
        phoneSmsRealm.setCredentialsMatcher(smsCredentialsMatcher());
        phoneSmsRealm.setCachingEnabled(false);
        return phoneSmsRealm;
    }

    @Bean
    public QRCodeRealm qrCodeRealm() {
        QRCodeRealm qrCodeRealm = new QRCodeRealm();
        qrCodeRealm.setCredentialsMatcher(qrCodeCredentialsMatcher());
        qrCodeRealm.setCachingEnabled(false);
        return qrCodeRealm;
    }

    @Bean
    public MultipleRealm multipleRealm() {
        MultipleRealm multipleRealm = new MultipleRealm();
        Map<SerConstant.LoginType, AuthorizingRealm> map = new HashMap<>();
        map.put(SerConstant.LoginType.密码登录, userPasswordRealm());
        map.put(SerConstant.LoginType.扫码登录, qrCodeRealm());
        map.put(SerConstant.LoginType.短信登录, phoneSmsRealm());
        multipleRealm.setMyRealms(map);
        return multipleRealm;
    }

    /**
     * session管理设置 采用redis进行session管理
     *
     * @param redisSessionDAO
     * @return
     */
    @Bean(name = "sessionManager")
    public DefaultWebSessionManager sessionManager(RedisSessionDAO redisSessionDAO) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO);
        sessionManager.setGlobalSessionTimeout(21600000);
        return sessionManager;
    }

    @Bean
    public SimpleCookie rememberMeCookie() {
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //如果httyOnly设置为true，则客户端不会暴露给客户端脚本代码，使用HttpOnly cookie有助于减少某些类型的跨站点脚本攻击；
        simpleCookie.setHttpOnly(true);
        //记住我cookie生效时间,默认30天 ,单位秒：60 * 60 * 24 * 30
        simpleCookie.setMaxAge(2592000);
        return simpleCookie;
    }

    /**
     * cookie管理器;
     *
     * @return
     */
    @Bean
    public CookieRememberMeManager cookieRememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        //rememberme cookie加密的密钥 默认AES算法 密钥长度(128 256 512 位)
        //KeyGenerator keygen = KeyGenerator.getInstance("AES");
        //SecretKey deskey = keygen.generateKey();
        //System.out.println(Base64.encodeToString(deskey.getEncoded()));
        byte[] cipherKey = Base64.decode("t5bEITBJmKnOeLlPw1HqtQ==");
        cookieRememberMeManager.setCipherKey(cipherKey);
        cookieRememberMeManager.setCookie(rememberMeCookie());
        return cookieRememberMeManager;
    }

    @Bean
    public SecurityManager securityManager(DefaultWebSessionManager sessionManager, RedisCacheManager redisCacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //自定义多种认证方式
        securityManager.setAuthenticator(multipleRealm());
        //设置session管理方式
        securityManager.setSessionManager(sessionManager);
        securityManager.setCacheManager(redisCacheManager);
        securityManager.setRememberMeManager(cookieRememberMeManager());
        return securityManager;
    }

    public static void main(String[] args) {
        KeyGenerator keygen = null;
        try {
            keygen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey deskey = keygen.generateKey();
        System.out.println(Base64.encodeToString(deskey.getEncoded()));
    }
}

