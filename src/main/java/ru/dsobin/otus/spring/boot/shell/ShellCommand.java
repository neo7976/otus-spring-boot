package ru.dsobin.otus.spring.boot.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.dsobin.otus.spring.boot.quiz.service.ConsoleIOService;
import ru.dsobin.otus.spring.boot.quiz.service.QuizService;

@ShellComponent
@RequiredArgsConstructor
public class ShellCommand {

    private final QuizService quizService;
    private final ConsoleIOService io;
    private final MessageSource messageSource;

    @ShellMethod(key = {"hello", "hello-to"}, value = "Say hello to username")
    public void helloTo(@ShellOption({"username", "u"}) String username) {
        var locale = LocaleContextHolder.getLocale();
        io.print(messageSource.getMessage("quiz.hello.simple", new Object[]{username}, locale));
    }

    @ShellMethod(key = {"start", "go", "Yes", "yes", "Да", "да"}, value = "Start quiz")
    public void startQuiz() {
        quizService.conductQuiz(io);
    }
}