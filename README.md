# SQLBuilder
### 描述

* 快速生成SQL
* 静态工厂设计模式
* 动态代理设计模式

### 主要类

1. SQLBuilderConfiguration（配置类）

   ```java
   // sqlBuilder实现类
   private Class sqlBuilderClass;
   
   // sqlBuilder执行器类
   private Class sqlBuilderExecutorClass;
   
   // sqlBuilder实现类名
   private String sqlBuilderClassName;
   
   // sqlBuilder执行器类名
   private String sqlBuilderExecutorClassName;
   
   // 是否驼峰命名
   private boolean camelCase = false;
   ```

2. SQLBuilderFactory（SQLBuilder生成工厂）

   ```java
   // 创建默认SQLBuilder
   public static ISQLBuilder createDefaultSQLBuilder(String tableName) {
       MyISQLBuilder mySQLBuilder = new MyISQLBuilder(tableName);
       SQLBuilderProxy sqlBuilderProxy = new SQLBuilderProxy(mySQLBuilder, null);
       return (ISQLBuilder) Proxy.newProxyInstance(classLoader, mySQLBuilder.getClass().getInterfaces(), sqlBuilderProxy);
   }
   
   // 根据配置创建SQLBuilder
   public static ISQLBuilder createSQLBuilder(SQLBuilderConfiguration configuration, String tableName) {
       try {
           Class sqlBuilderClass = configuration.getSqlBuilderClass();
           if (BeanUtil.isEmpty(sqlBuilderClass) && !BeanUtil.isEmpty(configuration.getSqlBuilderClassName())) {
               sqlBuilderClass = Class.forName(configuration.getSqlBuilderClassName());
           }
           if(!BeanUtil.isEmpty(sqlBuilderClass)) {
               Object sqlBuilderObj = sqlBuilderClass.getDeclaredConstructor(String.class).newInstance(tableName);
               SQLBuilderProxy sqlBuilderProxy = new SQLBuilderProxy(sqlBuilderObj, configuration);
               return (ISQLBuilder) Proxy.newProxyInstance(classLoader, sqlBuilderClass.getInterfaces(), sqlBuilderProxy);
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       return createDefaultSQLBuilder(tableName);
   }
   ```

3. SQLBuilderProxy（SQLBuilder代理）

   ```java
   // 主要方法
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
       if (!filterObjectMethod(method.getName())) {
           if (args[0] instanceof Collection) {
               args[0] = before((Collection) args[0]);
           }
           if (args[0] instanceof SQLParam) {
               SQLParam sqlParam = (SQLParam) args[0];
               beforeCheckSimilar(sqlParam);
               beforeWithParam(sqlParam);
               args[0] = sqlParam;
           }
       }
       StringBuilder sqlBuilder = (StringBuilder) method.invoke(object, args);
       SimilarType similarType = SimilarType.getInstance(method.getName().toLowerCase());
       if (args[0] instanceof SQLParam) {
           SQLParam sqlParam = (SQLParam) args[0];
           afterSimilarWithParam(sqlParam, sqlBuilder);
           afterWithParam(sqlParam, sqlBuilder);
       }
       if (!similarType.equals(SimilarType.OTHER)) {
           afterSimilar(args[0], sqlBuilder);
       } else {
           if (args[0] instanceof Collection) {
               after((Collection) args[0], sqlBuilder);
           }
       }
       String sqlBuilderStr = sqlBuilder.toString().trim();
       if (sqlBuilderStr.endsWith("AND") || sqlBuilderStr.endsWith("OR")) {
           sqlBuilderStr = sqlBuilderStr
               .substring(0, sqlBuilderStr.lastIndexOf(" "));
       }
       return new StringBuilder(sqlBuilderStr.replaceAll("\\s+", " "));
   }
   ```

4. SQLParam（SQLBuilder参数）

   ```java
   // 条件字段
   private Collection<String> conditionFields;
   
   // 映射字段
   private Collection<String> executionFields;
   
   // 全模糊查询字段
   private Map<String, Object> similarMap;
   
   // 映射字段
   private Map<String, Object> preSimilarMap;
   
   // 前模糊查询字段
   private Map<String, Object> suffixSimilarMap;
   
   // 后模糊查询字段
   private Collection<RangeParam> rangeParam;
   
   // 分页查询字段
   private PageParam pageParam;
   
   // 排序查询字段
   private Collection<OrderParam> orderParam;
   ```

### 参照案例

>```java
>// generate sqlBuilderConfiguration
>SQLBuilderConfiguration sqlBuilderConfiguration = new SQLBuilderConfiguration();
>sqlBuilderConfiguration.setSqlBuilderClassName("com.rukawa.sql.interfaces.impl.MyISQLBuilder");
>sqlBuilderConfiguration.setSqlBuilderExecutorClass(JdbcTemplate.class);
>sqlBuilderConfiguration.setCamelCase(true);
>// generate params
>SQLParam sqlParam = new SQLParam();
>sqlParam.setExecutionFields(Arrays.asList("testR", "testT"));
>sqlParam.setConditionFields(Arrays.asList("testR", "testT"));
>// build range param
>sqlParam.setRangeParam(Arrays.asList(new RangeParam("testR", 1, 2, RangeSymbol.BETWEEN_AND),
>                                     new RangeParam("testT", 2, null, RangeSymbol.GREATER_THAN)));
>// build similar param
>Map<String, Object> similarMap = new HashMap<>();
>similarMap.put("testR", 1);
>similarMap.put("testT", 2);
>sqlParam.setSimilarMap(similarMap);
>// build order param
>sqlParam.setOrderParam(Arrays.asList(new OrderParam("testR", OrderSymbol.DEFAULT),
>                                     new OrderParam("testT", OrderSymbol.DESC)));
>// build page param
>sqlParam.setPageParam(new PageParam(1, 20));
>// generate ISQLBuilder
>ISQLBuilder ISQLBuilder = SQLBuilderFactory.createSQLBuilder(sqlBuilderConfiguration, "abc");
>System.out.println(ISQLBuilder.buildSelectSQLWithParam(sqlParam));
>```

### 注意事项

1. 模糊查询会处理一个Map，将Map封装成真实参数
2. 提供SQLBuilderAbs（抽象实现）、ISQLBuilder（接口）可以自行拓展目前一直执行器实现SQL生成