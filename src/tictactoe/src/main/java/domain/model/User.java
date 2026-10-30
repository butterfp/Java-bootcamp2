package domain.model;

import java.util.List;
import java.util.UUID;
//объявив Record, мы получаем класс, большую часть кода которого компилятор генерирует сам.
//это специальный вид класса для хранения
//неизменяемых данных: в заголовке перечисляют компоненты,
// а компилятор сам создает private final поля, канонический конструктор,
//методы доступа с именами полей, а также equals(), hashCode() и toString().
public record User(UUID id, String login, String password, List<Role> roles) {}