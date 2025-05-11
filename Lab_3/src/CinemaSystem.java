import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class User {
    String login;
    String password;
    String role;

    public User(String login, String password, String role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }
}

class Movie {
    String title;
    int durationMinutes;

    public Movie(String title, int durationMinutes) {
        this.title = title;
        this.durationMinutes = durationMinutes;
    }

    @Override
    public String toString() {
        return title + " (" + durationMinutes + " мин)";
    }
}

class Hall {
    String name;
    int rows;
    int seatsPerRow;
    int[][] seats; // 0 - свободно, 1 - занято

    public Hall(String name, int rows, int seatsPerRow) {
        this.name = name;
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
        this.seats = new int[rows][seatsPerRow];
    }

    public boolean isSeatAvailable(int row, int seat) {
        return row >= 0 && row < rows && seat >= 0 && seat < seatsPerRow && seats[row][seat] == 0;
    }

    public void bookSeat(int row, int seat) {
        if (isSeatAvailable(row, seat)) {
            seats[row][seat] = 1;
        }
    }

    public void printHallLayout() {
        System.out.println("План зала '" + name + "':");
        System.out.print("  ");
        for (int i = 0; i < seatsPerRow; i++) {
            System.out.print((i+1) + " ");
        }
        System.out.println();

        for (int i = 0; i < rows; i++) {
            System.out.print((i+1) + " ");
            for (int j = 0; j < seatsPerRow; j++) {
                System.out.print((seats[i][j] == 0 ? "O" : "X") + " ");
            }
            System.out.println();
        }
    }
}

class Session {
    Movie movie;
    Hall hall;
    LocalTime startTime;

    public Session(Movie movie, Hall hall, LocalTime startTime) {
        this.movie = movie;
        this.hall = hall;
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return startTime.plusMinutes(movie.durationMinutes);
    }

    @Override
    public String toString() {
        return movie.title + " | " + hall.name + " | " + startTime.toString().substring(0, 5) + "-" + getEndTime().toString().substring(0, 5);
    }
}

class Theater {
    String name;
    List<Hall> halls = new ArrayList<>();

    public Theater(String name) {
        this.name = name;
    }

    public void addHall(Hall hall) {
        halls.add(hall);
    }
}

public class CinemaSystem {
    static List<User> users = new ArrayList<>();
    static List<Movie> movies = new ArrayList<>();
    static List<Theater> theaters = new ArrayList<>();
    static List<Session> sessions = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeTestData();
        System.out.println("Добро пожаловать в билетную систему кинотеатров!");

