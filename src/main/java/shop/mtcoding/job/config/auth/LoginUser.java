package shop.mtcoding.job.config.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

@Getter
@ToString
@Slf4j
public class LoginUser {
    private final Integer id;
    private final String  role;

    @Builder
    public LoginUser(Integer id, String role) {
        this.id = id;
        this.role = role;
    }

    @PostConstruct
    public void init (){
        log.debug(toString());
    }
}