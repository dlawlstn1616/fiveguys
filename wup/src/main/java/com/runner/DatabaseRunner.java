@Component
@Order(1)
@Slf4j
public class DatabaseRunner implements ApplicationRunner {
    @Autowired
    DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("DataSource 구현객체는 {} ", dataSource.getClass().getName());

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            log.info("DB Product Name = {}", metaData.getDatabaseProductName());
            log.info("DB URL = {}", metaData.getURL());
            log.info("DB Username = {}", metaData.getUserName());
        }
    }
}