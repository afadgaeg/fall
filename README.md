# 我的个人MVC、ORM框架 fall
链接： <http://www.iteye.com/topic/1136264>

- fall通过反射将请求参数绑定到JavaBean，解析JavaBean注解并调用Bean Validation进行数据验证
- fall实现了将multipart/form-data表单中上传的文件自动解析绑定至JavaBean并验证类型、大小
- fall实现将参数自动绑定至javaBean中的集合类型的属性并进行数据验证
- fall通过包装jdbc实现sql命名参数和ORM，通过反射和注解自动完成sql参数、查询结果集和JavaBean的映射和绑定
- fall早期版本的视图实现jstl规范，直接读取XML文件并将之渲染成HTML，后期版本抛弃这种不良设计
- fall实现允许通配符的用户分组权限模块
- fall实现简易的规则引擎
- fall的其它模块：数据分页、通过路径码实现数据库树状结构分类的模块等
- fall包括java和PHP两个版本