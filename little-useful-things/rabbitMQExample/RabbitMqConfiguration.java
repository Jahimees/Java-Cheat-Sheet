package com.example.rabbitmq;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost", 5672);
        connectionFactory.setUsername("simple");
        connectionFactory.setPassword("password");
        return connectionFactory;
    }

//    @Bean
//    public ConnectionFactory connectionFactory()
//    {
//        //получаем адрес AMQP у провайдера
//        String uri = System.getenv("CLOUDAMQP_URL");
//        if (uri == null) //значит мы запущены локально и нужно подключаться к локальному rabbitmq
//            uri = "amqp://guest:guest@localhost";
//        URI url = null;
//        try
//        {
//            url = new URI(uri);
//        } catch (URISyntaxException e)
//        {
//            e.printStackTrace(); //тут ошибка крайне маловероятна
//        }
//
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setHost(url.getHost());
//        connectionFactory.setUsername(url.getUserInfo().split(":")[0]);
//        connectionFactory.setPassword(url.getUserInfo().split(":")[1]);
//        if (StringUtils.isNotBlank(url.getPath()))
//            connectionFactory.setVirtualHost(url.getPath().replace("/", ""));
//        connectionFactory.setConnectionTimeout(3000);
//        connectionFactory.setRequestedHeartBeat(30);
//        return connectionFactory;
//    }

    @Bean
    public Binding errorBinding() {
        return BindingBuilder.bind(queue1()).to(directExchange()).with("error");
    }

    @Bean
    public Binding infoBinding() {
        return BindingBuilder.bind(queue2()).to(directExchange()).with("info");
    }

    @Bean
    public Binding warningBinding() {
        return BindingBuilder.bind(queue3()).to(directExchange()).with("warning");
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("direct-exchange");
    }

    @Bean
    public FanoutExchange fanoutExchangeA() {
        return new FanoutExchange("exchange");
    }

    @Bean
    public Binding binding1() {
        return BindingBuilder.bind(queue1()).to(fanoutExchangeA());
    }

    @Bean
    public Binding binding2() {
        return BindingBuilder.bind(queue2()).to(fanoutExchangeA());
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public Queue queue1() {
        return new Queue("queue1");
    }

    @Bean
    public Queue queue2() {
        return new Queue("queue2");
    }

    @Bean
    public Queue queue3() {
        return new Queue("queue3");
    }

}
