_Источники: https://struchkov.dev/blog/ru/optional-in-java/_

## Зачем нужен Optional?

Допустим, мы хотим найти пользователя в базе данных по идентификатору. После чего нам требуется вывести на консоль длину имени пользователя. На первый взгляд это простой и безобидный пример.

        final Person person = personRepository.findById(1L);
        final String firstName = person.getFirstName();
        System.out.println("Длина твоего имени: " + firstName.length());

Выполнив этот код, мы можем получить исключение:

          Exception in thread "main" java.lang.NullPointerException: Cannot invoke "dev.struchkov.example.optional.Person.getFirstName()" because "person" is null
	          at dev.struchkov.example.optional.Main.test(Main.java:18)
	          at dev.struchkov.example.optional.Main.main(Main.java:13)

Почему это могло произойти? Дело в том, что мы не учли того факта, что пользователя с идентификатором 1L может не быть в нашей HashMap. И у нас нет варианта, кроме как вернуть null.

Решение "в лоб"

      final User user = userRepository.findById(1L);
        if (user != null) {
	        final String firstName = user.getFirstName();
	        if (firstName != null) {
		        System.out.println("Длина твоего имени: " + firstName.length());
	        }
      }

Слишком много "если"...

## Введение в Optional

Можно воспринимать Optional, как некую коробку, обертку, в которую кладется какой-либо объект. Optional всего лишь контейнер: он может содержать объект некоторого типа Т, а может быть пустым.


