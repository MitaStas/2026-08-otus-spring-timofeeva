package ru.otus.hw;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.TestRunnerService;

public class Application {
    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("/spring-context.xml");
        var ioService = context.getBean(IOService.class);
        ioService.printLine("Enter your Last Name:");
        var lastName = ioService.nextLine();
        ioService.printLine("Enter your First Name:");
        var firstName = ioService.nextLine();
        var testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();


    }
}