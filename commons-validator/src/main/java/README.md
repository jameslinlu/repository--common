
请求 地址 如/api/customer/save

一：拦截器方式 （Web请求）
1.由拦截器 从handlerMapping中获取指定方法 签名  返回类型+路径+方法名
2.通过ValidatorCache中按 key = 方法签名 以获取验证的方法
3.抛出响应为ResponseErrorMessage的body内为com.commons.metadata.model.validator.model.ResponseValidatorMessage

使用范围
1.定义
大多数验证由Web处理,Service处理 逻辑性验证 ，如电话号码重复 等等
使用方式同自定义验证web层验证

