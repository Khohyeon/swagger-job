package shop.mtcoding.job.config.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoginEnt {
    public static Object builder;
    private Integer id;
    private String role;

    @Builder
    public LoginEnt(Integer id, String role) {
        this.id = id;
        this.role = role;
    }
}