        while (true) {
            System.out.println("\nВыберите действие:");
            System.out.println("1. Войти как администратор");
            System.out.println("2. Войти как пользователь");
            System.out.println("3. Выход");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    if (authenticate("admin", "1234")) {
                        adminMenu();
                    }
                    break;
                case 2:
                    if (authenticate("user", "1234")) {
                        userMenu();
                    }
                    break;
                case 3:
                    System.out.println("До свидания!");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте еще раз.");
            }
        }
    }

    private static void initializeTestData() {
        users.add(new User("admin", "1234", "admin"));
        users.add(new User("user", "1234", "user"));

        movies.add(new Movie("Аватар", 162));
        movies.add(new Movie("Титаник", 195));
        movies.add(new Movie("Мстители: Финал", 181));

        Theater theater1 = new Theater("Синема Парк");
        theater1.addHall(new Hall("Зал 1", 5, 10));
        theater1.addHall(new Hall("VIP Зал", 3, 6));
        theaters.add(theater1);

        Theater theater2 = new Theater("Формула Кино");
        theater2.addHall(new Hall("Зал А", 6, 12));
        theater2.addHall(new Hall("Зал B", 4, 8));
        theaters.add(theater2);

        sessions.add(new Session(movies.get(0), theater1.halls.get(0), LocalTime.of(10, 0)));
        sessions.add(new Session(movies.get(0), theater1.halls.get(0), LocalTime.of(13, 0)));
        sessions.add(new Session(movies.get(1), theater1.halls.get(1), LocalTime.of(11, 30)));
        sessions.add(new Session(movies.get(2), theater2.halls.get(0), LocalTime.of(14, 0)));
        sessions.add(new Session(movies.get(1), theater2.halls.get(1), LocalTime.of(16, 30)));
    }

    private static boolean authenticate(String expectedRole, String expectedPassword) {
        System.out.print("Введите логин: ");
        String login = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        for (User user : users) {
            if (user.login.equals(login) && user.password.equals(password) && user.role.equals(expectedRole)) {
                System.out.println("Успешный вход!");
                return true;
            }
        }
        System.out.println("Ошибка аутентификации!");
        return false;
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\nМеню администратора:");
            System.out.println("1. Добавить фильм");
            System.out.println("2. Добавить кинотеатр");
            System.out.println("3. Добавить зал в кинотеатр");
            System.out.println("4. Добавить сеанс фильма в зал");
            System.out.println("5. Вернуться в главное меню");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    addMovie();
                    break;
                case 2:
                    addTheater();
                    break;
                case 3:
                    addHallToTheater();
                    break;
                case 4:
                    addSessionToHall(); // <-- Теперь работает
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте еще раз.");
            }
        }
    }

    private static void userMenu() {
        while (true) {
            System.out.println("\nМеню пользователя:");
            System.out.println("1. Просмотреть все фильмы");
            System.out.println("2. Найти ближайший сеанс фильма");
            System.out.println("3. Забронировать билет");
            System.out.println("4. Просмотреть план зала для сеанса");
            System.out.println("5. Вернуться в главное меню");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    viewAllMovies();
                    break;
                case 2:
                    findNextSession();
                    break;
                case 3:
                    bookTicket();
                    break;
                case 4:
                    viewHallLayout();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте еще раз.");
            }
        }
    }

    private static void addMovie() {
        System.out.print("Введите название фильма: ");
        String title = scanner.nextLine();
        System.out.print("Введите длительность фильма в минутах: ");
        int duration = getIntInput();
        movies.add(new Movie(title, duration));
        System.out.println("Фильм добавлен!");
    }

    private static void addTheater() {
        System.out.print("Введите название кинотеатра: ");
        String name = scanner.nextLine();
        theaters.add(new Theater(name));
        System.out.println("Кинотеатр добавлен!");
    }

    private static void addHallToTheater() {
        if (theaters.isEmpty()) {
            System.out.println("Сначала добавьте кинотеатр!");
            return;
        }

        System.out.println("Выберите кинотеатр:");
        for (int i = 0; i < theaters.size(); i++) {
            System.out.println((i + 1) + ". " + theaters.get(i).name);
        }

        int theaterIndex = getIntInput() - 1;

        System.out.print("Введите название зала: ");
        String hallName = scanner.nextLine();

        System.out.print("Введите количество рядов: ");
        int rows = getIntInput();

        System.out.print("Введите количество мест в ряду: ");
        int seatsPerRow = getIntInput();

        theaters.get(theaterIndex).addHall(new Hall(hallName, rows, seatsPerRow));
        System.out.println("Зал добавлен в кинотеатр.");
    }

    private static void addSessionToHall() {
        if (movies.isEmpty() || theaters.isEmpty()) {
            System.out.println("Сначала добавьте фильмы и кинотеатры!");
            return;
        }

        System.out.println("Выберите кинотеатр:");
        for (int i = 0; i < theaters.size(); i++) {
            System.out.println((i + 1) + ". " + theaters.get(i).name);
        }
        int theaterIndex = getIntInput() - 1;

        Theater selectedTheater = theaters.get(theaterIndex);

        if (selectedTheater.halls.isEmpty()) {
            System.out.println("Этот кинотеатр не имеет залов!");
            return;
        }

        System.out.println("Выберите зал:");
        for (int i = 0; i < selectedTheater.halls.size(); i++) {
            System.out.println((i + 1) + ". " + selectedTheater.halls.get(i).name);
        }
        int hallIndex = getIntInput() - 1;

        System.out.println("Выберите фильм:");
        for (int i = 0; i < movies.size(); i++) {
            System.out.println((i + 1) + ". " + movies.get(i).title);
        }
        int movieIndex = getIntInput() - 1;

        System.out.print("Введите время начала сеанса (в формате ЧЧ:ММ): ");
        String[] timeParts = scanner.nextLine().split(":");
        LocalTime startTime = LocalTime.of(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));

        sessions.add(new Session(
                movies.get(movieIndex),
                selectedTheater.halls.get(hallIndex),
                startTime
        ));

        System.out.println("Сеанс добавлен в зал '" +
                selectedTheater.halls.get(hallIndex).name +
                "' кинотеатра '" +
                selectedTheater.name + "'");
    }

    private static void viewAllMovies() {
        System.out.println("\nДоступные фильмы:");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }

    private static void findNextSession() {
        if (movies.isEmpty() || sessions.isEmpty()) {
            System.out.println("Нет доступных фильмов или сеансов!");
            return;
        }

        System.out.println("Выберите фильм:");
        for (int i = 0; i < movies.size(); i++) {
            System.out.println((i + 1) + ". " + movies.get(i).title);
        }

        int movieIndex = getIntInput() - 1;
        Movie selectedMovie = movies.get(movieIndex);
        LocalTime now = LocalTime.now();
        Session nextSession = null;

        for (Session session : sessions) {
            if (session.movie.title.equals(selectedMovie.title) && session.startTime.isAfter(now)) {
                if (nextSession == null || session.startTime.isBefore(nextSession.startTime)) {
                    nextSession = session;
                }
            }
        }

        if (nextSession != null) {
            System.out.println("Ближайший сеанс: " + nextSession.toString());
        } else {
            System.out.println("Сеансов для этого фильма не найдено.");
        }
    }

    private static void bookTicket() {
        if (sessions.isEmpty()) {
            System.out.println("Нет доступных сеансов!");
            return;
        }

        System.out.println("Выберите сеанс:");
        for (int i = 0; i < sessions.size(); i++) {
            System.out.println((i + 1) + ". " + sessions.get(i).toString());
        }

        int sessionIndex = getIntInput() - 1;
        Session selectedSession = sessions.get(sessionIndex);

        selectedSession.hall.printHallLayout();

        System.out.print("Введите номер ряда: ");
        int row = getIntInput() - 1;

        System.out.print("Введите номер места: ");
        int seat = getIntInput() - 1;

        if (selectedSession.hall.isSeatAvailable(row, seat)) {
            selectedSession.hall.bookSeat(row, seat);
            System.out.println("Билет забронирован!");
        } else {
            System.out.println("Это место уже занято!");
        }
    }

    private static void viewHallLayout() {
        if (sessions.isEmpty()) {
            System.out.println("Нет доступных сеансов!");
            return;
        }

        System.out.println("Выберите сеанс:");
        for (int i = 0; i < sessions.size(); i++) {
            System.out.println((i + 1) + ". " + sessions.get(i).toString());
        }

        int sessionIndex = getIntInput() - 1;
        sessions.get(sessionIndex).hall.printHallLayout();
    }

    private static int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Пожалуйста, введите число: ");
            }
        }
    }
}