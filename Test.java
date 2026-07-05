import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class Test {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        System.out.println(encoder.encode("Admin@123"));
        System.out.println(encoder.matches("Admin@123", "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LvlsiAg87re"));
    }
}
