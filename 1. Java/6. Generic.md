_Источники_
_https://www.youtube.com/watch?v=87pm79sPSvc_
_https://www.youtube.com/watch?v=MniNZsyjH9E&list=PL6jg6AGdCNaX1yIJpX4sgALBTmTVc_uOJ (плейлист)_

## Основы Generics

Дженерики (обобщения) — это особые средства языка Java для реализации обобщённого программирования: особого подхода к описанию данных и алгоритмов, позволяющего работать с различными типами данных без изменения их описания. Появилась фича в Java 5.
Например в ArrayList можно засунуть абсолютно любой тип данных. И хорошо, если вы работаете один и запомните, что положили туда String.
А если нет? Как второй разработчик поймет, что внутри ArrayList?

    ArrayList list = new ArrayList();
    list.add("Привет");
    list.add("Я");
    list.add("Люблю");
    list.add("Java");
    ...
    //Приходит Вася и делает так:
    Integer myInt = (Integer) list.get(0); //Бадум тс, все сломалось

При этом, важно понимать, что ArrayList должен оставаться таким классом, который бы позволял хранить в себе разные типы данных. Не создавать же отдельно ArrayIntegerList, ArrayDoubleList и так далее. Ведь помимо основных типов данных в ArrayList можно хранить и объекты своих классов:

    MyClass myObj = new MyClass();
    list.add(myObj);

А с generics все становится проще и понятнее:

    List<MyClass> myClassList = new ArrayList<MyClass>(); //и здесь и компилятор и разработчик видят, что в конкретно этом листе хранится MyClass
    myClassList.add(myObj);
    ...
    MyClass anotherObj = myClassList.get(0); //Все шикарно

Вот еще пример. Существует класс, который должен хранить объекты разных типов:

    class Box {
      private Object item;
      //get set
    }

Но при использовании?

    Box b = new Box();
    b.setItem(new Integer(5)); //все здорово
    ...
    b.getItem(); //я-то знаю, что там int. А другой человек? Как ему понять?

Получается, нужно делать так:

    Object o = b.getItem();
    o.getClass(); //надо проверять... И сколько же проверок делать? Классов миллионы...

Для этого и были придуманы Generics - ограничить использование типов и внести ясность, какой тип будет получен на выходе.
Видоизмененный класс Box:

    class Box<T> {
        private T item;
        // get set;
    }

И теперь:

    Box<Integer> b = new Box<>(); //даймонды в java 8
    b.setItem(new Integer(5)); //компилятор знает и ожидает Integer
    
    Box<String> b2 = new Box<>();
    b2.setItem("hello"); //компилятор знает и ожидает String
    
**Плюсы:**
* generics позволяют написать класс, который взаимодействует с неизвестным заранее ему типом. При этом, при создании объекта и передачи ему дженерика мы избегаем runtime ошибок, потому как компилятор сразу скажет, что объект типа **A** нельзя запихнуть в объект **new Box<C>**, потому что **ИМЕННО ЭТОТ** объект работает с объектами типа **C**.
* Избегаются дополнительные проверки на получаемый тип объекта.

**Минусы:**
1. С другой стороны, **внутри** того же Box мы не можем сделать так:

    T item = new T();

Почему? Потому что код компилируется до того, как мы определим тип до создания объектов. Мы не знаем ничего о T, что это за объект? А вдруг там нет такого конструктора вообще? Java запрещает так делать. _Внутри класса нельзя создавать объект типа generic_
2. Без использования generics

    Box b;
    Box c;
    c.getClass().equals(b.getClass()); //true

А с generics:

    Box<Integer> b;
    Box<String> c;
    c.getClass().equals(b.getClass()); //false

Вытекающее:

    Box<Number> b1;
    Box<Integer> b2;
    
    sum(Box<Number> b);

    sum(b1) //можно
    sum(b2) //НЕЛЬЗЯ

Вот еще пример:

    List<String> lstr = new ArrayList<>();
    List<Object> lobj = lstr; //Compile error

Если бы не было ошибки компиляции, то было бы возможным следующее:

    lobj.add(new Object());
    String s = lstr.get(0);

