package com.commons.metadata.model.mq;


import java.lang.annotation.*;

/**
 * 动态代理注解
 * MQ发布方法执行失败后回调注解方法
 * 需保持与接口参数列表一致，
 * 第二个参数为调用状态 0成功|1失败|2超时|3队列溢出,
 * 第三个参数为返回值 无返回值也需要设置为Object none,
 * 第四个参数为Exception
 * 队列溢出可丢弃 可重发 可视为失败 按需操作
 * 注意：定义多个相同Consumer会被同时触发,若触发方法内部异常则默认无处理,可手工catch并重发
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface Consumer {
    //消费接口
    Class<?> serviceInterface();

    //消费方法名
    String method();

    //触发状态 = 0成功|1失败|2超时|3队列溢出
    State state() default State.ALL;

    String scope() default "";//interface+method+scope 作为锁

    enum State {
        ALL("ALL"),
        SUCCESS("SUCCESS"),
        FAIL("FAIL"),
        OVERFLOW("OVERFLOW"),
        TIMEOUT("TIMEOUT");

        private String value;

        State(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static State getEnum(String value) {
            for (State type : State.values()) {
                if (type.getValue().equalsIgnoreCase(value.trim())) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Not Found Consumer State value=" + value);
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
