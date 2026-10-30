package domain.model;

import org.springframework.security.core.GrantedAuthority;


//enum - любое перечисление
public enum Role implements GrantedAuthority {
    USER;


    //метод name() от enum -берет имя константы и превращает в строку
    @Override
    public String getAuthority() {
        return name(); // // Метод name() смотрит
        // на имя константы и возвращает строку "USER"
    }
}