Что в принципе лишало бы смысла generic'ов. Нужно запомнить, что **у generic нет наследования в привычном понимании**!

![image](https://github.com/Jahimees/Java-Cheat-Sheet/assets/36009821/199daf50-67c0-4403-9053-ddf0784463a9)

но

![image](https://github.com/Jahimees/Java-Cheat-Sheet/assets/36009821/812acb0b-5239-4ed0-85d7-77e7fe8a0ca2)

Пример

    void print(Collection<String> list) {
      for (String string : list) {
        sout(string);
      }
    }

    List<String> strList = new ArrayList<>();
    List<Object> objList = new ArrayList<>();

    print(strList); //Ok
    print(strList); //compile error

А если мы хотим использовать, чтобы проходила какая-то иерархия, то нам помогут wildcards...

## WildCards

### ? extends Class. Ограничение сверху

Отсюда вытекает частая ошибка. Если мы указываем тип параметра Number: **sum(Number n)**, то мы можем передать **Integer**, но если мы указываем **sum(Box<Number> b)**, то **Box<Integer>** мы передать **НЕ МОЖЕМ**. Для поддержки такого рода наследования у generics используются **wildcards**:

    class Box<T extends Number> {
        ... //все как и было
    }

Данная запись значит:
- Класс Box дженерализируется только типом, который наследуется от Number, **либо является им**

    Box<Integer> b1; //можно
    Box<Float> b2; //можно
    Box<Number> b3; //можно
    Box<String> b4; //ОШИБКА компиляции

Таким образом, мы вносим определенные ограничения в то, какие типы могут быть использованы. И возвращаясь к **sum(Box<*T extends Number> b);** уже можно делать так:

    Box<Number> b1;
    Box<Integer> b2;
    sum(b1) //можно
    sum(b2) //можно

*. На самом деле, может писаться просто <T>, поскольку <T extеnds Number> указывается (зачастую, но НЕ ВСЕГДА)  в определении класса class Box<T extеnds Number>, а дальше просто используется T. хотя бывают случаи, когда в прямо в методе указывается такая длинная запись, но немного иначе:

    public <T extends Number> T getSomeThing(Class<T> type, T obj);

Тут:
* T extends Number - мы объявляем, что будет использоваться тип T, который наследуется от Number
* Далее T - возвращаемый тип
* Class<T> type - мы передаем сам тип, который будет использоваться
* T obj - передаем объект

Понятное дело, что это не эталон, но если мы хотим локально определять тип (на уровне метода), то обязательным условием выступает:
* До типа возвращаемого значения указывать, что мы будем использовать generics: public **<T, K, M, N>** void ...

Но вернемся к примеру с Box. Напомню, что у нас:

        class Box<T extends Number> {
        ... //все как и было
        }

И где-то там мы пишем метод sum:

    Number sum(Box<???> box);

_И здесь вопрос на засыпку: "что писать вместо вопросов?" При том, что мы не хотим делать ту длинную запись, передавать тип и так далее, определять его в сигнатуре._

#### ОФФТОП. Стирание информации в runtime и сырые типы

**Вообще, можно не писать ничего и оставить Number sum(Box box); Поскольку мы знаем, что <T extends Number>. И в действительности даже при создании объекта мы можем сделать так: Box b = new Box();**

**НО** компилятор вас предупредит, что это "сырой тип". Да, компилятор все еще не даст вам сделать дичь (только в данном примере), но вы как разработчик запутаетесь и лучше явно указывать generics.

А вот пример, когда это может привести к Runtime ошибке:

    List rawList = new ArrayList();
    List<String> list = new ArrayList<>();

    rawList = list;
    rawList.add(8);  //Все нормально. НО будет warning. Все будет хорошо, потому что Java стирает информацию о generics после компиляции. Т.е. ее нет в рантайме

    String s = list.get(0); //А вот тут runtime ClassCastException

* Generics позволяют проверять типы ВО ВРЕМЯ КОМПИЛЯЦИИ
* Во время исполнения програмыы информация про типы НЕ ДОСТУПНА (доступ только через reflection)

_Интересное поведение_

    public class SomeType<T> { //да. это не ошибка, не E, а T

        public <E> void test(Collection<E> collection) {
          for (E e : collection) {
            sout(e);
          }
        }

        public void test(List<Integer> integerList) {
          for (Integer integer : integerList) {
            sout(integer);
          }
        }
    }

    ...

    SomeType someType = new SomeType();
    List<String> list = Arrays.asList("value");
    someType.test(list);

**Что случится?**
ClassCastException!! Внезапно? Внезапно!
Поскольку SomeType создан без generic типа. И компилятор далее не дженерализирует весь класс, поэтому считает, что

    public void test(Collection collection);
    public void test(List integerList); //соответственно попадаем сюда, а потом пытаемся int закастить к String и все

Даже несмотря на то, что тип T вообще не важен -> ЕСЛИ ЕСТЬ ДЖЕНЕРИКИ ИХ НАДО ИСПОЛЬЗОВАТЬ

#### ОФФТОП ОКОНЧЕН

И все же. Что писать **Box<???>**

_Еще раз: Мы можем написать Box<T>, но нам необходимо будет этот тип T объявить в сигнатуре методо ДО возвращаемого типа: public <T extеnds Number> void someMethod(Box<T> n)_
Но есть следующая запись:

    Number sum(Box<? extends Number> box);

В данном случае знак вопроса (?) подразумевает **ЛЮБОЙ**.

**В ЧЕМ ОТЛИЧИЕ?**

* В случае с T вы можете работать с этим. Условно T.getClass(). То есть, условно, можно представить ее как переменную, а вернее - как ссылку на класс.
* Если вы ставите "?", то вы уже не сможете так сделать, нет ссылки на класс. Оооочень грубо говоря - у вас анонимная ссылка на класс. Вы используете знак вопроса, когда не собираетесь работать со ссылкой на класс.

Пример:

    1: <T extеnds Number> Number sum1(Box<T extеnds Number> b, Class<T> type); // могу сделать T.getClass() или b instanceof T
    2: Number sum2(Box<? extends Number> b); //не могу
    
* Если вы используете "?" для двух и более параметров, вы не контролируете "одинаковость" их типов, поскольку каждый параметр не зависим и "?" дает волю им быть разным. Если же вы указываете:

    <T> void someMethod(List<T> obj1, List<T> obj2) // ТО ВЫ ГАРАНТИРОВАННО ИМЕЕТЕ 2 ПАРАМЕТРА ОДИНАКОВОГО ТИПА
    void someMethod(List<?> obj1, List<?> obj2) //obj1 и obj2 могут иметь разные типы List, а могут и одинаковые.


**Но по сути, когда например вы используете <? extends Number> в КОНТЕКСТЕ метода sum, то нам плевать, какой там тип в ?. Главное, что этот ? наследуется от Number. Ведь все Number можно суммировать, верно?**

Поэтому, использование того или иного способа зависит от стоящей перед вами задачи.

### ? super Class Ограничение снизу

Также можно встретить такую запись:

    <? super Integer>

Данная запись работает противоположно рассмотренной ранее. В данном случае, можно расшифровать так: требуется тип, который является РОДИТЕЛЕМ для класса Integer.
Для понимания различий я приведу рисунок, как работает ограничения снизу и сверху.

![image](https://github.com/Jahimees/Java-Cheat-Sheet/assets/36009821/e95e6cfd-66da-4215-9341-d83d0bcea4c4)

Для более краткого и быстрого запоминания, можно запомнить, что super - идет по иерархии наверх, а extends - по иерархии вниз:
![image](https://github.com/Jahimees/Java-Cheat-Sheet/assets/36009821/f66a64ea-a888-4e57-aaa1-4a594b5597e7)

~~~~~~~~~~~~~~~~~~~~~~~~~~~

_Можно создать еще более узкое ограничение_

    class Name<T extends A & B & C & D> {
    }

Что означает, что тип T наследует и A и B и C ... НО! Как известно, в java нет множественного наследования. Множественное наследование в java поддерживается только благодаря множественности реализаций интерфейсов. А значит, такая запись предполагает реализацию интерфейсов.

Тут важно запомнить одно правило - первый указанный элемент (A) может быть либо классом, либо интерфейсом, все остальные - строго интерфейсы:
* A - класс или интерфейс
* B C D - только интерфейсы

~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**Когда использовать extends а когда super?**
Частный случай. Например 2 коллекции. Одна входная (IN), другая - выходная (OUT). Для IN - extends, для OUT - super

![image](https://github.com/Jahimees/Java-Cheat-Sheet/assets/36009821/955ad125-24e2-4c04-a928-2bc66d7cb7ab)

Для понимания. Есть иерархия: Продукт. От него наследуются камеры и телефоны. Допустим, на входе у нас коллекция телефонов. И мы же не можем коллекцию телефонов положить в коллекцию камер... Зато мы можем положить в список продуктов или список объектов.

## Рекурсивное расширение типа

Представим

    class Product implements Comparable<Product> {
        //implements compareTo. Сравнение по цене
    }

Да, это сработает и для наследников. Они отлично будут сравниваться по цене. Но что, если захочется сравнить Камеры между собой... По мегапикселям?
Допустим, добавим абстрактный метод subCompare в Product и реализуем его
**ШАГ 1**

    abstract class Product implements Comparable<Product> {
        ...
        abstract boolean subCompare(Product p);
    }

    class Camera extends Product {

      public void subCompare(Product p) {
          if (p instanceof Camera) {
              // мы опять вернулись к instanceof...
          }
      }
    }

Как убрать проверку instanceof и сделать так, чтобы в subCompare передавалась ТОЛЬКО камера?
Ну например, попробуем поменять параметр на Camera в классе Camera:
**ШАГ 2**

    class Camera extends Product {
        void subCompare(Camera p) {
            // вай-вай. ошибка компиляции..  Нельзя так метод переопределять
        }
    }

Тогда слегка изменим наш суперкласс. И напишем, чтобы входной параметр был просто типом T:

    class Product<T> implements Comparable<Product> { //warning на Comparable<Product> - сырой тип. Мы ж теперь используем Product<T> а не просто Product. Скрытый ClassCastException
        abstract void subCompare(T p);
    }

Значит, надо поменять и класс камеры:

    class Camera extends Product<Camera> {
        void subCompare(Camera p) {
         //Ну вроде неплохо
        }
    }

**ШАГ 3**

Надо бы что-то сделать с Warning. Поменяем чуть

    class Product<T> implements Comparable<T> { //warning на Comparable<Product> - сырой тип. Мы ж теперь используем Product<T> а не просто Product. Скрытый ClassCastException
        abstract void subCompare(T p);

          @Override
        public int compareTo(T o) { //Тут тоже меняем
            return this.getPrice() - o.getPrice(); //а откуда мы знаем что у типа T есть getPrice? Получается T extends Product???
        }
    }

**ШАГ 4**

Давайте ограничим ограничим class Product<T extеnds Product>. Мда, ну и запись...

    class Product<T extеnds Product> implements Comparable<T> {// тут опять warning... Product - сырой тип
        abstract void subCompare(T p);

          @Override
        public int compareTo(T o) {...}
    }

Кажется, мы попали в петлю... Удовлетворим хотелки компилятора:

    class Product<T extеnds Product<T>> implements Comparable<T> {// тут опять warning... Product - сырой тип
        abstract void subCompare(T p);

          @Override
        public int compareTo(T o) {...}
    }

Ура! Получилось? Все работает. Проверяем:

    Camera camera1 = new Camera();
    Camera camera2 = new Camera();

    Phone phone1 = new Phone();

    camera1.compareTo(camera2); //ok
    camera1.compareTo(phone1); // Ошибка компиляции! Да, теперь нельзя сравнивать Камеру с телефоном. Можно только телефон-телефон, камера-камера

Для поняности, чисто условно, подставим вместо T например Camera:

    class Product<Camera extends Product<Camera>> implements Comparable<Camera> {}

Сразу все станет понятно, что создавая продукт камеры, мы говорим, что КАМЕРА должна быть наследником ПРОДУКТА с дженериком КАМЕРЫ.