![image](https://github.com/Jahimees/Java-Cheat-Sheet/assets/36009821/a813ffe8-a034-404c-a1fb-fd95356e7772)

Перепишем наш пример с использованием Optional и посмотрим, что изменится:

       public class PersonRepository {

            private final Map<Long, Person> persons;

            public PersonRepository(Map<Long, Person> persons) {
                  this.persons = persons;
            }    

            public Optional<Person> findById(Long id) {
                  return Optional.ofNullable(persons.get(id));
            }

        }

**Работа с Optional:**

      final Optional<Person> optPerson = personRepository.findById(1L);
      if (optPerson.isPresent()) {
          final Person person = optPerson.get();
          final String firstName = person.getFirstName();
          if (firstName != null) {
    	        System.out.println("Длина твоего имени: " + firstName.length());
          }
      }        

случилось концептуально важное событие, вы точно знаете что метод findById() возвращает контейнер, в котором объекта может не быть. Больше вы не ожидаете внезапного **null** значения, если только разработчик этого метода не сумашедший, ведь ничто не мешает ему вернуть **null** вместо **Optional**

Здесь мы с вами увидели два основных метода: isPresent() возвращает true, если внутри есть объект, а метод get() возвращает этот объект.

Упростим этот код с помощью других методов Optional, чтобы увидеть какие преимущества нам это даст. Обо всех этих методах мы еще подробнее поговорим ниже.

      final Optional<Person> optPerson = personRepository.findById(1L);
      optUser.map(person -> person.getFirstName())
        .ifPresent(
                firstName -> System.out.println("Длина твоего имени: " + firstName.length())
        );

.* Если ты еще не знаешь, что это за магическая запись **_person -> person.getFirstName()_**, тогда тебе в [Stream и Лямбда](https://github.com/Jahimees/Java-Cheat-Sheet/blob/main/Java/%D0%A7%D0%B0%D1%81%D1%82%D0%BD%D1%8B%D0%B5%20%D1%82%D0%B5%D0%BC%D1%8B/Stream%20%D0%B8%20%D0%BB%D1%8F%D0%BC%D0%B1%D0%B4%D0%B0.md).

Мы воспользовались методом map(), который преобразует наш Optional в Optional другого типа. В данном случае мы получили фамилию пользователя, то есть преобразовали Optional<User> в Optional<String>.

Чтобы было проще понять, эквивалентно:

      final Optional<Person> optPerson = personRepository.findById(1L);
      final Optional<String> optFirstName = optPerson.map(user -> user.getFirstName());

      optFirstName.ifPresent(
	      firstName -> System.out.println("Длина твоего имени: " + firstName.length())
      );

Сократим код еще:

      personRepository.findById(1L)
          .map(User::getFirstName)
          .ifPresent(
                  firstName -> System.out.println("Длина твоего имени: " + firstName.length())
          );

## Методы Optional

### Cоздание Optional

У данного класса нет конструкторов, но есть три статических метода, которые и создают экземпляры класса.

#### Метод Optional.ofNullable(T t)

Optional.ofNullable принимает любой тип и создает контейнер. Если в параметры этого метода передать null, то будет создан пустой контейнер.

Используйте этот метод, когда есть вероятность, что упаковываемый **объект может иметь null значение**.

        public Optional<Person> findById(Long id) {
            return Optional.ofNullable(persons.get(id));
        }

#### Метод Optional.of(T t)

Этот метод аналогичен Optional.ofNullable, но _если передать в параметр null значение, то получим старый добрый NullPointerException_.

Используйте этот метод, когда точно уверены, **что упаковываемое значение не должно быть null**.        

       public Optional<Person> findByLogin(String login) {
            for (Person person : persons.values()) {
                if (person.getLogin().equals(login)) {
                    return Optional.of(person);
                }
            }
            return Optional.empty();
        }

Новый метод позволяет найти пользователя по логину. Для этого мы проходимся по значениям Map и сравниваем логины пользователей с переданным, как только находим совпадение вызываем метод Optional.of(), потому что мы уверены, что такой объект существует.

#### Метод Optional.empty()

Но что делать, если пользователь с переданным логином не был найден? Метод все равно должен вернуть Optional.

Можно вызвать Optional.ofNullable(null) и вернуть его, но лучше воспользоваться методом Optional.empty(), который возвращает пустой Optional.

### Получение содержимого

#### Метод isPresent()

Прежде, чем достать что-то, неплохо убедиться, что это что-то там действительно есть. И с этим нам поможет метод isPresent(), который возвращает true, если в контейнере есть объект и false в противном случае.

      Optional<Person> optPerson = personRepository.findById(1L);

      if(optPerson.isPresent()) {
	      // если пользователь найден, то выполняем этот блок кода
      }

Фактически это обычная проверка, как если бы мы сами написали if (value != null). И если зайти в реализацию метода isPresent(), то это мы там и увидим:

      public boolean isPresent() {
	      return value != null;
      }

#### Метод isEmpty()

Метод isEmpty() это противоположность методу isPresent(). Метод вернет true, если объекта внутри нет и false в противном случае. Думаю, вы уже догадались, как выглядит реализация этого метода:

      public boolean isEmpty() {
	      return value == null;
      }

#### Метод get()

После того, как вы убедились в наличии объекта с помощью предыдущих методов, вы можете смело достать объект из контейнера с помощью метода get().

      Optional<Person> optPerson = personRepository.findById(1L);

      if(optPerson.isPresent()) {
      final Person person = optPerson.get();
    
	        // остальной ваш код
      }

Конечно, вы можете вызвать метод get() и без проверки. Но если объекта там не окажется, то вы получите NoSuchElementException.

#### Метод ifPresent()

Помимо метода isPresent(), имеется метод ifPresent(), который принимает в качестве аргумента функциональный интерфейс Consumer. Это позволяет нам выполнить какую-то логику над объектом, если объект имеется.

Давайте с помощью этого метода выведем имя и фамилию пользователя на консоль.

      personRepository.findById(id).ifPresent(
            person -> System.out.println(person.getFirstName() + " " + person.getLastName())
      );

#### Метод ifPresentOrElse()

Метод **ifPresent()** ничего не сделает, если у вас нет объекта, но если вам и в этом случае необходимо выполнить какой-то код, то используйте метод **ifPresentOrElse()**, который принимает в качестве параметра еще и функциональный интерфейс **_Runnable_**.

        personRepository.findById(id).ifPresentOrElse(
              person -> System.out.println(person.getFirstName() + " " + person.getLastName()),
              () -> System.out.println("Иван Иванов")
        );

#### Метод orElse()

Метод делает ровно то, что ожидается от его названия: возвращает значение в контейнере или значение по-умолчанию, которое вы указали.

Например мы можем вернуть пользователя по идентификатору, а если такого пользователя нет, то вернем анонимного пользователя.

      public Person getPersonOrAnon(Long id) {
          return personRepository.findById(id)
               .orElse(new Person(-1L, "anon", "anon", "anon"));
      }

Используйте этот метод, когда хотите вернуть значение по умолчанию, если контейнер пустой.


![image](https://github.com/Jahimees/Java-Cheat-Sheet/assets/36009821/49e812b3-345e-4ee6-8ae8-794133315adc)

#### Метод orElseGet()

Метод похож на предыдущий, но вместо возвращения значения, он выполнит функциональный интерфейс Supplier.

Возьмем пришлый пример и будем также выводить в консоль сообщение о том, что пользователь не был найден.

      public Person getPersonOrAnonWithLog(Long id) {
          return personRepository.findById(id)
                  .orElseGet(() -> {
                      System.out.println("Пользователь не был найден, отправляем анонимного");
                      return new Person(-1L, "anon", "anon", "anon");
          });
      }

Используйте этот метод, когда вам не достаточно просто вернуть какое-то дефолтное значение, но и требуется выполнить какую-то более сложную логику.

#### orElseThrow()

Этот метод вернет объект если он есть, в противном случае выбросит стандартное исключение NoSuchElementException("No value present").

#### orElseThrow(Supplier s)

Этот метод позволяет вернуть любое исключение вместо стандартного NoSuchElementException, если объекта нет.

      public Person getPersonOrThrow(Long id) {
          return personRepository.findById(id)
                  .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
      }


.** Для красоты:
![image](https://github.com/Jahimees/Java-Cheat-Sheet/assets/36009821/35a30e51-64dc-44c7-b0b8-e7be45e2add3)

### Преобразование

Optional также имеет ряд методов, которые могет быть знакомы вам по стримам: map, filter и flatMap. Они имеют такие же названия, и делают примерно то же самое.

#### Метод filter()

Если внутри контейнера есть значение и оно удовлетворяет переданному условию в виде функционального интерфейса Predicate, то будет возвращен новый объект Optional с этим значением, иначе будет возвращен пустой Optional.

Например, мы сделаем метод, который возвращает только взрослых пользователей по id.

      public Optional<Person> getAdultById(Long id) {
          return personRepository.findById(id)
                  .filter(person -> person.getAge() > 18);
      }

Используйте его, когда вам нужен контейнер, объект в котором удвлетворят какому-то условию.

#### Метод map()

Если внутри контейнера есть значение, то к значению применяется переданная функция, результат помещается в новый Optional и возвращается, в случае отсутствия значения будет возвращен пустой контейнер. Для преобразования используется функциональный интерфейс Function.

Сделаем метод, который по идентификатору пользователя возвращает Optional<String>, содержащий имя и фамилию этого пользователя.

      public Optional<String> getFirstAndLastNames(Long id) {
          return personRepository.findById(id)
                  .map(person -> person.getFirstName() + " " + person.getLastName());
      }

Используйте этот метод, когда необходимо преобразовать объект внутри контейнера в другой объект.

#### Метод flatMap()

Как уже было сказано, map() оборачивает возвращаемый результат лямбды Function. Но что, если эта лямбда у нас будет возвращать уже обернутый результат, у нас получится Optional<Optional<T>>. С таким дважды упакованным объектом будет сложно работать.

Для примера, мы можем запрашивать какие-то данные о пользователе из другого сервиса, и этих данных тоже может не быть, поэтому возникает второй контейнер. Вот как это будет выглядеть:

      Optional<Optional<String>> optUserPhoneNumber = personRepository.findById(1L)
              .map(person -> {
                  Optional<String> optPhoneNumber = phoneNumberRepository.findByPersonId(person.getId());
                  return optPhoneNumber;
              });

В таком случае используйте flatMap(), он позволит вам избавиться от лишнего контейнера.

      Optional<String> optUserPhoneNumber = personRepository.findById(1L)
              .flatMap(person -> {
                  Optional<String> optPhoneNumber = phoneNumberRepository.findByLogin(user.getLogin());
                  return optPhoneNumber;
              });

#### Метод Stream()

В Java 9 в Optional был добавлен новый метод stream(), который позволяет удобно работать со стримом от коллекции Optional элементов.

Допустим, вы запрашивали множество пользователей по различным идентификаторам, а в ответ вам возвращался Optional<Person>. Вы сложили полученные объекты в коллекцию, и теперь с ними нужно как-то работать. Можно создать стрим от этой коллекции, и используя метод flatMap() стрима и метод stream() у Optional, чтобы получить стрим существующих пользователей. При этом все пустые контейнеры будут отброшены.

      final List<Optional<Person>> optPeople = new ArrayList<>();
      final List<Person> people = optPeople.stream()
              .flatMap(optItem -> optItem.stream())
              .toList();

#### Метод or()

Начиная с Java 11 добавили новый метод or(). Он позволяет изменить пустой Optional передав новый объект, раньше так сделать было нельзя.

Важно понимать, что этот _метод не изменяет объект Optional_, а создает и возвращает новый.

Например мы запрашиваем пользователя по идентификатору, и если его нет в хранилище, то мы передаем Optional анонимного пользователя.

      public Optional<Person> getPersonOrAnonOptional(Long id) {
          return personRepository.findById(id)
                  .or(() -> Optional.of(new Person(-1L, "anon", "anon", "anon", 0L)));
      }

### Комбинирование методов

Все перечисленные методы возвращают в ответ Optional, поэтому вы можете составлять из них цепочки, прямо как у стримов.

Пример оторванный от реальности, но иллюстрирующий цепочку методов:

      final LocalDateTime now = LocalDateTime.now();
      final DayOfWeek dayWeek = Optional.of(now)
              .map(LocalDateTime::getDayOfWeek)
              .filter(dayOfWeek -> DayOfWeek.SUNDAY.equals(dayOfWeek))
              .orElse(DayOfWeek.MONDAY);

## Прочие нюансы

1. Когда использовать?

**JavaDoc:** Optional в первую очередь предназначен для использования в качестве типа возвращаемого значения метода, когда существует явная необходимость представлять «отсутствие результата» и где использование null может вызвать ошибки.

2. Как НЕ стоит использовать?
* Как параметр метода
* Как свойство класса - конфликты с Spring Data/Hibernate
* Как обертка коллекции

Не оборачивайте коллекции в Optional. Любая коллекция является контейнером сама по себе. Чтобы вернуть пустую коллекцию, вместо null, можно воспользоваться следующими методами Collections.emptyList(), Collections.emptySet() и прочими.

3. Optional не должен равняться null

Присваивание null вместо объекта Optional разрушает саму концепцию его использования. Никто из пользователей вашего метода не будет проверять Optional на эквивалентность с null. Вместо присваивания null следует использовать Optional.empty().

4. Примитивы и Optional

Для работы с обертками примитивов есть java.util.OptionalDouble, java.util.OptionalInt и java.util.OptionalLong, которые позволяют избегать лишних автоупаковок и распаковок. Однако не смотря на это, на практике используются они крайне редко.

Все эти классы похожи на Optional, но не имеют методов преобразования. В них доступны только: get, orElse, orElseGet, orElseThrow, ifPresent и isPresent.

**Основные моменты, которые стоит запомнить**
* Если методу требуется вернуть null, верните Optional;
* Не используйте Optional в качестве параметра методов и как свойство класса;
* Проверяйте Optional перед тем, как достать его содержимое;
* Никогда так не делайте: optional.orElse(null);

