import java.util.*;

class Book {
    private String title;
    private String author;
    private int year;

    public Book(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }

    @Override
    public String toString() {
        return "Книга: \"" + title + "\", Автор: " + author + ", Год: " + year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return year == book.year &&
                Objects.equals(title, book.title) &&
                Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, year);
    }
}

class Library {
    private List<Book> books = new ArrayList<>();
    private Set<String> uniqueAuthors = new HashSet<>();
    private Map<String, Integer> authorCount = new HashMap<>();

    public void addBook(Book book) {
        books.add(book);
        uniqueAuthors.add(book.getAuthor());
        authorCount.put(book.getAuthor(), authorCount.getOrDefault(book.getAuthor(), 0) + 1);
    }

    public void removeBook(Book book) {
        if (books.remove(book)) {
            String author = book.getAuthor();
            int count = authorCount.get(author);
            if (count > 1) {
                authorCount.put(author, count - 1);
            } else {
                authorCount.remove(author);
                uniqueAuthors.remove(author);
            }
        }
    }

    public List<Book> findBooksByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthor().equals(author)) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> findBooksByYear(int year) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getYear() == year) {
                result.add(book);
            }
        }
        return result;
    }

    public void printAllBooks() {
        System.out.println("Список всех книг в библиотеке:");
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public void printUniqueAuthors() {
        System.out.println("Уникальные авторы:");
        for (String author : uniqueAuthors) {
            System.out.println(author);
        }
    }

    public void printAuthorStatistics() {
        System.out.println("Статистика по авторам:");
        for (Map.Entry<String, Integer> entry : authorCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " книг(и)");
        }
    }
}

public class LibraryTest {
    public static void main(String[] args) {
        // Создаем библиотеку
        Library library = new Library();

        // Добавляем несколько книг
        Book book1 = new Book("Война и мир", "Лев Толстой", 1869);
        Book book2 = new Book("Анна Каренина", "Лев Толстой", 1877);
        Book book3 = new Book("Преступление и наказание", "Федор Достоевский", 1866);
        Book book4 = new Book("Идиот", "Федор Достоевский", 1869);
        Book book5 = new Book("Мастер и Маргарита", "Михаил Булгаков", 1967);

        library.addBook(book1);
        library.addBook(book2);
        library.addBook(book3);
        library.addBook(book4);
        library.addBook(book5);

        // Тестируем методы
        System.out.println("=== Вывод всех книг ===");
        library.printAllBooks();

        System.out.println("\n=== Вывод уникальных авторов ===");
        library.printUniqueAuthors();

        System.out.println("\n=== Вывод статистики по авторам ===");
        library.printAuthorStatistics();

        System.out.println("\n=== Поиск книг по автору 'Лев Толстой' ===");
        List<Book> tolstoyBooks = library.findBooksByAuthor("Лев Толстой");
        for (Book book : tolstoyBooks) {
            System.out.println(book);
        }

        System.out.println("\n=== Поиск книг, изданных в 1869 году ===");
        List<Book> booksFrom1869 = library.findBooksByYear(1869);
        for (Book book : booksFrom1869) {
            System.out.println(book);
        }

        System.out.println("\n=== Удаление книги 'Война и мир' ===");
        library.removeBook(book1);
        library.printAllBooks();
        library.printAuthorStatistics();
    }
}