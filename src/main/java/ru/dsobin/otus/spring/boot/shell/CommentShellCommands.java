package ru.dsobin.otus.spring.boot.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.dsobin.otus.spring.boot.model.Comment;
import ru.dsobin.otus.spring.boot.service.BookService;
import ru.dsobin.otus.spring.boot.service.CommentService;

import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class CommentShellCommands {
    private final CommentService commentService;
    private final BookService bookService;

//    @ShellMethod(key = {"list-comments", "l-c"}, value = "List comments for book")
//    public void listComments(@ShellOption long bookId) {
//        bookService.findById(bookId).ifPresentOrElse(book -> {
//            List<Comment> comments = commentService.findByBookId(bookId);
//            if (comments.isEmpty()) {
//                System.out.println("No comments for book " + bookId);
//            } else {
//                comments.forEach(c -> System.out.println(c.getId() + ": " + c.getText()));
//            }
//        }, () -> System.out.println("Book not found"));
//    }

//    @ShellMethod(key = {"a-c", "add-comment"}, value = "Add comment to book")
//    public void addComment(@ShellOption long bookId, @ShellOption String text) {
//        try {
//            Comment comment = commentService.create(bookId, text);
//            System.out.println("Comment added with ID: " + comment.getId());
//        } catch (Exception e) {
//            System.out.println("Error: " + e.getMessage());
//        }
//    }

    @ShellMethod(key = {"u-c", "update-comment", "upd-comment"}, value = "Update comment")
    public void updateComment(@ShellOption long id, @ShellOption String text) {
        commentService.update(id, text);
        System.out.println("Comment updated");
    }

    @ShellMethod(key = {"d-c", "delete-comment", "del-comment"}, value = "Delete comment")
    public void deleteComment(@ShellOption long id) {
        commentService.deleteById(id);
        System.out.println("Comment deleted");
    }
}
