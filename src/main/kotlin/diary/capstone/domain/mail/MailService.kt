package diary.capstone.domain.mail

import diary.capstone.config.AUTH_CODE_DIGITS
import diary.capstone.config.AUTH_CODE_VALID_MINUTE
import diary.capstone.util.logger
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class MailService(private val javaMailSender: JavaMailSender) {

    // 다른 IP로 로그인 시 인증을 위해 발송
    @Async
    fun sendLoginAuthMail(code: String, email: String) {
        try {
            val mail = javaMailSender.createMimeMessage()
            val mailHelper = MimeMessageHelper(mail, false, "UTF-8")
            var text = ""

            mailHelper.setTo(email)
            mailHelper.setFrom("ahdwjdtprtm@naver.com", "모두의 일기장")
            mailHelper.setSubject("[모두의 일기장] 메일 인증 코드")
            text += "<html>" +
                    "<head><meta charset='utf-8'></head>" +
                    "<body>"

            text += "<h2>로그인 하기 위해 아래 인증 코드 ${AUTH_CODE_DIGITS}자리를 입력해주세요.</h2>" +
                    "<h3>인증 코드 : <strong>$code</strong></h3><br/>" +
                    "해당 메일은 다른 IP로 로그인을 시도할 경우 발송되며, ${AUTH_CODE_VALID_MINUTE}분간 유효합니다.<br/>" +
                    "만일 본인이 로그인을 시도하지 않았을 경우 비밀번호를 교체해주세요."

            text += "</body>" +
                    "</html>"

            mailHelper.setText(text, true)
            javaMailSender.send(mail)

        } catch (e: Exception) {
            logger().warn("메일 발송 오류 : ", e.message, e.printStackTrace())
        }
    }

    // 회원 가입 시 이메일 인증을 위해 발송
    @Async
    fun sendEmailAuthMail(code: String, email: String) {
        try {
            val mail = javaMailSender.createMimeMessage()
            val mailHelper = MimeMessageHelper(mail, false, "UTF-8")
            var text = ""

            mailHelper.setTo(email)
            mailHelper.setFrom("ahdwjdtprtm@naver.com", "모두의 일기장")
            mailHelper.setSubject("[모두의 일기장] 메일 인증 코드")
            text += "<html>" +
                    "<head><meta charset='utf-8'></head>" +
                    "<body>"

            text += "<h2>이메일 인증을 위해 아래 인증 코드 ${AUTH_CODE_DIGITS}자리를 입력해주세요.</h2>" +
                    "<h3>인증 코드 : <strong>$code</strong></h3><br/>" +
                    "해당 메일은 회원 가입 시 이메일 확인을 위해 발송되며, ${AUTH_CODE_VALID_MINUTE}분간 유효합니다.<br/>"

            text += "</body>" +
                    "</html>"

            mailHelper.setText(text, true)
            javaMailSender.send(mail)

        } catch (e: Exception) {
            logger().warn("메일 발송 오류 : ", e.message, e.printStackTrace())
        }
    }
}