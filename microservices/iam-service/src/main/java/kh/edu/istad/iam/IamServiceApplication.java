package kh.edu.istad.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class IamServiceApplication {

    @GetMapping("/login")
    public String Login() {
        return "login";
    }

    public static void main(String[] args) {
        SpringApplication.run(IamServiceApplication.class, args);
    }

}
