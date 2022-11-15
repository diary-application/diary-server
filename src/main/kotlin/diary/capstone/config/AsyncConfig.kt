package diary.capstone.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

/**
 * 비동기 로직 처리를 위한 Async thread 설정
 */
@Configuration
@EnableAsync
class AsyncConfig: AsyncConfigurer {

    override fun getAsyncExecutor(): ThreadPoolTaskExecutor =
        ThreadPoolTaskExecutor().let { executor ->
            executor.setThreadNamePrefix("async-thread-")
            executor.corePoolSize = 10      // 실행 대기 스레드 수
            executor.maxPoolSize = 10       // 동작 최대 스레드 수
            executor.queueCapacity = 500    // 대기 가능 스레드 수

            executor.initialize()
            executor
        }
}