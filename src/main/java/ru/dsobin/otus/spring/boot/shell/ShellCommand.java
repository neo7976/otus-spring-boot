package ru.dsobin.otus.spring.boot.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@RequiredArgsConstructor
public class ShellCommand {

    private final MessageSource messageSource;

    @ShellMethod(key = {"hello", "hello-to"}, value = "Say hello to username")
    public String helloTo(@ShellOption({"username", "u"}) String username) {
        return messageSource.getMessage("quiz.hello.simple", new Object[]{username}, LocaleContextHolder.getLocale());
    }
}
