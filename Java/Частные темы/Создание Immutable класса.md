## Неизменяемый класс

Иммутабельный (неизменяемый, immutable) класс — это класс, который после инициализации не может изменить свое состояние. То есть если в коде есть ссылка на экземпляр иммутабельного класса, то любые изменения в нем приводят к созданию нового экземпляра.

* Чтобы класс был иммутабельным, он должен соответствовать следующим требованиям:
* Должен быть объявлен как final, чтобы от него нельзя было наследоваться. Иначе дочерние классы могут нарушить иммутабельность.
* Все поля класса должны быть приватными в соответствии с принципами инкапсуляции.
* Для корректного создания экземпляра в нем должны быть параметризованные конструкторы, через которые осуществляется первоначальная инициализация полей класса.
* Для исключения возможности изменения состояния после инстанцирования, в классе не должно быть сеттеров.
* Для полей-коллекций необходимо делать глубокие копии, чтобы гарантировать их неизменность.
* * Для того, чтобы невозможно было изменять поля через рефлексию - все поля должны быть final.
 
    import java.util.HashMap;
    import java.util.Map;

    public class ImmutableClass {
      private final String field;
      private final Map<String, String> fieldMap;

      public AlmostMutableClass(String field, Map<String, String> fieldMap) {
        this.field = field;      
        Map<String, String> deepCopy = new HashMap<String, String>(); //Создается новый, независимый от внешней ссылки Map.
        for(String key : fieldMap.keySet()) {
          deepCopy.put(key, fieldMap.get(key)); //Перезаписываются все значения сюда. (Если в качестве ключей или значений передаются объекты -> тоже глубокое копирование)
        }
        this.fieldMap = deepCopy;
      }
    
      public String getField() {
        return field;
      }
    
      public Map<String, String> getFieldMap() {
        Map<String, String> deepCopy = new HashMap<String, String>();
        for(String key : fieldMap.keySet()) {
          deepCopy.put(key, fieldMap.get(key));
        }
        return deepCopy;
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        return Collections.unmodifiableMap(fieldMap)
      }
    }
