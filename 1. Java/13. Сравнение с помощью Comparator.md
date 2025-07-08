_За основу взято: https://javarush.com/groups/posts/2262-comparator-v-java_

## Comparable, Comparator

### Comparable

Для чего используется: для сравнивания объектов между собой, чтобы выявить, кто больше, лучше, выше, сильнее.

Если собираемся сравнивать объекты, класс должен реализовать интерфейс Comparable<T>:

      public class Car implements Comparable<Car> {
      ...
          @Override
          public int compareTo(Car o) {
            return this.getManufactureYear() - o.getManufactureYear();
          }
      }

* _Если возвращаемое значение > 0, значит a > b._
* _Если результат compareTo < 0, значит а < b._ 
* _Ну а если результат == 0, значит два объекта равны: a == b._

#### В каких случаях надо использовать Comparable?

Реализованный в Comparable метод сравнения называют «**natural ordering**» — естественной сортировкой. Это потому, что в методе compareTo() ты описываешь наиболее распространенный способ сравнения, который будет использоваться для объектов этого класса в твоей программе.

**Natural Ordering** уже присутствует в Java. Например, Java знает, что строки чаще всего сортируют по алфавиту, а числа — по возрастанию их значения. Поэтому если вызвать на списке чисел или строк метод sort(), так они и будут отсортированы.

Если в нашей программе машины в большинстве случаев будут сравниваться и сортироваться по году выпуска, значит, стоит определить для них натуральную сортировку с помощью интерфейса Comparable<Car> и метода compareTo().

**Но что, если нам этого недостаточно?**

Но иногда среди наших клиентов попадаются любители быстрой езды. Если мы готовим для них каталог автомобилей на выбор, их нужно _упорядочить по максимальной скорости_.

### Comparator
Здесь нам приходит на помощь другой интерфейс — **Comparator**. Так же, как и Comparable, он типизированный.
А в чем же разница?

_Comparable_ делает наши объекты «_сравнимыми_» и создает для них наиболее естественный порядок сортировки, который будет использоваться в большинстве случаев.

_Comparator_ — это отдельный класс-«_сравниватель_».

Если нам нужно реализовать какую-то специфическую сортировку, нам необязательно лезть в класс Car и менять логику compareTo().

Вместо этого мы можем создать отдельный класс-comparator в нашей программе и научить его делать нужную нам сортировку. ОБРАЩАЕМ ВНИМАНИЕ, ЧТО ОН - ФУНКЦИОНАЛЬНЫЙ

      import java.util.Comparator;

      public class MaxSpeedCarComparator implements Comparator<Car> {

         @Override
         public int compare(Car o1, Car o2) {
           return o1.getMaxSpeed() - o2.getMaxSpeed();
         }
      }

_Как же нам пользоваться этим?_

**Очень просто:**

       //...
       Comparator speedComparator = new MaxSpeedCarComparator();
       Collections.sort(cars, speedComparator);
       //...

**Со звездочкой * (если вдруг данное пособие нужно будет не только мне)**

см. [Анонимные функции и классы тут](https://github.com/Jahimees/Java-Cheat-Sheet/blob/main/Java/%D0%A7%D0%B0%D1%81%D1%82%D0%BD%D1%8B%D0%B5%20%D1%82%D0%B5%D0%BC%D1%8B/%D0%90%D0%BD%D0%BE%D0%BD%D0%B8%D0%BC%D0%BD%D1%8B%D0%B5%20%D1%84%D1%83%D0%BD%D0%BA%D1%86%D0%B8%D0%B8%20%D0%B8%20%D0%BA%D0%BB%D0%B0%D1%81%D1%81%D1%8B.md)

см. [Лямбды тут](https://github.com/Jahimees/Java-Cheat-Sheet/blob/main/Java/%D0%A7%D0%B0%D1%81%D1%82%D0%BD%D1%8B%D0%B5%20%D1%82%D0%B5%D0%BC%D1%8B/Stream%20%D0%B8%20%D0%BB%D1%8F%D0%BC%D0%B1%D0%B4%D0%B0.md) 

см.[Функциональные интерфейсы тут](https://github.com/Jahimees/Java-Cheat-Sheet/blob/main/Java/%D0%A7%D0%B0%D1%81%D1%82%D0%BD%D1%8B%D0%B5%20%D1%82%D0%B5%D0%BC%D1%8B/%D0%A4%D1%83%D0%BD%D0%BA%D1%86%D0%B8%D0%BE%D0%BD%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5%20%D0%B8%D0%BD%D1%82%D0%B5%D1%80%D1%84%D0%B5%D0%B9%D1%81%D1%8B.md)

Поскольку Comparator является функциональным интерфейсом, то можно реализовать компаратор налету прямо в коде (если он будет применяться единожды).
Пойдем от простого к сложному. От большого кода к малому. Оставим пример с машинами и скоростью.

Предусловие к каждому примеру:

        Car car1 = new Car(100);
        Car car2 = new Car(120);
        Car car3 = new Car(90);
        Car car4 = new Car(110);

        List<Car> cars = new LinkedList<>(Arrays.asList(car1, car2, car3, car4));

**1. Мы можем реализовать анонимный класс прямо в коде:**

        cars.sort(new Comparator<Car>() {
            @Override
            public int compare(Car o1, Car o2) {
                return o1.getMaxSpeed() - o2.getMaxSpeed();
            }
        });

**2. Запись можно упростить, поскольку известно, что у интерфейса Comparator всего один метод - compare. Можно применить лямбды:**

        cars.sort((Car o1, Car o2) -> {
            return o1.getMaxSpeed() - o2.getMaxSpeed();
        });

В данном случае уходит полностью сигнатура метода и код становится короче. Из минусов: сразу начинаются проблемы с читабельностью

**3. Поскольку нам известны типы входных параметров, их также можно не указывать. А также с учетом того, что операция у нас всего одна, можно опустить фигурные скобки и слово return:**

        cars.sort((o1, o2) -> o1.getMaxSpeed() - o2.getMaxSpeed());

**4. У Comparator есть ряд методов-заготовок для сравнения:**

* comparing
* comparingInt
* comparingDouble
* comparingLong

В качестве параметра принимает Function keyExtractor. Сюда необходимо передать функцию, которая бы "вытаскивала" нужный параметр для сравнения. Например:

    Comparator comp = Comparator.comparing(Car::getMaxSpeed);

В одну строку мы реализовали компаратор, который можно передавать в метод sort:

    cars.sort(comp);

Либо реализовывать прямо в методе:

    cars.sort(Comparator.comparingInt(Car::getMaxSpeed));

То есть. Статическая функция Comparator.comparing принимает функцию ключа сортировки и возвращает Comparator для типа, содержащего ключ сортировки:

    static <T,U extends Comparable<? super U>> Comparator<T> comparing(
       Function<? super T,? extends U> keyExtractor)

