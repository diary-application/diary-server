package diary.capstone.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableWebSecurity
class SecurityConfig: WebSecurityConfigurerAdapter() {

    // 비밀번호 암호화 객체
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    // 자동 생성할 AuthenticationManager 를 재정의
    // Security UserDetailsServiceAuthConfiguration 비활성화, 어플리케이션 구동 시 비밀번호 로깅 방지
    override fun configure(auth: AuthenticationManagerBuilder?) {}

    /**
     * 기본 Security 설정
     * Rest API 서버, 토큰 기반 인증을 제공하므로
     * Security 에서 제공하는 csrf, formLogin, httpBasic 비활성화
     * 또한 세션 정책을 Stateless 로 설정하여 세션을 비활성화함
     * 인증 프로세스는 AOP 및 ArgumentResolver 에서 수행
     */
    override fun configure(http: HttpSecurity) {
        http.csrf().disable() // csrf 보안 토큰 disable 처리
            .formLogin().disable()
            .httpBasic().disable() // rest api
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 사용 안함

            .and()
            .authorizeRequests()
            .antMatchers("/**").permitAll()
    }
}