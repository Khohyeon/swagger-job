package shop.mtcoding.job.config.aop;

// TODO 왜 이건 안되는지 찾아오기
public enum UserEnum {

    DEFAULT(0);

    private final int value;

    UserEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
